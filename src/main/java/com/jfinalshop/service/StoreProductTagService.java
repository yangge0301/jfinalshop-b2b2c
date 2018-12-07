package com.jfinalshop.service;

import java.util.List;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.dao.StoreProductTagDao;
import com.jfinalshop.model.Store;
import com.jfinalshop.model.StoreProductTag;

/**
 * Service - 店铺商品标签
 * 
 */
@Singleton
public class StoreProductTagService extends BaseService<StoreProductTag> {

	/**
	 * 构造方法
	 */
	public StoreProductTagService() {
		super(StoreProductTag.class);
	}
	
	@Inject
	private StoreProductTagDao storeProductTagDao;
	
	/**
	 * 查找店铺商品标签
	 * 
	 * @param store
	 *            店铺
	 * @param isEnabled
	 *            是否启用
	 * @return 店铺商品标签
	 */
	public List<StoreProductTag> findList(Store store, Boolean isEnabled) {
		return storeProductTagDao.findList(store, isEnabled);
	}

	/**
	 * 查找店铺商品标签分页
	 * 
	 * @param store
	 *            店铺
	 * @param pageable
	 *            分页
	 * @return 店铺商品标签分页
	 */
	public Page<StoreProductTag> findPage(Store store, Pageable pageable) {
		return storeProductTagDao.findPage(store, pageable);
	}

}