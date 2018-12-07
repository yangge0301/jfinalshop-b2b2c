package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.MemberAttribute;

/**
 * Dao - 会员注册项
 * 
 */
public class MemberAttributeDao extends BaseDao<MemberAttribute> {

	/**
	 * 构造方法
	 */
	public MemberAttributeDao() {
		super(MemberAttribute.class);
	}
	
	/**
	 * 查找未使用的属性序号
	 * 
	 * @return 未使用的属性序号，若不存在则返回null
	 */
	public Integer findUnusedPropertyIndex() {
		for (int i = 0; i < Member.ATTRIBUTE_VALUE_PROPERTY_COUNT; i++) {
			String sql = "SELECT COUNT(*) FROM member_attribute WHERE property_index = ?";
			Long count = Db.queryLong(sql, i);
			if (count == 0) {
				return i;
			}
		}
		return null;
	}

	/**
	 * 查找会员注册项
	 * 
	 * @param isEnabled
	 *            是否启用
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 会员注册项
	 */
	public List<MemberAttribute> findList(Boolean isEnabled, Integer count, List<Filter> filters, List<Order> orders) {
		String sql = "SELECT * FROM member_attribute WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		if (isEnabled != null) {
			sql += "AND is_enabled = ?";
			params.add(isEnabled);
		}
		return super.findList(sql, null, count, filters, orders, params);
	}

}