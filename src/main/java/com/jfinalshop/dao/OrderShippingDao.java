package com.jfinalshop.dao;

import com.jfinalshop.model.Order;
import com.jfinalshop.model.OrderShipping;

/**
 * Dao - 订单发货
 * 
 */
public class OrderShippingDao extends BaseDao<OrderShipping> {

	/**
	 * 构造方法
	 */
	public OrderShippingDao() {
		super(OrderShipping.class);
	}
	
	/**
	 * 查找最后一条订单发货
	 * 
	 * @param order
	 *            订单
	 * @return 订单发货，若不存在则返回null
	 */
	public OrderShipping findLast(Order order) {
		if (order == null) {
			return null;
		}
		String sql = "SELECT * FROM order_shipping WHERE order_id = ? ORDER BY created_date DESC";
		return modelManager.findFirst(sql, order.getId());
	}

}