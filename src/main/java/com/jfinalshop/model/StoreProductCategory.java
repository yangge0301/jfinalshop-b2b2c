package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinalshop.model.base.BaseStoreProductCategory;

/**
 * Model - 店铺商品分类
 * 
 */
public class StoreProductCategory extends BaseStoreProductCategory<StoreProductCategory> {
	private static final long serialVersionUID = 654265136251989335L;
	public static final StoreProductCategory dao = new StoreProductCategory().dao();
	
	/**
	 * 树路径分隔符
	 */
	public static final String TREE_PATH_SEPARATOR = ",";

	/**
	 * 路径
	 */
	private static final String PATH = "/product/list?storeProductCategoryId=%d";
	
	
	/**
	 * 店铺
	 */
	private Store store;

	/**
	 * 上级分类
	 */
	private StoreProductCategory parent;

	/**
	 * 下级分类
	 */
	private List<StoreProductCategory> children = new ArrayList<>();

	/**
	 * 商品
	 */
	private List<Product> products = new ArrayList<>();
	
	/**
	 * 获取店铺
	 * 
	 * @return 店铺
	 */
	public Store getStore() {
		if (store == null) {
			store = Store.dao.findById(getStoreId());
		}
		return store;
	}

	/**
	 * 设置店铺
	 * 
	 * @param store
	 *            店铺
	 */
	public void setStore(Store store) {
		this.store = store;
	}

	/**
	 * 获取上级分类
	 * 
	 * @return 上级分类
	 */
	public StoreProductCategory getParent() {
		if (parent == null) {
			parent = StoreProductCategory.dao.findById(getParentId());
		}
		return parent;
	}

	/**
	 * 设置上级分类
	 * 
	 * @param parent
	 *            上级分类
	 */
	public void setParent(StoreProductCategory parent) {
		this.parent = parent;
	}

	/**
	 * 获取下级分类
	 * 
	 * @return 下级分类
	 */
	public List<StoreProductCategory> getChildren() {
		if (CollectionUtils.isEmpty(children)) {
			String sql = "SELECT * FROM `store_product_category` WHERE parent_id = ?";
			children = StoreProductCategory.dao.find(sql, getId());
		}
		return children;
	}

	/**
	 * 设置下级分类
	 * 
	 * @param children
	 *            下级分类
	 */
	public void setChildren(List<StoreProductCategory> children) {
		this.children = children;
	}

	/**
	 * 获取商品
	 * 
	 * @return 商品
	 */
	public List<Product> getProducts() {
		if (CollectionUtils.isEmpty(products)) {
			String sql = "SELECT * FROM `product` WHERE store_product_category_id = ?";
			products = Product.dao.find(sql, getId());
		}
		return products;
	}

	/**
	 * 设置商品
	 * 
	 * @param products
	 *            商品
	 */
	public void setProducts(List<Product> products) {
		this.products = products;
	}

	/**
	 * 获取路径
	 * 
	 * @return 路径
	 */
	public String getPath() {
		return String.format(StoreProductCategory.PATH, getId());
	}

	/**
	 * 获取所有上级分类ID
	 * 
	 * @return 所有上级分类ID
	 */
	public Long[] getParentIds() {
		String[] parentIds = StringUtils.split(getTreePath(), TREE_PATH_SEPARATOR);
		Long[] result = new Long[parentIds.length];
		for (int i = 0; i < parentIds.length; i++) {
			result[i] = Long.valueOf(parentIds[i]);
		}
		return result;
	}

	/**
	 * 获取所有上级分类
	 * 
	 * @return 所有上级分类
	 */
	public List<StoreProductCategory> getParents() {
		List<StoreProductCategory> parents = new ArrayList<>();
		recursiveParents(parents, this);
		return parents;
	}

	/**
	 * 递归上级分类
	 * 
	 * @param parents
	 *            上级分类
	 * @param storeProductCategory
	 *            店铺商品分类
	 */
	private void recursiveParents(List<StoreProductCategory> parents, StoreProductCategory storeProductCategory) {
		if (storeProductCategory == null) {
			return;
		}
		StoreProductCategory parent = storeProductCategory.getParent();
		if (parent != null) {
			parents.add(0, parent);
			recursiveParents(parents, parent);
		}
	}
	
	
}
