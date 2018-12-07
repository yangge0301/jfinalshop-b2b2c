package com.jfinalshop.model;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinalshop.model.base.BaseDeliveryTemplate;

/**
 * Model - 快递单模板
 * 
 */
public class DeliveryTemplate extends BaseDeliveryTemplate<DeliveryTemplate> {
	private static final long serialVersionUID = -1955994248357991793L;
	public static final DeliveryTemplate dao = new DeliveryTemplate().dao();
	
	/**
	 * 属性标签名称
	 */
	private static final String ATTRIBUTE_TAG_NMAE = "{%s}";

	/**
	 * 店铺属性
	 */
	public enum StoreAttribute {

		/**
		 * 店铺名称
		 */
		storeName("name"),

		/**
		 * 店铺E-mail
		 */
		storeEmail("email"),

		/**
		 * 店铺手机
		 */
		storeMobile("mobile"),

		/**
		 * 店铺电话
		 */
		storePhone("phone"),

		/**
		 * 店铺地址
		 */
		storeAddress("address"),

		/**
		 * 店铺邮编
		 */
		storeZipCode("zipCode");

		/**
		 * 名称
		 */
		private String name;

		/**
		 * 构造方法
		 * 
		 * @param name
		 *            名称
		 */
		StoreAttribute(String name) {
			this.name = name;
		}

		/**
		 * 获取标签名称
		 * 
		 * @return 标签名称
		 */
		public String getTagName() {
			return String.format(DeliveryTemplate.ATTRIBUTE_TAG_NMAE, toString());
		}

		/**
		 * 获取值
		 * 
		 * @param store
		 *            店铺
		 * @return 值
		 */
		public String getValue(Store store) {
			if (store == null) {
				return null;
			}

			try {
				Object value = PropertyUtils.getProperty(store, name);
				return value != null ? String.valueOf(value) : StringUtils.EMPTY;
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e.getMessage(), e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e.getMessage(), e);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}

	}

	/**
	 * 发货点属性
	 */
	public enum DeliveryCenterAttribute {

		/**
		 * 发货点名称
		 */
		deliveryCenterName("name"),

		/**
		 * 发货点联系人
		 */
		deliveryCenterContact("contact"),

		/**
		 * 发货点地区
		 */
		deliveryCenterAreaName("areaName"),

		/**
		 * 发货点地址
		 */
		deliveryCenterAddress("address"),

		/**
		 * 发货点邮编
		 */
		deliveryCenterZipCode("zipCode"),

		/**
		 * 发货点电话
		 */
		deliveryCenterPhone("phone"),

		/**
		 * 发货点手机
		 */
		deliveryCenterMobile("mobile");

		/**
		 * 名称
		 */
		private String name;

		/**
		 * 构造方法
		 * 
		 * @param name
		 *            名称
		 */
		DeliveryCenterAttribute(String name) {
			this.name = name;
		}

		/**
		 * 获取标签名称
		 * 
		 * @return 标签名称
		 */
		public String getTagName() {
			return String.format(DeliveryTemplate.ATTRIBUTE_TAG_NMAE, toString());
		}

		/**
		 * 获取值
		 * 
		 * @param deliveryCenter
		 *            发货点
		 * @return 值
		 */
		public String getValue(DeliveryCenter deliveryCenter) {
			if (deliveryCenter == null) {
				return null;
			}

			try {
				Object value = PropertyUtils.getProperty(deliveryCenter, name);
				return value != null ? String.valueOf(value) : StringUtils.EMPTY;
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e.getMessage(), e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e.getMessage(), e);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}

	}

	/**
	 * 订单属性
	 */
	public enum OrderAttribute {

		/**
		 * 订单编号
		 */
		orderSn("sn"),

		/**
		 * 订单收货人
		 */
		orderConsignee("consignee"),

		/**
		 * 订单收货地区
		 */
		orderAreaName("areaName"),

		/**
		 * 订单收货地址
		 */
		orderAddress("address"),

		/**
		 * 订单收货邮编
		 */
		orderZipCode("zipCode"),

		/**
		 * 订单收货电话
		 */
		orderPhone("phone"),

		/**
		 * 订单附言
		 */
		orderMemo("memo");

		/**
		 * 名称
		 */
		private String name;

		/**
		 * 构造方法
		 * 
		 * @param name
		 *            名称
		 */
		OrderAttribute(String name) {
			this.name = name;
		}

		/**
		 * 获取标签名称
		 * 
		 * @return 标签名称
		 */
		public String getTagName() {
			return String.format(DeliveryTemplate.ATTRIBUTE_TAG_NMAE, toString());
		}

		/**
		 * 获取值
		 * 
		 * @param order
		 *            订单
		 * @return 值
		 */
		public String getValue(Order order) {
			if (order == null) {
				return null;
			}

			try {
				Object value = PropertyUtils.getProperty(order, name);
				return value != null ? String.valueOf(value) : StringUtils.EMPTY;
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e.getMessage(), e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e.getMessage(), e);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}

	}
	
	/**
	 * 店铺
	 */
	private Store store;
	
	/**
	 * 获取店铺
	 * 
	 * @return 店铺
	 */
	public Store getStore() {
		if (store == null) {
			store = Store.dao.findById(getStoreId());
		}
		return store;
	}

	/**
	 * 设置店铺
	 * 
	 * @param store
	 *            店铺
	 */
	public void setStore(Store store) {
		this.store = store;
	}
	
	
}
