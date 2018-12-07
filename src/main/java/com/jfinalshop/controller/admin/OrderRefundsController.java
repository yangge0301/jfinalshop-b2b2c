package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.service.OrderRefundsService;

/**
 * Controller - 订单退款
 * 
 */
@ControllerBind(controllerKey = "/admin/refunds")
public class OrderRefundsController extends BaseController {

	@Inject
	private OrderRefundsService orderRefundsService;
	
	/**
	 * 查看
	 */
	public void view() {
		Long id = getParaToLong("id");
		setAttr("refunds", orderRefundsService.find(id));
		render("/admin/order_refunds/view.ftl");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", orderRefundsService.findPage(pageable));
		render("/admin/order_refunds/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		orderRefundsService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}
	
}