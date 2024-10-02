package com.auth.employee.controller;


import com.auth.auth.service.AuthService;
import com.auth.employee.dto.EmployeeDto;
import com.auth.employee.entity.Employee;
import com.auth.employee.mapper.EmployeeMapper;
import com.auth.employee.service.EmployeeService;
import com.auth.utils.UriCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final static String EMPLOYEE_DEFAULT_URL = "/employees";
    private final EmployeeService employeeService;
    private final EmployeeMapper employeeMapper;
    private final AuthService authService;

    // 회원가입 완료
    @PostMapping("/signup")
    public ResponseEntity createEmployee(@Valid @RequestBody EmployeeDto.Post employeeDto, BindingResult bindingResult) {
        // 유효성 검사 후 에러가 있으면 처리
        if (bindingResult.hasErrors()) {
            // 유효성 검사에서 발생한 오류 메시지들을 반환
            List<String> errorMessages = bindingResult.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errorMessages);
        }
        Employee employee = employeeMapper.employeePostDtoToEmployee(employeeDto);
        employeeService.createEmployee(employeeDto);
        URI location = UriCreator.createUri(EMPLOYEE_DEFAULT_URL, employee.getEmployeeId());
        return ResponseEntity.created(location).build();
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDto.Response>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        List<EmployeeDto.Response> response = employees.stream()
                .map(employeeMapper::employeeToEmployeeResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto.Response> getEmployeeById(@PathVariable Long id) {
        Employee employee = employeeService.getEmployeeById(id);
        EmployeeDto.Response response = employeeMapper.employeeToEmployeeResponseDto(employee);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EmployeeDto.Response> updateEmployee(@PathVariable Long id, @RequestBody EmployeeDto.Patch employeePatchDto) {
        Employee employee = employeeService.updateEmployee(id, employeePatchDto);
        EmployeeDto.Response response = employeeMapper.employeeToEmployeeResponseDto(employee);
        return ResponseEntity.ok(response);
    }
}
