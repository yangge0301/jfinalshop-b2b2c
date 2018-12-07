package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.CategoryApplication;
import com.jfinalshop.model.ProductCategory;
import com.jfinalshop.model.Store;

/**
 * Dao - 经营分类申请
 * 
 */
public class CategoryApplicationDao extends BaseDao<CategoryApplication> {
	
	/**
	 * 构造方法
	 */
	public CategoryApplicationDao() {
		super(CategoryApplication.class);
	}

	/**
	 * 查找经营分类申请
	 * 
	 * @param store
	 *            店铺
	 * @param productCategory
	 *            经营分类
	 * @param status
	 *            状态
	 * @return 经营分类申请
	 */
	public List<CategoryApplication> findList(Store store, ProductCategory productCategory, CategoryApplication.Status status) {
		String sql = "SELECT * FROM category_application WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		if (status != null) {
			sql += " AND status = ?";
			params.add(status.ordinal());
		}
		if (store != null) {
			sql += " AND store_id = ?";
			params.add(store.getId());
		}
		if (productCategory != null) {
			sql += " AND product_category_id = ?";
			params.add(productCategory.getId());
		}
		return super.findList(sql, params);
	}

	/**
	 * 查找经营分类申请分页
	 * 
	 * @param store
	 *            店铺
	 * @param pageable
	 *            分页
	 * @return 经营分类申请分页
	 */
	public Page<CategoryApplication> findPage(Store store, Pageable pageable) {
		String sqlExceptSelect = "FROM category_application WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		if (store != null) {
			sqlExceptSelect += " AND store_id = ?";
			params.add(store.getId());
		}
		return super.findPage(sqlExceptSelect, pageable, params);
	}

}