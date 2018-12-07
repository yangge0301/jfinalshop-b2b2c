package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.CategoryApplication;
import com.jfinalshop.service.CategoryApplicationService;

/**
 * Controller - 经营分类申请
 * 
 */
@ControllerBind(controllerKey = "/admin/category_application")
public class CategoryApplicationController extends BaseController {

	@Inject
	private CategoryApplicationService categoryApplicationService;

	/**
	 * 审核
	 */
	@Before(Tx.class)
	public void review() {
		Long id = getParaToLong("id");
		Boolean isPassed = getParaToBoolean("isPassed");
		CategoryApplication categoryApplication = categoryApplicationService.find(id);
		if (categoryApplication == null || isPassed == null || !CategoryApplication.Status.pending.equals(categoryApplication.getStatusName())) {
			setAttr("errorMessage", "状态不等于等待审核!");
			render(ERROR_VIEW);
			return;
		}
		if (categoryApplicationService.exist(categoryApplication.getStore(), categoryApplication.getProductCategory(), CategoryApplication.Status.approved)) {
			setAttr("errorMessage", "经营分类申请已存在!");
			render(ERROR_VIEW);
			return;
		}

		categoryApplicationService.review(categoryApplication, isPassed);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", categoryApplicationService.findPage(pageable));
		render("/admin/category_application/list.ftl");
	}

}