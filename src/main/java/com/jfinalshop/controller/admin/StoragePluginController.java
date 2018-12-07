package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.service.PluginService;

/**
 * Controller - 存储插件
 * 
 */
@ControllerBind(controllerKey = "/admin/storage_plugin")
public class StoragePluginController extends BaseController {

	@Inject
	private PluginService pluginService;

	/**
	 * 列表
	 */
	public void list() {
		setAttr("storagePlugins", pluginService.getStoragePlugins());
		render("/admin/storage_plugin/list.ftl");
	}

}