package com.jfinalshop.controller.business;

import java.math.BigDecimal;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Business;
import com.jfinalshop.model.Cash;
import com.jfinalshop.service.BusinessService;
import com.jfinalshop.service.CashService;

/**
 * Controller - 提现
 * 
 */
@ControllerBind(controllerKey = "/business/cash")
public class CashController extends BaseController {

	@Inject
	private CashService cashService;
	@Inject
	private BusinessService businessService;

	/**
	 * 检查余额
	 */
	@ActionKey("/business/cash/check_balance")
	public void checkBalance() {
		BigDecimal amount = new BigDecimal(getPara("amount", "0"));
		Business currentUser = businessService.getCurrentUser();
		
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			renderJson(false);
			return;
		}
		renderJson(currentUser.getBalance().compareTo(amount) >= 0);
	}

	/**
	 * 申请提现
	 */
	public void application() {
		render("/business/cash/application.ftl");
	}

	/**
	 * 申请提现
	 */
	@Before(Tx.class)
	public void save() {
		Cash cash = getModel(Cash.class);
		Business currentUser = businessService.getCurrentUser();
		if (currentUser.getBalance().compareTo(cash.getAmount()) < 0) {
			setAttr("errorMessage", "当前用户余额小于0!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		cashService.applyCash(cash, currentUser);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		Business currentUser = businessService.getCurrentUser();
		
		setAttr("pageable", pageable);
		setAttr("page", cashService.findPage(currentUser, pageable));
		render("/business/cash/list.ftl");
	}

}