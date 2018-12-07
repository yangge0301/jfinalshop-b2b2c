package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.DeliveryCenter;
import com.jfinalshop.model.Store;
import com.jfinalshop.util.Assert;

/**
 * Dao - 发货点
 * 
 */
public class DeliveryCenterDao extends BaseDao<DeliveryCenter> {

	/**
	 * 构造方法
	 */
	public DeliveryCenterDao() {
		super(DeliveryCenter.class);
	}
	
	/**
	 * 查找默认发货点
	 * 
	 * @param store
	 *            店铺
	 * @return 默认发货点，若不存在则返回null
	 */
	public DeliveryCenter findDefault(Store store) {
		String sql = "SELECT * FROM delivery_center WHERE is_default = TRUE AND store_id = ?";
		return modelManager.findFirst(sql, store.getId());
	}

	/**
	 * 清除默认
	 * 
	 * @param store
	 *            店铺
	 */
	public void clearDefault(Store store) {
		String sql = "UPDATE delivery_center SET is_default = FALSE WHERE is_default = TRUE AND store_id = ?";
		Db.update(sql, store.getId());
	}

	/**
	 * 清除默认
	 * 
	 * @param exclude
	 *            排除发货点
	 */
	public void clearDefault(DeliveryCenter exclude) {
		Assert.notNull(exclude);

		String sql = "UPDATE delivery_center SET is_default = FALSE WHERE is_default = TRUE AND id != ? AND store_id = ?";
		Db.update(sql, exclude.getId(), exclude.getStore().getId());
	}

	/**
	 * 查找发货点分页
	 * 
	 * @param store
	 *            店铺
	 * @param pageable
	 *            分页
	 * @return 发货点分页
	 */
	public Page<DeliveryCenter> findPage(Store store, Pageable pageable) {
		String sqlExceptSelect = "FROM delivery_center WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		
		if (store != null) {
			sqlExceptSelect += " AND store_id = ?";
			params.add(store.getId());
		}
		return super.findPage(sqlExceptSelect, pageable, params);
	}

	/**
	 * 查找发货点
	 * 
	 * @param store
	 *            店铺
	 * @return 发货点
	 */
	public List<DeliveryCenter> findAll(Store store) {
		String sql = "SELECT * FROM delivery_center WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		
		if (store != null) {
			sql += " AND store_id = ?";
			params.add(store.getId());
		}
		return super.findList(sql, params);
	}

}