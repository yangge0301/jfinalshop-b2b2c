package com.jfinalshop.controller.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinalshop.dao.OrderItemDao;
import com.jfinalshop.dao.OrderLogDao;
import com.jfinalshop.model.*;
import com.jfinalshop.service.BusinessService;
import net.hasor.core.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.UnauthorizedException;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Pageable;
import com.jfinalshop.Results;
import com.jfinalshop.Setting;
import com.jfinalshop.interceptor.MobileInterceptor;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.OrderService;
import com.jfinalshop.service.OrderShippingService;
import com.jfinalshop.util.SystemUtils;

/**
 * Controller - 订单
 * 
 */
@ControllerBind(controllerKey = "/member/order")
public class OrderController extends BaseController {

	/**
	 * 每页记录数
	 */
	private static final int PAGE_SIZE = 10;

	@Inject
	private OrderService orderService;
	@Inject
	private OrderShippingService orderShippingService;
	@Inject
	private MemberService memberService;

	/**
	 * 添加属性
	 */
	public void populateModel() {
		String orderSn = getPara("orderSn");
		String orderShippingSn = getPara("orderShippingSn");
		Member currentUser = memberService.getCurrentUser();
		
		Order order = orderService.findBySn(orderSn);
		if (order != null && !currentUser.equals(order.getMember())) {
			throw new UnauthorizedException();
		}
		setAttr("order", order);

		OrderShipping orderShipping = orderShippingService.findBySn(orderShippingSn);
		if (orderShipping != null && orderShipping.getOrder() != null && !currentUser.equals(orderShipping.getOrder().getMember())) {
			throw new UnauthorizedException();
		}
		setAttr("orderShipping", orderShipping);
	}

	/**
	 * 检查锁定
	 */
	@ActionKey("/member/order/check_lock")
	public void checkLock() {
		String orderSn = getPara("orderSn");
		Order order = orderService.findBySn(orderSn);
		Member currentUser = memberService.getCurrentUser();
		
		if (order == null) {
			Results.notFound(getResponse(), Results.DEFAULT_NOT_FOUND_MESSAGE);
			return;
		}

		if (!orderService.acquireLock(order, currentUser)) {
			Results.unprocessableEntity(getResponse(), "member.order.locked");
		}
		renderJson(Results.OK);
	}

	/**
	 * 物流动态
	 */
	@ActionKey("/member/order/transit_step")
	public void transitStep() {
		OrderShipping orderShipping = getModel(OrderShipping.class);
		//Member currentUser = memberService.getCurrentUser();
		
		Map<String, Object> data = new HashMap<>();
		if (orderShipping == null) {
			Results.notFound(getResponse(), Results.DEFAULT_NOT_FOUND_MESSAGE);
			return;
		}

		Setting setting = SystemUtils.getSetting();
		if (StringUtils.isEmpty(setting.getKuaidi100Key()) || StringUtils.isEmpty(orderShipping.getDeliveryCorpCode()) || StringUtils.isEmpty(orderShipping.getTrackingNo())) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		data.put("transitSteps", orderShippingService.getTransitSteps(orderShipping));
		renderJson(data);
	}

	/**
	 * 列表
	 */
	@Before(MobileInterceptor.class)
	public void list() {
		Order.Status status = getParaEnum(Order.Status.class, getPara("status"));
		Boolean hasExpired = getParaToBoolean("hasExpired");
		Integer pageNumber = getParaToInt("pageNumber");
		Member currentUser = memberService.getCurrentUser();
		
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		Setting setting = SystemUtils.getSetting();
		setAttr("status", status);
		setAttr("hasExpired", hasExpired);
		setAttr("isKuaidi100Enabled", StringUtils.isNotEmpty(setting.getKuaidi100Key()));
		setAttr("pageable", pageable);
		setAttr("page", orderService.findPage(null, status, null, currentUser, null, null, null, null, null, null, hasExpired, pageable));
		render("/member/order/list.ftl");
	}
	/**
	 * 列表
	 */
	@ActionKey("/member/order/m_list")
	public void mList() {
		Order.Status status = getParaEnum(Order.Status.class, getPara("status"));
		Boolean hasExpired = getParaToBoolean("hasExpired");
		Integer pageNumber = getParaToInt("pageNumber", 1);
		Member currentUser = memberService.getCurrentUser();
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		Page<Order> pages = orderService.findPage(null, status, null, currentUser, null, null, null, null, null, null, hasExpired, pageable);
		
		List<Order> orders = new ArrayList<Order>();
		if (CollectionUtils.isNotEmpty(pages.getList())) {
			for (Order order : pages.getList()) {
				Store store = order.getStore();
				store.put("type", store.getTypeName());
				store.put("path", store.getPath());
				order.put("store", store);
				List<OrderItem> orderItems = order.getOrderItems();
				List<OrderItem> pOrderItems = new ArrayList<OrderItem>();
				if (CollectionUtils.isNotEmpty(orderItems)) {
					for (OrderItem orderItem : orderItems) {
						orderItem.put("specifications", orderItem.getSpecificationsConverter());
						pOrderItems.add(orderItem);
					}
				}
				order.put("orderItems", pOrderItems);
				order.put("status", order.getStatusName());
				order.put("type", order.getTypeName());
				orders.add(order);
			}
		}
		renderJson(orders);
	}

	/**
	 * 查看
	 */
	@Before(MobileInterceptor.class)
	public void view() {
		String orderSn = getPara("orderSn");
		Order order = orderService.findBySn(orderSn);
		//Member currentUser = memberService.getCurrentUser();
		
		if (order == null) {
			setAttr("errorMessage", "订单不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		Setting setting = SystemUtils.getSetting();
		setAttr("isKuaidi100Enabled", StringUtils.isNotEmpty(setting.getKuaidi100Key()));
		setAttr("order", order);
		render("/member/order/view.ftl");
	}

	/**
	 * 取消
	 */
	@Before(Tx.class)
	public void cancel() {
		String orderSn = getPara("orderSn");
		Order order = orderService.findBySn(orderSn);
		Member currentUser = memberService.getCurrentUser();
		
		if (order == null) {
			Results.notFound(getResponse(), Results.DEFAULT_NOT_FOUND_MESSAGE);
			return;
		}

		if (order.hasExpired() || (!Order.Status.pendingPayment.equals(order.getStatusName()) && !Order.Status.pendingReview.equals(order.getStatusName()))) {
			Results.notFound(getResponse(), Results.DEFAULT_NOT_FOUND_MESSAGE);
			return;
		}
		if (!orderService.acquireLock(order, currentUser)) {
			Results.unprocessableEntity(getResponse(), "member.order.locked");
			return;
		}
		orderService.cancel(order);
		renderJson(Results.OK);
	}

	/**
	 * 收货
	 */
	@Before(Tx.class)
	public void receive() {
		String orderSn = getPara("orderSn");
		Order order = orderService.findBySn(orderSn);
		Member currentUser = memberService.getCurrentUser();
		
		if (order == null) {
			Results.notFound(getResponse(), Results.DEFAULT_NOT_FOUND_MESSAGE);
			return;
		}

		if (order.hasExpired() || !Order.Status.shipped.equals(order.getStatusName())) {
			Results.notFound(getResponse(), Results.DEFAULT_NOT_FOUND_MESSAGE);
			return;
		}
		if (!orderService.acquireLock(order, currentUser)) {
			Results.unprocessableEntity(getResponse(), "member.order.locked");
			return;
		}
		orderService.receive(order);
		renderJson(Results.OK);
	}

}