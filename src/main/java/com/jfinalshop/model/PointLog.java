package com.jfinalshop.model;

import com.jfinalshop.model.base.BasePointLog;

/**
 * Model - 积分记录
 * 
 */
public class PointLog extends BasePointLog<PointLog> {
	private static final long serialVersionUID = -3444473497099804707L;
	public static final PointLog dao = new PointLog().dao();
	
	/**
	 * 类型
	 */
	public enum Type {

		/**
		 * 积分赠送
		 */
		reward,

		/**
		 * 积分兑换
		 */
		exchange,

		/**
		 * 积分兑换撤销
		 */
		undoExchange,

		/**
		 * 积分调整
		 */
		adjustment,
		/**
		 * 积分使用
		 * **/
		pointuse,
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
