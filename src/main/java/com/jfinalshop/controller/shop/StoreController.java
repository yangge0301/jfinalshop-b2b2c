package com.jfinalshop.controller.shop;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.exception.ResourceNotFoundException;
import com.jfinalshop.interceptor.MobileInterceptor;
import com.jfinalshop.model.Store;
import com.jfinalshop.service.StoreProductCategoryService;
import com.jfinalshop.service.StoreProductTagService;
import com.jfinalshop.service.StoreService;

/**
 * Controller - 店铺
 * 
 */
@ControllerBind(controllerKey = "/store")
public class StoreController extends BaseController {

	@Inject
	private StoreService storeService;
	@Inject
	private StoreProductCategoryService storeProductCategoryService;
	@Inject
	private StoreProductTagService storeProductTagService;

	/**
	 * 首页
	 */
	@Before(MobileInterceptor.class)
	public void index() {
		Long storeId = getParaToLong(0);
		Store store = storeService.find(storeId);
		if (store == null) {
			throw new ResourceNotFoundException();
		}
		setAttr("store", store);
		setAttr("storeProductCategoryTree", storeProductCategoryService.findTree(store));
		setAttr("storeProductTags", storeProductTagService.findList(store, true));
		render("/shop/store/index.ftl");
	}

}