package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinalshop.model.base.BaseOrderReturns;

/**
 * Model - 订单退货
 * 
 */
public class OrderReturns extends BaseOrderReturns<OrderReturns> {
	private static final long serialVersionUID = -6640735965037632391L;
	public static final OrderReturns dao = new OrderReturns().dao();
	
	/**
	 * 订单
	 */
	private Order order;

	/**
	 * 订单退货项
	 */
	private List<OrderReturnsItem> orderReturnsItems = new ArrayList<>();
	
	/**
	 * 获取订单
	 * 
	 * @return 订单
	 */
	public Order getOrder() {
		if (order == null) {
			order = Order.dao.findById(getOrderId());
		}
		return order;
	}

	/**
	 * 设置订单
	 * 
	 * @param order
	 *            订单
	 */
	public void setOrder(Order order) {
		this.order = order;
	}

	/**
	 * 获取订单退货项
	 * 
	 * @return 订单退货项
	 */
	public List<OrderReturnsItem> getOrderReturnsItems() {
		if (CollectionUtils.isEmpty(orderReturnsItems)) {
			String sql = "SELECT * FROM `order_returns_item` WHERE order_returns_id = ?";
			orderReturnsItems = OrderReturnsItem.dao.find(sql, getId());
		}
		return orderReturnsItems;
	}

	/**
	 * 设置订单退货项
	 * 
	 * @param orderReturnsItems
	 *            订单退货项
	 */
	public void setOrderReturnsItems(List<OrderReturnsItem> orderReturnsItems) {
		this.orderReturnsItems = orderReturnsItems;
	}

	/**
	 * 获取数量
	 * 
	 * @return 数量
	 */
	public int getQuantity() {
		int quantity = 0;
		if (getOrderReturnsItems() != null) {
			for (OrderReturnsItem orderReturnsItem : getOrderReturnsItems()) {
				if (orderReturnsItem != null && orderReturnsItem.getQuantity() != null) {
					quantity += orderReturnsItem.getQuantity();
				}
			}
		}
		return quantity;
	}

	/**
	 * 设置配送方式
	 * 
	 * @param shippingMethod
	 *            配送方式
	 */
	public void setShippingMethod(ShippingMethod shippingMethod) {
		setShippingMethod(shippingMethod != null ? shippingMethod.getName() : null);
	}

	/**
	 * 设置物流公司
	 * 
	 * @param deliveryCorp
	 *            物流公司
	 */
	public void setDeliveryCorp(DeliveryCorp deliveryCorp) {
		setDeliveryCorp(deliveryCorp != null ? deliveryCorp.getName() : null);
	}

	/**
	 * 设置地区
	 * 
	 * @param area
	 *            地区
	 */
	public void setArea(Area area) {
		setArea(area != null ? area.getFullName() : null);
	}

	/**
	 * 持久化前处理
	 */
	public void prePersist() {
		setSn(StringUtils.lowerCase(getSn()));
	}
	
	/**
	 * 判断是否为新建对象
	 * 
	 * @return 是否为新建对象
	 */
	public boolean isNew() {
		return getId() == null;
	}
}
