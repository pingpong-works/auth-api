package com.auth.department.entity;

import com.auth.audit.Auditable;
import com.auth.employee.entity.Employee;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "department")
public class Department extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "department_name", nullable = false, length = Integer.MAX_VALUE, unique = true)
    private String name;

    @OneToMany(mappedBy = "department")
    @JsonManagedReference // 순환 참조 방지
    private List<Employee> employees;
}
