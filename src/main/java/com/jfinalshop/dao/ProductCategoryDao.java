package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.CompareToBuilder;

import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.model.ProductCategory;
import com.jfinalshop.model.Store;
import com.jfinalshop.util.SqlUtils;

/**
 * Dao - 商品分类
 * 
 */
public class ProductCategoryDao extends BaseDao<ProductCategory> {

	/**
	 * 构造方法
	 */
	public ProductCategoryDao() {
		super(ProductCategory.class);
	}
	
	/**
	 * 查找商品分类
	 * 
	 * @param store
	 *            店铺
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 商品分类
	 */
	public List<ProductCategory> findList(Store store, Integer count, List<Filter> filters, List<Order> orders) {
		String sql = "SELECT pc.* FROM `product_category` pc LEFT JOIN product_category_store pcs ON pc.`id` = pcs.`product_categories_id` WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		
		if (store != null) {
			sql += " AND pcs.`stores_id` = ?";
			params.add(store.getId());
		}
		return super.findList(sql, params);
	}

	/**
	 * 查找顶级商品分类
	 * 
	 * @param count
	 *            数量
	 * @return 顶级商品分类
	 */
	public List<ProductCategory> findRoots(Integer count) {
		String sql = "SELECT * FROM `product_category` WHERE parent_id IS NULL ORDER BY orders ASC ";
		List<Object> params = new ArrayList<Object>();
		
		if (count != null) {
			sql += " LIMIT 0 , ?";
			params.add(count);
		}
		return modelManager.find(sql, params.toArray());
	}

	/**
	 * 查找上级商品分类
	 * 
	 * @param productCategory
	 *            商品分类
	 * @param recursive
	 *            是否递归
	 * @param count
	 *            数量
	 * @return 上级商品分类
	 */
	public List<ProductCategory> findParents(ProductCategory productCategory, boolean recursive, Integer count) {
		if (productCategory == null || productCategory.getParent() == null) {
			return Collections.emptyList();
		}
		String sql = "";
		List<Object> params = new ArrayList<Object>();
		
		if (recursive) {
			sql = "SELECT * FROM `product_category` WHERE id IN " + SqlUtils.getSQLIn(Arrays.asList(productCategory.getParentIds())) + " ORDER BY grade ASC ";
		} else {
			sql = "SELECT * FROM `product_category` WHERE id = ?";
			params.add(productCategory.getParent());
		}
		if (count != null) {
			sql += " LIMIT 0 , ?";
			params.add(count);
		}
		return modelManager.find(sql, params.toArray());
	}

	/**
	 * 查找下级商品分类
	 * 
	 * @param productCategory
	 *            商品分类
	 * @param recursive
	 *            是否递归
	 * @param count
	 *            数量
	 * @return 下级商品分类
	 */
	public List<ProductCategory> findChildren(ProductCategory productCategory, boolean recursive, Integer count) {
		String sql = "";
		List<Object> params = new ArrayList<Object>();
		if (recursive) {
			if (productCategory != null) {
				sql = "SELECT * FROM `product_category` WHERE tree_path LIKE ? ORDER BY grade ASC, orders ASC ";
				params.add("%" + ProductCategory.TREE_PATH_SEPARATOR + productCategory.getId() + ProductCategory.TREE_PATH_SEPARATOR + "%");
			} else {
				sql = "SELECT * FROM `product_category`  ORDER BY grade ASC, orders ASC ";
			}
			if (count != null) {
				sql += " LIMIT 0 , ?";
				params.add(count);
			}
			List<ProductCategory> result = modelManager.find(sql, params.toArray());
			sort(result);
			return result;
		} else {
			sql = "SELECT * FROM `product_category` WHERE parent_id = ? ORDER BY orders ASC ";
			params.add(productCategory.getId());
			if (count != null) {
				sql += " LIMIT 0, ?";
				params.add(count);
			}
			return modelManager.find(sql, params.toArray());
		}
	}

	/**
	 * 查找下级商品分类
	 * 
	 * @param productCategory
	 *            商品分类
	 * @param recursive
	 *            是否递归
	 * @param count
	 *            数量
	 * @return 下级商品分类
	 */
	public List<ProductCategory> findChildren(ProductCategory productCategory) {
		List<Object> params = new ArrayList<Object>();
		String sql = "SELECT `id`,`name`,`image` FROM `product_category` WHERE parent_id = ? AND is_marketable = TRUE ORDER BY orders ASC ";
		params.add(productCategory.getId());
		return modelManager.find(sql, params.toArray());
	}
	
	/**
	 * 查找商品分类
	 * @param isMarketable 是否上架
	 * @param isTop 是否置顶
	 * @return 分类列表
	 */
	public List<ProductCategory> findRoots(Boolean isMarketable, Boolean isTop) {
		List<Object> params = new ArrayList<Object>();
		String sql = "SELECT `id`,`name`,`image` FROM product_category WHERE 1 = 1 ";
		if (isMarketable != null) {
			sql += " AND `is_marketable` = ?";
			params.add(isMarketable);
		}
		if (isTop != null) {
			sql += " AND `is_top` = ?";
			params.add(isTop);
		}
		sql += " ORDER BY orders ASC ";
		return modelManager.find(sql, params.toArray());
	}
	
	/**
	 * 排序商品分类
	 * 
	 * @param productCategories
	 *            商品分类
	 */
	private void sort(List<ProductCategory> productCategories) {
		if (CollectionUtils.isEmpty(productCategories)) {
			return;
		}
		final Map<Long, Integer> orderMap = new HashMap<>();
		for (ProductCategory productCategory : productCategories) {
			orderMap.put(productCategory.getId(), productCategory.getOrders());
		}
		Collections.sort(productCategories, new Comparator<ProductCategory>() {
			@Override
			public int compare(ProductCategory productCategory1, ProductCategory productCategory2) {
				Long[] ids1 = (Long[]) ArrayUtils.add(productCategory1.getParentIds(), productCategory1.getId());
				Long[] ids2 = (Long[]) ArrayUtils.add(productCategory2.getParentIds(), productCategory2.getId());
				Iterator<Long> iterator1 = Arrays.asList(ids1).iterator();
				Iterator<Long> iterator2 = Arrays.asList(ids2).iterator();
				CompareToBuilder compareToBuilder = new CompareToBuilder();
				while (iterator1.hasNext() && iterator2.hasNext()) {
					Long id1 = iterator1.next();
					Long id2 = iterator2.next();
					Integer order1 = orderMap.get(id1);
					Integer order2 = orderMap.get(id2);
					compareToBuilder.append(order1, order2).append(id1, id2);
					if (!iterator1.hasNext() || !iterator2.hasNext()) {
						compareToBuilder.append(productCategory1.getGrade(), productCategory2.getGrade());
					}
				}
				return compareToBuilder.toComparison();
			}
		});
	}
	
}