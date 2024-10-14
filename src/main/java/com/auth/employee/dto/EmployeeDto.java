package com.auth.employee.dto;

import com.auth.employee.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
public class EmployeeDto {

    // Post DTO (Employee 생성용)
    @Getter
    @Setter
    @NoArgsConstructor
    @PasswordMatches
    public static class EmployeePost { // 직원

        @NotNull
        @Pattern(regexp = "^[가-힣]+$", message = "이름은 한글만 입력 가능합니다.")
        private String name;

        @NotNull
        @Email
        private String email;

        @NotNull(message = "비밀번호는 필수 항목입니다.")
        @Size(min = 8, max = 15, message = "비밀번호는 8자에서 15자 사이여야 합니다.")
        @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?`~]{8,15}$",
                message = "비밀번호는 8자이상 15자 이하의 알파벳, 숫자, 특수문자만 포함할 수 있습니다.")
        private String password;

        @NotNull(message = "비밀번호를 한번더 입력해주세요.")
        private String confirmPassword;

        @NotNull
        @Pattern(regexp = "^(010-\\d{4}-\\d{4}|010\\d{8})$", message = "휴대폰 번호는 '010-XXXX-XXXX' 또는 '010XXXXXXXX' 형식이어야 합니다.")
        private String phoneNumber;

        private String extensionNumber;

        private String profilePicture;

        private Long departmentId;

        @NotNull
        private Employee.EmployeeRank employeeRank; // 직급 필드 추가
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @PasswordMatches
    public static class AdminPost { // 직원

        @NotNull
        @Pattern(regexp = "^[가-힣]+$", message = "이름은 한글만 입력 가능합니다.")
        private String name;

        @NotNull
        @Email
        private String email;

        @NotNull(message = "비밀번호는 필수 항목입니다.")
        @Size(min = 8, max = 15, message = "비밀번호는 8자에서 15자 사이여야 합니다.")
        @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?`~]{8,15}$",
                message = "비밀번호는 8자이상 15자 이하의 알파벳, 숫자, 특수문자만 포함할 수 있습니다.")
        private String password;

        @NotNull(message = "비밀번호를 한번더 입력해주세요.")
        private String confirmPassword;

    }

    // Patch DTO (Employee 수정용)
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Patch {

        @Pattern(regexp = "^[가-힣]+$", message = "이름은 한글만 입력 가능합니다.")
        private String name;

        @Email
        private String email;

        @Pattern(regexp = "^(010-\\d{4}-\\d{4}|010\\d{8})$", message = "휴대폰 번호는 '010-XXXX-XXXX' 또는 '010XXXXXXXX' 형식이어야 합니다.")
        private String phoneNumber;

        private String extensionNumber;
        private String emergencyNumber;
        private String address;
        private String vehicleNumber;
        private String profilePicture;
        private Long departmentId;
        private Employee.EmployeeRank employeeRank;
    }

    // Response DTO (Admin 응답용)
    @Getter
    @Setter
    @NoArgsConstructor
    public static class AdminResponse {
        private long employeeId;
        private String name;
        private String email;
        private String phoneNumber;
        private String profilePicture;
        private String extensionNumber;
        private String emergencyNumber;
        private String address;
        private String vehicleNumber;
        private String createdAt;
        private Employee.EmployeeStatus status;
        private String departmentName;
        private String employeeRank;
    }

    // Response DTO (Employee 응답용)
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResponse {
        private Long employeeId;
        private String name;
        private String email;
        private String phoneNumber;
        private String employeeRank;
        private String extensionNumber;
        private String emergencyNumber;
        private String departmentName;
        private Long departmentId;
        private String status;
    }

    @Getter
    @Setter
    public static class UpdatePassword {
        @NotNull(message = "기존 비밀번호를 입력해주세요.")
        private String currentPassword;

        @NotNull(message = "새 비밀번호를 입력해주세요.")
        @Size(min = 8, max = 20, message = "비밀번호는 8자에서 20자 사이여야 합니다.")
        @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?`~]+$",
                message = "비밀번호는 알파벳, 숫자, 특수문자만 포함할 수 있습니다.")
        private String newPassword;

        @NotNull(message = "새 비밀번호 확인을 입력해주세요.")
        private String confirmNewPassword;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InfoResponse {
        private long employeeId;
        private String name;
        private String email;
        private String profilePicture;
        private String departmentName;
        private Long departmentId;
        private String phoneNumber;
        private String extensionNumber;
        private String emergencyNumber;
        private String address;
        private String vehicleNumber;
        private String createdAt;
        private String employeeRank;
        private String status;
    }
}
