package com.jfinalshop.service;

import java.util.List;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.dao.StoreRankDao;
import com.jfinalshop.model.StoreRank;

/**
 * Service - 店铺等级
 * 
 */
@Singleton
public class StoreRankService extends BaseService<StoreRank> {

	/**
	 * 构造方法
	 */
	public StoreRankService() {
		super(StoreRank.class);
	}
	
	@Inject
	private StoreRankDao storeRankDao;
	
	/**
	 * 判断名称是否存在
	 * 
	 * @param name
	 *            名称
	 * @return 名称是否存在
	 */
	public boolean nameExists(String name) {
		return storeRankDao.exists("name", name);
	}

	/**
	 * 判断名称是否唯一
	 * 
	 * @param id
	 *            ID
	 * @param name
	 *            名称
	 * @return 名称是否唯一
	 */
	public boolean nameUnique(Long id, String name) {
		return storeRankDao.unique(id, "name", name);
	}

	/**
	 * 查找店铺等级
	 * 
	 * @param isAllowRegister
	 *            是否允许注册
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 店铺等级
	 */
	public List<StoreRank> findList(Boolean isAllowRegister, List<Filter> filters, List<Order> orders) {
		return storeRankDao.findList(isAllowRegister, filters, orders);
	}
}