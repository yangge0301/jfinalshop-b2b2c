package com.jfinalshop.service;

import javax.inject.Inject;

import net.hasor.core.Singleton;

import org.apache.commons.lang.StringUtils;

import com.jfinalshop.dao.OrderPaymentDao;
import com.jfinalshop.dao.SnDao;
import com.jfinalshop.model.OrderPayment;
import com.jfinalshop.model.Sn;
import com.jfinalshop.util.Assert;

/**
 * Service - 订单支付
 * 
 */
@Singleton
public class OrderPaymentService extends BaseService<OrderPayment> {

	/**
	 * 构造方法
	 */
	public OrderPaymentService() {
		super(OrderPayment.class);
	}
	
	@Inject
	private OrderPaymentDao orderPaymentDao;
	@Inject
	private SnDao snDao;
	
	/**
	 * 根据编号查找订单支付
	 * 
	 * @param sn
	 *            编号(忽略大小写)
	 * @return 订单支付，若不存在则返回null
	 */
	public OrderPayment findBySn(String sn) {
		return orderPaymentDao.find("sn", StringUtils.lowerCase(sn));
	}

	@Override
	public OrderPayment save(OrderPayment orderPayment) {
		Assert.notNull(orderPayment);

		orderPayment.setSn(snDao.generate(Sn.Type.orderPayment));

		return super.save(orderPayment);
	}
}