package com.jfinalshop.controller.admin;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Message;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Permission;
import com.jfinalshop.model.Role;
import com.jfinalshop.service.PermissionService;
import com.jfinalshop.service.RoleService;
import net.hasor.core.Inject;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * Controller - 角色
 * 
 */
@ControllerBind(controllerKey = "/admin/role")
public class RoleController extends BaseController {

	@Inject
	private RoleService roleService;
	@Inject
	private PermissionService permissionService;
	

	/**
	 * 添加
	 */
	public void add() {
		setAttr("modules", permissionService.getModules());
		setAttr("permissions", permissionService.findAll());
		render("/admin/role/add.ftl");
	}

	/**
	 * 保存
	 */
	@Before(Tx.class)
	public void save() {
		Role role = getModel(Role.class);
		Boolean isEnabled = getParaToBoolean("isEnabled", true);
		Long[] ids = getParaValuesToLong("permissions");
		List<Permission> permissions = permissionService.findList(ids);
		
		role.setPermissions(permissions);
		role.setIsEnabled(isEnabled);
		role.setIsSystem(false);
		roleService.save(role);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("role", roleService.find(id));
		setAttr("modules", permissionService.getModules());
		setAttr("permissions", permissionService.findAll());
		render("/admin/role/edit.ftl");
	}

	/**
	 * 更新
	 */
	@Before(Tx.class)
	public void update() {
		Role role = getModel(Role.class);
		Boolean isEnabled = getParaToBoolean("isEnabled", true);
		
		
		Role pRole = roleService.find(role.getId());
		if (pRole == null || pRole.getIsSystem()) {
			setAttr("errorMessage", "角色不存在!");
			render(ERROR_VIEW);
			return;
		}
		Long[] ids = getParaValuesToLong("permissions");
		List<Permission> permissions = permissionService.findList(ids);
		role.setPermissions(permissions);
		role.setIsEnabled(isEnabled);
		roleService.update(role, "isSystem", "createdDate");
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", roleService.findPage(pageable));
		render("/admin/role/list.ftl");
	}

	/**
	 * 删除
	 */
	@Before(Tx.class)
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids != null) {
			for (Long id : ids) {
				Role role = roleService.find(id);
				if (role != null && (role.getIsSystem() || CollectionUtils.isNotEmpty(role.getAdmins()))) {
					renderJson(Message.error("admin.role.deleteExistNotAllowed", role.getName()));
					return;
				}
				Db.deleteById("permission_role", "roles_id", role.getId());
			}
			roleService.delete(ids);
		}
		renderJson(SUCCESS_MESSAGE);
	}

}