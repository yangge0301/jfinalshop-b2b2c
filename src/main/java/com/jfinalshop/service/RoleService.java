package com.jfinalshop.service;

import java.util.List;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import org.apache.commons.collections.CollectionUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.dao.RoleDao;
import com.jfinalshop.model.Admin;
import com.jfinalshop.model.Business;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Permission;
import com.jfinalshop.model.PermissionRole;
import com.jfinalshop.model.Role;

/**
 * Service - 角色
 * 
 */
@Singleton
public class RoleService extends BaseService<Role> {
	
	/**
	 * 构造方法
	 */
	public RoleService() {
		super(Role.class);
	}
	
	@Inject
	RoleDao roleDao;
	
	/**
	 * 根据管理员查找角色
	 * 
	 * @param admin
	 * @return 角色，若不存在则返回null
	 */
	public List<Role> findByAdmin(Admin admin) {
		return roleDao.findByAdmin(admin);
	}
	
	/**
	 * 根据商家查找角色
	 * 
	 * @param business
	 * @return 角色，若不存在则返回null
	 */
	public List<Role> findByBusiness(Business business) {
		return roleDao.findByBusiness(business);
	}
	
	/**
	 * 根据会员查找角色
	 * 
	 * @param business
	 * @return 角色，若不存在则返回null
	 */
	public List<Role> findByMember(Member member) {
		return roleDao.findByMember(member);
	}
	
	
	@Override
	public Role save(Role role) {
		super.save(role);
		List<Permission> permissions = role.getPermissions();
		if (CollectionUtils.isNotEmpty(permissions)) {
			for (Permission permission : role.getPermissions()) {
				PermissionRole permissionRole = new PermissionRole();
				permissionRole.setPermissionsId(permission.getId());
				permissionRole.setRolesId(role.getId());
				permissionRole.save();
			}
		}
		return role;
	}
	
	@Override
	public Role update(Role role, String... ignoreProperties) {
		super.update(role, ignoreProperties);
		List<Permission> permissions = role.getPermissions();
		if (CollectionUtils.isNotEmpty(permissions)) {
			//先清除，再保存
			Db.deleteById("permission_role", "roles_id", role.getId());
			for (Permission permission : role.getPermissions()) {
				PermissionRole permissionRole = new PermissionRole();
				permissionRole.setPermissionsId(permission.getId());
				permissionRole.setRolesId(role.getId());
				permissionRole.save();
			}
		}
		return role;
	}
	
	@Override
	public void delete(Long id) {
		super.delete(id);
	}
	
	@Override
	public void delete(Long... ids) {
		super.delete(ids);
	}
	
	@Override
	public void delete(Role role) {
		super.delete(role);
	}
}