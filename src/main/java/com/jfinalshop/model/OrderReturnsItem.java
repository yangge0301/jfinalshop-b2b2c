package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BaseOrderReturnsItem;
import com.jfinalshop.util.JsonUtils;

/**
 * Model - 退货项
 * 
 */
public class OrderReturnsItem extends BaseOrderReturnsItem<OrderReturnsItem> {
	private static final long serialVersionUID = 4633735717297649673L;
	public static final OrderReturnsItem dao = new OrderReturnsItem().dao();
	
	/**
	 * 订单退货
	 */
	private OrderReturns orderReturns;

	/**
	 * 规格
	 */
	private List<String> specifications = new ArrayList<>();
	
	/**
	 * 获取订单退货
	 * 
	 * @return 订单退货
	 */
	public OrderReturns getOrderReturns() {
		if (orderReturns == null) {
			orderReturns = OrderReturns.dao.findById(getOrderReturnsId());
		}
		return orderReturns;
	}

	/**
	 * 设置订单退货
	 * 
	 * @param orderReturns
	 *            订单退货
	 */
	public void setOrderReturns(OrderReturns orderReturns) {
		this.orderReturns = orderReturns;
	}

	/**
	 * 获取规格
	 * 
	 * @return 规格
	 */
	public List<String> getSpecificationConverter() {
		if (CollectionUtils.isEmpty(specifications)) {
			specifications = JsonUtils.convertJsonStrToList(getSpecifications());
		}
		return specifications;
	}

	/**
	 * 设置规格
	 * 
	 * @param specifications
	 *            规格
	 */
	public void setSpecifications(List<String> specifications) {
		this.specifications = specifications;
	}

}
