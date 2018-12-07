package com.jfinalshop.api.controller.shop;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.hasor.core.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinalshop.api.common.bean.DatumResponse;
import com.jfinalshop.api.controller.BaseAPIController;
import com.jfinalshop.api.interceptor.TokenInterceptor;
import com.jfinalshop.entity.Invoice;
import com.jfinalshop.model.Cart;
import com.jfinalshop.model.CartItem;
import com.jfinalshop.model.CouponCode;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Order;
import com.jfinalshop.model.OrderItem;
import com.jfinalshop.model.PaymentMethod;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.Receiver;
import com.jfinalshop.model.ShippingMethod;
import com.jfinalshop.model.Sku;
import com.jfinalshop.plugin.PaymentPlugin;
import com.jfinalshop.service.CartService;
import com.jfinalshop.service.CouponCodeService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.OrderService;
import com.jfinalshop.service.PaymentMethodService;
import com.jfinalshop.service.PluginService;
import com.jfinalshop.service.ReceiverService;
import com.jfinalshop.service.ShippingMethodService;
import com.jfinalshop.service.SkuService;

/**
 * 
 * 移动API - 订单
 * 
 */
@ControllerBind(controllerKey = "/api/order")
@Before(TokenInterceptor.class)
public class OrderAPIController extends BaseAPIController {
	
	@Inject
	private ReceiverService receiverService;
	@Inject
	private OrderService orderService;
	@Inject
	private PaymentMethodService paymentMethodService;
	@Inject
	private ShippingMethodService shippingMethodService;
	@Inject
	private CouponCodeService couponCodeService;
	@Inject
	private SkuService skuService;
	@Inject
	private MemberService memberService;
	@Inject
	private PluginService pluginService;
	@Inject
	protected CartService cartService;
	
	private Res res = I18n.use();
	
	/**
	 * 普通订单-结算
	 * 
	 */
	public void checkout() {
		Long[] skuIds = convertToLong(getPara("skuIds"));
		String cartKey = getPara("cartKey");
		Cart currentCart = cartService.getCurrent(cartKey);
		
		if (!"post".equalsIgnoreCase(getRequest().getMethod())) {
			renderArgumentError("必须采用POST请求!");
            return;
        }
		if (currentCart == null || currentCart.isEmpty()) {
			renderArgumentError("购物车不能为空!");
			return;
		}
		if (currentCart.hasNotActive(null)) {
			renderArgumentError("存在已失效SKU!");
			return;
		}
		if (currentCart.hasNotMarketable(null)) {
			renderArgumentError(res.format("shop.order.hasNotMarketable"));
			return;
		}
		
		List<CartItem> currentCartItems = currentCart.getCartItems();
		if (CollectionUtils.isEmpty(currentCartItems)) {
			renderArgumentError("购物车项不能为空!");
			return;
		}
		
		// 根据传过来的id，从用户的购物车中查找商品
		List<Sku> skus = skuService.findList(skuIds);
		List<CartItem> checkoutCartItems = new ArrayList<CartItem>();
		for (CartItem cartItem : currentCartItems) {
			if (skus.contains(cartItem.getSku())) {
				checkoutCartItems.add(cartItem);
			}
		}
		
		Member currentUser = getMember();
		currentCart.setCartItems(checkoutCartItems);
		currentCart.setMemberId(currentUser.getId());
		cartService.update(currentCart);
		
		Receiver defaultReceiver = receiverService.findDefault(currentUser);
		List<Order> orders = orderService.generate(Order.Type.general, currentCart, defaultReceiver, null, null, null, null, null, null);

		// 缓存购物车
		CacheKit.put(Cart.CURRENT_CART_NAME, currentCart.getCartKey(), currentCart);
				
//		BigDecimal price = BigDecimal.ZERO;
//		BigDecimal fee = BigDecimal.ZERO;
//		BigDecimal freight = BigDecimal.ZERO;
//		BigDecimal tax = BigDecimal.ZERO;
		BigDecimal promotionDiscount = BigDecimal.ZERO;
		BigDecimal amount = BigDecimal.ZERO;
		BigDecimal amountPayable = BigDecimal.ZERO;
		BigDecimal couponDiscount = BigDecimal.ZERO;
		Long rewardPoint = 0L;
		Boolean isDelivery = false;

		for (Order order : orders) {
			//price = price.add(order.getPrice());
			//fee = fee.add(order.getFee());
			//freight = freight.add(order.getFreight());
			//tax = fee.add(order.getTax());
			List<OrderItem> orderItems = order.getOrderItems();
			if (CollectionUtils.isNotEmpty(orderItems)) {
				for (OrderItem orderItem : orderItems) {
					Product product = orderItem.getSku().getProduct();
					Product.Type type = product.getTypeName();
					orderItem.put("type", type);
					orderItem.put("unit", product.getUnit());
					orderItem.put("brand", product.getBrand() != null ? product.getBrand().getName() : "");
				}
			}
			order.put("orderItems", orderItems);
			order.put("store", order.getStore().getName());
			promotionDiscount = promotionDiscount.add(order.getPromotionDiscount());
			couponDiscount = couponDiscount.add(order.getCouponDiscount());
			amount = amount.add(order.getAmount());
			amountPayable = amountPayable.add(order.getAmountPayable());
			rewardPoint = rewardPoint + order.getRewardPoint();
			if (order.getIsDelivery()) {
				isDelivery = true;
			}
		}

		List<PaymentMethod> paymentMethods = paymentMethodService.findAll();
		List<PaymentMethod> availablePaymentMethods = new ArrayList<>();
		for (PaymentMethod paymentMethod : paymentMethods) {
			if (currentCart.isContainOffline()) {
				if (paymentMethod.getMethodName().equals(PaymentMethod.Method.offline)) {
					availablePaymentMethods.add(paymentMethod);
				}
			} else {
				if (paymentMethod.getMethodName().equals(PaymentMethod.Method.online)) {
					availablePaymentMethods.add(paymentMethod);
				}
			}
		}
		Map<String, Object> data = new HashMap<>();
		data.put("promotionDiscount", promotionDiscount); // 促销折扣
		data.put("couponDiscount", couponDiscount); // 优惠券折扣
		data.put("amount", amount); // 订单金额
		data.put("amountPayable", amountPayable); // 应付金额
		data.put("isDelivery", isDelivery); // 是否需要物流
		data.put("rewardPoint", rewardPoint); // 赠送积分
		data.put("orderType", Order.Type.general); // 订单类型
		data.put("orders", orders); // 订单集合
		data.put("defaultReceiver", defaultReceiver); // 默认收货地址
		data.put("cartTag", currentCart.getTag()); // 购物车标签，验证用
		data.put("cartKey", currentCart.getCartKey());
		data.put("paymentMethods", availablePaymentMethods); // 支付方式
		data.put("shippingMethods", shippingMethodService.findAll()); // 配送方式
		renderJson(new DatumResponse(data));
	}
	
	/**
	 * 创建
	 */
	@Before(Tx.class)
	public void create() {
		String cartTag = getPara("cartTag"); 
		Long receiverId = getParaToLong("receiverId");
		Long paymentMethodId = getParaToLong("paymentMethodId");
		Long shippingMethodId = getParaToLong("shippingMethodId", 1L);
		String code = getPara("code");
		String invoiceTitle = getPara("invoiceTitle");
		BigDecimal balance = new BigDecimal(getPara("balance", "0"));
		String memo = getPara("memo");
		String cartKey = getPara("cartKey");
		
		Order.Source source = getParaEnum(Order.Source.class, getPara("source"));
		if (source == null) {
			renderArgumentError("订单来源不能为空!");
			return;
		}
		
		// 从缓存取出当前用户的选中的购物车项
		Cart currentCart = CacheKit.get(Cart.CURRENT_CART_NAME, cartKey);
		Map<String, Object> data = new HashMap<>();
		if (currentCart == null || currentCart.isEmpty()) {
			renderArgumentError("购物车不能为空!");
			return;
		}
		if (!StringUtils.equals(currentCart.getTag(), cartTag)) {
			renderArgumentError(res.format("shop.order.cartHasChanged"));
			return;
		}
		if (currentCart.hasNotActive(null)) {
			renderArgumentError(res.format("shop.order.hasNotActive"));
			return;
		}
		if (currentCart.hasNotMarketable(null)) {
			renderArgumentError(res.format("shop.order.hasNotMarketable"));
			return;
		}
		if (currentCart.getIsLowStock(null)) {
			renderArgumentError(res.format("shop.order.cartLowStock"));
			return;
		}
		
		Member currentUser = getMember();
		Receiver receiver = null;
		ShippingMethod shippingMethod = null;
		PaymentMethod paymentMethod = paymentMethodService.find(paymentMethodId);
		if (currentCart.getIsDelivery(null)) {
			receiver = receiverService.find(receiverId);
			if (receiver == null || !currentUser.getId().equals(receiver.getMemberId())) {
				renderArgumentError("收货地址与用户不对应!");
				return;
			}
			shippingMethod = shippingMethodService.find(shippingMethodId);
			if (shippingMethod == null) {
				renderArgumentError("配送方式不能为空!");
				return;
			}
		}
		CouponCode couponCode = couponCodeService.findByCode(code);
		if (couponCode != null && couponCode.getCoupon() != null && !currentCart.isValid(couponCode, couponCode.getCoupon().getStore())) {
			renderArgumentError("优惠码无效!");
			return;
		}
		if (balance != null && balance.compareTo(BigDecimal.ZERO) < 0) {
			renderArgumentError("余额小于0!");
			return;
		}
		if (balance != null && balance.compareTo(currentUser.getBalance()) > 0) {
			renderArgumentError(res.format("shop.order.insufficientBalance"));
			return;
		}
		Invoice invoice = StringUtils.isNotEmpty(invoiceTitle) ? new Invoice(invoiceTitle, null) : null;
		List<Order> orders = orderService.create(Order.Type.general, source, currentCart, receiver, paymentMethod, shippingMethod, couponCode, invoice, balance, memo);
		List<String> orderSns = new ArrayList<>();
		for (Order order : orders) {
			if (order != null) {
				orderSns.add(order.getSn());
			}
		}
		data.put("orderSns", orderSns);
		renderJson(new DatumResponse(data));
	}
	
	/**
	 * 支付
	 */
	@Before(Tx.class)
	public void payment() {
		String[] orderSns = getParaValues("orderSns");
		Member currentUser = getMember();
		
		if (orderSns.length <= 0) {
			renderArgumentError("订单号不能为空!");
			return;
		}
		List<PaymentPlugin> paymentPlugins = pluginService.getActivePaymentPlugins(getRequest());
		PaymentPlugin defaultPaymentPlugin = null;
		BigDecimal amount = BigDecimal.ZERO;
		List<String> pOrderSn = new ArrayList<>();
		for (String orderSn : orderSns) {
			Order order = orderService.findBySn(orderSn);
			if (order == null) {
				renderArgumentError("订单不存在!");
				return;
			}
			if (order.getAmount().compareTo(order.getAmountPaid()) != 0) {
				if (!currentUser.getId().equals(order.getMemberId()) || order.getPaymentMethod() == null || order.getAmountPayable().compareTo(BigDecimal.ZERO) <= 0) {
					renderArgumentError("订单异常!");
					return;
				}
				if (PaymentMethod.Method.online.equals(order.getPaymentMethod().getMethodName())) {
					if (!orderService.acquireLock(order, currentUser)) {
						renderArgumentError(res.format("shop.order.locked"));
						return;
					}
					if (CollectionUtils.isNotEmpty(paymentPlugins)) {
						defaultPaymentPlugin = paymentPlugins.get(0);
						amount = amount.add(order.getAmountPayable());
					}
				} else {
					amount = amount.add(order.getAmountPayable());
				}
				pOrderSn.add(order.getSn());
			}
		}
		Map<String, Object> data = new HashMap<>();
		if (defaultPaymentPlugin != null) {
			data.put("defaultPaymentPlugin", defaultPaymentPlugin); // 默认支付插件
			data.put("paymentPlugins", paymentPlugins); // 所有支付插件
		}
		data.put("amount", amount); // 应付金额
		data.put("orderSns", pOrderSn); // 订单数组编号
		renderJson(new DatumResponse(data));
	}
}
