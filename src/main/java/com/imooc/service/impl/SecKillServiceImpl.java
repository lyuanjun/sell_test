package com.imooc.service.impl;

import com.imooc.exception.SellExecption;
import com.imooc.service.RedisLock;
import com.imooc.utils.KeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SecKillServiceImpl {

    @Autowired
    private RedisLock redisLock;

    static Map<String,Integer> products;
    static Map<String,Integer> stock;
    static Map<String,String> orders;
    static
    {
        products = new HashMap<>();
        stock = new HashMap<>();
        orders = new HashMap<>();
        products.put("123456",10000);
        stock.put("123456",10000);
    }

    private String queryMap(String productId){

        return "国庆活动，S8手机特价，限量份"
                + products.get(productId)
                +" 还剩：" + stock.get(productId) + " 部"
                +" 该商品成功下单用户数目： "
                + orders.size() + " 人";
    }

    public String querySecKillProductInfo(String productId){
        return this.queryMap(productId);
    }

    public void orderProductMockDiffUser(String productId){

        //加锁
        String currenttime = String.valueOf(System.currentTimeMillis()+10*1000);
        boolean flag = redisLock.lock(productId,currenttime);
        if(!flag){
            throw new SellExecption(101,"购买人数太多，请稍后再操作");
        }
        int stockNum = stock.get(productId);
        if(stockNum == 0){
            throw new SellExecption(100,"活动结束");
        }else{
            orders.put(KeyUtil.getUniqueKey(),productId);
            stockNum = stockNum-1;
            try {
                Thread.sleep(100);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            stock.put(productId,stockNum);
        }
        redisLock.unLock(productId,currenttime);
    }

}
