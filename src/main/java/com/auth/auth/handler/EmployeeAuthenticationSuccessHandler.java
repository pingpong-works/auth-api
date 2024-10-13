
package com.auth.auth.handler;

import com.auth.employee.entity.Employee;
import com.auth.employee.repository.EmployeeRepository;
import com.auth.employee.service.EmployeeService;
import com.auth.exception.BusinessLogicException;
import com.auth.exception.ExceptionCode;
import lombok.NoArgsConstructor;
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

    public EmployeeAuthenticationSuccessHandler( EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("Authenticated Success");
        String email = authentication.getName();
        Employee employee = employeeRepository.findByEmail(email)
                        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.EMPLOYEE_NOT_FOUND));
        employee.setAttendanceStatus(Employee.AttendanceStatus.CLOCKED_IN);
        employeeRepository.save(employee);

    }
}
