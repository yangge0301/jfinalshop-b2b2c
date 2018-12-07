package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Area;
import com.jfinalshop.model.AreaFreightConfig;
import com.jfinalshop.model.ShippingMethod;
import com.jfinalshop.model.Store;

/**
 * Dao - 地区运费配置
 * 
 */
public class AreaFreightConfigDao extends BaseDao<AreaFreightConfig> {

	/**
	 * 构造方法
	 */
	public AreaFreightConfigDao() {
		super(AreaFreightConfig.class);
	}
	
	/**
	 * 判断运费配置是否存在
	 * 
	 * @param shippingMethod
	 *            配送方式
	 * @param store
	 *            店铺
	 * @param area
	 *            地区
	 * @return 运费配置是否存在
	 */
	public boolean exists(ShippingMethod shippingMethod, Store store, Area area) {
		if (shippingMethod == null || store == null || area == null) {
			return false;
		}
		String sql = "SELECT * FROM area_freight_config WHERE shipping_method_id = ? AND store_id = ? AND area_id = ?";
		return modelManager.find(sql, shippingMethod, store, area).size() > 0;
	}

	/**
	 * 判断运费配置是否存在
	 * 
	 * @param id
	 *            ID
	 * @param shippingMethod
	 *            配送方式
	 * @param area
	 *            地区
	 * @param store
	 *            店铺
	 * @return 运费配置是否唯一
	 */
	public boolean unique(Long id, ShippingMethod shippingMethod, Store store, Area area) {
		if (shippingMethod == null || store == null || area == null) {
			return false;
		}
		if (id != null) {
			String sql = "SELECT COUNT(*) FROM area_freight_config WHERE id != ? AND shipping_method_id = ? AND store_id = ? AND area_id = ?";
			Long count = Db.queryLong(sql, id, shippingMethod.getId(), store.getId(), area.getId());
			return count <= 0;
		} else {
			String sql = "SELECT COUNT(*) FROM area_freight_config WHERE shipping_method_id = ? AND store_id = ? AND area_id = ?";
			Long count = Db.queryLong(sql, shippingMethod.getId(), store.getId(), area.getId());
			return count <= 0;
		}
	}

	/**
	 * 查找运费配置分页
	 * 
	 * @param shippingMethod
	 *            配送方式
	 * @param store
	 *            店铺
	 * @param pageable
	 *            分页信息
	 * @return 运费配置分页
	 */
	public Page<AreaFreightConfig> findPage(ShippingMethod shippingMethod, Store store, Pageable pageable) {
		String sqlExceptSelect = "FROM area_freight_config WHERE 1 = 1 ";
		
		List<Object> params = new ArrayList<Object>();
		if (shippingMethod != null) {
			sqlExceptSelect += " AND shipping_method_id = ?";
			params.add(shippingMethod.getId());
		}
		if (store != null) {
			sqlExceptSelect += " AND store_id = ?";
			params.add(store.getId());
		}
		return super.findPage(sqlExceptSelect, pageable, params);
	}

}