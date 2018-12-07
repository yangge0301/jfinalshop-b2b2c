package com.jfinalshop.dao;

import com.jfinalshop.model.CartItem;

/**
 * Dao - 购物车项
 * 
 */
public class CartItemDao extends BaseDao<CartItem> {

	/**
	 * 构造方法
	 */
	public CartItemDao() {
		super(CartItem.class);
	}
	
}