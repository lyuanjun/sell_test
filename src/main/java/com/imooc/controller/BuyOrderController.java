package com.imooc.controller;

import com.imooc.VO.ResultVO;
import com.imooc.converter.OrderForm2OrderDTOConverter;
import com.imooc.dto.OrderDTO;
import com.imooc.enums.ResultEnum;
import com.imooc.exception.SellExecption;
import com.imooc.form.OrderForm;
import com.imooc.service.BuyerService;
import com.imooc.service.OrderService;
import com.imooc.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/buy/order")
@Slf4j
public class BuyOrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private BuyerService buyerService;

    /**
     *创建订单
     */
    @RequestMapping("/create")
    public ResultVO<Map<String,String>> create(@Valid OrderForm orderForm,BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            log.error("【创建订单】参数不正确，orderForm = {}",orderForm);
                throw new SellExecption(ResultEnum.PARAM_ERROR.getCode(),bindingResult.getFieldError().getDefaultMessage());
        }

        OrderDTO orderDTO = OrderForm2OrderDTOConverter.convert(orderForm);
        if(CollectionUtils.isEmpty(orderDTO.getOrderDetailList())){
            log.error("【创建订单】购物车不能为空，orderForm = {}",orderForm);
            throw new SellExecption(ResultEnum.CART_EMPTY);
        }

        OrderDTO resultOrderDto =  orderService.create(orderDTO);

        //webSocket.sendMessage("有新的订单了！");
        Map<String,String> map = new HashMap<String,String>();
        map.put("orderId",resultOrderDto.getOrderId());
        return ResultVOUtil.success(map);
    }

    /**
     *订单列表
     */
    @RequestMapping("/list")
    public ResultVO<Map<String,String>> list(@RequestParam("openid") String openid,
                                             @RequestParam(value = "page" , defaultValue = "0") Integer page,
                                             @RequestParam(value = "size", defaultValue = "10") Integer size){
        //判断openid
        if(StringUtils.isEmpty(openid)){
            log.error("【查询订单列表】openid为空");
            throw new SellExecption(ResultEnum.PARAM_ERROR);
        }
        //查询列表
        PageRequest pageRequest = new PageRequest(page,size);
        Page<OrderDTO> orderDTOPage = orderService.findList(openid,pageRequest);

        return ResultVOUtil.success(orderDTOPage.getContent());
    }


    /**
    *订单详情
    */
    @RequestMapping("/detail")
    public ResultVO detail(@RequestParam("openid")String openid,@RequestParam("orderId")String orderId){

        OrderDTO orderDTO = buyerService.findOrderOne(openid,orderId);

        return ResultVOUtil.success(orderDTO);
    }

    /**
    *取消订单
     */
    @RequestMapping("/cancel")
    public ResultVO cancel(@RequestParam("openid")String openid,@RequestParam("orderId")String orderId){

        buyerService.cancelOrder(openid,orderId);

        return ResultVOUtil.success();
    }
}
