package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Consultation;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.Store;

/**
 * Dao - 咨询
 * 
 */
public class ConsultationDao extends BaseDao<Consultation> {

	/**
	 * 构造方法
	 */
	public ConsultationDao() {
		super(Consultation.class);
	}
	
	/**
	 * 查找咨询
	 * 
	 * @param member
	 *            会员
	 * @param product
	 *            商品
	 * @param isShow
	 *            是否显示
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 咨询，不包含咨询回复
	 */
	public List<Consultation> findList(Member member, Product product, Boolean isShow, Integer count, List<Filter> filters, List<Order> orders) {
		String sql = "SELECT * FROM consultation WHERE for_consultation_id IS NULL ";
		List<Object> params = new ArrayList<Object>();
		if (member != null) {
			sql += " AND member_id = ?";
			params.add(member.getId());
		}
		if (product != null) {
			sql += " AND product_id = ?";
			params.add(product.getId());
		}
		if (isShow != null) {
			sql += " AND is_show = ?";
			params.add(isShow);
		}
		return super.findList(sql, null, count, filters, orders, params);
	}

	/**
	 * 查找咨询分页
	 * 
	 * @param member
	 *            会员
	 * @param product
	 *            商品
	 * @param store
	 *            店铺
	 * @param isShow
	 *            是否显示
	 * @param pageable
	 *            分页信息
	 * @return 咨询分页，不包含咨询回复
	 */
	public Page<Consultation> findPage(Member member, Product product, Store store, Boolean isShow, Pageable pageable) {
		String sqlExceptSelect = "FROM consultation WHERE for_consultation_id IS NULL ";
		List<Object> params = new ArrayList<Object>();
		if (member != null) {
			sqlExceptSelect += " AND member_id = ?";
			params.add(member.getId());
		}
		if (product != null) {
			sqlExceptSelect += " AND product_id = ?";
			params.add(product.getId());
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
	 * 查找咨询分页(API)
	 * 
	 * @param member
	 *            会员
	 * @param goods
	 *            货品
	 * @param isShow
	 *            是否显示
	 * @param pageable
	 *            分页信息
	 * @return 咨询分页，不包含咨询回复
	 */
	public Page<Consultation> findPage(Member member, Pageable pageable) {
		String select = "SELECT * ";
		String sqlExceptSelect = "FROM (SELECT * FROM consultation WHERE for_consultation_id IS NULL AND member_id = " + member.getId();
		sqlExceptSelect += " UNION ";
		sqlExceptSelect += " SELECT * FROM consultation WHERE for_consultation_id IN (SELECT id FROM consultation WHERE for_consultation_id IS NULL AND member_id = " + member.getId() + ")) AS temp ";
		sqlExceptSelect += " ORDER BY created_date ASC ";
		return modelManager.paginate(pageable.getPageNumber(), pageable.getPageSize(), select, sqlExceptSelect);
	}

	/**
	 * 查找咨询数量
	 * 
	 * @param member
	 *            会员
	 * @param product
	 *            商品
	 * @param isShow
	 *            是否显示
	 * @return 咨询数量，不包含咨询回复
	 */
	public Long count(Member member, Product product, Boolean isShow) {
		String sql = "SELECT COUNT(1) FROM consultation WHERE for_consultation_id IS NULL ";
		List<Object> params = new ArrayList<Object>();
		if (member != null) {
			sql += " AND member_id = ?";
			params.add(member.getId());
		}
		if (product != null) {
			sql += " AND product_id = ?";
			params.add(product.getId());
		}
		if (isShow != null) {
			sql += " AND is_show = ?";
			params.add(isShow);
		}
		return super.count(sql, params);
	}


}