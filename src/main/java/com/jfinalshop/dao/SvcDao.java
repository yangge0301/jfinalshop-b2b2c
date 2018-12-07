package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.List;

import com.jfinalshop.Order;
import com.jfinalshop.model.Store;
import com.jfinalshop.model.StoreRank;
import com.jfinalshop.model.Svc;

/**
 * Dao - 服务
 * 
 */
public class SvcDao extends BaseDao<Svc> {

	/**
	 * 构造方法
	 */
	public SvcDao() {
		super(Svc.class);
	}
	
	/**
	 * 查找服务
	 * 
	 * @param store
	 *            店铺
	 * @param promotionPluginId
	 *            促销插件Id
	 * @param storeRank
	 *            店铺等级
	 * @param orders
	 *            排序
	 * @return 服务
	 */
	public List<Svc> find(Store store, String promotionPluginId, StoreRank storeRank, List<Order> orders) {
		String sql = "SELECT s.* FROM `svc` s CROSS JOIN promotion_plugin_svc pps WHERE s.`id` = pps.`id` ";
		List<Object> params = new ArrayList<Object>();
		
		if (store != null) {
			sql += " AND s.store_id = ?";
			params.add(store.getId());
		}
		if (promotionPluginId != null) {
			sql += " AND pps.promotion_plugin_id = ?";
			params.add(promotionPluginId);
		}
		if (storeRank != null) {
			sql += " AND s.store_id IN (SELECT id FROM store t WHERE t.`store_rank_id` = ?) ";
			params.add(storeRank.getId());
		}
		if (orders == null || orders.isEmpty()) {
			sql += " ORDER BY grade DESC";
		}
		return super.findList(sql, null, null, null, orders, params);
	}

}