package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.service.PromotionService;

/**
 * Controller - 促销
 * 
 */
@ControllerBind(controllerKey = "/admin/promotion")
public class PromotionController extends BaseController {

	@Inject
	private PromotionService promotionService;

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", promotionService.findPage(pageable));
		render("/admin/promotion/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		promotionService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}

}