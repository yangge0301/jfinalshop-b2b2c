package com.jfinalshop.api.controller.member;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.api.common.bean.BaseResponse;
import com.jfinalshop.api.common.bean.DatumResponse;
import com.jfinalshop.api.controller.BaseAPIController;
import com.jfinalshop.api.interceptor.TokenInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Order;
import com.jfinalshop.model.OrderLog;
import com.jfinalshop.service.OrderLogService;
import com.jfinalshop.service.OrderService;
import com.jfinalshop.service.PaymentMethodService;

/**
 * 会员中心 - 订单信息相关
 * 
 */
@ControllerBind(controllerKey = "/api/member/order")
@Before(TokenInterceptor.class)
public class OrderAPIController extends BaseAPIController{
	
	@Inject
	private OrderService orderService;
	@Inject
	private OrderLogService orderLogService;
	@Inject
	private PaymentMethodService paymentMethodService;
	
	/** 默认分页大小 */
	private static final int PAGE_SIZE = 10;
	
	/**
	 * 订单退款
	 */
	public void refunds() {
		
	}
	
	/**
	 * 取消订单
	 */
	public void cancel() {
		String sn = getPara("sn");
		
		Order order = orderService.findBySn(sn);
		if (order == null) {
			renderArgumentError("订单未找到!");
			return;
		}
		Member member = getMember();
		if (!member.getId().equals(order.getMemberId())) {
			renderArgumentError("订单创建人与当前用户不同!");
			return;
		}
		if (!Order.Status.pendingPayment.equals(order.getStatusName()) && !Order.Status.pendingReview.equals(order.getStatusName()) && !Order.Status.pendingShipment.equals(order.getStatusName())) {
			renderArgumentError("订单过期或已接单!");
			return;
		}
		if (!orderService.acquireLock(order, member)) {
			renderArgumentError(res.format("member.order.locked"));
			return;
		}
		
		orderService.cancel(order);
		renderJson(new BaseResponse("取消成功!"));
	}
	
	/**
	 * 确认收货
	 */
	public void receive() {
		String sn = getPara("sn");
		
		Order order = orderService.findBySn(sn);
		if (order == null) {
			renderArgumentError("订单未找到!");
			return;
		}
		Member member = getMember();
		if (!member.getId().equals(order.getMemberId())) {
			renderArgumentError("订单创建人与当前用户不同!");
			return;
		}
		if (order.hasExpired() || !Order.Status.shipped.equals(order.getStatusName())) {
			renderArgumentError("状态非已发货!");
			return;
		}
		if (!orderService.acquireLock(order, member)) {
			renderArgumentError(res.format("member.order.locked"));
			return;
		}
		
		orderService.receive(order);
		orderService.complete(order);
		renderJson(new BaseResponse("确认成功!"));
	}
	
	/**
	 * 订单详情
	 */
	public void view() {
		String sn = getPara("sn");
		
		Order order = orderService.findBySn(sn);
		if (order == null) {
			renderArgumentError("订单不存在!");
			return;
		}
		Member member = getMember();
		if (!member.getId().equals(order.getMemberId())) {
			renderArgumentError("订单创建人与当前用户不同!");
			return;
		}
		
		// 处理订单日志
		List<OrderLog> orderLogs = order.getOrderLogs();
		for (OrderLog orderLog: orderLogs) {
			orderLog.put("typeName", res.format("OrderLog.Type." + orderLog.getTypeName()));
		}
		
		order.put("order_items", order.getOrderItems());
		order.put("status_name", res.format("Order.Status." + order.getStatusName()));
		order.put("refunds", order.getOrderRefunds());
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("order", order);
		map.put("order_log", orderLogs);
		renderJson(new DatumResponse(map));
	}
	
	/**
	 * 我的订单列表
	 */
	public void list() {
		Integer pageNumber = getParaToInt("pageNumber", 1);
		Integer pageSize = getParaToInt("pageSize", PAGE_SIZE);
		
		Order.Status status = getParaEnum(Order.Status.class, getPara("status"));
		Order.Type type = getParaEnum(Order.Type.class, getPara("type"));
		 
		Member member = getMember();
		Pageable pageable = new Pageable(pageNumber, pageSize);
		Page<Order> pages = orderService.findPage(type, status, null, member, null, null, null, null, null, null, null, pageable);
		List<Order> orders = pages.getList();
		for (Order order : orders) {
			convertOrder(order);
		}
		renderJson(new DatumResponse(pages));
	}
	
}
