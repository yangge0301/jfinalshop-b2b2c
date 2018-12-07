package com.jfinalshop.model;

import com.jfinalshop.model.base.BaseAdminRole;

/**
 * Model - 管理员角色中间表
 * 
 */
public class AdminRole extends BaseAdminRole<AdminRole> {
	private static final long serialVersionUID = 2636136668999664047L;
	public static final AdminRole dao = new AdminRole().dao();
}
