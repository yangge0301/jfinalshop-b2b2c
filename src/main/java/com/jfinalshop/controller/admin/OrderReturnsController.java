package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.service.OrderReturnsService;

/**
 * Controller - 订单退货
 * 
 */
@ControllerBind(controllerKey = "/admin/returns")
public class OrderReturnsController extends BaseController {

	@Inject
	private OrderReturnsService orderReturnsService;

	/**
	 * 查看
	 */
	public void view() {
		Long id = getParaToLong("id");
		setAttr("returns", orderReturnsService.find(id));
		render("/admin/order_returns/view.ftl"); ;
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", orderReturnsService.findPage(pageable));
		render("/admin/order_returns/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		orderReturnsService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}

}