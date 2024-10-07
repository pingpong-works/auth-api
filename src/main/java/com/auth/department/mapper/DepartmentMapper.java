package com.auth.department.mapper;




import com.auth.department.dto.DepartmentDto;
import com.auth.department.entity.Department;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    Department toEntity(DepartmentDto.Post departmentPostDto);
    DepartmentDto.Post toDto(Department department);
    List<DepartmentDto.Response> departmentDtoList (List<Department> entity);
}