package com.auth.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisTestService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisTestService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void testRedis() {
        String key = "testKey";
        String value = "testValue";

        // Redis에 값 저장
        redisTemplate.opsForValue().set(key, value);
        System.out.println("Saved testKey in Redis");

        // Redis에서 값 가져오기
        String redisValue = (String) redisTemplate.opsForValue().get(key);
        System.out.println("Retrieved value from Redis: " + redisValue);
    }
}
