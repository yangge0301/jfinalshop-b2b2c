package com.jfinalshop.service;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinalshop.dao.AdPositionDao;
import com.jfinalshop.model.AdPosition;

/**
 * Service - 广告位
 * 
 */
@Singleton
public class AdPositionService extends BaseService<AdPosition> {
	
	/**
	 * 构造方法
	 */
	public AdPositionService() {
		super(AdPosition.class);
	}

	@Inject
	private AdPositionDao adPositionDao;
	
	/**
	 * 查找广告位
	 * 
	 * @param id
	 *            ID
	 * @param useCache
	 *            是否使用缓存
	 * @return 广告位
	 */
	public AdPosition find(Long id, boolean useCache) {
		return adPositionDao.find(id);
	}

	@Override
	public AdPosition save(AdPosition adPosition) {
		return super.save(adPosition);
	}
	
	@Override
	public AdPosition update(AdPosition adPosition) {
		return super.update(adPosition);
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
	public void delete(AdPosition adPosition) {
		super.delete(adPosition);
	}

}