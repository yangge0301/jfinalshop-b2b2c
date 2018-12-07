package com.jfinalshop.dao;

import com.jfinalshop.model.OrderItem;

/**
 * Dao - 订单项
 * 
 */
public class OrderItemDao extends BaseDao<OrderItem> {

	/**
	 * 构造方法
	 */
	public OrderItemDao() {
		super(OrderItem.class);
	}
	
}