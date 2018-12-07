package com.jfinalshop.controller.business;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.hasor.core.Inject;

import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.Results;
import com.jfinalshop.model.Business;
import com.jfinalshop.plugin.PaymentPlugin;
import com.jfinalshop.service.BusinessDepositLogService;
import com.jfinalshop.service.BusinessService;
import com.jfinalshop.service.PluginService;

/**
 * Controller - 预存款
 * 
 */
@ControllerBind(controllerKey = "/business/deposit")
public class DepositController extends BaseController {

	@Inject
	private BusinessDepositLogService businessDepositLogService;
	@Inject
	private PluginService pluginService;
	@Inject
	private BusinessService businessService;

	/**
	 * 计算
	 */
	public void calculate() {
		String paymentPluginId = getPara("paymentPluginId");
		BigDecimal rechargeAmount = new BigDecimal(getPara("rechargeAmount", "0"));
		
		Map<String, Object> data = new HashMap<>();
		PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin(paymentPluginId);
		if (paymentPlugin == null || !paymentPlugin.getIsEnabled()) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		if (rechargeAmount == null || rechargeAmount.compareTo(BigDecimal.ZERO) <= 0) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		data.put("fee", paymentPlugin.calculateFee(rechargeAmount));
		renderJson(data);
	}

	/**
	 * 检查余额
	 */
	@ActionKey("/business/deposit/check_balance")
	public void checkBalance() {
		Business currenUser = businessService.getCurrentUser();
		
		Map<String, Object> data = new HashMap<>();
		data.put("balance", currenUser.getBalance());
		renderJson(data);
	}

	/**
	 * 充值
	 */
	public void recharge() {
		List<PaymentPlugin> paymentPlugins = pluginService.getActivePaymentPlugins(getRequest());
		Business currentUser = businessService.getCurrentUser();
		
		if (!paymentPlugins.isEmpty()) {
			setAttr("defaultPaymentPlugin", paymentPlugins.get(0));
			setAttr("paymentPlugins", paymentPlugins);
		}
		setAttr("currentUser", currentUser);
		render("/business/deposit/recharge.ftl");
	}

	/**
	 * 记录
	 */
	public void log() {
		Pageable pageable = getBean(Pageable.class);
		Business currenUser = businessService.getCurrentUser();
		
		setAttr("pageable", pageable);
		setAttr("page", businessDepositLogService.findPage(currenUser, pageable));
		render("/business/deposit/log.ftl");
	}

}