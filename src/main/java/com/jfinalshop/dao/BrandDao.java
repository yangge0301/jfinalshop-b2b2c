package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.List;

import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.model.Brand;
import com.jfinalshop.model.ProductCategory;

/**
 * Dao - 品牌
 * 
 */
public class BrandDao extends BaseDao<Brand> {

	/**
	 * 构造方法
	 */
	public BrandDao() {
		super(Brand.class);
	}
	
	/**
	 * 查找品牌
	 * 
	 * @param productCategory
	 *            商品分类
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 品牌
	 */
	public List<Brand> findList(ProductCategory productCategory, Integer count, List<Filter> filters, List<Order> orders) {
		String sql = "SELECT * FROM `brand` b ";
		List<Object> params = new ArrayList<Object>();
		if (productCategory != null) {
			sql += "LEFT JOIN product_category_brand pcb ON b.id = pcb.brands_id WHERE pcb.product_categories_id = ?" ;
			params.add(productCategory.getId());
		} else {
			sql += " WHERE 1 = 1 ";
		}
		return super.findList(sql, null, count, filters, orders, params);
	}

}