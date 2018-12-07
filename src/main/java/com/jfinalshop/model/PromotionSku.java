package com.jfinalshop.model;

import com.jfinalshop.model.base.BasePromotionSku;

/**
 * Model - 促销SKU中间表
 * 
 */
public class PromotionSku extends BasePromotionSku<PromotionSku> {
	private static final long serialVersionUID = -8165631766188450011L;
	public static final PromotionSku dao = new PromotionSku().dao();
	
	
}
