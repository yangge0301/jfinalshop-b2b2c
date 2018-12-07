package com.jfinalshop.service;

import net.hasor.core.Singleton;

import com.jfinalshop.model.DeliveryCorp;

/**
 * Service - 物流公司
 * 
 */
@Singleton
public class DeliveryCorpService extends BaseService<DeliveryCorp> {

	/**
	 * 构造方法
	 */
	public DeliveryCorpService() {
		super(DeliveryCorp.class);
	}
	
}