package com.jfinalshop.service;

import java.util.List;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.dao.FriendLinkDao;
import com.jfinalshop.model.FriendLink;

/**
 * Service - 友情链接
 * 
 */
@Singleton
public class FriendLinkService extends BaseService<FriendLink> {

	/**
	 * 构造方法
	 */
	public FriendLinkService() {
		super(FriendLink.class);
	}
	
	@Inject
	private FriendLinkDao friendLinkDao;
	
	/**
	 * 查找友情链接
	 * 
	 * @param type
	 *            类型
	 * @return 友情链接
	 */
	public List<FriendLink> findList(FriendLink.Type type) {
		return friendLinkDao.findList(type);
	}

	/**
	 * 查找友情链接
	 * 
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @param useCache
	 *            是否使用缓存
	 * @return 友情链接
	 */
	public List<FriendLink> findList(Integer count, List<Filter> filters, List<Order> orders, boolean useCache) {
		return friendLinkDao.findList(null, count, filters, orders);
	}

	@Override
	public FriendLink save(FriendLink friendLink) {
		return super.save(friendLink);
	}
	
	@Override
	public FriendLink update(FriendLink friendLink) {
		return super.update(friendLink);
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
	public void delete(FriendLink friendLink) {
		super.delete(friendLink);
	}

}