package com.auth.department.controller;


import com.auth.department.dto.DepartmentDto;
import com.auth.department.entity.Department;
import com.auth.department.mapper.DepartmentMapper;
import com.auth.department.service.DepartmentService;
import com.auth.dto.SingleResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/departments")
public class DepartmentController {
    private final DepartmentService departmentService;
    private final DepartmentMapper mapper;

    @PostMapping
    public ResponseEntity<DepartmentDto.Post> createDepartment(Authentication authentication,
                                                               @RequestBody DepartmentDto.Post departmentDto) {

        DepartmentDto.Post createdDepartment = departmentService.createDepartment(departmentDto, authentication);
        return new ResponseEntity<>(createdDepartment, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public List<DepartmentDto.Response> getDepartments() {
        return departmentService.findAll();
    }

    @GetMapping("{id}")
    public ResponseEntity getDepartment(@PathVariable("id") @Positive long id) {
        Department department = departmentService.findVerifiedDepartment(id);
        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.toDepartmentResponse(department)), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteDepartment(@PathVariable("id") @Positive long id, Authentication authentication) {
        departmentService.deleteDepartmentById(id, authentication);
        return ResponseEntity.noContent().build();
    }
}