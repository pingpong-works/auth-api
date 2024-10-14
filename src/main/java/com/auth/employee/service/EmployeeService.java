package com.auth.employee.service;

import com.auth.department.entity.Department;
import com.auth.department.repository.DepartmentRepository;
import com.auth.employee.dto.EmployeeDto;
import com.auth.employee.entity.Employee;
import com.auth.employee.mapper.EmployeeMapper;
import com.auth.employee.repository.EmployeeRepository;
import com.auth.exception.BusinessLogicException;
import com.auth.exception.ExceptionCode;
import com.auth.utils.ExtractMemberEmail;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class EmployeeService extends ExtractMemberEmail {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;  // 패스워드 암호화 처리


    // 직원 생성
    public Employee createEmployee(EmployeeDto.Post employeePostDto) {
        Department department = departmentRepository.findById(employeePostDto.getDepartmentId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.DEPARTMENT_NOT_FOUND));

        //직원이 이미 있는 지 검증
        verifyExistEmployee(employeePostDto.getEmail());
        verifyExistPhoneNumber(employeePostDto.getPhoneNumber());

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(employeePostDto.getPassword());
        employeePostDto.setPassword(encodedPassword);

        // Employee 객체 생성
        Employee employee = employeeMapper.employeePostToEmployee(employeePostDto);
        employee.setDepartment(department);

        // 기본 권한 설정 (모든 직원에게 ROLE_USER 부여)
        employee.getPermissions().add("ROLE_USER");

        // 관리자 계정일 경우 ROLE_ADMIN 권한 추가
        if ("admin@example.com".equals(employee.getEmail())) {
            employee.getPermissions().add("ROLE_ADMIN");
        }

        return employeeRepository.save(employee);
    }

//    // 출근 처리
//    public void clockIn(Employee employee) {
//        employee.setAttendanceStatus(Employee.AttendanceStatus.CLOCKED_IN);
//        employeeRepository.save(employee);
//    }
//
//    // 퇴근 처리
//    public void clockOut(Employee employee) {
//        employee.setAttendanceStatus(Employee.AttendanceStatus.CLOCKED_OUT);
//        employeeRepository.save(employee);
//    }


    // 직원 정보 조회 (Admin 권한만 가능)
    public Employee findEmployeeById(Long id, Authentication authentication) {

        return employeeRepository.findById(id)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.EMPLOYEE_NOT_FOUND));
    }

    // 전체 직원 조회
//    public Page<Employee> findEmployees(int page, int size, Authentication authentication) {
//
//        return employeeRepository.findAll(PageRequest.of(page, size, Sort.by("employeeId").descending()));
//    }
    public Page<Employee> findEmployees(int page, int size, Authentication authentication) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("employeeId").ascending());
        Page<Employee> allEmployees = employeeRepository.findAll(pageRequest);

        // 관리자 이메일인 admin@example.com을 제외하고, 상태가 EMPLOYEE_QUIT가 아닌 직원들만 필터링
        List<Employee> filteredEmployees = allEmployees.stream()
                .filter(employee -> !"admin@example.com".equals(employee.getEmail())) // 관리자 이메일 제외
                .filter(employee -> employee.getStatus() != Employee.EmployeeStatus.EMPLOYEE_QUIT) // EMPLOYEE_QUIT 상태 제외
                .collect(Collectors.toList());

        // 필터링된 직원 리스트로 다시 Page 객체 생성
        return new PageImpl<>(filteredEmployees, pageRequest, filteredEmployees.size());
    }



    // 부서별 직원 조회 로직 -관리자 및 직원 나누기.
    public Page<Employee> findEmployeesByDepartment(Long departmentId, int page, int size, Authentication authentication) {

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.DEPARTMENT_NOT_FOUND));

        return employeeRepository.findByDepartment_IdAndStatusNot(
                departmentId,
                Employee.EmployeeStatus.EMPLOYEE_QUIT,  // EMPLOYEE_QUIT 상태를 제외
                PageRequest.of(page, size, Sort.by("employeeId").descending())
        );
    }

    // 직원 정보 수정 (Patch)
    public Employee updateEmployee(Employee employee, Authentication authentication) {
        Employee authenticatedEmployee = extractEmployeeFromAuthentication(authentication, employeeRepository);

        // 이름 업데이트
        Optional.ofNullable(employee.getName())
                .filter(name -> !name.equals(authenticatedEmployee.getName()))
                .ifPresent(authenticatedEmployee::setName);

        // 이메일 업데이트
        Optional.ofNullable(employee.getEmail())
                .filter(email -> !email.equals(authenticatedEmployee.getEmail()))
                .ifPresent(authenticatedEmployee::setEmail);

        // 휴대폰 번호 업데이트
        Optional.ofNullable(employee.getPhoneNumber())
                .filter(phoneNumber -> !phoneNumber.equals(authenticatedEmployee.getPhoneNumber()))
                .ifPresent(authenticatedEmployee::setPhoneNumber);

        // 프로필 사진 업데이트
        Optional.ofNullable(employee.getProfilePicture())
                .filter(profilePicture -> !profilePicture.equals(authenticatedEmployee.getProfilePicture()))
                .ifPresent(authenticatedEmployee::setProfilePicture);

        // 내선 번호 업데이트
        Optional.ofNullable(employee.getExtensionNumber())
                .filter(extensionNumber -> !extensionNumber.equals(authenticatedEmployee.getExtensionNumber()))
                .ifPresent(authenticatedEmployee::setExtensionNumber);

        // 긴급 연락망 업데이트
        Optional.ofNullable(employee.getEmergencyNumber())
                .filter(emergencyNumber -> !emergencyNumber.equals(authenticatedEmployee.getEmergencyNumber()))
                .ifPresent(authenticatedEmployee::setEmergencyNumber);

        // 주소 업데이트
        Optional.ofNullable(employee.getAddress())
                .filter(address -> !address.equals(authenticatedEmployee.getAddress()))
                .ifPresent(authenticatedEmployee::setAddress);

        // 차량 번호 업데이트
        Optional.ofNullable(employee.getVehicleNumber())
                .filter(vehicleNumber -> !vehicleNumber.equals(authenticatedEmployee.getVehicleNumber()))
                .ifPresent(authenticatedEmployee::setVehicleNumber);

        // 직급 업데이트
        Optional.ofNullable(employee.getEmployeeRank())
                .filter(employeeRank -> !employeeRank.equals(authenticatedEmployee.getEmployeeRank()))
                .ifPresent(authenticatedEmployee::setEmployeeRank);

        return employeeRepository.save(authenticatedEmployee);
    }

    // 로그 아웃 -> 오프라인 상태 변경
    @Transactional
    public void updateStatus(String email) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.EMPLOYEE_NOT_FOUND));

        // 상태 변경
        employee.setStatus(Employee.EmployeeStatus.LOGGED_OUT);

        employeeRepository.save(employee);  // 상태 변경 후 저장
    }

    //비밀번호 변경
    public Employee updatePassword(String email, EmployeeDto.UpdatePassword updatePasswordDto) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.EMPLOYEE_NOT_FOUND));

        if (!passwordEncoder.matches(updatePasswordDto.getCurrentPassword(), employee.getPassword())) {
            throw new BusinessLogicException(ExceptionCode.PASSWORD_MISMATCH);
        }

        String newEncryptedPassword = passwordEncoder.encode(updatePasswordDto.getNewPassword());
        employee.setPassword(newEncryptedPassword);
        return employeeRepository.save(employee);
    }

    //관리자가 직원 삭제
    @Transactional
    public void deleteEmployeeById(Long employeeId, Authentication authentication) {

        checkAdminAuthority(authentication);

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.EMPLOYEE_NOT_FOUND));

        employee.setStatus(Employee.EmployeeStatus.EMPLOYEE_QUIT);
        employeeRepository.save(employee);
    }

    // 인증된 사용자에서 직원 정보를 추출하는 메서드
    public Employee extractEmployeeFromAuthentication(Authentication authentication, EmployeeRepository employeeRepository) {
        if (authentication == null) {
            throw new BusinessLogicException(ExceptionCode.NO_AUTHORITY);
        }

        String email = (String) authentication.getPrincipal();
        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.EMPLOYEE_NOT_FOUND));
    }

    public Employee findVerifiedEmployee(String email) {
        Optional<Employee> optionalEmployee = employeeRepository.findByEmail(email);
        Employee findEmployee = optionalEmployee.orElseThrow(()
                -> new BusinessLogicException(ExceptionCode.EMPLOYEE_NOT_FOUND));
        return findEmployee;
    }

    // 직원 존재 여부 검증 (수정됨: 직원이 존재하면 예외 발생)
    private void verifyExistEmployee(String email) {
        Optional<Employee> employee = employeeRepository.findByEmail(email);
        if (employee.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.EMPLOYEE_EXIST);
        }
    }

    // 관리자 권한 체크 (이메일로 확인)
    public void checkAdminAuthority(Authentication authentication) {
        if (authentication == null || !authentication.getPrincipal().equals("admin@example.com")) {
            throw new BusinessLogicException(ExceptionCode.ACCESS_DENIED);
        }
    }

    public List<Long> getTotalEmployeeIds() {
       return employeeRepository.findAllEmployeeIds();
    }


    //전화 번호 유효성 체크
    private void verifyExistPhoneNumber(String phoneNumber) {
        Optional<Employee> employee = employeeRepository.findByPhoneNumber(phoneNumber);
        if (employee.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.PHONE_NUMBER_EXIST);
        }
    }

    public List<Long> getEmployeeIdsByDepartment(Long departmentId) {
        return employeeRepository.findIdByDepartment(departmentId);
    }
}
