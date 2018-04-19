package com.imooc.controller;

import com.imooc.config.ProjectUrlConfig;
import com.imooc.constant.CookieConstant;
import com.imooc.constant.RedisConstant;
import com.imooc.dataobject.SellerInfo;
import com.imooc.enums.ResultEnum;
import com.imooc.service.SellerService;
import com.imooc.utils.CookieUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
@Slf4j
@RequestMapping("/seller/user")
public class SellerUserController {

    @Autowired
    private SellerService sellerService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ProjectUrlConfig projectUrlConfig;

    @GetMapping("/login")
    public ModelAndView login(@RequestParam("username")String openid,@RequestParam("password") String password, Map<String,Object> map, HttpServletResponse httpServletResponse){

        //1、openid匹配
        SellerInfo sellerInfo = sellerService.findSellerInfoByOpenid(openid);
        if(sellerInfo==null){
            log.error("【用户登录】失败，没有此用户！");
            map.put("msg", ResultEnum.LOGIN_FAIL.getMessage());
            map.put("url", "/sell/seller/order/list");
            return new ModelAndView("common/error");
        }

        if(!sellerInfo.getPassword().equals(password)){
            log.error("【用户登录】失败，密码错误！");
            map.put("msg", ResultEnum.LOGOUT_FAIL_PASSWORD.getMessage());
            map.put("url", "/sell/seller/order/list");
            return new ModelAndView("common/error");
        }

        //设置token进入redis
        String token = String.format(RedisConstant.TOKEN_PREFIX,UUID.randomUUID().toString());
        Integer time = RedisConstant.EXPIRE;
        redisTemplate.opsForValue().set(token,openid,time, TimeUnit.SECONDS);
        //设置token进入cookies
        CookieUtil.set(httpServletResponse, CookieConstant.TOKEN,token,time);
        return new ModelAndView("redirect:" + projectUrlConfig.getSell() +  "/sell/seller/order/list");
    }

    @GetMapping("/logout")
    public ModelAndView logout(HttpServletRequest request,HttpServletResponse response,Map<String,Object> map){

        //获取cookies的token
        Cookie cookie = CookieUtil.get(request,CookieConstant.TOKEN);

        if(cookie != null){
            //删除redis的键值对
            redisTemplate.opsForValue().getOperations().delete(cookie.getValue());
            //删除cookies的token
            CookieUtil.set(response,CookieConstant.TOKEN,null,0);
        }

        map.put("msg",ResultEnum.LOGOUT_SUCCESS.getMessage());
        map.put("url", "/sell/seller/product/list");
        return new ModelAndView("common/success", map);
    }

    @GetMapping("/loginUI")
    public ModelAndView loginUI(){
        return new ModelAndView("login/login");
    }

}
