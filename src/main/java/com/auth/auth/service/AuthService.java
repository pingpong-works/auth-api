package com.auth.auth.service;

import com.auth.auth.jwt.JwtTokenizer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {
    private final JwtTokenizer jwtTokenizer;

    public AuthService(JwtTokenizer jwtTokenizer) {
        this.jwtTokenizer = jwtTokenizer;
    }
    public boolean logout(String username){
        //    레디스에서 username(이메일)을 기준으로 저장된 토큰을 삭제
        return jwtTokenizer.deleteRegisterToken(username);
    }
}