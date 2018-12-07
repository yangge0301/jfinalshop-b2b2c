package com.jfinalshop.api.controller.shop;

import java.util.List;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.api.common.bean.DataResponse;
import com.jfinalshop.api.common.bean.DatumResponse;
import com.jfinalshop.api.controller.BaseAPIController;
import com.jfinalshop.api.interceptor.AccessInterceptor;
import com.jfinalshop.model.Brand;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.ProductCategory;
import com.jfinalshop.model.ProductTag;
import com.jfinalshop.model.Promotion;
import com.jfinalshop.service.BrandService;
import com.jfinalshop.service.ProductCategoryService;
import com.jfinalshop.service.ProductService;
import com.jfinalshop.service.ProductTagService;
import com.jfinalshop.service.PromotionService;

/**
 * 移动API - 分类
 *
 */
@ControllerBind(controllerKey = "/api/productCategory")
@Before(AccessInterceptor.class)
public class ProductCategoryAPIController extends BaseAPIController {

	@Inject
	private ProductCategoryService productCategoryService;
	@Inject
	private ProductService productService;
	@Inject
	private BrandService brandService;
	@Inject
	private PromotionService promotionService;
	@Inject
	private ProductTagService productTagService;
	
	/**
	 * 查询左侧分类
	 * 
	 */
	public void findRoots() {
		List<ProductCategory> productCategorys = productCategoryService.findRoots(true, null);
		renderJson(new DataResponse(productCategorys));
	}
	
	/**
	 * 查找商品分类的下级
	 * 
	 */
	public void findChildren() {
		Long productCategoryId = getParaToLong("productCategoryId");
		ProductCategory productCategory = productCategoryService.find(productCategoryId);
		if (productCategory == null) {
			renderArgumentError("商品分类不能为空!");
			return;
		}
		List<ProductCategory> childrenCategorys = productCategoryService.findChildren(productCategory);
		renderJson(new DataResponse(childrenCategorys));
	}
	
	/**
	 * 按分类查找商品分页
	 * 
	 */
	public void findProducts() {
		Long productCategoryId = getParaToLong("productCategoryId");
		Integer pageNumber = getParaToInt("pageNumber", 1);
		Integer pageSize = getParaToInt("pageSize", 20);
		
		ProductCategory productCategory = productCategoryService.find(productCategoryId);
		if (productCategory == null) {
			renderArgumentError("商品分类不能为空!");
			return;
		}
		// 类型
		String typeName = getPara("type");
		Product.Type type = StrKit.notBlank(typeName) ? Product.Type.valueOf(typeName) : null;
		// 排序类型
		String orderTypeName = getPara("orderType");
		Product.OrderType orderType = StrKit.notBlank(orderTypeName) ? Product.OrderType.valueOf(orderTypeName) : null;
		// 品牌
		Long brandId = getParaToLong("brandId");
		Brand brand = brandService.find(brandId);
		// 促销
		Long promotionId = getParaToLong("promotionId");
		Promotion promotion = promotionService.find(promotionId);
		// 标签
		Long productTagId = getParaToLong("productTagId");
		ProductTag productTag = productTagService.find(productTagId);
		Pageable pageable = new Pageable(pageNumber, pageSize);
		
		Page<Product> products = productService.findPage(type, null, productCategory, null, brand, promotion, productTag, null, null, null, null, true, true, null, true, false, null, null, orderType, pageable);
		convertProduct(products.getList());
		renderJson(new DatumResponse(products));
	}
	
	
}
