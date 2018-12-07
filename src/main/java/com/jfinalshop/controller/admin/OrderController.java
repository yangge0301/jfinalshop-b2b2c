package com.jfinalshop.controller.admin;

import java.util.HashMap;
import java.util.Map;

import net.hasor.core.Inject;

import org.apache.commons.lang.StringUtils;

import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.Setting;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Order;
import com.jfinalshop.model.OrderPayment;
import com.jfinalshop.model.OrderRefunds;
import com.jfinalshop.model.OrderShipping;
import com.jfinalshop.service.DeliveryCorpService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.OrderService;
import com.jfinalshop.service.OrderShippingService;
import com.jfinalshop.service.PaymentMethodService;
import com.jfinalshop.service.ShippingMethodService;
import com.jfinalshop.util.SystemUtils;

/**
 * Controller - 订单
 * 
 */
@ControllerBind(controllerKey = "/admin/order")
public class OrderController extends BaseController {

	@Inject
	private OrderService orderService;
	@Inject
	private ShippingMethodService shippingMethodService;
	@Inject
	private PaymentMethodService paymentMethodService;
	@Inject
	private DeliveryCorpService deliveryCorpService;
	@Inject
	private OrderShippingService orderShippingService;
	@Inject
	private MemberService memberService;

	/**
	 * 物流动态
	 */
	@ActionKey("/admin/order/transit_step")
	public void transitStep() {
		Long shippingId = getParaToLong("shippingId");
		Map<String, Object> data = new HashMap<>();
		OrderShipping orderShipping = orderShippingService.find(shippingId);
		if (orderShipping == null) {
			data.put("message", ERROR_MESSAGE);
			renderJson(data);
			return;
		}
		Setting setting = SystemUtils.getSetting();
		if (StringUtils.isEmpty(setting.getKuaidi100Key()) || StringUtils.isEmpty(orderShipping.getDeliveryCorpCode()) || StringUtils.isEmpty(orderShipping.getTrackingNo())) {
			data.put("message", ERROR_MESSAGE);
			renderJson(data);
			return;
		}
		data.put("message", SUCCESS_MESSAGE);
		data.put("transitSteps", orderShippingService.getTransitSteps(orderShipping));
		renderJson(data);
	}

	/**
	 * 查看
	 */
	public void view() {
		Long id = getParaToLong("id");
		Setting setting = SystemUtils.getSetting();
		setAttr("methods", OrderPayment.Method.values());
		setAttr("refundsMethods", OrderRefunds.Method.values());
		setAttr("paymentMethods", paymentMethodService.findAll());
		setAttr("shippingMethods", shippingMethodService.findAll());
		setAttr("deliveryCorps", deliveryCorpService.findAll());
		setAttr("isKuaidi100Enabled", StringUtils.isNotEmpty(setting.getKuaidi100Key()));
		setAttr("order", orderService.find(id));
		render("/admin/order/view.ftl");
	}

	/**
	 * 列表
	 */
	public void list() {
		Order.Type type = getParaEnum(Order.Type.class, getPara("type"));
		Order.Status status = getParaEnum(Order.Status.class, getPara("status"));
		
		String memberUsername = getPara("memberUsername");
		Boolean isPendingReceive = getParaToBoolean("isPendingReceive");
		Boolean isPendingRefunds = getParaToBoolean("isPendingRefunds");
		Boolean isAllocatedStock = getParaToBoolean("isAllocatedStock");
		Boolean hasExpired = getParaToBoolean("hasExpired");
		Pageable pageable = getBean(Pageable.class);
		
		setAttr("types", Order.Type.values());
		setAttr("statuses", Order.Status.values());
		setAttr("type", type);
		setAttr("status", status);
		setAttr("memberUsername", memberUsername);
		setAttr("isPendingReceive", isPendingReceive);
		setAttr("isPendingRefunds", isPendingRefunds);
		setAttr("isAllocatedStock", isAllocatedStock);
		setAttr("hasExpired", hasExpired);
		setAttr("pageable", pageable);

		Member member = memberService.findByUsername(memberUsername);
		if (StringUtils.isNotEmpty(memberUsername) && member == null) {
			setAttr("page", null);
		} else {
			setAttr("page", orderService.findPage(type, status, null, member, null, isPendingReceive, isPendingRefunds, null, null, isAllocatedStock, hasExpired, pageable));
		}
		render("/admin/order/list.ftl");
	}

}