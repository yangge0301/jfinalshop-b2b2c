package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Business;
import com.jfinalshop.model.BusinessDepositLog;

/**
 * Dao - 商家预存款记录
 * 
 */
public class BusinessDepositLogDao extends BaseDao<BusinessDepositLog> {

	/**
	 * 构造方法
	 */
	public BusinessDepositLogDao() {
		super(BusinessDepositLog.class);
	}
	
	/**
	 * 查找商家预存款记录分页
	 * 
	 * @param business
	 *            商家
	 * @param pageable
	 *            分页信息
	 * @return 商家预存款记录分页
	 */
	public Page<BusinessDepositLog> findPage(Business business, Pageable pageable) {
		if (business == null) {
			return null;
		}
		List<Object> params = new ArrayList<Object>();
		String sqlExceptSelect = "FROM business_deposit_log WHERE business_id = ?";
		params.add(business.getId());
		return super.findPage(sqlExceptSelect, pageable, params);
	}

}