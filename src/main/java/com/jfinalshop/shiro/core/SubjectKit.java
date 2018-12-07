package com.jfinalshop.shiro.core;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.subject.Subject;

import com.jfinal.plugin.activerecord.Model;
import com.jfinalshop.shiro.CaptchaUsernamePasswordToken;
import com.jfinalshop.util.EncriptionKit;


/**
 * Created by wangrenhui on 14-4-24.
 */
public class SubjectKit {

	private static String[] baseRole = new String[] { "R_ADMIN", "R_BUSINESS", "R_MEMBER", "R_USER" };

	//登录类型
	public enum UserType {
		
		/**
		 * 管理员
		 */
	    ADMIN, 
	    
	    /**
	     * 商家
	     */
	    BUSINESS, 
	    
	    /**
	     * 会员
	     */
	    MEMBER
	}
	
	private SubjectKit() {
	}

	public static Subject getSubject() {
		return SecurityUtils.getSubject();
	}

	public static Session getSession() {
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		if (session == null) {
			throw new UnknownSessionException("Unable found required Session");
		} else {
			return session;
		}
	}

	/**
	 * 获取用户对象
	 *
	 * @param <T>
	 *            User
	 * @return T User
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Model<?>> T getUser() {
		Subject subject = getSubject();
		Object user = subject.getPrincipal();
		if (user == null)
			return null;
		else {
			return (T) user;
		}
	}

	/**
	 * login user
	 *
	 * @param username
	 *            用户名
	 * @param password
	 *            密码 // * @param user 完整用户对象 // * @param T User
	 * @return bolean
	 */
	public static boolean login(String username, String password, UserType userType) {
		return login(username, password, false, userType);
	}

	public static boolean login(String username, String password, boolean rememberMe, UserType userType) {
		CaptchaUsernamePasswordToken token = new CaptchaUsernamePasswordToken(username, password, userType);
		try {
			token.setRememberMe(rememberMe);
			SecurityUtils.getSubject().login(token);
			return true;
		} catch (AuthenticationException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 验证验证码
	 *
	 * @param captchaName
	 *            captchaName
	 * @param captchaToken
	 *            token
	 * @return boolean
	 */
	public static boolean doCaptcha(String captchaName, String captchaToken) {
		Session session = getSession();
		if (session.getAttribute(captchaName) != null) {
			String captcha = session.getAttribute(captchaName).toString();
			if (captchaToken != null && captcha.equalsIgnoreCase(EncriptionKit.encrypt(captchaToken))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断是否已经登录
	 *
	 * @return boolean
	 */
	public static boolean isAuthed() {
		Subject subject = getSubject();
		if (subject == null || subject.getPrincipal() == null || (!subject.isAuthenticated() && !subject.isRemembered())) {
			return false;
		} else
			return true;
	}

	public static boolean wasBaseRole(String roleValue) {
		if (ArrayUtils.contains(baseRole, roleValue)) {
			return true;
		}
		return false;
	}

	public String[] getBaseRole() {
		return baseRole;
	}

	public void setBaseRole(String... baseRole) {
		SubjectKit.baseRole = baseRole;
	}
	
	/**
	 * 是否管理员
	 * @return
	 */
	public static boolean hasRoleAdmin() {
		Subject subject = SubjectKit.getSubject();
		if(subject.hasRole("R_ADMIN")) {
			return true;
		}
		return false;
	}
	
}