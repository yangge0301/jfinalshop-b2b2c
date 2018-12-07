package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Order.Direction;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.OrderRefunds;

/**
 * Dao - 订单退款
 * 
 */
public class OrderRefundsDao extends BaseDao<OrderRefunds> {
	
	/**
	 * 构造方法
	 */
	public OrderRefundsDao() {
		super(OrderRefunds.class);
	}

	
	/**
	 * 退款单分页
	 *
	 */
	public Page<OrderRefunds> findPage(Member member, Pageable pageable) {
		String sqlExceptSelect = "FROM order_refunds t WHERE 1 = 1 ";
		
		List<Object> params = new ArrayList<Object>();
		if (member != null) {
			sqlExceptSelect += " AND EXISTS (SELECT 1 FROM `order` o WHERE o.member_id = ? AND o.id = t.order_id)";
			params.add(member.getId());
		}
		
		pageable.setOrderProperty(CREATED_DATE_PROPERTY_NAME);
		pageable.setOrderDirection(Direction.desc.name());
		return super.findPage(sqlExceptSelect, pageable, params);
	}

}