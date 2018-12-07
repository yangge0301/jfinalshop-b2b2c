package com.jfinalshop.controller.common;

import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.route.ControllerBind;


/**
 * Controller - 错误
 * 
 */
@ControllerBind(controllerKey = "/common/error")
public class ErrorController extends Controller {

	/**
	 * 无此访问权限
	 */
	@ActionKey("/common/error/unauthorized")
	public void unauthorized() {
		render("/common/error/unauthorized.ftl");
	}

	/**
	 * 资源未找到
	 */
	@ActionKey("/common/error/not_found")
	public void notFound() {
		render("/common/error/not_found.ftl");
	}

	/**
	 * 验证码错误
	 */
	@ActionKey("/common/error/ncorrect_captcha")
	public void ncorrectCaptcha() {
		render("/common/error/ncorrect_captcha.ftl");
	}

	/**
	 * CSRF令牌错误
	 */
	@ActionKey("/common/error/invalid_csrf_token")
	public void invalidCsrfToken() {
		render("/common/error/invalid_csrf_token.ftl");
	}

}