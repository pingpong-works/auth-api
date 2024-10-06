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

@Service
@RequiredArgsConstructor
public class DepartmentService {
    public final DepartmentRepository departmentRepository;
    public final DepartmentMapper departmentMapper;


    public DepartmentDto createDepartment(DepartmentDto departmentDto) {
        if (departmentRepository.existsByName(departmentDto.getName())) {
            throw new BusinessLogicException(ExceptionCode.DEPARTMENT_EXIST);
        }

        Department department = departmentMapper.toEntity(departmentDto);
        Department savedDepartment = departmentRepository.save(department);
        return departmentMapper.toDto(savedDepartment);
    }

    public List<DepartmentDto> findAll() {
        return departmentMapper.departmentDtoList(departmentRepository.findAll());
    }
}
