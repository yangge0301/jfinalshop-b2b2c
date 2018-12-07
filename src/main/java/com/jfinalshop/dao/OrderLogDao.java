package com.jfinalshop.dao;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.OrderLog;

/**
 * Dao - 订单记录
 * 
 */
public class OrderLogDao extends BaseDao<OrderLog> {

	/**
	 * 构造方法
	 */
	public OrderLogDao() {
		super(OrderLog.class);
	}
	
	/**
	 * 查找订单日志
	 * 只查询第一条符合的记录
	 * @param orderId 订单ID
	 * @param type 类型
	 * @return 订单记录
	 */
	public OrderLog findOrderLog(Long orderId, OrderLog.Type type) {
		if (orderId == null || type == null) {
			return null;
		}
		try {
			String sql = "SELECT * FROM order_log WHERE order_id = ? AND type = ?";
			return modelManager.findFirst(sql, orderId, type.ordinal());
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 删除该订单的所有记录
	 * @param orderId 订单ID
	 */
	public void deleteByOrderId(Long orderId) {
		Db.deleteById("order_log", "order_id", orderId);
	}
	
}