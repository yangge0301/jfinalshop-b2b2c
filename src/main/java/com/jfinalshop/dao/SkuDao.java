package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.jfinalshop.model.Product;
import com.jfinalshop.model.Sku;
import com.jfinalshop.model.Store;
import com.jfinalshop.util.SqlUtils;

/**
 * Dao - SKU
 * 
 */
public class SkuDao extends BaseDao<Sku> {

	/**
	 * 构造方法
	 */
	public SkuDao() {
		super(Sku.class);
	}
	
	/**
	 * 通过编号、名称查找SKU
	 * 
	 * @param store
	 *            店铺
	 * @param type
	 *            类型
	 * @param keyword
	 *            关键词
	 * @param excludes
	 *            排除SKU
	 * @param count
	 *            数量
	 * @return SKU
	 */
	public List<Sku> search(Store store, Product.Type type, String keyword, Set<Sku> excludes, Integer count) {
		if (StringUtils.isEmpty(keyword)) {
			return Collections.emptyList();
		}
		List<Object> params = new ArrayList<Object>();
		String sql = "SELECT s.* FROM sku s CROSS JOIN product p WHERE s.product_id = p.id ";
		if (store != null) {
			sql += " AND p.store_id = ?";
			params.add(store.getId());
		}
		if (type != null) {
			sql += " AND p.type = ?" ;
			params.add(type.ordinal());
		}
		sql += " AND (s.sn LIKE ? OR p.name LIKE ?)";
		params.add("%" + keyword + "%");
		params.add("%" + keyword + "%");
		if (CollectionUtils.isNotEmpty(excludes)) {
			sql += " AND s.id NOT IN " + SqlUtils.getSQLIn(Arrays.asList(getSkuIds(excludes)));
		}
		return super.findList(sql, null, count, null, null, params);
	}


	/**
	 * 获取排除SKU ID
	 * 
	 * @return sku ID
	 */
	private Long[] getSkuIds(Set<Sku> skus) {
		Long[] result = new Long[skus.size()];
		int i = 0;
		for (Sku sku : skus) {
			result[i] = sku.getId();
			i++;
		}
		return result;
	}
	
}