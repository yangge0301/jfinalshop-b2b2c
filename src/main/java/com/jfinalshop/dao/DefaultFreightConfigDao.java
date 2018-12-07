package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Area;
import com.jfinalshop.model.DefaultFreightConfig;
import com.jfinalshop.model.ShippingMethod;
import com.jfinalshop.model.Store;

/**
 * Dao - 默认运费配置
 * 
 */
public class DefaultFreightConfigDao extends BaseDao<DefaultFreightConfig> {

	/**
	 * 构造方法
	 */
	public DefaultFreightConfigDao() {
		super(DefaultFreightConfig.class);
	}
	
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
		if (shippingMethod == null || area == null) {
			return false;
		}
		String sql = "SELECT COUNT(*) FROM area_freight_config WHERE shipping_method_id = ? AND area_id = ?";
		Long count = Db.queryLong(sql, shippingMethod.getId(), area.getId());
		return count > 0;
	}

	/**
	 * 查找默认运费配置分页
	 * 
	 * @param store
	 *            店铺
	 * @param pageable
	 *            分页信息
	 * @return 运费配置分页
	 */
	public Page<DefaultFreightConfig> findPage(Store store, Pageable pageable) {
		String sqlExceptSelect = "FROM default_freight_config ";
		List<Object> params = new ArrayList<Object>();
		
		if (store != null) {
			sqlExceptSelect += "AND store_id = ?";
			params.add(store.getId());
		}
		return super.findPage(sqlExceptSelect, pageable, params);
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
		if (shippingMethod == null || store == null) {
			return null;
		}
		String sql = "SELECT * FROM default_freight_config WHERE store_id = ? AND shipping_method_id = ? ORDER BY store_id ASC";
		return modelManager.findFirst(sql, store.getId(), shippingMethod.getId());
	}

}