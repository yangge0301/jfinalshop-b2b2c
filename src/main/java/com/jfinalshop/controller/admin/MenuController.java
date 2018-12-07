package com.jfinalshop.controller.admin;

import java.util.ArrayList;
import java.util.List;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Order;
import com.jfinalshop.Pageable;
import com.jfinalshop.interceptor.CsrfInterceptor;
import com.jfinalshop.model.Menu;
import com.jfinalshop.service.MenuService;


/**
 * Controller - 菜单
 * 
 */
@ControllerBind(controllerKey = "/admin/menu")
public class MenuController extends BaseController {

	@Inject
	MenuService menuService;
	
	/**
	 * 检查用户名是否存在
	 */
	@ActionKey("/admin/menu/find_level_code")
	public void findLevelCode() {
		Long parentId = getParaToLong("parentId", 0L);
		renderJson(Kv.by("levelCode", menuService.findLevelCode(parentId)));
	}
	
	/**
	 * 查询所有菜单
	 */
	@Clear(CsrfInterceptor.class)
	public void findAll() {
		List<Menu> menus = menuService.findAll(Menu.Type.admin);
		renderJson(menus);
	}
	
	
	/**
	 * 添加
	 */
	public void add() {
		setAttr("menuTree", menuService.findTree());
		setAttr("types", Menu.Type.values());
		render("/admin/menu/add.ftl");
	}
	
	/**
	 * 保存
	 */
	@Before(Tx.class)
	public void save() {
		Menu menu = getModel(Menu.class);
		Menu.Type type = getParaEnum(Menu.Type.class, getPara("type"));
		menu.setType(type.ordinal());
		Long parentId = getParaToLong("parentId", 0L);
		Boolean isEnabled = getParaToBoolean("isEnabled");
		
		menu.setParentId(parentId);
		menu.setIsEnabled(isEnabled);
		menu.setGrade(parentId == 0L ? 0 : 1);
		menuService.save(menu);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}
	
	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		Menu menu = menuService.find(id);
		setAttr("types", Menu.Type.values());
		setAttr("menuTree", menuService.findTree());
		setAttr("children", menuService.findChildren(menu));
		setAttr("menu", menuService.find(id));
		render("/admin/menu/edit.ftl");
	}
	
	/**
	 * 更新
	 */
	@Before(Tx.class)
	public void update() {
		Menu menu = getModel(Menu.class);
		Menu.Type type = getParaEnum(Menu.Type.class, getPara("type"));
		menu.setType(type.ordinal());
		
		menuService.update(menu);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}
	
	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		List<Order> orders = new ArrayList<Order>();
		orders.add(new Order("level_code", Order.Direction.asc));
		pageable.setOrders(orders);
		setAttr("pageable", pageable);
		setAttr("page", menuService.findPage(pageable));
		render("/admin/menu/list.ftl");
	}
	
	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		menuService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}
}
