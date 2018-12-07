package com.jfinalshop.model;

import org.apache.commons.lang3.StringUtils;

import com.jfinalshop.model.base.BaseOrderRefunds;

/**
 * Model - 订单退款
 * 
 */
public class OrderRefunds extends BaseOrderRefunds<OrderRefunds> {
	private static final long serialVersionUID = -440904850170972479L;
	public static final OrderRefunds dao = new OrderRefunds().dao();
	
	/**
	 * 方式
	 */
	public enum Method {

		/**
		 * 在线支付
		 */
		online,

		/**
		 * 线下支付
		 */
		offline,

		/**
		 * 预存款支付
		 */
		deposit
	}
	
	
	/**
	 * 订单
	 */
	private Order order;
	
	/**
	 * 获取方式
	 * 
	 * @return 方式
	 */
	public OrderRefunds.Method getMethodName() {
		return getMethod() == null ? null : Method.values()[getMethod()];
	}
	
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
	 * 设置支付方式
	 * 
	 * @param paymentMethod
	 *            支付方式
	 */
	public void setPaymentMethod(PaymentMethod paymentMethod) {
		setPaymentMethod(paymentMethod != null ? paymentMethod.getName() : null);
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
