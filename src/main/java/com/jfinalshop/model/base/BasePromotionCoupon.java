package com.jfinalshop.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BasePromotionCoupon<M extends BasePromotionCoupon<M>> extends Model<M> implements IBean {

	public void setPromotionsId(java.lang.Long promotionsId) {
		set("promotions_id", promotionsId);
	}

	public java.lang.Long getPromotionsId() {
		return getLong("promotions_id");
	}

	public void setCouponsId(java.lang.Long couponsId) {
		set("coupons_id", couponsId);
	}

	public java.lang.Long getCouponsId() {
		return getLong("coupons_id");
	}

}