package com.jfinalshop.controller.admin;

import java.util.ArrayList;
import java.util.List;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Message;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.ProductCategory;
import com.jfinalshop.service.BrandService;
import com.jfinalshop.service.ProductCategoryService;
import com.jfinalshop.service.PromotionService;

/**
 * Controller - 商品分类
 * 
 */
@ControllerBind(controllerKey = "/admin/product_category")
public class ProductCategoryController extends BaseController {

	@Inject
	private ProductCategoryService productCategoryService;
	@Inject
	private BrandService brandService;
	@Inject
	private PromotionService promotionService;

	/**
	 * 添加
	 */
	public void add() {
		setAttr("productCategoryTree", productCategoryService.findTree());
		setAttr("brands", brandService.findAll());
		setAttr("promotions", promotionService.findAll());
		render("/admin/product_category/add.ftl");
	}

	/**
	 * 保存
	 */
	public void save() {
		ProductCategory productCategory = getModel(ProductCategory.class);
		Long parentId = getParaToLong("parentId");
		Long[] brandIds = getParaValuesToLong("brandIds");
		Long[] promotionIds = getParaValuesToLong("promotionIds");
		
		productCategory.setIsMarketable(getParaToBoolean("isMarketable", false));
		productCategory.setIsTop(getParaToBoolean("isTop", false));
		productCategory.setIsCash(getParaToBoolean("isCash", false));
		
		ProductCategory pProductCategory = productCategoryService.find(parentId);
		productCategory.setParentId(pProductCategory == null ? null : pProductCategory.getId());
		productCategory.setBrands(new ArrayList<>(brandService.findList(brandIds)));
		productCategory.setPromotions(new ArrayList<>(promotionService.findList(promotionIds)));
		
		productCategoryService.save(productCategory);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		ProductCategory productCategory = productCategoryService.find(id);
		setAttr("productCategoryTree", productCategoryService.findTree());
		setAttr("brands", brandService.findAll());
		setAttr("promotions", promotionService.findAll());
		setAttr("productCategory", productCategory);
		setAttr("children", productCategoryService.findChildren(productCategory, true, null));
		render("/admin/product_category/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		ProductCategory productCategory = getModel(ProductCategory.class);
		Long parentId = getParaToLong("parentId");
		Long[] brandIds = getParaValuesToLong("brandIds");
		Long[] promotionIds = getParaValuesToLong("promotionIds");
		
		productCategory.setIsMarketable(getParaToBoolean("isMarketable", false));
		productCategory.setIsTop(getParaToBoolean("isTop", false));
		productCategory.setIsCash(getParaToBoolean("isCash", false));
		
		productCategory.setParent(productCategoryService.find(parentId));
		productCategory.setBrands(new ArrayList<>(brandService.findList(brandIds)));
		productCategory.setPromotions(new ArrayList<>(promotionService.findList(promotionIds)));
		
		if (productCategory.getParent() != null) {
			ProductCategory parent = productCategory.getParent();
			if (parent.getId().equals(productCategory.getId())) {
				setAttr("errorMessage", "分类与上级分类相同!");
				render(ERROR_VIEW);
				return;
			}
			List<ProductCategory> children = productCategoryService.findChildren(parent, true, null);
			if (children != null && children.contains(parent)) {
				setAttr("errorMessage", "上级分类存在下级分类中!");
				render(ERROR_VIEW);
				return;
			}
		}
		productCategoryService.update(productCategory);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 列表
	 */
	public void list() {
		setAttr("productCategoryTree", productCategoryService.findTree());
		render("/admin/product_category/list.ftl"); ;
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long id = getParaToLong("id");
		ProductCategory productCategory = productCategoryService.find(id);
		if (productCategory == null) {
			renderJson(ERROR_MESSAGE);
			return;
		}
		List<ProductCategory> children = productCategory.getChildren();
		if (children != null && !children.isEmpty()) {
			renderJson(Message.error("admin.productCategory.deleteExistChildrenNotAllowed"));
			return;
		}
		List<Product> products = productCategory.getProducts();
		if (products != null && !products.isEmpty()) {
			renderJson(Message.error("admin.productCategory.deleteExistProductNotAllowed"));
			return;
		}
		productCategoryService.delete(id);
		renderJson(SUCCESS_MESSAGE);
	}

}