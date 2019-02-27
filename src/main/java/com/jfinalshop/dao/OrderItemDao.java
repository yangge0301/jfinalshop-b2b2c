package com.jfinalshop.dao;

import com.jfinalshop.model.OrderItem;

import java.util.List;

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


	/**
	 * 查找订单日志
	 * 只查询第一条符合的记录
	 * @param orderId 订单ID
	 * @param type 类型
	 * @return 订单记录
	 */
	public List<OrderItem> findOrderLog(Long orderId) {
		if (orderId == null ) {
			return null;
		}
		try {
			String sql = "SELECT * FROM order_item WHERE order_id = ?";
			return modelManager.find(sql, orderId);
		} catch (Exception e) {
			return null;
		}
	}



}