package com.jfinalshop.dao;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.AuditLog;

/**
 * Dao - 审计日志
 * 
 */
public class AuditLogDao extends BaseDao<AuditLog> {

	/**
	 * 构造方法
	 */
	public AuditLogDao() {
		super(AuditLog.class);
	}
	
	/**
	 * 删除所有
	 */
	public void removeAll() {
		String sql = "DELETE FROM audit_log";
		Db.update(sql);
	}

}