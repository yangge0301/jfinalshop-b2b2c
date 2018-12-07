package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.List;

import com.jfinalshop.model.FriendLink;

/**
 * Dao - 友情链接
 * 
 */
public class FriendLinkDao extends BaseDao<FriendLink> {

	/**
	 * 构造方法
	 */
	public FriendLinkDao() {
		super(FriendLink.class);
	}
	
	/**
	 * 查找友情链接
	 * 
	 * @param type
	 *            类型
	 * @return 友情链接
	 */
	public List<FriendLink> findList(FriendLink.Type type) {
		String sql = "SELECT * FROM `friend_link` WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		
		if (type != null) {
			sql += " AND type = ?";
			params.add(type.ordinal());
		}
		
		return modelManager.find(sql, params.toArray());
	}

}