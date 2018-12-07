package com.jfinalshop.plugin.discountPromotion;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.controller.admin.BaseController;
import com.jfinalshop.model.PluginConfig;
import com.jfinalshop.model.Promotion;
import com.jfinalshop.plugin.PromotionPlugin;
import com.jfinalshop.service.PluginConfigService;
import com.jfinalshop.service.PromotionService;

/**
 * Controller - 折扣促销
 * 
 */
@ControllerBind(controllerKey = "/admin/promotion_plugin/discount_promotion")
public class DiscountPromotionController extends BaseController {

	@Inject
	private DiscountPromotionPlugin discountPromotionPlugin;
	@Inject
	private PluginConfigService pluginConfigService;
	@Inject
	private PromotionService promotionService;

	/**
	 * 安装
	 */
	public void install() {
		if (!discountPromotionPlugin.getIsInstalled()) {
			PluginConfig pluginConfig = new PluginConfig();
			pluginConfig.setPluginId(discountPromotionPlugin.getId());
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
		if (discountPromotionPlugin.getIsInstalled()) {
			pluginConfigService.deleteByPluginId(discountPromotionPlugin.getId());
			promotionService.shutDownPromotion(Promotion.Type.discount);
		}
		renderJson(SUCCESS_MESSAGE);
	}

	/**
	 * 设置
	 */
	public void setting() {
		PluginConfig pluginConfig = discountPromotionPlugin.getPluginConfig();
		setAttr("pluginConfig", pluginConfig);
		render("/plugin/discountPromotion/setting.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		BigDecimal price = new BigDecimal(getPara("price"));
		Boolean isEnabled = getParaToBoolean("isEnabled", false); 
		Integer order = getParaToInt("order");
		
		if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
			setAttr("errorMessage", "单价为空或小于0");
			renderJson(ERROR_VIEW);
		}
		PluginConfig pluginConfig = discountPromotionPlugin.getPluginConfig();
		Map<String, String> attributes = new HashMap<>();
		pluginConfig.setAttributes(attributes);
		attributes.put(PromotionPlugin.PRICE, price.toString());
		pluginConfig.setIsEnabled(isEnabled);
		pluginConfig.setOrders(order);
		pluginConfigService.update(pluginConfig);
		if (!pluginConfig.getIsEnabled()) {
			promotionService.shutDownPromotion(Promotion.Type.discount);
		}
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/promotion_plugin/list");
	}

}