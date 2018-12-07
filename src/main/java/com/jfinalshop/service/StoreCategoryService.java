package com.jfinalshop.service;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinalshop.dao.StoreCategoryDao;
import com.jfinalshop.model.StoreCategory;

/**
 * Service - 店铺分类
 * 
 */
@Singleton
public class StoreCategoryService extends BaseService<StoreCategory> {

	/**
	 * 构造方法
	 */
	public StoreCategoryService() {
		super(StoreCategory.class);
	}
	
	@Inject
	private StoreCategoryDao storeCategoryDao;
	
	/**
	 * 判断名称是否存在
	 * 
	 * @param name
	 *            名称(忽略大小写)
	 * @return 名称是否存在
	 */
	public boolean nameExists(String name) {
		return storeCategoryDao.exists("name", name, true);
	}

}