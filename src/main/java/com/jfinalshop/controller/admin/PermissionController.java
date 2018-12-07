package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Message;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Permission;
import com.jfinalshop.service.PermissionService;
import com.xiaoleilu.hutool.util.CollectionUtil;

/**
 * Controller - 权限
 * 
 */
@ControllerBind(controllerKey = "/admin/permission")
public class PermissionController extends BaseController {

	@Inject
	PermissionService permissionService;
	
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("page", permissionService.findPage(pageable));
		setAttr("pageable", pageable);
		render("/admin/permission/list.ftl");
	}
	
	/**
	 * 添加
	 */
	public void add() {
		render("/admin/permission/add.ftl");
	}
	
	/**
	 * 保存
	 */
	@Before(Tx.class)
	public void save() {
		Permission permission = getModel(Permission.class);
		Boolean isEnabled = getParaToBoolean("isEnabled", false);
		permission.setIsEnabled(isEnabled);
		
		if (permissionService.nameExists(permission.getValue())) {
			setAttr("errorMessage", "权限值已存在!");
			render(ERROR_VIEW);
			return;
		}
		
		permissionService.save(permission);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}
	
	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("permission", permissionService.find(id));
		render("/admin/permission/edit.ftl");
	}
	
	/**
	 * 更新
	 */
	@Before(Tx.class)
	public void update() {
		Permission permission = getModel(Permission.class);
		Boolean isEnabled = getParaToBoolean("isEnabled", false);
		permission.setIsEnabled(isEnabled);
		
		if (permissionService.nameUnique(permission.getId(), permission.getValue())) {
			setAttr("errorMessage", "权限值已存在!");
			render(ERROR_VIEW);
			return;
		}
		
		permissionService.update(permission);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}
	
	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids != null) {
			for (Long id : ids) {
				Permission permission = permissionService.find(id);
				if (CollectionUtil.isNotEmpty(permission.getRoles())) {
					renderJson(Message.error("admin.permission.deleteExistNotAllowed", permission.getName()));
					return;
				}
			}
			permissionService.delete(ids);
		}
		renderJson(SUCCESS_MESSAGE);
	}
}
