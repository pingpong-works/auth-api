package com.auth.employee.mapper;

import com.auth.employee.dto.EmployeeDto;
import com.auth.employee.entity.Employee;
import org.mapstruct.Mapper;


import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    // 관리자용
    default EmployeeDto.AdminResponse employeeToAdminResponseDto(Employee employee) {
        if (employee == null) {
            return null;
        }
        EmployeeDto.AdminResponse response = new EmployeeDto.AdminResponse();
        response.setEmployeeId(employee.getEmployeeId());
        response.setName(employee.getName());
        response.setEmail(employee.getEmail());
        response.setPhoneNumber(employee.getPhoneNumber());
        response.setProfilePicture(employee.getProfilePicture());
        response.setCreatedAt(employee.getCreatedAt().toString());  // createdAt을 String으로 변환
        response.setStatus(employee.getStatus());
        response.setAttendanceStatus(employee.getAttendanceStatus());
        response.setEmployeeRank(employee.getEmployeeRank());
        // 추가된 필드 설정
        response.setExtensionNumber(employee.getExtensionNumber());
        response.setEmergencyNumber(employee.getEmergencyNumber());
        response.setAddress(employee.getAddress());
        response.setVehicleNumber(employee.getVehicleNumber());
        // Department 정보 설정 (departmentName)
        if (employee.getDepartment() != null) {
            response.setDepartmentName(employee.getDepartment().getName());
        }

        return response;
    }

    //직원 용
    default EmployeeDto.UserResponse employeeToUserResponseDto(Employee employee) {
        if (employee == null) {
            return null;
        }
        EmployeeDto.UserResponse response = new EmployeeDto.UserResponse();
        response.setEmployeeId(employee.getEmployeeId());
        response.setName(employee.getName());
        response.setEmail(employee.getEmail());
        response.setPhoneNumber(employee.getPhoneNumber());
        response.setEmployeeRank(employee.getEmployeeRank().toString());
        response.setExtensionNumber(employee.getExtensionNumber());
        response.setEmergencyNumber(employee.getEmergencyNumber());
        response.setDepartmentName(employee.getDepartment().getName()) ;

        return response;
    }

    // 관리자 용
    default List<EmployeeDto.AdminResponse> employeesToAdminResponseDto(List<Employee> employees) {
        if (employees == null) {
            return null;
        }

        return employees.stream()
                .map(this::employeeToAdminResponseDto)
                .collect(Collectors.toList());
    }

    // 직원 용
    default List<EmployeeDto.UserResponse> employeesToUserResponseDto(List<Employee> employees) {
        if (employees == null) {
            return null;
        }

        return employees.stream()
                .map(this::employeeToUserResponseDto)
                .collect(Collectors.toList());
    }


    default Employee employeePostToEmployee(EmployeeDto.Post postDto) {
        if (postDto == null) {
            return null;
        }
        Employee employee = new Employee();
        employee.setName(postDto.getName());
        employee.setEmail(postDto.getEmail());
        employee.setPassword(postDto.getPassword());
        employee.setPhoneNumber(postDto.getPhoneNumber());
        employee.setProfilePicture(postDto.getProfilePicture());
        employee.setStatus(Employee.EmployeeStatus.EMPLOYEE_ACTIVE);
        employee.setEmployeeRank(postDto.getEmployeeRank());
        employee.setExtensionNumber(postDto.getExtensionNumber());

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
        employee.setEmployeeRank(patchDto.getEmployeeRank());
        // 추가된 필드 설정
        employee.setExtensionNumber(patchDto.getExtensionNumber());
        employee.setEmergencyNumber(patchDto.getEmergencyNumber());
        employee.setAddress(patchDto.getAddress());
        employee.setVehicleNumber(patchDto.getVehicleNumber());

        return employee;
    }

    EmployeeDto.InfoResponse employeeToEmployeeInfoResponse(Employee employee);
}

