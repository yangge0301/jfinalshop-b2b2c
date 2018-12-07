package com.jfinalshop.interceptor;

import javax.servlet.http.HttpServletRequest;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;
import com.jfinalshop.model.AuditLog;
import com.jfinalshop.service.AdminService;
import com.jfinalshop.service.AuditLogService;
import com.jfinalshop.util.HasorUtils;
import com.jfinalshop.util.IpUtil;

public class AuditLogInterceptor implements Interceptor {

	private AuditLogService auditLogService = HasorUtils.getBean(AuditLogService.class);
	
	private AdminService adminService = HasorUtils.getBean(AdminService.class);;
	
	@Override
	public void intercept(Invocation inv) {
		Controller c = inv.getController();
		HttpServletRequest request = c.getRequest();
		if (c != null && (inv.getMethodName().equals("save") || inv.getMethodName().equals("update") || inv.getMethodName().equals("delete"))) {
			AuditLog auditLog = new AuditLog();
			auditLog.setAction(inv.getActionKey());
			auditLog.setIp(IpUtil.getIpAddr(request));
			auditLog.setRequestUrl(request.getRequestURL().toString());
			auditLog.setParameters(JsonKit.toJson(request.getParameterMap()));
			auditLog.setAdminId(adminService.getCurrent().getId());
			auditLogService.create(auditLog);
		}
		inv.invoke();
	}

}
