package com.auth.employee.controller;


import com.auth.auth.service.AuthService;
import com.auth.department.entity.Department;
import com.auth.department.repository.DepartmentRepository;
import com.auth.dto.MultiResponseDto;
import com.auth.dto.SingleResponseDto;
import com.auth.employee.dto.EmployeeDto;
import com.auth.employee.entity.Employee;
import com.auth.employee.mapper.EmployeeMapper;
import com.auth.employee.repository.EmployeeRepository;
import com.auth.employee.service.EmployeeService;
import com.auth.exception.BusinessLogicException;
import com.auth.exception.ExceptionCode;
import com.auth.utils.UriCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping
@RequiredArgsConstructor
public class EmployeeController {
    private final static String EMPLOYEE_DEFAULT_URL = "/employees";
    private final EmployeeService employeeService;
    private final EmployeeMapper employeeMapper;
    private final AuthService authService;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    // 직원
    @PostMapping("/employees")
    public ResponseEntity createEmployee(@Valid @RequestBody EmployeeDto.EmployeePost employeeDto, BindingResult bindingResult) {
        // 유효성 검사 후 에러가 있으면 처리
        if (bindingResult.hasErrors()) {
            // 유효성 검사에서 발생한 오류 메시지들을 반환
            List<String> errorMessages = bindingResult.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errorMessages);
        }
        Employee employee = employeeMapper.employeePostToEmployee(employeeDto);
        employeeService.createEmployee(employeeDto);
        URI location = UriCreator.createUri(EMPLOYEE_DEFAULT_URL, employee.getEmployeeId());
        return ResponseEntity.created(location).build();
    }

    // 관리자 생성
    @PostMapping("/signup")
    public ResponseEntity createAdmin(@RequestBody EmployeeDto.AdminPost employeeDto, BindingResult bindingResult) {
        // 유효성 검사 후 에러가 있으면 처리
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error -> {
                System.out.println("Field: " + error.getField() + " - Message: " + error.getDefaultMessage());
            });
            List<String> errorMessages = bindingResult.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errorMessages);
        }

        Employee employee = employeeMapper.adminPostToAdmin(employeeDto);
        employeeService.createAdmin(employeeDto);
        URI location = UriCreator.createUri(EMPLOYEE_DEFAULT_URL, employee.getEmployeeId());
        return ResponseEntity.created(location).build();
    }

    // 전체 회원 조회는 직원, 관리자 나누지 않음.
    // 전체 회원 조회 (주소록)
    @GetMapping("/employees/all")
    public ResponseEntity getEmployeesForAdmin(@RequestParam @Positive int page,
                                               @RequestParam @Positive int size, Authentication authentication) {

        // Service에서 인증 및 권한 검증 수행
        Page<Employee> pageEmployees = employeeService.findEmployees(page - 1, size, authentication);
        List<Employee> employees = pageEmployees.getContent();

        return new ResponseEntity<>(
                new MultiResponseDto<>(employeeMapper.employeesToEmployeeInfoResponseDto(employees), pageEmployees),
                HttpStatus.OK
        );
    }



    // 특정 회원 조회 - 관리자, 직원 (주소록)
    @GetMapping("/employees/{id}")
    public ResponseEntity<SingleResponseDto<EmployeeDto.InfoResponse>> getEmployeeByIdForAdmin(@PathVariable Long id ) {

//        employeeService.checkAdminAuthority(authentication);
        // Service에서 인증 및 권한 검증 수행
        Employee employee = employeeService.findEmployeeById(id);
        EmployeeDto.InfoResponse response = employeeMapper.employeeToEmployeeInfoResponse(employee);

        return ResponseEntity.ok(new SingleResponseDto<>(response));
    }

    // 부서별 직원 조회 - 관리자용 (주소록)
    @GetMapping("/admin/employees/departments/{departmentId}")
    public ResponseEntity<MultiResponseDto<EmployeeDto.InfoResponse>> getEmployeesByDepartmentForAdmin(
            @PathVariable Long departmentId,
            @RequestParam @Positive int page,
            @RequestParam @Positive int size, Authentication authentication) {

//        employeeService.checkAdminAuthority(authentication);
        // Service에서 인증 및 권한 검증 수행
        Page<Employee> pageEmployees = employeeService.findEmployeesByDepartment(departmentId, page - 1, size, authentication);
        List<EmployeeDto.InfoResponse> responseDtos = employeeMapper.employeesToEmployeeInfoResponseDto(pageEmployees.getContent());

        return new ResponseEntity<>(
                new MultiResponseDto<>(responseDtos, pageEmployees),
                HttpStatus.OK
        );
    }


    // 특정 회원 조회 - 직원용 (주소록)
    @GetMapping("/user/employees/{id}")
    public ResponseEntity<SingleResponseDto<EmployeeDto.UserResponse>> getEmployeeByIdForUser(@PathVariable Long id) {

        // Service에서 인증 및 권한 검증 수행
        Employee employee = employeeService.findEmployeeById(id);
        EmployeeDto.UserResponse response = employeeMapper.employeeToUserResponseDto(employee);

        return ResponseEntity.ok(new SingleResponseDto<>(response));
    }

    // 부서별 직원 조회 - 직원용 (주소록) -> 대시보드 부서 활동에서 사용
    @GetMapping("/user/employees/departments/{departmentId}")
    public ResponseEntity<MultiResponseDto<EmployeeDto.InfoResponse>> getEmployeesByDepartmentForUser(
            @PathVariable Long departmentId,
            @RequestParam @Positive int page,
            @RequestParam @Positive int size, Authentication authentication) {

        // Service에서 인증 및 권한 검증 수행
        Page<Employee> pageEmployees = employeeService.findEmployeesByDepartment(departmentId, page - 1, size, authentication);
        List<EmployeeDto.InfoResponse> responseDtos = employeeMapper.employeesToEmployeeInfoResponseDto(pageEmployees.getContent());

        return new ResponseEntity<>(
                new MultiResponseDto<>(responseDtos, pageEmployees),
                HttpStatus.OK
        );
    }

    // 로그아웃 -> LOGGED_OUT 상태 변경
    @PatchMapping("/employees/update-status")
    public ResponseEntity updateEmployeeStatus(Authentication authentication) {
        String email = (String) authentication.getPrincipal();

        // 서비스에서 상태 업데이트 수행
        employeeService.updateStatus(email);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 직원 정보 수정
    @PatchMapping("/employees/{employee-id}")
    public ResponseEntity patchEmployee(
            @PathVariable("employee-id") Long employeeId,
            Authentication authentication,
            @Valid @RequestBody EmployeeDto.Patch patch,
            BindingResult bindingResult) {

        employeeService.checkAdminAuthority(authentication);
        // 입력 값에 오류가 있는지 확인
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        // Service에서 인증 및 권한 검증 수행 후 정보 수정
        Employee employee = employeeMapper.employeePatchToEmployee(patch);
        Department department = departmentRepository.findById(patch.getDepartmentId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.DEPARTMENT_NOT_FOUND));
        employee.setDepartment(department);

        Employee updateEmployee = employeeService.updateEmployee(employee, employeeId);


        return new ResponseEntity<>(
                new SingleResponseDto<>(employeeMapper.employeeToAdminResponseDto(updateEmployee)), HttpStatus.OK
        );
    }

    // 내 정보 수정
    @PatchMapping("/employees")
    public ResponseEntity patchMyInfo(Authentication authentication,
                                        @Valid @RequestBody EmployeeDto.Patch patch,
                                        BindingResult bindingResult) {

        // 입력 값에 오류가 있는지 확인
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        // Service에서 인증 및 권한 검증 수행 후 정보 수정
        Employee employee = employeeMapper.employeePatchToEmployee(patch);

        Employee updateEmployee = employeeService.updateMyInfo(employee, authentication);

        return new ResponseEntity<>(
                new SingleResponseDto<>(employeeMapper.employeeToEmployeeInfoResponse(updateEmployee)), HttpStatus.OK
        );
    }

    //내 정보 조회 - 직원용
    @GetMapping("/employees/my-info")
    public ResponseEntity getMyInfo(@AuthenticationPrincipal Object principal) {
        // principal 객체에서 사용자 정보를 추출
        String email = principal.toString(); // principal에서 email을 가져오는 방식이 필요함

        // 직원 정보 조회
        Employee employee = employeeService.findVerifiedEmployee(email);

        // 직급에 따라 응답 분기
        if (employee.getEmail().equals("admin@pingpong-works.com")) {  // 관리자인 경우
            // 관리자용 응답
            EmployeeDto.AdminResponse adminInfoResponse = employeeMapper.employeeToAdminInfoResponse(employee);

            return new ResponseEntity(
                    new SingleResponseDto<>(adminInfoResponse), HttpStatus.OK);
        } else {
            // 직원용 응답
            EmployeeDto.InfoResponse employeeInfoResponse = employeeMapper.employeeToEmployeeInfoResponse(employee);
            employeeInfoResponse.setDepartmentName(employee.getDepartment() != null ? employee.getDepartment().getName() : null);

            return new ResponseEntity(
                    new SingleResponseDto<>(employeeInfoResponse), HttpStatus.OK);
        }
    }




    //비밀번호 변경
    @PatchMapping("/employees/password")
    @CrossOrigin(origins = "http://localhost:5173")
    public ResponseEntity updatePassword(Authentication authentication,
                                         @Valid @RequestBody EmployeeDto.UpdatePassword updatePasswordDto,
                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        String email = (String) authentication.getPrincipal();
        boolean isLoggedOut = !authService.isTokenValid(email);
        if (isLoggedOut) {
            return new ResponseEntity<>("User Logged Out", HttpStatus.UNAUTHORIZED);
        }

        Employee employee = employeeService.updatePassword(email, updatePasswordDto);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 직원 삭제(상태 변경) API (관리자만 가능)
    @DeleteMapping("/employees/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id, Authentication authentication) {
        employeeService.deleteEmployeeById(id, authentication);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/employees")
    public ResponseEntity getEmployeeIds (Authentication authentication) {
        List<Long> ids = employeeService.getTotalEmployeeIds();
        return new ResponseEntity<>(ids, HttpStatus.OK);
    }

    @GetMapping("/departments/{department-id}/employees")
    public ResponseEntity getEmployeeIdsByDepartment (@PathVariable("department-id") Long departmentId,
                                                      Authentication authentication) {
        List<Long> ids = employeeService.getEmployeeIdsByDepartment(departmentId);

        return new ResponseEntity<>(ids, HttpStatus.OK);
    }
}
