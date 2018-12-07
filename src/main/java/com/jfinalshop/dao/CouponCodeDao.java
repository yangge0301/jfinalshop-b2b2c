package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Coupon;
import com.jfinalshop.model.CouponCode;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Store;
import com.xiaoleilu.hutool.date.DateUtil;

/**
 * Dao - 优惠码
 * 
 */
public class CouponCodeDao extends BaseDao<CouponCode> {
	
	/**
	 * 构造方法
	 */
	public CouponCodeDao() {
		super(CouponCode.class);
	}
	
	/**
	 * 查找优惠码分页
	 * 
	 * @param member
	 *            会员
	 * @param pageable
	 *            分页信息
	 * @return 优惠码分页
	 */
	public Page<CouponCode> findPage(Member member, Boolean isUsed, Pageable pageable) {
		String sqlExceptSelect = "FROM coupon_code WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		if (member != null) {
			sqlExceptSelect += "AND member_id = ?";
			params.add(member.getId());
		}
		if (isUsed != null) {
			sqlExceptSelect += " AND is_used = ?";
			params.add(isUsed);
		}
		return super.findPage(sqlExceptSelect, pageable, params);
	}
	
	
	/**
	 * 查找优惠码分页
	 * 
	 * @param coupon
	 *            优惠券
	 * @param pageable
	 *            分页信息
	 * @return 优惠码分页
	 */
	public Page<CouponCode> findPage(Coupon coupon, Store store, Pageable pageable) {
		String sqlExceptSelect = "FROM coupon_code cc WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		if (coupon != null) {
			sqlExceptSelect += "AND cc.coupon_id = ?";
			params.add(coupon.getId());
		}
		sqlExceptSelect += " AND EXISTS (SELECT 1 FROM coupon c WHERE c.id = cc.coupon_id AND c.store_id = ?)";
		params.add(store.getId());
		return super.findPage(sqlExceptSelect, pageable, params);
	}

	/**
	 * 查找优惠码数量
	 * 
	 * @param coupon
	 *            优惠券
	 * @param member
	 *            会员
	 * @param hasBegun
	 *            是否已开始
	 * @param hasExpired
	 *            是否已过期
	 * @param isUsed
	 *            是否已使用
	 * @return 优惠码数量
	 */
	public Long count(Coupon coupon, Member member, Boolean hasBegun, Boolean hasExpired, Boolean isUsed) {
		String sql = "SELECT COUNT(1) FROM `coupon_code` cc LEFT JOIN `coupon` c ON cc.`coupon_id` = c.`id` WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		if (coupon != null) {
			sql += " AND c.`id` = ?";
			params.add(coupon.getId());
		}
		if (member != null) {
			sql += " AND cc.`member_id` = ?";
			params.add(member.getId());
		}
		if (hasBegun != null) {
			if (hasBegun) {
				sql += " AND (c.begin_date IS NULL OR c.begin_date <= ?)";
				params.add(DateUtil.now());
			} else {
				sql += " AND (c.begin_date IS NOT NULL OR c.begin_date > ?)";
				params.add(DateUtil.now());
			}
		}
		if (hasExpired != null) {
			if (hasExpired) {
				sql += " AND (c.end_date IS NOT NULL OR c.end_date <= ?)";
				params.add(DateUtil.now());
			} else {
				sql += " AND (c.end_date IS NULL OR c.end_date > ?)";
				params.add(DateUtil.now());
			}
		}
		if (isUsed != null) {
			sql += " AND cc.`is_used` = ?";
			params.add(isUsed);
		}
		return super.count(sql, params);
	}

}