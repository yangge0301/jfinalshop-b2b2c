package com.jfinalshop.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BasePaymentTransaction;
import com.jfinalshop.plugin.PaymentPlugin;

/**
 * Model - 支付事务
 * 
 */
public class PaymentTransaction extends BasePaymentTransaction<PaymentTransaction> {
	private static final long serialVersionUID = -9122597889039061839L;
	public static final PaymentTransaction dao = new PaymentTransaction().dao();
	

	/**
	 * 类型
	 */
	public enum Type {

		/**
		 * 订单支付
		 */
		ORDER_PAYMENT,

		/**
		 * 服务支付
		 */
		SVC_PAYMENT,

		/**
		 * 预存款充值
		 */
		DEPOSIT_RECHARGE,

		/**
		 * 保证金支付
		 */
		BAIL_PAYMENT
	}
	
	/**
	 * 父事务
	 */
	private PaymentTransaction parent;

	/**
	 * 订单
	 */
	private Order order;

	/**
	 * 服务
	 */
	private Svc svc;

	/**
	 * 店铺
	 */
	private Store store;

	/**
	 * 用户
	 */
	private Member member;
	
	/**
	 * 商家
	 */
	private Business business;

	/**
	 * 子事务
	 */
	private List<PaymentTransaction> children = new ArrayList<>();
	
	/**
	 * 类型名称
	 */
	public Type getTypeName() {
		return getType() != null ? Type.values()[getType()] : null;
	}
	
	/**
	 * 获取父事务
	 * 
	 * @return 父事务
	 */
	public PaymentTransaction getParent() {
		if (parent == null) {
			parent = PaymentTransaction.dao.findById(getParentId());
		}
		return parent;
	}

	/**
	 * 设置父事务
	 * 
	 * @param parent
	 *            父事务
	 */
	public void setParent(PaymentTransaction parent) {
		this.parent = parent;
	}

	/**
	 * 获取订单
	 * 
	 * @return 订单
	 */
	public Order getOrder() {
		if (order == null) {
			order = Order.dao.findById(getOrderId());
		}
		return order;
	}

	/**
	 * 设置订单
	 * 
	 * @param order
	 *            订单
	 */
	public void setOrder(Order order) {
		setOrderId(order.getId());
	}

	/**
	 * 获取服务
	 * 
	 * @return 服务
	 */
	public Svc getSvc() {
		if (svc == null) {
			svc = Svc.dao.findById(getSvcId());
		}
		return svc;
	}

	/**
	 * 设置服务
	 * 
	 * @param svc
	 *            服务
	 */
	public void setSvc(Svc svc) {
		setSvcId(svc.getId());
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
		setStoreId(store.getId());
	}

	/**
	 * 获取用户
	 * 
	 * @return 用户
	 */
	public Business getBusiness() {
		if (business == null) {
			business = Business.dao.findById(getBusinessId());
		}
		return business;
	}

	/**
	 * 设置用户
	 * 
	 * @param business
	 *            用户
	 */
	public void setBusiness(Business business) {
		setBusinessId(business.getId());
	}
	
	/**
	 * 获取用户
	 * 
	 * @return 用户
	 */
	public Member getMember() {
		if (member == null) {
			member = Member.dao.findById(getMemberId());
		}
		return member;
	}

	/**
	 * 设置用户
	 * 
	 * @param member
	 *            用户
	 */
	public void setMember(Member member) {
		setMemberId(member.getId());
	}

	/**
	 * 获取子事务
	 * 
	 * @return 子事务
	 */
	public List<PaymentTransaction> getChildren() {
		if (CollectionUtils.isEmpty(children)) {
			String sql = "SELECT * FROM `payment_transaction` WHERE parent_id = ?";
			children = PaymentTransaction.dao.find(sql, getId());
		}
		return children;
	}

	/**
	 * 设置子事务
	 * 
	 * @param children
	 *            子事务
	 */
	public void setChildren(List<PaymentTransaction> children) {
		this.children = children;
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
	 * 获取有效金额
	 * 
	 * @return 有效金额
	 */
	public BigDecimal getEffectiveAmount() {
		BigDecimal effectiveAmount = getAmount().subtract(getFee());
		return effectiveAmount.compareTo(BigDecimal.ZERO) >= 0 ? effectiveAmount : BigDecimal.ZERO;
	}

	/**
	 * 设置支付插件
	 * 
	 * @param paymentPlugin
	 *            支付插件
	 */
	public void setPaymentPlugin(PaymentPlugin paymentPlugin) {
		setPaymentPluginId(paymentPlugin != null ? paymentPlugin.getId() : null);
		setPaymentPluginName(paymentPlugin != null ? paymentPlugin.getName() : null);
	}

	/**
	 * 设置支付目标
	 * 
	 * @param target
	 *            支付目标
	 */
	public void setTarget(Object target) {
		if (target == null) {
			return;
		}
		if (target instanceof Order) {
			setOrder((Order) target);
		} else if (target instanceof Member) {
			setMember((Member) target);
		} else if (target instanceof Business) {
			setBusiness((Business) target);
		} else if (target instanceof Store) {
			setStore((Store) target);
		} else if (target instanceof Svc) {
			setSvc((Svc) target);
		}
	}

	/**
	 * 支付明细
	 * 
	 */
	public abstract static class LineItem {
		/**
		 * 金额
		 */
		private BigDecimal amount;

		/**
		 * 获取金额
		 * 
		 * @return 金额
		 */
		public BigDecimal getAmount() {
			return amount;
		}

		/**
		 * 设置金额
		 * 
		 * @param amount
		 *            金额
		 */
		public void setAmount(BigDecimal amount) {
			this.amount = amount;
		}

		/**
		 * 获取支付事务类型
		 * 
		 * @return 支付事务类型
		 */
		public abstract PaymentTransaction.Type getType();

		/**
		 * 获取支付目标
		 * 
		 * @return 支付目标
		 */
		public abstract Object getTarget();
	}

	/**
	 * 订单支付明细
	 * 
	 */
	public static class OrderLineItem extends LineItem {

		/**
		 * 订单
		 */
		private Order order;

		/**
		 * 构造方法
		 * 
		 * @param order
		 *            订单
		 */
		public OrderLineItem(Order order) {
			this.order = order;
			super.amount = order.getAmountPayable();
		}

		/**
		 * 获取订单
		 * 
		 * @return 订单
		 */
		public Order getOrder() {
			return order;
		}

		/**
		 * 设置订单
		 * 
		 * @param order
		 *            订单
		 */
		public void setOrder(Order order) {
			this.order = order;
		}

		@Override
		public Type getType() {
			return PaymentTransaction.Type.ORDER_PAYMENT;
		}

		@Override
		public Object getTarget() {
			return this.order;
		}
	}

	/**
	 * 服务支付明细
	 * 
	 */
	public static class SvcLineItem extends LineItem {

		/**
		 * 服务
		 */
		private Svc svc;

		/**
		 * 构造方法
		 * 
		 * @param svc
		 *            服务
		 */
		public SvcLineItem(Svc svc) {
			this.svc = svc;
			super.amount = svc.getAmount();
		}

		/**
		 * 获取服务
		 * 
		 * @return 服务
		 */
		public Svc getSvc() {
			return svc;
		}

		/**
		 * 设置服务
		 * 
		 * @param svc
		 *            服务
		 */
		public void setSvc(Svc svc) {
			this.svc = svc;
		}

		@Override
		public Type getType() {
			return PaymentTransaction.Type.SVC_PAYMENT;
		}

		@Override
		public Object getTarget() {
			return this.svc;
		}
	}

	/**
	 * 预存款充值明细
	 * 
	 */
	public static class DepositRechargerBusinessLineItem extends LineItem {

		/**
		 * 用户
		 */
		private Business business;

		/**
		 * 构造方法
		 * 
		 * @param member
		 *            用户
		 * @param amount
		 *            金额
		 */
		public DepositRechargerBusinessLineItem(Business business, BigDecimal amount) {
			this.business = business;
			super.amount = amount;
		}

		/**
		 * 获取用户
		 * 
		 * @return 用户
		 */
		public Business getBusiness() {
			return business;
		}

		/**
		 * 设置用户
		 * 
		 * @param user
		 *            用户
		 */
		public void setBusiness(Business business) {
			this.business = business;
		}

		@Override
		public Type getType() {
			return PaymentTransaction.Type.DEPOSIT_RECHARGE;
		}

		@Override
		public Object getTarget() {
			return this.business;
		}
	}

	/**
	 * 预存款充值明细
	 * 
	 */
	public static class DepositRechargerMemberLineItem extends LineItem {

		/**
		 * 商家
		 */
		private Member member;

		/**
		 * 构造方法
		 * 
		 * @param member
		 *            用户
		 * @param amount
		 *            金额
		 */
		public DepositRechargerMemberLineItem(Member member, BigDecimal amount) {
			this.member = member;
			super.amount = amount;
		}

		/**
		 * 获取用户
		 * 
		 * @return 用户
		 */
		public Member getMember() {
			return member;
		}

		/**
		 * 设置用户
		 * 
		 * @param user
		 *            用户
		 */
		public void setMember(Member member) {
			this.member = member;
		}

		@Override
		public Type getType() {
			return PaymentTransaction.Type.DEPOSIT_RECHARGE;
		}

		@Override
		public Object getTarget() {
			return this.member;
		}
	}
	
	/**
	 * 保证金支付明细
	 * 
	 */
	public static class BailPaymentLineItem extends LineItem {

		/**
		 * 店铺
		 */
		private Store store;

		/**
		 * 构造方法
		 * 
		 * @param store
		 *            店铺
		 * @param amount
		 *            金额
		 */
		public BailPaymentLineItem(Store store, BigDecimal amount) {
			this.store = store;
			super.amount = amount;
		}

		/**
		 * 获取店铺
		 * 
		 * @return 店铺
		 */
		public Store getStore() {
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

		@Override
		public Type getType() {
			return PaymentTransaction.Type.BAIL_PAYMENT;
		}

		@Override
		public Object getTarget() {
			return this.store;
		}
	}
}
