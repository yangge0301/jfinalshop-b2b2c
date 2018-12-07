package com.jfinalshop.shiro;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.realm.Realm;

import com.xiaoleilu.hutool.util.CollectionUtil;

/**
 * 自定义认证器
 * 
 */
public class MyRealmAuthenticator extends ModularRealmAuthenticator {

	/**
	 * 根据用户类型判断使用哪个Realm
	 */
	@Override
	protected AuthenticationInfo doAuthenticate(AuthenticationToken token) throws AuthenticationException {
		
		// 判断getRealms()是否返回为空
        assertRealmsConfigured();
        
        // 强制转换回自定义的
        CaptchaUsernamePasswordToken user = (CaptchaUsernamePasswordToken) token;
		
		// 所有Realm
        Collection<Realm> realms = getRealms();
		Realm realm = null;
		if (CollectionUtil.isNotEmpty(realms)) {
			for (Realm pRealm : realms) {
	           String pRealmName = StringUtils.remove(pRealm.getName(), "Realm");
	           if (StringUtils.equals(StringUtils.upperCase(pRealmName), user.getUserType().name())) {
	        	   realm = pRealm;
	           }
		    }
		}
		return this.doSingleRealmAuthentication(realm, token);
	}

}
