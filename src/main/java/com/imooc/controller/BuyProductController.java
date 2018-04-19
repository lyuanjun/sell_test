package com.imooc.controller;

import com.imooc.VO.ProductInfoVO;
import com.imooc.VO.ProductVO;
import com.imooc.VO.ResultVO;
import com.imooc.dataobject.ProductCategory;
import com.imooc.dataobject.ProductInfo;
import com.imooc.repository.ProductCategoryRepository;
import com.imooc.repository.ProductInfoRepository;
import com.imooc.utils.ResultVOUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/buyer/product")
public class BuyProductController {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    @Autowired
    private ProductInfoRepository productInfoRepository;

    @RequestMapping("/list")
    public ResultVO list(){

        //查出所有的商品
        List<ProductInfo> productInfoList = productInfoRepository.findAll();
        //所有的类目
        //List<Integer> categroyList = new ArrayList<Integer>();
        //java8 lambda
        List<Integer> categroyList = productInfoList.stream().map(e -> e.getCategoryType()).collect(Collectors.toList());
        List<ProductCategory> productCategoryList = productCategoryRepository.findByCategoryTypeIn(categroyList);
        //封装数据
        List<ProductVO> productVOList = new ArrayList<ProductVO>();
        for (ProductCategory productCategory: productCategoryList) {
            ProductVO productVO = new ProductVO();
            productVO.setCategoryName(productCategory.getCategoryName());
            productVO.setCategorytype(productCategory.getCategoryType());
            List<ProductInfoVO> productInfoVOList = new ArrayList<ProductInfoVO>();
            for (ProductInfo productInfo: productInfoList) {
                if(productCategory.getCategoryType().equals(productInfo.getCategoryType())){
                    ProductInfoVO productInfoVO = new ProductInfoVO();
                    BeanUtils.copyProperties(productInfo,productInfoVO);
                    productInfoVOList.add(productInfoVO);
                }
            }
            productVO.setProductInfoVOList(productInfoVOList);
            productVOList.add(productVO);
        }

        /*ResultVO resultVO = new ResultVO();
        resultVO.setData(productVOList);
        resultVO.setCode(0);
        resultVO.setMsg("成功！");*/
        return ResultVOUtil.success(productVOList);
    }

}
