package com.jfinalshop.dao;

import java.util.List;

import com.jfinalshop.model.Admin;
import com.jfinalshop.model.Business;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Role;

/**
 * Dao - 角色
 * 
 */
public class RoleDao extends BaseDao<Role> {

	/**
	 * 构造方法
	 */
	public RoleDao() {
		super(Role.class);
	}

	public static final RoleDao me = new RoleDao();
	
	/**
	 * 根据管理员查找角色
	 * 
	 * @param admin
	 * @return 角色，若不存在则返回null
	 */
	public List<Role> findByAdmin(Admin admin) {
		if (admin == null) {
			return null;
		}
		String sql = "SELECT * FROM role WHERE id IN (SELECT roles_id FROM admin_role WHERE admins_id = ?)";
		return modelManager.find(sql, admin.getId());
	}
	
	/**
	 * 根据商家查找角色
	 * 
	 * @param admin
	 * @return 角色，若不存在则返回null
	 */
	public List<Role> findByBusiness(Business business) {
		if (business == null) {
			return null;
		}
		String sql = "SELECT * FROM role WHERE id IN (SELECT roles_id FROM business_role WHERE business_id = ?)";
		return modelManager.find(sql, business.getId());
	}
	
	/**
	 * 根据会员查找角色
	 * 
	 * @param admin
	 * @return 角色，若不存在则返回null
	 */
	public List<Role> findByMember(Member member) {
		if (member == null) {
			return null;
		}
		String sql = "SELECT * FROM role WHERE id IN (SELECT roles_id FROM member_role WHERE members_id = ?)";
		return modelManager.find(sql, member.getId());
	}
	
}