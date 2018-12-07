package com.jfinalshop.model;

import com.jfinalshop.model.base.BaseStoreAdImage;

/**
 * Model - 店铺广告图片
 * 
 */
public class StoreAdImage extends BaseStoreAdImage<StoreAdImage> {
	private static final long serialVersionUID = -4133311873917430171L;
	public static final StoreAdImage dao = new StoreAdImage().dao();
	

	/**
	 * 最大店铺广告图片数
	 */
	public static final Integer MAX_COUNT = 5;
	
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
