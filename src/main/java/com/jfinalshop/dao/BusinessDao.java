package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.jfinalshop.model.Business;

/**
 * Dao - 商家
 * 
 */
public class BusinessDao extends BaseDao<Business> {

	/**
	 * 构造方法
	 */
	public BusinessDao() {
		super(Business.class);
	}
	
	/**
	 * 通过名称查找商家
	 * 
	 * @param keyword
	 *            关键词
	 * @param count
	 *            数量
	 * @return 商家
	 */
	public List<Business> search(String keyword, Integer count) {
		if (StringUtils.isEmpty(keyword)) {
			return Collections.emptyList();
		}
		List<Object> params = new ArrayList<Object>();
		String sql = "SELECT * FROM business WHERE username like ?";
		params.add("%" + keyword + "%");
		return super.findList(sql, null, count, null, null, params);
	}

}