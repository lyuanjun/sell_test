package com.imooc.service.impl;

import com.imooc.config.WechatAccountConfig;
import com.imooc.dto.OrderDTO;
import com.imooc.service.PushMessageService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class PushMessageServiceImpl implements PushMessageService{

    @Autowired
    private WechatAccountConfig wechatAccountConfig;

    @Autowired
    private WxMpService wxMpService;

    @Override
    public void orderStatus(OrderDTO orderDTO) {
        WxMpTemplateMessage wxMpTemplateMessage = new WxMpTemplateMessage();
        wxMpTemplateMessage.setTemplateId(wechatAccountConfig.getTemplateId().get("ordertemplateId"));
        wxMpTemplateMessage.setToUser("oRctp1RgLvZRGuKV79vyjdNnahzw");
        List<WxMpTemplateData> data = Arrays.asList(
                new WxMpTemplateData("frist","亲，您的订单状态改变了"),
                new WxMpTemplateData("seller1","LYJ大商城"),
                new WxMpTemplateData("seller2","18874903232"),
                new WxMpTemplateData("seller3",orderDTO.getOrderId()),
                new WxMpTemplateData("seller4",orderDTO.getOrderStatusEnum().getMessage()),
                new WxMpTemplateData("seller5","￥"+orderDTO.getOrderAmount()),
                new WxMpTemplateData("opinion","欢迎下次光临！！！")
        );
        wxMpTemplateMessage.setData(data);
        try {
            wxMpService.getTemplateMsgService().sendTemplateMsg(wxMpTemplateMessage);
        } catch (WxErrorException e) {
            log.error("【微信模版消息】发送失败, {}", e);
        }
    }


}
