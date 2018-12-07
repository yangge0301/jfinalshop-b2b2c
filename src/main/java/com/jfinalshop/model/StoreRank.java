package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BaseStoreRank;

/**
 * Model - 店铺等级
 * 
 */
public class StoreRank extends BaseStoreRank<StoreRank> {
	private static final long serialVersionUID = 3615894631183229605L;
	public static final StoreRank dao = new StoreRank().dao();
	
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
			String sql = "SELECT * FROM `store` WHERE store_rank_id = ?";
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
