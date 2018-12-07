package com.jfinalshop.model;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import com.jfinalshop.model.base.BaseOrderPayment;

/**
 * Model - 订单支付
 * 
 * @author SHOP++ Team
 * @version 5.0
 */
public class OrderPayment extends BaseOrderPayment<OrderPayment> {
	private static final long serialVersionUID = 1354820084896544715L;
	public static final OrderPayment dao = new OrderPayment().dao();
	
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
	public OrderPayment.Method getMethodName() {
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
	 * 获取有效金额
	 * 
	 * @return 有效金额
	 */
	public BigDecimal getEffectiveAmount() {
		BigDecimal effectiveAmount = getAmount().subtract(getFee());
		return effectiveAmount.compareTo(BigDecimal.ZERO) >= 0 ? effectiveAmount : BigDecimal.ZERO;
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
