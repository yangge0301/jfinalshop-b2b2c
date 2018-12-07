package com.jfinalshop.model;

import com.jfinalshop.model.base.BaseSocialUser;

/**
 * Model - 社会化用户
 * 
 */
public class SocialUser extends BaseSocialUser<SocialUser> {
	private static final long serialVersionUID = 7340894158897108786L;
	public static final SocialUser dao = new SocialUser().dao();
	
	/**
	 * 用户
	 */
	private User user;
	
	/**
	 * 获取用户
	 * 
	 * @return 用户
	 */
	public User getUser() {
		return user;
	}

	/**
	 * 设置用户
	 * 
	 * @param user
	 *            用户
	 */
	public void setUser(User user) {
		this.user = user;
	}
}
