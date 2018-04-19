package com.imooc.controller;

import com.imooc.dto.OrderDTO;
import com.imooc.enums.ResultEnum;
import com.imooc.exception.SellExecption;
import com.imooc.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/pay")
@Slf4j
public class PayController {

    @Autowired
    private OrderService orderService;

    @RequestMapping("/create")
    public void create(@RequestParam("orderId") String orderId,@RequestParam("returnUrl") String returnUrl){

        //查询订单
        OrderDTO orderDTO = orderService.findOne(orderId);
        if(orderDTO == null){
            throw new SellExecption(ResultEnum.ORDER_NOT_EXIST);
        }

        //发起支付


    }
}
