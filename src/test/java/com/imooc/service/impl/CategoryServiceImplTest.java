package com.imooc.service.impl;

import com.imooc.dataobject.ProductCategory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CategoryServiceImplTest {

    @Autowired
    private CategoryServiceImpl categoryServiceimpl;

    @Test
    public void findOne() {
        ProductCategory productCategory = categoryServiceimpl.findOne(1);
        Assert.assertNotNull(productCategory);
    }

    @Test
    public void findAll() {
        List<ProductCategory> list =categoryServiceimpl.findAll();
        Assert.assertNotEquals(0,list.size());
    }

    @Test
    public void findByCategoryTypeIn() {
        List params = Arrays.asList(1,2,3,4);
        List<ProductCategory> list =categoryServiceimpl.findByCategoryTypeIn(params);
        Assert.assertNotEquals(0,list.size());
    }

    @Test
    public void save() {
        ProductCategory productCategory = new ProductCategory("中西结合",4);
        ProductCategory result =categoryServiceimpl.save(productCategory);
        Assert.assertNotNull(result);
    }
}