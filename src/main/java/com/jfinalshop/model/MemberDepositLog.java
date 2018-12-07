package com.jfinalshop.model;

import com.jfinalshop.model.base.BaseMemberDepositLog;

/**
 * Model - 会员预存款记录
 * 
 */
public class MemberDepositLog extends BaseMemberDepositLog<MemberDepositLog> {
	private static final long serialVersionUID = -2908370880989528884L;
	public static final MemberDepositLog dao = new MemberDepositLog().dao();
	
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
		 * 订单退款
		 */
		orderRefunds
	}

	/**
	 * 会员
	 */
	private Member member;
	
	/**
	 * 类型名称
	 */
	public Type getTypeName() {
		return getType() != null ? Type.values()[getType()] : null;
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
}
