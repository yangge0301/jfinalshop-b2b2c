package com.jfinalshop.model;

import com.jfinalshop.model.base.BaseShippingMethodPaymentMethod;

/**
 * Model - 配送方式支付方式中间表
 * 
 */
public class ShippingMethodPaymentMethod extends BaseShippingMethodPaymentMethod<ShippingMethodPaymentMethod> {
	private static final long serialVersionUID = -3441708244730074572L;
	public static final ShippingMethodPaymentMethod dao = new ShippingMethodPaymentMethod().dao();
}
