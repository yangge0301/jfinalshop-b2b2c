package com.jfinalshop.model;

import com.jfinalshop.model.base.BaseProductCategoryStore;

/**
 * Model - 店铺商品分类中间表
 * 
 */
public class ProductCategoryStore extends BaseProductCategoryStore<ProductCategoryStore> {
	private static final long serialVersionUID = 6998687364789547476L;
	public static final ProductCategoryStore dao = new ProductCategoryStore().dao();
	
	
}
