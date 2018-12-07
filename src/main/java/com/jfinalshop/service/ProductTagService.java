package com.jfinalshop.service;

import java.util.List;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.dao.ProductTagDao;
import com.jfinalshop.model.ProductTag;

/**
 * Service - 商品标签
 * 
 */
@Singleton
public class ProductTagService extends BaseService<ProductTag> {

	/**
	 * 构造方法
	 */
	public ProductTagService() {
		super(ProductTag.class);
	}
	
	@Inject
	private ProductTagDao productTagDao;
	
	/**
	 * 查找商品标签
	 * 
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @param useCache
	 *            是否使用缓存
	 * @return 商品标签
	 */
	public List<ProductTag> findList(Integer count, List<Filter> filters, List<Order> orders, boolean useCache) {
		return productTagDao.findList(null, count, filters, orders);
	}

	@Override
	public ProductTag save(ProductTag productTag) {
		return super.save(productTag);
	}
	
	@Override
	public ProductTag update(ProductTag productTag) {
		return super.update(productTag);
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
	public void delete(ProductTag productTag) {
		super.delete(productTag);
	}
	
}