package com.auth.auth.handler;

import com.auth.employee.entity.Employee;
import com.auth.employee.repository.EmployeeRepository;
import com.auth.exception.BusinessLogicException;
import com.auth.exception.ExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class EmployeeAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final EmployeeRepository employeeRepository;

    public EmployeeAuthenticationSuccessHandler(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("Authenticated Success");
        String email = authentication.getName();
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.EMPLOYEE_NOT_FOUND));

        // 로그인 성공 시 상태를 LOGGED_IN으로 변경
        employee.setStatus(Employee.EmployeeStatus.LOGGED_IN);
        employeeRepository.save(employee);
    }
}
