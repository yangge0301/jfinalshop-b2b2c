package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.model.Attribute;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.ProductCategory;
import com.jfinalshop.util.Assert;

/**
 * Dao - 属性
 * 
 */
public class AttributeDao extends BaseDao<Attribute> {

	/**
	 * 构造方法
	 */
	public AttributeDao() {
		super(Attribute.class);
	}
	
	/**
	 * 查找未使用的属性序号
	 * 
	 * @param productCategory
	 *            商品分类
	 * @return 未使用的属性序号，若不存在则返回null
	 */
	public Integer findUnusedPropertyIndex(ProductCategory productCategory) {
		Assert.notNull(productCategory);

		for (int i = 0; i < Product.ATTRIBUTE_VALUE_PROPERTY_COUNT; i++) {
			String sql = "SELECT COUNT(*) FROM attribute WHERE product_category_id = ? AND property_index = ?";
			Long count = Db.queryLong(sql, productCategory.getId(), i);
			if (count == 0) {
				return i;
			}
		}
		return null;
	}

	/**
	 * 查找属性
	 * 
	 * @param productCategory
	 *            商品分类
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 属性
	 */
	public List<Attribute> findList(ProductCategory productCategory, Integer count, List<Filter> filters, List<Order> orders) {
		String sql = "SELECT * FROM attribute WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		if (productCategory != null) {
			sql += "AND product_category_id = ?";
			params.add(productCategory.getId());
		}
		return super.findList(sql, null, count, filters, orders, params);
	}
	

}