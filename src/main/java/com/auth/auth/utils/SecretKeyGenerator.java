package com.auth.auth.utils;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;

public class SecretKeyGenerator {
    public static void main(String[] args) throws Exception {
        // HMAC 알고리즘을 위한 비밀 키 생성
        KeyGenerator keyGen = KeyGenerator.getInstance("HmacSha256");
        keyGen.init(256); // 256 비트 키 생성
        SecretKey secretKey = keyGen.generateKey();

        // Base64 인코딩된 비밀 키 출력
        String base64EncodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        System.out.println("Base64 Encoded Key: " + base64EncodedKey);
    }
}
