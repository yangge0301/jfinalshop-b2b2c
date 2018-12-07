package com.jfinalshop.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.jfinal.kit.StrKit;
import com.jfinalshop.model.Order;
import com.jfinalshop.model.PaymentTransaction;
import com.jfinalshop.model.PaymentTransaction.LineItem;
import com.jfinalshop.plugin.PaymentPlugin;
import com.jfinalshop.util.Assert;

/**
 * Dao - 支付事务
 * 
 */
public class PaymentTransactionDao extends BaseDao<PaymentTransaction> {
	
	/**
	 * 构造方法
	 */
	public PaymentTransactionDao() {
		super(PaymentTransaction.class);
	}

	/**
	 * 查找可用支付事务
	 * 
	 * @param lineItem
	 *            支付明细
	 * @param paymentPlugin
	 *            支付插件
	 * @return 可用支付事务，若不存在则返回null
	 */
	public PaymentTransaction findAvailable(PaymentTransaction.LineItem lineItem, PaymentPlugin paymentPlugin) {
		Assert.notNull(lineItem);
		Assert.notNull(paymentPlugin);
		Assert.notNull(lineItem.getAmount());
		Assert.notNull(lineItem.getType());
		Assert.notNull(lineItem.getTarget());

		BigDecimal amount = paymentPlugin.calculateAmount(lineItem.getAmount());
		BigDecimal fee = paymentPlugin.calculateFee(lineItem.getAmount());
		
		String sql = "SELECT * FROM `payment_transaction` t WHERE `type` = ? AND amount = ? AND fee = ? AND is_success = FALSE AND payment_plugin_id = ? AND parent_id IS NULL AND (expire IS NULL OR expire > NOW()) ";
		List<Object> params = new ArrayList<Object>();
		params.add(lineItem.getType().ordinal());
		params.add(amount);
		params.add(fee);
		params.add(paymentPlugin.getId());
		
		switch (lineItem.getType()) {
		case ORDER_PAYMENT:
			sql += " AND order_id = ?";
			params.add(lineItem.getTarget());
			break;
		case SVC_PAYMENT:
			sql += " AND svc_id = ?";
			params.add(lineItem.getTarget());
			break;
		case DEPOSIT_RECHARGE:
			sql += " AND business_id = ?";
			params.add(lineItem.getTarget());
			break;
		case BAIL_PAYMENT:
			sql += " AND store_id = ?";
			params.add(lineItem.getTarget());
			break;
		default:
			break;
		}
		return modelManager.findFirst(sql, params.toArray());
	}

	/**
	 * 查找可用父支付事务
	 * 
	 * @param lineItems
	 *            支付明细
	 * @param paymentPlugin
	 *            支付插件
	 * @return 可用父支付事务，若不存在则返回null
	 */
	public PaymentTransaction findAvailableParent(Collection<PaymentTransaction.LineItem> lineItems, PaymentPlugin paymentPlugin) {
		Assert.notEmpty(lineItems);
		Assert.state(lineItems.size() > 1);
		Assert.notNull(paymentPlugin);
		
		String sql = "SELECT * FROM `payment_transaction` t WHERE (t.expire IS NULL OR t.expire > NOW()) AND t.is_success = FALSE AND t.payment_plugin_id = ? ";
		List<Object> params = new ArrayList<Object>();
		params.add(paymentPlugin.getId());
		
		for (LineItem lineItem : lineItems) {
			Assert.notNull(lineItem);
			Assert.notNull(lineItem.getAmount());
			Assert.notNull(lineItem.getType());
			Assert.notNull(lineItem.getTarget());

			BigDecimal amount = paymentPlugin.calculateAmount(lineItem.getAmount());
			BigDecimal fee = paymentPlugin.calculateFee(lineItem.getAmount());
			
			sql += "AND EXISTS (SELECT * FROM `payment_transaction` t1 WHERE t1.`type` = ? AND t1.amount = ? AND t1.fee = ? AND t1.parent_id = t.id ";
			params.add(lineItem.getType().ordinal());
			params.add(amount);
			params.add(fee);
			
			switch (lineItem.getType()) {
			case ORDER_PAYMENT:
				sql += " AND order_id = ?";
				params.add(lineItem.getTarget());
				break;
			case SVC_PAYMENT:
				sql += " AND svc_id = ?";
				params.add(lineItem.getTarget());
				break;
			case DEPOSIT_RECHARGE:
				sql += " AND business_id = ?";
				params.add(lineItem.getTarget());
				break;
			case BAIL_PAYMENT:
				sql += " AND store_id = ?";
				params.add(lineItem.getTarget());
				break;
			default:
				break;
			}
			sql += ")";
		}
		return modelManager.findFirst(sql, params.toArray());
	}

	/**
	 * 查询订单支付成功记录
	 * @author yangzhicong
	 * @param paymentPluginId 支付插件ID, 不能为空
	 * @param order 订单, 不能为空
	 * @return
	 */
	public PaymentTransaction findSuccessOrderPayment(String paymentPluginId, Order order) {
		if (StrKit.isBlank(paymentPluginId) || order == null) {
			return null;
		}
		
		String sql = "SELECT * FROM `payment_transaction` t "
				+ "	WHERE t.order_id = ?"
				+ " AND t.payment_plugin_id = ?"
				+ " AND t.is_success = TRUE"
				+ " OR EXISTS ("
				+ "		SELECT 1 FROM `payment_transaction` t1"
				+ "		WHERE t1.parent_id = t.id"
				+ "		AND order_id = ?"
				+ "		AND t1.type = ? ) "
				+ "ORDER BY t.id ASC LIMIT 0,1";
		
		return modelManager.findFirst(sql, order.getId(), paymentPluginId, order.getId(), PaymentTransaction.Type.ORDER_PAYMENT.ordinal());
	}
	

}