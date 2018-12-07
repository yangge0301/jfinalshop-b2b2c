package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BaseStoreProductTag;

/**
 * Model - 店铺商品标签
 * 
 */
public class StoreProductTag extends BaseStoreProductTag<StoreProductTag> {
	private static final long serialVersionUID = 4930745735731579235L;
	public static final StoreProductTag dao = new StoreProductTag().dao();
	
	/**
	 * 店铺
	 */
	private Store store;

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
	 * 获取商品
	 * 
	 * @return 商品
	 */
	public List<Product> getProducts() {
		if (CollectionUtils.isEmpty(products)) {
			String sql ="SELECT p.*  FROM product p LEFT JOIN product_store_product_tag pspt ON p.id = pspt.`products_id` WHERE pspt.`store_product_tags_id` = ?";
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
	 * 删除前处理
	 */
	public void preRemove() {
		List<Product> products = getProducts();
		if (products != null) {
			for (Product product : products) {
				product.getStoreProductTags().remove(this);
			}
		}
	}

}
