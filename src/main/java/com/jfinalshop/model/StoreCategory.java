package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BaseStoreCategory;

/**
 * Model - 店铺分类
 * 
 */
public class StoreCategory extends BaseStoreCategory<StoreCategory> {
	private static final long serialVersionUID = -2058428349532253523L;
	public static final StoreCategory dao = new StoreCategory().dao();
	
	/**
	 * 店铺
	 */
	private List<Store> stores = new ArrayList<>();
	
	/**
	 * 获取店铺
	 * 
	 * @return 店铺
	 */
	public List<Store> getStores() {
		if (CollectionUtils.isEmpty(stores)) {
			String sql = "SELECT * FROM `store` WHERE store_category_id = ?";
			stores = Store.dao.find(sql, getId());
		}
		return stores;
	}

	/**
	 * 设置店铺
	 * 
	 * @param stores
	 *            店铺
	 */
	public void setStores(List<Store> stores) {
		this.stores = stores;
	}
}
