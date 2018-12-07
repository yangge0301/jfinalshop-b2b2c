package com.jfinalshop.model;

import com.jfinalshop.model.base.BasePermissionRole;

/**
 * Model - 权限角色
 * 
 */
public class PermissionRole extends BasePermissionRole<PermissionRole> {
	private static final long serialVersionUID = 7286551807614151724L;
	public static final PermissionRole dao = new PermissionRole().dao();
	
	
}
