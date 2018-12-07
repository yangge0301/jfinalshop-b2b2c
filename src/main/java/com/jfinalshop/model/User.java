package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BaseUser;

/**
 * Model - 用户
 * 
 */
public class User extends BaseUser<User> {
	private static final long serialVersionUID = -6504626825279818040L;
	public static final User dao = new User().dao();
	
	/**
	 * 密码找回类型
	 */
	public enum PasswordType {

		/**
		 * 会员
		 */
		member,

		/**
		 * 商家
		 */
		business
	}

	/**
	 * "登录失败尝试次数"缓存名称
	 */
	public static final String FAILED_LOGIN_ATTEMPTS_CACHE_NAME = "failedLoginAttempts";

	/**
	 * 社会化用户
	 */
	private List<SocialUser> socialUsers = new ArrayList<>();

	/**
	 * 支付事务
	 */
	private List<PaymentTransaction> paymentTransactions = new ArrayList<>();

	/**
	 * 审计日志
	 */
	private List<AuditLog> auditLogs = new ArrayList<>();


	/**
	 * 获取社会化用户
	 * 
	 * @return 社会化用户
	 */
	public List<SocialUser> getSocialUsers() {
		if (CollectionUtils.isEmpty(socialUsers)) {
			String sql = "SELECT * FROM `social_user` WHERE user_id = ?";
			socialUsers = SocialUser.dao.find(sql, getId());
		}
		return socialUsers;
	}

	/**
	 * 设置社会化用户
	 * 
	 * @param socialUsers
	 *            社会化用户
	 */
	public void setSocialUsers(List<SocialUser> socialUsers) {
		this.socialUsers = socialUsers;
	}

	/**
	 * 获取支付事务
	 * 
	 * @return 支付事务
	 */
	public List<PaymentTransaction> getPaymentTransactions() {
		if (CollectionUtils.isEmpty(paymentTransactions)) {
			String sql = "SELECT * FROM `payment_transaction` WHERE user_id = ?";
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
	 * 获取审计日志
	 * 
	 * @return 审计日志
	 */
	public List<AuditLog> getAuditLogs() {
		if (CollectionUtils.isEmpty(auditLogs)) {
			String sql = "SELECT * FROM `audit_log` WHERE user_id = ?";
			auditLogs = AuditLog.dao.find(sql, getId());
		}
		return auditLogs;
	}

	/**
	 * 设置审计日志
	 * 
	 * @param auditLogs
	 *            审计日志
	 */
	public void setAuditLogs(List<AuditLog> auditLogs) {
		this.auditLogs = auditLogs;
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
