package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.List;

import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.model.StoreRank;

/**
 * Dao - 店铺等级
 * 
 */
public class StoreRankDao extends BaseDao<StoreRank> {

	/**
	 * 构造方法
	 */
	public StoreRankDao() {
		super(StoreRank.class);
	}
	
	/**
	 * 查找店铺等级
	 * 
	 * @param isAllowRegister
	 *            是否允许注册
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 店铺等级
	 */
	public List<StoreRank> findList(Boolean isAllowRegister, List<Filter> filters, List<Order> orders) {
		String sql = "SELECT * FROM store_rank WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		
		if (isAllowRegister != null) {
			sql += " AND is_allow_register = ?";
			params.add(isAllowRegister);
		}
		return super.findList(sql, null, null, filters, orders, params);
	}

}