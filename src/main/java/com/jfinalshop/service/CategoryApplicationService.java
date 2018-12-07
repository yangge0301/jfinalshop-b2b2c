package com.jfinalshop.service;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.dao.CategoryApplicationDao;
import com.jfinalshop.dao.ProductDao;
import com.jfinalshop.model.CategoryApplication;
import com.jfinalshop.model.ProductCategory;
import com.jfinalshop.model.ProductCategoryStore;
import com.jfinalshop.model.Store;
import com.jfinalshop.util.Assert;

/**
 * Service - 经营分类申请
 * 
 */
@Singleton
public class CategoryApplicationService extends BaseService<CategoryApplication> {

	/**
	 * 构造方法
	 */
	public CategoryApplicationService() {
		super(CategoryApplication.class);
	}
	
	@Inject
	private CategoryApplicationDao categoryApplicationDao;
	@Inject
	private ProductDao productDao;
	
	/**
	 * 判断经营分类申请是否存在
	 * 
	 * @param store
	 *            店铺
	 * @param productCategory
	 *            经营分类
	 * @param status
	 *            状态
	 * @return 经营分类申请是否存在
	 */
	public boolean exist(Store store, ProductCategory productCategory, CategoryApplication.Status status) {
		Assert.notNull(status);
		Assert.notNull(store);
		Assert.notNull(productCategory);

		return categoryApplicationDao.findList(store, productCategory, status).size() > 0;
	}

	/**
	 * 查找经营分类申请分页
	 * 
	 * @param store
	 *            店铺
	 * @param pageable
	 *            分页
	 * @return 经营分类申请分页
	 */
	public Page<CategoryApplication> findPage(Store store, Pageable pageable) {
		return categoryApplicationDao.findPage(store, pageable);
	}

	/**
	 * 审核经营分类申请
	 * 
	 * @param categoryApplication
	 *            经营分类申请
	 * @param isPassed
	 *            是否审核通过
	 */
	public void review(CategoryApplication categoryApplication, boolean isPassed) {
		Assert.notNull(categoryApplication);

		if (isPassed) {
			Store store = categoryApplication.getStore();
			ProductCategory productCategory = categoryApplication.getProductCategory();

			categoryApplication.setStatus(CategoryApplication.Status.approved.ordinal());
//			store.getProductCategories().add(productCategory);
//			Set<ProductCategory> productCategories = new HashSet<>();
//			productCategories.add(productCategory);
			ProductCategoryStore productCategoryStore = new ProductCategoryStore();
			productCategoryStore.setStoresId(store.getId());
			productCategoryStore.setProductCategoriesId(productCategory.getId());
			productCategoryStore.save();
			
			productDao.refreshActive(store);
		} else {
			categoryApplication.setStatus(CategoryApplication.Status.failed.ordinal());
		}
		super.update(categoryApplication);
	}

}