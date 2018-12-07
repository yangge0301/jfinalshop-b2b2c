package com.jfinalshop.dao;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.Permission;
import com.jfinalshop.model.Role;

public class PermissionDao extends BaseDao<Permission> {

	/**
	 * 构造方法
	 */
	public PermissionDao() {
		super(Permission.class);
	}
	
	/**
	 * 获取模块分组
	 * 
	 * @return 模块分组
	 */
	public List<String> getModules() {
		String sql = "SELECT DISTINCT module FROM permission";
		return Db.query(sql);
	}
	
	/**
	 * 获取权限
	 * @param role
	 * @return
	 */
	public List<Permission> findByRole(Role role) {
		if (role == null) {
			return null;
		}
		String sql = "SELECT * FROM permission WHERE id IN (SELECT permissions_id FROM permission_role WHERE roles_id = ?)";
		return modelManager.find(sql, role.getId());
	}
	
}
