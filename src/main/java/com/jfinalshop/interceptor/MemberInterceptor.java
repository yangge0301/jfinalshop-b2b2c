package com.jfinalshop.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinalshop.model.Member;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.util.HasorUtils;

public class MemberInterceptor implements Interceptor{

	private static final MemberService memberService = HasorUtils.getBean(MemberService.class);
	
	@Override
	public void intercept(Invocation inv) {
		Controller c = inv.getController();
		c.getResponse().addHeader("Access-Control-Allow-Origin", "*");
		inv.invoke();
		Member member = memberService.getCurrentUser();
		if (member != null) {
			c.setAttr("currentUser", member);
			c.setAttr("currentMemberUsername", member.getUsername());
		}
	}

}
