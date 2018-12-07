package com.jfinalshop.api.controller.member;

import java.util.List;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.api.common.bean.DatumResponse;
import com.jfinalshop.api.controller.BaseAPIController;
import com.jfinalshop.api.interceptor.TokenInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Order;
import com.jfinalshop.model.OrderRefunds;
import com.jfinalshop.service.OrderRefundsService;
import com.jfinalshop.service.OrderService;

@ControllerBind(controllerKey = "/api/member/orderRefunds")
@Before(TokenInterceptor.class)
public class OrderRefundsAPIController extends BaseAPIController {
	
	private Res res = I18n.use();
	@Inject
	private OrderRefundsService orderRefundsService;
	@Inject
	private OrderService orderService;
	
	/**
	 * 用户退款单分页
	 */
	public void list() {
		Integer pageNumber = getParaToInt("pageNumber", 1);
		Integer pageSize = getParaToInt("pageSize", 20);
		
		Pageable pageable = new Pageable(pageNumber, pageSize);
		Member member = getMember();
		Page<OrderRefunds> pages = orderRefundsService.findPage(member, pageable);
		List<OrderRefunds> refunds = pages.getList();
		for (OrderRefunds refund : refunds) {
			Order order = refund.getOrder();
			//orderService.formatOrder(order);
			convertOrder(order);
			refund.put("order", order);
		}
		renderJson(new DatumResponse(pages));
	}
	
	/**
	 * 退款详情
	 */
	public void view() {
		Long refundsId = getParaToLong("refundsId");
		if (refundsId == null) {
			renderArgumentError("退款单ID不能为空!");
			return;
		}
		OrderRefunds orderRefunds = orderRefundsService.find(refundsId);
		if (orderRefunds == null) {
			renderArgumentError("退款单不存在!");
			return;
		}
		Member member = getMember();
		Order order = orderRefunds.getOrder();
		if (!member.getId().equals(order.getMemberId())) {
			renderArgumentError("退款单不属于当前登录用户!");
			return;
		}
		
		orderRefunds.put("statusName", res.format("Order.Status." + order.getStatusName()));
		renderJson(new DatumResponse(orderRefunds));
	}

}
