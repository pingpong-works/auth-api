package com.auth.employee.mapper;

import com.auth.employee.dto.EmployeeDto;
import com.auth.employee.entity.Employee;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    // 관리자용 응답
    default EmployeeDto.AdminResponse employeeToAdminResponseDto(Employee employee) {
        if (employee == null) {
            return null;
        }
        EmployeeDto.AdminResponse response = new EmployeeDto.AdminResponse();
        response.setEmployeeId(employee.getEmployeeId());
        response.setName(employee.getName());
        response.setEmail(employee.getEmail());
        return response;
    }

    // 직원용 응답
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
        if (employee.getDepartment() != null) {
            response.setDepartmentName(employee.getDepartment().getName());
            response.setDepartmentId(employee.getDepartment().getId());
        }
        response.setStatus(employee.getStatus().toString());
        return response;
    }

    // 관리자용 InfoResponse
    default EmployeeDto.AdminResponse employeeToAdminInfoResponse(Employee employee) {
        if (employee == null) {
            return null;
        }
        EmployeeDto.AdminResponse response = new EmployeeDto.AdminResponse();
        response.setEmployeeId(employee.getEmployeeId());
        response.setName(employee.getName());
        response.setEmail(employee.getEmail());
        return response;
    }

    // 직원용 InfoResponse
    default EmployeeDto.InfoResponse employeeToEmployeeInfoResponse(Employee employee) {
        if (employee == null) {
            return null;
        }
        EmployeeDto.InfoResponse response = new EmployeeDto.InfoResponse();
        response.setEmployeeId(employee.getEmployeeId());
        response.setName(employee.getName());
        response.setEmail(employee.getEmail());
        response.setProfilePicture(employee.getProfilePicture());
        response.setDepartmentName(employee.getDepartment() != null ? employee.getDepartment().getName() : null);
        response.setDepartmentId(employee.getDepartment() != null ? employee.getDepartment().getId() : null);
        response.setPhoneNumber(employee.getPhoneNumber());
        response.setExtensionNumber(employee.getExtensionNumber());
        response.setEmergencyNumber(employee.getEmergencyNumber());
        response.setAddress(employee.getAddress());
        response.setVehicleNumber(employee.getVehicleNumber());
        response.setCreatedAt(employee.getCreatedAt().toString());
        response.setEmployeeRank(employee.getEmployeeRank().getRankName());
        response.setStatus(employee.getStatus().getStatus());
        return response;
    }

    // 관리자 리스트 변환
    default List<EmployeeDto.AdminResponse> employeesToAdminInfoResponseDto(List<Employee> employees) {
        if (employees == null) {
            return null;
        }
        return employees.stream()
                .map(this::employeeToAdminInfoResponse)
                .collect(Collectors.toList());
    }

    // 직원 리스트 변환
    default List<EmployeeDto.InfoResponse> employeesToEmployeeInfoResponseDto(List<Employee> employees) {
        if (employees == null) {
            return null;
        }
        return employees.stream()
                .map(this::employeeToEmployeeInfoResponse)
                .collect(Collectors.toList());
    }

    // 직원 생성 시 DTO -> Employee 변환
    default Employee employeePostToEmployee(EmployeeDto.EmployeePost postDto) {
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

    // 관리자 생성 시 DTO -> Employee 변환
    default Employee adminPostToAdmin(EmployeeDto.AdminPost postDto) {
        if (postDto == null) {
            return null;
        }
        Employee employee = new Employee();
        employee.setName(postDto.getName());
        employee.setEmail(postDto.getEmail());
        employee.setPassword(postDto.getPassword());
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
        employee.setExtensionNumber(patchDto.getExtensionNumber());
        employee.setEmergencyNumber(patchDto.getEmergencyNumber());
        employee.setAddress(patchDto.getAddress());
        employee.setVehicleNumber(patchDto.getVehicleNumber());
        return employee;
    }
}
