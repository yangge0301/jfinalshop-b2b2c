package com.jfinalshop.shiro.listeners;

import org.apache.log4j.Logger;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;

/**
 * Created by wangrenhui on 14-1-4.
 */
public class ShiroSessionListener implements SessionListener {
	
	private static Logger log = Logger.getLogger(ShiroSessionListener.class);
	
	@Override
	public void onStart(Session session) {
		log.info("会话创建：" + session.getId());  
	}

	@Override
	public void onStop(Session session) {
		log.info("会话停止：" + session.getId());  
	}

	@Override
	public void onExpiration(Session session) {
		log.info("会话过期：" + session.getId()); 
	}

}
