package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Store;
import com.jfinalshop.model.StoreAdImage;

/**
 * Dao - 店铺广告图片
 * 
 */
public class StoreAdImageDao extends BaseDao<StoreAdImage> {

	/**
	 * 构造方法
	 */
	public StoreAdImageDao() {
		super(StoreAdImage.class);
	}
	
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
		String sqlExceptSelect = "FROM `store_ad_image` WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		
		if (store != null) {
			sqlExceptSelect += " AND store_id = ?";
			params.add(store.getId());
		}
		return super.findPage(sqlExceptSelect, pageable, params);
	}

}