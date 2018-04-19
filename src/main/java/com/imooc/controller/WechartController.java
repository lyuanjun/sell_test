package com.imooc.controller;

import com.imooc.enums.ResultEnum;
import com.imooc.exception.SellExecption;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;

@Controller
@RequestMapping("/wechat")
@Slf4j
public class WechartController {

    @Autowired
    private WxMpService wxMpService;
   /* @Autowired
    private StringRedisTemplate stringRedisTemplate;*/

    @RequestMapping("/authorize")
    public String authorize(@RequestParam("returnUrl") String returnUrl){
        String url = "http://lyj.mynatapp.cc/sell/wechat/userInfo";
        String redirectUrl = wxMpService.oauth2buildAuthorizationUrl(url, WxConsts.OAUTH2_SCOPE_BASE, URLEncoder.encode(returnUrl));
        return "redirect:" + redirectUrl;
    }

    @RequestMapping("/userInfo")
    public String userInfo(@RequestParam("code") String code,@RequestParam("state") String resultUrl){
        WxMpOAuth2AccessToken wxMpOAuth2AccessToken = new WxMpOAuth2AccessToken();
        try {
            wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
        } catch (WxErrorException e) {
            log.error("【微信网页授权】{}",e);
            throw new SellExecption(ResultEnum.WECHAT_MP_ERROR.getCode(),e.getError().getErrorMsg());
        }
        String openid = wxMpOAuth2AccessToken.getOpenId();
      //  stringRedisTemplate.opsForValue().set("wx_token",wxMpOAuth2AccessToken.getAccessToken());
        return "redirect:" + resultUrl + "?openid=" + openid;
    }

}
