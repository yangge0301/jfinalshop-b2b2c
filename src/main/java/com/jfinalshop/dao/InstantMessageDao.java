package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.InstantMessage;
import com.jfinalshop.model.Store;

/**
 * Dao - 即时通讯
 * 
 */
public class InstantMessageDao extends BaseDao<InstantMessage> {

	/**
	 * 构造方法
	 */
	public InstantMessageDao() {
		super(InstantMessage.class);
	}
	
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
		String sqlExceptSelect = "FROM `instant_message` WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		
		if (store != null) {
			sqlExceptSelect += " AND store_id = ?";
			params.add(store.getId());
		}
		return super.findPage(sqlExceptSelect, pageable, params);
	}

}