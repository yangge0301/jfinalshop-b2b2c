package com.jfinalshop.service;

import net.hasor.core.Singleton;

import com.jfinalshop.model.Specification;

/**
 * Service - 规格
 * 
 */
@Singleton
public class SpecificationService extends BaseService<Specification> {

	/**
	 * 构造方法
	 */
	public SpecificationService() {
		super(Specification.class);
	}
	
}