package com.auth.employee.repository;

import com.auth.employee.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);

    Optional<Employee> findByPhoneNumber(String phoneNumber);

    Page<Employee> findByDepartment_IdAndStatusNot(Long departmentId, Employee.EmployeeStatus status, Pageable pageable);

    // 전체 직원 id 조회
    @Query("SELECT e.id FROM employee e")
    List<Long> findAllEmployeeIds();

    // 부서별 직원 id 조회
    @Query("SELECT e.id FROM employee e WHERE e.department.id = :departmentId")
    List<Long> findIdByDepartment(@Param("departmentId") Long departmentId);
}


