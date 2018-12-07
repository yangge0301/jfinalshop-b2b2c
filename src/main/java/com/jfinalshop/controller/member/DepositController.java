package com.jfinalshop.controller.member;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.Results;
import com.jfinalshop.interceptor.MobileInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.MemberDepositLog;
import com.jfinalshop.plugin.PaymentPlugin;
import com.jfinalshop.service.MemberDepositLogService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.PluginService;
import com.xiaoleilu.hutool.util.CollectionUtil;

/**
 * Controller - 预存款
 * 
 */
@ControllerBind(controllerKey = "/member/deposit")
public class DepositController extends BaseController {

	/**
	 * 每页记录数
	 */
	private static final int PAGE_SIZE = 10;

	@Inject
	private MemberDepositLogService memberDepositLogService;
	@Inject
	private PluginService pluginService;
	@Inject
	private MemberService memberService;

	/**
	 * 计算支付手续费
	 */
	@ActionKey("/member/deposit/calculate_fee")
	public void calculateFee() {
		String paymentPluginId = getPara("paymentPluginId");
		BigDecimal rechargeAmount = new BigDecimal(getPara("rechargeAmount", "0"));
		
		PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin(paymentPluginId);
		if (paymentPlugin == null) {
			Results.notFound(getResponse(), Results.DEFAULT_NOT_FOUND_MESSAGE);
			return;
		}
		if (!paymentPlugin.getIsEnabled() || rechargeAmount == null || rechargeAmount.compareTo(BigDecimal.ZERO) < 0) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		Map<String, Object> data = new HashMap<>();
		data.put("fee", paymentPlugin.calculateFee(rechargeAmount));
		renderJson(data);
	}

	/**
	 * 检查余额
	 */
	@ActionKey("/member/deposit/check_balance")
	public void checkBalance() {
		Member currentUser = memberService.getCurrentUser();
		Map<String, Object> data = new HashMap<>();
		data.put("balance", currentUser.getBalance());
		renderJson(data);
	}

	/**
	 * 充值
	 */
	@Before(MobileInterceptor.class)
	public void recharge() {
		List<PaymentPlugin> paymentPlugins = pluginService.getActivePaymentPlugins(getRequest());
		if (!paymentPlugins.isEmpty()) {
			setAttr("defaultPaymentPlugin", paymentPlugins.get(0));
			setAttr("paymentPlugins", paymentPlugins);
		}
		render("/member/deposit/recharge.ftl");
	}

	/**
	 * 记录
	 */
	@Before(MobileInterceptor.class)
	public void log() {
		Integer pageNumber = getParaToInt("pageNumber");
		Member currentUser = memberService.getCurrentUser();
		
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("pageable", pageable);
		setAttr("page", memberDepositLogService.findPage(currentUser, pageable));
		render("/member/deposit/log.ftl");
	}

	/**
	 * 记录
	 */
	@ActionKey("/member/deposit/m_log")
	public void mLog() {
		Integer pageNumber = getParaToInt("pageNumber", 1);
		Member currentUser = memberService.getCurrentUser();
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		Page<MemberDepositLog> pages = memberDepositLogService.findPage(currentUser, pageable);
		
		List<MemberDepositLog> memberDepositLogs = new ArrayList<MemberDepositLog>();
		if (CollectionUtil.isNotEmpty(pages.getList())) {
			for (MemberDepositLog memberDepositLog : pages.getList()) {
				memberDepositLog.put("type", memberDepositLog.getTypeName());
				memberDepositLogs.add(memberDepositLog);
			}
		}
		renderJson(memberDepositLogs);
	}

}