package com.auth.auth.userdetails;

import com.auth.auth.utils.JwtAuthorityUtils;
import com.auth.employee.entity.Employee;
import com.auth.employee.repository.EmployeeRepository;
import com.auth.exception.BusinessLogicException;
import com.auth.exception.ExceptionCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

@Component
public class EmployeeDetailsService implements UserDetailsService {
    private final EmployeeRepository employeeRepository;
    private final JwtAuthorityUtils authorityUtils;

    public EmployeeDetailsService(EmployeeRepository employeeRepository, JwtAuthorityUtils authorityUtils) {
        this.employeeRepository = employeeRepository;
        this.authorityUtils = authorityUtils;
    }

    //username 은 사용자를 인증하기 위한 고유 식별자. 우리 서비스는 memberId 로 식별함.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Employee> optionalEmployee = employeeRepository.findByEmail(username);
        Employee findEmployee = optionalEmployee.orElseThrow(() -> new BusinessLogicException(ExceptionCode.EMPLOYEE_NOT_FOUND));

        return new EmployeeDetails(findEmployee);
    }



    private final class EmployeeDetails extends Employee implements UserDetails {
        EmployeeDetails(Employee employee) {
            setEmployeeId(employee.getEmployeeId());
            setEmail(employee.getEmail());  // email 필드를 복사합니다.
            setPassword(employee.getPassword());
            setPermissions(employee.getPermissions());
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorityUtils.createAuthorities(this.getPermissions());
        }

        // Username 을 memberId (사원 번호)로 함.
        @Override
        public String getUsername() {
            return getEmail();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}