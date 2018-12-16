package com.jfinalshop.controller.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.hasor.core.Inject;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.upload.UploadFile;
import com.jfinalshop.FileType;
import com.jfinalshop.Results;
import com.jfinalshop.Setting;
import com.jfinalshop.model.Business;
import com.jfinalshop.model.BusinessAttribute;
import com.jfinalshop.model.Role;
import com.jfinalshop.service.BusinessAttributeService;
import com.jfinalshop.service.BusinessService;
import com.jfinalshop.service.FileService;
import com.jfinalshop.service.RoleService;
import com.jfinalshop.shiro.core.SubjectKit;
import com.jfinalshop.shiro.hasher.Hasher;
import com.jfinalshop.shiro.hasher.HasherInfo;
import com.jfinalshop.shiro.hasher.HasherKit;
import com.jfinalshop.util.IpUtil;
import com.jfinalshop.util.SystemUtils;

/**
 * Controller - 商家注册
 * 
 */
@ControllerBind(controllerKey = "/business/register")
public class RegisterController extends Controller {

	@Inject
	private BusinessService businessService;
	@Inject
	private BusinessAttributeService businessAttributeService;
	@Inject
	private FileService fileService;
	@Inject
	private RoleService roleService;

	/**
	 * 检查用户名是否存在
	 */
	@ActionKey("/business/register/check_username")
	public void checkUsername() {
		String username = getPara("business.username");
		renderJson(StringUtils.isNotEmpty(username) && !businessService.usernameExists(username));
	}

	/**
	 * 检查E-mail是否存在
	 */
	@ActionKey("/business/register/check_email")
	public void checkEmail() {
		String email = getPara("business.email");
		renderJson(StringUtils.isNotEmpty(email) && !businessService.emailExists(email));
	}

	/**
	 * 检查手机是否存在
	 */
	@ActionKey("/business/register/check_mobile")
	public void checkMobile() {
		String mobile = getPara("business.mobile");
		renderJson(StringUtils.isNotEmpty(mobile) && !businessService.mobileExists(mobile));
	}

	/**
	 * 上传
	 */
	public void uploader() {
		UploadFile file = getFile();
		FileType fileType = FileType.valueOf(getPara("fileType", "image"));
		Map<String, Object> data = new HashMap<>();
		if (fileType == null || file == null || file.getFile().length() <= 0) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		if (!fileService.isValid(fileType, file)) {
			Results.unprocessableEntity(getResponse(), "business.upload.invalid");
			return;
		}
		String url = fileService.upload(fileType, file, false);
		if (StringUtils.isEmpty(url)) {
			Results.unprocessableEntity(getResponse(), "business.upload.error");
			return;
		}
		data.put("url", url);
		renderJson(data);
	}

	/**
	 * 注册页面
	 */
	public void index() {
		render("/business/register/index.ftl");
	}

	/**
	 * 注册提交
	 */
	public void submit() {
		Business business = getModel(Business.class);
		String captcha = getPara("captcha");
		String rePassword = getPara("rePassword");
		
		if (!StringUtils.equals(business.getPassword(), rePassword)) {
			setAttr("errorMessage", "两次密码不一致!");
			Results.unprocessableEntity(getResponse(), "两次密码不一致!");
			return;
		}
		if (!SubjectKit.doCaptcha("captcha", captcha)) {
			Results.unprocessableEntity(getResponse(), "common.message.ncorrectCaptcha");
			return;
		}
		Setting setting = SystemUtils.getSetting();
		if (!ArrayUtils.contains(setting.getAllowedRegisterTypes(), Setting.RegisterType.business)) {
			Results.unprocessableEntity(getResponse(), "business.register.disabled");
			return;
		}
		if (businessService.usernameExists(business.getUsername())) {
			Results.unprocessableEntity(getResponse(), "business.register.usernameExist");
			return;
		}
		if (businessService.emailExists(business.getEmail())) {
			Results.unprocessableEntity(getResponse(), "business.register.emailExist");
			return;
		}
		if (!StringUtils.isNotEmpty(business.getMobile()) && businessService.mobileExists(business.getMobile())) {
			Results.unprocessableEntity(getResponse(), "business.register.mobileExist");
			return;
		}

		business.removeAttributeValue();
		for (BusinessAttribute businessAttribute : businessAttributeService.findList(true, true)) {
			String[] values = getParaValues("businessAttribute_" + businessAttribute.getId());
			if (!businessAttributeService.isValid(businessAttribute, values)) {
				renderJson(Results.UNPROCESSABLE_ENTITY);
				return;
			}
			Object memberAttributeValue = businessAttributeService.toBusinessAttributeValue(businessAttribute, values);
			business.setAttributeValue(businessAttribute, memberAttributeValue);
		}
		business.setUsername(business.getUsername());
		business.setEmail(StringUtils.lowerCase(business.getEmail()));
		business.setMobile(StringUtils.lowerCase(business.getMobile()));
		HasherInfo hasherInfo = HasherKit.hash(business.getPassword(), Hasher.DEFAULT);
		business.setPassword(hasherInfo.getHashResult());
		business.setHasher(hasherInfo.getHasher().value());
		business.setSalt(hasherInfo.getSalt());
		business.setBalance(BigDecimal.ZERO);
		business.setFrozenFund(BigDecimal.ZERO);
		business.setIsEnabled(true);
		business.setIsLocked(false);
		business.setLastLoginIp(IpUtil.getIpAddr(getRequest()));
		business.setLastLoginDate(new Date());
		List<Role> roles = new ArrayList<Role>();
		roles.add(roleService.find(2L));
		business.setRoles(roles);
		businessService.save(business);
		
		Results.ok(getResponse(), "business.register.success");
	}

}