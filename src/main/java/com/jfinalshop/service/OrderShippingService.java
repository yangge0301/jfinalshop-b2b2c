package com.jfinalshop.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jfinalshop.Setting;
import com.jfinalshop.dao.OrderShippingDao;
import com.jfinalshop.dao.SnDao;
import com.jfinalshop.model.OrderShipping;
import com.jfinalshop.model.Sn;
import com.jfinalshop.util.Assert;
import com.jfinalshop.util.JsonUtils;
import com.jfinalshop.util.SystemUtils;
import com.jfinalshop.util.WebUtils;

/**
 * Service - 订单发货
 * 
 */
@Singleton
public class OrderShippingService extends BaseService<OrderShipping> {

	/**
	 * 构造方法
	 */
	public OrderShippingService() {
		super(OrderShipping.class);
	}
	
	@Inject
	private OrderShippingDao orderShippingDao;
	@Inject
	private SnDao snDao;
	
	/**
	 * 根据编号查找订单发货
	 * 
	 * @param sn
	 *            编号(忽略大小写)
	 * @return 订单发货，若不存在则返回null
	 */
	public OrderShipping findBySn(String sn) {
		return orderShippingDao.find("sn", StringUtils.lowerCase(sn));
	}

	/**
	 * 获取物流动态
	 * 
	 * @param orderShipping
	 *            订单发货
	 * @return 物流动态
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, String>> getTransitSteps(OrderShipping orderShipping) {
		Assert.notNull(orderShipping);

		Setting setting = SystemUtils.getSetting();
		if (StringUtils.isEmpty(setting.getKuaidi100Key()) || StringUtils.isEmpty(orderShipping.getDeliveryCorpCode()) || StringUtils.isEmpty(orderShipping.getTrackingNo())) {
			return Collections.emptyList();
		}
		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put("id", setting.getKuaidi100Key());
		parameterMap.put("com", orderShipping.getDeliveryCorpCode());
		parameterMap.put("nu", orderShipping.getTrackingNo());
		parameterMap.put("show", "0");
		parameterMap.put("muti", "1");
		parameterMap.put("order", "asc");
		String content = WebUtils.get("http://api.kuaidi100.com/api", parameterMap);
		Map<String, Object> data = JsonUtils.toObject(content, new TypeReference<Map<String, Object>>() {
		});
		if (!StringUtils.equals(String.valueOf(data.get("status")), "1")) {
			return Collections.emptyList();
		}
		return (List<Map<String, String>>) data.get("data");
	}

	@Override
	public OrderShipping save(OrderShipping orderShipping) {
		Assert.notNull(orderShipping);

		orderShipping.setSn(snDao.generate(Sn.Type.orderShipping));

		return super.save(orderShipping);
	}
}