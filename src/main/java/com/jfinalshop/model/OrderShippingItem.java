package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BaseOrderShippingItem;
import com.jfinalshop.util.JsonUtils;

/**
 * Model - 发货项
 * 
 */
public class OrderShippingItem extends BaseOrderShippingItem<OrderShippingItem> {
	private static final long serialVersionUID = -6595373066334088430L;
	public static final OrderShippingItem dao = new OrderShippingItem().dao();
	
	/**
	 * SKU
	 */
	private Sku sku;

	/**
	 * 订单发货
	 */
	private OrderShipping orderShipping;

	/**
	 * 规格
	 */
	private List<String> specifications = new ArrayList<>();
	
	/**
	 * 获取SKU
	 * 
	 * @return SKU
	 */
	public Sku getSku() {
		if (sku == null) {
			sku = Sku.dao.findById(getSkuId());
		}
		return sku;
	}

	/**
	 * 设置SKU
	 * 
	 * @param sku
	 *            SKU
	 */
	public void setSku(Sku sku) {
		this.sku = sku;
	}

	/**
	 * 获取订单发货
	 * 
	 * @return 订单发货
	 */
	public OrderShipping getOrderShipping() {
		if (orderShipping == null) {
			orderShipping = OrderShipping.dao.findById(getOrderShippingId());
		}
		return orderShipping;
	}

	/**
	 * 设置订单发货
	 * 
	 * @param orderShipping
	 *            订单发货
	 */
	public void setOrderShipping(OrderShipping orderShipping) {
		this.orderShipping = orderShipping;
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
