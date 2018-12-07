package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.service.PluginService;

/**
 * Controller - 登录插件
 * 
 */
@ControllerBind(controllerKey = "/admin/login_plugin")
public class LoginPluginController extends BaseController {

	@Inject
	private PluginService pluginService;

	/**
	 * 列表
	 */
	public void list() {
		setAttr("loginPlugins", pluginService.getLoginPlugins());
		render("/admin/login_plugin/list.ftl");
	}

}