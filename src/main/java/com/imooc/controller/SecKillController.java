package com.imooc.controller;

import com.imooc.service.impl.SecKillServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/skill")
@Slf4j
public class SecKillController {

    @Autowired
    private SecKillServiceImpl secKillService;

    @GetMapping("/query/{productId}")
    public String query(@PathVariable String productId){
        return secKillService.querySecKillProductInfo(productId);
    }

    @GetMapping("/order/{productId}")
    public String skill(@PathVariable String productId){
        log.info("@skill request,productId:"+productId);
        secKillService.orderProductMockDiffUser(productId);
        return secKillService.querySecKillProductInfo(productId);
    }



}
