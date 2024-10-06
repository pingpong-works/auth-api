package com.auth.employee.controller;


import com.auth.auth.service.AuthService;
import com.auth.dto.MultiResponseDto;
import com.auth.dto.SingleResponseDto;
import com.auth.employee.dto.EmployeeDto;
import com.auth.employee.entity.Employee;
import com.auth.employee.mapper.EmployeeMapper;
import com.auth.employee.service.EmployeeService;
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

@RestController
@RequestMapping
@RequiredArgsConstructor
public class EmployeeController {
    private final static String EMPLOYEE_DEFAULT_URL = "/employees";
    private final EmployeeService employeeService;
    private final EmployeeMapper employeeMapper;
    private final AuthService authService;

    // 회원가입 완료
    @PostMapping("/signup")
    public ResponseEntity createEmployee(@Valid @RequestBody EmployeeDto.Post employeeDto, BindingResult bindingResult) {
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

    // 전체 회원 조회
    @GetMapping("/employees")
    public ResponseEntity getEmployees(@RequestParam @Positive int page,
                                       @RequestParam @Positive int size, Authentication authentication) {

        // Service에서 인증 및 권한 검증 수행
        Page<Employee> pageEmployees = employeeService.findEmployees(page - 1, size, authentication);
        List<Employee> employees = pageEmployees.getContent();

        return new ResponseEntity<>(
                new MultiResponseDto<>(employeeMapper.employeesToResponseDto(employees), pageEmployees),
                HttpStatus.OK
        );
    }

    // 특정 회원 조회
    @GetMapping("/employees/{id}")
    public ResponseEntity<SingleResponseDto<EmployeeDto.Response>> getEmployeeById(@PathVariable Long id, Authentication authentication) {

        // Service에서 인증 및 권한 검증 수행
        Employee employee = employeeService.findEmployeeById(id, authentication);
        EmployeeDto.Response response = employeeMapper.employeeToResponseDto(employee);

        return ResponseEntity.ok(new SingleResponseDto<>(response));
    }

    // 부서별 직원 조회 API
    @GetMapping("/employees/departments/{departmentId}")
    public ResponseEntity<MultiResponseDto<EmployeeDto.Response>> getEmployeesByDepartment(
            @PathVariable Long departmentId,
            @RequestParam @Positive int page,
            @RequestParam @Positive int size, Authentication authentication) {

        // Service에서 인증 및 권한 검증 수행
        Page<Employee> pageEmployees = employeeService.findEmployeesByDepartment(departmentId, page - 1, size, authentication);
        List<EmployeeDto.Response> responseDtos = employeeMapper.employeesToResponseDto(pageEmployees.getContent());

        return new ResponseEntity<>(
                new MultiResponseDto<>(responseDtos, pageEmployees),
                HttpStatus.OK
        );
    }


    // 내 정보 수정
    @PatchMapping("/employees")
    public ResponseEntity patchEmployee(Authentication authentication,
                                        @Valid @RequestBody EmployeeDto.Patch patch,
                                        BindingResult bindingResult) {

        // 입력 값에 오류가 있는지 확인
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        // Service에서 인증 및 권한 검증 수행 후 정보 수정
        Employee employee = employeeService.updateEmployee(employeeMapper.employeePatchToEmployee(patch), authentication);

        return new ResponseEntity<>(
                new SingleResponseDto<>(employeeMapper.employeeToResponseDto(employee)), HttpStatus.OK
        );
    }

    //내 정보 조회
    @GetMapping("/employees/my-info")
    public ResponseEntity getMyInfo(@AuthenticationPrincipal Object principal) {
        Employee employee = employeeService.findVerifiedEmployees(principal.toString());
        EmployeeDto.InfoResponse infoResponse = employeeMapper.employeeToEmployeeInfoResponse(employee);
        infoResponse.setDepartmentName(employee.getDepartment().getName());

        return new ResponseEntity(
                new SingleResponseDto<>(infoResponse), HttpStatus.OK);
    }

    //비밀번호 변경
    @PatchMapping("/employees/password")
    @CrossOrigin(origins = "http://localhost:3000")
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
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id, Authentication authentication) {
        employeeService.deleteEmployeeById(id, authentication);
        return ResponseEntity.noContent().build();
    }
}
