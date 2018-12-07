package com.jfinalshop.controller.member;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.Results;
import com.jfinalshop.interceptor.MobileInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.SocialUser;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.SocialUserService;

/**
 * Controller - 社会化用户
 * 
 */
@ControllerBind(controllerKey = "/member/social_user")
public class SocialUserController extends BaseController {

	/**
	 * 每页记录数
	 */
	private static final int PAGE_SIZE = 10;

	@Inject
	private SocialUserService socialUserService;
	@Inject
	private MemberService memberService;

	/**
	 * 列表
	 */
	@Before(MobileInterceptor.class)
	public void list() {
		Integer pageNumber = getParaToInt("pageNumber");
		Member currentUser = memberService.getCurrentUser();
		
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("page", socialUserService.findPage(currentUser, pageable));
		render("/member/social_user/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long id = getParaToLong("id");
		Member currentUser = memberService.getCurrentUser();
		
		if (id == null) {
			Results.notFound(getResponse(), Results.DEFAULT_NOT_FOUND_MESSAGE);
			return;
		}
		SocialUser socialUser = socialUserService.find(id);
		if (socialUser == null || !currentUser.equals(socialUser.getUser())) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		socialUserService.delete(socialUser);
		renderJson(Results.OK);
	}

}