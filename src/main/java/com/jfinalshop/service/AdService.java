package com.jfinalshop.service;

import net.hasor.core.Singleton;

import com.jfinalshop.model.Ad;

/**
 * Service - 广告
 * 
 */
@Singleton
public class AdService extends BaseService<Ad> {

	/**
	 * 构造方法
	 */
	public AdService() {
		super(Ad.class);
	}
	
	@Override
	public Ad save(Ad ad) {
		return super.save(ad);
	}
	
	@Override
	public Ad update(Ad ad) {
		return super.update(ad);
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
	public void delete(Ad ad) {
		super.delete(ad);
	}
}