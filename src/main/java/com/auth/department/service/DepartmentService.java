package com.auth.department.service;


import com.auth.department.dto.DepartmentDto;
import com.auth.department.entity.Department;
import com.auth.department.mapper.DepartmentMapper;
import com.auth.department.repository.DepartmentRepository;
import com.auth.employee.entity.Employee;
import com.auth.employee.service.EmployeeService;
import com.auth.exception.BusinessLogicException;
import com.auth.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    public final DepartmentRepository departmentRepository;
    public final DepartmentMapper departmentMapper;
    public final EmployeeService employeeService;

    public DepartmentDto.Post createDepartment(DepartmentDto.Post departmentPostDto, Authentication authentication) {

        //관리자 인지 체크
        employeeService.checkAdminAuthority(authentication);

        if (departmentRepository.existsByName(departmentPostDto.getName())) {
            throw new BusinessLogicException(ExceptionCode.DEPARTMENT_EXIST);
        }

        Department department = departmentMapper.toEntity(departmentPostDto);
        Department savedDepartment = departmentRepository.save(department);
        return departmentMapper.toDto(savedDepartment);
    }

    public List<DepartmentDto.Response> findAll() {
        return departmentMapper.departmentDtoList(departmentRepository.findAll());
    }

    public Department findVerifiedDepartment(long departmentId) {
        Optional<Department> optionalDepartment = departmentRepository.findById(departmentId);
        return optionalDepartment.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.DEPARTMENT_NOT_FOUND));
    }

    @Transactional
    public void deleteDepartmentById(Long departmentId, Authentication authentication) {

        employeeService.checkAdminAuthority(authentication);

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.EMPLOYEE_NOT_FOUND));

        departmentRepository.delete(department);
    }
}
