package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BaseBrand;

/**
 * Model - 品牌
 * 
 */
public class Brand extends BaseBrand<Brand> {
	private static final long serialVersionUID = 4587688878405294634L;
	public static final Brand dao = new Brand().dao();
	
	/**
	 * 路径
	 */
	private static final String PATH = "/brand/detail/%d";

	/**
	 * 类型
	 */
	public enum Type {

		/**
		 * 文本
		 */
		text,

		/**
		 * 图片
		 */
		image
	}
	
	/**
	 * 商品
	 */
	private List<Product> products = new ArrayList<Product>();

	/**
	 * 商品分类
	 */
	private List<ProductCategory> productCategories = new ArrayList<ProductCategory>();

	
	/**
	 * 设置类型
	 * 
	 * @param type
	 *            类型
	 */
	public void setType(Brand.Type type) {
		setType(type.ordinal());
	}
	
	/**
	 * 获取类型
	 * 
	 * @return 类型
	 */
	public Brand.Type getTypeName() {
		return getType() == null ? null : Type.values()[getType()];
	}
	
	/**
	 * 获取商品
	 * 
	 * @return 商品
	 */
	public List<Product> getProducts() {
		if (CollectionUtils.isEmpty(products)) {
			String sql = "SELECT * FROM `product` WHERE `brand_id` = ? ";
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
	 * 获取商品分类
	 * 
	 * @return 商品分类
	 */
	public List<ProductCategory> getProductCategories() {
		if (CollectionUtils.isEmpty(productCategories)) {
			String sql ="SELECT pc.* FROM `product_category_brand` pcb LEFT JOIN `product_category` pc ON pcb.`product_categories_id` = pc.`id` WHERE pcb.`brands_id` = ?";
			productCategories = ProductCategory.dao.find(sql, getId());
		}
		return productCategories;
	}

	/**
	 * 设置商品分类
	 * 
	 * @param productCategories
	 *            商品分类
	 */
	public void setProductCategories(List<ProductCategory> productCategories) {
		this.productCategories = productCategories;
	}

	/**
	 * 获取路径
	 * 
	 * @return 路径
	 */
	public String getPath() {
		return String.format(Brand.PATH, getId());
	}

	/**
	 * 删除前处理
	 */
	public void preRemove() {
		List<Product> products = getProducts();
		if (products != null) {
			for (Product product : products) {
				product.setBrand(null);
			}
		}
		List<ProductCategory> productCategories = getProductCategories();
		if (productCategories != null) {
			for (ProductCategory productCategory : productCategories) {
				productCategory.getBrands().remove(this);
			}
		}
	}
	
	
}
