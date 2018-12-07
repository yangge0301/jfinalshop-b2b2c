package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.service.DeliveryTemplateService;

/**
 * Controller - 快递单模板
 * 
 */
@ControllerBind(controllerKey = "/admin/delivery_template")
public class DeliveryTemplateController extends BaseController {

	@Inject
	private DeliveryTemplateService deliveryTemplateService;

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", deliveryTemplateService.findPage(pageable));
		render("/admin/delivery_template/list.ftl");
	}

}