package com.jfinalshop.controller.shop;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.interceptor.MobileInterceptor;
import com.jfinalshop.service.ProductCategoryService;

/**
 * Controller - 商品分类
 * 
 */
@ControllerBind(controllerKey = "/product_category")
public class ProductCategoryController extends BaseController {

	@Inject
	private ProductCategoryService productCategoryService;

	/**
	 * 首页
	 */
	@Before(MobileInterceptor.class)
	public void index() {
		setAttr("rootProductCategories", productCategoryService.findRoots());
		render("/shop/product_category/index.ftl");
	}

}