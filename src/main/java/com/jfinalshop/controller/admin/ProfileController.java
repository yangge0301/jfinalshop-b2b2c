package com.jfinalshop.controller.admin;

import org.apache.commons.lang.StringUtils;

import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.model.Admin;
import com.jfinalshop.shiro.hasher.Hasher;
import com.jfinalshop.shiro.hasher.HasherInfo;
import com.jfinalshop.shiro.hasher.HasherKit;

/**
 * Controller - 个人资料
 * 
 */
@ControllerBind(controllerKey = "/admin/profile")
public class ProfileController extends BaseController {

	/**
	 * 验证当前密码
	 */
	@ActionKey("/admin/profile/check_current_password")
	public void checkCurrentPassword() {
		String currentPassword = getPara("currentPassword");
		if (StringUtils.isEmpty(currentPassword)) {
			renderJson(false);
			return;
		}
		Admin admin = adminService.getCurrent();
		renderJson(HasherKit.match(currentPassword, admin.getPassword()));
	}

	/**
	 * 编辑
	 */
	public void edit() {
		setAttr("admin", adminService.getCurrent());
		render("/admin/profile/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		String currentPassword = getPara("currentPassword");
		String password = getPara("password");
		String email = getPara("email");
		
		Admin pAdmin = adminService.getCurrent();
		if (StringUtils.isNotEmpty(currentPassword) && StringUtils.isNotEmpty(password)) {
			if (!HasherKit.match(currentPassword, pAdmin.getPassword())) {
				setAttr("errorMessage", "会员不能为空!");
				redirect(ERROR_VIEW);
				return;
			}
			HasherInfo passwordInfo = HasherKit.hash(password, Hasher.DEFAULT);
			pAdmin.setPassword(passwordInfo.getHashResult());
			pAdmin.setHasher(passwordInfo.getHasher().value());
			pAdmin.setSalt(passwordInfo.getSalt());
		}
		pAdmin.setEmail(email);
		pAdmin.update();
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("edit");
	}

}