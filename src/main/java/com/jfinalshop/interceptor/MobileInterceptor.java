package com.jfinalshop.interceptor;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.render.Render;
import com.jfinalshop.util.MobileWebUtils;

public class MobileInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		Controller c = inv.getController();
		c.getResponse().addHeader("Access-Control-Allow-Origin", "*");
		inv.invoke();
		Render render = c.getRender();
		String view = render.getView();
		String userAgent = StringUtils.lowerCase(c.getRequest().getHeader("USER-AGENT"));
		if (MobileWebUtils.check(userAgent)) {
			render.setView("/mobile" + view);
		}
	}

}
