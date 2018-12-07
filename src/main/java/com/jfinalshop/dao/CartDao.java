package com.jfinalshop.dao;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.Cart;
import com.xiaoleilu.hutool.date.DateUtil;

/**
 * Dao - 购物车
 * 
 */
public class CartDao extends BaseDao<Cart> {

	/**
	 * 构造方法
	 */
	public CartDao() {
		super(Cart.class);
	}
	
	/**
	 * 删除过期购物车
	 */
	public void deleteExpired() {
		String cartItemSql = "DELETE FROM cart_item WHERE cart_id IN (SELECT id FROM cart WHERE expire IS NOT NULL AND expire <= ?)";
		String cartSql = "DELETE FROM cart WHERE expire IS NOT NULL AND expire <= ?";
		Db.update(cartItemSql, DateUtil.now());
		Db.update(cartSql, DateUtil.now());
	}

}