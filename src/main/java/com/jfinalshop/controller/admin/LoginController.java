package com.jfinalshop.controller.admin;

import java.util.Date;

import net.hasor.core.Inject;

import com.jfinal.core.Controller;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinalshop.entity.ResultCode;
import com.jfinalshop.model.Admin;
import com.jfinalshop.service.AdminService;
import com.jfinalshop.shiro.core.SubjectKit;
import com.jfinalshop.shiro.core.SubjectKit.UserType;
import com.jfinalshop.util.IpUtil;

/**
 * Controller - 管理员登录
 * 
 */
@ControllerBind(controllerKey = "/admin/login")
public class LoginController extends Controller {

	/**
	 * 消息名称
	 */
	public static final String RESULT = "result";
	
	@Inject
	private AdminService adminService;
	
	/**
	 * 登录页面
	 */
	public void index() {
		String username = getPara("username");
		String captcha = getPara("captcha");
		String password = getPara("password");
		
		if (StrKit.notBlank(username) || StrKit.notBlank(password)) {
			if (!SubjectKit.doCaptcha("captcha", captcha)) {
				setAttr(RESULT, new ResultCode(4, "验证码输入错误!"));
				return;
			}
			Admin admin = adminService.findByUsername(username);
			if (admin == null) {
				setAttr(RESULT, new ResultCode(1, "用户不存在!"));
				return;
			}
			if (!admin.getIsEnabled()) {
				setAttr(RESULT, new ResultCode(1, "用户禁用中!"));
				return;
			}
			if (admin.getIsLocked()) {
				setAttr(RESULT, new ResultCode(1, "用户锁定中!"));
				return;
			}
			if (SubjectKit.login(username, password, false, UserType.ADMIN)) {
				admin.setLastLoginIp(IpUtil.getIpAddr(getRequest()));
				admin.setLastLoginDate(new Date());
				admin.update();
			} else {
				setAttr(RESULT, new ResultCode(2, "用户名或密码错误!"));
				return;
			}
		}
		if (adminService.isAuthenticated() && adminService.getCurrent() != null) {
			redirect("/admin/index");
		} else {
			setAttr(RESULT, new ResultCode(1, "您没有得到相应的授权!"));
			render("/admin/login/index.ftl");
		}
	}

}