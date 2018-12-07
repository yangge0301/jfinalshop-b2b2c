package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.MemberRank;
import com.jfinalshop.model.ProductCategory;
import com.jfinalshop.model.Promotion;
import com.jfinalshop.model.Store;
import com.xiaoleilu.hutool.date.DateUtil;

/**
 * Dao - 促销
 * 
 */
public class PromotionDao extends BaseDao<Promotion> {

	/**
	 * 构造方法
	 */
	public PromotionDao() {
		super(Promotion.class);
	}
	
	/**
	 * 查找促销
	 * 
	 * @param store
	 *            店铺
	 * @param type
	 *            类型
	 * @param isEnabled
	 *            是否开启
	 * @return 促销
	 */
	public List<Promotion> findList(Store store, Promotion.Type type, Boolean isEnabled) {
		String sql = "SELECT * FROM promotion WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		if (store != null) {
			sql += " AND store_id = ? ";
			params.add(store.getId());
		}
		if (type != null) {
			sql += " AND type = ? ";
			params.add(type.ordinal());
		}
		if (isEnabled != null) {
			sql += " AND is_enabled = ? ";
			params.add(isEnabled);
		}
		return super.findList(sql, null, null, null, null, params);
	}

	/**
	 * 查找促销
	 * 
	 * @param store
	 *            店铺
	 * @param type
	 *            类型
	 * @param memberRank
	 *            会员等级
	 * @param productCategory
	 *            商品分类
	 * @param hasBegun
	 *            是否已开始
	 * @param hasEnded
	 *            是否已结束
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 促销
	 */
	public List<Promotion> findList(Store store, Promotion.Type type, MemberRank memberRank, ProductCategory productCategory, Boolean hasBegun, Boolean hasEnded, Integer count, List<Filter> filters, List<Order> orders) {
		String sql = "SELECT * FROM promotion WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		
		if (store != null) {
			sql += " AND store_id = ?";
			params.add(store.getId());
		}
		if (type != null) {
			sql += " AND type = ?";
			params.add(type.ordinal());
		}
		if (memberRank != null) {
			sql += " AND id IN (SELECT p.id FROM `promotion_member_rank` pmr left join promotion p on pmr.promotions_id = p.id WHERE pmr.member_ranks_id = ?) ";
			params.add(memberRank.getId());
		}
		if (productCategory != null) {
			sql += " AND id IN (SELECT p.id FROM `product_category_promotion` pcp left join promotion p on pcp.promotions_id = p.id WHERE pcp.product_categories_id = ?) ";
			params.add(productCategory.getId());
		}
		if (hasBegun != null) {
			if (hasBegun) {
				sql += " AND (begin_date IS NULL OR begin_date <= ?)";
				params.add(DateUtil.now());
			} else {
				sql += " AND (begin_date IS NOT NULL) AND begin_date > ?";
				params.add(DateUtil.now());
			}
		}
		if (hasEnded != null) {
			if (hasEnded) {
				sql += " AND (end_date IS NOT NULL) AND end_date <= ?";
				params.add(DateUtil.now());
			} else {
				sql += " AND (begin_date IS NULL OR end_date > ?)";
				params.add(DateUtil.now());
			}
		}
		return super.findList(sql, null, count, filters, orders, params);
	}

	/**
	 * 查找促销
	 * 
	 * @param store
	 *            店铺
	 * @param type
	 *            类型
	 * @param pageable
	 *            分页
	 * @return 促销分页
	 */
	public Page<Promotion> findPage(Store store, Promotion.Type type, Pageable pageable) {
		String sqlExceptSelect = "FROM promotion WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		if (store != null) {
			sqlExceptSelect += " AND store_id = ?";
			params.add(store.getId());
		}
		if (type != null) {
			sqlExceptSelect += " AND type = ?";
			params.add(type.ordinal());
		}
		return super.findPage(sqlExceptSelect, pageable, params);
	}

}