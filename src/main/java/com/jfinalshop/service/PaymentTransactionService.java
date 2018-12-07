package com.jfinalshop.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.inject.Singleton;

import net.hasor.core.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.jfinalshop.Setting;
import com.jfinalshop.dao.PaymentTransactionDao;
import com.jfinalshop.dao.SnDao;
import com.jfinalshop.entity.PaymentItem;
import com.jfinalshop.entity.PromotionPluginSvc;
import com.jfinalshop.model.Business;
import com.jfinalshop.model.BusinessDepositLog;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.MemberDepositLog;
import com.jfinalshop.model.Order;
import com.jfinalshop.model.OrderPayment;
import com.jfinalshop.model.PaymentTransaction;
import com.jfinalshop.model.PaymentTransaction.LineItem;
import com.jfinalshop.model.Sn;
import com.jfinalshop.model.Store;
import com.jfinalshop.model.Svc;
import com.jfinalshop.plugin.PaymentPlugin;
import com.jfinalshop.plugin.discountPromotion.DiscountPromotionPlugin;
import com.jfinalshop.plugin.fullReductionPromotion.FullReductionPromotionPlugin;
import com.jfinalshop.util.Assert;
import com.jfinalshop.util.SystemUtils;

/**
 * Service - 支付事务
 * 
 */
@Singleton
public class PaymentTransactionService extends BaseService<PaymentTransaction> {

	/**
	 * 构造方法
	 */
	public PaymentTransactionService() {
		super(PaymentTransaction.class);
	}
	
	@Inject
	private PaymentTransactionDao paymentTransactionDao;
	@Inject
	private SnDao snDao;
	@Inject
	private ProductService productService;
	@Inject
	private OrderService orderService;
	@Inject
	private MemberService memberService;
	@Inject
	private BusinessService businessService;
	@Inject
	private StoreService storeService;
	@Inject
	private SvcService svcService;
	
	/**
	 * 查询订单支付成功记录
	 * @author yangzhicong
	 * @param paymentPluginId 支付插件ID, 不能为空
	 * @param order 订单, 不能为空
	 * @return
	 */
	public PaymentTransaction findSuccessOrderPayment(String paymentPluginId, Order order) {
		return paymentTransactionDao.findSuccessOrderPayment(paymentPluginId, order);
	}
	
	/**
	 * 根据编号查找支付事务
	 * 
	 * @param sn
	 *            编号(忽略大小写)
	 * @return 支付事务，若不存在则返回null
	 */
	public PaymentTransaction findBySn(String sn) {
		return paymentTransactionDao.find("sn", StringUtils.lowerCase(sn));
	}

	/**
	 * 生成支付事务
	 * 
	 * @param lineItem
	 *            支付明细
	 * @param paymentPlugin
	 *            支付插件
	 * @return 支付事务
	 */
	public PaymentTransaction generate(PaymentTransaction.LineItem lineItem, PaymentPlugin paymentPlugin) {
		Assert.notNull(lineItem);
		Assert.notNull(paymentPlugin);
		Assert.notNull(lineItem.getAmount());
		Assert.notNull(lineItem.getType());
		Assert.notNull(lineItem.getTarget());

		PaymentTransaction paymentTransaction = paymentTransactionDao.findAvailable(lineItem, paymentPlugin);
		if (paymentTransaction == null) {
			paymentTransaction = new PaymentTransaction();
			paymentTransaction.setSn(snDao.generate(Sn.Type.paymentTransaction));
			paymentTransaction.setType(lineItem.getType().ordinal());
			paymentTransaction.setAmount(paymentPlugin.calculateAmount(lineItem.getAmount()));
			paymentTransaction.setFee(paymentPlugin.calculateFee(lineItem.getAmount()));
			paymentTransaction.setIsSuccess(false);
			paymentTransaction.setExpire(DateUtils.addSeconds(new Date(), paymentPlugin.getTimeout()));
			paymentTransaction.setParent(null);
			paymentTransaction.setChildren(null);
			paymentTransaction.setTarget(lineItem.getTarget());
			paymentTransaction.setPaymentPlugin(paymentPlugin);
			paymentTransactionDao.save(paymentTransaction);
		}
		return paymentTransaction;
	}

	/**
	 * 生成父支付事务
	 * 
	 * @param lineItems
	 *            支付明细
	 * @param paymentPlugin
	 *            支付插件
	 * @return 父支付事务
	 */
	public PaymentTransaction generateParent(Collection<PaymentTransaction.LineItem> lineItems, PaymentPlugin paymentPlugin) {
		Assert.notEmpty(lineItems);
		Assert.notNull(paymentPlugin);
		Assert.isTrue(lineItems.size() > 1);

		PaymentTransaction parentPaymentTransaction = paymentTransactionDao.findAvailableParent(lineItems, paymentPlugin);
		if (parentPaymentTransaction == null) {
			BigDecimal amount = BigDecimal.ZERO;
			for (PaymentTransaction.LineItem lineItem : lineItems) {
				Assert.notNull(lineItem);
				Assert.notNull(lineItem.getAmount());
				Assert.notNull(lineItem.getType());
				Assert.notNull(lineItem.getTarget());

				amount = amount.add(lineItem.getAmount());
			}

			parentPaymentTransaction = new PaymentTransaction();
			parentPaymentTransaction.setSn(snDao.generate(Sn.Type.paymentTransaction));
			parentPaymentTransaction.setType(null);
			parentPaymentTransaction.setAmount(paymentPlugin.calculateAmount(amount));
			parentPaymentTransaction.setFee(paymentPlugin.calculateFee(amount));
			parentPaymentTransaction.setIsSuccess(false);
			parentPaymentTransaction.setExpire(DateUtils.addSeconds(new Date(), paymentPlugin.getTimeout()));
			parentPaymentTransaction.setParent(null);
			parentPaymentTransaction.setChildren(null);
			parentPaymentTransaction.setTarget(null);
			parentPaymentTransaction.setPaymentPlugin(paymentPlugin);
			paymentTransactionDao.save(parentPaymentTransaction);
			for (PaymentTransaction.LineItem lineItem : lineItems) {
				Assert.notNull(lineItem);
				Assert.notNull(lineItem.getAmount());
				Assert.notNull(lineItem.getType());
				Assert.notNull(lineItem.getTarget());

				PaymentTransaction paymentTransaction = new PaymentTransaction();
				paymentTransaction.setSn(snDao.generate(Sn.Type.paymentTransaction));
				paymentTransaction.setType(lineItem.getType().ordinal());
				paymentTransaction.setAmount(paymentPlugin.calculateAmount(lineItem.getAmount()));
				paymentTransaction.setFee(paymentPlugin.calculateFee(lineItem.getAmount()));
				paymentTransaction.setIsSuccess(null);
				paymentTransaction.setExpire(null);
				paymentTransaction.setChildren(null);
				paymentTransaction.setTarget(lineItem.getTarget());
				paymentTransaction.setPaymentPlugin(null);
				paymentTransaction.setParentId(parentPaymentTransaction.getId());
				paymentTransactionDao.save(paymentTransaction);
			}
		}
		return parentPaymentTransaction;
	}

	/**
	 * 支付处理
	 * 
	 * @param paymentTransaction
	 *            支付事务
	 */
	public void handle(PaymentTransaction paymentTransaction) {
		Assert.notNull(paymentTransaction);

		if (BooleanUtils.isNotFalse(paymentTransaction.getIsSuccess())) {
			return;
		}

		List<PaymentTransaction> paymentTransactions = new ArrayList<>();
		List<PaymentTransaction> childrenList = paymentTransaction.getChildren();
		if (CollectionUtils.isNotEmpty(childrenList)) {
			paymentTransaction.setIsSuccess(true);
			paymentTransactions = childrenList;
		} else {
			paymentTransactions.add(paymentTransaction);
		}

		for (PaymentTransaction transaction : paymentTransactions) {
			Svc svc = transaction.getSvc();
			Store store = transaction.getStore();
			
			BigDecimal effectiveAmount = transaction.getEffectiveAmount();

			Assert.notNull(transaction.getTypeName());
			switch (transaction.getTypeName()) {
			case ORDER_PAYMENT:
				Order order = transaction.getOrder();
				if (order != null) {
					OrderPayment orderPayment = new OrderPayment();
					orderPayment.setMethod(OrderPayment.Method.online.ordinal());
					orderPayment.setPaymentMethod(transaction.getPaymentPluginName());
					orderPayment.setAmount(transaction.getAmount());
					orderPayment.setFee(transaction.getFee());
					orderPayment.setOrderId(order.getId());
					orderService.payment(order, orderPayment);
				}
				break;
			case SVC_PAYMENT:
				if (svc == null || svc.getStore() == null) {
					break;
				}
				store = svc.getStore();

				Integer durationDays = svc.getDurationDays();
				if (svc instanceof Svc) {
					storeService.addEndDays(store, durationDays);
					if (Store.Status.approved.equals(store.getStatusName()) && !store.hasExpired() && store.getBailPayable().compareTo(BigDecimal.ZERO) == 0) {
						store.setStatus(Store.Status.success.ordinal());
					} else {
						productService.refreshActive(store);
					}
					storeService.update(store);
				} else if (svc instanceof PromotionPluginSvc) {
					String promotionPluginId = ((PromotionPluginSvc) svc).getPromotionPluginId();
					switch (promotionPluginId) {
					case DiscountPromotionPlugin.ID:
						storeService.addDiscountPromotionEndDays(store, durationDays);
						break;
					case FullReductionPromotionPlugin.ID:
						storeService.addFullReductionPromotionEndDays(store, durationDays);
						break;
					default:
						break;
					}
				}
				break;
			case DEPOSIT_RECHARGE:
				Business business = businessService.find(transaction.getBusinessId());
				Member member = memberService.find(transaction.getMemberId());
				if (member instanceof Member) {
					memberService.addBalance(member, effectiveAmount, MemberDepositLog.Type.recharge, null);
				} else if (business instanceof Business) {
					businessService.addBalance(business, effectiveAmount, BusinessDepositLog.Type.recharge, null);
				}
				break;
			case BAIL_PAYMENT:
				if (store == null) {
					break;
				}

				storeService.addBailPaid(store, effectiveAmount);
				if (Store.Status.approved.equals(store.getStatusName()) && !store.hasExpired() && store.getBailPayable().compareTo(BigDecimal.ZERO) == 0) {
					store.setStatus(Store.Status.success.ordinal());
				} else {
					productService.refreshActive(store);
				}
				break;
			default:
				break;
			}
			transaction.setIsSuccess(true);
			paymentTransactionDao.update(transaction);
		}
	}

	/**
	 * 生成支付明细
	 * 
	 * @param paymentItem
	 *            支付项
	 * @return 支付明细
	 */
	public LineItem generate(PaymentItem paymentItem, Member member) {
		if (paymentItem == null || paymentItem.getType() == null) {
			return null;
		}
		Setting setting = SystemUtils.getSetting();
		Business business;
		switch (paymentItem.getType()) {
		case ORDER_PAYMENT:
			if (member == null) {
				member = memberService.getCurrentUser();
			}
			if (member == null) {
				return null;
			}
			Order order = orderService.findBySn(paymentItem.getOrderSn());
			if (order == null || !member.getId().equals(order.getMemberId()) || !orderService.acquireLock(order, member)) {
				return null;
			}
			if (order.getPaymentMethod() == null) {
				return null;
			}
			if (order.getAmountPayable().compareTo(BigDecimal.ZERO) <= 0) {
				return null;
			}
			return new PaymentTransaction.OrderLineItem(order);
		case SVC_PAYMENT:
			Svc svc = svcService.findBySn(paymentItem.getSvcSn());
			if (svc == null) {
				return null;
			}
			if (svc.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
				return null;
			}
			return new PaymentTransaction.SvcLineItem(svc);
		case DEPOSIT_RECHARGE:
			business = businessService.getCurrentUser();
			if (member == null) {
				member = memberService.getCurrentUser();
			}
			if (business == null && member == null) {
				return null;
			}
			if (paymentItem.getAmount() == null || paymentItem.getAmount().compareTo(BigDecimal.ZERO) <= 0 || paymentItem.getAmount().precision() > 15 || paymentItem.getAmount().scale() > setting.getPriceScale()) {
				return null;
			}
			if (member instanceof Member) {
				return new PaymentTransaction.DepositRechargerMemberLineItem(member, paymentItem.getAmount());
			} else if (business instanceof Business) {
				return new PaymentTransaction.DepositRechargerBusinessLineItem(business, paymentItem.getAmount());
			} else {
				return null;
			}
		case BAIL_PAYMENT:
			Store store = storeService.getCurrent();
			if (store == null) {
				return null;
			}
			if (paymentItem.getAmount() == null || paymentItem.getAmount().compareTo(BigDecimal.ZERO) <= 0 || paymentItem.getAmount().precision() > 15 || paymentItem.getAmount().scale() > setting.getPriceScale()) {
				return null;
			}
			return new PaymentTransaction.BailPaymentLineItem(store, paymentItem.getAmount());
			
		default:
			return null;
		}
	}

	@Override
	public PaymentTransaction save(PaymentTransaction paymentTransaction) {
		Assert.notNull(paymentTransaction);
		paymentTransaction.setSn(snDao.generate(Sn.Type.paymentTransaction));

		return super.save(paymentTransaction);
	}

}