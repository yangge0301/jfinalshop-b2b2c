package com.jfinalshop.controller.business;

import java.util.List;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.CategoryApplication;
import com.jfinalshop.model.ProductCategory;
import com.jfinalshop.model.Store;
import com.jfinalshop.service.BusinessService;
import com.jfinalshop.service.CategoryApplicationService;
import com.jfinalshop.service.ProductCategoryService;
import com.jfinalshop.service.StoreService;

/**
 * Controller - 经营分类申请
 * 
 */
@ControllerBind(controllerKey = "/business/category_application")
public class CategoryApplicationController extends BaseController {

	@Inject
	private CategoryApplicationService categoryApplicationService;
	@Inject
	private ProductCategoryService productCategoryService;
	@Inject
	private StoreService storeService;
	@Inject
	private BusinessService businessService;

	/**
	 * 添加
	 */
	public void add() {
		Store currentStore = businessService.getCurrentStore();
		
		if (currentStore == null) {
			setAttr("errorMessage", "当前店铺不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		List<ProductCategory> productCategories = storeService.findProductCategoryList(currentStore, CategoryApplication.Status.pending);
		setAttr("productCategoryTree", productCategoryService.findTree());
		setAttr("appliedProductCategories", productCategories);
		render("/business/category_application/add.ftl");
	}

	/**
	 * 保存
	 */
	public void save() {
		Long productCategoryId = getParaToLong("productCategoryId");
		Store currentStore = businessService.getCurrentStore();
		
		ProductCategory productCategory = productCategoryService.find(productCategoryId);
		if (productCategory == null || currentStore == null) {
			setAttr("errorMessage", "产品分类或店铺不否存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (storeService.productCategoryExists(currentStore, productCategory) || categoryApplicationService.exist(currentStore, productCategory, CategoryApplication.Status.pending)) {
			setAttr("errorMessage", "经营分类不否存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		CategoryApplication categoryApplication = new CategoryApplication();
		categoryApplication.setStatus(CategoryApplication.Status.pending.ordinal());
		categoryApplication.setRate(Store.Type.general.equals(currentStore.getTypeName()) ? productCategory.getGeneralRate() : productCategory.getSelfRate());
		categoryApplication.setStoreId(currentStore.getId());
		categoryApplication.setProductCategoryId(productCategory.getId());
		categoryApplicationService.save(categoryApplication);

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
		setAttr("page", categoryApplicationService.findPage(currentStore, pageable));
		render("/business/category_application/list.ftl");
	}

}