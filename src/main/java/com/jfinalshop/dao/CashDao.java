package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Business;
import com.jfinalshop.model.Cash;

/**
 * Dao - 提现
 * 
 */
public class CashDao extends BaseDao<Cash> {
	
	/**
	 * 构造方法
	 */
	public CashDao() {
		super(Cash.class);
	}

	/**
	 * 查找提现记录分页
	 * 
	 * @param business
	 *            商家
	 * @param pageable
	 *            分页信息
	 * @return 提现记录分页
	 */
	public Page<Cash> findPage(Business business, Pageable pageable) {
		if (business == null) {
			return null;
		}
		String sqlExceptSelect = "FROM cash WHERE business_id = ? ";
		List<Object> params = new ArrayList<Object>();
		params.add(business.getId());
		return super.findPage(sqlExceptSelect, pageable, params);
	}
	
}