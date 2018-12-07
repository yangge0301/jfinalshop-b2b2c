package com.jfinalshop.api.common.token;

import org.apache.commons.lang3.StringUtils;

import com.jfinalshop.model.Member;
import com.jfinalshop.shiro.session.RedisManager;
import com.xiaoleilu.hutool.util.RandomUtil;

public class TokenManager {
	
	private static TokenManager	me	= new TokenManager();

	private int	expirationTime	= 3600 * 24 * 10;	 // 超时时间，秒
	private RedisManager redisManager = new RedisManager();

	/**
	 * 获取单例对象
	 * 
	 * @return
	 */
	public static TokenManager getMe() {
		return me;
	}

	/**
	 * 验证token
	 * 
	 * @param token
	 * @return
	 */
	public Member validate(String token) {
		String memberToken = redisManager.get(token);
		Member member = null;
		if (StringUtils.isNotEmpty(memberToken)) {
			member = TokenSerializationUtils.deserialize(memberToken);
		}
		return member;
	}

	/**
	 * 生成token值
	 * 
	 * @param member
	 * @return
	 */
	public String generateToken(Member member) {
		String token = RandomUtil.randomUUID();
		String memberStr = TokenSerializationUtils.serialize(member);
		redisManager.set(token, memberStr, expirationTime);
		return token;
	}
	
	/**
	 * 注销
	 * @param token
	 */
	public void remove(String token){
		redisManager.del(token);
	}
	
}
