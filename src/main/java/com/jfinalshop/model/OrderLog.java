package com.jfinalshop.model;

import com.jfinalshop.model.base.BaseOrderLog;

/**
 * Model - 订单记录
 * 
 */
public class OrderLog extends BaseOrderLog<OrderLog> {
	private static final long serialVersionUID = 6707539448251320656L;
	public static final OrderLog dao = new OrderLog().dao();
	
	/**
	 * 类型
	 */
	public enum Type {

		/**
		 * 订单创建
		 */
		create,

		/**
		 * 订单修改
		 */
		modify,

		/**
		 * 订单取消
		 */
		cancel,

		/**
		 * 订单审核
		 */
		review,

		/**
		 * 订单收款
		 */
		payment,

		/**
		 * 订单退款
		 */
		refunds,

		/**
		 * 订单发货
		 */
		shipping,

		/**
		 * 订单退货
		 */
		returns,

		/**
		 * 订单收货
		 */
		receive,

		/**
		 * 订单完成
		 */
		complete,

		/**
		 * 订单失败
		 */
		fail
	}

	/**
	 * 订单
	 */
	private Order order;

	/**
	 * 获取类型
	 * 
	 * @return 类型
	 */
	public OrderLog.Type getTypeName() {
		return getType() == null ? null : Type.values()[getType()];
	}
	
	
	/**
	 * 获取订单
	 * 
	 * @return 订单
	 */
	public Order getOrder() {
		if (order == null) {
			order = Order.dao.findById(getOrderId());
		}
		return order;
	}

	/**
	 * 设置订单
	 * 
	 * @param order
	 *            订单
	 */
	public void setOrder(Order order) {
		this.order = order;
	}
	
	
}
