package com.jfinalshop.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinalshop.model.Business;
import com.jfinalshop.model.Store;
import com.jfinalshop.service.BusinessService;
import com.jfinalshop.util.HasorUtils;

public class BusinessInterceptor implements Interceptor {

	private static final BusinessService businessService = HasorUtils.getBean(BusinessService.class);
	
	@Override
	public void intercept(Invocation inv) {
		Controller c = inv.getController();
		inv.invoke();
		Store currentStore = businessService.getCurrentStore();
		Business currentUser = businessService.getCurrentUser();
		c.setAttr("currentStore", currentStore);
		c.setAttr("currentUser", currentUser);
		if (currentStore == null) {
			c.render("/business/common/main.ftl");
		}
		
	}

}
