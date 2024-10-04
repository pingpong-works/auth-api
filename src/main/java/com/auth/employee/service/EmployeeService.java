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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


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
                .orElseThrow(() -> new RuntimeException("Department not found"));

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

        // 직원 저장
        return employeeRepository.save(employee);
    }


    // 관리자 권한 체크 (이메일로 확인)
    private void checkAdminAuthority(Authentication authentication) {
        if (authentication == null || !authentication.getPrincipal().equals("admin@example.com")) {
            throw new BusinessLogicException(ExceptionCode.ACCESS_DENIED);
        }
    }

    // 직원 정보 조회 (Admin 권한만 가능)
    public Employee findEmployeeById(Long id, Authentication authentication) {
        checkAdminAuthority(authentication);

        return employeeRepository.findById(id)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.EMPLOYEE_NOT_FOUND));
    }

    // 전체 직원 조회
    public Page<Employee> findEmployees(int page, int size, Authentication authentication) {
        checkAdminAuthority(authentication);

        return employeeRepository.findAll(PageRequest.of(page, size, Sort.by("employeeId").descending()));
    }

    // 부서별 직원 조회 로직
    public Page<Employee> findEmployeesByDepartment(Long departmentId, int page, int size, Authentication authentication) {
        checkAdminAuthority(authentication);

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.DEPARTMENT_NOT_FOUND));

        return employeeRepository.findByDepartment(department, PageRequest.of(page, size, Sort.by("employeeId").descending()));
    }

    // 직원 정보 수정 (Patch)
    public Employee updateEmployee(Employee employee, Authentication authentication) {
        Employee authenticatedEmployee = extractEmployeeFromAuthentication(authentication, employeeRepository);

        // 비밀번호 업데이트
        if (employee.getPassword() != null && !passwordEncoder.matches(employee.getPassword(), authenticatedEmployee.getPassword())) {
            String encryptedPassword = passwordEncoder.encode(employee.getPassword()); // 비밀번호 암호화
            authenticatedEmployee.setPassword(encryptedPassword); // 암호화된 비밀번호로 설정
        }

        // 이름 업데이트
        if (employee.getName() != null && !employee.getName().equals(authenticatedEmployee.getName())) {
            authenticatedEmployee.setName(employee.getName());
        }

        return employeeRepository.save(authenticatedEmployee);
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

    // 직원 존재 여부 검증 (수정됨: 직원이 존재하면 예외 발생)
    private void verifyExistEmployee(String email) {
        Optional<Employee> employee = employeeRepository.findByEmail(email);
        if (employee.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.EMPLOYEE_EXIST);
        }
    }
}
