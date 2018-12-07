package com.jfinalshop.service;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.dao.StoreAdImageDao;
import com.jfinalshop.model.Store;
import com.jfinalshop.model.StoreAdImage;

/**
 * Service - 店铺广告图片
 * 
 */
@Singleton
public class StoreAdImageService extends BaseService<StoreAdImage> {

	/**
	 * 构造方法
	 */
	public StoreAdImageService() {
		super(StoreAdImage.class);
	}
	
	@Inject
	private StoreAdImageDao storeAdImageDao;
	
	/**
	 * 查找店铺广告图片分页
	 * 
	 * @param store
	 *            店铺
	 * @param pageable
	 *            分页信息
	 * @return 店铺广告图片分页
	 */
	public Page<StoreAdImage> findPage(Store store, Pageable pageable) {
		return storeAdImageDao.findPage(store, pageable);
	}

}