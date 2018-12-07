package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.service.PluginService;

/**
 * Controller - 促销插件
 * 
 */
@ControllerBind(controllerKey = "/admin/promotion_plugin")
public class PromotionPluginController extends BaseController {

	@Inject
	private PluginService pluginService;

	/**
	 * 列表
	 */
	public void list() {
		setAttr("promotionPlugins", pluginService.getPromotionPlugins());
		render("/admin/promotion_plugin/list.ftl");
	}

}