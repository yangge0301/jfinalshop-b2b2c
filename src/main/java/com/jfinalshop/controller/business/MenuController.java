package com.jfinalshop.controller.business;

import java.util.List;

import net.hasor.core.Inject;

import com.jfinal.aop.Clear;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.interceptor.CsrfInterceptor;
import com.jfinalshop.model.Menu;
import com.jfinalshop.service.MenuService;


/**
 * Controller - 菜单
 * 
 */
@ControllerBind(controllerKey = "/business/menu")
public class MenuController extends BaseController {

	@Inject
	MenuService menuService;
	
	/**
	 * 查询所有菜单
	 */
	@Clear(CsrfInterceptor.class)
	public void findAll() {
		List<Menu> menus = menuService.findAll(Menu.Type.business);
		renderJson(menus);
	}
	
}
