package com.jfinalshop.service;

import net.hasor.core.Singleton;

import com.jfinalshop.model.CartItem;

/**
 * Service - 购物车项
 * 
 */
@Singleton
public class CartItemService extends BaseService<CartItem> {

	/**
	 * 构造方法
	 */
	public CartItemService() {
		super(CartItem.class);
	}
	
}