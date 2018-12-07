package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BaseAdmin;

/**
 * Model - 管理员
 * 
 */
public class Admin extends BaseAdmin<Admin> {
	private static final long serialVersionUID = 2587495102702379706L;
	public static final Admin dao = new Admin().dao();
	
	/**
	 * "登录失败尝试次数"缓存名称
	 */
	public static final String FAILED_LOGIN_ATTEMPTS_CACHE_NAME = "failedLoginAttempts";
	
	/**
	 * 角色
	 */
	private List<Role> roles = new ArrayList<Role>();
	

	/**
	 * 获取角色
	 * 
	 * @return 角色
	 */
	public List<Role> getRoles() {
		if (CollectionUtils.isEmpty(roles)) {
			String sql = "SELECT r.* FROM role r LEFT JOIN admin_role ar ON r.id = ar.roles_id WHERE ar.admins_id = ?";
			roles = Role.dao.find(sql, getId());
		}
		return roles;
	}

	/**
	 * 设置角色
	 * 
	 * @param roles
	 *            角色
	 */
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	
	public String getDisplayName() {
		return getUsername();
	}

	public Object getPrincipal() {
		return getUsername();
	}

	public Object getCredentials() {
		return getPassword();
	}

	/**
	 * 判断是否为新建对象
	 * 
	 * @return 是否为新建对象
	 */
	public boolean isNew() {
		return getId() == null;
	}
}
