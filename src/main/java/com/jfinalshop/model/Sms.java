package com.jfinalshop.model;

import com.jfinalshop.model.base.BaseSms;

/**
 * Model - 短信
 * 
 */
public class Sms extends BaseSms<Sms> {
	private static final long serialVersionUID = -8698609986551556657L;
	public static final Sms dao = new Sms().dao();
}
