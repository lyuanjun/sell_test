package com.imooc.repository;

import com.imooc.dataobject.OrderDetail;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderDetailRepositoryTest {

    @Autowired
    private OrderDetailRepository repository;

    @Test
    public void saveTest() {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setDetailId("123123123");
        orderDetail.setOrderId("11111111");
        orderDetail.setProductIcon("http://xxxx.jpg");
        orderDetail.setProductId("11111112");
        orderDetail.setProductName("炸鸡汉堡2");
        orderDetail.setProductPrice(new BigDecimal(20));
        orderDetail.setProductQuantity(1);

        OrderDetail result = repository.save(orderDetail);
        Assert.assertNotNull(result);
    }

    @Test
    public void findByOrderId() throws Exception {
        List<OrderDetail> orderDetailList = repository.findByOrderId("11111111");
        Assert.assertNotEquals(0, orderDetailList.size());
    }

}