package com.jfinalshop.api.common.bean;

import com.jfinal.plugin.activerecord.Model;

public class LoginResponse extends BaseResponse {
	
	private Model<?>	info;
	
	private String				token;

	public LoginResponse() {
		super();
	}

	public LoginResponse(Integer code) {
		super(code);
	}

	public LoginResponse(Integer code, String message) {
		super(code, message);
	}

	
	public Model<?> getInfo() {
		return info;
	}

	public void setInfo(Model<?> info) {
		this.info = info;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}