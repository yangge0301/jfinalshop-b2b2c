package com.jfinalshop.service;

import java.util.Collections;
import java.util.List;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.dao.BrandDao;
import com.jfinalshop.dao.ProductCategoryDao;
import com.jfinalshop.model.Brand;
import com.jfinalshop.model.ProductCategory;

/**
 * Service - 品牌
 * 
 */
@Singleton
public class BrandService extends BaseService<Brand> {

	/**
	 * 构造方法
	 */
	public BrandService() {
		super(Brand.class);
	}
	
	@Inject
	private BrandDao brandDao;
	@Inject
	private ProductCategoryDao productCategoryDao;
	
	/**
	 * 查找品牌
	 * 
	 * @param productCategory
	 *            商品分类
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 品牌
	 */
	public List<Brand> findList(ProductCategory productCategory, Integer count, List<Filter> filters, List<Order> orders) {
		return brandDao.findList(productCategory, count, filters, orders);
	}


	/**
	 * 查找品牌
	 * 
	 * @param productCategoryId
	 *            商品分类ID
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @param useCache
	 *            是否使用缓存
	 * @return 品牌
	 */
	public List<Brand> findList(Long productCategoryId, Integer count, List<Filter> filters, List<Order> orders, boolean useCache) {
		ProductCategory productCategory = productCategoryDao.find(productCategoryId);
		if (productCategoryId != null && productCategory == null) {
			return Collections.emptyList();
		}
		return brandDao.findList(productCategory, count, filters, orders);
	}

	@Override
	public Brand save(Brand brand) {
		return super.save(brand);
	}
	
	@Override
	public Brand update(Brand brand) {
		return super.update(brand);
	}
	
	@Override
	public void delete(Long id) {
		super.delete(id);
	}
	
	@Override
	public void delete(Long... ids) {
		super.delete(ids);
	}
	
	@Override
	public void delete(Brand brand) {
		super.delete(brand);
	}
}