package com.imooc.service.impl;

import com.imooc.dataobject.ProductInfo;
import com.imooc.dto.CartDTO;
import com.imooc.enums.ProductStatusEnum;
import com.imooc.enums.ResultEnum;
import com.imooc.exception.SellExecption;
import com.imooc.repository.ProductInfoRepository;
import com.imooc.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductInfoRepository productInfoRepository;

    @Override
    public ProductInfo findOne(String productId) {
        return productInfoRepository.findOne(productId);
    }

    @Override
    public List<ProductInfo> findUpAll() {
        return productInfoRepository.findByProductStatus(ProductStatusEnum.UP.getCode());
    }

    @Override
    public Page<ProductInfo> findAll(Pageable pageable) {
        return productInfoRepository.findAll(pageable);
    }

    @Override
    public ProductInfo save(ProductInfo productInfo) {
        return productInfoRepository.save(productInfo);
    }

    @Override
    //@Transactional
    public void increaseStock(List<CartDTO> cartDTOList) {
        for ( CartDTO cartDTO :  cartDTOList) {
            ProductInfo productInfo = this.findOne(cartDTO.getProductId());
            if(productInfo == null){
                throw new SellExecption(ResultEnum.PRODUCT_NOT_EXIST);
            }
            Integer stock = productInfo.getProductStock()+cartDTO.getProductQuantity();
            productInfo.setProductStock(stock);
            productInfoRepository.save(productInfo);
        }
    }

    @Override
    //@Transactional
    public void decreaseStock(List<CartDTO> cartDTOList) {
        for ( CartDTO cartDTO :  cartDTOList) {
            ProductInfo productInfo = this.findOne(cartDTO.getProductId());
            if(productInfo == null){
                throw new SellExecption(ResultEnum.PRODUCT_NOT_EXIST);
            }
            Integer stock = productInfo.getProductStock()-cartDTO.getProductQuantity();
            if (stock<0){
                throw new SellExecption(ResultEnum.PRODUCT_STOCK_ERROR);
            }
            productInfo.setProductStock(stock);
            productInfoRepository.save(productInfo);
        }
    }

    @Override
    public ProductInfo onSale(String productId) {
        ProductInfo productInfo = productInfoRepository.findOne(productId);
        if(productInfo == null){
            throw new SellExecption(ResultEnum.PRODUCT_NOT_EXIST);
        }
        if(productInfo.getProductStatusEnum() == ProductStatusEnum.UP){
            throw new SellExecption(ResultEnum.PRODUCT_STATUS_ERROR);
        }
        productInfo.setProductStatus(ProductStatusEnum.UP.getCode());
        return productInfoRepository.save(productInfo);
    }

    @Override
    public ProductInfo offSale(String productId) {
        ProductInfo productInfo = productInfoRepository.findOne(productId);
        if(productInfo == null){
            throw new SellExecption(ResultEnum.PRODUCT_NOT_EXIST);
        }
        if(productInfo.getProductStatusEnum() == ProductStatusEnum.DOWN){
            throw new SellExecption(ResultEnum.PRODUCT_STATUS_ERROR);
        }
        productInfo.setProductStatus(ProductStatusEnum.DOWN.getCode());
        return productInfoRepository.save(productInfo);
    }
}
