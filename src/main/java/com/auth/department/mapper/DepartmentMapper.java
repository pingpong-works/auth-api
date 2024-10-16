package com.auth.department.mapper;




import com.auth.department.dto.DepartmentDto;
import com.auth.department.entity.Department;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    DepartmentMapper INSTANCE = Mappers.getMapper(DepartmentMapper.class);

    Department toEntity(DepartmentDto.Post departmentPostDto);
    DepartmentDto.Post toDto(Department department);

    // 부서 엔티티 리스트를 DTO 리스트로 변환
    default List<DepartmentDto.Response> departmentDtoList(List<Department> entities) {
        return entities.stream().map(this::toDepartmentResponse).collect(Collectors.toList());
    }

    // Department -> DepartmentDto.Response로 변환
    default DepartmentDto.Response toDepartmentResponse(Department department) {
        return new DepartmentDto.Response(
                department.getId(),
                department.getName(),
                department.getEmployees() != null ? department.getEmployees().size() : 0  // 직원 수 계산
        );
    }
}