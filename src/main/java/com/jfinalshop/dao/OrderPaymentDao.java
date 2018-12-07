package com.jfinalshop.dao;

import com.jfinalshop.model.OrderPayment;

/**
 * Dao - 订单支付
 * 
 */
public class OrderPaymentDao extends BaseDao<OrderPayment> {

	/**
	 * 构造方法
	 */
	public OrderPaymentDao() {
		super(OrderPayment.class);
	}
	
}