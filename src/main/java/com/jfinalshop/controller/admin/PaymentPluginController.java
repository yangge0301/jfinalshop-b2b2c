package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.service.PluginService;

/**
 * Controller - 支付插件
 * 
 */
@ControllerBind(controllerKey = "/admin/payment_plugin")
public class PaymentPluginController extends BaseController {

	@Inject
	private PluginService pluginService;

	/**
	 * 列表
	 */
	public void list() {
		setAttr("paymentPlugins", pluginService.getPaymentPlugins());
		render("/admin/payment_plugin/list.ftl");
	}

}