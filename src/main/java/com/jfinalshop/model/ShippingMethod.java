package com.jfinalshop.model;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BaseShippingMethod;

/**
 * Model - 配送方式
 * 
 */
public class ShippingMethod extends BaseShippingMethod<ShippingMethod> {
	private static final long serialVersionUID = 6946157114728483227L;
	public static final ShippingMethod dao = new ShippingMethod().dao();
	
	/**
	 * 默认物流公司
	 */
	private DeliveryCorp defaultDeliveryCorp;

	/**
	 * 支持支付方式
	 */
	private List<PaymentMethod> paymentMethods = new ArrayList<>();

	/**
	 * 默认运费配置
	 */
	private List<DefaultFreightConfig> defaultFreightConfigs = new ArrayList<>();

	/**
	 * 地区运费配置
	 */
	private List<AreaFreightConfig> areaFreightConfigs = new ArrayList<>();

	/**
	 * 订单
	 */
	private List<Order> orders = new ArrayList<>();
	
	/**
	 * 获取默认物流公司
	 * 
	 * @return 默认物流公司
	 */
	public DeliveryCorp getDefaultDeliveryCorp() {
		if (defaultDeliveryCorp == null) {
			defaultDeliveryCorp = DeliveryCorp.dao.findById(getDefaultDeliveryCorpId());
		}
		return defaultDeliveryCorp;
	}

	/**
	 * 设置默认物流公司
	 * 
	 * @param defaultDeliveryCorp
	 *            默认物流公司
	 */
	public void setDefaultDeliveryCorp(DeliveryCorp defaultDeliveryCorp) {
		this.defaultDeliveryCorp = defaultDeliveryCorp;
	}

	/**
	 * 获取支持支付方式
	 * 
	 * @return 支持支付方式
	 */
	public List<PaymentMethod> getPaymentMethods() {
		if (CollectionUtils.isEmpty(paymentMethods)) {
			String sql = "SELECT p.*  FROM payment_method p LEFT JOIN shipping_method_payment_method smpm ON p.id = smpm.`payment_methods_id` WHERE smpm.`shipping_methods_id` = ?";
			paymentMethods = PaymentMethod.dao.find(sql, getId());
		}
		return paymentMethods;
	}

	/**
	 * 设置支持支付方式
	 * 
	 * @param paymentMethods
	 *            支持支付方式
	 */
	public void setPaymentMethods(List<PaymentMethod> paymentMethods) {
		this.paymentMethods = paymentMethods;
	}

	/**
	 * 获取默认运费配置
	 * 
	 * @return 默认运费配置
	 */
	public List<DefaultFreightConfig> getDefaultFreightConfigs() {
		if (CollectionUtils.isEmpty(defaultFreightConfigs)) {
			String sql = "SELECT * FROM default_freight_config WHERE shipping_method_id = ?";
			defaultFreightConfigs = DefaultFreightConfig.dao.find(sql, getId());
		}
		return defaultFreightConfigs;
	}

	/**
	 * 设置默认运费配置
	 * 
	 * @param defaultFreightConfigs
	 *            默认运费配置
	 */
	public void setDefaultFreightConfigs(List<DefaultFreightConfig> defaultFreightConfigs) {
		this.defaultFreightConfigs = defaultFreightConfigs;
	}

	/**
	 * 获取地区运费配置
	 * 
	 * @return 地区运费配置
	 */
	public List<AreaFreightConfig> getAreaFreightConfigs() {
		if (CollectionUtils.isEmpty(areaFreightConfigs)) {
			String sql = "SELECT * FROM area_freight_config WHERE shipping_method_id = ?";
			areaFreightConfigs = AreaFreightConfig.dao.find(sql, getId());
		}
		return areaFreightConfigs;
	}

	/**
	 * 设置地区运费配置
	 * 
	 * @param areaFreightConfigs
	 *            地区运费配置
	 */
	public void setAreaFreightConfigs(List<AreaFreightConfig> areaFreightConfigs) {
		this.areaFreightConfigs = areaFreightConfigs;
	}

	/**
	 * 获取订单
	 * 
	 * @return 订单
	 */
	public List<Order> getOrder() {
		if (CollectionUtils.isEmpty(orders)) {
			String sql = "SELECT * FROM `order` WHERE shipping_method_id = ?";
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

	/**
	 * 判断是否支持支付方式
	 * 
	 * @param paymentMethod
	 *            支付方式
	 * @return 是否支持支付方式
	 */
	public boolean isSupported(PaymentMethod paymentMethod) {
		return paymentMethod == null || (getPaymentMethods() != null && getPaymentMethods().contains(paymentMethod));
	}

	/**
	 * 获取地区运费配置
	 * 
	 * @param store
	 *            店铺
	 * @param area
	 *            地区
	 * @return 地区运费配置
	 */
	public AreaFreightConfig getAreaFreightConfig(Store store, Area area) {
		if (area == null || store == null || CollectionUtils.isEmpty(getAreaFreightConfigs())) {
			return null;
		}
		for (AreaFreightConfig areaFreightConfig : getAreaFreightConfigs()) {
			if (areaFreightConfig.getArea() != null && store.equals(areaFreightConfig.getStore()) && areaFreightConfig.getArea().equals(area)) {
				return areaFreightConfig;
			}
		}
		return null;
	}

	/**
	 * 获取默认运费配置
	 * 
	 * @param store
	 *            店铺
	 * @return 默认运费配置
	 */
	@Transient
	public DefaultFreightConfig getDefaultFreightConfig(final Store store) {
		DefaultFreightConfig defaultFreightConfig = new DefaultFreightConfig();
		if (store == null || CollectionUtils.isEmpty(getDefaultFreightConfigs())) {
			return defaultFreightConfig;
		}
		for (DefaultFreightConfig pDefaultFreightConfig : getDefaultFreightConfigs()) {
			if (pDefaultFreightConfig.getStore() != null && pDefaultFreightConfig.getStore().equals(store)) {
				return pDefaultFreightConfig;
			}
		}
		return defaultFreightConfig;
	}

	/**
	 * 删除前处理
	 */
	public void preRemove() {
		List<Order> orders = getOrder();
		if (orders != null) {
			for (Order order : orders) {
				order.setShippingMethod(null);
			}
		}
	}
	
	
}
