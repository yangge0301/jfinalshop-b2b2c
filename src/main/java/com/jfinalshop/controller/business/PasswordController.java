package com.jfinalshop.controller.business;

import net.hasor.core.Inject;

import org.apache.commons.lang.StringUtils;

import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.model.Business;
import com.jfinalshop.service.BusinessService;
import com.jfinalshop.shiro.hasher.Hasher;
import com.jfinalshop.shiro.hasher.HasherInfo;
import com.jfinalshop.shiro.hasher.HasherKit;

/**
 * Controller - 密码
 * 
 */
@ControllerBind(controllerKey = "/business/password")
public class PasswordController extends BaseController {

	@Inject
	private BusinessService businessService;

	/**
	 * 验证当前密码
	 */
	@ActionKey("/business/password/check_current_password")
	public void checkCurrentPassword() {
		String currentPassword = getPara("currentPassword");
		Business currentUser = businessService.getCurrentUser();
		renderJson(StringUtils.isNotEmpty(currentPassword) && HasherKit.match(currentPassword, currentUser.getPassword()));
	}

	/**
	 * 编辑
	 */
	public void edit() {
		render("/business/password/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		String currentPassword = getPara("currentPassword");
		String password = getPara("password");
		Business currentUser = businessService.getCurrentUser();
		
		if (StringUtils.isEmpty(currentPassword) || StringUtils.isEmpty(password)) {
			setAttr("errorMessage", "当前密码不能为空!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (!HasherKit.match(currentPassword, currentUser.getPassword())) {
			setAttr("errorMessage", "旧密码错误!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		HasherInfo passwordInfo = HasherKit.hash(password, Hasher.DEFAULT);
		currentUser.setPassword(passwordInfo.getHashResult());
		currentUser.setHasher(passwordInfo.getHasher().value());
		currentUser.setSalt(passwordInfo.getSalt());
		businessService.update(currentUser);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("edit");
	}

}