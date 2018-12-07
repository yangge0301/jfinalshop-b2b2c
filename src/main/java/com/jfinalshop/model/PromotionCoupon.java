package com.jfinalshop.model;

import com.jfinalshop.model.base.BasePromotionCoupon;

/**
 * Model - 促销优惠券中间表
 * 
 */
public class PromotionCoupon extends BasePromotionCoupon<PromotionCoupon> {
	private static final long serialVersionUID = 6402689975757641260L;
	public static final PromotionCoupon dao = new PromotionCoupon().dao();
}
