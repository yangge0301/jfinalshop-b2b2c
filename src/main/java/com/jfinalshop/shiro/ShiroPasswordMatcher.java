package com.jfinalshop.shiro;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.PasswordMatcher;

import com.jfinal.plugin.activerecord.Model;
import com.jfinalshop.shiro.hasher.Hasher;


/**
 * Created by wangrenhui on 14-1-3.
 */
public class ShiroPasswordMatcher extends PasswordMatcher {
	
	@Override
	public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
		boolean match = false;
		String hasher = ((Model<?>) info.getPrincipals().getPrimaryPrincipal()).get("hasher");
		String default_hasher = Hasher.DEFAULT.value();
		if (default_hasher.equals(hasher)) {
			match = super.doCredentialsMatch(token, info);
		}
		return match;
	}

	@Override
	protected Object getSubmittedPassword(AuthenticationToken token) {
		Object submit = super.getSubmittedPassword(token);
		if (submit instanceof char[]) {
			submit = String.valueOf((char[]) submit);
		}
		return submit;
	}

	@Override
	protected Object getStoredPassword(AuthenticationInfo storedUserInfo) {
		Object stored = super.getStoredPassword(storedUserInfo);

		if (stored instanceof char[]) {
			stored = String.valueOf((char[]) stored);
		}
		return stored;
	}
}