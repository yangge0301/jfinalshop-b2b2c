package com.jfinalshop.api.interceptor;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinalshop.api.common.bean.BaseResponse;
import com.jfinalshop.api.common.bean.Code;
import com.jfinalshop.api.common.token.TokenManager;
import com.jfinalshop.model.Member;

public class TokenInterceptor implements Interceptor {

	public void intercept(Invocation i) {
        Controller c = i.getController();
        // 解决跨域问题
        c.getResponse().addHeader("Access-Control-Allow-Origin", "*");
        
        // 文件上传API需要先getFiles
        if ("/api/fs/upload".equalsIgnoreCase(i.getActionKey()))
        	c.getFiles();
        
        String token = c.getPara("token");
        if (StringUtils.isEmpty(token)) {
            c.renderJson(new BaseResponse(Code.TOKEN_INVALID, "登录信息错误,请先登录!"));
            return;
        }

        Member member = TokenManager.getMe().validate(token);
        if (member == null) {
            c.renderJson(new BaseResponse(Code.TOKEN_INVALID, "登录信息失效了,请重新登录!"));
            return;
        }
        i.invoke();
    }
    
}
