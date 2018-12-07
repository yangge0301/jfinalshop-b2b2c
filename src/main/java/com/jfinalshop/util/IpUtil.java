package com.jfinalshop.util;

import javax.servlet.http.HttpServletRequest;


import com.jfinal.kit.StrKit;

/**
 * 类功能说明 TODO:IP工具类
 * 类修改者	修改日期
 * 修改说明
 */
public class IpUtil {

	/**
	 * 获取访问用户的客户端IP（适用于公网与局域网）.
	 */
	public static final String getIpAddr(final HttpServletRequest request) {
	    String ipString = request.getHeader("x-forwarded-for");
	    if (StrKit.isBlank(ipString) || "unknown".equalsIgnoreCase(ipString)) {
	        ipString = request.getHeader("Proxy-Client-IP");
	    }
	    if (StrKit.isBlank(ipString) || "unknown".equalsIgnoreCase(ipString)) {
	        ipString = request.getHeader("WL-Proxy-Client-IP");
	    }
	    if (StrKit.isBlank(ipString) || "unknown".equalsIgnoreCase(ipString)) {
	        ipString = request.getRemoteAddr();
	    }
	 
	    // 多个路由时，取第一个非unknown的ip
	    final String[] arr = ipString.split(",");
	    for (final String str : arr) {
	        if (!"unknown".equalsIgnoreCase(str)) {
	            ipString = str;
	            break;
	        }
	    }
	    String ipFromNginx = request.getHeader("X-Real-IP");
	    return StrKit.isBlank(ipFromNginx) ? ipString : ipFromNginx;
	}
	
}
