package com.auth.auth.service;

import com.auth.auth.jwt.JwtTokenizer;
import com.auth.employee.entity.Employee;
import com.auth.employee.repository.EmployeeRepository;
import com.auth.exception.BusinessLogicException;
import com.auth.exception.ExceptionCode;
import io.jsonwebtoken.JwtException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AuthService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtTokenizer jwtTokenizer;
    private final EmployeeRepository employeeRepository;

    public AuthService(RedisTemplate<String, Object> redisTemplate, JwtTokenizer jwtTokenizer, EmployeeRepository employeeRepository) {
        this.redisTemplate = redisTemplate;
        this.jwtTokenizer = jwtTokenizer;
        this.employeeRepository = employeeRepository;
    }

//    public boolean logout(String token) {
//        try {
//            // 토큰 검증
//            jwtTokenizer.verifySignature(token, jwtTokenizer.getSecretKey());
//
//            // 토큰에서 이메일 추출
//            String email = jwtTokenizer.getEmailFromToken(token);
//
//            // 이메일로 Employee 찾기
//            Optional<Employee> optionalEmployee = employeeRepository.findByEmail(email);
//            Employee employee = optionalEmployee.orElseThrow(() -> new BusinessLogicException(ExceptionCode.EMPLOYEE_NOT_FOUND));
//
//            // 출근 상태를 '퇴근'으로 변경
//            employee.setAttendanceStatus(Employee.AttendanceStatus.CLOCKED_OUT);
//            employeeRepository.save(employee); // 상태 변경 후 저장
//
//
//            // Redis에 토큰 무효화 처리
//            redisTemplate.opsForValue().set(token, "logout", 10, TimeUnit.MINUTES);
//
//            return true;
//        } catch (JwtException | BusinessLogicException e) {
//            // 예외 발생 시 로그아웃 실패 처리
//            return false;
//        }
//    }

    public boolean logout(String username) {
        try {
            // 이메일로 Employee 찾기
            Optional<Employee> optionalEmployee = employeeRepository.findByEmail(username);
            Employee employee = optionalEmployee.orElseThrow(() -> new BusinessLogicException(ExceptionCode.EMPLOYEE_NOT_FOUND));

            // 상태를 LOGGED_OUT으로 변경
            employee.setStatus(Employee.EmployeeStatus.LOGGED_OUT);
            employeeRepository.save(employee); // 상태 변경 후 저장

            // Redis에서 토큰 삭제 로직 실행
            return jwtTokenizer.deleteRegisterToken(username);

        } catch (BusinessLogicException e) {
            // 예외 발생 시 로그아웃 실패 처리
            return false;
        }
    }

    public boolean isTokenValid(String userName) {
        return redisTemplate.hasKey(userName);
    }
}

