package com.jfinalshop.service;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinalshop.dao.SnDao;
import com.jfinalshop.model.OrderReturns;
import com.jfinalshop.model.Sn;
import com.jfinalshop.util.Assert;

/**
 * Service - 订单退货
 * 
 */
@Singleton
public class OrderReturnsService extends BaseService<OrderReturns> {

	/**
	 * 构造方法
	 */
	public OrderReturnsService() {
		super(OrderReturns.class);
	}
	
	@Inject
	private SnDao snDao;
	
	@Override
	public OrderReturns save(OrderReturns orderReturns) {
		Assert.notNull(orderReturns);

		orderReturns.setSn(snDao.generate(Sn.Type.orderReturns));

		return super.save(orderReturns);
	}
	
}