package com.jfinalshop.security;

import com.jfinalshop.model.Admin;
import com.jfinalshop.shiro.core.SubjectKit;

/**
 * 工具类
 * 
 */
public class ShiroUtil {

	/**
	 * 返回当前登录的认证实体AdminId
	 * 
	 * @return
	 */
	public static Long getId() {
		Admin principal = getAdmin();
		if (principal != null) {
			return principal.getId();
		}
		return 0L;
	}

	/**
	 * 获取当前认证Admin实体的登录名称
	 * 
	 * @return
	 */
	public static String getName() {
		Admin principal = getAdmin();
		if (principal != null) {
			return principal.getName();
		}
		throw new RuntimeException("user's name is null.");
	}

	
	/**
	 * 返回当前登录的认证实体Admin
	 * 
	 * @return
	 */
	public static Admin getAdmin() {
		Admin principal = SubjectKit.getUser();
		if (principal != null) {
			return principal;
		}
		return null;
	}
	
}
