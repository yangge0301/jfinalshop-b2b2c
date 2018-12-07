package com.jfinalshop.service;

import java.util.List;
import java.util.Set;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;

import com.jfinalshop.dao.SkuDao;
import com.jfinalshop.dao.StockLogDao;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.Sku;
import com.jfinalshop.model.StockLog;
import com.jfinalshop.model.Store;
import com.jfinalshop.util.Assert;

/**
 * Service - SKU
 * 
 */
@Singleton
public class SkuService extends BaseService<Sku> {

	/**
	 * 构造方法
	 */
	public SkuService() {
		super(Sku.class);
	}
	
	@Inject
	private SkuDao skuDao;
	@Inject
	private StockLogDao stockLogDao;
	
	/**
	 * 判断编号是否存在
	 * 
	 * @param sn
	 *            编号(忽略大小写)
	 * @return 编号是否存在
	 */
	public boolean snExists(String sn) {
		return skuDao.exists("sn", StringUtils.lowerCase(sn));
	}

	/**
	 * 根据编号查找SKU
	 * 
	 * @param sn
	 *            编号(忽略大小写)
	 * @return SKU，若不存在则返回null
	 */
	public Sku findBySn(String sn) {
		return skuDao.find("sn", StringUtils.lowerCase(sn));
	}

	/**
	 * 通过编号、名称查找SKU
	 * 
	 * @param store
	 *            店铺
	 * @param type
	 *            类型
	 * @param keyword
	 *            关键词
	 * @param excludes
	 *            排除SKU
	 * @param count
	 *            数量
	 * @return SKU
	 */
	public List<Sku> search(Store store, Product.Type type, String keyword, Set<Sku> excludes, Integer count) {
		return skuDao.search(store, type, keyword, excludes, count);
	}

	/**
	 * 增加库存
	 * 
	 * @param sku
	 *            SKU
	 * @param amount
	 *            值
	 * @param type
	 *            类型
	 * @param memo
	 *            备注
	 */
	public void addStock(Sku sku, int amount, StockLog.Type type, String memo) {
		Assert.notNull(sku);
		Assert.notNull(type);

		if (amount == 0) {
			return;
		}

		Assert.notNull(sku.getStock());
		Assert.state(sku.getStock() + amount >= 0);

		sku.setStock(sku.getStock() + amount);
		skuDao.update(sku);

		StockLog stockLog = new StockLog();
		stockLog.setType(type.ordinal());
		stockLog.setInQuantity(amount > 0 ? amount : 0);
		stockLog.setOutQuantity(amount < 0 ? Math.abs(amount) : 0);
		stockLog.setStock(sku.getStock());
		stockLog.setMemo(memo);
		stockLog.setSkuId(sku.getId());
		stockLogDao.save(stockLog);
	}

	/**
	 * 增加已分配库存
	 * 
	 * @param sku
	 *            SKU
	 * @param amount
	 *            值
	 */
	public void addAllocatedStock(Sku sku, int amount) {
		Assert.notNull(sku);

		if (amount == 0) {
			return;
		}

		Assert.notNull(sku.getAllocatedStock());
		Assert.state(sku.getAllocatedStock() + amount >= 0);

		sku.setAllocatedStock(sku.getAllocatedStock() + amount);
		skuDao.update(sku);
	}

	/**
	 * SKU过滤
	 * 
	 * @param skus
	 *            SKU
	 */
	public void filter(List<Sku> skus) {
		CollectionUtils.filter(skus, new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				Sku sku = (Sku) object;
				return sku != null && sku.getStock() != null;
			}
		});
	}

}