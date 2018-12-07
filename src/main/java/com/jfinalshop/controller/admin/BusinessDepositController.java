package com.jfinalshop.controller.admin;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Message;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Business;
import com.jfinalshop.model.BusinessDepositLog;
import com.jfinalshop.service.BusinessDepositLogService;
import com.jfinalshop.service.BusinessService;

/**
 * Controller - 商家预存款
 * 
 */
@ControllerBind(controllerKey = "/admin/business_deposit")
public class BusinessDepositController extends BaseController {

	@Inject
	private BusinessDepositLogService businessDepositLogService;
	@Inject
	private BusinessService businessService;

	/**
	 * 检查商家
	 */
	@ActionKey("/admin/business_deposit/check_business")
	public void checkBusiness() {
		String username = getPara("username");
		Map<String, Object> data = new HashMap<String, Object>();
		Business business = businessService.findByUsername(username);
		if (business == null) {
			data.put("message", Message.warn("admin.businessDeposit.businessNotExist"));
			renderJson(data);
			return;
		}
		data.put("message", SUCCESS_MESSAGE);
		data.put("balance", business.getBalance());
		renderJson(data);
	}

	/**
	 * 调整
	 */
	public void adjust() {
		render("/admin/business_deposit/adjust.ftl");
	}

	/**
	 * 调整
	 */
	@Before(Tx.class)
	@ActionKey("/admin/business_deposit/save_adjust")
	public void saveAdjust() {
		String username = getPara("username");
		BigDecimal amount = new BigDecimal(getPara("amount", "0"));
		String memo = getPara("memo");
		Business business = businessService.findByUsername(username);
		if (business == null) {
			setAttr("errorMessage", "商家不存在!");
			render(ERROR_VIEW);
			return;
		}
		if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
			setAttr("errorMessage", "金额不能等于0!");
			render(ERROR_VIEW);
			return;
		}
		if (business.getBalance() == null || business.getBalance().add(amount).compareTo(BigDecimal.ZERO) < 0) {
			setAttr("errorMessage", "余额加调整额不能等于0!");
			render(ERROR_VIEW);
			return;
		}
		businessService.addBalance(business, amount, BusinessDepositLog.Type.adjustment, memo);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("log");
	}

	/**
	 * 记录
	 */
	public void log() {
		Long businessId = getParaToLong("businessId");
		Pageable pageable = getBean(Pageable.class);
		Business business = businessService.find(businessId);
		if (business != null) {
			setAttr("business", business);
			setAttr("page", businessDepositLogService.findPage(business, pageable));
		} else {
			setAttr("page", businessDepositLogService.findPage(pageable));
		}
		render("/admin/business_deposit/log.ftl");
	}

}