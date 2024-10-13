package com.auth.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedisController {

    private final RedisTestService redisTestService;

    @Autowired
    public RedisController(RedisTestService redisTestService) {
        this.redisTestService = redisTestService;
    }

    @GetMapping("/test-redis")
    public String testRedis() {
        redisTestService.testRedis();
        return "Check console for Redis test output.";
    }
}
