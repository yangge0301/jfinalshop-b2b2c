package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.service.DeliveryCenterService;

/**
 * Controller - 发货点
 * 
 */
@ControllerBind(controllerKey = "/admin/delivery_center")
public class DeliveryCenterController extends BaseController {

	@Inject
	private DeliveryCenterService deliveryCenterService;

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", deliveryCenterService.findPage(pageable));
		render("/admin/delivery_center/list.ftl");
	}
	
	
}