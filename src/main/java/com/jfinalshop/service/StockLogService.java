package com.jfinalshop.service;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.dao.StockLogDao;
import com.jfinalshop.model.StockLog;
import com.jfinalshop.model.Store;

/**
 * Service - 库存记录
 * 
 */
@Singleton
public class StockLogService extends BaseService<StockLog> {

	/**
	 * 构造方法
	 */
	public StockLogService() {
		super(StockLog.class);
	}
	
	@Inject
	private StockLogDao stockLogDao;
	
	/**
	 * 查找库存记录分页
	 * 
	 * @param store
	 *            店铺
	 * @param pageable
	 *            分页
	 * @return 库存记录分页
	 */
	public Page<StockLog> findPage(Store store, Pageable pageable) {
		return stockLogDao.findPage(store, pageable);
	}

}