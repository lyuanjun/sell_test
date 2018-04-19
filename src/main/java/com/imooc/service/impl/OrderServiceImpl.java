package com.imooc.service.impl;

import com.imooc.converter.OrderMaster2OderDtoConverter;
import com.imooc.dataobject.OrderDetail;
import com.imooc.dataobject.OrderMaster;
import com.imooc.dataobject.ProductInfo;
import com.imooc.dto.CartDTO;
import com.imooc.dto.OrderDTO;
import com.imooc.enums.OrderStatusEnum;
import com.imooc.enums.PayStatusEnum;
import com.imooc.enums.ResultEnum;
import com.imooc.exception.SellExecption;
import com.imooc.repository.OrderDetailRepository;
import com.imooc.repository.OrderMasterRepository;
import com.imooc.service.OrderService;
import com.imooc.service.ProductService;
import com.imooc.utils.KeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ProductService productService;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private OrderMasterRepository orderMasterRepository;

    @Override
    @Transactional
    public OrderDTO create(OrderDTO orderDTO) {
        BigDecimal orderAmount = new BigDecimal(BigInteger.ZERO);
        String orderId = KeyUtil.getUniqueKey();
        //1、查询商品数量、单价
        for (OrderDetail orderDetail:orderDTO.getOrderDetailList()) {
            ProductInfo productInfo = productService.findOne(orderDetail.getProductId());
            if(productInfo==null){
                throw new SellExecption(ResultEnum.PRODUCT_NOT_EXIST);
                //throw new DifferentStatusException();
            }
            //2、计算出总价及订单详情
            orderAmount = productInfo.getProductPrice().multiply(new BigDecimal(orderDetail.getProductQuantity())).add(orderAmount);
            orderDetail.setDetailId(KeyUtil.getUniqueKey());
            BeanUtils.copyProperties(productInfo,orderDetail);
            orderDetail.setOrderId(orderId);
            orderDetailRepository.save(orderDetail);
        }
        //3、写入订单主表
        OrderMaster orderMaster = new OrderMaster();
        orderDTO.setOrderId(orderId);
        BeanUtils.copyProperties(orderDTO,orderMaster);
        orderMaster.setOrderAmount(orderAmount);
        orderMaster.setOrderStatus(OrderStatusEnum.NEW.getCode());
        orderMaster.setPayStatus(PayStatusEnum.WAIT.getCode());
        orderMasterRepository.save(orderMaster);
        //4、减库存
        List<CartDTO> cartDTOList = orderDTO.getOrderDetailList().stream().map(e -> new CartDTO(e.getProductId(),e.getProductQuantity())).collect(Collectors.toList());
        productService.decreaseStock(cartDTOList);
        return orderDTO;
    }

    @Override
    public OrderDTO findOne(String orderId) {
        OrderMaster orderMaster = orderMasterRepository.findOne(orderId);
        if(orderMaster==null){
            throw new SellExecption(ResultEnum.ORDER_NOT_EXIST);
        }
        List<OrderDetail> orderDetailList = orderDetailRepository.findByOrderId(orderId);
        if(CollectionUtils.isEmpty(orderDetailList)){
            throw new SellExecption(ResultEnum.ORDERDETAIL_NOT_EXIST);
        }
        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(orderMaster,orderDTO);
        orderDTO.setOrderDetailList(orderDetailList);
        return orderDTO;
    }

    @Override
    public Page<OrderDTO> findList(String buyerOpenid, Pageable pageable) {
        Page<OrderMaster> orderMasterPage = orderMasterRepository.findByBuyerOpenid(buyerOpenid,pageable);
        Page<OrderDTO> orderDTOPage = new PageImpl<OrderDTO>(OrderMaster2OderDtoConverter.convert(orderMasterPage.getContent()),pageable,orderMasterPage.getTotalElements());
        return orderDTOPage;
    }

    @Override
    @Transactional
    public OrderDTO cancel(OrderDTO orderDTO) {
        //判断订单状态
        if(!orderDTO.getOrderStatus().equals(OrderStatusEnum.NEW.getCode())){
            log.error("【取消订单】失败，订单状态有误，orderId={} ， orderStatus={}",orderDTO.getOrderId(),orderDTO.getOrderStatus());
            throw new SellExecption(ResultEnum.ORDER_STATUS_ERROR);
        }
        //修改订单状态
        OrderMaster orderMaster = new OrderMaster();
        orderDTO.setOrderStatus(OrderStatusEnum.CANCEL.getCode());
        BeanUtils.copyProperties(orderDTO,orderMaster);
        OrderMaster updateResult = orderMasterRepository.save(orderMaster);
        if(updateResult==null){
            log.error("【取消订单】失败，订单状态更新失败,orderMaster={}",orderMaster);
            throw new SellExecption(ResultEnum.ORDER_UPDATE_FAIL);
        }
        //加库存
        List<OrderDetail> orderDetailList = orderDetailRepository.findByOrderId(orderMaster.getOrderId());
        if(CollectionUtils.isEmpty(orderDetailList)){
            log.error("【取消订单】失败，订单中无商品详情，orderDto={}",orderDTO);
            throw new SellExecption(ResultEnum.ORDER_DETAIL_EMPTY);
        }
        List<CartDTO> cartDTOList = orderDetailList.stream().map(e -> new CartDTO(e.getProductId(),e.getProductQuantity())).collect(Collectors.toList());
        productService.increaseStock(cartDTOList);
        //如果支付需退款
        if (orderMaster.getPayStatus().equals(PayStatusEnum.SUCCESS)){
            //TODO
        }
        return orderDTO;
    }

    @Override
    public OrderDTO finish(OrderDTO orderDTO) {
        //判断订单状态
        if(!orderDTO.getOrderStatus().equals(OrderStatusEnum.NEW.getCode())){
            log.error("【完结订单】失败，订单状态有误，orderId={} ， orderStatus={}",orderDTO.getOrderId(),orderDTO.getOrderStatus());
            throw new SellExecption(ResultEnum.ORDER_STATUS_ERROR);
        }
        //修改订单状态
        OrderMaster orderMaster = new OrderMaster();
        orderDTO.setOrderStatus(OrderStatusEnum.FINISHED.getCode());
        BeanUtils.copyProperties(orderDTO,orderMaster);
        OrderMaster updateResult = orderMasterRepository.save(orderMaster);
        if(updateResult==null){
            log.error("【完结订单】失败，订单状态更新失败,orderMaster={}",orderMaster);
            throw new SellExecption(ResultEnum.ORDER_UPDATE_FAIL);
        }
        return orderDTO;
    }

    @Override
    public OrderDTO paid(OrderDTO orderDTO) {
        //判断订单状态
        if(!orderDTO.getOrderStatus().equals(OrderStatusEnum.NEW.getCode())){
            log.error("【支付订单】失败，订单状态有误，orderId={} ， orderStatus={}",orderDTO.getOrderId(),orderDTO.getOrderStatus());
            throw new SellExecption(ResultEnum.ORDER_STATUS_ERROR);
        }
        //判断支付状态
        if(!orderDTO.getPayStatus().equals(PayStatusEnum.WAIT.getCode())){
            log.error("【支付订单】失败，订单支付状态有误，orderId={} ， payStatus={}",orderDTO.getOrderId(),orderDTO.getPayStatus());
            throw new SellExecption(ResultEnum.ORDER_PAY_STATUS_ERROR);
        }
        //修改支付状态
        OrderMaster orderMaster = new OrderMaster();
        orderDTO.setPayStatus(PayStatusEnum.SUCCESS.getCode());
        BeanUtils.copyProperties(orderDTO,orderMaster);
        OrderMaster updateResult = orderMasterRepository.save(orderMaster);
        if(updateResult==null){
            log.error("【支付订单】失败，订单支付状态更新失败,orderMaster={}",orderMaster);
            throw new SellExecption(ResultEnum.ORDER_UPDATE_FAIL);
        }
        return orderDTO;
    }

    @Override
    public Page<OrderDTO> findList(Pageable pageable) {
        Page<OrderMaster> orderMasterPage = orderMasterRepository.findAll(pageable);
        Page<OrderDTO> orderDTOPage = new PageImpl<OrderDTO>(OrderMaster2OderDtoConverter.convert(orderMasterPage.getContent()),pageable,orderMasterPage.getTotalElements());
        return orderDTOPage;
    }
}
