package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.model.Business;
import com.jfinalshop.model.BusinessAttribute;
import com.jfinalshop.util.StringUtils;

/**
 * Dao - 商家注册项
 * 
 */
public class BusinessAttributeDao extends BaseDao<BusinessAttribute> {
	
	/**
	 * 构造方法
	 */
	public BusinessAttributeDao() {
		super(BusinessAttribute.class);
	}

	/**
	 * 查找未使用的对象属性序号
	 * 
	 * @return 未使用的对象属性序号，若无可用序号则返回null
	 */
	public Integer findUnusedPropertyIndex() {
		for (int i = 0; i < Business.COMMON_ATTRIBUTE_VALUE_PROPERTY_COUNT; i++) {
			String sql = "SELECT COUNT(*) FROM business_attribute WHERE property_index = ?";
			Long count = Db.queryLong(sql, i);
			if (count == 0) {
				return i;
			}
		}
		return null;
	}


	/**
	 * 查找商家注册项
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
	public List<BusinessAttribute> findList(Boolean isEnabled, Integer count, List<Filter> filters, List<Order> orders) {
		String sql = "SELECT * FROM business_attribute WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		if (isEnabled != null) {
			sql += "AND is_enabled = ?";
			params.add(isEnabled);
		}
		return super.findList(sql, null, count, filters, orders, params);
	}

	/**
	 * 清空商家注册项值
	 * 
	 * @param businessAttribute
	 *            商家注册项
	 */
	public void clearAttributeValue(BusinessAttribute businessAttribute) {
		if (businessAttribute == null || businessAttribute.getType() == null || businessAttribute.getPropertyIndex() == null) {
			return;
		}

		String propertyName;
		switch (businessAttribute.getTypeName()) {
		case text:
		case select:
		case checkbox:
		case image:
		case date:
			propertyName = Business.ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + businessAttribute.getPropertyIndex();
			break;
		default:
			propertyName = String.valueOf(businessAttribute.getTypeName());
			break;
		}
		String sql = "UPDATE business SET " + StringUtils.camelToUnderline(propertyName) + " = NULL";
		Db.update(sql);
	}

}