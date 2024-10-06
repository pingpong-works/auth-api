package com.auth.employee.entity;

import com.auth.audit.Auditable;
import com.auth.department.entity.Department;
import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @Column(name = "name", length = 255, nullable = false, unique = false)
    private String name;

    @NotNull
    @Column(name = "email", length = 255, nullable = false, unique = true)
    private String email;

    @NotNull
    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @NotNull
    @Column(name ="phone_number", unique = true)
    private String phoneNumber;

    @Column(name = "profile_picture", columnDefinition = "TEXT")
    private String profilePicture;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "employee_rank", length = 20, nullable = false)
    private EmployeeRank employeeRank;  // 직급 필드 추가

    @Enumerated(value = EnumType.STRING)
    @Column(name = "employee_status", length = 20, nullable = false)
    private EmployeeStatus status = EmployeeStatus.EMPLOYEE_ACTIVE;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> permissions = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "department_id")
    @JsonBackReference // 역참조 관계 설정
    private Department department;

    public enum EmployeeRank {
        INTERN("인턴"),
        STAFF("사원"),
        SENIOR_STAFF("주임"),
        ASSISTANT_MANAGER("대리"),
        MANAGER("과장"),
        SENIOR_MANAGER("차장"),
        DIRECTOR("부장");

        @Getter
        private String rankName;

        EmployeeRank(String rankName) {
            this.rankName = rankName;
        }
    }

    public enum EmployeeStatus {
        EMPLOYEE_ACTIVE("회원 상태"),
        LOGGED_IN("로그인"),
        LOGGED_OUT("오프라인"),
        EMPLOYEE_QUIT("탈퇴 상태");

        @Getter
        private String status;

        EmployeeStatus(String status) {
            this.status = status;
        }
    }
}
