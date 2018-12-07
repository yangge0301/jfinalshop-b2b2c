package com.jfinalshop.service;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.dao.SocialUserDao;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.SocialUser;
import com.jfinalshop.model.User;
import com.jfinalshop.util.Assert;

/**
 * Service - 社会化用户
 * 
 */
@Singleton
public class SocialUserService extends BaseService<SocialUser> {

	/**
	 * 构造方法
	 */
	public SocialUserService() {
		super(SocialUser.class);
	}
	
	@Inject
	private SocialUserDao socialUserDao;
	
	/**
	 * 查找社会化用户
	 * 
	 * @param loginPluginId
	 *            登录插件ID
	 * @param uniqueId
	 *            唯一ID
	 * @return 社会化用户，若不存在则返回null
	 */
	public SocialUser find(String loginPluginId, String uniqueId) {
		return socialUserDao.find(loginPluginId, uniqueId);
	}

	/**
	 * 查找社会化用户分页
	 * 
	 * @param user
	 *            用户
	 * @param pageable
	 *            分页信息
	 * @return 社会化用户分页
	 */
	public Page<SocialUser> findPage(Member member, Pageable pageable) {
		return socialUserDao.findPage(member, pageable);
	}

	/**
	 * 绑定用户
	 * 
	 * @param user
	 *            用户
	 * @param socialUser
	 *            社会化用户
	 * @param uniqueId
	 *            唯一ID
	 */
	public void bindUser(User user, SocialUser socialUser, String uniqueId) {
		Assert.notNull(socialUser);
		Assert.hasText(uniqueId);

		if (!socialUser.getUniqueId().equals(uniqueId) || socialUser.getUser() != null) {
			return;
		}

		socialUser.setUser(user);
	}

}