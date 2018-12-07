package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Store;
import com.jfinalshop.model.StoreProductTag;

/**
 * Dao - 店铺商品标签
 * 
 */
public class StoreProductTagDao extends BaseDao<StoreProductTag> {

	/**
	 * 构造方法
	 */
	public StoreProductTagDao() {
		super(StoreProductTag.class);
	}
	
	/**
	 * 查找店铺商品标签
	 * 
	 * @param store
	 *            店铺
	 * @param isEnabled
	 *            是否启用
	 * @return 店铺商品标签
	 */
	public List<StoreProductTag> findList(Store store, Boolean isEnabled) {
		String sql = "SELECT * FROM store_product_tag WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		
		if (store != null) {
			sql += " AND store_id = ?";
			params.add(store.getId());
		}
		if (isEnabled != null) {
			sql += " AND is_enabled = ?";
			params.add(isEnabled);
		}
		sql += " ORDER BY orders ASC ";
		return modelManager.find(sql, params.toArray());
	}

	/**
	 * 查找店铺商品标签分页
	 * 
	 * @param store
	 *            店铺
	 * @param pageable
	 *            分页
	 * @return 店铺商品标签分页
	 */
	public Page<StoreProductTag> findPage(Store store, Pageable pageable) {
		String sqlExceptSelect = "FROM store_product_tag WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		
		if (store != null) {
			sqlExceptSelect += " AND store_id = ?";
			params.add(store.getId());
		}
		return super.findPage(sqlExceptSelect, pageable, params);
	}

}