package com.jfinalshop.controller.shop;

import net.hasor.core.Inject;
import net.hasor.core.InjectSettings;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.model.SocialUser;
import com.jfinalshop.plugin.LoginPlugin;
import com.jfinalshop.service.PluginService;
import com.jfinalshop.service.SocialUserService;

/**
 * Controller - 社会化用户登录
 * 
 */
@ControllerBind(controllerKey = "/social_user_login")
public class SocialUserLoginController extends BaseController {

	@InjectSettings("${member_index}")
	private String memberIndex;
	@InjectSettings("${member_login}")
	private String memberLogin;
	@InjectSettings("${member_login_view}")
	private String memberLoginView;

	@Inject
	private PluginService pluginService;
	@Inject
	private SocialUserService socialUserService;

	/**
	 * 首页
	 */
	public void index() {
		String loginPluginId = getPara("loginPluginId");
		LoginPlugin loginPlugin = pluginService.getLoginPlugin(loginPluginId);
		if (loginPlugin == null || BooleanUtils.isNotTrue(loginPlugin.getIsEnabled())) {
			setAttr("errorMessage", "插件为空或禁用!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		redirect(loginPlugin.getPreSignInUrl(loginPlugin));
	}

	/**
	 * 登录前处理
	 * @throws Exception 
	 */
	@ActionKey("/social_user_login/pre_sign_in")
	public void preSignIn() throws Exception {
		String loginPluginId = getPara(0);
		String extra = getPara(1);
		LoginPlugin loginPlugin = pluginService.getLoginPlugin(loginPluginId);
		if (loginPlugin == null || BooleanUtils.isNotTrue(loginPlugin.getIsEnabled())) {
			setAttr("errorMessage", "登录插件禁用!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		loginPlugin.preSignInHandle(loginPlugin, extra, getRequest(), getResponse(), this);
	}

	/**
	 * 登录处理
	 */
	@ActionKey("/social_user_login/sign_in")
	public void signIn() throws Exception {
		String loginPluginId = getPara(0);
		String extra = getPara(1);
		LoginPlugin loginPlugin = pluginService.getLoginPlugin(loginPluginId);
		if (loginPlugin == null || BooleanUtils.isNotTrue(loginPlugin.getIsEnabled())) {
			setAttr("errorMessage", "登录插件禁用!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		loginPlugin.preSignInHandle(loginPlugin, extra, getRequest(), getResponse(), this);
	}

	/**
	 * 登录后处理
	 */
	@ActionKey("/social_user_login/post_sign_in")
	public void postSignIn() throws Exception {
		String loginPluginId = getPara(0);
		String extra = getPara(1);
		
		LoginPlugin loginPlugin = pluginService.getLoginPlugin(loginPluginId);
		if (loginPlugin == null || BooleanUtils.isNotTrue(loginPlugin.getIsEnabled())) {
			setAttr("errorMessage", "登录插件禁用!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		boolean isSigninSuccess = loginPlugin.isSignInSuccess(loginPlugin, extra, getRequest(), getResponse());
		if (!isSigninSuccess) {
			redirect(memberIndex);
			return;
		}

		String uniqueId = loginPlugin.getUniqueId(getRequest());
		if (StringUtils.isEmpty(uniqueId)) {
			setAttr("errorMessage", message("member.login.pluginError"));
			setAttr("errorMessage", "获取唯一ID为空!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		SocialUser socialUser = socialUserService.find(loginPluginId, uniqueId);
		if (socialUser != null) {
			if (socialUser.getUser() != null) {
				//userService.login(new SocialUserAuthenticationToken(socialUser, false, request.getRemoteAddr()));
			} else {
				setAttr("socialUserId", socialUser.getId());
				setAttr("uniqueId", uniqueId);
			}
		} else {
			socialUser = new SocialUser();
			socialUser.setLoginPluginId(loginPluginId);
			socialUser.setUniqueId(uniqueId);
			socialUser.setUser(null);
			socialUserService.save(socialUser);
			setAttr("socialUserId", socialUser.getId());
			setAttr("uniqueId", uniqueId);
		}
		loginPlugin.postSignInHandle(loginPlugin, socialUser, extra, isSigninSuccess, getRequest(), getResponse(), this);
	}

}