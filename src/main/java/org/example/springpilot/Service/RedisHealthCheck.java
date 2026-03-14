package org.example.springpilot.Service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RedisHealthCheck {
    //checking if the connection with redis is successfull or nor.

    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostConstruct
    public void checkRedisConnection() {
        try {
            String pong = redisTemplate.getConnectionFactory()
                    .getConnection()
                    .ping();
            log.info("✅ Redis connected! PING response: " + pong);
        } catch (Exception e) {
            log.error("❌ Redis connection FAILED: " + e.getMessage());
        }
    }
}
