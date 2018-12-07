package com.jfinalshop.service;

import java.util.List;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.Pageable;
import com.jfinalshop.dao.StoreFavoriteDao;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Store;
import com.jfinalshop.model.StoreFavorite;

/**
 * Service - 店铺收藏
 * 
 */
@Singleton
public class StoreFavoriteService extends BaseService<StoreFavorite> {

	/**
	 * 构造方法
	 */
	public StoreFavoriteService() {
		super(StoreFavorite.class);
	}
	
	@Inject
	private StoreFavoriteDao storeFavoriteDao;
	
	/**
	 * 判断店铺收藏是否存在
	 * 
	 * @param member
	 *            会员
	 * @param store
	 *            店铺
	 * @return 店铺收藏是否存在
	 */
	public boolean exists(Member member, Store store) {
		return storeFavoriteDao.exists(member, store);
	}

	/**
	 * 查找店铺收藏
	 * 
	 * @param member
	 *            会员
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 店铺收藏
	 */
	public List<StoreFavorite> findList(Member member, Integer count, List<Filter> filters, List<Order> orders) {
		return storeFavoriteDao.findList(member, count, filters, orders);
	}

	/**
	 * 查找店铺收藏
	 * 
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @param useCache
	 *            是否使用缓存
	 * @return 店铺收藏
	 */
	public List<StoreFavorite> findList(Integer count, List<Filter> filters, List<Order> orders, boolean useCache) {
		return storeFavoriteDao.findList((Integer) null, count, filters, orders);
	}
	

	/**
	 * 查找店铺收藏分页
	 * 
	 * @param member
	 *            会员
	 * @param pageable
	 *            分页信息
	 * @return 店铺收藏分页
	 */
	public Page<StoreFavorite> findPage(Member member, Pageable pageable) {
		return storeFavoriteDao.findPage(member, pageable);
	}

	/**
	 * 查找店铺收藏数量
	 * 
	 * @param member
	 *            会员
	 * @return 店铺收藏数量
	 */
	public Long count(Member member) {
		return storeFavoriteDao.count(member);
	}

	@Override
	public StoreFavorite save(StoreFavorite storeFavorite) {
		return super.save(storeFavorite);
	}
	
	@Override
	public StoreFavorite update(StoreFavorite storeFavorite) {
		return super.update(storeFavorite);
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
	public void delete(StoreFavorite storeFavorite) {
		super.delete(storeFavorite);
	}

}