package com.jfinalshop.service;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.dao.BusinessDepositLogDao;
import com.jfinalshop.model.Business;
import com.jfinalshop.model.BusinessDepositLog;

/**
 * Service - 商家预存款记录
 * 
 */
@Singleton
public class BusinessDepositLogService extends BaseService<BusinessDepositLog> {

	/**
	 * 构造方法
	 */
	public BusinessDepositLogService() {
		super(BusinessDepositLog.class);
	}
	
	@Inject
	private BusinessDepositLogDao businessDepositLogDao;
	
	/**
	 * 查找商家预存款记录分页
	 * 
	 * @param business
	 *            商家
	 * @param pageable
	 *            分页信息
	 * @return 商家预存款记录分页
	 */
	public Page<BusinessDepositLog> findPage(Business business, Pageable pageable) {
		return businessDepositLogDao.findPage(business, pageable);
	}

}