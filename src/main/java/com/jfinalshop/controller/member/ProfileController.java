package com.jfinalshop.controller.member;

import net.hasor.core.Inject;

import org.apache.commons.lang.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.interceptor.MobileInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.MemberAttribute;
import com.jfinalshop.service.MemberAttributeService;
import com.jfinalshop.service.MemberService;

/**
 * Controller - 个人资料
 * 
 */
@ControllerBind(controllerKey = "/member/profile")
public class ProfileController extends BaseController {

	@Inject
	private MemberService memberService;
	@Inject
	private MemberAttributeService memberAttributeService;

	/**
	 * 检查E-mail是否唯一
	 */
	@ActionKey("/member/profile/check_email")
	public void checkEmail() {
		String email = getPara("email");
		Member currentUser = memberService.getCurrentUser();
		renderJson(StringUtils.isNotEmpty(email) && memberService.emailUnique(currentUser.getId(), email));
	}

	/**
	 * 检查手机是否唯一
	 */
	@ActionKey("/member/profile/check_mobile")
	public void checkMobile() {
		String mobile = getPara("mobile");
		Member currentUser = memberService.getCurrentUser();
		renderJson(StringUtils.isNotEmpty(mobile) && memberService.mobileUnique(currentUser.getId(), mobile));
	}

	/**
	 * 编辑
	 */
	@Before(MobileInterceptor.class)
	public void edit() {
		setAttr("genders", Member.Gender.values());
		render("/member/profile/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		String email = getPara("email");
		String mobile = getPara("mobile");
		Member currentUser = memberService.getCurrentUser();
		
		if (!memberService.emailUnique(currentUser.getId(), email)) {
			setAttr("errorMessage", "e-mail存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (!memberService.mobileUnique(currentUser.getId(), mobile)) {
			setAttr("errorMessage", "手机存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		currentUser.setEmail(email);
		currentUser.setMobile(mobile);
		currentUser.removeAttributeValue();
		for (MemberAttribute memberAttribute : memberAttributeService.findList(true, true)) {
			String[] values = getParaValues("memberAttribute_" + memberAttribute.getId());
			if (!memberAttributeService.isValid(memberAttribute, values)) {
				setAttr("errorMessage", "会员注册项值验证错误!");
				render(UNPROCESSABLE_ENTITY_VIEW);
				return;
			}
			Object memberAttributeValue = memberAttributeService.toMemberAttributeValue(memberAttribute, values);
			currentUser.setAttributeValue(memberAttribute, memberAttributeValue);
		}
		memberService.update(currentUser);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("edit");
	}

}