package com.jfinalshop.controller.business;

import net.hasor.core.Inject;

import org.apache.shiro.authz.UnauthorizedException;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.Results;
import com.jfinalshop.model.Store;
import com.jfinalshop.model.StoreProductTag;
import com.jfinalshop.service.BusinessService;
import com.jfinalshop.service.StoreProductTagService;

/**
 * Controller - 店铺商品标签
 * 
 */
@ControllerBind(controllerKey = "/business/store_product_tag")
public class StoreProductTagController extends BaseController {

	@Inject
	private StoreProductTagService storeProductTagService;
	@Inject
	private BusinessService businessService;

	/**
	 * 添加属性
	 */
	public void populateModel() {
		Long storeProductTagId = getParaToLong("storeProductTagId");
		Store currentStore = businessService.getCurrentStore();

		StoreProductTag storeProductTag = storeProductTagService.find(storeProductTagId);
		if (storeProductTag != null && !currentStore.equals(storeProductTag.getStore())) {
			throw new UnauthorizedException();
		}
		setAttr("storeProductTag", storeProductTag);
	}

	/**
	 * 添加
	 */
	public void add() {
		render("/business/store_product_tag/add.ftl");
	}

	/**
	 * 保存
	 */
	public void save() {
		StoreProductTag storeProductTag = getModel(StoreProductTag.class);
		Store currentStore = businessService.getCurrentStore();
		
		storeProductTag.setStoreId(currentStore.getId());
		storeProductTagService.save(storeProductTag);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long storeProductTagId = getParaToLong("storeProductTagId");
		
		StoreProductTag storeProductTag = storeProductTagService.find(storeProductTagId);
		if (storeProductTag == null) {
			setAttr("errorMessage", "店铺商品标签不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		setAttr("storeProductTag", storeProductTag);
		render("/business/store_product_tag/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		StoreProductTag storeProductTag = getModel(StoreProductTag.class);
		//Store currentStore = businessService.getCurrentStore();
		
		if (storeProductTag == null) {
			setAttr("errorMessage", "店铺商品标签不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		storeProductTagService.update(storeProductTag);
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
		setAttr("page", storeProductTagService.findPage(currentStore, pageable));
		render("/business/store_product_tag/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		Store currentStore = businessService.getCurrentStore();
		
		for (Long id : ids) {
			StoreProductTag storeProductTag = storeProductTagService.find(id);
			if (storeProductTag == null || !currentStore.equals(storeProductTag.getStore())) {
				Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
				return;
			}
			storeProductTagService.delete(id);
		}
		renderJson(Results.OK);
	}

}