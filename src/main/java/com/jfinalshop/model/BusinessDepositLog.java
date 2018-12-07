package com.jfinalshop.model;

import com.jfinalshop.model.base.BaseBusinessDepositLog;

/**
 * Model - 商家预存款记录
 * 
 */
public class BusinessDepositLog extends BaseBusinessDepositLog<BusinessDepositLog> {
	private static final long serialVersionUID = 1099074335081662452L;
	public static final BusinessDepositLog dao = new BusinessDepositLog().dao();
	
	/**
	 * 类型
	 */
	public enum Type {

		/**
		 * 预存款充值
		 */
		recharge,

		/**
		 * 预存款调整
		 */
		adjustment,

		/**
		 * 订单支付
		 */
		orderPayment,

		/**
		 * 服务支付
		 */
		svcPayment,

		/**
		 * 订单退款
		 */
		orderRefunds,

		/**
		 * 订单结算
		 */
		orderSettlement,

		/**
		 * 冻结资金
		 */
		frozen,

		/**
		 * 解冻资金
		 */
		unfrozen,

		/**
		 * 提现
		 */
		cash
	}

	/**
	 * 商家
	 */
	private Business business;
	
	/**
	 * 类型名称
	 */
	public Type getTypeName() {
		return Type.values()[getType()];
	}
	
	/**
	 * 获取商家
	 * 
	 * @return 商家
	 */
	public Business getBusiness() {
		if (business == null) {
			business = Business.dao.findById(getBusinessId());
		}
		return business;
	}

	/**
	 * 设置商家
	 * 
	 * @param business
	 *            商家
	 */
	public void setBusiness(Business business) {
		this.business = business;
	}
}
