package com.jfinalshop.plugin.localStorage;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.controller.admin.BaseController;
import com.jfinalshop.model.PluginConfig;
import com.jfinalshop.service.PluginConfigService;

/**
 * Controller - 本地文件存储
 * 
 */
@ControllerBind(controllerKey = "/admin/storage_plugin/local_storage")
public class LocalStorageController extends BaseController {

	@Inject
	private LocalStoragePlugin localStoragePlugin;
	@Inject
	private PluginConfigService pluginConfigService;

	/**
	 * 设置
	 */
	public void setting() {
		PluginConfig pluginConfig = localStoragePlugin.getPluginConfig();
		setAttr("pluginConfig", pluginConfig);
		render("/plugin/localStorage/setting.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		Integer order = getParaToInt("order");
		
		PluginConfig pluginConfig = localStoragePlugin.getPluginConfig();
		pluginConfig.setIsEnabled(true);
		pluginConfig.setOrders(order);
		pluginConfigService.update(pluginConfig);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/storage_plugin/list");
	}

}