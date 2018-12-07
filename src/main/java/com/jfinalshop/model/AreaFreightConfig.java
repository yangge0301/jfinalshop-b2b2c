package com.jfinalshop.model;

import com.jfinalshop.model.base.BaseAreaFreightConfig;

/**
 * Model - 地区运费配置
 * 
 */
public class AreaFreightConfig extends BaseAreaFreightConfig<AreaFreightConfig> {
	private static final long serialVersionUID = 3445335350897285223L;
	public static final AreaFreightConfig dao = new AreaFreightConfig().dao();
	
	/**
	 * 配送方式
	 */
	private ShippingMethod shippingMethod;
	
	/**
	 * 店铺
	 */
	private Store store;
	
	/**
	 * 地区
	 */
	private Area area;

	/**
	 * 获取地区
	 * 
	 * @return 地区
	 */
	public Area getArea() {
		if (area == null) {
			area = Area.dao.findById(getAreaId());
		}
		return area;
	}

	/**
	 * 设置地区
	 * 
	 * @param area
	 *            地区
	 */
	public void setArea(Area area) {
		this.area = area;
	}
	
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

	/**
	 * 获取配送方式
	 * 
	 * @return 配送方式
	 */
	public ShippingMethod getShippingMethod() {
		if (shippingMethod == null) {
			shippingMethod = ShippingMethod.dao.findById(getShippingMethodId());
		}
		return shippingMethod;
	}

	/**
	 * 设置配送方式
	 * 
	 * @param shippingMethod
	 *            配送方式
	 */
	public void setShippingMethod(ShippingMethod shippingMethod) {
		this.shippingMethod = shippingMethod;
	}
}
