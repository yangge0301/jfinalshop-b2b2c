package com.jfinalshop.controller.member;

import net.hasor.core.Inject;

import org.apache.commons.lang.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.interceptor.MobileInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.shiro.hasher.Hasher;
import com.jfinalshop.shiro.hasher.HasherInfo;
import com.jfinalshop.shiro.hasher.HasherKit;

/**
 * Controller - 密码
 * 
 */
@ControllerBind(controllerKey = "/member/password")
public class PasswordController extends BaseController {

	@Inject
	private MemberService memberService;

	/**
	 * 验证当前密码
	 */
	@ActionKey("/member/password/check_current_password")
	public void checkCurrentPassword() {
		String currentPassword = getPara("currentPassword");
		Member currentUser = memberService.getCurrentUser();
		renderJson(StringUtils.isNotEmpty(currentPassword) && HasherKit.match(currentPassword, currentUser.getPassword()));
	}

	/**
	 * 编辑
	 */
	@Before(MobileInterceptor.class)
	public void edit() {
		render("/member/password/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		String currentPassword = getPara("currentPassword");
		String password = getPara("password");
		Member currentUser = memberService.getCurrentUser();
		
		if (StringUtils.isEmpty(password) || StringUtils.isEmpty(currentPassword)) {
			setAttr("errorMessage", "旧密码和新密码不能为空!");
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
		memberService.update(currentUser);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("edit");
	}

}