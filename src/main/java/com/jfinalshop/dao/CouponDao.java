package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Coupon;
import com.jfinalshop.model.Store;
import com.xiaoleilu.hutool.date.DateUtil;

/**
 * Dao - 优惠券
 * 
 */
public class CouponDao extends BaseDao<Coupon> {

	/**
	 * 构造方法
	 */
	public CouponDao() {
		super(Coupon.class);
	}
	
	/**
	 * 查找优惠券
	 * 
	 * @param store
	 *            店铺
	 * @param isEnabled
	 *            是否启用
	 * @param isExchange
	 *            是否允许积分兑换
	 * @param hasExpired
	 *            是否已过期
	 * @return 优惠券
	 */
	public List<Coupon> findList(Store store, Boolean isEnabled, Boolean isExchange, Boolean hasExpired) {
		String sql = "SELECT * FROM `coupon` WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		
		if (store != null) {
			sql += " AND store_id = ?";
			params.add(store.getId());
		}
		if (isEnabled != null) {
			sql += " AND is_enabled = ?";
			params.add(isEnabled);
		}
		if (isExchange != null) {
			sql += " AND is_exchange = ?";
			params.add(isExchange);
		}
		if (hasExpired != null) {
			if (hasExpired) {
				sql += " AND end_date <= ?";
				params.add(DateUtil.now());
			} else {
				sql += " AND end_date > ?";
				params.add(DateUtil.now());
			}
		}
		return super.findList(sql, params);
	}

	/**
	 * 查找优惠券分页
	 * 
	 * @param isEnabled
	 *            是否启用
	 * @param isExchange
	 *            是否允许积分兑换
	 * @param hasExpired
	 *            是否已过期
	 * @param pageable
	 *            分页信息
	 * @return 优惠券分页
	 */
	public Page<Coupon> findPage(Boolean isEnabled, Boolean isExchange, Boolean hasExpired, Pageable pageable) {
		String sqlExceptSelect = "FROM `coupon` WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		
		if (isEnabled != null) {
			sqlExceptSelect += " AND is_enabled = ?";
			params.add(isEnabled);
		}
		if (isExchange != null) {
			sqlExceptSelect += " AND is_exchange = ?";
			params.add(isExchange);
		}
		if (hasExpired != null) {
			if (hasExpired) {
				sqlExceptSelect += " IS NOT NULL AND end_date <= ?";
				params.add(DateUtil.now());
			} else {
				sqlExceptSelect += " IS NULL AND end_date > ?";
				params.add(DateUtil.now());
			}
		}
		return super.findPage(sqlExceptSelect, pageable, params);
	}

	/**
	 * 查找优惠券分页
	 * 
	 * @param store
	 *            店铺
	 * @param pageable
	 *            分页信息
	 * @return 优惠券分页
	 */
	public Page<Coupon> findPage(Store store, Pageable pageable) {
		String sqlExceptSelect = "FROM `coupon` WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		
		if (store != null) {
			sqlExceptSelect += "AND store_id = ?";
			params.add(store.getId());
		}
		return super.findPage(sqlExceptSelect, pageable, params);
	}


}