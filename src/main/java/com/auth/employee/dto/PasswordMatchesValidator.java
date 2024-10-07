package com.auth.employee.dto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj instanceof EmployeeDto.Post) {
            EmployeeDto.Post dto = (EmployeeDto.Post) obj;
            return dto.getPassword() != null && dto.getPassword().equals(dto.getConfirmPassword());
        }
        return false;
    }
}
