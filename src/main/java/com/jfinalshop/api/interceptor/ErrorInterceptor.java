package com.jfinalshop.api.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.LogKit;
import com.jfinalshop.api.common.bean.BaseResponse;
import com.jfinalshop.api.common.bean.Code;

public class ErrorInterceptor implements Interceptor {

	@Override
	public void intercept(Invocation inv) {
		Controller c = inv.getController();
		try {
			inv.invoke();
		} catch (Exception e) {
			LogKit.error(e.getMessage(), e);
			c.renderJson(new BaseResponse(Code.ERROR, "server error"));
		}

	}

}
