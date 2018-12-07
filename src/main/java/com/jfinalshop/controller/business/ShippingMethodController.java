package com.jfinalshop.controller.business;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.exception.UnauthorizedException;
import com.jfinalshop.model.DefaultFreightConfig;
import com.jfinalshop.model.ShippingMethod;
import com.jfinalshop.model.Store;
import com.jfinalshop.service.BusinessService;
import com.jfinalshop.service.DefaultFreightConfigService;
import com.jfinalshop.service.ShippingMethodService;

/**
 * Controller - 配送方式
 * 
 */
@ControllerBind(controllerKey = "/business/shipping_method")
public class ShippingMethodController extends BaseController {

	@Inject
	private ShippingMethodService shippingMethodService;
	@Inject
	private DefaultFreightConfigService defaultFreightConfigService;
	@Inject
	private BusinessService businessService;

	/**
	 * 添加属性
	 */
	public void populateModel() {
		Long shippingMethodId = getParaToLong("shippingMethodId");
		Long defaultFreightConfigId = getParaToLong("defaultFreightConfigId");
		Store currentStore = businessService.getCurrentStore();
		
		setAttr("shippingMethod", shippingMethodService.find(shippingMethodId));

		DefaultFreightConfig defaultFreightConfig = defaultFreightConfigService.find(defaultFreightConfigId);
		if (defaultFreightConfig != null && !currentStore.equals(defaultFreightConfig.getStore())) {
			throw new UnauthorizedException();
		}
		setAttr("defaultFreightConfig", defaultFreightConfig);
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		Store currentStore = businessService.getCurrentStore();
		
		setAttr("currentStore", currentStore);
		setAttr("pageable", pageable);
		setAttr("page", shippingMethodService.findPage(pageable));
		render("/business/shipping_method/list.ftl");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long shippingMethodId = getParaToLong("shippingMethodId");
		ShippingMethod shippingMethod = shippingMethodService.find(shippingMethodId);
		Store currentStore = businessService.getCurrentStore();
		
		if (shippingMethod == null) {
			setAttr("errorMessage", " 配送方式不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		DefaultFreightConfig defaultFreightConfig = defaultFreightConfigService.find(shippingMethod, currentStore);
		if (null != defaultFreightConfig) {
			setAttr("defaultFreightConfig", defaultFreightConfig);
		} else {
			setAttr("defaultFreightConfig", null);
		}
		setAttr("shippingMethod", shippingMethod);
		render("/business/shipping_method/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		DefaultFreightConfig defaultFreightConfig = getModel(DefaultFreightConfig.class);
		Long shippingMethodId = getParaToLong("shippingMethodId");
		ShippingMethod shippingMethod = shippingMethodService.find(shippingMethodId);
		Store currentStore = businessService.getCurrentStore();
		
		if (shippingMethod == null) {
			setAttr("errorMessage", " 配送方式不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		defaultFreightConfigService.update(defaultFreightConfig, currentStore, shippingMethod);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

}