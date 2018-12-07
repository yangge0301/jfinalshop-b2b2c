package com.jfinalshop.service;

import net.hasor.core.Singleton;

import com.jfinalshop.model.PaymentMethod;

/**
 * Service - 支付方式
 * 
 */
@Singleton
public class PaymentMethodService extends BaseService<PaymentMethod> {

	/**
	 * 构造方法
	 */
	public PaymentMethodService() {
		super(PaymentMethod.class);
	}
	
}