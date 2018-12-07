package com.jfinalshop.api.controller.member;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.api.controller.BaseAPIController;
import com.jfinalshop.api.interceptor.TokenInterceptor;

/**
 * 会员中心 - 我的
 * 
 */
@ControllerBind(controllerKey = "/api/member")
@Before(TokenInterceptor.class)
public class MemberAPIController extends BaseAPIController {

	public void index() {
		renderText("Hello World!");
	}
	
}
