package com.jfinalshop.service;

import net.hasor.core.Singleton;

import com.jfinalshop.model.Parameter;

/**
 * Service - 参数
 * 
 */
@Singleton
public class ParameterService extends BaseService<Parameter> {

	/**
	 * 构造方法
	 */
	public ParameterService() {
		super(Parameter.class);
	}
	
}