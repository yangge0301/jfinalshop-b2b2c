package com.jfinalshop.model;

import com.jfinalshop.model.base.BaseSn;

/**
 * Model - 序列号
 * 
 */
public class Sn extends BaseSn<Sn> {
	private static final long serialVersionUID = 7977837709024723646L;
	public static final Sn dao = new Sn().dao();
	
	/**
	 * 类型
	 */
	public enum Type {

		/**
		 * 商品
		 */
		product,

		/**
		 * 订单
		 */
		order,

		/**
		 * 订单支付
		 */
		orderPayment,

		/**
		 * 订单退款
		 */
		orderRefunds,

		/**
		 * 订单发货
		 */
		orderShipping,

		/**
		 * 订单退货
		 */
		orderReturns,

		/**
		 * 支付事务
		 */
		paymentTransaction,

		/**
		 * 平台服务
		 */
		platformService
	}
	
	
}
