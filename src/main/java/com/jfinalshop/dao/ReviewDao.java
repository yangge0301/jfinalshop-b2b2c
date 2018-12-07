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
import com.jfinalshop.model.Review;
import com.jfinalshop.model.Store;
import com.jfinalshop.util.Assert;

/**
 * Dao - 评论
 * 
 */
public class ReviewDao extends BaseDao<Review> {

	/**
	 * 构造方法
	 */
	public ReviewDao() {
		super(Review.class);
	}
	
	/**
	 * 查找评论
	 * 
	 * @param member
	 *            会员
	 * @param product
	 *            商品
	 * @param type
	 *            类型
	 * @param isShow
	 *            是否显示
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 评论
	 */
	public List<Review> findList(Member member, Product product, Review.Type type, Boolean isShow, Integer count, List<Filter> filters, List<Order> orders) {
		String sql = "SELECT * FROM review r WHERE for_review_id IS NULL ";
		List<Object> params = new ArrayList<Object>();
		
		if (member != null) {
			sql += " AND member_id = ?";
			params.add(member.getId());
		}
		if (product != null) {
			sql += " AND product_id = ?";
			params.add(product.getId());
		}
		if (type != null) {
			switch (type) {
			case positive:
				sql += " AND score > ?";
				params.add(4);
				break;
			case moderate:
				sql += " AND score = ?";
				params.add(3);
				break;
			case negative:
				sql += " AND score < ?";
				params.add(2);
				break;
			default:
				break;
			}
		}
		if (isShow != null) {
			sql += " AND is_show = ?";
			params.add(isShow);
		}
		return super.findList(sql, null, count, filters, orders, params);
	}

	/**
	 * 查找评论分页
	 * 
	 * @param member
	 *            会员
	 * @param product
	 *            商品
	 * @param store
	 *            店铺
	 * @param type
	 *            类型
	 * @param isShow
	 *            是否显示
	 * @param pageable
	 *            分页信息
	 * @return 评论分页
	 */
	public Page<Review> findPage(Member member, Product product, Store store, Review.Type type, Boolean isShow, Pageable pageable) {
		String sqlExceptSelect = "FROM review r WHERE for_review_id IS NULL ";
		List<Object> params = new ArrayList<Object>();
		
		if (member != null) {
			sqlExceptSelect += " AND member_id = ?";
			params.add(member.getId());
		}
		if (product != null) {
			sqlExceptSelect += " AND product_id = ?";
			params.add(product.getId());
		}
		if (type != null) {
			switch (type) {
			case positive:
				sqlExceptSelect += " AND score > ?";
				params.add(4);
				break;
			case moderate:
				sqlExceptSelect += " AND score = ?";
				params.add(3);
				break;
			case negative:
				sqlExceptSelect += " AND score < ?";
				params.add(2);
				break;
			default:
				break;
			}
		}
		if (isShow != null) {
			sqlExceptSelect += " AND is_show = ?";
			params.add(isShow);
		}
		if (store != null) {
			sqlExceptSelect += " AND store_id = ?";
			params.add(store.getId());
		}
		return super.findPage(sqlExceptSelect, pageable, params);
	}

	/**
	 * 查找评论数量
	 * 
	 * @param member
	 *            会员
	 * @param product
	 *            商品
	 * @param type
	 *            类型
	 * @param isShow
	 *            是否显示
	 * @return 评论数量
	 */
	public Long count(Member member, Product product, Review.Type type, Boolean isShow) {
		String sql = "SELECT COUNT(1) FROM review r WHERE for_review_id IS NULL ";
		List<Object> params = new ArrayList<Object>();
		
		if (member != null) {
			sql += " AND member_id = ?";
			params.add(member.getId());
		}
		if (product != null) {
			sql += " AND product_id = ?";
			params.add(product.getId());
		}
		if (type != null) {
			switch (type) {
			case positive:
				sql += " AND score > ?";
				params.add(4);
				break;
			case moderate:
				sql += " AND score = ?";
				params.add(3);
				break;
			case negative:
				sql += " AND score < ?";
				params.add(2);
				break;
			default:
				break;
			}
		}
		if (isShow != null) {
			sql += " AND is_show = ?";
			params.add(isShow);
		}
		return super.count(sql, params);
	}

	/**
	 * 计算商品总评分
	 * 
	 * @param product
	 *            商品
	 * @return 商品总评分，仅计算显示评论
	 */
	public long calculateTotalScore(Product product) {
		Assert.notNull(product);

		String sql = "SELECT SUM(score) FROM review WHERE product_id = ? AND is_show = ? AND for_review_id IS NULL";
		Long totalScore = Db.queryLong(sql, product.getId(), true);
		return totalScore != null ? totalScore : 0L;
	}

	/**
	 * 计算商品评分次数
	 * 
	 * @param product
	 *            商品
	 * @return 商品评分次数，仅计算显示评论
	 */
	public long calculateScoreCount(Product product) {
		Assert.notNull(product);

		String sql = "SELECT COUNT(*) FROM review WHERE product_id = ? AND is_show = ? AND for_review_id IS NULL";
		return Db.queryLong(sql, product.getId(), true);
	}

}