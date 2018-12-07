package com.jfinalshop.service;


import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.dao.DefaultFreightConfigDao;
import com.jfinalshop.model.Area;
import com.jfinalshop.model.DefaultFreightConfig;
import com.jfinalshop.model.ShippingMethod;
import com.jfinalshop.model.Store;
import com.jfinalshop.util.Assert;

/**
 * Service - 默认运费配置
 * 
 */
@Singleton
public class DefaultFreightConfigService extends BaseService<DefaultFreightConfig> {

	/**
	 * 构造方法
	 */
	public DefaultFreightConfigService() {
		super(DefaultFreightConfig.class);
	}
	
	@Inject
	private DefaultFreightConfigDao defaultFreightConfigDao;
	
	/**
	 * 判断运费配置是否存在
	 * 
	 * @param shippingMethod
	 *            配送方式
	 * @param area
	 *            地区
	 * @return 运费配置是否存在
	 */
	public boolean exists(ShippingMethod shippingMethod, Area area) {
		return defaultFreightConfigDao.exists(shippingMethod, area);
	}

	/**
	 * 判断运费配置是否唯一
	 * 
	 * @param shippingMethod
	 *            配送方式
	 * @param previousArea
	 *            修改前地区
	 * @param currentArea
	 *            当前地区
	 * @return 运费配置是否唯一
	 */
	public boolean unique(ShippingMethod shippingMethod, Area previousArea, Area currentArea) {
		if (previousArea != null && previousArea.equals(currentArea)) {
			return true;
		}
		return !defaultFreightConfigDao.exists(shippingMethod, currentArea);
	}

	/**
	 * 查找运费配置分页
	 * 
	 * @param store
	 *            店铺
	 * @param pageable
	 *            分页信息
	 * @return 运费配置分页
	 */
	public Page<DefaultFreightConfig> findPage(Store store, Pageable pageable) {
		return defaultFreightConfigDao.findPage(store, pageable);
	}

	/**
	 * 查找默认运费配置
	 * 
	 * @param shippingMethod
	 *            配送方式
	 * @param store
	 *            店铺
	 * @return 默认运费配置
	 */
	public DefaultFreightConfig find(ShippingMethod shippingMethod, Store store) {
		return defaultFreightConfigDao.find(shippingMethod, store);
	}

	/**
	 * 更新
	 * 
	 * @param defaultFreightConfig
	 *            默认运费配置
	 * @param store
	 *            店铺
	 * @param shippingMethod
	 *            配送方式
	 */
	public void update(DefaultFreightConfig defaultFreightConfig, Store store, ShippingMethod shippingMethod) {
		Assert.notNull(store);
		Assert.notNull(shippingMethod);
		if (!defaultFreightConfig.isNew()) {
			super.update(defaultFreightConfig);
		} else {
			defaultFreightConfig.setStoreId(store.getId());
			defaultFreightConfig.setShippingMethodId(shippingMethod.getId());
			super.save(defaultFreightConfig);
		}
	}

}