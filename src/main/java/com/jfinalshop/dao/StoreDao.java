package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Enhancer;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.CategoryApplication;
import com.jfinalshop.model.ProductCategory;
import com.jfinalshop.model.Store;
import com.xiaoleilu.hutool.date.DateUtil;

/**
 * Dao - 店铺
 * 
 */
public class StoreDao extends BaseDao<Store> {

	/**
	 * 构造方法
	 */
	public StoreDao() {
		super(Store.class);
	}
	
	private ProductCategoryDao productCategoryDao = Enhancer.enhance(ProductCategoryDao.class);
	
	/**
	 * 查找店铺
	 * 
	 * @param type
	 *            类型
	 * @param status
	 *            状态
	 * @param isEnabled
	 *            是否启用
	 * @param hasExpired
	 *            是否过期
	 * @param first
	 *            起始记录
	 * @param count
	 *            数量
	 * @return 店铺
	 */
	public List<Store> findList(Store.Type type, Store.Status status, Boolean isEnabled, Boolean hasExpired, Integer first, Integer count) {
		String sql = "SELECT * FROM `store` WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		
		if (type != null) {
			sql += " AND type = ?";
			params.add(type.ordinal());
		}
		if (status != null) {
			sql += " AND status = ?";
			params.add(status.ordinal());
		}
		if (isEnabled != null) {
			sql += " AND is_enabled = ?";
			params.add(isEnabled);
		}
		if (hasExpired != null) {
			if (hasExpired) {
				sql += " AND (end_date IS NOT NULL) AND end_date <= NOW()";
			} else {
				sql += " AND (end_date IS NULL OR end_date > NOW())";
			}
		}
		return findList(sql, first, count, params);
	}

	/**
	 * 查找经营分类
	 * 
	 * @param store
	 *            店铺
	 * @param status
	 *            状态
	 * @return 经营分类
	 */
	public List<ProductCategory> findProductCategoryList(Store store, CategoryApplication.Status status) {
		String sql = "SELECT pc.* FROM `product_category` pc left join `category_application` ca on pc.`id` = ca.`product_category_id` WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		
		if (store != null) {
			sql += " AND ca.`store_id` = ?";
			params.add(store.getId());
		}
		if (status != null) {
			sql += " AND ca.`status` = ?";
			params.add(status.ordinal());
		}
		return productCategoryDao.findList(sql, params);
	}

	/**
	 * 查找店铺分页
	 * 
	 * @param type
	 *            类型
	 * @param status
	 *            状态
	 * @param isEnabled
	 *            是否启用
	 * @param hasExpired
	 *            是否过期
	 * @param pageable
	 *            分页信息
	 * @return 店铺分页
	 */
	public Page<Store> findPage(Store.Type type, Store.Status status, Boolean isEnabled, Boolean hasExpired, Pageable pageable) {
		String sqlExceptSelect = "FROM `store` WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		
		if (type != null) {
			sqlExceptSelect += " AND type = ?";
			params.add(type);
		}
		if (status != null) {
			sqlExceptSelect += " AND status = ?";
			params.add(status.ordinal());
		}
		if (isEnabled != null) {
			sqlExceptSelect += " AND is_enabled = ?";
			params.add(isEnabled);
		}
		if (hasExpired != null) {
			if (hasExpired) {
				sqlExceptSelect += " AND (end_date IS NOT NULL) AND end_date <= ?";
				params.add(DateUtil.now());
			} else {
				sqlExceptSelect += " AND (end_date IS NULL OR end_date > ?)";
				params.add(DateUtil.now());
			}
		}
		return super.findPage(sqlExceptSelect, pageable, params);
	}

}