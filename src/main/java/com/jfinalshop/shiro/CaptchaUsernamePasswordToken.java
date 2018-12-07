package com.jfinalshop.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

import com.jfinalshop.shiro.core.SubjectKit.UserType;

/**
 * Created by wangrenhui on 14-1-3.
 */
public class CaptchaUsernamePasswordToken extends UsernamePasswordToken {

	private static final long serialVersionUID = -3598492658907566986L;
	private String captcha;
	private UserType userType;

	public String getCaptcha() {
		return captcha;
	}

	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}
	
	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}
	
	public CaptchaUsernamePasswordToken(String username, String password, boolean rememberMe, String host) {
		super(username, password, rememberMe, host);
	}

	public CaptchaUsernamePasswordToken(String username, String password, boolean rememberMe, String host, String captcha) {
		super(username, password, rememberMe, host);
		this.captcha = captcha;
	}
	
	public CaptchaUsernamePasswordToken(String username, String password, UserType userType) {
		super(username, password);
		this.userType = userType;
	}
}