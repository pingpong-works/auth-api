package com.auth.employee.entity;

import com.auth.audit.Auditable;
import com.auth.department.entity.Department;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "employee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long employeeId;

    @NotNull
    @Column(name = "name", length = 255, nullable = false, unique = true)
    private String name;

    @NotNull
    @Column(name = "email", length = 255, nullable = false, unique = true)
    private String email;

    @NotNull
    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @NotNull
    @Column(name ="phone_number")
    private String phoneNumber;

    @Column(name = "profile_picture", columnDefinition = "TEXT")
    private String profilePicture;


    @Enumerated(value = EnumType.STRING)
    @Column(length = 20, nullable = false)
    private EmployeeStatus status = EmployeeStatus.Employee_ACTIVE;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> permissions = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    public enum EmployeeStatus {
        Employee_ACTIVE("회원 상태"),
        LOGGED_IN("로그인"),
        LOGGED_OUT("오프라인"),
        Employee_QUIT("탈퇴 상태");

        @Getter
        private String status;

        EmployeeStatus(String status) {
            this.status = status;
        }
    }

}
