package com.jfinalshop.service;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.dao.OrderRefundsDao;
import com.jfinalshop.dao.SnDao;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.OrderRefunds;
import com.jfinalshop.model.Sn;
import com.jfinalshop.util.Assert;

/**
 * Service - 订单退款
 * 
 */
@Singleton
public class OrderRefundsService extends BaseService<OrderRefunds> {

	/**
	 * 构造方法
	 */
	public OrderRefundsService() {
		super(OrderRefunds.class);
	}
	
	@Inject
	private SnDao snDao;
	@Inject
	private OrderRefundsDao orderRefundsDao;
	
	/**
	 * 退款单分页
	 * @author yangzhicong
	 * @param member 会员
	 * @param pageable 分页信息
	 * @return Page<OrderRefunds>结果集
	 */
	public Page<OrderRefunds> findPage(Member member, Pageable pageable) {
		return orderRefundsDao.findPage(member, pageable);
	}
	
	@Override
	public OrderRefunds save(OrderRefunds orderRefunds) {
		Assert.notNull(orderRefunds);

		orderRefunds.setSn(snDao.generate(Sn.Type.orderRefunds));

		return super.save(orderRefunds);
	}
	
}