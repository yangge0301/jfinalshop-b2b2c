package com.jfinalshop.api.controller.shop;

import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.ProductCategory;
import com.jfinalshop.service.ProductCategoryService;
import net.hasor.core.Inject;

import java.util.List;

public class ProductController extends Controller {


    @Inject
    private ProductCategoryService productCategoryService;

    @ActionKey("/getcategory")
    public void category(){
        renderJson(productCategoryService.findRoots());
    }

}
