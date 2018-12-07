package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.DeliveryTemplate;
import com.jfinalshop.model.Store;
import com.jfinalshop.util.Assert;


/**
 * Dao - 快递单模板
 * 
 */
public class DeliveryTemplateDao extends BaseDao<DeliveryTemplate> {
	
	/**
	 * 构造方法
	 */
	public DeliveryTemplateDao() {
		super(DeliveryTemplate.class);
	}

	/**
	 * 查找默认快递单模板
	 * 
	 * @param store
	 *            店铺
	 * @return 默认快递单模板，若不存在则返回null
	 */
	public DeliveryTemplate findDefault(Store store) {
		Assert.notNull(store);

		String sql = "SELECT * FROM delivery_template WHERE is_default = TRUE AND store_id = ?";
		return modelManager.findFirst(sql, store.getId());
	}

	/**
	 * 查找快递单模板
	 * 
	 * @param store
	 *            店铺
	 * @return 快递单模板
	 */
	public List<DeliveryTemplate> findList(Store store) {
		String sql = "SELECT * FROM delivery_template WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		
		if (store != null) {
			sql += " AND store_id = ?";
			params.add(store.getId());
		}
		return super.findList(sql, params);
	}

	/**
	 * 查找快递单模板分页
	 * 
	 * @param store
	 *            店铺
	 * @param pageable
	 *            分页信息
	 * @return 快递单模板分页
	 */
	public Page<DeliveryTemplate> findPage(Store store, Pageable pageable) {
		String sqlExceptSelect = "FROM delivery_template WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		
		if (store != null) {
			sqlExceptSelect += " AND store_id = ?";
			params.add(store.getId());
		}
		return super.findPage(sqlExceptSelect, pageable, params);
	}

	/**
	 * 清除默认
	 * 
	 * @param store
	 *            店铺
	 */
	public void clearDefault(Store store) {
		Assert.notNull(store);

		String sql = "UPDATE delivery_template SET is_default = FALSE WHERE is_default = TRUE AND store_id = ?";
		Db.update(sql, store.getId());
	}

	/**
	 * 清除默认
	 * 
	 * @param exclude
	 *            排除快递单模板
	 */
	public void clearDefault(DeliveryTemplate exclude) {
		Assert.notNull(exclude);

		String sql = "UPDATE delivery_template SET is_default = FALSE WHERE is_default = TRUE AND id != ? AND store_id = ?";
		Db.update(sql, exclude.getId(), exclude.getStore().getId());
	}

}