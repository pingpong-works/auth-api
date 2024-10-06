package com.auth.employee.mapper;

import com.auth.employee.dto.EmployeeDto;
import com.auth.employee.entity.Employee;
import org.mapstruct.Mapper;


import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    // Employee -> EmployeeDto.Response 변환
    default EmployeeDto.Response employeeToResponseDto(Employee employee) {
        if (employee == null) {
            return null;
        }

        EmployeeDto.Response response = new EmployeeDto.Response();
        response.setEmployeeId(employee.getEmployeeId());
        response.setName(employee.getName());
        response.setEmail(employee.getEmail());
        response.setPhoneNumber(employee.getPhoneNumber());
        response.setProfilePicture(employee.getProfilePicture());
        response.setCreatedAt(employee.getCreatedAt().toString());  // createdAt을 String으로 변환
        response.setStatus(employee.getStatus());
        response.setEmployeeRank(employee.getEmployeeRank());
        // Department 정보 설정 (departmentName)
        if (employee.getDepartment() != null) {
            response.setDepartmentName(employee.getDepartment().getName());
        }

        return response;
    }

    // List<Employee> -> List<EmployeeDto.Response> 변환
    default List<EmployeeDto.Response> employeesToResponseDto(List<Employee> employees) {
        if (employees == null) {
            return null;
        }

        return employees.stream()
                .map(this::employeeToResponseDto)
                .collect(Collectors.toList());
    }

    // EmployeeDto.Post -> Employee 변환
    default Employee employeePostToEmployee(EmployeeDto.Post postDto) {
        if (postDto == null) {
            return null;
        }

        Employee employee = new Employee();
        employee.setName(postDto.getName());
        employee.setEmail(postDto.getEmail());
        employee.setPassword(postDto.getPassword());  // Password 설정
        employee.setPhoneNumber(postDto.getPhoneNumber());
        employee.setProfilePicture(postDto.getProfilePicture());
        employee.setStatus(Employee.EmployeeStatus.EMPLOYEE_ACTIVE);
        employee.setEmployeeRank(postDto.getEmployeeRank());
        return employee;
    }

    // EmployeeDto.Patch -> Employee 변환
    default Employee employeePatchToEmployee(EmployeeDto.Patch patchDto) {
        if (patchDto == null) {
            return null;
        }

        Employee employee = new Employee();
        employee.setName(patchDto.getName());
        employee.setEmail(patchDto.getEmail());
        employee.setPhoneNumber(patchDto.getPhoneNumber());
        employee.setProfilePicture(patchDto.getProfilePicture());
//        employee.setDepartment(department); department는 바깥에서 저장해주고 있음.
        employee.setEmployeeRank(patchDto.getEmployeeRank());

        return employee;
    }
    EmployeeDto.InfoResponse employeeToEmployeeInfoResponse(Employee employee);
}
