package com.jfinalshop.model;

import com.jfinalshop.model.base.BaseProductPromotion;

/**
 * Model - 商品促销中间表
 * 
 */
public class ProductPromotion extends BaseProductPromotion<ProductPromotion> {
	private static final long serialVersionUID = 590821453569941270L;
	public static final ProductPromotion dao = new ProductPromotion().dao();
	
	
}
