package com.auth.department.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDto {

    @NotNull(message = "이름은 필수 입력 사항입니다.")
    private String name;

    @NotNull(message = "설명은 필수 입력 사항입니다.")
    private String description;
}
