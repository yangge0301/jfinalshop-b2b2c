package com.jfinalshop.service;


import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinalshop.dao.AuditLogDao;
import com.jfinalshop.model.AuditLog;

/**
 * Service - 审计日志
 * 
 */
@Singleton
public class AuditLogService extends BaseService<AuditLog> {

	/**
	 * 构造方法
	 */
	public AuditLogService() {
		super(AuditLog.class);
	}
	
	@Inject
	private AuditLogDao auditLogDao;
	
	/**
	 * 创建审计日志(异步)
	 * 
	 * @param auditLog
	 *            审计日志
	 */
	public void create(AuditLog auditLog) {
		auditLogDao.save(auditLog);
	}

	/**
	 * 清空审计日志
	 */
	public void clear() {
		auditLogDao.removeAll();
	}

}