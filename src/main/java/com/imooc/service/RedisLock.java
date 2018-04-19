package com.imooc.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RedisLock {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 加锁
     * @param key
     * @param value  当前时间+超时时间
     * @return
     */
    public boolean lock(String key,String value){
        if (stringRedisTemplate.opsForValue().setIfAbsent(key,value)){
            return true;
        }

        String currentValue = stringRedisTemplate.opsForValue().get(key);
        //如果锁过期
        if(!StringUtils.isNotEmpty(currentValue) && Long.parseLong(currentValue)<System.currentTimeMillis()){
            //获取上一个锁的事时间
            String oldValue = stringRedisTemplate.opsForValue().getAndSet(key,value);
            if(StringUtils.isNotEmpty(oldValue) && oldValue.equals(currentValue)){
                return true;
            }
        }

        return false;
    }

    public void unLock(String key,String value){
        try {
            String currentValue = stringRedisTemplate.opsForValue().get(key);
            if(StringUtils.isNotEmpty(currentValue) && currentValue.equals(value)){
                stringRedisTemplate.opsForValue().getOperations().delete(key);
            }
        }catch (Exception e){
            log.error("【redis分布式锁】出错");
        }

    }
}
