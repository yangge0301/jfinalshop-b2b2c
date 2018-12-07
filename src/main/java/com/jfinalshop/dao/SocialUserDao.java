package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.SocialUser;

/**
 * Dao - 社会化用户
 * 
 */
public class SocialUserDao extends BaseDao<SocialUser> {

	/**
	 * 构造方法
	 */
	public SocialUserDao() {
		super(SocialUser.class);
	}
	
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
		if (StringUtils.isEmpty(loginPluginId) || StringUtils.isEmpty(uniqueId)) {
			return null;
		}
		String sql = "SELECT * FROM social_user WHERE login_plugin_id = ? AND unique_id = ?";
		return modelManager.findFirst(sql, loginPluginId, uniqueId);
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
		String sqlExceptSelect = "FROM social_user WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		
		if (member != null) {
			sqlExceptSelect += " AND user_id = ?";
			params.add(member.getId());
		}
		return super.findPage(sqlExceptSelect, pageable, params);
	}

}