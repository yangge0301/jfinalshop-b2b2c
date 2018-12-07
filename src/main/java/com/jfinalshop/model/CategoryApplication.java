package com.jfinalshop.model;

import com.jfinalshop.model.base.BaseCategoryApplication;

/**
 * Model - 经营分类申请
 * 
 */
public class CategoryApplication extends BaseCategoryApplication<CategoryApplication> {
	private static final long serialVersionUID = -5962515559513478064L;
	public static final CategoryApplication dao = new CategoryApplication().dao();
	
	/**
	 * 状态
	 */
	public enum Status {

		/**
		 * 等待审核
		 */
		pending,

		/**
		 * 审核通过
		 */
		approved,

		/**
		 * 审核失败
		 */
		failed
	}

	/**
	 * 店铺
	 */
	private Store store;

	/**
	 * 经营分类
	 */
	private ProductCategory productCategory;
	
	
	/**
	 * 获取类型
	 * 
	 * @return 类型
	 */
	public Status getStatusName() {
		if (getStatus() == null) {
			return null;
		}
		return Status.values()[getStatus()];
	}
	
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
	 * 获取经营分类
	 * 
	 * @return 经营分类
	 */
	public ProductCategory getProductCategory() {
		if (productCategory == null) {
			productCategory = ProductCategory.dao.findById(getProductCategoryId());
		}
		return productCategory;
	}

	/**
	 * 设置经营分类
	 * 
	 * @param productCategory
	 *            经营分类
	 */
	public void setProductCategory(ProductCategory productCategory) {
		this.productCategory = productCategory;
	}
	
	
}
