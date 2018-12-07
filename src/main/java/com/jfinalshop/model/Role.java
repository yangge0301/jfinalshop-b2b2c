package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BaseRole;

/**
 * Model - 角色
 * 
 */
public class Role extends BaseRole<Role> {
	private static final long serialVersionUID = -8991427892027436090L;
	public static final Role dao = new Role().dao();
	
	/** 权限 */
	private List<Permission> permissions = new ArrayList<Permission>();

	/** 管理员 */
	private List<Admin> admins = new ArrayList<Admin>();
	
	/**
	 * 获取所有
	 * @return
	 */
	public List<Role> getAll() {
		String sql = "SELECT * FROM role";
		return find(sql);
	}
	
	/**
	 * 获取权限
	 * 
	 * @return 权限
	 */
	public List<Permission> getPermissions() {
		if (CollectionUtils.isEmpty(permissions)) {
			String sql = "SELECT p.* FROM permission_role pr INNER JOIN permission p ON pr.`permissions_id` = p.`id` WHERE pr.`roles_id` = ?";
			permissions = Permission.dao.find(sql, getId());
		}
		return permissions;
	}

	/**
	 * 设置权限
	 * 
	 * @param authorities
	 *            权限
	 */
	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}

	/**
	 * 获取管理员
	 * 
	 * @return 管理员
	 */
	public List<Admin> getAdmins() {
		if (CollectionUtils.isEmpty(admins)) {
			String sql = "SELECT a.* FROM `admin_role` ar LEFT JOIN `admin` a ON ar.`admins_id` = a.`id` WHERE ar.`roles_id` = ?";
			admins = Admin.dao.find(sql, getId());
		}
		return admins;
	}

	/**
	 * 设置管理员
	 * 
	 * @param admins
	 *            管理员
	 */
	public void setAdmins(List<Admin> admins) {
		this.admins = admins;
	}
}
