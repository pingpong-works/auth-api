package com.auth.department.dto;


import com.auth.employee.entity.Employee;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class DepartmentDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Post {
        @NotNull(message = "이름은 필수 입력 사항입니다.")
        private String name;

        @NotNull(message = "설명은 필수 입력 사항입니다.")
        private String description;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Response {

        private long id;

        private String name;

        private String description;
    }
}
