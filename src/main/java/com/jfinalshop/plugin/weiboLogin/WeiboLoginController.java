package com.jfinalshop.plugin.weiboLogin;

import java.util.HashMap;
import java.util.Map;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.controller.admin.BaseController;
import com.jfinalshop.model.PluginConfig;
import com.jfinalshop.plugin.LoginPlugin;
import com.jfinalshop.plugin.PaymentPlugin;
import com.jfinalshop.service.PluginConfigService;

/**
 * Controller - 新浪微博登录
 * 
 */
@ControllerBind(controllerKey = "/admin/login_plugin/weibo_login")
public class WeiboLoginController extends BaseController {

	@Inject
	private WeiboLoginPlugin weiboLoginPlugin;
	@Inject
	private PluginConfigService pluginConfigService;

	/**
	 * 安装
	 */
	public void install() {
		if (!weiboLoginPlugin.getIsInstalled()) {
			PluginConfig pluginConfig = new PluginConfig();
			pluginConfig.setPluginId(weiboLoginPlugin.getId());
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
		if (weiboLoginPlugin.getIsInstalled()) {
			pluginConfigService.deleteByPluginId(weiboLoginPlugin.getId());
		}
		renderJson(SUCCESS_MESSAGE);
	}

	/**
	 * 设置
	 */
	public void setting() {
		PluginConfig pluginConfig = weiboLoginPlugin.getPluginConfig();
		setAttr("pluginConfig", pluginConfig);
		render("/plugin/weiboLogin/setting.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		String loginMethodName = getPara("loginMethodName");
		String oauthKey = getPara("oauthKey");
		String oauthSecret = getPara("oauthSecret");
		String logo = getPara("logo");
		String description = getPara("description");
		Boolean isEnabled = getParaToBoolean("isEnabled", false);
		Integer order = getParaToInt("order");
		
		PluginConfig pluginConfig = weiboLoginPlugin.getPluginConfig();
		Map<String, String> attributes = new HashMap<>();
		attributes.put(LoginPlugin.LOGIN_METHOD_NAME_ATTRIBUTE_NAME, loginMethodName);
		attributes.put("oauthKey", oauthKey);
		attributes.put("oauthSecret", oauthSecret);
		attributes.put(PaymentPlugin.LOGO_ATTRIBUTE_NAME, logo);
		attributes.put(PaymentPlugin.DESCRIPTION_ATTRIBUTE_NAME, description);
		pluginConfig.setAttributes(attributes);
		pluginConfig.setIsEnabled(isEnabled);
		pluginConfig.setOrders(order);
		pluginConfigService.update(pluginConfig);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/login_plugin/list");
	}

}