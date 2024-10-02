package com.auth.employee.dto;

import com.auth.employee.entity.Employee;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeDto {

    // Post DTO (Employee 생성용)
    @Getter
    @Setter
    public static class Post {

        private String name;
        private String email;
        private String password;
        private String phoneNumber;
        private String profilePicture;
        private Long departmentId;
    }

    // Patch DTO (Employee 수정용)
    @Getter
    @Setter
    public static class Patch {
        private String name;
        private String email;
        private String phoneNumber;
        private String profilePicture;
        private Long departmentId;
    }

    // Response DTO (Employee 응답용)
    @Getter
    @Setter
    public static class Response {
        private long id;
        private String name;
        private String email;
        private String password;
        private String phoneNumber;
        private String profilePicture;
        private String createdAt;
        private Employee.EmployeeStatus status;
        private String departmentName;
    }
}
