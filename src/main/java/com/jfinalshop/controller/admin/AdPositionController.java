package com.jfinalshop.controller.admin;


import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.AdPosition;
import com.jfinalshop.service.AdPositionService;

/**
 * Controller - 广告位
 * 
 */
@ControllerBind(controllerKey = "/admin/ad_position")
public class AdPositionController extends BaseController {

	@Inject
	private AdPositionService adPositionService;

	/**
	 * 添加
	 */
	public void add() {
		render("/admin/ad_position/add.ftl");
	}

	/**
	 * 保存
	 */
	@Before(Tx.class)
	public void save() {
		AdPosition adPosition = getModel(AdPosition.class);
		
		adPosition.setAds(null);
		adPositionService.save(adPosition);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("adPosition", adPositionService.find(id));
		render("/admin/ad_position/edit.ftl");
	}

	/**
	 * 更新
	 */
	@Before(Tx.class)
	public void update() {
		AdPosition adPosition = getModel(AdPosition.class);
		adPositionService.update(adPosition);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", adPositionService.findPage(pageable));
		render("/admin/ad_position/list.ftl");
	}

	/**
	 * 删除
	 */
	@Before(Tx.class)
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		adPositionService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}

}