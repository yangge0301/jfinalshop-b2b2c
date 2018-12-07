package com.jfinalshop.service;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinalshop.dao.OrderLogDao;
import com.jfinalshop.model.OrderLog;

/**
 * Service - 订单记录
 * 
 */
@Singleton
public class OrderLogService extends BaseService<OrderLog> {

	/**
	 * 构造方法
	 */
	public OrderLogService() {
		super(OrderLog.class);
	}
	
	@Inject
	private OrderLogDao orderLogDao;
	
	/**
	 * 查找订单日志
	 * 只查询第一条符合的记录
	 * @param orderId 订单ID
	 * @param type 类型
	 * @return 订单记录
	 */
	public OrderLog findOrderLog(Long orderId, OrderLog.Type type) {
		return orderLogDao.findOrderLog(orderId, type);
	}
	
	/**
	 * 删除该订单的所有记录
	 * @param orderId 订单ID
	 */
	public void deleteByOrderId(Long orderId) {
		orderLogDao.deleteByOrderId(orderId);
	}
	
}