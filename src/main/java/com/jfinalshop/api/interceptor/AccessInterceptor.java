package com.jfinalshop.api.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

public class AccessInterceptor implements Interceptor {

	public void intercept(Invocation inv) {
		Controller c = inv.getController();
		c.getResponse().addHeader("Access-Control-Allow-Origin", "*");
		inv.invoke();
	}

}
