package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinalshop.model.base.BaseOrderShipping;

/**
 * Model - 订单发货
 * 
 */
public class OrderShipping extends BaseOrderShipping<OrderShipping> {
	private static final long serialVersionUID = 196610615624833473L;
	public static final OrderShipping dao = new OrderShipping().dao();
	
	/**
	 * 订单
	 */
	private Order order;

	/**
	 * 订单发货项
	 */
	private List<OrderShippingItem> orderShippingItems = new ArrayList<>();
	
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
	 * 获取订单发货项
	 * 
	 * @return 订单发货项
	 */
	public List<OrderShippingItem> getOrderShippingItems() {
		if (CollectionUtils.isEmpty(orderShippingItems)) {
			String sql = "SELECT * FROM `order_shipping_item` WHERE order_shipping_id = ?";
			orderShippingItems = OrderShippingItem.dao.find(sql, getId());
		}
		return orderShippingItems;
	}

	/**
	 * 设置订单发货项
	 * 
	 * @param orderShippingItems
	 *            订单发货项
	 */
	public void setOrderShippingItems(List<OrderShippingItem> orderShippingItems) {
		this.orderShippingItems = orderShippingItems;
	}

	/**
	 * 获取数量
	 * 
	 * @return 数量
	 */
	public int getQuantity() {
		int quantity = 0;
		if (getOrderShippingItems() != null) {
			for (OrderShippingItem orderShippingItem : getOrderShippingItems()) {
				if (orderShippingItem != null && orderShippingItem.getQuantity() != null) {
					quantity += orderShippingItem.getQuantity();
				}
			}
		}
		return quantity;
	}

	/**
	 * 获取是否需要物流
	 * 
	 * @return 是否需要物流
	 */
	public boolean getIsDelivery() {
		return CollectionUtils.exists(getOrderShippingItems(), new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				OrderShippingItem orderShippingItem = (OrderShippingItem) object;
				return orderShippingItem != null && BooleanUtils.isTrue(orderShippingItem.getIsDelivery());
			}
		});
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
		setDeliveryCorpUrl(deliveryCorp != null ? deliveryCorp.getUrl() : null);
		setDeliveryCorpCode(deliveryCorp != null ? deliveryCorp.getCode() : null);
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
