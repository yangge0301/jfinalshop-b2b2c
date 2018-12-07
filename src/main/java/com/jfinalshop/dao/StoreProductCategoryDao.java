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

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Store;
import com.jfinalshop.model.StoreProductCategory;
import com.jfinalshop.util.SqlUtils;

/**
 * Dao - 店铺商品分类
 * 
 */
public class StoreProductCategoryDao extends BaseDao<StoreProductCategory> {
	
	/**
	 * 构造方法
	 */
	public StoreProductCategoryDao() {
		super(StoreProductCategory.class);
	}

	/**
	 * 查找顶级店铺商品分类
	 * 
	 * @param store
	 *            店铺
	 * @param count
	 *            数量
	 * @return 顶级店铺商品分类
	 */
	public List<StoreProductCategory> findRoots(Store store, Integer count) {
		String sql = "SELECT * FROM store_product_category WHERE store_id = ? AND parent_id IS NULL ORDER BY orders ASC";
		List<Object> params = new ArrayList<Object>();
		
		params.add(store.getId());
		if (count != null) {
			sql += " LIMIT 0 , ?";
			params.add(count);
		}
		return modelManager.find(sql, params.toArray());
	}

	/**
	 * 查找上级店铺商品分类
	 * 
	 * @param storeProductCategory
	 *            店铺商品分类
	 * @param recursive
	 *            是否递归
	 * @param count
	 *            数量
	 * @return 上级店铺商品分类
	 */
	public List<StoreProductCategory> findParents(StoreProductCategory storeProductCategory, boolean recursive, Integer count) {
		if (storeProductCategory == null || storeProductCategory.getParent() == null) {
			return Collections.emptyList();
		}
		
		String sql = "";
		List<Object> params = new ArrayList<Object>();
		if (recursive) {
			sql = "SELECT * FROM store_product_category WHERE id IN ? ORDER BY grade ASC";
			params.add(SqlUtils.getSQLIn(Arrays.asList(storeProductCategory.getParentIds())));
		} else {
			sql = "SELECT * FROM store_product_category WHERE id = ?";
			params.add(storeProductCategory.getParentId());
		}
		if (count != null) {
			sql += " LIMIT 0, ?";
			params.add(count);
		}
		return modelManager.find(sql, params.toArray());
	}

	/**
	 * 查找下级店铺商品分类
	 * 
	 * @param storeProductCategory
	 *            店铺商品分类
	 * @param store
	 *            店铺
	 * @param recursive
	 *            是否递归
	 * @param count
	 *            数量
	 * @return 下级店铺商品分类
	 */
	public List<StoreProductCategory> findChildren(StoreProductCategory storeProductCategory, Store store, boolean recursive, Integer count) {
		String sql = "";
		List<Object> params = new ArrayList<Object>();
		
		if (recursive) {
			if (storeProductCategory != null) {
				sql = "SELECT * FROM store_product_category WHERE store_id = ? AND tree_path LIKE ? ORDER BY grade ASC";
				params.add(store.getId());
				params.add("%" + StoreProductCategory.TREE_PATH_SEPARATOR + storeProductCategory.getId() + StoreProductCategory.TREE_PATH_SEPARATOR + "%");
			} else {
				sql = "SELECT * FROM store_product_category WHERE store_id = ? ORDER BY grade ASC";
				params.add(store.getId());
			}
			if (count != null) {
				sql += " LIMIT 0, ?";
				params.add(count);
			}
			List<StoreProductCategory> result = modelManager.find(sql, params.toArray());
			sort(result);
			return result;
		} else {
			sql = "SELECT * FROM store_product_category WHERE parent_id = ? AND store_id = ? ORDER BY orders ASC";
			params.add(storeProductCategory.getId());
			params.add(store.getId());
			
			if (count != null) {
				sql += " LIMIT 0, ?";
				params.add(count);
			}
			return modelManager.find(sql, params.toArray());
		}
	}

	/**
	 * 查找店铺商品分类
	 * 
	 * @param store
	 *            店铺
	 * @param pageable
	 *            分页
	 * @return 店铺商品分类
	 */
	public Page<StoreProductCategory> findPage(Store store, Pageable pageable) {
		String sqlExceptSelect = "FROM store_product_category WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		
		if (store != null) {
			sqlExceptSelect += " AND store_id = ?";
			params.add(store.getId());
		}
		return super.findPage(sqlExceptSelect, pageable, params);
	}
	
	/**
	 * 排序店铺商品分类
	 * 
	 * @param storeProductCategorys
	 *            店铺商品分类
	 */
	private void sort(List<StoreProductCategory> storeProductCategorys) {
		if (CollectionUtils.isEmpty(storeProductCategorys)) {
			return;
		}
		final Map<Long, Integer> orderMap = new HashMap<>();
		for (StoreProductCategory shopProductCategory : storeProductCategorys) {
			orderMap.put(shopProductCategory.getId(), shopProductCategory.getOrders());
		}
		Collections.sort(storeProductCategorys, new Comparator<StoreProductCategory>() {
			@Override
			public int compare(StoreProductCategory storeProductCategory1, StoreProductCategory storeProductCategory2) {
				Long[] ids1 = (Long[]) ArrayUtils.add(storeProductCategory1.getParentIds(), storeProductCategory1.getId());
				Long[] ids2 = (Long[]) ArrayUtils.add(storeProductCategory2.getParentIds(), storeProductCategory2.getId());
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
						compareToBuilder.append(storeProductCategory1.getGrade(), storeProductCategory2.getGrade());
					}
				}
				return compareToBuilder.toComparison();
			}
		});
	}
}