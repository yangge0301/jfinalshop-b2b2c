package com.jfinalshop.controller.business;

import java.util.List;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Pageable;
import com.jfinalshop.Results;
import com.jfinalshop.exception.UnauthorizedException;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.Store;
import com.jfinalshop.model.StoreProductCategory;
import com.jfinalshop.service.BusinessService;
import com.jfinalshop.service.StoreProductCategoryService;

/**
 * Controller - 店铺商品分类
 * 
 */
@ControllerBind(controllerKey = "/business/store_product_category")
public class StoreProductCategoryController extends BaseController {

	@Inject
	private StoreProductCategoryService storeProductCategoryService;
	@Inject
	private BusinessService businessService;

	/**
	 * 添加属性
	 */
	public void populateModel() {
		Long storeProductCategoryId = getParaToLong("storeProductCategoryId"); 
		Store currentStore = businessService.getCurrentStore();
		
		StoreProductCategory storeProductCategory = storeProductCategoryService.find(storeProductCategoryId);
		if (storeProductCategory != null && !currentStore.equals(storeProductCategory.getStore())) {
			throw new UnauthorizedException();
		}
		setAttr("storeProductCategory", storeProductCategory);
	}

	/**
	 * 添加
	 */
	public void add() {
		Store currentStore = businessService.getCurrentStore();
		setAttr("storeProductCategoryTree", storeProductCategoryService.findTree(currentStore));
		render("/business/store_product_category/add.ftl");
	}

	/**
	 * 保存
	 */
	@Before(Tx.class)
	public void save() {
		StoreProductCategory storeProductCategory = getModel(StoreProductCategory.class);
		Long parentId = getParaToLong("parentId");
		Store currentStore = businessService.getCurrentStore();
		
		StoreProductCategory pStoreProductCategory = storeProductCategoryService.find(parentId);
		if (parentId == null && pStoreProductCategory != null && pStoreProductCategory.getParent() != null) {
			if (!currentStore.equals(pStoreProductCategory.getStore())) {
				setAttr("errorMessage", "当前店铺与所属分类店铺不同!");
				render(UNPROCESSABLE_ENTITY_VIEW);
				return;
			}
		}
		if (pStoreProductCategory != null) {
			storeProductCategory.setParentId(pStoreProductCategory.getId());
		}
		storeProductCategory.setStoreId(currentStore.getId());
		storeProductCategoryService.save(storeProductCategory);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long storeProductCategoryId = getParaToLong("storeProductCategoryId");
		StoreProductCategory storeProductCategory = storeProductCategoryService.find(storeProductCategoryId);
		Store currentStore = businessService.getCurrentStore();
		
		if (storeProductCategory == null) {
			setAttr("errorMessage", "店铺分类不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		setAttr("storeProductCategoryTree", storeProductCategoryService.findTree(currentStore));
		setAttr("storeProductCategory", storeProductCategory);
		setAttr("children", storeProductCategoryService.findChildren(storeProductCategory, currentStore, true, null));
		render("/business/store_product_category/edit.ftl");
	}

	/**
	 * 更新
	 */
	@Before(Tx.class)
	public void update() {
		StoreProductCategory storeProductCategory = getModel(StoreProductCategory.class);
		Long parentId = getParaToLong("parentId");
		Store currentStore = businessService.getCurrentStore();
		
		StoreProductCategory pParen = storeProductCategoryService.find(parentId);
		if (storeProductCategory == null) {
			setAttr("errorMessage", "店铺产品分类不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		if (storeProductCategory.getParent() != null) {
			StoreProductCategory parent = storeProductCategory.getParent();
			if (parent.equals(storeProductCategory)) {
				setAttr("errorMessage", "店铺产品分类与上级相同!");
				render(UNPROCESSABLE_ENTITY_VIEW);
				return;
			}
			List<StoreProductCategory> children = storeProductCategoryService.findChildren(storeProductCategory, currentStore, true, null);
			if (children != null && children.contains(parent)) {
				setAttr("errorMessage", "店铺产品分类存在下级中!");
				render(UNPROCESSABLE_ENTITY_VIEW);
				return;
			}
			if (!currentStore.equals(pParen.getStore())) {
				setAttr("errorMessage", "当前店铺与上级分类相同!");
				render(UNPROCESSABLE_ENTITY_VIEW);
				return;
			}
		}
		if (pParen != null) {
			storeProductCategory.setParentId(pParen.getId());
		}
		storeProductCategoryService.update(storeProductCategory);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		Store currentStore = businessService.getCurrentStore();
		
		setAttr("pageable", pageable);
		setAttr("storeProductCategoryTree", storeProductCategoryService.findTree(currentStore));
		render("/business/store_product_category/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long storeProductCategoryId = getParaToLong("storeProductCategoryId");
		StoreProductCategory storeProductCategory = storeProductCategoryService.find(storeProductCategoryId);
		
		if (storeProductCategory == null) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}

		List<StoreProductCategory> children = storeProductCategory.getChildren();
		if (children != null && !children.isEmpty()) {
			Results.unprocessableEntity(getResponse(), "business.storeProductCategory.deleteExistChildrenNotAllowed");
			return;
		}
		List<Product> product = storeProductCategory.getProducts();
		if (product != null && !product.isEmpty()) {
			Results.unprocessableEntity(getResponse(), "business.storeProductCategory.deleteExistProductNotAllowed");
			return;
		}
		storeProductCategoryService.delete(storeProductCategory);
		renderJson(Results.OK);
	}

}