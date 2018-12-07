package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Store;
import com.jfinalshop.model.StoreFavorite;

/**
 * Dao - 店铺收藏
 * 
 */
public class StoreFavoriteDao extends BaseDao<StoreFavorite> {

	/**
	 * 构造方法
	 */
	public StoreFavoriteDao() {
		super(StoreFavorite.class);
	}
	
	/**
	 * 判断店铺收藏是否存在
	 * 
	 * @param member
	 *            会员
	 * @param store
	 *            店铺
	 * @return 店铺收藏是否存在
	 */
	public boolean exists(Member member, Store store) {
		String sql = "SELECT COUNT(*) FROM store_favorite WHERE member_id = ? AND store_id = ?";
		Long count = Db.queryLong(sql, member.getId(), store.getId());
		return count > 0;
	}

	/**
	 * 查找店铺收藏
	 * 
	 * @param member
	 *            会员
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 店铺收藏
	 */
	public List<StoreFavorite> findList(Member member, Integer count, List<Filter> filters, List<Order> orders) {
		String sql = "SELECT COUNT(*) FROM store_favorite WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		if (member != null) {
			sql += " AND member_id = ?";
			params.add(member.getId());
		}
		return super.findList(sql, null, count, filters, orders, params);
	}

	/**
	 * 查找店铺收藏分页
	 * 
	 * @param member
	 *            会员
	 * @param pageable
	 *            分页信息
	 * @return 店铺收藏分页
	 */
	public Page<StoreFavorite> findPage(Member member, Pageable pageable) {
		String sqlExceptSelect = "FROM store_favorite WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		
		if (member != null) {
			sqlExceptSelect += " AND member_id = ?";
			params.add(member.getId());
		}
		return super.findPage(sqlExceptSelect, pageable, params);
	}

	/**
	 * 查找店铺收藏数量
	 * 
	 * @param member
	 *            会员
	 * @return 店铺收藏数量
	 */
	public Long count(Member member) {
		String sql = "SELECT COUNT(*) FROM store_favorite WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		
		if (member != null) {
			sql += " AND member_id = ?";
			params.add(member.getId());
		}
		return super.count(sql, params);
	}

}