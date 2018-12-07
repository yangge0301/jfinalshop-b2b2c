package com.jfinalshop.controller.admin;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import net.hasor.core.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Message;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Business;
import com.jfinalshop.model.BusinessAttribute;
import com.jfinalshop.model.Role;
import com.jfinalshop.service.BusinessAttributeService;
import com.jfinalshop.service.BusinessService;
import com.jfinalshop.service.RoleService;
import com.jfinalshop.shiro.hasher.Hasher;
import com.jfinalshop.shiro.hasher.HasherInfo;
import com.jfinalshop.shiro.hasher.HasherKit;

/**
 * Controller - 商家
 * 
 */
@ControllerBind(controllerKey = "/admin/business")
public class BusinessController extends BaseController {

	@Inject
	private BusinessService businessService;
	@Inject
	private BusinessAttributeService businessAttributeService;
	@Inject
	private RoleService roleService;

	/**
	 * 检查用户名是否存在
	 */
	@ActionKey("/admin/business/check_username")
	public void checkUsername() {
		String username = getPara("business.username");
		renderJson(StringUtils.isNotEmpty(username) && !businessService.usernameExists(username));
	}

	/**
	 * 检查E-mail是否存在
	 */
	@ActionKey("/admin/business/check_email")
	public void checkEmail() {
		Long id = getParaToLong("id");
		String email = getPara("business.email");
		renderJson(StringUtils.isNotEmpty(email) && businessService.emailUnique(id, email));
	}

	/**
	 * 检查手机是否存在
	 */
	@ActionKey("/admin/business/check_mobile")
	public void checkMobile() {
		Long id = getParaToLong("id");
		String mobile = getPara("business.mobile");
		renderJson(StringUtils.isNotEmpty(mobile) && businessService.mobileUnique(id, mobile));
	}

	/**
	 * 查看
	 */
	public void view() {
		Long id = getParaToLong("id");
		setAttr("businessAttributes", businessAttributeService.findList(true, true));
		setAttr("business", businessService.find(id));
		render("/admin/business/view.ftl");
	}

	/**
	 * 添加
	 */
	public void add() {
		setAttr("business", businessService.findAll());
		setAttr("roles", roleService.findAll());
		setAttr("businessAttributes", businessAttributeService.findList(true, true));
		render("/admin/business/add.ftl");
	}

	/**
	 * 保存
	 */
	@Before(Tx.class)
	public void save() {
		Business business = getModel(Business.class);
		Boolean isEnabled = getParaToBoolean("isEnabled", false);
		String password = getPara("password");
		String rePassword = getPara("rePassword");
		
		if (!StringUtils.equals(password, rePassword)) {
			setAttr("errorMessage", "两次密码不一致!");
			render(ERROR_VIEW);
			return;
		}
		
		if (businessService.usernameExists(business.getUsername())) {
			setAttr("errorMessage", "用户名已存在!");
			render(ERROR_VIEW);
			return;
		}
		if (businessService.emailExists(business.getEmail())) {
			setAttr("errorMessage", "E-mail已存在!");
			render(ERROR_VIEW);
			return;
		}
		if (!StringUtils.isNotEmpty(business.getMobile()) && businessService.mobileExists(business.getMobile())) {
			setAttr("errorMessage", "手机已存在!");
			render(ERROR_VIEW);
			return;
		}
		
		Long[] roleIds = getParaValuesToLong("roleIds");
		List<Role> roles = roleService.findList(roleIds);
		business.setRoles(roles);
		
		business.removeAttributeValue();
		for (BusinessAttribute businessAttribute : businessAttributeService.findList(true, true)) {
			String[] values = getParaValues("businessAttribute_" + businessAttribute.getId());
			Object memberAttributeValue = businessAttributeService.toBusinessAttributeValue(businessAttribute, values);
			business.setAttributeValue(businessAttribute, memberAttributeValue);
		}

		business.setUsername(StringUtils.lowerCase(business.getUsername()));
		business.setEmail(StringUtils.lowerCase(business.getEmail()));
		business.setMobile(StringUtils.lowerCase(business.getMobile()));
		business.setIsEnabled(isEnabled);
		HasherInfo hasherInfo = HasherKit.hash(password, Hasher.DEFAULT);
		business.setPassword(hasherInfo.getHashResult());
		business.setHasher(hasherInfo.getHasher().value());
		business.setSalt(hasherInfo.getSalt());
		business.setBalance(BigDecimal.ZERO);
		business.setFrozenFund(BigDecimal.ZERO);
		business.setIsLocked(false);
		business.setLockDate(null);
		business.setLastLoginIp(getRequest().getRemoteHost());
		business.setLastLoginDate(new Date());
		business.setStore(null);
		business.setCashes(null);
		business.setBusinessDepositLogs(null);

		businessService.save(business);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("businessAttributes", businessAttributeService.findList(true, true));
		setAttr("business", businessService.find(id));
		setAttr("roles", roleService.findAll());
		render("/admin/business/edit.ftl");
	}

	/**
	 * 更新
	 */
	@Before(Tx.class)
	public void update() {
		Business business = getModel(Business.class);
		Boolean unlock = getParaToBoolean("unlock");
		Boolean isEnabled = getParaToBoolean("isEnabled", false);
		String password = getPara("password");
		String rePassword = getPara("rePassword");
		
		if (!businessService.emailUnique(business.getId(), business.getEmail())) {
			setAttr("errorMessage", "E-mail不唯一!");
			render(ERROR_VIEW);
			return;
		}
		if (StringUtils.isNotEmpty(business.getMobile()) && !businessService.mobileUnique(business.getId(), business.getMobile())) {
			setAttr("errorMessage", "手机不唯一!");
			render(ERROR_VIEW);
			return;
		}
		business.removeAttributeValue();
		for (BusinessAttribute businessAttribute : businessAttributeService.findList(true, true)) {
			String[] values = getParaValues("businessAttribute_" + businessAttribute.getId());
			if (!businessAttributeService.isValid(businessAttribute, values)) {
				setAttr("errorMessage", "商家注册项值验证错误!");
				render(ERROR_VIEW);
				return;
			}
			Object businessAttributeValue = businessAttributeService.toBusinessAttributeValue(businessAttribute, values);
			business.setAttributeValue(businessAttribute, businessAttributeValue);
		}
		Business pBusiness = businessService.find(business.getId());
		if (pBusiness == null) {
			setAttr("errorMessage", "商家不存在!");
			render(ERROR_VIEW);
			return;
		}
		
		if (StringUtils.isNotEmpty(password) && StringUtils.isNotEmpty(rePassword)) {
			if (!StringUtils.equals(password, rePassword)) {
				setAttr("errorMessage", "两次密码不一致!");
				render(ERROR_VIEW);
				return;
			}
			HasherInfo hasherInfo = HasherKit.hash(password, Hasher.DEFAULT);
			business.setPassword(hasherInfo.getHashResult());
			business.setHasher(hasherInfo.getHasher().value());
			business.setSalt(hasherInfo.getSalt());
		} else {
			business.setPassword(pBusiness.getPassword());
			business.setHasher(pBusiness.getHasher());
			business.setSalt(pBusiness.getSalt());
		}
		
		Long[] roleIds = getParaValuesToLong("roleIds");
		List<Role> roles = roleService.findList(roleIds);
		business.setRoles(roles);
		
		business.setEmail(StringUtils.lowerCase(business.getEmail()));
		business.setMobile(StringUtils.lowerCase(business.getMobile()));
		business.setIsEnabled(isEnabled);
		if (BooleanUtils.isTrue(pBusiness.getIsLocked()) && BooleanUtils.isTrue(unlock)) {
			businessService.unlock(business);
			businessService.update(business, "username", "balance", "frozenFund", "businessDepositLogs", "cashes", "lastLoginIp", "lastLoginDate");
		} else {
			businessService.update(business, "username", "balance", "frozenFund", "businessDepositLogs", "cashes", "isLocked", "lockDate", "lastLoginIp", "lastLoginDate");
		}
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("business", businessService.findAll());
		setAttr("businessAttributes", businessAttributeService.findAll());
		setAttr("pageable", pageable);
		setAttr("page", businessService.findPage(pageable));
		render("/admin/business/list.ftl");
	}

	/**
	 * 删除
	 */
	@Before(Tx.class)
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids != null) {
			for (Long id : ids) {
				Business business = businessService.find(id);
				if (business != null && (business.getBalance().compareTo(BigDecimal.ZERO) > 0 || business.getStore() != null)) {
					renderJson(Message.error("admin.business.deleteExistDepositNotAllowed", business.getUsername()));
					return;
				}
				Db.deleteById("business_role", "business_id", id);
			}
			businessService.delete(ids);
		}
		renderJson(SUCCESS_MESSAGE);
	}

}