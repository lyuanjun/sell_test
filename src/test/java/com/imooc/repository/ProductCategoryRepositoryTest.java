package com.imooc.repository;

import com.imooc.dataobject.ProductCategory;
import com.imooc.SellApplication;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SellApplication.class)
public class ProductCategoryRepositoryTest {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Test
    public void saveTest(){
        ProductCategory productCategory = new ProductCategory("中式餐食2",10);
        ProductCategory result = productCategoryRepository.save(productCategory);
        //Assert.assertNotNull(result);
    }

    @Test
    public void updateTest(){
        ProductCategory productCategory = productCategoryRepository.findOne(1);
        productCategory.setCategoryType(2);
        productCategoryRepository.save(productCategory);
    }

    @Test
    public void findOneTest(){
        ProductCategory productCategory = productCategoryRepository.findOne(1);
        System.out.println(productCategory.toString());
    }

    @Test
    public void findByTypeTest(){
        List list = Arrays.asList(1,2,3);
        List<ProductCategory> productCategoryList = productCategoryRepository.findByCategoryTypeIn(list);
        Assert.assertNotEquals(0, productCategoryList.size());
    }

}