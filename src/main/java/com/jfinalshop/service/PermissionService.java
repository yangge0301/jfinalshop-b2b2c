package com.jfinalshop.service;

import java.util.List;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinalshop.dao.PermissionDao;
import com.jfinalshop.model.Permission;
import com.jfinalshop.model.Role;

/**
 * Service - 权限
 * 
 */
@Singleton
public class PermissionService extends BaseService<Permission> {
	
	@Inject
	PermissionDao permissionDao;
	
	/**
	 * 构造方法
	 */
	public PermissionService() {
		super(Permission.class);
	}

	/**
	 * 判断名称是否存在
	 * 
	 * @param name
	 *            名称(忽略大小写)
	 * @return 名称是否存在
	 */
	public boolean nameExists(String value) {
		return permissionDao.exists("value", value, true);
	}
	
	/**
	 * 判断名称是否唯一
	 * 
	 * @param id
	 *            ID
	 * @param name
	 *            名称(忽略大小写)
	 * @return 名称是否唯一
	 */
	public boolean nameUnique(Long id, String name) {
		return permissionDao.unique(id, "value", name, true);
	}
	
	/**
	 * 获取模块分组
	 * 
	 * @return 模块分组
	 */
	public List<String> getModules() {
		return permissionDao.getModules();
	}
	
	/**
	 * 根据角色查找权限
	 * @param roleId
	 * @return
	 */
	public List<Permission> findByRole(Role role) {
		return permissionDao.findByRole(role);
	}
	
}
