package com.auth.utils;

import com.auth.employee.entity.Employee;
import com.auth.employee.repository.EmployeeRepository;
import com.auth.exception.BusinessLogicException;
import com.auth.exception.ExceptionCode;
import org.springframework.security.core.Authentication;

public abstract class ExtractMemberEmail {

    public Employee extractEmployeeFromAuthentication(Authentication authentication,
                                                      EmployeeRepository employeeRepository)  {
        if(authentication == null){
            throw new BusinessLogicException(ExceptionCode.TOKEN_INVALID);
        }
        String email = (String) authentication.getPrincipal();

        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.EMPLOYEE_NOT_FOUND));
    }
}