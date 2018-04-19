package com.imooc.service.impl;

import com.imooc.dataobject.ProductInfo;
import com.imooc.enums.ProductStatusEnum;
import com.imooc.service.ProductService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductServiceImplTest {

    @Autowired
    private ProductService productService;

    @Test
    public void findOne() {
        ProductInfo productInfo = productService.findOne("123456");
        Assert.assertNotNull(productInfo);
    }

    @Test
    public void findUpAll() {
        List<ProductInfo> result = productService.findUpAll();
        Assert.assertNotEquals(0,result.size());
    }

    @Test
    public void findAll() {
        PageRequest pageRequest = new PageRequest(0,5);
        Page<ProductInfo> result = productService.findAll(pageRequest);
        System.out.println(result.getTotalElements()+","+result.getSize());
        Assert.assertNotEquals(0,result.getTotalElements());
    }

    @Test
    public void save() {
        ProductInfo productInfo = new ProductInfo();
        productInfo.setProductId("123456");
        productInfo.setProductName("家常豆腐");
        productInfo.setProductPrice(new BigDecimal(16));
        productInfo.setProductStock(100);
        productInfo.setProductDescription("贼好吃的家常豆腐");
        productInfo.setProductIcon("http://xxxx.jpg");
        productInfo.setProductStatus(ProductStatusEnum.UP.getCode());
        productInfo.setCategoryType(1);
        productService.save(productInfo);
    }

    @Test
    public void onSale() {
        ProductInfo result = productService.onSale("123456");
        Assert.assertEquals(ProductStatusEnum.UP, result.getProductStatusEnum());
    }

    @Test
    public void offSale() {
        ProductInfo result = productService.offSale("123456");
        Assert.assertEquals(ProductStatusEnum.DOWN, result.getProductStatusEnum());
    }
}