package com.jfinalshop.model;

import com.jfinalshop.model.base.BaseDefaultFreightConfig;

/**
 * Model - 默认运费配置
 * 
 */
public class DefaultFreightConfig extends BaseDefaultFreightConfig<DefaultFreightConfig> {
	private static final long serialVersionUID = -5712824632866176997L;
	public static final DefaultFreightConfig dao = new DefaultFreightConfig().dao();
	
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
	
	/**
	 * 判断是否为新建对象
	 * 
	 * @return 是否为新建对象
	 */
	public boolean isNew() {
		return getId() == null;
	}
}
