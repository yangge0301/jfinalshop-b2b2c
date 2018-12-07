package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.service.OrderService;

/**
 * Controller - 打印
 * 
 */
@ControllerBind(controllerKey = "/admin/print")
public class PrintController extends BaseController {

	@Inject
	private OrderService orderService;

	/**
	 * 订单打印
	 */
	public void order() {
		Long id = getParaToLong("id");
		setAttr("order", orderService.find(id));
		render("/admin/print/order.ftl");
	}

}