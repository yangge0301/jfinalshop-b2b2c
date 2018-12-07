package com.jfinalshop.controller.business;

import java.util.List;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.Results;
import com.jfinalshop.model.ProductNotify;
import com.jfinalshop.model.Store;
import com.jfinalshop.service.BusinessService;
import com.jfinalshop.service.ProductNotifyService;

/**
 * Controller - 到货通知
 * 
 */
@ControllerBind(controllerKey = "/business/product_notify")
public class ProductNotifyController extends BaseController {

	@Inject
	private ProductNotifyService productNotifyService;
	@Inject
	private BusinessService businessService;

	/**
	 * 发送到货通知
	 */
	public void send() {
		Long[] ids = getParaValuesToLong("ids");
		Store currentStore = businessService.getCurrentStore();
		
		if (ids == null) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		List<ProductNotify> productNotifies = productNotifyService.findList(ids);
		for (ProductNotify productNotify : productNotifies) {
			if (productNotify == null) {
				Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
				return;
			}
			if (!currentStore.equals(productNotify.getStore())) {
				Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
				return;
			}
		}
		int count = productNotifyService.send(productNotifies);
		Results.ok(getResponse(), "business.productNotify.sentSuccess", count);
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		Boolean isMarketable = getParaToBoolean("isMarketable");
		Boolean isOutOfStock = getParaToBoolean("isOutOfStock");
		Boolean hasSent = getParaToBoolean("hasSent"); 
		
		Store currentStore = businessService.getCurrentStore();
		
		setAttr("isMarketable", isMarketable);
		setAttr("isOutOfStock", isOutOfStock);
		setAttr("hasSent", hasSent);
		setAttr("pageable", pageable);
		setAttr("page", productNotifyService.findPage(currentStore, null, isMarketable, isOutOfStock, hasSent, pageable));
		render("/business/product_notify/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		Store currentStore = businessService.getCurrentStore();
		
		if (ids == null) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		for (Long id : ids) {
			ProductNotify productNotify = productNotifyService.find(id);
			if (productNotify == null) {
				Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
				return;
			}
			if (!currentStore.equals(productNotify.getStore())) {
				Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
				return;
			}
			productNotifyService.delete(id);
		}
		renderJson(Results.OK);
	}

}