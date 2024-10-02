package com.auth.department.mapper;




import com.auth.department.dto.DepartmentDto;
import com.auth.department.entity.Department;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    Department toEntity(DepartmentDto departmentDto);
    DepartmentDto toDto(Department department);
    List<DepartmentDto> departmentDtoList (List<Department> entity);
}