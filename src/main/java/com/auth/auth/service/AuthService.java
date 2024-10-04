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
import java.util.concurrent.TimeUnit;

//@Service
//@Transactional
//public class AuthService {
//    private final JwtTokenizer jwtTokenizer;
//    private final RedisTemplate<String, Object> redisTemplate;
//    private final EmployeeRepository employeeRepository;
//
//    public AuthService(JwtTokenizer jwtTokenizer, RedisTemplate<String, Object> redisTemplate, EmployeeRepository employeeRepository) {
//        this.jwtTokenizer = jwtTokenizer;
//        this.redisTemplate = redisTemplate;
//        this.employeeRepository = employeeRepository;
//    }
//    public boolean logout(String username){
//        //    레디스에서 username(이메일)을 기준으로 저장된 토큰을 삭제
//        boolean tokenDeleted = jwtTokenizer.deleteRegisterToken(username);
//
//        Employee employee = employeeRepository.findByEmail(username)
//                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.EMPLOYEE_NOT_FOUND));
//
//        employee.setStatus(Employee.EmployeeStatus.LOGGED_OUT);
//        employeeRepository.save(employee);
//
//        return tokenDeleted;
//    }
//}
@Service
public class AuthService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtTokenizer jwtTokenizer;
    private final EmployeeRepository employeeRepository;

    public AuthService(RedisTemplate<String, Object> redisTemplate, JwtTokenizer jwtTokenizer, EmployeeRepository employeeRepository) {
        this.redisTemplate = redisTemplate;
        this.jwtTokenizer = jwtTokenizer;
        this.employeeRepository = employeeRepository;
    }

    public boolean logout(String token) {
        try {
            // 토큰 검증
            jwtTokenizer.verifySignature(token, jwtTokenizer.getSecretKey());

            // 토큰에서 이메일 추출
            String email = jwtTokenizer.getEmailFromToken(token);

            // 이메일로 Employee 찾기
            Optional<Employee> optionalEmployee = employeeRepository.findByEmail(email);
            Employee employee = optionalEmployee.orElseThrow(() -> new BusinessLogicException(ExceptionCode.EMPLOYEE_NOT_FOUND));

            // Redis에 토큰 무효화 처리
            redisTemplate.opsForValue().set(token, "logout", 10, TimeUnit.MINUTES);

            return true;
        } catch (JwtException | BusinessLogicException e) {
            // 예외 발생 시 로그아웃 실패 처리
            return false;
        }
    }
    public boolean isTokenValid(String userName) {
        return redisTemplate.hasKey(userName);
    }
}
