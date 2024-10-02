package com.auth.department.service;


import com.auth.department.dto.DepartmentDto;
import com.auth.department.entity.Department;
import com.auth.department.mapper.DepartmentMapper;
import com.auth.department.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    public final DepartmentRepository departmentRepository;
    public final DepartmentMapper departmentMapper;


    public DepartmentDto createDepartment(DepartmentDto departmentDto) {
        Department department = departmentMapper.toEntity(departmentDto);
        Department savedDepartment = departmentRepository.save(department);
        return departmentMapper.toDto(savedDepartment);
    }

    public List<DepartmentDto> findAll() {
        return departmentMapper.departmentDtoList(departmentRepository.findAll());
    }
}
