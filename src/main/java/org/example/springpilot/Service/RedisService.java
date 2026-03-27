package org.example.springpilot.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisService {
    //we'll make use of RedisService to interact with Redis

    @Autowired
    private RedisTemplate redisTemplate;

    //we will create two methods one of which will set the value and the other will get the value.

    public <T> T get(String key, Class <T> entityClass){
        try{
            Object o = redisTemplate.opsForValue().get(key);//this Object class must be converted to POJO class
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(o.toString(), entityClass);
        }
        catch(Exception e){
            log.error("An error occurred: ",e);
            return null;
        }
    }

    public void set(String key, Object o, Long ttl){
        try{
            ObjectMapper out = new ObjectMapper();
            String jsonValue = out.writeValueAsString(o);
            redisTemplate.opsForValue().set(key,jsonValue,ttl, TimeUnit.SECONDS);
            //here we are setting a key value with a expiry time/value
        }
        catch(Exception e){
            log.error("An error occured: ",e);
        }
    }

}
