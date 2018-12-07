package com.jfinalshop.plugin.weiboLogin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.jfinal.core.Controller;
import com.jfinalshop.model.PluginConfig;
import com.jfinalshop.plugin.LoginPlugin;
import com.jfinalshop.util.JsonUtils;
import com.jfinalshop.util.WebUtils;

/**
 * Plugin - 新浪微博登录
 * 
 */
public class WeiboLoginPlugin extends LoginPlugin {

	/**
	 * code请求URL
	 */
	private static final String CODE_REQUEST_URL = "https://api.weibo.com/oauth2/authorize";

	/**
	 * uid请求URL
	 */
	private static final String UID_REQUEST_URL = "https://api.weibo.com/oauth2/access_token";

	/**
	 * "状态"属性名称
	 */
	private static final String STATE_ATTRIBUTE_NAME = WeiboLoginPlugin.class.getName() + ".STATE";

	@Override
	public String getName() {
		return "新浪微博登录";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public String getAuthor() {
		return "JFinalShop";
	}

	@Override
	public String getSiteUrl() {
		return "http://www.jfinalshop.com";
	}

	@Override
	public String getInstallUrl() {
		return "weibo_login/install";
	}

	@Override
	public String getUninstallUrl() {
		return "weibo_login/uninstall";
	}

	@Override
	public String getSettingUrl() {
		return "weibo_login/setting";
	}

	@Override
	public void signInHandle(LoginPlugin loginPlugin, String extra, HttpServletRequest request, HttpServletResponse response, Controller controller) throws Exception {
		String state = DigestUtils.md5Hex(UUID.randomUUID() + RandomStringUtils.randomAlphabetic(30));
		request.getSession().setAttribute(STATE_ATTRIBUTE_NAME, state);
		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put("response_type", "code");
		parameterMap.put("client_id", getClientId());
		parameterMap.put("redirect_uri", getPostSignInUrl(loginPlugin));
		parameterMap.put("state", state);

		controller.setAttr("requestUrl", CODE_REQUEST_URL);
		controller.setAttr("parameterMap", parameterMap);
		controller.render(LoginPlugin.DEFAULT_PAY_VIEW_NAME);
	}

	@Override
	public boolean isSignInSuccess(LoginPlugin loginPlugin, String extra, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String state = (String) request.getSession().getAttribute(STATE_ATTRIBUTE_NAME);
		if (StringUtils.isNotEmpty(state) && StringUtils.equals(state, request.getParameter("state")) && StringUtils.isNotEmpty(request.getParameter("code"))) {
			request.getSession().removeAttribute(STATE_ATTRIBUTE_NAME);
			Map<String, Object> parameterMap = new HashMap<>();
			parameterMap.put("grant_type", "authorization_code");
			parameterMap.put("client_id", getClientId());
			parameterMap.put("client_secret", getClientSecret());
			parameterMap.put("redirect_uri", getPostSignInUrl(loginPlugin));
			parameterMap.put("code", request.getParameter("code"));
			String content = WebUtils.post(UID_REQUEST_URL, parameterMap);
			JsonNode tree = JsonUtils.toTree(content);
			String accessToken = tree.get("access_token").textValue();
			String uid = tree.get("uid").textValue();
			if (StringUtils.isNotEmpty(accessToken) && StringUtils.isNotEmpty(uid)) {
				request.setAttribute("accessToken", tree.get("access_token").textValue());
				request.setAttribute("uid", uid);
				return true;
			}
		}
		return false;
	}

	@Override
	public String getUniqueId(HttpServletRequest request) {
		String uid = (String) request.getAttribute("uid");
		if (StringUtils.isNotEmpty(uid)) {
			return uid;
		}
		return null;
	}

	/**
	 * 获取 ClientId
	 * 
	 * @return ClientId
	 */
	private String getClientId() {
		PluginConfig pluginConfig = getPluginConfig();
		return pluginConfig.getAttribute("oauthKey");
	}

	/**
	 * 获取 ClientSecret
	 * 
	 * @return ClientSecret
	 */
	private String getClientSecret() {
		PluginConfig pluginConfig = getPluginConfig();
		return pluginConfig.getAttribute("oauthSecret");
	}

}