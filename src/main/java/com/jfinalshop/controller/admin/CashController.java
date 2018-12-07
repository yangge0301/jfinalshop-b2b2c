package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Cash;
import com.jfinalshop.service.CashService;

/**
 * Controller - 提现
 * 
 */
@ControllerBind(controllerKey = "/admin/cash")
public class CashController extends BaseController {

	@Inject
	private CashService cashService;

	/**
	 * 审核
	 */
	@Before(Tx.class)
	public void review() {
		Long id = getParaToLong("id");
		Boolean isPassed = getParaToBoolean("isPassed");
		Cash cash = cashService.find(id);
		if (isPassed == null || cash == null || !Cash.Status.pending.equals(cash.getStatusName())) {
			setAttr("errorMessage", "状态不等于等待审核!");
			render(ERROR_VIEW);
			return;
		}
		cashService.review(cash, isPassed);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", cashService.findPage(pageable));
		render("/admin/cash/list.ftl");
	}

}