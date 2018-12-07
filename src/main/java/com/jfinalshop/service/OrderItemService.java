package com.jfinalshop.service;

import net.hasor.core.Singleton;

import com.jfinalshop.model.OrderItem;

/**
 * Service - 订单项
 * 
 */
@Singleton
public class OrderItemService extends BaseService<OrderItem> {

	/**
	 * 构造方法
	 */
	public OrderItemService() {
		super(OrderItem.class);
	}
	
}