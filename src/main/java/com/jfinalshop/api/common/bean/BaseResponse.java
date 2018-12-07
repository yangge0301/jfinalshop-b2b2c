package com.jfinalshop.api.common.bean;

import com.jfinalshop.Setting;
import com.jfinalshop.util.SystemUtils;

public class BaseResponse {

	private Integer	code	= Code.SUCCESS;

	private String	message;
	
	/** 系统设置 */
	protected Setting setting = SystemUtils.getSetting();
	
	/** 返回图片地址 */
	private String siteImageUrl = setting.getSiteImageUrl();
	
	/** 站点名称 */
	private String siteName = setting.getSiteName();
	
	public BaseResponse() {
	}

	public BaseResponse(String message) {
		this.message = message;
	}

	public BaseResponse(Integer code) {
		this.code = code;
	}

	public BaseResponse(Integer code, String message) {
		this.code = code;
		this.message = message;
	}

	public BaseResponse setCode(Integer code) {
		this.code = code;
		return this;
	}

	public BaseResponse setMessage(String message) {
		this.message = message;
		return this;
	}

	public Integer getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public String getSiteImageUrl() {
		return siteImageUrl;
	}

	public void setSiteImageUrl(String siteImageUrl) {
		this.siteImageUrl = siteImageUrl;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	
}
