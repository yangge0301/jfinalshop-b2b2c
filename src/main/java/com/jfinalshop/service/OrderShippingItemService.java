package com.jfinalshop.service;

import net.hasor.core.Singleton;

import com.jfinalshop.model.OrderShippingItem;

/**
 * Service - 发货项
 * 
 */
@Singleton
public class OrderShippingItemService extends BaseService<OrderShippingItem> {

	/**
	 * 构造方法
	 */
	public OrderShippingItemService() {
		super(OrderShippingItem.class);
	}
	
}