package com.imooc.aspect;


import com.imooc.constant.CookieConstant;
import com.imooc.exception.SellerAuthorizeException;
import com.imooc.utils.CookieUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpRequest;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@Slf4j
public class SellerAuthorizeAspect {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Pointcut("execution(public * com.imooc.controller.Seller*.*(..)) && !execution(public * com.imooc.controller.SellerUserController.*(..))")
    public void verify(){

    }

    @Before("verify()")
    public void doVerify(){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            Cookie cookie = CookieUtil.get(request, CookieConstant.TOKEN);
            if(cookie==null){
                log.warn("【登录效验】cookies中找不到token");
                throw new SellerAuthorizeException();
            }
            String tokenValue = stringRedisTemplate.opsForValue().get(cookie.getValue());
            if(StringUtils.isEmpty(tokenValue)){
                log.warn("【登录效验】redis中找不到token");
                throw new SellerAuthorizeException();
        }

    }
}
