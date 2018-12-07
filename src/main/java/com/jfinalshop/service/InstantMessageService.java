package com.jfinalshop.service;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.dao.InstantMessageDao;
import com.jfinalshop.model.InstantMessage;
import com.jfinalshop.model.Store;

/**
 * Service - 即时通讯
 * 
 */
@Singleton
public class InstantMessageService extends BaseService<InstantMessage> {

	/**
	 * 构造方法
	 */
	public InstantMessageService() {
		super(InstantMessage.class);
	}
	
	@Inject
	private InstantMessageDao instantMessageDao;
	
	/**
	 * 查找即时通讯分页
	 * 
	 * @param store
	 *            店铺
	 * @param pageable
	 *            分页
	 * @return 即时通讯分页
	 */
	public Page<InstantMessage> findPage(Store store, Pageable pageable) {
		return instantMessageDao.findPage(store, pageable);
	}

}