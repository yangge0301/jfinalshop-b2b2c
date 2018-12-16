package com.jfinalshop.service;

import java.util.List;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinalshop.dao.AdminDao;
import com.jfinalshop.model.Admin;
import com.jfinalshop.model.AdminRole;
import com.jfinalshop.model.Role;
import com.jfinalshop.shiro.core.SubjectKit;
import com.jfinalshop.util.Assert;
import com.xiaoleilu.hutool.util.CollectionUtil;

/**
 * Service - 管理员
 * 
 */
@Singleton
public class AdminService extends BaseService<Admin> {

	/**
	 * 构造方法
	 */
	public AdminService() {
		super(Admin.class);
	}

	@Inject
	private AdminDao adminDao;
	
	private CacheManager cacheManager = CacheKit.getCacheManager();
	
	/**
	 * 判断用户名是否存在
	 * 
	 * @param username
	 *            用户名(忽略大小写)
	 * @return 用户名是否存在
	 */
	public boolean usernameExists(String username) {
		return adminDao.exists("username", username);
	}

	/**
	 * 根据用户名查找管理员
	 * 
	 * @param username
	 *            用户名(忽略大小写)
	 * @return 管理员，若不存在则返回null
	 */
	public Admin findByUsername(String username) {
		return adminDao.find("username", StringUtils.lowerCase(username));
	}

	/**
	 * 判断E-mail是否存在
	 * 
	 * @param email
	 *            E-mail(忽略大小写)
	 * @return E-mail是否存在
	 */
	public boolean emailExists(String email) {
		return adminDao.exists("email", StringUtils.lowerCase(email));
	}

	/**
	 * 判断E-mail是否唯一
	 * 
	 * @param id
	 *            ID
	 * @param email
	 *            E-mail(忽略大小写)
	 * @return E-mail是否唯一
	 */
	public boolean emailUnique(Long id, String email) {
		return adminDao.unique(id, "email", StringUtils.lowerCase(email));
	}

	/**
	 * 根据E-mail查找管理员
	 * 
	 * @param email
	 *            E-mail(忽略大小写)
	 * @return 管理员，若不存在则返回null
	 */
	public Admin findByEmail(String email) {
		return adminDao.find("email", StringUtils.lowerCase(email));
	}

	/**
	 * 判断管理员是否登录
	 * 
	 * @return 管理员是否登录
	 */
	public boolean isAuthenticated() {
		Subject subject = SecurityUtils.getSubject();
		if (subject != null) {
			return subject.isAuthenticated();
		}
		return false;
	}
	
	/**
	 * 获取当前登录管理员
	 * 
	 * @return 当前登录管理员，若不存在则返回null
	 */
	public Admin getCurrent() {
		Subject subject = SecurityUtils.getSubject();
		if (subject != null) {
			if (!(SubjectKit.getUser() instanceof Admin)) {
				 return null;
			}
			Admin principal = SubjectKit.getUser();
			if (principal != null) {
				return adminDao.find(principal.getId());
			}
		}
		return null;
	}
	
	/**
	 * 获取当前登录用户名
	 * 
	 * @return 当前登录用户名，若不存在则返回null
	 */
	public String getCurrentUsername() {
		Subject subject = SecurityUtils.getSubject();
		if (subject != null) {
			if (!(SubjectKit.getUser() instanceof Admin)) {
				 return null;
			}
			Admin principal = SubjectKit.getUser();
			if (principal != null) {
				return principal.getUsername();
			}
		}
		return null;
	}

	/**
	 * 用户解锁
	 * 
	 * @param user
	 *            用户
	 */
	public void unlock(Admin admin) {
		Assert.notNull(admin);
		Assert.isTrue(!admin.isNew());

		if (BooleanUtils.isFalse(admin.getIsLocked())) {
			return;
		}

		admin.setIsLocked(false);
		admin.setLockDate(null);
		resetFailedLoginAttempts(admin);
	}
	
	/**
	 * 重置登录失败尝试次数
	 * 
	 * @param user
	 *            用户
	 */
	public void resetFailedLoginAttempts(Admin admin) {
		Assert.notNull(admin);
		Assert.isTrue(!admin.isNew());

		Ehcache cache = cacheManager.getEhcache(Admin.FAILED_LOGIN_ATTEMPTS_CACHE_NAME);
		cache.remove(admin.getId());
	}
	
	@Override
	public Admin save(Admin admin) {
		super.save(admin);
		// 关联保存
		List<Role> roles = admin.getRoles();
		if (CollectionUtil.isNotEmpty(roles)) {
			for (Role role : roles) {
				AdminRole adminRole = new AdminRole();
				adminRole.setAdminsId(admin.getId());
				adminRole.setRolesId(role.getId());
				adminRole.save();
			}
		}
		return admin;
	}
	
	@Override
	public Admin update(Admin admin) {
		super.update(admin);
		//先清除，再保存
		Db.deleteById("admin_role", "admins_id", admin.getId());
		List<Role> roles = admin.getRoles();
		if (CollectionUtil.isNotEmpty(roles)) {
			for (Role role : roles) {
				AdminRole adminRole = new AdminRole();
				adminRole.setAdminsId(admin.getId());
				adminRole.setRolesId(role.getId());
				adminRole.save();
			}
		}
		return admin;
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
	public void delete(Admin admin) {
		super.delete(admin);
	}
}