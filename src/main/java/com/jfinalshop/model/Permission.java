package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BasePermission;

/**
 * Model - 权限
 * 
 */
public class Permission extends BasePermission<Permission> {
	private static final long serialVersionUID = 3670043421592392565L;
	public static final Permission dao = new Permission().dao();
	
	/** 角色 */
	private List<Role> roles = new ArrayList<Role>();
	
	
	/**
	 * 获取角色
	 * 
	 * @return 角色
	 */
	public List<Role> getRoles() {
		if (CollectionUtils.isEmpty(roles)) {
			String sql = "SELECT r.* FROM permission_role pr INNER JOIN role r ON pr.`roles_id` = r.`id` WHERE pr.`permissions_id` = ?";
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
	
}
