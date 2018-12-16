package com.jfinalshop.controller.shop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Results;
import com.jfinalshop.entity.Invoice;
import com.jfinalshop.interceptor.MobileInterceptor;
import com.jfinalshop.model.*;
import com.jfinalshop.plugin.PaymentPlugin;
import com.jfinalshop.service.*;
import net.hasor.core.Inject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller - 订单
 * 
 */
@ControllerBind(controllerKey = "/order")
public class OrderController extends BaseController {

	@Inject
	private SkuService skuService;
	@Inject
	private AreaService areaService;
	@Inject
	private ReceiverService receiverService;
	@Inject
	private PaymentMethodService paymentMethodService;
	@Inject
	private ShippingMethodService shippingMethodService;
	@Inject
	private CouponCodeService couponCodeService;
	@Inject
	private OrderService orderService;
	@Inject
	private PluginService pluginService;
	@Inject
	private CartService cartService;
	@Inject
	private MemberService memberService;

	/**
	 * 检查积分兑换
	 */
	@ActionKey("/order/check_exchange")
	public void checkExchange() {
		Long skuId = getParaToLong("skuId");
		Integer quantity = getParaToInt("quantity");
		Member currentUser = memberService.getCurrentUser();
		
		if (quantity == null || quantity < 1) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		Sku sku = skuService.find(skuId);
		if (sku == null) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		if (!Product.Type.exchange.equals(sku.getType())) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		if (!sku.getIsActive()) {
			Results.unprocessableEntity(getResponse(), "shop.order.skuNotActive");
			return;
		}
		if (!sku.getIsMarketable()) {
			Results.unprocessableEntity(getResponse(), "shop.order.skuNotMarketable");
			return;
		}
		if (quantity > sku.getAvailableStock()) {
			Results.unprocessableEntity(getResponse(), "shop.order.skuLowStock");
			return;
		}
		if (currentUser.getPoint() < sku.getExchangePoint() * quantity) {
			Results.unprocessableEntity(getResponse(), "shop.order.lowPoint");
			return;
		}
		renderJson(Results.OK);
	}

	/**
	 * 获取收货地址
	 */
	@ActionKey("/order/receiver_list")
	public void receiverList() {
		Member currentUser = memberService.getCurrentUser();
		List<Receiver> list = receiverService.findList(currentUser);
		List<Receiver> list1 = new ArrayList<Receiver>();
		if(list!=null&&list.size()>0){
			for(int i=0;i<list.size();i++){
				Receiver r=list.get(i);
				r.setMid(list.get(i).getMemberId()+"");
				r.setCid(list.get(i).getId()+"");
				list1.add(r);
			}
		}
		renderJson(list1);
	}

	/**
	 * 保存收货地址
	 */
	@ActionKey("/order/save_receiver")
	public void saveReceiver() {
		Receiver receiver = getModel(Receiver.class);
		Long areaId = getParaToLong("areaId");
		Boolean isDefault = getParaToBoolean("isDefault", false);
		String consignee = getPara("consignee");
		String phone = getPara("phone");
		String zipCode = getPara("zipCode");
		Member currentUser = memberService.getCurrentUser();
		Area area = areaService.find(areaId);
		receiver.setAreaId(area.getId());
		receiver.setZipCode(zipCode);
		receiver.setConsignee(consignee);
		receiver.setPhone(phone);
		receiver.setIsDefault(isDefault);
		if (Receiver.MAX_RECEIVER_COUNT != null && currentUser.getReceivers().size() >= Receiver.MAX_RECEIVER_COUNT) {
			Results.unprocessableEntity(getResponse(), "shop.order.addReceiverCountNotAllowed", Receiver.MAX_RECEIVER_COUNT);
		}
		if (area != null) {
			receiver.setAreaName(area.getFullName());
		}
		receiver.setMemberId(currentUser.getId());
		renderJson(receiverService.save(receiver));
	}

	/**
	 * 订单锁定
	 */
	public void lock() {
		String[] orderSns = getParaValues("orderSns");
		Member currentUser = memberService.getCurrentUser();
		
		for (String orderSn : orderSns) {
			Order order = orderService.findBySn(orderSn);
			if (order != null && currentUser.equals(order.getMember()) && order.getPaymentMethod() != null && PaymentMethod.Method.online.equals(order.getPaymentMethod().getMethod()) && order.getAmountPayable().compareTo(BigDecimal.ZERO) > 0) {
				orderService.acquireLock(order, currentUser);
			}
		}
	}

	/**
	 * 检查等待付款
	 */
	@ActionKey("/order/check_pending_payment")
	public void checkPendingPayment() {
		String[] orderSns = getParaValues("orderSns");
		Member currentUser = memberService.getCurrentUser();
		boolean flag = false;
		for (String orderSn : orderSns) {
			Order order = orderService.findBySn(orderSn);
			flag = order != null && currentUser.getId().equals(order.getMember().getId()) && order.getPaymentMethod() != null && PaymentMethod.Method.online.equals(order.getPaymentMethod().getMethodName()) && order.getAmountPayable().compareTo(BigDecimal.ZERO) > 0;
		}
		renderJson(flag);
	}

	/**
	 * 检查优惠券
	 */
	@ActionKey("/order/check_coupon")
	public void checkCoupon() {
		String code = getPara("code");
		Cart currentCart = cartService.getCurrent(getRequest());
		
		Map<String, Object> data = new HashMap<>();
		if (currentCart == null || currentCart.isEmpty()) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		CouponCode couponCode = couponCodeService.findByCode(code);
		if (couponCode != null && couponCode.getCoupon() != null) {
			Coupon coupon = couponCode.getCoupon();
			Store store = coupon.getStore();
			if (couponCode.getIsUsed()) {
				Results.unprocessableEntity(getResponse(), "shop.order.couponCodeUsed");
				return;
			}
			if (!coupon.getIsEnabled()) {
				Results.unprocessableEntity(getResponse(), "shop.order.couponDisabled");
				return;
			}
			if (!coupon.hasBegun()) {
				Results.unprocessableEntity(getResponse(), "shop.order.couponNotBegin");
				return;
			}
			if (coupon.hasExpired()) {
				Results.unprocessableEntity(getResponse(), "shop.order.couponHasExpired");
				return;
			}
			if (!currentCart.isValid(coupon, store)) {
				Results.unprocessableEntity(getResponse(), "shop.order.couponInvalid");
				return;
			}
			if (currentCart.getStores().contains(store) && !currentCart.isCouponAllowed(store)) {
				Results.unprocessableEntity(getResponse(), "shop.order.couponNotAllowed");
				return;
			}
			data.put("couponName", coupon.getName());
			renderJson(data);
		} else {
			Results.unprocessableEntity(getResponse(), "shop.order.couponCodeNotExist");
		}
	}

	/**
	 * 结算入口
	 */
	@Before(MobileInterceptor.class)
	public void checkout() {
		Product.Type type = getParaEnum(Product.Type.class, getPara("type"));
		if (Product.Type.exchange.equals(type)) {
			checkoutExchange();
		} else {
			checkoutGeneral();
		}
	}

	/**
	 * 结算
	 */
	private void checkoutGeneral() {
		Member currentUser = memberService.getCurrentUser();
		Cart currentCart = cartService.getCurrent(getRequest());
		
		if (currentCart == null || currentCart.isEmpty()) {
			redirect("/cart/list");
			return;
		}
		if (currentCart.hasNotActive(null)) {
			addFlashMessage("shop.order.hasNotActive");
			redirect("/cart/list");
			return;
		}
		if (currentCart.hasNotMarketable(null)) {
			addFlashMessage("shop.order.hasNotMarketable");
			redirect("/cart/list");
			return;
		}
		Receiver defaultReceiver = receiverService.findDefault(currentUser);
		List<Order> orders = orderService.generate(Order.Type.general, currentCart, defaultReceiver, null, null, null, null, null, null);

		BigDecimal price = BigDecimal.ZERO;
		BigDecimal fee = BigDecimal.ZERO;
		BigDecimal freight = BigDecimal.ZERO;
		BigDecimal tax = BigDecimal.ZERO;
		BigDecimal promotionDiscount = BigDecimal.ZERO;
		BigDecimal amount = BigDecimal.ZERO;
		BigDecimal amountPayable = BigDecimal.ZERO;
		BigDecimal couponDiscount = BigDecimal.ZERO;
		Long rewardPoint = 0L;
		Boolean isDelivery = false;

		for (Order order : orders) {
			price = price.add(order.getPrice());
			fee = fee.add(order.getFee());
			freight = freight.add(order.getFreight());
			tax = fee.add(order.getTax());
			promotionDiscount = promotionDiscount.add(order.getPromotionDiscount());
			couponDiscount = couponDiscount.add(order.getCouponDiscount());
			amount = amount.add(order.getAmount());
			amountPayable = amountPayable.add(order.getAmountPayable());
			rewardPoint = rewardPoint + order.getRewardPoint();
			if (order.getIsDelivery()) {
				isDelivery = true;
			}
		}

		setAttr("price", price);
		setAttr("fee", fee);
		setAttr("freight", freight);
		setAttr("tax", tax);
		setAttr("promotionDiscount", promotionDiscount);
		setAttr("couponDiscount", couponDiscount);
		setAttr("amount", amount);
		setAttr("amountPayable", amountPayable);
		setAttr("isDelivery", isDelivery);
		setAttr("rewardPoint", rewardPoint);
		setAttr("orderType", Order.Type.general);
		setAttr("orders", orders);
		setAttr("defaultReceiver", defaultReceiver);
		setAttr("cartTag", currentCart.getTag());
		List<PaymentMethod> paymentMethods = paymentMethodService.findAll();
		List<PaymentMethod> availablePaymentMethods = new ArrayList<>();
		for (PaymentMethod paymentMethod : paymentMethods) {
			if (currentCart.isContainGeneral()) {
				if (paymentMethod.getMethodName().equals(PaymentMethod.Method.online)) {
					availablePaymentMethods.add(paymentMethod);
				}
			} else {
				availablePaymentMethods.add(paymentMethod);
			}
		}
		setAttr("member", currentUser);
		setAttr("paymentMethods", availablePaymentMethods);
		setAttr("shippingMethods", shippingMethodService.findAll());
		render("/shop/order/checkout.ftl");
	}

	/**
	 * 结算-兑换订单
	 */
	private void checkoutExchange() {
		Long skuId = getParaToLong("skuId");
		Integer quantity = getParaToInt("quantity");
		Member currentUser = memberService.getCurrentUser();
		
		if (quantity == null || quantity < 1) {
			setAttr("errorMessage", "数量小于1!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		Sku sku = skuService.find(skuId);
		if (sku == null) {
			setAttr("errorMessage", "SKU为空!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (!Product.Type.exchange.equals(sku.getType())) {
			setAttr("errorMessage", "SKU是兑换商品!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (!sku.getIsActive()) {
			setAttr("errorMessage", "SKU不是有效商品!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (!sku.getIsMarketable()) {
			setAttr("errorMessage", "SKU没有上架!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (quantity > sku.getAvailableStock()) {
			setAttr("errorMessage", "SKU没有可用库存!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (currentUser.getPoint() < sku.getExchangePoint() * quantity) {
			setAttr("errorMessage", "积分不足!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		List<CartItem> cartItems = new ArrayList<>();
		CartItem cartItem = new CartItem();
		cartItem.setSku(sku);
		cartItem.setQuantity(quantity);
		cartItems.add(cartItem);
		Cart cart = new Cart();
		cart.setMember(currentUser);
		cart.setCartItems(cartItems);
		Receiver defaultReceiver = receiverService.findDefault(currentUser);
		List<Order> orders = orderService.generate(Order.Type.exchange, cart, defaultReceiver, null, null, null, null, null, null);

		Long exchangePoint = 0L;
		Long rewardPoint = 0L;
		Boolean isDelivery = false;
		BigDecimal fee = BigDecimal.ZERO;
		BigDecimal freight = BigDecimal.ZERO;
		BigDecimal tax = BigDecimal.ZERO;
		BigDecimal promotionDiscount = BigDecimal.ZERO;
		BigDecimal amount = BigDecimal.ZERO;
		BigDecimal amountPayable = BigDecimal.ZERO;
		BigDecimal couponDiscount = BigDecimal.ZERO;

		for (Order order : orders) {
			exchangePoint = exchangePoint + order.getExchangePoint();
			rewardPoint = rewardPoint + order.getRewardPoint();
			fee = fee.add(order.getFee());
			freight = freight.add(order.getFreight());
			tax = fee.add(order.getTax());
			promotionDiscount = promotionDiscount.add(order.getPromotionDiscount());
			couponDiscount = couponDiscount.add(order.getCouponDiscount());
			amount = amount.add(order.getAmount());
			amountPayable = amountPayable.add(order.getAmountPayable());
			if (order.getIsDelivery()) {
				isDelivery = true;
			}
		}

		setAttr("orders", orders);
		setAttr("exchangePoint", exchangePoint);
		setAttr("rewardPoint", rewardPoint);
		setAttr("fee", fee);
		setAttr("freight", freight);
		setAttr("tax", tax);
		setAttr("promotionDiscount", promotionDiscount);
		setAttr("couponDiscount", couponDiscount);
		setAttr("amount", amount);
		setAttr("amountPayable", amountPayable);
		setAttr("skuId", skuId);
		setAttr("quantity", quantity);
		setAttr("isDelivery", isDelivery);
		setAttr("orderType", Order.Type.exchange);
		setAttr("defaultReceiver", defaultReceiver);
		setAttr("paymentMethods", paymentMethodService.findAll());
		List<PaymentMethod> paymentMethods = paymentMethodService.findAll();
		List<PaymentMethod> availablePaymentMethods = new ArrayList<>();
		for (PaymentMethod paymentMethod : paymentMethods) {
			if (cart.isContainGeneral()) {
				if (paymentMethod.getMethod().equals(PaymentMethod.Method.online)) {
					availablePaymentMethods.add(paymentMethod);
				}
			} else {
				availablePaymentMethods.add(paymentMethod);
			}
		}
		setAttr("shippingMethods", shippingMethodService.findAll());
		render("/shop/order/checkout.ftl");
	}

	/**
	 * 计算入口
	 */
	@Before(MobileInterceptor.class)
	public void calculate() {
		Product.Type type = getParaEnum(Product.Type.class, getPara("type"));
		if (Product.Type.exchange.equals(type)) {
			calculateExchange();
		} else {
			calculateGeneral();
		}
	}

	/**
	 * 计算
	 */
	private void calculateGeneral() {
		Long receiverId = getParaToLong("receiverId");
		Long paymentMethodId = getParaToLong("paymentMethodId");
		Long shippingMethodId = getParaToLong("shippingMethodId");
		String code = getPara("code");
		String invoiceTitle = getPara("invoiceTitle");
		BigDecimal balance = new BigDecimal(getPara("balance", "0"));
		String memo = getPara("memo");
		Member currentUser = memberService.getCurrentUser();
		Cart currentCart = cartService.getCurrent(getRequest());
		
		Map<String, Object> data = new HashMap<>();
		if (currentCart == null || currentCart.isEmpty()) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		Receiver receiver = receiverService.find(receiverId);
		if (receiver != null && !currentUser.equals(receiver.getMember())) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		if (balance != null && balance.compareTo(BigDecimal.ZERO) < 0) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		if (balance != null && balance.compareTo(currentUser.getBalance()) > 0) {
			Results.unprocessableEntity(getResponse(), "shop.order.insufficientBalance");
			return;
		}

		PaymentMethod paymentMethod = paymentMethodService.find(paymentMethodId);
		ShippingMethod shippingMethod = shippingMethodService.find(shippingMethodId);
		CouponCode couponCode = couponCodeService.findByCode(code);
		Invoice invoice = StringUtils.isNotEmpty(invoiceTitle) ? new Invoice(invoiceTitle, null) : null;
		List<Order> orders = orderService.generate(Order.Type.general, currentCart, receiver, paymentMethod, shippingMethod, couponCode, invoice, balance, memo);

		BigDecimal price = BigDecimal.ZERO;
		BigDecimal fee = BigDecimal.ZERO;
		BigDecimal freight = BigDecimal.ZERO;
		BigDecimal tax = BigDecimal.ZERO;
		BigDecimal promotionDiscount = BigDecimal.ZERO;
		BigDecimal amount = BigDecimal.ZERO;
		BigDecimal amountPayable = BigDecimal.ZERO;
		BigDecimal couponDiscount = BigDecimal.ZERO;

		for (Order order : orders) {
			price = price.add(order.getPrice());
			fee = fee.add(order.getFee());
			freight = freight.add(order.getFreight());
			tax = fee.add(order.getTax());
			promotionDiscount = promotionDiscount.add(order.getPromotionDiscount());
			couponDiscount = couponDiscount.add(order.getCouponDiscount());
			amount = amount.add(order.getAmount());
			amountPayable = amountPayable.add(order.getAmountPayable());
		}

		data.put("price", price);
		data.put("fee", fee);
		data.put("freight", freight);
		data.put("tax", tax);
		data.put("promotionDiscount", promotionDiscount);
		data.put("couponDiscount", couponDiscount);
		data.put("amount", amount);
		data.put("amountPayable", amountPayable);
		renderJson(data);
	}

	/**
	 * 计算
	 */
	private void calculateExchange() {
		Long skuId = getParaToLong("skuId");
		Integer quantity = getParaToInt("quantity");
		Long receiverId = getParaToLong("receiverId");
		Long paymentMethodId = getParaToLong("paymentMethodId");
		Long shippingMethodId = getParaToLong("shippingMethodId");
		BigDecimal balance = new BigDecimal(getPara("balance", "0"));
		//String memo = getPara("memo");
		Member currentUser = memberService.getCurrentUser();
		
		Map<String, Object> data = new HashMap<>();
		if (quantity == null || quantity < 1) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		Sku sku = skuService.find(skuId);
		if (sku == null) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		Receiver receiver = receiverService.find(receiverId);
		if (receiver != null && !currentUser.equals(receiver.getMember())) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		if (balance != null && balance.compareTo(BigDecimal.ZERO) < 0) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		if (balance != null && balance.compareTo(currentUser.getBalance()) > 0) {
			Results.unprocessableEntity(getResponse(), "shop.order.insufficientBalance");
		}
		PaymentMethod paymentMethod = paymentMethodService.find(paymentMethodId);
		ShippingMethod shippingMethod = shippingMethodService.find(shippingMethodId);
		List<CartItem> cartItems = new ArrayList<>();
		CartItem cartItem = new CartItem();
		cartItem.setSku(sku);
		cartItem.setQuantity(quantity);
		cartItems.add(cartItem);
		Cart cart = new Cart();
		cart.setMemberId(currentUser.getId());
		cart.setCartItems(cartItems);
		List<Order> orders = orderService.generate(Order.Type.general, cart, receiver, paymentMethod, shippingMethod, null, null, balance, null);
		BigDecimal price = BigDecimal.ZERO;
		BigDecimal fee = BigDecimal.ZERO;
		BigDecimal freight = BigDecimal.ZERO;
		BigDecimal tax = BigDecimal.ZERO;
		BigDecimal promotionDiscount = BigDecimal.ZERO;
		BigDecimal amount = BigDecimal.ZERO;
		BigDecimal amountPayable = BigDecimal.ZERO;
		BigDecimal couponDiscount = BigDecimal.ZERO;

		for (Order order : orders) {
			price = price.add(order.getPrice());
			fee = fee.add(order.getFee());
			freight = freight.add(order.getFreight());
			tax = fee.add(order.getTax());
			promotionDiscount = promotionDiscount.add(order.getPromotionDiscount());
			couponDiscount = couponDiscount.add(order.getCouponDiscount());
			amount = amount.add(order.getAmount());
			amountPayable = amountPayable.add(order.getAmountPayable());
		}

		data.put("price", price);
		data.put("fee", fee);
		data.put("freight", freight);
		data.put("tax", tax);
		data.put("promotionDiscount", promotionDiscount);
		data.put("couponDiscount", couponDiscount);
		data.put("amount", amount);
		data.put("amountPayable", amountPayable);
		renderJson(data);
	}

	/**
	 * 创建入口
	 */
	public void create() {
		Product.Type type = getParaEnum(Product.Type.class, getPara("type"));
		if (Product.Type.exchange.equals(type)) {
			createExchange();
		} else {
			createGeneral();
		}
	}

	/**
	 * 创建
	 */
	@Before({Tx.class,MobileInterceptor.class})
	private void createGeneral() {
		String cartTag = getPara("cartTag"); 
		Long receiverId = getParaToLong("receiverId");
		Long paymentMethodId = getParaToLong("paymentMethodId");
		Long shippingMethodId = getParaToLong("shippingMethodId");
		String code = getPara("code");
		String invoiceTitle = getPara("invoiceTitle");
		BigDecimal balance = new BigDecimal(getPara("balance", "0"));
		String memo = getPara("memo");
		Member currentUser = memberService.getCurrentUser();
		Cart currentCart = cartService.getCurrent(getRequest());
		
		Map<String, Object> data = new HashMap<>();
		if (currentCart == null || currentCart.isEmpty()) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		if (!StringUtils.equals(currentCart.getTag(), cartTag)) {
			Results.unprocessableEntity(getResponse(), "shop.order.cartHasChanged");
			return;
		}
		if (currentCart.hasNotActive(null)) {
			Results.unprocessableEntity(getResponse(), "shop.order.hasNotActive");
			return;
		}
		if (currentCart.hasNotMarketable(null)) {
			Results.unprocessableEntity(getResponse(), "shop.order.hasNotMarketable");
			return;
		}
		if (currentCart.getIsLowStock(null)) {
			Results.unprocessableEntity(getResponse(), "shop.order.cartLowStock");
			return;
		}
		Receiver receiver = null;
		ShippingMethod shippingMethod = null;
		PaymentMethod paymentMethod = paymentMethodService.find(paymentMethodId);
		if (currentCart.getIsDelivery(null)) {
			receiver = receiverService.find(receiverId);
			if (receiver == null || !currentUser.equals(receiver.getMember())) {
				Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
				return;
			}
			shippingMethod = shippingMethodService.find(shippingMethodId);
			if (shippingMethod == null) {
				Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
				return;
			}
		}
		CouponCode couponCode = couponCodeService.findByCode(code);
		if (couponCode != null && couponCode.getCoupon() != null && !currentCart.isValid(couponCode, couponCode.getCoupon().getStore())) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		if (balance != null && balance.compareTo(BigDecimal.ZERO) < 0) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		if (balance != null && balance.compareTo(currentUser.getBalance()) > 0) {
			Results.unprocessableEntity(getResponse(), "shop.order.insufficientBalance");
			return;
		}
		Invoice invoice = StringUtils.isNotEmpty(invoiceTitle) ? new Invoice(invoiceTitle, null) : null;
		List<Order> orders = orderService.create(Order.Type.general, Order.Source.PC, currentCart, receiver, paymentMethod, shippingMethod, couponCode, invoice, balance, memo);
		List<String> orderSns = new ArrayList<>();
		for (Order order : orders) {
			if (order != null) {
				orderSns.add(order.getSn());
			}
		}
		data.put("orderSns", orderSns);
		renderJson(data);
	}

	/**
	 * 创建
	 */
	@Before({Tx.class,MobileInterceptor.class})
	private void createExchange() {
		Long skuId = getParaToLong("skuId");
		Integer quantity = getParaToInt("quantity");
		Long receiverId = getParaToLong("receiverId");
		Long paymentMethodId = getParaToLong("paymentMethodId");
		Long shippingMethodId = getParaToLong("shippingMethodId");
		BigDecimal balance = new BigDecimal(getPara("balance", "0"));
		String memo = getPara("memo");
		Member currentUser = memberService.getCurrentUser();
		
		Map<String, Object> data = new HashMap<>();
		if (quantity == null || quantity < 1) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		Sku sku = skuService.find(skuId);
		if (sku == null) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		if (!Product.Type.exchange.equals(sku.getType())) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		if (!sku.getIsActive()) {
			Results.unprocessableEntity(getResponse(), "shop.order.skuNotActive");
			return;
		}
		if (!sku.getIsMarketable()) {
			Results.unprocessableEntity(getResponse(), "shop.order.skuNotMarketable");
			return;
		}
		if (quantity > sku.getAvailableStock()) {
			Results.unprocessableEntity(getResponse(), "shop.order.skuLowStock");
			return;
		}
		Receiver receiver = null;
		ShippingMethod shippingMethod = null;
		PaymentMethod paymentMethod = paymentMethodService.find(paymentMethodId);
		if (sku.getIsDelivery()) {
			receiver = receiverService.find(receiverId);
			if (receiver == null || !currentUser.equals(receiver.getMember())) {
				Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
				return;
			}
			shippingMethod = shippingMethodService.find(shippingMethodId);
			if (shippingMethod == null) {
				Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
				return;
			}
		}
		if (currentUser.getPoint() < sku.getExchangePoint() * quantity) {
			Results.unprocessableEntity(getResponse(), "shop.order.lowPoint");
			return;
		}
		if (balance != null && balance.compareTo(currentUser.getBalance()) > 0) {
			Results.unprocessableEntity(getResponse(), "shop.order.insufficientBalance");
			return;
		}

		Cart currentCart = cartService.create();
		cartService.add(currentCart, sku, quantity);

		List<String> orderSns = new ArrayList<>();
		List<Order> orders = orderService.create(Order.Type.exchange, Order.Source.PC, currentCart, receiver, paymentMethod, shippingMethod, null, null, balance, memo);
		for (Order order : orders) {
			if (order != null) {
				orderSns.add(order.getSn());
			}
		}
		data.put("orderSns", orderSns);
		renderJson(data);
	}

	/**
	 * 支付
	 */
	@Before(MobileInterceptor.class)
	public void payment() {
		String[] orderSns = StringUtils.split(getPara("orderSns"), ",");
		Member currentUser = memberService.getCurrentUser();
		
		if (orderSns.length <= 0) {
			setAttr("errorMessage", "订单号不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		List<PaymentPlugin> paymentPlugins = pluginService.getActivePaymentPlugins(getRequest());
		PaymentPlugin defaultPaymentPlugin = null;
		BigDecimal fee = BigDecimal.ZERO;
		BigDecimal amount = BigDecimal.ZERO;
		List<String> pOrderSn = new ArrayList<>();
		boolean online = false;
		List<Order> orders = new ArrayList<>();
		for (String orderSn : orderSns) {
			Order order = orderService.findBySn(orderSn);
			if (order == null) {
				setAttr("errorMessage", "订单不存在!");
				render(UNPROCESSABLE_ENTITY_VIEW);
				return;
			}
			if (order.getAmount().compareTo(order.getAmountPaid()) != 0) {
				if (!currentUser.equals(order.getMember()) || order.getPaymentMethod() == null || order.getAmountPayable().compareTo(BigDecimal.ZERO) <= 0) {
					setAttr("errorMessage", "订单异常!");
					render(UNPROCESSABLE_ENTITY_VIEW);
					return;
				}
				if (PaymentMethod.Method.online.equals(order.getPaymentMethod().getMethodName())) {
					if (!orderService.acquireLock(order, currentUser)) {
						addFlashMessage("shop.order.locked");
						redirect("/member/order/list");
						return;
					}
					if (CollectionUtils.isNotEmpty(paymentPlugins)) {
						defaultPaymentPlugin = paymentPlugins.get(0);
						amount = amount.add(order.getAmountPayable());
					}
					online = true;
				} else {
					amount = amount.add(order.getAmountPayable());
					fee = fee.add(order.getFee());
					online = false;
				}
				pOrderSn.add(order.getSn());
				orders.add(order);
			}
		}
		if (defaultPaymentPlugin != null) {
			amount = defaultPaymentPlugin.calculateFee(amount).add(amount);
			setAttr("fee", defaultPaymentPlugin.calculateFee(amount));
			setAttr("online", online);
			setAttr("fee", fee);
			setAttr("defaultPaymentPlugin", defaultPaymentPlugin);
			setAttr("paymentPlugins", paymentPlugins);
		}
		setAttr("fee", online && defaultPaymentPlugin != null ? defaultPaymentPlugin.calculateFee(amount) : fee);
		setAttr("amount", amount);
		setAttr("shippingMethodName", orders.get(0).getShippingMethodName());
		setAttr("paymentMethodName", orders.get(0).getPaymentMethodName());
		setAttr("paymentMethod", orders.get(0).getPaymentMethod());
		setAttr("expireDate", orders.get(0).getExpire());
		setAttr("orders", orders);
		setAttr("orderSns", pOrderSn);
		render("/shop/order/payment.ftl");
	}

	/**
	 * 计算支付金额
	 */
	@ActionKey("/order/calculate_amount")
	public void calculateAmount() {
		String paymentPluginId = getPara("paymentPluginId");
		String[] orderSns = StringUtils.split(getPara("orderSns"), ",");
		Member currentUser = memberService.getCurrentUser();
		
		Map<String, Object> data = new HashMap<>();
		if (orderSns.length <= 0) {
			setAttr("errorMessage", "订单号不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin(paymentPluginId);
		BigDecimal amount = BigDecimal.ZERO;
		for (String orderSn : orderSns) {
			Order order = orderService.findBySn(orderSn);
			if (order == null || !currentUser.equals(order.getMember()) || paymentPlugin == null || !paymentPlugin.getIsEnabled()) {
				setAttr("errorMessage", "订单异常!");
				render(UNPROCESSABLE_ENTITY_VIEW);
				return;
			}
			amount = amount.add(order.getAmountPayable());
		}
		BigDecimal fee = paymentPlugin.calculateFee(amount);
		data.put("fee", fee);
		data.put("amount", amount.add(fee));
		renderJson(data);
	}

}