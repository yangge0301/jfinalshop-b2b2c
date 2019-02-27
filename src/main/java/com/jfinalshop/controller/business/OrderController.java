package com.jfinalshop.controller.business;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.jfinalshop.dao.OrderItemDao;
import com.jfinalshop.dao.OrderLogDao;
import com.jfinalshop.model.*;
import net.hasor.core.Inject;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.UnauthorizedException;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Pageable;
import com.jfinalshop.Results;
import com.jfinalshop.Setting;
import com.jfinalshop.entity.Invoice;
import com.jfinalshop.service.AreaService;
import com.jfinalshop.service.BusinessService;
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
@ControllerBind(controllerKey = "/business/order")
public class OrderController extends BaseController {

	@Inject
	private OrderItemDao orderItemDao;
	@Inject
	private OrderLogDao orderLogDao;
	@Inject
	private AreaService areaService;
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
	@Inject
	private BusinessService businessService;

	/**
	 * 添加属性
	 */
	public void populateModel() {
		Long orderId = getParaToLong("orderId");
		Store currentStore = businessService.getCurrentStore();
		
		Order order = orderService.find(orderId);
		if (order != null && !currentStore.equals(order.getStore())) {
			throw new UnauthorizedException();
		}
		setAttr("order", order);
	}

	/**
	 * 获取订单锁
	 */
	@ActionKey("/business/order/acquire_lock")
	public void acquireLock() {
		Long orderId = getParaToLong("orderId");
		Order order = orderService.find(orderId);
		
		Business currentUser = businessService.getCurrentUser(); 
		renderJson(order != null && orderService.acquireLock(order, currentUser));
	}

	/**
	 * 计算
	 */
	public void calculate() {
		Long orderId = getParaToLong("orderId");
		Order order = orderService.find(orderId);
		
		BigDecimal freight = new BigDecimal(getPara("freight"));
		BigDecimal tax = new BigDecimal(getPara("tax"));
		BigDecimal offsetAmount = new BigDecimal(getPara("offsetAmount"));
		
		if (order == null) {
			Results.unprocessableEntity(Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}

		Map<String, Object> data = new HashMap<>();
		data.put("amount", orderService.calculateAmount(order.getPrice(), order.getFee(), freight, tax, order.getPromotionDiscount(), order.getCouponDiscount(), offsetAmount));
		renderJson(data);
	}

	/**
	 * 物流动态
	 */
	@ActionKey("/business/order/transit_step")
	public void transitStep() {
		Long shippingId = getParaToLong("shippingId");
		Store currentStore = businessService.getCurrentStore();
		
		Map<String, Object> data = new HashMap<>();
		OrderShipping orderShipping = orderShippingService.find(shippingId);
		if (orderShipping == null || !currentStore.equals(orderShipping.getOrder().getStore())) {
			Results.unprocessableEntity(Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		Setting setting = SystemUtils.getSetting();
		if (StringUtils.isEmpty(setting.getKuaidi100Key()) || StringUtils.isEmpty(orderShipping.getDeliveryCorpCode()) || StringUtils.isEmpty(orderShipping.getTrackingNo())) {
			Results.unprocessableEntity(Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		data.put("transitSteps", orderShippingService.getTransitSteps(orderShipping));
		renderJson(data);
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long orderId = getParaToLong("orderId");
		Order order = orderService.find(orderId);
		
		if (order == null || order.hasExpired() || (!Order.Status.pendingPayment.equals(order.getStatusName()) && !Order.Status.pendingReview.equals(order.getStatusName()))) {
			setAttr("errorMessage", "订单异常!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		setAttr("paymentMethods", paymentMethodService.findAll());
		setAttr("shippingMethods", shippingMethodService.findAll());
		setAttr("order", order);
		render("/business/order/edit.ftl");
	}

	/**
	 * 更新
	 */
	@Before(Tx.class)
	public void update() {
		Long areaId = getParaToLong("areaId");
		Long paymentMethodId = getParaToLong("paymentMethodId");
		Long shippingMethodId = getParaToLong("shippingMethodId"); 
		BigDecimal freight = new BigDecimal(getPara("freight", "0"));
		BigDecimal tax = new BigDecimal(getPara("tax", "0"));
		BigDecimal offsetAmount = new BigDecimal(getPara("offsetAmount", "0"));
		Long rewardPoint = getParaToLong("rewardPoint");
		String consignee = getPara("consignee");
		String address = getPara("address");
		String zipCode = getPara("zipCode");
		String phone = getPara("phone");
		String invoiceTitle = getPara("invoiceTitle");
		String memo = getPara("memo");
		Business currentUser = businessService.getCurrentUser();
		
		Area area = areaService.find(areaId);
		PaymentMethod paymentMethod = paymentMethodService.find(paymentMethodId);
		ShippingMethod shippingMethod = shippingMethodService.find(shippingMethodId);

		Long orderId = getParaToLong("orderId");
		Order order = orderService.find(orderId);
		if (order == null || !orderService.acquireLock(order, currentUser)) {
			setAttr("errorMessage", "订单锁定中!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (order.hasExpired() || (!Order.Status.pendingPayment.equals(order.getStatusName()) && !Order.Status.pendingReview.equals(order.getStatusName()))) {
			setAttr("errorMessage", "订单异常!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		Invoice invoice = StringUtils.isNotEmpty(invoiceTitle) ? new Invoice(invoiceTitle, null) : null;
		order.setTax(invoice != null ? tax : BigDecimal.ZERO);
		order.setOffsetAmount(offsetAmount);
		order.setRewardPoint(rewardPoint);
		order.setMemo(memo);
		order.setInvoice(invoice);
		
		if (paymentMethod != null) {
			order.setPaymentMethodId(paymentMethod.getId());
			order.setPaymentMethodName(paymentMethod.getName());
			order.setPaymentMethodType(paymentMethod.getType());
		}
		if (order.getIsDelivery()) {
			order.setFreight(freight);
			order.setConsignee(consignee);
			order.setAddress(address);
			order.setZipCode(zipCode);
			order.setPhone(phone);
			order.setAreaId(area.getId());
			order.setAreaName(area.getFullName());
			order.setShippingMethodId(shippingMethod.getId());
		} else {
			order.setFreight(BigDecimal.ZERO);
			order.setConsignee(null);
			order.setAreaName(null);
			order.setAddress(null);
			order.setZipCode(null);
			order.setPhone(null);
			order.setShippingMethodName(null);
			order.setArea(null);
			order.setShippingMethod(null);
		}
		orderService.modify(order);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 查看
	 */
	public void view() {
		Long orderId = getParaToLong("orderId");
		Order order = orderService.find(orderId);
		
		if (order == null) {
			setAttr("errorMessage", "订单不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		Setting setting = SystemUtils.getSetting();
		setAttr("methods", OrderPayment.Method.values());
		setAttr("refundsMethods", OrderRefunds.Method.values());
		setAttr("paymentMethods", paymentMethodService.findAll());
		setAttr("shippingMethods", shippingMethodService.findAll());
		setAttr("deliveryCorps", deliveryCorpService.findAll());
		setAttr("isKuaidi100Enabled", StringUtils.isNotEmpty(setting.getKuaidi100Key()));
		setAttr("order", order);
		render("/business/order/view.ftl");
	}

	/**
	 * 审核
	 */
	@Before(Tx.class)
	public void review() {
		Long orderId = getParaToLong("orderId");
		Boolean passed = getParaToBoolean("passed");
		Business currentUser = businessService.getCurrentUser();
		
		Order order = orderService.find(orderId);
		if (order == null || order.hasExpired() || !Order.Status.pendingReview.equals(order.getStatusName()) || passed == null) {
			setAttr("errorMessage", "订单异常!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (!orderService.acquireLock(order, currentUser)) {
			setAttr("errorMessage", "订单锁定中!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		orderService.review(order, passed);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("view?orderId=" + order.getId());
	}

	/**
	 * 收款
	 */
	@Before(Tx.class)
	public void payment() {
		OrderPayment orderPayment = getModel(OrderPayment.class);
		Long paymentMethodId = getParaToLong("paymentMethodId");
		OrderPayment.Method method = getParaEnum(OrderPayment.Method.class, getPara("method"));
		
		Long orderId = getParaToLong("orderId");
		Order order = orderService.find(orderId);
		
		Business currentUser = businessService.getCurrentUser();
		Store currentStore = businessService.getCurrentStore();
		
		if (order == null || !Store.Type.self.equals(currentStore.getTypeName())) {
			setAttr("errorMessage", "订单为空或店铺不是自营!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		orderPayment.setMethod(method.ordinal());
		orderPayment.setOrderId(order.getId());
		orderPayment.setPaymentMethod(paymentMethodService.find(paymentMethodId));

		if (!orderService.acquireLock(order, currentUser)) {
			setAttr("errorMessage", "订单锁定中!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		orderPayment.setFee(BigDecimal.ZERO);
		orderService.payment(order, orderPayment);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("view?orderId=" + order.getId());
	}

	/**
	 * 退款
	 */
	@Before(Tx.class)
	public void refunds() {
		OrderRefunds orderRefunds = getModel(OrderRefunds.class);
		Long paymentMethodId = getParaToLong("paymentMethodId");
		OrderRefunds.Method method = getParaEnum(OrderRefunds.Method.class, getPara("method"));
		
		Business currentUser = businessService.getCurrentUser();
		
		Long orderId = getParaToLong("orderId");
		Order order = orderService.find(orderId);
		if (order == null || order.getRefundableAmount().compareTo(BigDecimal.ZERO) <= 0) {
			setAttr("errorMessage", "应退金额小于0!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		orderRefunds.setOrder(order);
		orderRefunds.setPaymentMethod(paymentMethodService.find(paymentMethodId));
		orderRefunds.setMethod(method.ordinal());

		if (OrderRefunds.Method.deposit.equals(orderRefunds.getMethodName()) && orderRefunds.getAmount().compareTo(order.getStore().getBusiness().getBalance()) > 0) {
			setAttr("errorMessage", "应退金额大于店铺金额!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (!orderService.acquireLock(order, currentUser)) {
			setAttr("errorMessage", "订单销定中!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		orderService.refunds(order, orderRefunds);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("view?orderId=" + order.getId());
	}

	/**
	 * 发货
	 */
	@Before(Tx.class)
	public void shipping() {
		OrderShipping orderShipping = getModel(OrderShipping.class);
		Long shippingMethodId = getParaToLong("shippingMethodId");
		Long deliveryCorpId = getParaToLong("deliveryCorpId");
		Long areaId = getParaToLong("areaId");
		List<OrderShippingItem> orderShippingItems = getBeans(OrderShippingItem.class, "orderShippingItems");
		orderShipping.setOrderShippingItems(orderShippingItems);
		
		Business currentUser = businessService.getCurrentUser();
		
		Long orderId = getParaToLong("orderId");
		Order order = orderService.find(orderId);
		if (order == null || order.getShippableQuantity() <= 0) {
			setAttr("errorMessage", "发货数量小于0!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
	//	boolean isDelivery = false;
		for (Iterator<OrderShippingItem> iterator = orderShipping.getOrderShippingItems().iterator(); iterator.hasNext();) {
			OrderShippingItem orderShippingItem = iterator.next();
			if (orderShippingItem == null || StringUtils.isEmpty(orderShippingItem.getSn()) || orderShippingItem.getQuantity() == null || orderShippingItem.getQuantity() <= 0) {
				iterator.remove();
				continue;
			}
			OrderItem orderItem = order.getOrderItem(orderShippingItem.getSn());
			if (orderItem == null || orderShippingItem.getQuantity() > orderItem.getShippableQuantity()) {
				setAttr("errorMessage", "发货数量大于可发货数量!");
				render(UNPROCESSABLE_ENTITY_VIEW);
				return;
			}
			Sku sku = orderItem.getSku();
			if (sku != null && orderShippingItem.getQuantity() > sku.getStock()) {
				setAttr("errorMessage", "发货数量大于库存数量!");
				render(UNPROCESSABLE_ENTITY_VIEW);
				return;
			}
			orderShippingItem.setName(orderItem.getName());
			orderShippingItem.setIsDelivery(orderItem.getIsDelivery());
			orderShippingItem.setSkuId(sku.getId());
			orderShippingItem.setOrderShipping(orderShipping);
			orderShippingItem.setSpecifications(orderItem.getSpecifications());
//			if (orderItem.getIsDelivery()) {
//				isDelivery = true;
//			}
		}
		orderShipping.setOrder(order);
		orderShipping.setShippingMethod(shippingMethodService.find(shippingMethodId));
		orderShipping.setDeliveryCorp(deliveryCorpService.find(deliveryCorpId));
		orderShipping.setArea(areaService.find(areaId));
//		if (!isDelivery) {
//			orderShipping.setShippingMethod((String) null);
//			orderShipping.setDeliveryCorp((String) null);
//			orderShipping.setDeliveryCorpUrl(null);
//			orderShipping.setDeliveryCorpCode(null);
//			orderShipping.setTrackingNo(null);
//			orderShipping.setFreight(null);
//			orderShipping.setConsignee(null);
//			orderShipping.setArea((String) null);
//			orderShipping.setAddress(null);
//			orderShipping.setZipCode(null);
//			orderShipping.setPhone(null);
//		}
		if (!orderService.acquireLock(order, currentUser)) {
			setAttr("errorMessage", "订单锁定中!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		orderService.shipping(order, orderShipping);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("view?orderId=" + order.getId());
	}

	/**
	 * 退货
	 */
	@Before(Tx.class)
	public void returns() {
		OrderReturns orderReturns = getModel(OrderReturns.class);
		Long shippingMethodId = getParaToLong("shippingMethodId");
		Long deliveryCorpId = getParaToLong("deliveryCorpId");
		Long areaId = getParaToLong("areaId");
		List<OrderReturnsItem> orderReturnsItems = getBeans(OrderReturnsItem.class, "orderReturnsItems");
		orderReturns.setOrderReturnsItems(orderReturnsItems);
		
		Business currentUser = businessService.getCurrentUser();
		
		Long orderId = getParaToLong("orderId");
		Order order = orderService.find(orderId);
		if (order == null || order.getReturnableQuantity() <= 0) {
			setAttr("errorMessage", "可退货数小于0!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		for (Iterator<OrderReturnsItem> iterator = orderReturns.getOrderReturnsItems().iterator(); iterator.hasNext();) {
			OrderReturnsItem orderReturnsItem = iterator.next();
			if (orderReturnsItem == null || StringUtils.isEmpty(orderReturnsItem.getSn()) || orderReturnsItem.getQuantity() == null || orderReturnsItem.getQuantity() <= 0) {
				iterator.remove();
				continue;
			}
			OrderItem orderItem = order.getOrderItem(orderReturnsItem.getSn());
			if (orderItem == null || orderReturnsItem.getQuantity() > orderItem.getReturnableQuantity()) {
				setAttr("errorMessage", "退货数大于可退货数!");
				render(UNPROCESSABLE_ENTITY_VIEW);
				return;
			}
			orderReturnsItem.setName(orderItem.getName());
			orderReturnsItem.setOrderReturns(orderReturns);
			orderReturnsItem.setSpecifications(orderItem.getSpecifications());
		}
		orderReturns.setOrderId(order.getId());
		orderReturns.setShippingMethod(shippingMethodService.find(shippingMethodId));
		orderReturns.setDeliveryCorp(deliveryCorpService.find(deliveryCorpId));
		orderReturns.setArea(areaService.find(areaId));
		
		if (!orderService.acquireLock(order, currentUser)) {
			setAttr("errorMessage", "订单销定中!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		orderService.returns(order, orderReturns);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("view?orderId=" + order.getId());
	}

	/**
	 * 完成
	 */
	@Before(Tx.class)
	public void complete() {
		Long orderId = getParaToLong("orderId");
		Order order = orderService.find(orderId);
		Business currentUser = businessService.getCurrentUser();
		
		if (order == null || order.hasExpired() || !Order.Status.received.equals(order.getStatusName())) {
			setAttr("errorMessage", "订单不是已收货状态!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (!orderService.acquireLock(order, currentUser)) {
			setAttr("errorMessage", "订单销定中!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		orderService.complete(order);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("view?orderId=" + order.getId());
	}

	/**
	 * 失败
	 */
	@Before(Tx.class)
	public void fail() {
		Long orderId = getParaToLong("orderId");
		Order order = orderService.find(orderId);
		Business currentUser = businessService.getCurrentUser();
		
		if (order == null || order.hasExpired() || (!Order.Status.pendingShipment.equals(order.getStatusName()) && !Order.Status.shipped.equals(order.getStatusName()) && !Order.Status.received.equals(order.getStatusName()))) {
			setAttr("errorMessage", "订单异常!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (!orderService.acquireLock(order, currentUser)) {
			setAttr("errorMessage", "订单锁定中!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		orderService.fail(order);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("view?orderId=" + order.getId());
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		String memberUsername = getPara("memberUsername");
		Boolean isPendingReceive = getParaToBoolean("isPendingReceive");
		Boolean isPendingRefunds = getParaToBoolean("isPendingRefunds");
		Boolean isAllocatedStock = getParaToBoolean("isAllocatedStock"); 
		Boolean hasExpired = getParaToBoolean("hasExpired");
		
		Order.Type type = getParaEnum(Order.Type.class, getPara("type"));
		Order.Status status = getParaEnum(Order.Status.class, getPara("status"));
		
		Store currentStore = businessService.getCurrentStore();
		
		setAttr("pageable", pageable);
		setAttr("types", Order.Type.values());
		setAttr("statuses", Order.Status.values());
		setAttr("type", type);
		setAttr("status", status);
		setAttr("memberUsername", memberUsername);
		setAttr("isPendingReceive", isPendingReceive);
		setAttr("isPendingRefunds", isPendingRefunds);
		setAttr("isAllocatedStock", isAllocatedStock);
		setAttr("hasExpired", hasExpired);

		Member member = memberService.findByUsername(memberUsername);
		if (StringUtils.isNotEmpty(memberUsername) && member == null) {
			setAttr("page", null);
		} else {
			setAttr("page", orderService.findPage(type, status, currentStore, member, null, isPendingReceive, isPendingRefunds, null, null, isAllocatedStock, hasExpired, pageable));
		}
		render("/business/order/list.ftl");
	}

	/**
	 * 删除
	 */
	@Before(Tx.class)
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		Business currentUser = businessService.getCurrentUser();
		Store currentStore = businessService.getCurrentStore();
		
		if (ids != null) {
			for (Long id : ids) {
				Order order = orderService.find(id);
				if (order == null || !currentStore.equals(order.getStore())) {
					Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
					return;
				}
				if (!orderService.acquireLock(order, currentUser)) {
					Results.unprocessableEntity(getResponse(), "business.order.deleteLockedNotAllowed", order.getSn());
					return;
				}
				if (!order.canDelete()) {
					Results.unprocessableEntity(getResponse(), "business.order.deleteStatusNotAllowed", order.getSn());
					return;
				}
			}

			if (ids != null) {
				for (Long id : ids) {
					OrderLog orderLog = orderLogDao.findOrderLog(id, OrderLog.Type.create);
					if(orderLog!=null){
						orderLogDao.remove(orderLog);
					}

					List<OrderItem> orderItems = orderItemDao.findOrderLog(id);
					if(orderItems!=null&&orderItems.size()>0){
						for (OrderItem orderItem: orderItems) {
							orderItemDao.remove(orderItem);
						}
					}
				}
			}
			orderService.delete(ids);
		}
		renderJson(Results.OK);
	}

}