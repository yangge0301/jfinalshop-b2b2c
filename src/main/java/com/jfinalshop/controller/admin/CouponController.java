package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.service.CouponService;

/**
 * Controller - 优惠券
 * 
 */
@ControllerBind(controllerKey = "/admin/coupon")
public class CouponController extends BaseController {

	@Inject
	private CouponService couponService;

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", couponService.findPage(pageable));
		render("/admin/coupon/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		couponService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}

}