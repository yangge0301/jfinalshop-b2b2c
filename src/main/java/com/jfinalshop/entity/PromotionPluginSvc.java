package com.jfinalshop.entity;

import com.jfinalshop.model.Svc;

/**
 * Entity - 促销插件服务
 * 
 */
public class PromotionPluginSvc extends Svc {

	private static final long serialVersionUID = 7240764880070217374L;

	/**
	 * 促销插件Id
	 */
	private String promotionPluginId;

	/**
	 * 获取促销插件Id
	 * 
	 * @return 促销插件Id
	 */
	public String getPromotionPluginId() {
		return promotionPluginId;
	}

	/**
	 * 设置促销插件Id
	 * 
	 * @param promotionPluginId
	 *            促销插件Id
	 */
	public void setPromotionPluginId(String promotionPluginId) {
		this.promotionPluginId = promotionPluginId;
	}

}