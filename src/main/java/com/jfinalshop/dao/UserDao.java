package com.jfinalshop.dao;

import com.jfinalshop.model.User;

/**
 * Dao - 用户
 * 
 */
public class UserDao extends BaseDao<User> {

	/**
	 * 构造方法
	 */
	public UserDao() {
		super(User.class);
	}
	
}