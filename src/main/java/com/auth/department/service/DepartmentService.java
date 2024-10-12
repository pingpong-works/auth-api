package com.auth.department.service;


import com.auth.department.dto.DepartmentDto;
import com.auth.department.entity.Department;
import com.auth.department.mapper.DepartmentMapper;
import com.auth.department.repository.DepartmentRepository;
import com.auth.exception.BusinessLogicException;
import com.auth.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    public final DepartmentRepository departmentRepository;
    public final DepartmentMapper departmentMapper;


    public DepartmentDto.Post createDepartment(DepartmentDto.Post departmentPostDto) {
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
}
