package com.auth.department.controller;


import com.auth.department.dto.DepartmentDto;
import com.auth.department.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/departments")
public class DepartmentController {
    private final DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<DepartmentDto.Post> createDepartment(@RequestBody DepartmentDto.Post departmentDto) {
        DepartmentDto.Post createdDepartment = departmentService.createDepartment(departmentDto);
        return new ResponseEntity<>(createdDepartment, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public List<DepartmentDto.Response> getDepartments() {
        return departmentService.findAll();
    }
}