package com.jfinalshop.controller.business;

import java.util.Date;

import net.hasor.core.Inject;

import org.apache.commons.lang.StringUtils;

import com.jfinal.core.Controller;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinalshop.entity.ResultCode;
import com.jfinalshop.model.Business;
import com.jfinalshop.service.BusinessService;
import com.jfinalshop.shiro.core.SubjectKit;
import com.jfinalshop.shiro.core.SubjectKit.UserType;
import com.jfinalshop.util.IpUtil;
import com.jfinalshop.util.WebUtils;

/**
 * Controller - 商家登录
 * 
 */
@ControllerBind(controllerKey = "/business/login")
public class LoginController extends Controller {

	/**
	 * "重定向令牌"Cookie名称
	 */
	private static final String REDIRECT_TOKEN_COOKIE_NAME = "redirectToken";

	/**
	 * 消息名称
	 */
	public static final String RESULT = "result";
	
	@Inject
	BusinessService businessService;
	
	/**
	 * 登录页面
	 */
	public void index() {
		String username = getPara("username");
		String captcha = getPara("captcha");
		String password = getPara("password");
		
		String redirectUrl = getPara("redirectUrl");
		String redirectToken = getPara("redirectToken");
		
		if (StrKit.notBlank(username) || StrKit.notBlank(password)) {
			if (!SubjectKit.doCaptcha("captcha", captcha)) {
				setAttr(RESULT, new ResultCode(4, "验证码输入错误!"));
				return;
			}
			
			Business business = businessService.findByUsername(username);
			if (business == null) {
				setAttr(RESULT, new ResultCode(1, "用户不存在!"));
				return;
			}
			if (!business.getIsEnabled()) {
				setAttr(RESULT, new ResultCode(1, "用户禁用中!"));
				return;
			}
			if (business.getIsLocked()) {
				setAttr(RESULT, new ResultCode(1, "用户锁定中!"));
				return;
			}
			if (SubjectKit.login(username, password, UserType.BUSINESS)) {
				business.setLastLoginIp(IpUtil.getIpAddr(getRequest()));
				business.setLastLoginDate(new Date());
				business.update();
			} else {
				setAttr(RESULT, new ResultCode(2, "用户名或密码错误!"));
				return;
			}
		}
		
		if (StringUtils.isNotEmpty(redirectUrl) && StringUtils.isNotEmpty(redirectToken) && StringUtils.equals(redirectToken, WebUtils.getCookie(getRequest(), REDIRECT_TOKEN_COOKIE_NAME))) {
			setAttr("redirectUrl", redirectUrl);
			WebUtils.removeCookie(getRequest(), getResponse(), REDIRECT_TOKEN_COOKIE_NAME);
		}
		
		if (businessService.isAuthenticated() && businessService.getCurrentUser() != null) {
			redirect("/business/index");
		} else {
			setAttr(RESULT, new ResultCode(1, "您没有得到相应的授权!"));
			render("/business/login/index.ftl");
		}
	}
	
	
}