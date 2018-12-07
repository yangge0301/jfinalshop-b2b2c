package com.jfinalshop.service;

import java.util.ArrayList;
import java.util.List;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.jfinalshop.Order;
import com.jfinalshop.dao.SnDao;
import com.jfinalshop.dao.SvcDao;
import com.jfinalshop.model.Sn;
import com.jfinalshop.model.Store;
import com.jfinalshop.model.StoreRank;
import com.jfinalshop.model.Svc;
import com.jfinalshop.util.Assert;

/**
 * Service - 服务
 * 
 */
@Singleton
public class SvcService extends BaseService<Svc> {

	/**
	 * 构造方法
	 */
	public SvcService() {
		super(Svc.class);
	}
	
	@Inject
	private SvcDao svcDao;
	@Inject
	private SnDao snDao;
	
	/**
	 * 根据编号查找服务
	 * 
	 * @param sn
	 *            编号(忽略大小写)
	 * @return 服务，若不存在则返回null
	 */
	public Svc findBySn(String sn) {
		return svcDao.find("sn", StringUtils.lowerCase(sn));
	}

	/**
	 * 查找最新服务
	 * 
	 * @param store
	 *            店铺
	 * @param promotionPluginId
	 *            促销插件Id
	 * @param storeRank
	 *            店铺等级
	 * @return 最新服务
	 */
	public Svc findTheLatest(Store store, String promotionPluginId, StoreRank storeRank) {

		List<Order> orderList = new ArrayList<>();
		orderList.add(new Order("createdDate", Order.Direction.desc));
		List<Svc> serviceOrders = svcDao.find(store, promotionPluginId, storeRank, orderList);

		return CollectionUtils.isNotEmpty(serviceOrders) ? serviceOrders.get(0) : null;
	}

	@Override
	public Svc save(Svc svc) {
		Assert.notNull(svc);

		svc.setSn(snDao.generate(Sn.Type.platformService));

		return super.save(svc);
	}
}