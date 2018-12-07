package com.jfinalshop.service;

import java.util.List;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.Pageable;
import com.jfinalshop.dao.ProductFavoriteDao;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.ProductFavorite;

/**
 * Service - 商品收藏
 * 
 */
@Singleton
public class ProductFavoriteService extends BaseService<ProductFavorite> {

	/**
	 * 构造方法
	 */
	public ProductFavoriteService() {
		super(ProductFavorite.class);
	}
	
	@Inject
	private ProductFavoriteDao productFavoriteDao;
	
	/**
	 * 判断商品收藏是否存在
	 * 
	 * @param member
	 *            会员
	 * @param product
	 *            商品
	 * @return 商品收藏是否存在
	 */
	public boolean exists(Member member, Product product) {
		return productFavoriteDao.exists(member, product);
	}

	/**
	 * 查找商品收藏
	 * 
	 * @param member
	 *            会员
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 商品收藏
	 */
	public List<ProductFavorite> findList(Member member, Integer count, List<Filter> filters, List<Order> orders) {
		return productFavoriteDao.findList(member, count, filters, orders);
	}

	/**
	 * 查找商品收藏
	 * 
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @param useCache
	 *            是否使用缓存
	 * @return 商品收藏
	 */
	public List<ProductFavorite> findList(Integer count, List<Filter> filters, List<Order> orders, boolean useCache) {
		return productFavoriteDao.findList((Integer) null, count, filters, orders);
	}
	

	/**
	 * 查找商品收藏分页
	 * 
	 * @param member
	 *            会员
	 * @param pageable
	 *            分页信息
	 * @return 商品收藏分页
	 */
	public Page<ProductFavorite> findPage(Member member, Pageable pageable) {
		return productFavoriteDao.findPage(member, pageable);
	}

	/**
	 * 查找商品收藏数量
	 * 
	 * @param member
	 *            会员
	 * @return 商品收藏数量
	 */
	public Long count(Member member) {
		return productFavoriteDao.count(member);
	}

	@Override
	public ProductFavorite save(ProductFavorite productFavorite) {
		return super.save(productFavorite);
	}
	
	@Override
	public ProductFavorite update(ProductFavorite productFavorite) {
		return super.update(productFavorite);
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
	public void delete(ProductFavorite productFavorite) {
		super.delete(productFavorite);
	}
	
}