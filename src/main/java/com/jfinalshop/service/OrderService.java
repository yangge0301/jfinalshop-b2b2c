package com.jfinalshop.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinalshop.Filter;
import com.jfinalshop.Pageable;
import com.jfinalshop.Setting;
import com.jfinalshop.dao.CartDao;
import com.jfinalshop.dao.OrderDao;
import com.jfinalshop.dao.OrderItemDao;
import com.jfinalshop.dao.OrderLogDao;
import com.jfinalshop.dao.OrderPaymentDao;
import com.jfinalshop.dao.OrderRefundsDao;
import com.jfinalshop.dao.OrderReturnsDao;
import com.jfinalshop.dao.OrderReturnsItemDao;
import com.jfinalshop.dao.OrderShippingDao;
import com.jfinalshop.dao.OrderShippingItemDao;
import com.jfinalshop.dao.PaymentMethodDao;
import com.jfinalshop.dao.SnDao;
import com.jfinalshop.entity.Invoice;
import com.jfinalshop.model.Area;
import com.jfinalshop.model.Business;
import com.jfinalshop.model.BusinessDepositLog;
import com.jfinalshop.model.Cart;
import com.jfinalshop.model.CartItem;
import com.jfinalshop.model.Coupon;
import com.jfinalshop.model.CouponCode;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.MemberDepositLog;
import com.jfinalshop.model.Order;
import com.jfinalshop.model.OrderItem;
import com.jfinalshop.model.OrderLog;
import com.jfinalshop.model.OrderPayment;
import com.jfinalshop.model.OrderRefunds;
import com.jfinalshop.model.OrderReturns;
import com.jfinalshop.model.OrderReturnsItem;
import com.jfinalshop.model.OrderShipping;
import com.jfinalshop.model.OrderShippingItem;
import com.jfinalshop.model.PaymentMethod;
import com.jfinalshop.model.PointLog;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.Receiver;
import com.jfinalshop.model.ShippingMethod;
import com.jfinalshop.model.Sku;
import com.jfinalshop.model.Sn;
import com.jfinalshop.model.StockLog;
import com.jfinalshop.model.Store;
import com.jfinalshop.util.Assert;
import com.jfinalshop.util.SystemUtils;
import com.xiaoleilu.hutool.util.CollectionUtil;

/**
 * Service - 订单
 * 
 */
@Singleton
public class OrderService extends BaseService<Order> {

	/**
	 * 构造方法
	 */
	public OrderService() {
		super(Order.class);
	}
	
	private CacheManager cacheManager = CacheKit.getCacheManager();
	@Inject
	private OrderDao orderDao;
	@Inject
	private OrderItemDao orderItemDao;
	@Inject
	private OrderLogDao orderLogDao;
	@Inject
	private CartDao cartDao;
	@Inject
	private SnDao snDao;
	@Inject
	private OrderPaymentDao orderPaymentDao;
	@Inject
	private OrderRefundsDao orderRefundsDao;
	@Inject
	private OrderShippingDao orderShippingDao;
	@Inject
	private OrderShippingItemDao orderShippingItemDao;
	@Inject
	private OrderReturnsDao orderReturnsDao;
	@Inject
	private OrderReturnsItemDao orderReturnsItemDao;
	@Inject
	private MemberService memberService;
	@Inject
	private BusinessService businessService;
	@Inject
	private CouponCodeService couponCodeService;
	@Inject
	private ProductService productService;
	@Inject
	private SkuService skuService;
	@Inject
	private ShippingMethodService shippingMethodService;
	@Inject
	private PaymentMethodDao paymentMethodDao;
//	@Inject
//	private MailService mailService;
//	@Inject
//	private SmsService smsService;
	
	/**
	 * 根据编号查找订单
	 * 
	 * @param sn
	 *            编号(忽略大小写)
	 * @return 订单，若不存在则返回null
	 */
	public Order findBySn(String sn) {
		return orderDao.find("sn", StringUtils.lowerCase(sn));
	}

	/**
	 * 查找订单
	 * 
	 * @param type
	 *            类型
	 * @param status
	 *            状态
	 * @param store
	 *            店铺
	 * @param member
	 *            会员
	 * @param product
	 *            商品
	 * @param isPendingReceive
	 *            是否等待收款
	 * @param isPendingRefunds
	 *            是否等待退款
	 * @param isUseCouponCode
	 *            是否已使用优惠码
	 * @param isExchangePoint
	 *            是否已兑换积分
	 * @param isAllocatedStock
	 *            是否已分配库存
	 * @param hasExpired
	 *            是否已过期
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 订单
	 */
	public List<Order> findList(Order.Type type, Order.Status status, Store store, Member member, Product product, Boolean isPendingReceive, Boolean isPendingRefunds, Boolean isUseCouponCode, Boolean isExchangePoint, Boolean isAllocatedStock, Boolean hasExpired, Integer count, List<Filter> filters,
			List<com.jfinalshop.Order> orders) {
		return orderDao.findList(type, status, store, member, product, isPendingReceive, isPendingRefunds, isUseCouponCode, isExchangePoint, isAllocatedStock, hasExpired, count, filters, orders);
	}

	/**
	 * 查找订单分页
	 * 
	 * @param type
	 *            类型
	 * @param status
	 *            状态
	 * @param store
	 *            店铺
	 * @param member
	 *            会员
	 * @param product
	 *            商品
	 * @param isPendingReceive
	 *            是否等待收款
	 * @param isPendingRefunds
	 *            是否等待退款
	 * @param isUseCouponCode
	 *            是否已使用优惠码
	 * @param isExchangePoint
	 *            是否已兑换积分
	 * @param isAllocatedStock
	 *            是否已分配库存
	 * @param hasExpired
	 *            是否已过期
	 * @param pageable
	 *            分页信息
	 * @return 订单分页
	 */
	public Page<Order> findPage(Order.Type type, Order.Status status, Store store, Member member, Product product, Boolean isPendingReceive, Boolean isPendingRefunds, Boolean isUseCouponCode, Boolean isExchangePoint, Boolean isAllocatedStock, Boolean hasExpired, Pageable pageable) {
		return orderDao.findPage(type, status, store, member, product, isPendingReceive, isPendingRefunds, isUseCouponCode, isExchangePoint, isAllocatedStock, hasExpired, pageable);
	}
	
	/**
	 * 线下订单查询
	 * 
	 * @param status
	 * @param source
	 * @param member
	 * @param startTime
	 * @param endTime
	 * @param pageable
	 * @param deleteFlag
	 * @return
	 */
	public Page<Order> findPage(Order.Type type, String sn, Order.Status status, Order.Source source, Member member, PaymentMethod paymentMethod, String startTime, String endTime, Pageable pageable, Boolean deleteFlag) {
		return orderDao.findPage(type, sn, status, source, member, paymentMethod, startTime, endTime, pageable, deleteFlag);
	}
	
	/**
	 * 查询订单数量
	 * 
	 * @param type
	 *            类型
	 * @param status
	 *            状态
	 * @param store
	 *            店铺
	 * @param member
	 *            会员
	 * @param product
	 *            商品
	 * @param isPendingReceive
	 *            是否等待收款
	 * @param isPendingRefunds
	 *            是否等待退款
	 * @param isUseCouponCode
	 *            是否已使用优惠码
	 * @param isExchangePoint
	 *            是否已兑换积分
	 * @param isAllocatedStock
	 *            是否已分配库存
	 * @param hasExpired
	 *            是否已过期
	 * @return 订单数量
	 */
	public Long count(Order.Type type, Order.Status status, Store store, Member member, Product product, Boolean isPendingReceive, Boolean isPendingRefunds, Boolean isUseCouponCode, Boolean isExchangePoint, Boolean isAllocatedStock, Boolean hasExpired) {
		return orderDao.count(type, status, store, member, product, isPendingReceive, isPendingRefunds, isUseCouponCode, isExchangePoint, isAllocatedStock, hasExpired);
	}

	/**
	 * 计算税金
	 * 
	 * @param price
	 *            SKU价格
	 * @param promotionDiscount
	 *            促销折扣
	 * @param couponDiscount
	 *            优惠券折扣
	 * @param offsetAmount
	 *            调整金额
	 * @return 税金
	 */
	public BigDecimal calculateTax(BigDecimal price, BigDecimal promotionDiscount, BigDecimal couponDiscount, BigDecimal offsetAmount) {
		Assert.notNull(price);
		Assert.state(price.compareTo(BigDecimal.ZERO) >= 0);
		Assert.state(promotionDiscount == null || promotionDiscount.compareTo(BigDecimal.ZERO) >= 0);
		Assert.state(couponDiscount == null || couponDiscount.compareTo(BigDecimal.ZERO) >= 0);

		Setting setting = SystemUtils.getSetting();
		if (!setting.getIsTaxPriceEnabled()) {
			return BigDecimal.ZERO;
		}
		BigDecimal amount = price;
		if (promotionDiscount != null) {
			amount = amount.subtract(promotionDiscount);
		}
		if (couponDiscount != null) {
			amount = amount.subtract(couponDiscount);
		}
		if (offsetAmount != null) {
			amount = amount.add(offsetAmount);
		}
		BigDecimal tax = amount.multiply(new BigDecimal(String.valueOf(setting.getTaxRate())));
		return tax.compareTo(BigDecimal.ZERO) >= 0 ? setting.setScale(tax) : BigDecimal.ZERO;
	}

	/**
	 * 计算税金
	 * 
	 * @param order
	 *            订单
	 * @return 税金
	 */
	public BigDecimal calculateTax(Order order) {
		Assert.notNull(order);

		if (order.getInvoice() == null) {
			return BigDecimal.ZERO;
		}
		return calculateTax(order.getPrice(), order.getPromotionDiscount(), order.getCouponDiscount(), order.getOffsetAmount());
	}

	/**
	 * 计算订单金额
	 * 
	 * @param price
	 *            SKU价格
	 * @param fee
	 *            支付手续费
	 * @param freight
	 *            运费
	 * @param tax
	 *            税金
	 * @param promotionDiscount
	 *            促销折扣
	 * @param couponDiscount
	 *            优惠券折扣
	 * @param offsetAmount
	 *            调整金额
	 * @return 订单金额
	 */
	public BigDecimal calculateAmount(BigDecimal price, BigDecimal fee, BigDecimal freight, BigDecimal tax, BigDecimal promotionDiscount, BigDecimal couponDiscount, BigDecimal offsetAmount) {
		Assert.notNull(price);
		Assert.state(price.compareTo(BigDecimal.ZERO) >= 0);
		Assert.state(fee == null || fee.compareTo(BigDecimal.ZERO) >= 0);
		Assert.state(freight == null || freight.compareTo(BigDecimal.ZERO) >= 0);
		Assert.state(tax == null || tax.compareTo(BigDecimal.ZERO) >= 0);
		Assert.state(promotionDiscount == null || promotionDiscount.compareTo(BigDecimal.ZERO) >= 0);
		Assert.state(couponDiscount == null || couponDiscount.compareTo(BigDecimal.ZERO) >= 0);

		Setting setting = SystemUtils.getSetting();
		BigDecimal amount = price;
		if (fee != null) {
			amount = amount.add(fee);
		}
		if (freight != null) {
			amount = amount.add(freight);
		}
		if (tax != null) {
			amount = amount.add(tax);
		}
		if (promotionDiscount != null) {
			amount = amount.subtract(promotionDiscount);
		}
		if (couponDiscount != null) {
			amount = amount.subtract(couponDiscount);
		}
		if (offsetAmount != null) {
			amount = amount.add(offsetAmount);
		}
		return amount.compareTo(BigDecimal.ZERO) >= 0 ? setting.setScale(amount) : BigDecimal.ZERO;
	}

	/**
	 * 计算订单金额
	 * 
	 * @param order
	 *            订单
	 * @return 订单金额
	 */
	public BigDecimal calculateAmount(Order order) {
		Assert.notNull(order);

		return calculateAmount(order.getPrice(), order.getFee(), order.getFreight(), order.getTax(), order.getPromotionDiscount(), order.getCouponDiscount(), order.getOffsetAmount());
	}

	/**
	 * 指定用户获取订单锁
	 * 
	 * @param order
	 *            订单
	 * @param user
	 *            用户
	 * @return 是否获取成功
	 */
	public boolean acquireLock(Order order, Business business) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.notNull(business);
		Assert.isTrue(!business.isNew());

		Long orderId = order.getId();
		Ehcache cache = cacheManager.getEhcache(Order.ORDER_LOCK_CACHE_NAME);
		cache.acquireWriteLockOnKey(orderId);
		try {
			Element element = cache.get(orderId);
			if (element != null && !business.getId().equals(element.getObjectValue())) {
				return false;
			}
			cache.put(new Element(orderId, business.getId()));
		} finally {
			cache.releaseWriteLockOnKey(orderId);
		}
		return true;
	}
	
	/**
	 * 指定用户获取订单锁
	 * 
	 * @param order
	 *            订单
	 * @param user
	 *            用户
	 * @return 是否获取成功
	 */
	public boolean acquireLock(Order order, Member member) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.notNull(member);
		Assert.isTrue(!member.isNew());

		Long orderId = order.getId();
		Ehcache cache = cacheManager.getEhcache(Order.ORDER_LOCK_CACHE_NAME);
		cache.acquireWriteLockOnKey(orderId);
		try {
			Element element = cache.get(orderId);
			if (element != null && !member.getId().equals(element.getObjectValue())) {
				return false;
			}
			cache.put(new Element(orderId, member.getId()));
		} finally {
			cache.releaseWriteLockOnKey(orderId);
		}
		return true;
	}

	/**
	 * 当前登录用户获取订单锁
	 * 
	 * @param order
	 *            订单
	 * @return 是否获取成功
	 */
//	public boolean acquireLock(Order order) {
//		Business currentUser = businessService.getCurrentUser();
//		if (currentUser == null) {
//			return false;
//		}
//		return acquireLock(order, currentUser);
//	}

	/**
	 * 释放订单锁
	 * 
	 * @param order
	 *            订单
	 */
	public void releaseLock(Order order) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());

		Ehcache cache = cacheManager.getEhcache(Order.ORDER_LOCK_CACHE_NAME);
		cache.remove(order.getId());
	}

	/**
	 * 过期订单优惠码使用撤销
	 */
	public void undoExpiredUseCouponCode() {
		while (true) {
			List<Order> orders = orderDao.findList(null, null, null, null, null, null, null, true, null, null, true, 100, null, null);
			if (CollectionUtils.isNotEmpty(orders)) {
				for (Order order : orders) {
					undoUseCouponCode(order);
				}
			}
			if (orders.size() < 100) {
				break;
			}
		}
	}

	/**
	 * 过期订单积分兑换撤销
	 */
	public void undoExpiredExchangePoint() {
		while (true) {
			List<Order> orders = orderDao.findList(null, null, null, null, null, null, null, null, true, null, true, 100, null, null);
			if (CollectionUtils.isNotEmpty(orders)) {
				for (Order order : orders) {
					undoExchangePoint(order);
				}
			}
			if (orders.size() < 100) {
				break;
			}
		}
	}

	/**
	 * 释放过期订单已分配库存
	 */
	public void releaseExpiredAllocatedStock() {
		while (true) {
			List<Order> orders = orderDao.findList(null, null, null, null, null, null, null, null, null, true, true, 100, null, null);
			if (CollectionUtils.isNotEmpty(orders)) {
				for (Order order : orders) {
					releaseAllocatedStock(order);
				}
			}
			if (orders.size() < 100) {
				break;
			}
		}
	}

	/**
	 * 自动收货
	 */
	public void automaticReceive() {
		Date currentTime = new Date();
		while (true) {
			List<Order> orders = orderDao.findList(null, Order.Status.shipped, null, null, null, null, null, null, null, null, false, 100, null, null);
			if (CollectionUtils.isNotEmpty(orders)) {
				for (Order order : orders) {
					OrderShipping orderShipping = orderShippingDao.findLast(order);
					Date automaticReceiveTime = DateUtils.addDays(orderShipping.getCreatedDate(), SystemUtils.getSetting().getAutomaticReceiveTime());
					if (automaticReceiveTime.compareTo(currentTime) < 0) {
						order.setStatus(Order.Status.received.ordinal());
						orderDao.update(order);
					}
				}
			}
			if (orders.size() < 100) {
				break;
			}
		}
	}
	

	/**
	 * 订单生成
	 * 
	 * @param type
	 *            类型
	 * @param cart
	 *            购物车
	 * @param receiver
	 *            收货地址
	 * @param paymentMethod
	 *            支付方式
	 * @param shippingMethod
	 *            配送方式
	 * @param couponCode
	 *            优惠码
	 * @param invoice
	 *            发票
	 * @param balance
	 *            使用余额
	 * @param memo
	 *            附言
	 * @return 订单
	 */
	public List<Order> generate(Order.Type type, Cart cart, Receiver receiver, PaymentMethod paymentMethod, ShippingMethod shippingMethod, CouponCode couponCode, Invoice invoice, BigDecimal balance, String memo,BigDecimal point) {
		Assert.notNull(type);
		Assert.notNull(cart);
		Assert.notNull(cart.getMember());
		Assert.notNull(cart.getStores());
		Assert.state(!cart.isEmpty());

		Setting setting = SystemUtils.getSetting();
		Member member = cart.getMember();
		BigDecimal price = BigDecimal.ZERO;
		BigDecimal discount = BigDecimal.ZERO;
		Long effectiveRewardPoint = 0L;
		BigDecimal couponDiscount = BigDecimal.ZERO;

		List<Store> stores = cart.getStores();
		List<Order> orders = new ArrayList<>();
		for (Store store : stores) {
			price = cart.getPrice(store);
			discount = cart.getDiscount(store);
			effectiveRewardPoint = cart.getEffectiveRewardPoint(store);
			couponDiscount = couponCode != null && cart.isCouponAllowed(store) && cart.isValid(couponCode, store) ? cart.getEffectivePrice(store).subtract(couponCode.getCoupon().calculatePrice(cart.getEffectivePrice(store), cart.getSkuQuantity(store))) : BigDecimal.ZERO;
			Order order = new Order();
			order.setType(type.ordinal());
			order.setPrice(price);
			order.setFee(BigDecimal.ZERO);
			order.setPromotionDiscount(discount);
			order.setOffsetAmount(BigDecimal.ZERO);
			order.setRefundAmount(BigDecimal.ZERO);
			order.setRewardPoint(effectiveRewardPoint);
			order.setExchangePoint(cart.getExchangePoint(store));
			order.setWeight(cart.getTotalWeight(store));
			order.setQuantity(cart.getTotalQuantity(store));
			order.setShippedQuantity(0);
			order.setReturnedQuantity(0);
			order.setMemo(memo);
			order.setIsUseCouponCode(false);
			order.setIsExchangePoint(false);
			order.setIsAllocatedStock(false);
			order.setInvoice(setting.getIsInvoiceEnabled() ? invoice : null);
			order.setPaymentMethod(paymentMethod);
			order.setMember(member);
			order.setStore(store);
			order.setPromotionNames(new ArrayList<>(cart.getPromotionNames(store)));
			order.setCoupons(new ArrayList<>(cart.getCoupons(store)));
			// 订单所属店铺名
			order.put("storeName", store.getName());

			if (shippingMethod != null && shippingMethod.isSupported(paymentMethod) && cart.getIsDelivery(store)) {
				order.setFreight(!cart.isFreeShipping(store) ? shippingMethodService.calculateFreight(shippingMethod, store, receiver, cart.getNeedDeliveryTotalWeight(store)) : BigDecimal.ZERO);
				order.setShippingMethod(shippingMethod);
			} else {
				order.setFreight(BigDecimal.ZERO);
				order.setShippingMethod(null);
			}

			if (couponCode != null && cart.isCouponAllowed(store) && cart.isValid(couponCode, store)) {
				order.setCouponDiscount(couponDiscount.compareTo(BigDecimal.ZERO) >= 0 ? couponDiscount : BigDecimal.ZERO);
				order.setCouponCode(couponCode);
				order.setCouponCodeId(couponCode.getId());
			} else {
				order.setCouponDiscount(BigDecimal.ZERO);
				order.setCouponCode(null);
			}

			order.setTax(calculateTax(order));
			order.setAmount(calculateAmount(order));
			if (balance != null && balance.compareTo(BigDecimal.ZERO) > 0 && balance.compareTo(member.getBalance()) <= 0) {
				if (balance.compareTo(order.getAmount()) <= 0) {
					order.setAmountPaid(balance);
				} else {
					order.setAmountPaid(order.getAmount());
					balance = balance.subtract(order.getAmount());
				}
			}
			else if (point != null && point.compareTo(BigDecimal.ZERO) > 0 && point.compareTo(new BigDecimal(member.getPoint())) <= 0) {
				if (point.compareTo(order.getAmount()) <= 0) {
					order.setAmountPaid(point);
				} else {
					order.setAmountPaid(order.getAmount());
					balance = balance.subtract(order.getAmount());
				}
			}
			else {
				order.setAmountPaid(BigDecimal.ZERO);
			}

			if (cart.getIsDelivery(store) && receiver != null) {
				order.setConsignee(receiver.getConsignee());
				order.setAreaName(receiver.getAreaName());
				order.setAddress(receiver.getAddress());
				order.setZipCode(receiver.getZipCode());
				order.setPhone(receiver.getPhone());
				order.setArea(receiver.getArea());
			}

			List<OrderItem> orderItems = order.getOrderItems();
			for (CartItem cartItem : cart.getCartItems(store)) {
				Sku sku = cartItem.getSku();
				if (sku != null) {
					OrderItem orderItem = new OrderItem();
					orderItem.setSn(sku.getSn());
					orderItem.setName(sku.getName());
					orderItem.setType(sku.getType().ordinal());
					orderItem.setPrice(cartItem.getPrice());
					orderItem.setWeight(sku.getWeight());
					orderItem.setIsDelivery(sku.getIsDelivery());
					orderItem.setThumbnail(sku.getThumbnail());
					orderItem.setQuantity(cartItem.getQuantity());
					orderItem.setShippedQuantity(0);
					orderItem.setReturnedQuantity(0);
					orderItem.setSku(cartItem.getSku());
					orderItem.setOrder(order);
					orderItem.setSpecifications(sku.getSpecifications());
					orderItems.add(orderItem);
				}
			}

			for (Sku gift : cart.getGifts(store)) {
				OrderItem orderItem = new OrderItem();
				orderItem.setSn(gift.getSn());
				orderItem.setName(gift.getName());
				orderItem.setType(gift.getType().ordinal());
				orderItem.setPrice(BigDecimal.ZERO);
				orderItem.setWeight(gift.getWeight());
				orderItem.setIsDelivery(gift.getIsDelivery());
				orderItem.setThumbnail(gift.getThumbnail());
				orderItem.setQuantity(1);
				orderItem.setShippedQuantity(0);
				orderItem.setReturnedQuantity(0);
				orderItem.setSku(gift);
				orderItem.setOrder(order);
				orderItem.setSpecifications(gift.getSpecifications());
				orderItems.add(orderItem);
			}
			orders.add(order);
		}
		return orders;
	}

	/**
	 * 订单创建
	 * 
	 * @param type
	 *            类型
	 * @param cart
	 *            购物车
	 * @param receiver
	 *            收货地址
	 * @param paymentMethod
	 *            支付方式
	 * @param shippingMethod
	 *            配送方式
	 * @param couponCode
	 *            优惠码
	 * @param invoice
	 *            发票
	 * @param balance
	 *            使用余额
	 * @param memo
	 *            附言
	 * @return 订单
	 */
	public List<Order> create(Order.Type type, Order.Source source, Cart cart, Receiver receiver, PaymentMethod paymentMethod, ShippingMethod shippingMethod, CouponCode couponCode, Invoice invoice, BigDecimal balance, String memo,BigDecimal point) {
		Assert.notNull(type);
		Assert.notNull(cart);
		Assert.notNull(cart.getMember());
		Assert.state(!cart.isEmpty());
		if (cart.getIsDelivery(null)) {
			Assert.notNull(receiver);
			Assert.notNull(shippingMethod);
			Assert.state(shippingMethod.isSupported(paymentMethod));
		} else {
			Assert.isNull(receiver);
			Assert.isNull(shippingMethod);
		}
		List<Store> stores = cart.getStores();
		List<Order> orders = new ArrayList<>();

		for (CartItem cartItem : cart.getCartItems()) {
			Sku sku = cartItem.getSku();
			if (sku == null || !sku.getIsMarketable() || cartItem.getQuantity() > sku.getAvailableStock()) {
				throw new IllegalArgumentException();
			}
		}

		for (Store store : stores) {
			for (Sku gift : cart.getGifts(store)) {
				if (!gift.getIsMarketable() || gift.getIsOutOfStock()) {
					throw new IllegalArgumentException();
				}
			}

			Setting setting = SystemUtils.getSetting();
			Member member = cart.getMember();

			Order order = new Order();
			order.setSn(snDao.generate(Sn.Type.order));
			order.setType(type.ordinal());
			order.setSource(source.ordinal());
			order.setPrice(cart.getPrice(store));
			order.setFee(BigDecimal.ZERO);
			order.setFreight(cart.getIsDelivery(store) && !cart.isFreeShipping(store) ? shippingMethodService.calculateFreight(shippingMethod, store, receiver, cart.getNeedDeliveryTotalWeight(store)) : BigDecimal.ZERO);
			order.setPromotionDiscount(cart.getDiscount(store));
			order.setOffsetAmount(BigDecimal.ZERO);
			order.setAmountPaid(BigDecimal.ZERO);
			order.setRefundAmount(BigDecimal.ZERO);
			order.setRewardPoint(cart.getEffectiveRewardPoint(store));
			order.setExchangePoint(cart.getExchangePoint(store));
			order.setWeight(cart.getTotalWeight(store));
			order.setQuantity(cart.getTotalQuantity(store));
			order.setShippedQuantity(0);
			order.setReturnedQuantity(0);
			if (cart.getIsDelivery(store)) {
				order.setConsignee(receiver.getConsignee());
				order.setAreaName(receiver.getAreaName());
				order.setAddress(receiver.getAddress());
				order.setZipCode(receiver.getZipCode());
				order.setPhone(receiver.getPhone());
				Area area = receiver.getArea();
				order.setAreaId(area != null ? area.getId() : null);
			}
			order.setMemo(memo);
			order.setIsUseCouponCode(false);
			order.setIsExchangePoint(false);
			order.setIsAllocatedStock(false);
			order.setInvoice(setting.getIsInvoiceEnabled() ? invoice : null);
			order.setShippingMethodId(shippingMethod.getId());
			order.setMemberId(member.getId());
			order.setStoreId(store.getId());
			order.setPromotionNames(cart.getPromotionNames(store));
			order.setCoupons(new ArrayList<>(cart.getCoupons(store)));

			if (couponCode != null && couponCode.getCoupon().getStore().equals(store)) {
				if (!cart.isCouponAllowed(store) || !cart.isValid(couponCode, store)) {
					throw new IllegalArgumentException();
				}
				BigDecimal couponDiscount = cart.getEffectivePrice(store).subtract(couponCode.getCoupon().calculatePrice(cart.getEffectivePrice(store), cart.getSkuQuantity(store)));
				order.setCouponDiscount(couponDiscount.compareTo(BigDecimal.ZERO) >= 0 ? couponDiscount : BigDecimal.ZERO);
				order.setCouponCode(couponCode);
				useCouponCode(order);
			} else {
				order.setCouponDiscount(BigDecimal.ZERO);
			}

			order.setTax(calculateTax(order));
			order.setAmount(calculateAmount(order));
			if (balance != null && (balance.compareTo(BigDecimal.ZERO) < 0 || balance.compareTo(member.getBalance()) > 0)) {
				throw new IllegalArgumentException();
			}
			if (point != null && (point.compareTo(BigDecimal.ZERO) < 0 || point.compareTo(member.getBalance()) > 0)) {
				throw new IllegalArgumentException();
			}
			BigDecimal amountPayable = balance != null ? order.getAmount().subtract(balance) : order.getAmount();
			BigDecimal pointPayable = point != null ? order.getAmount().subtract(point) : order.getAmount();
			String payType = "0";
			if (balance!=null&&balance.compareTo(BigDecimal.ZERO) > 0&&amountPayable.compareTo(BigDecimal.ZERO) > 0) {
				order.setPaymentMethodId(paymentMethod.getId());
				throw new IllegalArgumentException();
//				if (paymentMethod == null) {
//					throw new IllegalArgumentException();
//				}
//				order.setStatus(PaymentMethod.Type.deliveryAgainstPayment.equals(paymentMethod.getTypeName()) ? Order.Status.pendingPayment.ordinal() : Order.Status.pendingReview.ordinal());
//				order.setPaymentMethodId(paymentMethod.getId());
//				if (paymentMethod.getTimeout() != null && Order.Status.pendingPayment.equals(order.getStatusName())) {
//					order.setExpire(DateUtils.addMinutes(new Date(), paymentMethod.getTimeout()));
//				}
//				payType="1";
			}
			else if(point!=null&&point.compareTo(BigDecimal.ZERO) > 0&&pointPayable.compareTo(BigDecimal.ZERO) > 0){
				order.setPaymentMethodId(paymentMethod.getId());
				throw new IllegalArgumentException();
//				if (paymentMethod == null) {
//					throw new IllegalArgumentException();
//				}
//				order.setStatus(PaymentMethod.Type.deliveryAgainstPayment.equals(paymentMethod.getTypeName()) ? Order.Status.pendingPayment.ordinal() : Order.Status.pendingReview.ordinal());
//				order.setPaymentMethodId(paymentMethod.getId());
//				if (paymentMethod.getTimeout() != null && Order.Status.pendingPayment.equals(order.getStatusName())) {
//					order.setExpire(DateUtils.addMinutes(new Date(), paymentMethod.getTimeout()));
//				}
//				payType="2";
			}
			else {
				if(balance!=null&&balance.compareTo(BigDecimal.ZERO)>0||point!=null&&point.compareTo(BigDecimal.ZERO) > 0){
					order.setStatus(Order.Status.pendingReview.ordinal());
				}
				else{
					order.setStatus(Order.Status.pendingPayment.ordinal());
				}
				order.setPaymentMethodId(paymentMethod!=null?paymentMethod.getId():null);
			}
			
			if (receiver.getArea() != null) {
				order.setAreaName(receiver.getArea().getFullName());
			}
			if (paymentMethod != null) {
				order.setPaymentMethodName(paymentMethod.getName());
				order.setPaymentMethodType(paymentMethod.getType());
			}
			if (shippingMethod != null) {
				order.setShippingMethodName(shippingMethod.getName());
			}
			
			orderDao.save(order);
			List<OrderItem> orderItems = order.getOrderItems();
			for (CartItem cartItem : cart.getCartItems(store)) {
				Sku sku = cartItem.getSku();
				OrderItem orderItem = new OrderItem();
				orderItem.setSn(sku.getSn());
				orderItem.setName(sku.getName());
				orderItem.setType(sku.getType().ordinal());
				orderItem.setPrice(cartItem.getPrice());
				orderItem.setWeight(sku.getWeight());
				orderItem.setIsDelivery(sku.getIsDelivery());
				orderItem.setThumbnail(sku.getThumbnail());
				orderItem.setQuantity(cartItem.getQuantity());
				orderItem.setShippedQuantity(0);
				orderItem.setReturnedQuantity(0);
				orderItem.setCommissionTotals(sku.getCommission(store.getTypeName()).multiply(new BigDecimal(cartItem.getQuantity())));
				orderItem.setSkuId(sku.getId());
				orderItem.setOrderId(order.getId());
				orderItem.setSpecifications(sku.getSpecifications());
				orderItemDao.save(orderItem);
				orderItems.add(orderItem);
				cartItem.delete();
			}

			for (Sku gift : cart.getGifts(store)) {
				OrderItem orderItem = new OrderItem();
				orderItem.setSn(gift.getSn());
				orderItem.setName(gift.getName());
				orderItem.setType(gift.getType().ordinal());
				orderItem.setPrice(BigDecimal.ZERO);
				orderItem.setWeight(gift.getWeight());
				orderItem.setIsDelivery(gift.getIsDelivery());
				orderItem.setThumbnail(gift.getThumbnail());
				orderItem.setQuantity(1);
				orderItem.setShippedQuantity(0);
				orderItem.setReturnedQuantity(0);
				orderItem.setCommissionTotals(gift.getCommission(store.getTypeName()).multiply(new BigDecimal("1")));
				orderItem.setSkuId(gift.getId());
				orderItem.setOrderId(order.getId());
				orderItem.setSpecifications(gift.getSpecifications());
				orderItemDao.save(orderItem);
				orderItems.add(orderItem);
			}
			
			order.setOrderItems(orderItems);
			OrderLog orderLog = new OrderLog();
			orderLog.setType(OrderLog.Type.create.ordinal());
			orderLog.setOrderId(order.getId());
			orderLogDao.save(orderLog);

			exchangePoint(order);
			if (Setting.StockAllocationTime.order.equals(setting.getStockAllocationTime()) || (Setting.StockAllocationTime.payment.equals(setting.getStockAllocationTime()) && (order.getAmountPaid().compareTo(BigDecimal.ZERO) > 0 || order.getExchangePoint() > 0 || order.getAmountPayable().compareTo(BigDecimal.ZERO) <= 0))) {
				allocateStock(order);
			}

			if (balance != null && balance.compareTo(BigDecimal.ZERO) > 0) {
				OrderPayment orderPayment = new OrderPayment();
				orderPayment.setMethod(OrderPayment.Method.deposit.ordinal());
				orderPayment.setFee(BigDecimal.ZERO);
				orderPayment.setOrderId(order.getId());
				if (balance.compareTo(order.getAmount()) >= 0) {
					balance = balance.subtract(order.getAmount());
					orderPayment.setAmount(order.getAmount());
				} else {
					orderPayment.setAmount(balance);
					balance = BigDecimal.ZERO;
				}
				payment(order, orderPayment);
			}
			else if (point != null && point.compareTo(BigDecimal.ZERO) > 0 ) {
				OrderPayment orderPayment = new OrderPayment();
				orderPayment.setMethod(OrderPayment.Method.point.ordinal());
				orderPayment.setFee(BigDecimal.ZERO);
				orderPayment.setOrderId(order.getId());
				if (point.compareTo(order.getAmount()) >= 0) {
					point = point.subtract(order.getAmount());
					orderPayment.setAmount(order.getAmount());
				} else {
					orderPayment.setAmount(point);
					point = BigDecimal.ZERO;
				}
				payment(order, orderPayment);
			}
			//mailService.sendCreateOrderMail(order);
			//smsService.sendCreateOrderSms(order);
			orders.add(order);
		}
		return orders;
	}

	/**
	 * 订单更新
	 * 
	 * @param order
	 *            订单
	 */
	public void modify(Order order) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.state(!order.hasExpired() && (Order.Status.pendingPayment.equals(order.getStatusName()) || Order.Status.pendingReview.equals(order.getStatusName())));

		order.setAmount(calculateAmount(order));
		if (order.getAmountPayable().compareTo(BigDecimal.ZERO) <= 0) {
			order.setStatus(Order.Status.pendingReview.ordinal());
			order.setExpire(null);
		} else {
			if (order.getPaymentMethod() != null && PaymentMethod.Type.deliveryAgainstPayment.equals(order.getPaymentMethod().getType())) {
				order.setStatus(Order.Status.pendingPayment.ordinal());
			} else {
				order.setStatus(Order.Status.pendingReview.ordinal());
				order.setExpire(null);
			}
		}
		orderDao.update(order);

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.modify.ordinal());
		orderLog.setOrderId(order.getId());
		orderLogDao.save(orderLog);

		//mailService.sendUpdateOrderMail(order);
		//smsService.sendUpdateOrderSms(order);
	}

	/**
	 * 订单取消
	 * 
	 * @param order
	 *            订单
	 */
	public void cancel(Order order) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.state(Order.Status.pendingPayment.equals(order.getStatusName()) || Order.Status.pendingReview.equals(order.getStatusName()) || Order.Status.pendingShipment.equals(order.getStatusName()));

		order.setStatus(Order.Status.canceled.ordinal());
		order.setExpire(null);

		undoUseCouponCode(order);
		undoExchangePoint(order);
		releaseAllocatedStock(order);

		if (order.getRefundableAmount().compareTo(BigDecimal.ZERO) > 0) {
			businessService.addBalance(order.getStore().getBusiness(), order.getRefundableAmount(), BusinessDepositLog.Type.orderRefunds, null);
		}
		orderDao.update(order);
		
		// 如果订单状态是[等待接单中]，要产生退款单。
		if (order.getAmountPaid().compareTo(BigDecimal.ZERO) > 0) {
			List<OrderPayment> orderPayments = order.getOrderPayments();
			if (CollectionUtils.isNotEmpty(orderPayments)) {
				for (OrderPayment orderPayment : orderPayments) {
					OrderRefunds orderRefunds = new OrderRefunds();
					orderRefunds.setOrder(order);
					orderRefunds.setPaymentMethod(paymentMethodDao.find(1L));
					orderRefunds.setMethod(orderPayment.getMethod());
					orderRefunds.setPayee(order.getConsignee());
					orderRefunds.setAmount(orderPayment.getAmount());
					refunds(order, orderRefunds);
				}
			}
		}

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.cancel.ordinal());
		orderLog.setOrderId(order.getId());
		orderLogDao.save(orderLog);

		//mailService.sendCancelOrderMail(order);
		//smsService.sendCancelOrderSms(order);
	}

	/**
	 * 订单审核
	 * 
	 * @param order
	 *            订单
	 * @param passed
	 *            是否审核通过
	 */
	public void review(Order order, boolean passed) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.state(!order.hasExpired() && Order.Status.pendingReview.equals(order.getStatusName()));

		if (passed) {
			order.setStatus(Order.Status.pendingShipment.ordinal());
		} else {
			order.setStatus(Order.Status.denied.ordinal());

			if (order.getRefundableAmount().compareTo(BigDecimal.ZERO) > 0) {
				businessService.addBalance(order.getStore().getBusiness(), order.getRefundableAmount(), BusinessDepositLog.Type.orderRefunds, null);
			}
			undoUseCouponCode(order);
			undoExchangePoint(order);
			releaseAllocatedStock(order);
		}
		orderDao.update(order);

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.review.ordinal());
		orderLog.setOrderId(order.getId());
		orderLogDao.save(orderLog);

		//mailService.sendReviewOrderMail(order);
		//smsService.sendReviewOrderSms(order);
	}

	/**
	 * 订单收款
	 * 
	 * @param order
	 *            订单
	 * @param orderPayment
	 *            订单支付
	 */
	public void payment(Order order, OrderPayment orderPayment) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.notNull(orderPayment);
		Assert.isTrue(orderPayment.isNew());
		Assert.notNull(orderPayment.getAmount());
		Assert.state(orderPayment.getAmount().compareTo(BigDecimal.ZERO) > 0);

		orderPayment.setSn(snDao.generate(Sn.Type.orderPayment));
		orderPayment.setOrderId(order.getId());
		orderPaymentDao.save(orderPayment);

		if (order.getMember() != null && OrderPayment.Method.deposit.equals(orderPayment.getMethodName())) {
			memberService.addBalance(order.getMember(), orderPayment.getEffectiveAmount().negate(), MemberDepositLog.Type.orderPayment, null);
		}
		else if (order.getMember() != null && OrderPayment.Method.point.equals(orderPayment.getMethodName())) {
			memberService.addPoint(order.getMember(), orderPayment.getEffectiveAmount().negate().longValue(), PointLog.Type.pointuse, null);
		}

		Setting setting = SystemUtils.getSetting();
		if (Setting.StockAllocationTime.payment.equals(setting.getStockAllocationTime())) {
			allocateStock(order);
		}

		order.setAmountPaid(order.getAmountPaid().add(orderPayment.getEffectiveAmount()));
		order.setFee(order.getFee().add(orderPayment.getFee()));
		if (!order.hasExpired() && Order.Status.pendingPayment.equals(order.getStatusName()) && order.getAmountPayable().compareTo(BigDecimal.ZERO) <= 0) {
			order.setStatus(Order.Status.pendingReview.ordinal());
			order.setExpire(null);
		}
		orderDao.update(order);

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.payment.ordinal());
		orderLog.setDetail(StringUtils.isNotEmpty(orderPayment.getBank()) ? orderPayment.getBank() : null );
		orderLog.setOrderId(order.getId());
		orderLogDao.save(orderLog);

		//mailService.sendPaymentOrderMail(order);
		//smsService.sendPaymentOrderSms(order);
	}
	/**
	 * 订单退款
	 * 
	 * @param order
	 *            订单
	 * @param orderRefunds
	 *            订单退款
	 */
	public void refunds(Order order, OrderRefunds orderRefunds) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.state(order.getRefundableAmount().compareTo(BigDecimal.ZERO) > 0);
		Assert.notNull(orderRefunds);
		Assert.isTrue(orderRefunds.isNew());
		Assert.notNull(orderRefunds.getAmount());
		Assert.state(orderRefunds.getAmount().compareTo(BigDecimal.ZERO) > 0 && orderRefunds.getAmount().compareTo(order.getRefundableAmount()) <= 0);
		Assert.state(!OrderRefunds.Method.deposit.equals(orderRefunds.getMethodName()) || order.getStore().getBusiness().getBalance().compareTo(orderRefunds.getAmount()) >= 0);

		orderRefunds.setSn(snDao.generate(Sn.Type.orderRefunds));
		orderRefunds.setOrderId(order.getId());
		orderRefundsDao.save(orderRefunds);

		if (OrderRefunds.Method.deposit.equals(orderRefunds.getMethodName())) {
			memberService.addBalance(order.getMember(), orderRefunds.getAmount(), MemberDepositLog.Type.orderRefunds, null);
			businessService.addBalance(order.getStore().getBusiness(), orderRefunds.getAmount().negate(), BusinessDepositLog.Type.orderRefunds, null);
		}

		order.setAmountPaid(order.getAmountPaid().subtract(orderRefunds.getAmount()));
		order.setRefundAmount(order.getRefundAmount().add(orderRefunds.getAmount()));
		orderDao.update(order);

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.refunds.ordinal());
		orderLog.setOrderId(order.getId());
		orderLogDao.save(orderLog);

		//mailService.sendRefundsOrderMail(order);
		//smsService.sendRefundsOrderSms(order);
	}

	/**
	 * 订单发货
	 * 
	 * @param order
	 *            订单
	 * @param orderShipping
	 *            订单发货
	 */
	public void shipping(Order order, OrderShipping orderShipping) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.state(order.getShippableQuantity() > 0);
		Assert.notNull(orderShipping);
		Assert.isTrue(orderShipping.isNew());
		Assert.notEmpty(orderShipping.getOrderShippingItems());

		orderShipping.setSn(snDao.generate(Sn.Type.orderShipping));
		orderShipping.setOrderId(order.getId());
		orderShippingDao.save(orderShipping);
		
		List<OrderShippingItem> orderShippingItems = orderShipping.getOrderShippingItems();
		if (CollectionUtils.isNotEmpty(orderShippingItems)) {
			for (OrderShippingItem shippingItem : orderShippingItems) {
				shippingItem.setOrderShippingId(orderShipping.getId());
				orderShippingItemDao.save(shippingItem);
			}
		}

		Setting setting = SystemUtils.getSetting();
		if (Setting.StockAllocationTime.ship.equals(setting.getStockAllocationTime())) {
			allocateStock(order);
		}

		for (OrderShippingItem orderShippingItem : orderShipping.getOrderShippingItems()) {
			OrderItem orderItem = order.getOrderItem(orderShippingItem.getSn());
			if (orderItem == null || orderShippingItem.getQuantity() > orderItem.getShippableQuantity()) {
				throw new IllegalArgumentException();
			}
			orderItem.setShippedQuantity(orderItem.getShippedQuantity() + orderShippingItem.getQuantity());
			orderItemDao.update(orderItem);
			
			Sku sku = orderShippingItem.getSku();
			if (sku != null) {
				if (orderShippingItem.getQuantity() > sku.getStock()) {
					throw new IllegalArgumentException();
				}
				skuService.addStock(sku, -orderShippingItem.getQuantity(), StockLog.Type.stockOut, null);
				if (BooleanUtils.isTrue(order.getIsAllocatedStock())) {
					skuService.addAllocatedStock(sku, -orderShippingItem.getQuantity());
				}
			}
		}

		order.setShippedQuantity(order.getShippedQuantity() + orderShipping.getQuantity());
		if (order.getShippedQuantity() >= order.getQuantity()) {
			order.setStatus(Order.Status.shipped.ordinal());
			order.setIsAllocatedStock(false);
		}
		orderDao.update(order);

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.shipping.ordinal());
		orderLog.setOrderId(order.getId());
		orderLogDao.save(orderLog);

		//mailService.sendShippingOrderMail(order);
		//smsService.sendShippingOrderSms(order);
	}

	/**
	 * 订单退货
	 * 
	 * @param order
	 *            订单
	 * @param orderReturns
	 *            订单退货
	 */
	public void returns(Order order, OrderReturns orderReturns) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.state(order.getReturnableQuantity() > 0);
		Assert.notNull(orderReturns);
		Assert.isTrue(orderReturns.isNew());
		Assert.notEmpty(orderReturns.getOrderReturnsItems());

		orderReturns.setSn(snDao.generate(Sn.Type.orderReturns));
		orderReturns.setOrderId(order.getId());
		orderReturnsDao.save(orderReturns);
		
		List<OrderReturnsItem> orderReturnsItems = orderReturns.getOrderReturnsItems();
		if (CollectionUtil.isNotEmpty(orderReturnsItems)) {
			for (OrderReturnsItem orderReturnsItem : orderReturnsItems) {
				orderReturnsItem.setOrderReturnsId(orderReturns.getId());
				orderReturnsItemDao.save(orderReturnsItem);
			}
		}

		for (OrderReturnsItem orderReturnsItem : orderReturns.getOrderReturnsItems()) {
			OrderItem orderItem = order.getOrderItem(orderReturnsItem.getSn());
			if (orderItem == null || orderReturnsItem.getQuantity() > orderItem.getReturnableQuantity()) {
				throw new IllegalArgumentException();
			}
			orderItem.setReturnedQuantity(orderItem.getReturnedQuantity() + orderReturnsItem.getQuantity());
			orderItemDao.update(orderItem);
		}

		order.setReturnedQuantity(order.getReturnedQuantity() + orderReturns.getQuantity());
		orderDao.update(order);

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.returns.ordinal());
		orderLog.setOrderId(order.getId());
		orderLogDao.save(orderLog);

		//mailService.sendReturnsOrderMail(order);
		//smsService.sendReturnsOrderSms(order);
	}


	/**
	 * 订单收货
	 * 
	 * @param order
	 *            订单
	 */
	public void receive(Order order) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.state(!order.hasExpired() && Order.Status.shipped.equals(order.getStatusName()));

		order.setStatus(Order.Status.received.ordinal());
		orderDao.update(order);

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.receive.ordinal());
		orderLog.setOrderId(order.getId());
		orderLogDao.save(orderLog);

		//mailService.sendReceiveOrderMail(order);
		//smsService.sendReceiveOrderSms(order);
	}

	/**
	 * 订单完成
	 * 
	 * @param order
	 *            订单
	 */
	public void complete(Order order) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.state(!order.hasExpired() && Order.Status.received.equals(order.getStatusName()));

		Member member = order.getMember();
		if (order.getRewardPoint() > 0) {
			memberService.addPoint(member, order.getRewardPoint(), PointLog.Type.reward, null);
		}
		if (CollectionUtils.isNotEmpty(order.getCoupons())) {
			for (Coupon coupon : order.getCoupons()) {
				couponCodeService.generate(coupon, member);
			}
		}
		if (order.getAmountPaid().compareTo(BigDecimal.ZERO) > 0) {
			memberService.addAmount(member, order.getAmountPaid());
		}
		if (order.getSettlementAmount().compareTo(BigDecimal.ZERO) > 0) {
			businessService.addBalance(order.getStore().getBusiness(), order.getSettlementAmount(), BusinessDepositLog.Type.orderSettlement, null);
		}
		for (OrderItem orderItem : order.getOrderItems()) {
			Sku sku = orderItem.getSku();
			if (sku != null && sku.getProduct() != null) {
				productService.addSales(sku.getProduct(), orderItem.getQuantity());
			}
		}

		order.setStatus(Order.Status.completed.ordinal());
		order.setCompleteDate(new Date());
		orderDao.update(order);

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.complete.ordinal());
		orderLog.setOrderId(order.getId());
		orderLogDao.save(orderLog);

		//mailService.sendCompleteOrderMail(order);
		//smsService.sendCompleteOrderSms(order);
	}

	/**
	 * 订单失败
	 * 
	 * @param order
	 *            订单
	 */
	public void fail(Order order) {
		Assert.notNull(order);
		Assert.isTrue(!order.isNew());
		Assert.state(!order.hasExpired() && (Order.Status.pendingShipment.equals(order.getStatusName()) || Order.Status.shipped.equals(order.getStatusName()) || Order.Status.received.equals(order.getStatusName())));

		order.setStatus(Order.Status.failed.ordinal());

		undoUseCouponCode(order);
		undoExchangePoint(order);
		releaseAllocatedStock(order);

		if (order.getRefundableAmount().compareTo(BigDecimal.ZERO) > 0) {
			businessService.addBalance(order.getStore().getBusiness(), order.getRefundableAmount(), BusinessDepositLog.Type.orderRefunds, null);
		}
		orderDao.update(order);

		OrderLog orderLog = new OrderLog();
		orderLog.setType(OrderLog.Type.fail.ordinal());
		orderLog.setOrderId(order.getId());
		orderLogDao.save(orderLog);

		//mailService.sendFailOrderMail(order);
		//smsService.sendFailOrderSms(order);
	}

	/**
	 * 订单完成数量
	 * 
	 * @param store
	 *            店铺
	 * @param beginDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @return 订单完成数量
	 */
	public Long completeOrderCount(Store store, Date beginDate, Date endDate) {
		return orderDao.completeOrderCount(store, beginDate, endDate);
	}

	/**
	 * 订单完成金额
	 * 
	 * @param store
	 *            店铺
	 * @param beginDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @return 订单完成金额
	 */
	public BigDecimal completeOrderAmount(Store store, Date beginDate, Date endDate) {
		return orderDao.completeOrderAmount(store, beginDate, endDate);
	}

	
	@Override
	public void delete(Order order) {
		if (order != null && !Order.Status.completed.equals(order.getStatusName())) {
			undoUseCouponCode(order);
			undoExchangePoint(order);
			releaseAllocatedStock(order);
		}

		super.delete(order);
	}
	
	/**
	 * 线下收银报表
	 * 
	 */
	public List<Order> cashTotal(String startTime, String endTime, Member member, Member currentUser) {
		return orderDao.cashTotal(startTime, endTime, member, currentUser);
	}
	
	/**
	 * 线下上门收款
	 * 
	 */
	public List<Order> deliveryTotal(String startTime, String endTime, Member currentUser) {
		return orderDao.deliveryTotal(startTime, endTime, currentUser);
	}
	
	
	/**
	 * 优惠码使用
	 * 
	 * @param order
	 *            订单
	 */
	private void useCouponCode(Order order) {
		if (order == null || BooleanUtils.isNotFalse(order.getIsUseCouponCode()) || order.getCouponCode() == null) {
			return;
		}
		CouponCode couponCode = order.getCouponCode();
		couponCode.setIsUsed(true);
		couponCode.setUsedDate(new Date());
		couponCodeService.update(couponCode);
		
		order.setIsUseCouponCode(true);

	}

	/**
	 * 优惠码使用撤销
	 * 
	 * @param order
	 *            订单
	 */
	private void undoUseCouponCode(Order order) {
		if (order == null || BooleanUtils.isNotTrue(order.getIsUseCouponCode()) || order.getCouponCode() == null) {
			return;
		}
		CouponCode couponCode = order.getCouponCode();
		couponCode.setIsUsed(false);
		couponCode.setUsedDate(null);
		couponCodeService.update(couponCode);
		
		order.setIsUseCouponCode(false);
		order.setCouponCode(null);
		orderDao.update(order);
	}

	/**
	 * 积分兑换
	 * 
	 * @param order
	 *            订单
	 */
	private void exchangePoint(Order order) {
		if (order == null || BooleanUtils.isNotFalse(order.getIsExchangePoint()) || order.getExchangePoint() <= 0 || order.getMember() == null) {
			return;
		}
		memberService.addPoint(order.getMember(), -order.getExchangePoint(), PointLog.Type.exchange, null);
		order.setIsExchangePoint(true);
		orderDao.update(order);
	}

	/**
	 * 积分兑换撤销
	 * 
	 * @param order
	 *            订单
	 */
	private void undoExchangePoint(Order order) {
		if (order == null || BooleanUtils.isNotTrue(order.getIsExchangePoint()) || order.getExchangePoint() <= 0 || order.getMember() == null) {
			return;
		}
		memberService.addPoint(order.getMember(), order.getExchangePoint(), PointLog.Type.undoExchange, null);
		order.setIsExchangePoint(false);
		orderDao.update(order);
	}

	/**
	 * 分配库存
	 * 
	 * @param order
	 *            订单
	 */
	private void allocateStock(Order order) {
		if (order == null || BooleanUtils.isNotFalse(order.getIsAllocatedStock())) {
			return;
		}
		if (order.getOrderItems() != null) {
			for (OrderItem orderItem : order.getOrderItems()) {
				Sku sku = orderItem.getSku();
				if (sku != null) {
					skuService.addAllocatedStock(sku, orderItem.getQuantity() - orderItem.getShippedQuantity());
				}
			}
		}
		order.setIsAllocatedStock(true);
		orderDao.update(order);
	}

	/**
	 * 释放已分配库存
	 * 
	 * @param order
	 *            订单
	 */
	private void releaseAllocatedStock(Order order) {
		if (order == null || BooleanUtils.isNotTrue(order.getIsAllocatedStock())) {
			return;
		}
		if (order.getOrderItems() != null) {
			for (OrderItem orderItem : order.getOrderItems()) {
				Sku sku = orderItem.getSku();
				if (sku != null) {
					skuService.addAllocatedStock(sku, -(orderItem.getQuantity() - orderItem.getShippedQuantity()));
				}
			}
		}
		order.setIsAllocatedStock(false);
		orderDao.update(order);
	}
	
}