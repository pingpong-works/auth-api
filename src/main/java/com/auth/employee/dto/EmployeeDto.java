package com.auth.employee.dto;

import com.auth.employee.entity.Employee;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
public class EmployeeDto {

    // Post DTO (Employee 생성용)
    @Getter
    @Setter
    public static class Post {

        private String name;

        private String email;

        @NotNull(message = "비밀번호는 필수 항목입니다.")
        @Size(min = 8, max = 15, message = "비밀번호는 8자에서 15자 사이여야 합니다.")
        @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?`~]{8,15}$",
                message = "비밀번호는 8자이상 15자 이하의 알파벳, 숫자, 특수문자만 포함할 수 있습니다.")
        private String password;

        @NotNull(message = "비밀번호를 한번더 입력해주세요.")
        private String confirmPassword;

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

        @NotNull(message = "비밀번호는 필수 항목입니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8자에서 20자 사이여야 합니다.")
        @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?`~]+$",
                message = "비밀번호는 알파벳, 숫자, 특수문자만 포함할 수 있습니다.")
        private String password;

        @NotNull(message = "비밀번호를 한번더 입력해주세요.")
        private String confirmPassword;

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
        private String phoneNumber;
        private String profilePicture;
        private String createdAt;
        private Employee.EmployeeStatus status;
        private String departmentName;
    }
}
