package com.jfinalshop.controller.admin;

import java.util.List;

import net.hasor.core.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Message;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Admin;
import com.jfinalshop.model.Role;
import com.jfinalshop.service.RoleService;
import com.jfinalshop.shiro.hasher.Hasher;
import com.jfinalshop.shiro.hasher.HasherInfo;
import com.jfinalshop.shiro.hasher.HasherKit;

/**
 * Controller - 管理员
 * 
 */
@ControllerBind(controllerKey = "/admin/admin")
public class AdminController extends BaseController {

	@Inject
	private RoleService roleService;

	/**
	 * 检查用户名是否存在
	 */
	@ActionKey("/admin/admin/check_username")
	public void checkUsername() {
		String username = getPara("admin.username");
		renderJson(StringUtils.isNotEmpty(username) && !adminService.usernameExists(username));
	}

	/**
	 * 检查E-mail是否唯一
	 */
	@ActionKey("/admin/admin/check_email")
	public void checkEmail() {
		Long id = getParaToLong("id");
		String email = getPara("admin.email");
		renderJson(StringUtils.isNotEmpty(email) && adminService.emailUnique(id, email));
	}

	/**
	 * 添加
	 */
	public void add() {
		setAttr("roles", roleService.findAll());
		render("/admin/admin/add.ftl");
	}

	/**
	 * 保存
	 */
	@Before(Tx.class)
	public void save() {
		Admin admin = getModel(Admin.class);
		Boolean isEnabled = getParaToBoolean("isEnabled", false);
		String password = getPara("password");
		String rePassword = getPara("rePassword");
		
		if (!StringUtils.equals(password, rePassword)) {
			setAttr("errorMessage", "两次密码不一致!");
			render(ERROR_VIEW);
			return;
		}
		if (adminService.usernameExists(admin.getUsername())) {
			setAttr("errorMessage", "用户名已存在!");
			render(ERROR_VIEW);
			return;
		}
		if (adminService.emailExists(admin.getEmail())) {
			setAttr("errorMessage", "E-mail已存在!");
			render(ERROR_VIEW);
			return;
		}
		
		Long[] roleIds = getParaValuesToLong("roleIds");
		List<Role> roles = roleService.findList(roleIds);
		admin.setRoles(roles);
		
		HasherInfo hasherInfo = HasherKit.hash(password, Hasher.DEFAULT);
		admin.setPassword(hasherInfo.getHashResult());
		admin.setHasher(hasherInfo.getHasher().value());
		admin.setSalt(hasherInfo.getSalt());
		admin.setIsEnabled(isEnabled);
		admin.setUsername(StringUtils.lowerCase(admin.getUsername()));
		admin.setEmail(StringUtils.lowerCase(admin.getEmail()));
		admin.setIsLocked(false);
		admin.setLockDate(null);
		admin.setLastLoginIp(null);
		admin.setLastLoginDate(null);
		//admin.setPaymentTransactions(null);
		adminService.save(admin);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("roles", roleService.findAll());
		setAttr("admin", adminService.find(id));
		render("/admin/admin/edit.ftl");
	}

	/**
	 * 更新
	 */
	@Before(Tx.class)
	public void update() {
		Admin admin = getModel(Admin.class);
		Boolean unlock = getParaToBoolean("unlock", false);
		Boolean isEnabled = getParaToBoolean("isEnabled", false);
		String password = getPara("password");
		String rePassword = getPara("rePassword");
		
		if (!adminService.emailUnique(admin.getId(), admin.getEmail())) {
			setAttr("errorMessage", "E-mail不唯一!");
			render(ERROR_VIEW);
			return;
		}
		Admin pAdmin = adminService.find(admin.getId());
		if (pAdmin == null) {
			setAttr("errorMessage", "没有找到该用户!");
			render(ERROR_VIEW);
			return;
		}
		if (!StringUtils.equals(password, rePassword)) {
			setAttr("errorMessage", "两次密码不一致!");
			render(ERROR_VIEW);
			return;
		}
		if (StringUtils.isNotEmpty(password)) {
			HasherInfo hasherInfo = HasherKit.hash(password, Hasher.DEFAULT);
			admin.setPassword(hasherInfo.getHashResult());
			admin.setHasher(hasherInfo.getHasher().value());
			admin.setSalt(hasherInfo.getSalt());
		} else {
			admin.setPassword(pAdmin.getPassword());
		}
		
		Long[] roleIds = getParaValuesToLong("roleIds");
		List<Role> roles = roleService.findList(roleIds);
		admin.setRoles(roles);
		
		admin.setIsEnabled(isEnabled);
		admin.setEmail(StringUtils.lowerCase(admin.getEmail()));
		if (BooleanUtils.isTrue(pAdmin.getIsLocked()) && BooleanUtils.isTrue(unlock)) {
			adminService.unlock(admin);
		}
		adminService.update(admin);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("/admin/admin/list");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", adminService.findPage(pageable));
		render("/admin/admin/list.ftl");
	}

	/**
	 * 删除
	 */
	@Before(Tx.class)
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids.length >= adminService.count()) {
			renderJson(Message.error("admin.common.deleteAllNotAllowed"));
			return;
		}
		adminService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}

}