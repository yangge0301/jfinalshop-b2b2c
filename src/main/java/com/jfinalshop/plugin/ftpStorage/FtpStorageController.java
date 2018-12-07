package com.jfinalshop.plugin.ftpStorage;

import java.util.HashMap;
import java.util.Map;

import net.hasor.core.Inject;

import org.apache.commons.lang.StringUtils;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.controller.admin.BaseController;
import com.jfinalshop.model.PluginConfig;
import com.jfinalshop.service.PluginConfigService;

/**
 * Controller - FTP存储
 * 
 */
@ControllerBind(controllerKey = "/admin/storage_plugin/ftp_storage")
public class FtpStorageController extends BaseController {

	@Inject
	private FtpStoragePlugin ftpStoragePlugin;
	@Inject
	private PluginConfigService pluginConfigService;

	/**
	 * 安装
	 */
	public void install() {
		if (!ftpStoragePlugin.getIsInstalled()) {
			PluginConfig pluginConfig = new PluginConfig();
			pluginConfig.setPluginId(ftpStoragePlugin.getId());
			pluginConfig.setIsEnabled(false);
			//pluginConfig.setAttributes(null);
			pluginConfigService.save(pluginConfig);
		}
		renderJson(SUCCESS_MESSAGE);
	}

	/**
	 * 卸载
	 */
	public void uninstall() {
		if (ftpStoragePlugin.getIsInstalled()) {
			pluginConfigService.deleteByPluginId(ftpStoragePlugin.getId());
		}
		renderJson(SUCCESS_MESSAGE);
	}

	/**
	 * 设置
	 */
	public void setting() {
		PluginConfig pluginConfig = ftpStoragePlugin.getPluginConfig();
		setAttr("pluginConfig", pluginConfig);
		render("/plugin/ftpStorage/setting.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		String host = getPara("host");
		Integer port = getParaToInt("port");
		String username = getPara("username");
		String password = getPara("password");
		String urlPrefix = getPara("urlPrefix");
		Boolean isEnabled = getParaToBoolean("isEnabled", false); 
		Integer order = getParaToInt("order");
		
		PluginConfig pluginConfig = ftpStoragePlugin.getPluginConfig();
		Map<String, String> attributes = new HashMap<>();
		attributes.put("host", host);
		attributes.put("port", String.valueOf(port));
		attributes.put("username", username);
		attributes.put("password", password);
		attributes.put("urlPrefix", StringUtils.removeEnd(urlPrefix, "/"));
		pluginConfig.setAttributes(attributes);
		pluginConfig.setIsEnabled(isEnabled);
		pluginConfig.setOrders(order);
		pluginConfigService.update(pluginConfig);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/storage_plugin/list");
	}

}