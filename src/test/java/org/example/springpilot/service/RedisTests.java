package org.example.springpilot.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class RedisTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Disabled
    @Test
    void testRedis(){
        redisTemplate.opsForValue().set("email","shikhar@email.com");
        Object email = redisTemplate.opsForValue().get("email");
        int a=1;
    }
}
