package com.jfinalshop.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import org.apache.commons.collections.CollectionUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.Setting;
import com.jfinalshop.dao.DefaultFreightConfigDao;
import com.jfinalshop.model.Area;
import com.jfinalshop.model.AreaFreightConfig;
import com.jfinalshop.model.DefaultFreightConfig;
import com.jfinalshop.model.PaymentMethod;
import com.jfinalshop.model.Receiver;
import com.jfinalshop.model.ShippingMethod;
import com.jfinalshop.model.ShippingMethodPaymentMethod;
import com.jfinalshop.model.Store;
import com.jfinalshop.util.Assert;
import com.jfinalshop.util.SystemUtils;
import com.xiaoleilu.hutool.util.CollectionUtil;

/**
 * Service - 配送方式
 * 
 */
@Singleton
public class ShippingMethodService extends BaseService<ShippingMethod> {

	/**
	 * 构造方法
	 */
	public ShippingMethodService() {
		super(ShippingMethod.class);
	}
	
	@Inject
	private DefaultFreightConfigDao defaultFreightConfigDao;
	
	/**
	 * 计算运费
	 * 
	 * @param shippingMethod
	 *            配送方式
	 * @param store
	 *            店铺
	 * @param area
	 *            地区
	 * @param weight
	 *            重量
	 * @return 运费
	 */
	public BigDecimal calculateFreight(ShippingMethod shippingMethod, Store store, Area area, Integer weight) {
		Assert.notNull(shippingMethod);

		Setting setting = SystemUtils.getSetting();
		DefaultFreightConfig defaultFreightConfig = defaultFreightConfigDao.find(shippingMethod, store);
		BigDecimal firstPrice = defaultFreightConfig != null ? defaultFreightConfig.getFirstPrice() : BigDecimal.ZERO;
		BigDecimal continuePrice = defaultFreightConfig != null ? defaultFreightConfig.getContinuePrice() : BigDecimal.ZERO;
		Integer firstWeight = defaultFreightConfig != null ? defaultFreightConfig.getFirstWeight() : 0;
		Integer continueWeight = defaultFreightConfig != null ? defaultFreightConfig.getContinueWeight() : 1;
		if (area != null && CollectionUtils.isNotEmpty(shippingMethod.getAreaFreightConfigs())) {
			List<Area> areas = new ArrayList<>();
			areas.addAll(area.getParents());
			areas.add(area);
			for (int i = areas.size() - 1; i >= 0; i--) {
				AreaFreightConfig areaFreightConfig = shippingMethod.getAreaFreightConfig(store, areas.get(i));
				if (areaFreightConfig != null) {
					firstPrice = areaFreightConfig.getFirstPrice();
					continuePrice = areaFreightConfig.getContinuePrice();
					firstWeight = areaFreightConfig.getFirstWeight();
					continueWeight = areaFreightConfig.getContinueWeight();
					break;
				}
			}
		}
		if (weight == null || weight <= firstWeight || continuePrice.compareTo(BigDecimal.ZERO) == 0) {
			return setting.setScale(firstPrice);
		} else {
			double contiuneWeightCount = Math.ceil((weight - firstWeight) / (double) continueWeight);
			return setting.setScale(firstPrice.add(continuePrice.multiply(new BigDecimal(String.valueOf(contiuneWeightCount)))));
		}
	}

	/**
	 * 计算运费
	 * 
	 * @param shippingMethod
	 *            配送方式
	 * @param store
	 *            店铺
	 * @param receiver
	 *            收货地址
	 * @param weight
	 *            重量
	 * @return 运费
	 */
	public BigDecimal calculateFreight(ShippingMethod shippingMethod, Store store, Receiver receiver, Integer weight) {
		return calculateFreight(shippingMethod, store, receiver != null ? receiver.getArea() : null, weight);
	}
	
	@Override
	public ShippingMethod save(ShippingMethod shippingMethod) {
		super.save(shippingMethod);
		// 关联保存
		List<PaymentMethod> paymentMethods = shippingMethod.getPaymentMethods();
		if (CollectionUtil.isNotEmpty(paymentMethods)) {
			for (PaymentMethod paymentMethod : paymentMethods) {
				ShippingMethodPaymentMethod shippingMethodPaymentMethod = new ShippingMethodPaymentMethod();
				shippingMethodPaymentMethod.setPaymentMethodsId(paymentMethod.getId());
				shippingMethodPaymentMethod.setShippingMethodsId(shippingMethod.getId());
				shippingMethodPaymentMethod.save();
			}
		}
		return shippingMethod;
	}
	
	@Override
	public ShippingMethod update(ShippingMethod shippingMethod) {
		super.update(shippingMethod);
		
		//先清除，再保存
		Db.deleteById("shipping_method_payment_method", "shipping_methods_id", shippingMethod.getId());
		List<PaymentMethod> paymentMethods = shippingMethod.getPaymentMethods();
		if (CollectionUtil.isNotEmpty(paymentMethods)) {
			for (PaymentMethod paymentMethod : paymentMethods) {
				ShippingMethodPaymentMethod shippingMethodPaymentMethod = new ShippingMethodPaymentMethod();
				shippingMethodPaymentMethod.setPaymentMethodsId(paymentMethod.getId());
				shippingMethodPaymentMethod.setShippingMethodsId(shippingMethod.getId());
				shippingMethodPaymentMethod.save();
			}
		}
		return shippingMethod;
	}
}