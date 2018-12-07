package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.service.OrderPaymentService;

/**
 * Controller - 订单支付
 * 
 */
@ControllerBind(controllerKey = "/admin/order_payment")
public class OrderPaymentController extends BaseController {

	@Inject
	private OrderPaymentService orderPaymentService;

	/**
	 * 查看
	 */
	public void view() {
		Long id = getParaToLong("id");
		setAttr("orderPayment", orderPaymentService.find(id));
		render("/admin/order_payment/view.ftl");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", orderPaymentService.findPage(pageable));
		render("/admin/order_payment/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		orderPaymentService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}

}