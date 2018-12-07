package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.service.AuditLogService;

/**
 * Controller - 审计日志
 * 
 */
@ControllerBind(controllerKey = "/admin/audit_log")
public class AuditLogController extends BaseController {

	@Inject
	private AuditLogService auditLogService;

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", auditLogService.findPage(pageable));
		render("/admin/audit_log/list.ftl");
	}

	/**
	 * 查看
	 */
	public void view() {
		Long id = getParaToLong("id");
		setAttr("auditLog", auditLogService.find(id));
		render("/admin/audit_log/view.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		auditLogService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}

	/**
	 * 清空
	 */
	public void clear() {
		auditLogService.clear();
		renderJson(SUCCESS_MESSAGE);
	}

}