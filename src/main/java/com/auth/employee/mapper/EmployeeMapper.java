package com.auth.employee.mapper;

import com.auth.department.entity.Department;
import com.auth.employee.dto.EmployeeDto;
import com.auth.employee.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

//    // Employee -> EmployeeResponseDto
//    EmployeeDto.Response employeeToEmployeeResponseDto(Employee employee);
//
//    // EmployeePostDto -> Employee
//    @Mapping(source = "departmentId", target = "department.id")
//    Employee employeePostDtoToEmployee(EmployeeDto.Post employeePostDto);
//
//    // EmployeePatchDto -> Employee
//    @Mapping(source = "departmentId", target = "department.id")
//    void updateEmployeeFromPatchDto(EmployeeDto.Patch employeePatchDto, @MappingTarget Employee employee);

    Employee employeePostToEmployee(EmployeeDto.Post post);

    Employee employeePatchToEmployee(EmployeeDto.Patch patch);

    EmployeeDto.Response employeeToResponseDto(Employee employee);

    List<EmployeeDto.Response> employeesToResponseDto(List<Employee> employees);

//    EmployeeDto.InfoResponse employeeToEmployeeInfoResponse(Employee employee);
}
