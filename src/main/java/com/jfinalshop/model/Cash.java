package com.jfinalshop.model;

import com.jfinalshop.model.base.BaseCash;

/**
 * Model - 提现
 * 
 */
public class Cash extends BaseCash<Cash> {
	private static final long serialVersionUID = 8120486038228032145L;
	public static final Cash dao = new Cash().dao();
	
	/**
	 * 商家
	 */
	private Business business;
	
	/**
	 * 状态
	 */
	public enum Status {

		/**
		 * 等待审核
		 */
		pending,

		/**
		 * 审核通过
		 */
		approved,

		/**
		 * 审核失败
		 */
		failed
	}
	
	/**
	 * 获取类型
	 * 
	 * @return 类型
	 */
	public Cash.Status getStatusName() {
		return getStatus() == null ? null : Status.values()[getStatus()];
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
