package com.jfinalshop.controller.business;

import com.jfinal.ext.route.ControllerBind;

/**
 * Controller - 商家中心
 * 
 */
@ControllerBind(controllerKey = "/business/index")
public class IndexController extends BaseController {

	/**
	 * 首页
	 */
	public void index() {
		render("/business/index.ftl");
	}

	/**
	 * 仪表盘
	 */
	public void main() {
		render("/business/common/main.ftl");
	}
}