package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.ProductFavorite;

/**
 * Dao - 商品收藏
 * 
 */
public class ProductFavoriteDao extends BaseDao<ProductFavorite> {

	/**
	 * 构造方法
	 */
	public ProductFavoriteDao() {
		super(ProductFavorite.class);
	}
	
	/**
	 * 判断商品收藏是否存在
	 * 
	 * @param member
	 *            会员
	 * @param product
	 *            商品
	 * @return 商品收藏是否存在
	 */
	public boolean exists(Member member, Product product) {
		String sql = "SELECT COUNT(*) FROM product_favorite WHERE member_id = ? and product_id = ?";
		Long count = Db.queryLong(sql, member.getId(), product.getId());
		return count > 0;
	}

	/**
	 * 查找商品收藏
	 * 
	 * @param member
	 *            会员
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 商品收藏
	 */
	public List<ProductFavorite> findList(Member member, Integer count, List<Filter> filters, List<Order> orders) {
		String sql = "SELECT * FROM `product_favorite` WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		if (member != null) {
			sql += " AND member_id = ?";
			params.add(member.getId());
		}
		return super.findList(sql, null, count, filters, orders, params);
	}

	/**
	 * 查找商品收藏分页
	 * 
	 * @param member
	 *            会员
	 * @param pageable
	 *            分页信息
	 * @return 商品收藏分页
	 */
	public Page<ProductFavorite> findPage(Member member, Pageable pageable) {
		String sqlExceptSelect = "FROM `product_favorite` WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		
		if (member != null) {
			sqlExceptSelect += " AND member_id = ?";
			params.add(member.getId());
		}
		return super.findPage(sqlExceptSelect, pageable, params);
	}

	/**
	 * 查找商品收藏数量
	 * 
	 * @param member
	 *            会员
	 * @return 商品收藏数量
	 */
	public Long count(Member member) {
		String sql = "SELECT COUNT(1) FROM `product_favorite` WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		
		if (member != null) {
			sql += " AND member_id = ?";
			params.add(member.getId());
		}
		return super.count(sql, params);
	}

}