package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Navigation;
import com.jfinalshop.service.ArticleCategoryService;
import com.jfinalshop.service.NavigationService;
import com.jfinalshop.service.ProductCategoryService;

/**
 * Controller - 导航
 * 
 */
@ControllerBind(controllerKey = "/admin/navigation")
public class NavigationController extends BaseController {

	@Inject
	private NavigationService navigationService;
	@Inject
	private ArticleCategoryService articleCategoryService;
	@Inject
	private ProductCategoryService productCategoryService;

	/**
	 * 添加
	 */
	public void add() {
		setAttr("positions", Navigation.Position.values());
		setAttr("articleCategoryTree", articleCategoryService.findTree());
		setAttr("productCategoryTree", productCategoryService.findTree());
		render("/admin/navigation/add.ftl");
	}

	/**
	 * 保存
	 */
	@Before(Tx.class)
	public void save() {
		Navigation navigation = getModel(Navigation.class);
		Boolean isBlankTarget = getParaToBoolean("isBlankTarget", false);
		Navigation.Position position = getParaEnum(Navigation.Position.class, getPara("position"));
		
		navigation.setPosition(position.ordinal());
		navigation.setIsBlankTarget(isBlankTarget);
		navigationService.save(navigation);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("positions", Navigation.Position.values());
		setAttr("articleCategoryTree", articleCategoryService.findTree());
		setAttr("productCategoryTree", productCategoryService.findTree());
		setAttr("navigation", navigationService.find(id));
		render("/admin/navigation/edit.ftl");
	}

	/**
	 * 更新
	 */
	@Before(Tx.class)
	public void update() {
		Navigation navigation = getModel(Navigation.class);
		Boolean isBlankTarget = getParaToBoolean("isBlankTarget", false);
		Navigation.Position position = getParaEnum(Navigation.Position.class, getPara("position"));
		
		navigation.setPosition(position.ordinal());
		navigation.setIsBlankTarget(isBlankTarget);
		navigationService.update(navigation);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("topNavigations", navigationService.findList(Navigation.Position.top));
		setAttr("middleNavigations", navigationService.findList(Navigation.Position.middle));
		setAttr("bottomNavigations", navigationService.findList(Navigation.Position.bottom));
		render("/admin/navigation/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		navigationService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}

}