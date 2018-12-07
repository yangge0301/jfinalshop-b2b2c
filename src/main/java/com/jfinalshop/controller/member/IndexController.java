package com.jfinalshop.controller.member;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.interceptor.MobileInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Order;
import com.jfinalshop.service.ConsultationService;
import com.jfinalshop.service.CouponCodeService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.MessageService;
import com.jfinalshop.service.OrderService;
import com.jfinalshop.service.ProductFavoriteService;
import com.jfinalshop.service.ProductNotifyService;
import com.jfinalshop.service.ReviewService;

/**
 * Controller - 首页
 * 
 */
@ControllerBind(controllerKey = "/member/index")
public class IndexController extends BaseController {

	/**
	 * 最新订单数量
	 */
	private static final int NEW_ORDER_SIZE = 3;

	@Inject
	private OrderService orderService;
	@Inject
	private CouponCodeService couponCodeService;
	@Inject
	private MessageService messageService;
	@Inject
	private ProductFavoriteService productFavoriteService;
	@Inject
	private ProductNotifyService productNotifyService;
	@Inject
	private ReviewService reviewService;
	@Inject
	private ConsultationService consultationService;
	@Inject
	private MemberService memberService;

	/**
	 * 首页
	 */
	@Before(MobileInterceptor.class)
	public void index() {
		Member currentUser = memberService.getCurrentUser();
		setAttr("pendingPaymentOrderCount", orderService.count(null, Order.Status.pendingPayment, null, currentUser, null, null, null, null, null, null, false));
		setAttr("pendingShipmentOrderCount", orderService.count(null, Order.Status.pendingShipment, null, currentUser, null, null, null, null, null, null, null));
		setAttr("shippedOrderCount", orderService.count(null, Order.Status.shipped, null, currentUser, null, null, null, null, null, null, null));
		setAttr("messageCount", messageService.count(currentUser, false));
		setAttr("couponCodeCount", couponCodeService.count(null, currentUser, null, false, false));
		setAttr("productFavoriteCount", productFavoriteService.count(currentUser));
		setAttr("productNotifyCount", productNotifyService.count(currentUser, null, null, null));
		setAttr("reviewCount", reviewService.count(currentUser, null, null, null));
		setAttr("consultationCount", consultationService.count(currentUser, null, null));
		setAttr("newOrders", orderService.findList(null, null, null, currentUser, null, null, null, null, null, null, null, NEW_ORDER_SIZE, null, null));
		render("/member/index.ftl");
	}

}