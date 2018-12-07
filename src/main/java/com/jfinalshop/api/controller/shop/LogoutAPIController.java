package com.jfinalshop.api.controller.shop;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinalshop.api.common.bean.DatumResponse;
import com.jfinalshop.api.common.token.TokenManager;
import com.jfinalshop.api.controller.BaseAPIController;
import com.jfinalshop.api.interceptor.TokenInterceptor;

/**
 * 
 * 移动API - 会员注销
 * 
 */
@ControllerBind(controllerKey = "/api/logout")
@Before(TokenInterceptor.class)
public class LogoutAPIController extends BaseAPIController {

	
	/**
	 * 注销
	 * 
	 */
	public void index() {
		String token = getPara("token");
		if (StrKit.notBlank(token)) {
			TokenManager.getMe().remove(token);
		}
		renderJson(new DatumResponse("退出成功!"));
	}
	
}
