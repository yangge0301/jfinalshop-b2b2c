package com.jfinalshop.model;

import com.jfinalshop.model.base.BaseDeliveryCenter;

/**
 * Modle - 发货点
 * 
 */
public class DeliveryCenter extends BaseDeliveryCenter<DeliveryCenter> {
	private static final long serialVersionUID = -4284520832959174798L;
	public static final DeliveryCenter dao = new DeliveryCenter().dao();
	
	
	/**
	 * 地区
	 */
	private Area area;

	/**
	 * 店铺
	 */
	private Store store;
	
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

}
