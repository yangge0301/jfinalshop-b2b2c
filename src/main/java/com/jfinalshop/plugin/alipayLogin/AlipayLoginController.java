package com.jfinalshop.plugin.alipayLogin;

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
 * Controller - 支付宝快捷登录
 * 
 */
@ControllerBind(controllerKey = "/admin/login_plugin/alipay_login")
public class AlipayLoginController extends BaseController {

	@Inject
	private AlipayLoginPlugin alipayLoginPlugin;
	@Inject
	private PluginConfigService pluginConfigService;
	
	/**
	 * 安装
	 */
	public void install() {
		if (!alipayLoginPlugin.getIsInstalled()) {
			PluginConfig pluginConfig = new PluginConfig();
			pluginConfig.setPluginId(alipayLoginPlugin.getId());
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
		if (alipayLoginPlugin.getIsInstalled()) {
			pluginConfigService.deleteByPluginId(alipayLoginPlugin.getId());
		}
		renderJson(SUCCESS_MESSAGE);
	}

	/**
	 * 设置
	 */
	public void setting() {
		PluginConfig pluginConfig = alipayLoginPlugin.getPluginConfig();
		setAttr("pluginConfig", pluginConfig);
		render("/plugin/alipayLogin/setting.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		String loginMethodName = getPara("loginMethodName");
		String partner = getPara("partner");
		String key = getPara("key");
		String logo = getPara("logo");
		String description = getPara("description");
		Boolean isEnabled = getParaToBoolean("isEnabled", false);
		Integer order = getParaToInt("order");
		
		
		PluginConfig pluginConfig = alipayLoginPlugin.getPluginConfig();
		Map<String, String> attributes = new HashMap<>();
		attributes.put(LoginPlugin.LOGIN_METHOD_NAME_ATTRIBUTE_NAME, loginMethodName);
		attributes.put("partner", partner);
		attributes.put("key", key);
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