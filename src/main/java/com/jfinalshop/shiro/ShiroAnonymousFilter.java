package com.jfinalshop.shiro;

import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Created by wangrenhui on 14-1-9.
 */
public class ShiroAnonymousFilter extends ShiroFormAuthenticationFilter {

	private static final String DEFAULT_USER_NAME = "guest";
	private static final String DEFAULT_PASSWORD = "guest";
	private static final String DEFAULT_ROLE = "guest";
	private static String username = DEFAULT_USER_NAME;
	private static String password = DEFAULT_PASSWORD;
	private static String role = DEFAULT_ROLE;

	@SuppressWarnings("null")
	public void setGuest(List<String> guestString) {
		if (guestString == null && guestString.size() < 2) {
			return;
		}
		String[] usernamepassword = guestString.get(0).split(":");
		if (usernamepassword.length == 2) {
			username = usernamepassword[0];
			password = usernamepassword[1];
		}
		role = guestString.get(1);
	}

	/**
	 * Always returns <code>true</code> allowing unchecked access to the
	 * underlying path or resource.
	 *
	 * @return <code>true</code> always, allowing unchecked access to the
	 *         underlying path or resource.
	 */
	@Override
	public boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) {
		return true;
	}

	public static String getPassword() {
		return password;
	}

	public static String getRole() {
		return role;
	}

	public static String getUsername() {
		return username;
	}

}
