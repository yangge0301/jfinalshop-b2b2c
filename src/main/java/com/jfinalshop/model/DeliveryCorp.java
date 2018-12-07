package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BaseDeliveryCorp;

/**
 * Model - 物流公司
 * 
 */
public class DeliveryCorp extends BaseDeliveryCorp<DeliveryCorp> {
	private static final long serialVersionUID = -7250487884577128750L;
	public static final DeliveryCorp dao = new DeliveryCorp().dao();
	
	/**
	 * 配送方式
	 */
	private List<ShippingMethod> shippingMethods = new ArrayList<ShippingMethod>();
	
	/**
	 * 获取配送方式
	 * 
	 * @return 配送方式
	 */
	public List<ShippingMethod> getShippingMethods() {
		if (CollectionUtils.isEmpty(shippingMethods)) {
			String sql = "SELECT * FROM `shipping_method` WHERE default_delivery_corp_id = ?";
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
	 * 删除前处理
	 */
	public void preRemove() {
		List<ShippingMethod> shippingMethods = getShippingMethods();
		if (shippingMethods != null) {
			for (ShippingMethod shippingMethod : shippingMethods) {
				shippingMethod.setDefaultDeliveryCorp(null);
			}
		}
	}
}
