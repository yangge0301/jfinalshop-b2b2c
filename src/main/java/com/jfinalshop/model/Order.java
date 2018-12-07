package com.jfinalshop.model;

import com.jfinalshop.entity.Invoice;
import com.jfinalshop.model.base.BaseOrder;
import com.jfinalshop.util.JsonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Model - 订单
 * 
 */
public class Order extends BaseOrder<Order> {
	private static final long serialVersionUID = 3491084708343393819L;
	public static final Order dao = new Order().dao();
	
	/**
	 * "订单锁"缓存名称
	 */
	public static final String ORDER_LOCK_CACHE_NAME = "orderLock";


	/**
	 * 类型
	 */
	public enum Type {

		/**
		 * 普通订单
		 */
		general,

		/**
		 * 兑换订单
		 */
		exchange
	}

	/**
	 * 来源
	 */
	public enum Source {

		/** PC */
		PC,

		/** H5 */
		H5,
		
		/** IOS */
		IOS,
		
		/** android */
		ANDROID,
		
		/** 店铺 */
		HELPDESK,
		
		/** 小程序 */
		MINIWX
	}
	
	/**
	 * 状态
	 */
	public enum Status {

		/** 等待付款 0 */
		pendingPayment,

		/** 等待接单中 1 */
		pendingReview,

		/** 等待发货 2 */
		pendingShipment,

		/** 已发货 3 */
		shipped,

		/** 已收货 4 */
		received,

		/** 已完成 5 */
		completed,

		/** 已失败 6 */
		failed,

		/** 已取消 7 */
		canceled,

		/** 已拒绝 8 */
		denied,
		
		/** 未完成 9 */
		unfinished,
		
		/** 退款中 10 */
		refunding,
		
		/** 退款完成 11 */
		refunded,
		
		/** 所有已取消 12 */
		allCanceled,
		
		/** 已评价 13 */
		reviewed
	}

	/**
	 * 发票
	 */
	private Invoice invoice;
	
	/**
	 * 地区
	 */
	private Area area;

	/**
	 * 支付方式
	 */
	private PaymentMethod paymentMethod;

	/**
	 * 配送方式
	 */
	private ShippingMethod shippingMethod;

	/**
	 * 会员
	 */
	private Member member;

	/**
	 * 优惠码
	 */
	private CouponCode couponCode;

	/**
	 * 店铺
	 */
	private Store store;

	/**
	 * 促销名称
	 */
	private List<String> promotionNames = new ArrayList<String>();

	/**
	 * 赠送优惠券
	 */
	private List<Coupon> coupons = new ArrayList<Coupon>();

	/**
	 * 订单项
	 */
	private List<OrderItem> orderItems = new ArrayList<OrderItem>();

	/**
	 * 支付事务
	 */
	private List<PaymentTransaction> paymentTransactions = new ArrayList<PaymentTransaction>();

	/**
	 * 订单支付
	 */
	private List<OrderPayment> orderPayments = new ArrayList<OrderPayment>();

	/**
	 * 订单退款
	 */
	private List<OrderRefunds> orderRefunds = new ArrayList<OrderRefunds>();

	/**
	 * 订单发货
	 */
	private List<OrderShipping> orderShippings = new ArrayList<OrderShipping>();

	/**
	 * 订单退货
	 */
	private List<OrderReturns> orderReturns = new ArrayList<OrderReturns>();

	/**
	 * 订单记录
	 */
	private List<OrderLog> orderLogs = new ArrayList<OrderLog>();
	
	
	/**
	 * 获取类型
	 * 
	 * @return 类型
	 */
	public Order.Type getTypeName() {
		return getType() == null ? null : Type.values()[getType()];
	}
	
	/**
	 * 获取状态
	 * 
	 * @return 状态
	 */
	public Order.Status getStatusName() {
		return getStatus() == null ? null : Status.values()[getStatus()];
	}
	
	/**
	 * 获取发票
	 * 
	 * @return 发票
	 */
	public Invoice getInvoice() {
		invoice = new Invoice();
		invoice.setContent(getInvoiceContent());
		invoice.setTitle(getInvoiceTitle());
		return invoice;
	}

	/**
	 * 设置发票
	 * 
	 * @param invoice
	 *            发票
	 */
	public void setInvoice(Invoice invoice) {
		if (invoice != null) {
			setInvoiceContent(invoice.getContent());
			setInvoiceTitle(invoice.getTitle());
		}
	}
	
	/**
	 * 获取地区
	 * 
	 * @return 地区
	 */
	public Area getArea() {
		if (area == null) {
			area = Area.dao.findById(getAreaId());
		}
		return area;
	}

	/**
	 * 设置地区
	 * 
	 * @param area
	 *            地区
	 */
	public void setArea(Area area) {
		this.area = area;
	}

	/**
	 * 获取支付方式
	 * 
	 * @return 支付方式
	 */
	public PaymentMethod getPaymentMethod() {
		if (paymentMethod == null) {
			paymentMethod = PaymentMethod.dao.findById(getPaymentMethodId());
		}
		return paymentMethod;
	}

	/**
	 * 设置支付方式
	 * 
	 * @param paymentMethod
	 *            支付方式
	 */
	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	/**
	 * 获取配送方式
	 * 
	 * @return 配送方式
	 */
	public ShippingMethod getShippingMethod() {
		if (shippingMethod == null) {
			shippingMethod = ShippingMethod.dao.findById(getShippingMethodId());
		}
		return shippingMethod;
	}

	/**
	 * 设置配送方式
	 * 
	 * @param shippingMethod
	 *            配送方式
	 */
	public void setShippingMethod(ShippingMethod shippingMethod) {
		this.shippingMethod = shippingMethod;
	}

	/**
	 * 获取会员
	 * 
	 * @return 会员
	 */
	public Member getMember() {
		if (member == null) {
			member = Member.dao.findById(getMemberId());
		}
		return member;
	}

	/**
	 * 设置会员
	 * 
	 * @param member
	 *            会员
	 */
	public void setMember(Member member) {
		this.member = member;
	}

	/**
	 * 获取优惠码
	 * 
	 * @return 优惠码
	 */
	public CouponCode getCouponCode() {
		if (couponCode == null) {
			couponCode = CouponCode.dao.findById(getCouponCodeId());
		}
		return couponCode;
	}

	/**
	 * 设置优惠码
	 * 
	 * @param couponCode
	 *            优惠码
	 */
	public void setCouponCode(CouponCode couponCode) {
		this.couponCode = couponCode;
	}

	/**
	 * 获取店铺
	 * 
	 * @return 店铺
	 */
	public Store getStore() {
		if (store == null) {
			store = Store.dao.findById(getStoreId());
		}
		return store;
	}

	/**
	 * 设置店铺
	 * 
	 * @param store
	 *            店铺
	 */
	public void setStore(Store store) {
		this.store = store;
	}

	/**
	 * 获取促销名称
	 * 
	 * @return 促销名称
	 */
	public List<String> getPromotionNamesConverter() {
		if (CollectionUtils.isEmpty(promotionNames)) {
			promotionNames = JsonUtils.convertJsonStrToList(getPromotionNames());
		}
		return promotionNames;
	}

	/**
	 * 设置促销名称
	 * 
	 * @param promotionNames
	 *            促销名称
	 */
	public void setPromotionNames(List<String> promotionNames) {
		this.promotionNames = promotionNames;
	}

	/**
	 * 获取赠送优惠券
	 * 
	 * @return 赠送优惠券
	 */
	public List<Coupon> getCoupons() {
		if (CollectionUtils.isEmpty(coupons)) {
			String sql = "SELECT * FROM `coupon` WHERE id = ?";
			coupons = Coupon.dao.find(sql, getCouponCodeId());
		}
		return coupons;
	}

	/**
	 * 设置赠送优惠券
	 * 
	 * @param coupons
	 *            赠送优惠券
	 */
	public void setCoupons(List<Coupon> coupons) {
		this.coupons = coupons;
	}

	/**
	 * 获取订单项
	 * 
	 * @return 订单项
	 */
	public List<OrderItem> getOrderItems() {
		if (CollectionUtils.isEmpty(orderItems)) {
			String sql = "SELECT * FROM `order_item` WHERE order_id = ?";
			orderItems = OrderItem.dao.find(sql, getId());
		}
		return orderItems;
	}

	/**
	 * 设置订单项
	 * 
	 * @param orderItems
	 *            订单项
	 */
	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}

	/**
	 * 获取支付事务
	 * 
	 * @return 支付事务
	 */
	public List<PaymentTransaction> getPaymentTransactions() {
		if (CollectionUtils.isEmpty(paymentTransactions)) {
			String sql = "SELECT * FROM `payment_transaction` WHERE order_id = ?";
			paymentTransactions = PaymentTransaction.dao.find(sql, getId());
		}
		return paymentTransactions;
	}

	/**
	 * 设置支付事务
	 * 
	 * @param paymentTransactions
	 *            支付事务
	 */
	public void setPaymentTransactions(List<PaymentTransaction> paymentTransactions) {
		this.paymentTransactions = paymentTransactions;
	}

	/**
	 * 获取订单支付
	 * 
	 * @return 订单支付
	 */
	public List<OrderPayment> getOrderPayments() {
		if (CollectionUtils.isEmpty(orderPayments)) {
			String sql = "SELECT * FROM `order_payment` WHERE order_id = ?";
			orderPayments = OrderPayment.dao.find(sql, getId());
		}
		return orderPayments;
	}

	/**
	 * 设置订单支付
	 * 
	 * @param orderPayments
	 *            订单支付
	 */
	public void setOrderPayments(List<OrderPayment> orderPayments) {
		this.orderPayments = orderPayments;
	}

	/**
	 * 获取订单退款
	 * 
	 * @return 订单退款
	 */
	public List<OrderRefunds> getOrderRefunds() {
		if (CollectionUtils.isEmpty(orderRefunds)) {
			String sql = "SELECT * FROM `order_refunds` WHERE order_id = ?";
			orderRefunds = OrderRefunds.dao.find(sql, getId());
		}
		return orderRefunds;
	}

	/**
	 * 设置订单退款
	 * 
	 * @param orderRefunds
	 *            订单退款
	 */
	public void setOrderRefunds(List<OrderRefunds> orderRefunds) {
		this.orderRefunds = orderRefunds;
	}

	/**
	 * 获取订单发货
	 * 
	 * @return 订单发货
	 */
	public List<OrderShipping> getOrderShippings() {
		if (CollectionUtils.isEmpty(orderShippings)) {
			String sql = "SELECT * FROM `order_shipping` WHERE order_id = ?";
			orderShippings = OrderShipping.dao.find(sql, getId());
		}
		return orderShippings;
	}

	/**
	 * 设置订单发货
	 * 
	 * @param orderShippings
	 *            订单发货
	 */
	public void setOrderShippings(List<OrderShipping> orderShippings) {
		this.orderShippings = orderShippings;
	}

	/**
	 * 获取订单退货
	 * 
	 * @return 订单退货
	 */
	public List<OrderReturns> getOrderReturns() {
		if (CollectionUtils.isEmpty(orderReturns)) {
			String sql = "SELECT * FROM `order_returns` WHERE order_id = ?";
			orderReturns = OrderReturns.dao.find(sql, getId());
		}
		return orderReturns;
	}

	/**
	 * 设置订单退货
	 * 
	 * @param orderReturns
	 *            订单退货
	 */
	public void setOrderReturns(List<OrderReturns> orderReturns) {
		this.orderReturns = orderReturns;
	}

	/**
	 * 获取订单记录
	 * 
	 * @return 订单记录
	 */
	public List<OrderLog> getOrderLogs() {
		if (CollectionUtils.isEmpty(orderLogs)) {
			String sql = "SELECT * FROM `order_log` WHERE order_id = ?";
			orderLogs = OrderLog.dao.find(sql, getId());
		}
		return orderLogs;
	}

	/**
	 * 设置订单记录
	 * 
	 * @param orderLogs
	 *            订单记录
	 */
	public void setOrderLogs(List<OrderLog> orderLogs) {
		this.orderLogs = orderLogs;
	}

	/**
	 * 获取是否需要物流
	 * 
	 * @return 是否需要物流
	 */
	public boolean getIsDelivery() {
		return CollectionUtils.exists(getOrderItems(), new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				OrderItem orderItem = (OrderItem) object;
				return orderItem != null && BooleanUtils.isTrue(orderItem.getIsDelivery());
			}
		});
	}

	/**
	 * 获取应付金额
	 * 
	 * @return 应付金额
	 */
	public BigDecimal getAmountPayable() {
		if (!hasExpired() && !Order.Status.completed.equals(getStatusName()) && !Order.Status.failed.equals(getStatusName()) && !Order.Status.canceled.equals(getStatusName()) && !Order.Status.denied.equals(getStatusName())) {
			BigDecimal amountPayable = getAmount().subtract(getAmountPaid());
			return amountPayable.compareTo(BigDecimal.ZERO) >= 0 ? amountPayable : BigDecimal.ZERO;
		}
		return BigDecimal.ZERO;
	}

	/**
	 * 获取应收金额
	 * 
	 * @return 应收金额
	 */
	public BigDecimal getAmountReceivable() {
		if (!hasExpired() && PaymentMethod.Type.cashOnDelivery.equals(getPaymentMethodType()) && !Order.Status.completed.equals(getStatusName()) && !Order.Status.failed.equals(getStatusName()) && !Order.Status.canceled.equals(getStatusName()) && !Order.Status.denied.equals(getStatusName())) {
			BigDecimal amountReceivable = getAmount().subtract(getAmountPaid());
			return amountReceivable.compareTo(BigDecimal.ZERO) >= 0 ? amountReceivable : BigDecimal.ZERO;
		}
		return BigDecimal.ZERO;
	}

	/**
	 * 获取应退金额
	 * 
	 * @return 应退金额
	 */
	public BigDecimal getRefundableAmount() {
		if (hasExpired() || Order.Status.failed.equals(getStatusName()) || Order.Status.canceled.equals(getStatusName()) || Order.Status.denied.equals(getStatusName())) {
			BigDecimal refundableAmount = getAmountPaid();
			return refundableAmount.compareTo(BigDecimal.ZERO) >= 0 ? refundableAmount : BigDecimal.ZERO;
		}
		if (Order.Status.completed.equals(getStatusName())) {
			BigDecimal refundableAmount = getAmountPaid().subtract(getAmount());
			return refundableAmount.compareTo(BigDecimal.ZERO) >= 0 ? refundableAmount : BigDecimal.ZERO;
		}
		return BigDecimal.ZERO;
	}

	/**
	 * 获取佣金
	 * 
	 * @return 佣金
	 */
	public BigDecimal getCommission() {
		BigDecimal commission = BigDecimal.ZERO;
		if (getOrderItems() != null) {
			for (OrderItem orderItem : getOrderItems()) {
				if (orderItem != null && orderItem.getCommissionTotals().compareTo(BigDecimal.ZERO) > 0) {
					commission = commission.add(orderItem.getCommissionTotals());
				}
			}
		}
		return commission;
	}

	/**
	 * 获取结算金额
	 * 
	 * @return 结算金额
	 */
	public BigDecimal getSettlementAmount() {
		return getCommission() != null && getCommission().compareTo(BigDecimal.ZERO) > 0 ? getAmount().subtract(getCommission()) : getAmount();
	}

	/**
	 * 获取可发货数
	 * 
	 * @return 可发货数
	 */
	public int getShippableQuantity() {
		if (!hasExpired() && Order.Status.pendingShipment.equals(getStatusName())) {
			int shippableQuantity = getQuantity() - getShippedQuantity();
			return shippableQuantity >= 0 ? shippableQuantity : 0;
		}
		return 0;
	}

	/**
	 * 获取可退货数
	 * 
	 * @return 可退货数
	 */
	public int getReturnableQuantity() {
		if (!hasExpired() && Order.Status.failed.equals(getStatusName())) {
			int returnableQuantity = getShippedQuantity() - getReturnedQuantity();
			return returnableQuantity >= 0 ? returnableQuantity : 0;
		}
		return 0;
	}

	/**
	 * 判断是否已过期
	 * 
	 * @return 是否已过期
	 */
	public boolean hasExpired() {
		return getExpire() != null && !getExpire().after(new Date());
	}

	/**
	 * 获取订单项
	 * 
	 * @param sn
	 *            SKU编号
	 * @return 订单项
	 */
	public OrderItem getOrderItem(String sn) {
		if (StringUtils.isEmpty(sn) || CollectionUtils.isEmpty(getOrderItems())) {
			return null;
		}
		for (OrderItem orderItem : getOrderItems()) {
			if (orderItem != null && StringUtils.equalsIgnoreCase(orderItem.getSn(), sn)) {
				return orderItem;
			}
		}
		return null;
	}

	/**
	 * 判断订单是否允许刪除
	 * 
	 * @return 订单是否允许刪除
	 */
	public boolean canDelete() {
		return Order.Status.canceled.equals(getStatusName()) || Order.Status.failed.equals(getStatusName()) || Order.Status.denied.equals(getStatusName());
	}

	/**
	 * 判断是否为新建对象
	 * 
	 * @return 是否为新建对象
	 */
	public boolean isNew() {
		return getId() == null;
	}
	
}
