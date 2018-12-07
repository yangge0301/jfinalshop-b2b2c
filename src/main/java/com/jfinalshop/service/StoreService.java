package com.jfinalshop.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.dao.ProductDao;
import com.jfinalshop.dao.StoreDao;
import com.jfinalshop.model.Business;
import com.jfinalshop.model.BusinessDepositLog;
import com.jfinalshop.model.CategoryApplication;
import com.jfinalshop.model.ProductCategory;
import com.jfinalshop.model.Store;
import com.jfinalshop.plugin.PromotionPlugin;
import com.jfinalshop.plugin.discountPromotion.DiscountPromotionPlugin;
import com.jfinalshop.plugin.fullReductionPromotion.FullReductionPromotionPlugin;
import com.jfinalshop.util.Assert;

/**
 * Service - 店铺
 * 
 */
@Singleton
public class StoreService extends BaseService<Store> {

	/**
	 * 构造方法
	 */
	public StoreService() {
		super(Store.class);
	}
	
	@Inject
	private StoreDao storeDao;
	@Inject
	private ProductDao productDao;
	@Inject
	private BusinessService businessService;
	@Inject
	private MailService mailService;
	@Inject
	private SmsService smsService;
	
	/**
	 * 判断名称是否存在
	 * 
	 * @param name
	 *            名称(忽略大小写)
	 * @return 名称是否存在
	 */
	public boolean nameExists(String name) {
		return storeDao.exists("name", name, true);
	}

	/**
	 * 判断名称是否唯一
	 * 
	 * @param id
	 *            ID
	 * @param name
	 *            名称(忽略大小写)
	 * @return 名称是否唯一
	 */
	public boolean nameUnique(Long id, String name) {
		return storeDao.unique(id, "name", name, true);
	}

	/**
	 * 判断经营分类是否存在
	 * 
	 * @param store
	 *            店铺
	 * @param productCategory
	 *            商品分类
	 * @return 经营分类是否存在
	 */
	public boolean productCategoryExists(Store store, final ProductCategory productCategory) {
		Assert.notNull(productCategory);
		Assert.notNull(store);

		return CollectionUtils.exists(store.getProductCategories(), new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				ProductCategory storeProductCategory = (ProductCategory) object;
				return storeProductCategory != null && storeProductCategory == productCategory;
			}
		});
	}

	/**
	 * 根据名称查找店铺
	 * 
	 * @param name
	 *            名称(忽略大小写)
	 * @return 店铺
	 */
	public Store findByName(String name) {
		return storeDao.find("name", name, true);
	}

	/**
	 * 查找店铺
	 * 
	 * @param type
	 *            类型
	 * @param status
	 *            状态
	 * @param isEnabled
	 *            是否启用
	 * @param hasExpired
	 *            是否过期
	 * @param first
	 *            起始记录
	 * @param count
	 *            数量
	 * @return 店铺
	 */
	public List<Store> findList(Store.Type type, Store.Status status, Boolean isEnabled, Boolean hasExpired, Integer first, Integer count) {
		return storeDao.findList(type, status, isEnabled, hasExpired, first, count);
	}

	/**
	 * 查找经营分类
	 * 
	 * @param store
	 *            店铺
	 * @param status
	 *            状态
	 * @return 经营分类
	 */
	public List<ProductCategory> findProductCategoryList(Store store, CategoryApplication.Status status) {
		return storeDao.findProductCategoryList(store, status);
	}

	/**
	 * 查找店铺分页
	 * 
	 * @param type
	 *            类型
	 * @param status
	 *            状态
	 * @param isEnabled
	 *            是否启用
	 * @param hasExpired
	 *            是否过期
	 * @param pageable
	 *            分页信息
	 * @return 店铺分页
	 */
	public Page<Store> findPage(Store.Type type, Store.Status status, Boolean isEnabled, Boolean hasExpired, Pageable pageable) {
		return storeDao.findPage(type, status, isEnabled, hasExpired, pageable);
	}

	/**
	 * 获取当前登录商家店铺
	 * 
	 * @return 当前登录商家店铺，若不存在则返回null
	 */
	public Store getCurrent() {
		Business currentUser = businessService.getCurrentUser();
		return currentUser != null ? currentUser.getStore() : null;
	}

	/**
	 * 增加到期天数
	 * 
	 * @param store
	 *            店铺
	 * @param amount
	 *            值
	 */
	public void addEndDays(Store store, int amount) {
		Assert.notNull(store);

		if (amount == 0) {
			return;
		}

		Date now = new Date();
		Date currentEndDate = store.getEndDate();
		if (amount > 0) {
			store.setEndDate(DateUtils.addDays(currentEndDate.after(now) ? currentEndDate : now, amount));
		} else {
			store.setEndDate(DateUtils.addDays(currentEndDate, amount));
		}
		storeDao.update(store);
	}

	/**
	 * 增加折扣促销到期天数
	 * 
	 * @param store
	 *            店铺
	 * @param amount
	 *            值
	 */
	public void addDiscountPromotionEndDays(Store store, int amount) {
		Assert.notNull(store);

		if (amount == 0) {
			return;
		}

		Date now = new Date();
		Date currentDiscountPromotionEndDate = store.getDiscountPromotionEndDate() != null ? store.getDiscountPromotionEndDate() : now;
		if (amount > 0) {
			store.setDiscountPromotionEndDate(DateUtils.addDays(currentDiscountPromotionEndDate.after(now) ? currentDiscountPromotionEndDate : now, amount));
		} else {
			store.setDiscountPromotionEndDate(DateUtils.addDays(currentDiscountPromotionEndDate, amount));
		}
		storeDao.update(store);
	}

	/**
	 * 增加满减促销到期天数
	 * 
	 * @param store
	 *            店铺
	 * @param amount
	 *            值
	 */
	public void addFullReductionPromotionEndDays(Store store, int amount) {
		Assert.notNull(store);

		if (amount == 0) {
			return;
		}

		Date now = new Date();
		Date currentFullReductionPromotionEndDate = store.getFullReductionPromotionEndDate() != null ? store.getFullReductionPromotionEndDate() : now;
		if (amount > 0) {
			store.setFullReductionPromotionEndDate(DateUtils.addDays(currentFullReductionPromotionEndDate.after(now) ? currentFullReductionPromotionEndDate : now, amount));
		} else {
			store.setFullReductionPromotionEndDate(DateUtils.addDays(currentFullReductionPromotionEndDate, amount));
		}
		storeDao.update(store);
	}

	/**
	 * 增加已付保证金
	 * 
	 * @param store
	 *            店铺
	 * @param amount
	 *            值
	 */
	public void addBailPaid(Store store, BigDecimal amount) {
		Assert.notNull(store);
		Assert.notNull(amount);

		if (amount.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}

		Assert.notNull(store.getBailPaid());
		Assert.state(store.getBailPaid().add(amount).compareTo(BigDecimal.ZERO) >= 0);

		store.setBailPaid(store.getBailPaid().add(amount));
		storeDao.update(store);
	}

	/**
	 * 审核
	 * 
	 * @param store
	 *            店铺
	 * @param passed
	 *            是否审核成功
	 * @param content
	 *            审核失败内容
	 */
	public void review(Store store, boolean passed, String content) {
		Assert.notNull(store);
		Assert.state(Store.Status.pending.equals(store.getStatusName()));
		Assert.state(passed || StringUtils.isNotEmpty(content));

		if (passed) {
			BigDecimal serviceFee = store.getStoreRank().getServiceFee();
			BigDecimal bail = store.getStoreCategory().getBail();
			if (serviceFee.compareTo(BigDecimal.ZERO) <= 0 && bail.compareTo(BigDecimal.ZERO) <= 0) {
				store.setStatus(Store.Status.success.ordinal());
				store.setEndDate(DateUtils.addYears(new Date(), 1));
			} else {
				store.setStatus(Store.Status.approved.ordinal());
				store.setEndDate(new Date());
			}
			storeDao.update(store);
			//smsService.sendApprovalStoreSms(store);
			//mailService.sendApprovalStoreMail(store);
		} else {
			store.setStatus(Store.Status.failed.ordinal());
			//smsService.sendFailStoreSms(store, content);
			//mailService.sendFailStoreMail(store, content);
		}
	}

	/**
	 * 购买促销插件
	 * 
	 * @param store
	 *            店铺
	 * @param promotionPlugin
	 *            促销插件
	 * @param months
	 *            月数
	 */
	public void buy(Store store, PromotionPlugin promotionPlugin, int months) {
		Assert.notNull(store);
		Assert.notNull(promotionPlugin);
		Assert.state(promotionPlugin.getIsEnabled());
		Assert.state(months > 0);

		BigDecimal amount = promotionPlugin.getPrice().multiply(new BigDecimal(months));
		Business business = store.getBusiness();
		Assert.state(business.getBalance() != null && business.getBalance().compareTo(amount) >= 0);

		int days = months * 30;
		if (promotionPlugin instanceof DiscountPromotionPlugin) {
			addDiscountPromotionEndDays(store, days);
			businessService.addBalance(business, amount.negate(), BusinessDepositLog.Type.svcPayment, null);
		} else if (promotionPlugin instanceof FullReductionPromotionPlugin) {
			addFullReductionPromotionEndDays(store, days);
			businessService.addBalance(business, amount.negate(), BusinessDepositLog.Type.svcPayment, null);
		}
	}

	/**
	 * 过期店铺处理
	 */
	public void expiredStoreProcessing() {
		productDao.refreshExpiredStoreProductActive();
	}

	@Override
	public Store save(Store store) {
		super.save(store);
		// 保存店铺商品分类
//		List<ProductCategory> productCategories = store.getProductCategories();
//		if (CollectionUtils.isNotEmpty(productCategories)) {
//			List<Record> recordList = new ArrayList<Record>();
//			for (ProductCategory productCategory : productCategories) {
//				Record record = new Record();
//				record.set("stores_id", store.getId());
//				record.set("product_categories_id", productCategory.getId());
//				recordList.add(record);
//			}
//			Db.batchSave("store_product_category", recordList, 10);
//		}
		return store;
	}
	
	@Override
	public Store update(Store store) {
		productDao.refreshActive(store);

		return super.update(store);
	}
	
	@Override
	public void delete(Long id) {
		super.delete(id);
	}
	
	@Override
	public void delete(Long... ids) {
		super.delete(ids);
	}
	
	@Override
	public void delete(Store store) {
		super.delete(store);
	}

}