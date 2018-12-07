package com.jfinalshop.service;

import java.util.List;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.dao.NavigationDao;
import com.jfinalshop.model.Navigation;

/**
 * Service - 导航
 * 
 */
@Singleton
public class NavigationService extends BaseService<Navigation> {

	/**
	 * 构造方法
	 */
	public NavigationService() {
		super(Navigation.class);
	}
	
	@Inject
	private NavigationDao navigationDao;
	
	/**
	 * 查找导航
	 * 
	 * @param position
	 *            位置
	 * @return 导航
	 */
	public List<Navigation> findList(Navigation.Position position) {
		return navigationDao.findList(position);
	}

	/**
	 * 查找导航
	 * 
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @param useCache
	 *            是否使用缓存
	 * @return 导航
	 */
	public List<Navigation> findList(Integer count, List<Filter> filters, List<Order> orders, boolean useCache) {
		return navigationDao.findList(null, count, filters, orders);
	}

	@Override
	public Navigation save(Navigation navigation) {
		return super.save(navigation);
	}
	
	@Override
	public Navigation update(Navigation navigation) {
		return super.update(navigation);
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
	public void delete(Navigation navigation) {
		super.delete(navigation);
	}
	
	
}