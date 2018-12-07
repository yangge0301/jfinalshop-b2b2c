package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BasePaymentMethod;

/**
 * Model - 支付方式
 * 
 */
public class PaymentMethod extends BasePaymentMethod<PaymentMethod> {
	private static final long serialVersionUID = 6265962135239167560L;
	public static final PaymentMethod dao = new PaymentMethod().dao();
	
	
	/**
	 * 类型
	 */
	public enum Type {

		/**
		 * 款到发货
		 */
		deliveryAgainstPayment,

		/**
		 * 货到付款
		 */
		cashOnDelivery
	}

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
		offline
	}

	/**
	 * 配送方式
	 */
	private List<ShippingMethod> shippingMethods = new ArrayList<>();

	/**
	 * 订单
	 */
	private List<Order> orders = new ArrayList<>();
	
	/**
	 * 类型名称
	 */
	public Type getTypeName() {
		return getType() != null ? Type.values()[getType()] : null;
	}
	
	/**
	 * 方式名称
	 */
	public Method getMethodName() {
		return getMethod() != null ? Method.values()[getMethod()] : null;
	}
	
	/**
	 * 获取配送方式
	 * 
	 * @return 配送方式
	 */
	public List<ShippingMethod> getShippingMethods() {
		if (CollectionUtils.isEmpty(shippingMethods)) {
			String sql = "SELECT p.*  FROM shipping_method sm LEFT JOIN shipping_method_payment_method smpm ON sm.id = smpm.`shipping_methods_id` WHERE smpm.`payment_methods_id` = ?";
			shippingMethods = ShippingMethod.dao.find(sql, getId());
		}
		return shippingMethods;
	}

	/**
	 * 设置配送方式
	 * 
	 * @param shippingMethods
	 *            配送方式
	 */
	public void setShippingMethods(List<ShippingMethod> shippingMethods) {
		this.shippingMethods = shippingMethods;
	}

	/**
	 * 获取订单
	 * 
	 * @return 订单
	 */
	public List<Order> getOrder() {
		if (CollectionUtils.isEmpty(orders)) {
			String sql = "SELECT * FROM `order` WHERE payment_method_id = ?";
			orders = Order.dao.find(sql, getId());
		}
		return orders;
	}

	/**
	 * 设置订单
	 * 
	 * @param orders
	 *            订单
	 */
	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}
	
}
