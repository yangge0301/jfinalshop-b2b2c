package com.jfinalshop.model;

import com.jfinalshop.model.base.BaseMessageConfig;

/**
 * Model - 消息配置
 * 
 */
public class MessageConfig extends BaseMessageConfig<MessageConfig> {
	private static final long serialVersionUID = 8048069834649808449L;
	public static final MessageConfig dao = new MessageConfig().dao();
	

	/**
	 * 类型
	 */
	public enum Type {

		/**
		 * 会员注册
		 */
		registerMember,

		/**
		 * 订单创建
		 */
		createOrder,

		/**
		 * 订单更新
		 */
		updateOrder,

		/**
		 * 订单取消
		 */
		cancelOrder,

		/**
		 * 订单审核
		 */
		reviewOrder,

		/**
		 * 订单收款
		 */
		paymentOrder,

		/**
		 * 订单退款
		 */
		refundsOrder,

		/**
		 * 订单发货
		 */
		shippingOrder,

		/**
		 * 订单退货
		 */
		returnsOrder,

		/**
		 * 订单收货
		 */
		receiveOrder,

		/**
		 * 订单完成
		 */
		completeOrder,

		/**
		 * 订单失败
		 */
		failOrder,

		/**
		 * 商家注册
		 */
		registerBusiness,

		/**
		 * 店铺审核成功
		 */
		approvalStore,

		/**
		 * 店铺审核失败
		 */
		failStore
	}
	
	/**
	 * 类型名称
	 */
	public Type getTypeName() {
		return getType() != null ? Type.values()[getType()] : null;
	}
	
}
