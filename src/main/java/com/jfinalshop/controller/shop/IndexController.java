package com.jfinalshop.controller.shop;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.interceptor.MobileInterceptor;

/**
 * Controller - 首页
 * 
 */
@ControllerBind(controllerKey = "/")
public class IndexController extends BaseController {

	/**
	 * 首页
	 */
	@Before(MobileInterceptor.class)
	public void index() {
		render("/shop/index.ftl");
	}

	/**
	 * vue移动端
	 */
//	public void web() {
//		Setting setting = SystemUtils.getSetting();
//		redirect(setting.getSiteUrl() + "/web/?#/home");
//	}
	
}