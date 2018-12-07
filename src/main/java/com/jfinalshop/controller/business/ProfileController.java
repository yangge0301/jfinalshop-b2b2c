package com.jfinalshop.controller.business;

import net.hasor.core.Inject;

import org.apache.commons.lang.StringUtils;

import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.model.Business;
import com.jfinalshop.model.BusinessAttribute;
import com.jfinalshop.service.BusinessAttributeService;
import com.jfinalshop.service.BusinessService;

/**
 * Controller - 个人资料
 * 
 */
@ControllerBind(controllerKey = "/business/profile")
public class ProfileController extends BaseController {

	@Inject
	private BusinessService businessService;
	@Inject
	private BusinessAttributeService businessAttributeService;

	/**
	 * 检查E-mail是否唯一
	 */
	@ActionKey("/business/profile/check_email")
	public void checkEmail() {
		String email = getPara("email");
		Business currentUser = businessService.getCurrentUser();
		renderJson(StringUtils.isNotEmpty(email) && businessService.emailUnique(currentUser.getId(), email));
	}

	/**
	 * 检查手机是否唯一
	 */
	@ActionKey("/business/profile/check_mobile")
	public void checkMobile() {
		String mobile = getPara("mobile");
		Business currentUser = businessService.getCurrentUser();
		renderJson(StringUtils.isNotEmpty(mobile) && businessService.mobileUnique(currentUser.getId(), mobile));
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Business currentUser = businessService.getCurrentUser();
		setAttr("currentUser", currentUser);
		render("/business/profile/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		String email = getPara("email");
		String mobile = getPara("mobile");
		Business currentUser = businessService.getCurrentUser();

		if (!businessService.emailUnique(currentUser.getId(), email)) {
			setAttr("errorMessage", "E-mail已存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (!businessService.mobileUnique(currentUser.getId(), mobile)) {
			setAttr("errorMessage", "手机已存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		currentUser.setEmail(email);
		currentUser.setMobile(mobile);
		currentUser.removeAttributeValue();
		for (BusinessAttribute businessAttribute : businessAttributeService.findList(true, true)) {
			String[] values = getParaValues("businessAttribute_" + businessAttribute.getId());
			if (!businessAttributeService.isValid(businessAttribute, values)) {
				setAttr("errorMessage", "商家注册项值验证错误!");
				render(UNPROCESSABLE_ENTITY_VIEW);
				return;
			}
			Object businessAttributeValue = businessAttributeService.toBusinessAttributeValue(businessAttribute, values);
			currentUser.setAttributeValue(businessAttribute, businessAttributeValue);
		}
		businessService.update(currentUser);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("edit");
	}

}