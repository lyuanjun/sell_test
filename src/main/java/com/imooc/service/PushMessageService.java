package com.imooc.service;

import com.imooc.dto.OrderDTO;

public interface PushMessageService {

    /**
     * 推送消息
     * @param orderDTO
     */
    void orderStatus(OrderDTO orderDTO);
}
