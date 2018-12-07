package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BaseProductTag;

/**
 * Model - 商品标签
 * 
 */
public class ProductTag extends BaseProductTag<ProductTag> {
	private static final long serialVersionUID = 5855529951342450225L;
	public static final ProductTag dao = new ProductTag().dao();
	
	
	/**
	 * 商品
	 */
	private List<Product> products = new ArrayList<>();
	
	/**
	 * 获取商品
	 * 
	 * @return 商品
	 */
	public List<Product> getProducts() {
		if (CollectionUtils.isEmpty(products)) {
			String sql = "SELECT p.*  FROM product p LEFT JOIN product_product_tag ppt ON p.id = ppt.products_id WHERE ppt.product_tags_id = ?";
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
				product.getProductTags().remove(this);
			}
		}
	}
}
