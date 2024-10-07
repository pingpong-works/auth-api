package com.auth.employee.repository;

import com.auth.department.entity.Department;
import com.auth.employee.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);

    Page<Employee> findByDepartment_IdAndStatusNot(Long departmentId, Employee.EmployeeStatus status, Pageable pageable);
}

