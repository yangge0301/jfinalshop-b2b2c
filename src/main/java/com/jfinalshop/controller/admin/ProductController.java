package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Message;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Brand;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.ProductCategory;
import com.jfinalshop.model.ProductTag;
import com.jfinalshop.service.BrandService;
import com.jfinalshop.service.ProductCategoryService;
import com.jfinalshop.service.ProductService;
import com.jfinalshop.service.ProductTagService;

/**
 * Controller - 商品
 * 
 */
@ControllerBind(controllerKey = "/admin/product")
public class ProductController extends BaseController {

	@Inject
	private ProductService productService;
	@Inject
	private ProductCategoryService productCategoryService;
	@Inject
	private BrandService brandService;
	@Inject
	private ProductTagService productTagService;

	/**
	 * 列表
	 */
	public void list() {
		String typeName = getPara("type");
		Product.Type type = StrKit.notBlank(typeName) ? Product.Type.valueOf(typeName) : null;
		Long productCategoryId = getParaToLong("productCategoryId");
		Long brandId = getParaToLong("brandId");
		Long productTagId = getParaToLong("productTagId");
		Boolean isActive = getParaToBoolean("isActive");
		Boolean isMarketable = getParaToBoolean("isMarketable");
		Boolean isList = getParaToBoolean("isList");
		Boolean isTop = getParaToBoolean("isTop");
		Boolean isOutOfStock = getParaToBoolean("isOutOfStock");
		Boolean isStockAlert = getParaToBoolean("isStockAlert");
		Pageable pageable = getBean(Pageable.class);
	
		ProductCategory productCategory = productCategoryService.find(productCategoryId);
		Brand brand = brandService.find(brandId);
		ProductTag productTag = productTagService.find(productTagId);

		setAttr("types", Product.Type.values());
		setAttr("productCategoryTree", productCategoryService.findTree());
		setAttr("brands", brandService.findAll());
		setAttr("productTags", productTagService.findAll());
		setAttr("type", type);
		setAttr("productCategoryId", productCategoryId);
		setAttr("brandId", brandId);
		setAttr("productTagId", productTagId);
		setAttr("isMarketable", isMarketable);
		setAttr("isList", isList);
		setAttr("isTop", isTop);
		setAttr("isActive", isActive);
		setAttr("isOutOfStock", isOutOfStock);
		setAttr("isStockAlert", isStockAlert);
		setAttr("pageable", pageable);
		setAttr("page", productService.findPage(type, null, productCategory, null, brand, null, productTag, null, null, null, null, isMarketable, isList, isTop, isActive, isOutOfStock, isStockAlert, null, null, pageable));
		render("/admin/product/list.ftl");
	}

	/**
	 * 删除
	 */
	@Before(Tx.class)
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		productService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}

	/**
	 * 上架商品
	 */
	@Before(Tx.class)
	public void shelves() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids == null) {
			renderJson(ERROR_MESSAGE);
			return;
		}
		for (Long id : ids) {
			if (!productService.exists(id)) {
				renderJson(ERROR_MESSAGE);
				return;
			}
			Product product = productService.find(id);
			if (product.getStore().hasExpired() || !product.getStore().getIsEnabled()) {
				renderJson(Message.error("admin.product.isShelvesSku", ERROR_MESSAGE));
				return;
			}
			if (!product.getIsMarketable()) {
				product.setIsMarketable(true);
				productService.update(product);
			}
		}
		renderJson(SUCCESS_MESSAGE);
	}

	/**
	 * 下架商品
	 */
	@Before(Tx.class)
	public void shelf() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids == null) {
			renderJson(ERROR_MESSAGE);
			return;
		}
		for (Long id : ids) {
			if (!productService.exists(id)) {
				renderJson(ERROR_MESSAGE);
				return;
			}
			Product product = productService.find(id);
			if (product.getIsMarketable()) {
				product.setIsMarketable(false);
				productService.update(product);
			}
		}
		renderJson(SUCCESS_MESSAGE);
	}
}