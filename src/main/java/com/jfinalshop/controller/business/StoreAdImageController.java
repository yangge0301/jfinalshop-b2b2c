package com.jfinalshop.controller.business;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.Results;
import com.jfinalshop.exception.UnauthorizedException;
import com.jfinalshop.model.Store;
import com.jfinalshop.model.StoreAdImage;
import com.jfinalshop.service.BusinessService;
import com.jfinalshop.service.StoreAdImageService;

/**
 * Controller - 店铺广告图片
 * 
 */
@ControllerBind(controllerKey = "/business/store_ad_image")
public class StoreAdImageController extends BaseController {

	@Inject
	private StoreAdImageService storeAdImageService;
	@Inject
	private BusinessService businessService;

	/**
	 * 添加属性
	 */
	public void populateModel() {
		Long storeAdImageId = getParaToLong("storeAdImageId");
		Store currentStore = businessService.getCurrentStore();
		
		StoreAdImage storeAdImage = storeAdImageService.find(storeAdImageId);
		if (storeAdImage != null && !currentStore.equals(storeAdImage.getStore())) {
			throw new UnauthorizedException();
		}
		setAttr("storeAdImage", storeAdImage);
	}

	/**
	 * 添加
	 */
	public void add() {
		Store currentStore = businessService.getCurrentStore();
		
		if (StoreAdImage.MAX_COUNT != null && currentStore.getStoreAdImages().size() >= StoreAdImage.MAX_COUNT) {
			addFlashMessage("business.storeAdImage.addCountNotAllowed", StoreAdImage.MAX_COUNT);
			redirect("list");
		}
		render("/business/store_ad_image/add.ftl");
	}

	/**
	 * 保存
	 */
	public void save() {
		StoreAdImage storeAdImage = getModel(StoreAdImage.class);
		Store currentStore = businessService.getCurrentStore();
		
		if (storeAdImage == null) {
			setAttr("errorMessage", "店铺广告图片不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (StoreAdImage.MAX_COUNT != null && currentStore.getStoreAdImages().size() >= StoreAdImage.MAX_COUNT) {
			setAttr("errorMessage", "数量大于最大店铺广告图片数!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		storeAdImage.setStoreId(currentStore.getId());
		storeAdImageService.save(storeAdImage);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long storeAdImageId = getParaToLong("storeAdImageId");
		StoreAdImage storeAdImage = storeAdImageService.find(storeAdImageId);
		
		if (storeAdImage == null) {
			setAttr("errorMessage", " 店铺广告图片不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		setAttr("storeAdImage", storeAdImage);
		render("/business/store_ad_image/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		StoreAdImage storeAdImage = getModel(StoreAdImage.class);
		//Store currentStore = businessService.getCurrentStore();
		
		if (storeAdImage == null) {
			setAttr("errorMessage", " 店铺广告图片不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		storeAdImageService.update(storeAdImage);
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
		setAttr("page", storeAdImageService.findPage(currentStore, pageable));
		render("/business/store_ad_image/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		Store currentStore = businessService.getCurrentStore();
		
		for (StoreAdImage storeAdImage : storeAdImageService.findList(ids)) {
			if (!currentStore.equals(storeAdImage.getStore())) {
				Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
				return;
			}
		}
		storeAdImageService.delete(ids);
		renderJson(Results.OK);
	}
	
}