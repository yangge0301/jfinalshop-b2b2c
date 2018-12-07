package com.jfinalshop.controller.member;

import java.util.Date;

import net.hasor.core.Inject;
import net.hasor.core.InjectSettings;

import org.apache.commons.lang.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinalshop.interceptor.MobileInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.SocialUser;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.PluginService;
import com.jfinalshop.service.SocialUserService;
import com.jfinalshop.shiro.core.SubjectKit;
import com.jfinalshop.shiro.core.SubjectKit.UserType;
import com.jfinalshop.util.IpUtil;
import com.jfinalshop.util.WebUtils;

/**
 * Controller - 会员登录
 * 
 */
@ControllerBind(controllerKey = "/member/login")
public class LoginController extends BaseController {

	/**
	 * "重定向令牌"Cookie名称
	 */
	private static final String REDIRECT_TOKEN_COOKIE_NAME = "redirectToken";
	
	/**
	 * 消息名称
	 */
	public static final String MESSAGE = "message";

	@InjectSettings("${member_index}")
	private String memberIndex;
	@InjectSettings("${member_login_view}")
	private String memberLoginView;

	@Inject
	private PluginService pluginService;
	@Inject
	private SocialUserService socialUserService;
	@Inject
	private MemberService memberService;

	/**
	 * 登录页面
	 */
	@Before(MobileInterceptor.class)
	public void index() {
		String username = getPara("username");
		String captcha = getPara("captcha");
		String password = getPara("password");
		
		String redirectUrl = getPara("redirectUrl");
		String redirectToken = getPara("redirectToken");
		
		Long socialUserId = getParaToLong("socialUserId");
		String uniqueId = getPara("uniqueId");
		
		if (StrKit.notBlank(username) || StrKit.notBlank(password)) {
			if (!SubjectKit.doCaptcha("captcha", captcha)) {
				renderJson(Kv.by(MESSAGE, "验证码输入错误!"));
				return;
			}
			Member member = memberService.findByUsername(username);
			if (member == null) {
				renderJson(Kv.by(MESSAGE, "用户不存在!"));
				return;
			}
			if (!member.getIsEnabled()) {
				renderJson(Kv.by(MESSAGE, "用户禁用中!"));
				return;
			}
			if (member.getIsLocked()) {
				renderJson(Kv.by(MESSAGE, "用户锁定中!"));
				return;
			}
			if (SubjectKit.login(username, password, UserType.MEMBER)) {
				member.setLastLoginIp(IpUtil.getIpAddr(getRequest()));
				member.setLastLoginDate(new Date());
				member.update();
			} else {
				renderJson(Kv.by(MESSAGE, "用户名或密码错误!"));
				return;
			}
		}
		
		if (StringUtils.isNotEmpty(redirectUrl) && StringUtils.isNotEmpty(redirectToken) && StringUtils.equals(redirectToken, WebUtils.getCookie(getRequest(), REDIRECT_TOKEN_COOKIE_NAME))) {
			setAttr("redirectUrl", redirectUrl);
			setSessionAttr("redirectUrl", redirectUrl);
			WebUtils.removeCookie(getRequest(), getResponse(), REDIRECT_TOKEN_COOKIE_NAME);
		}
		if (socialUserId != null && StringUtils.isNotEmpty(uniqueId)) {
			SocialUser socialUser = socialUserService.find(socialUserId);
			if (socialUser == null || socialUser.getUser() != null || !StringUtils.equals(socialUser.getUniqueId(), uniqueId)) {
				setAttr("errorMessage", "用户不存在!");
				render(UNPROCESSABLE_ENTITY_VIEW);
				return;
			}
			setAttr("socialUserId", socialUserId);
			setAttr("uniqueId", uniqueId);
		}
		setAttr("loginPlugins", pluginService.getActiveLoginPlugins(getRequest()));
		
		if (memberService.isAuthenticated() && memberService.getCurrentUser() != null) {
			renderJson(Kv.by("redirectUrl", memberIndex));
		} else {
			render(memberLoginView);
		}
	}

}