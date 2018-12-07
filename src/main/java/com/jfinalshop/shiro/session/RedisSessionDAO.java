package com.jfinalshop.shiro.session;

import java.io.Serializable;
import java.util.Collection;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinalshop.util.HasorUtils;

public class RedisSessionDAO extends EnterpriseCacheSessionDAO {

	private static final Logger	log = LoggerFactory.getLogger(RedisSessionDAO.class);

	private int	expirationTime	= 1800;	 // 超时时间，秒

	private RedisManager redisManager = HasorUtils.getBean(RedisManager.class);

	@Override
	protected Serializable doCreate(Session session) {
		log.debug("Create session: '{}'", session.getId());
		Serializable sessionId = this.generateSessionId(session);
		assignSessionId(session, sessionId);

		String value = ShiroSerializationUtils.serialize(session);
		redisManager.set(String.valueOf(sessionId), value, expirationTime);
		return sessionId;
	}

	@Override
	public void update(Session session) throws UnknownSessionException {
		log.debug("update session: '{}'",session.getId());
		
		if (session instanceof ValidatingSession && !((ValidatingSession) session).isValid()) {
            return;
        }
		redisManager.set(String.valueOf(session.getId()),ShiroSerializationUtils.serialize(session), expirationTime);
	}
	
	@Override
	public void delete(Session session) {
		log.debug("delete session: '{}'",session.getId());
		redisManager.del(String.valueOf(session.getId()));
	}

	@Override
	protected Session doReadSession(Serializable sessionId) {
		log.debug("Read session: '{}'",sessionId);
		
		String sessionStr = redisManager.get(String.valueOf(sessionId));
		return sessionStr == null ? null : ShiroSerializationUtils.deserialize(sessionStr);
	}
	
	// 使用 会话验证调度器 需实现此方法
	@Override
	public Collection<Session> getActiveSessions() {
		return null;
	}

	public void setExpirationTime(int expirationTime) {
		this.expirationTime = expirationTime;
	}

	public void setRedisManager(RedisManager redisManager) {
		this.redisManager = redisManager;
	}

}
