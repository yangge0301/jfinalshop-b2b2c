package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BaseSvc;

/**
 * Model - 服务
 * 
 */
public class Svc extends BaseSvc<Svc> {
	private static final long serialVersionUID = 3061867599910355406L;
	public static final Svc dao = new Svc().dao();
	
	/**
	 * 店铺
	 */
	private Store store;

	/**
	 * 支付事务
	 */
	private List<PaymentTransaction> paymentTransactions = new ArrayList<>();
	
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
	 * 获取支付事务
	 * 
	 * @return 支付事务
	 */
	public List<PaymentTransaction> getPaymentTransactions() {
		if (CollectionUtils.isEmpty(paymentTransactions)) {
			String sql = "SELECT * FROM `payment_transaction` WHERE svc_id = ?";
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
}
