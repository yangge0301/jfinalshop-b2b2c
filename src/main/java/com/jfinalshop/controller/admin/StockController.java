package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.service.StockLogService;

/**
 * Controller - 库存
 * 
 */
@ControllerBind(controllerKey = "/admin/stock")
public class StockController extends BaseController {

	@Inject
	private StockLogService stockLogService;

	/**
	 * 记录
	 */
	public void log() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", stockLogService.findPage(pageable));
		render("/admin/stock/log.ftl");
	}

}