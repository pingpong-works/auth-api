package com.auth.employee.service;

import com.auth.department.entity.Department;
import com.auth.department.repository.DepartmentRepository;
import com.auth.employee.dto.EmployeeDto;
import com.auth.employee.entity.Employee;
import com.auth.employee.mapper.EmployeeMapper;
import com.auth.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;  // 패스워드 암호화 처리

    // 모든 직원 리스트 조회
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    // 직원 생성
    public Employee createEmployee(EmployeeDto.Post employeePostDto) {
        Department department = departmentRepository.findById(employeePostDto.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(employeePostDto.getPassword());
        employeePostDto.setPassword(encodedPassword);

        Employee employee = employeeMapper.employeePostDtoToEmployee(employeePostDto);
        return employeeRepository.save(employee);
    }

    // 직원 ID로 조회
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    // 직원 정보 수정 (Patch)
    public Employee updateEmployee(Long id, EmployeeDto.Patch employeePatchDto) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Department department = departmentRepository.findById(employeePatchDto.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        employeeMapper.updateEmployeeFromPatchDto(employeePatchDto, employee);
        employee.setDepartment(department);

        return employeeRepository.save(employee);
    }
}
