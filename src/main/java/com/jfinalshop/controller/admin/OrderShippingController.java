package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.OrderShipping;
import com.jfinalshop.service.OrderShippingService;

/**
 * Controller - 订单发货
 * 
 */
@ControllerBind(controllerKey = "/admin/shipping")
public class OrderShippingController extends BaseController {

	@Inject
	private OrderShippingService orderShippingService;

	/**
	 * 查看
	 */
	public void view() {
		Long id = getParaToLong("id");
		OrderShipping shipping = orderShippingService.find(id);
		setAttr("shipping", shipping);
		setAttr("shippingItems", shipping.getOrderShippingItems());
		render("/admin/order_shipping/view.ftl");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", orderShippingService.findPage(pageable));
		render("/admin/order_shipping/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		orderShippingService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}

}