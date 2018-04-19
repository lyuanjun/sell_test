package com.imooc.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class LoggerTest2 {

    @Test
    public void test(){
        String user = "im";
        String password = "12345";
        log.info("info...");
        log.info("user: {} , password: {}",user,password);
        log.error("error...");
        log.debug("debug...");
        log.warn("warn...");
    }
}
