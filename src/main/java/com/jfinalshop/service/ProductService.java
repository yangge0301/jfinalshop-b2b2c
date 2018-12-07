package com.jfinalshop.service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.Pageable;
import com.jfinalshop.Setting;
import com.jfinalshop.dao.AttributeDao;
import com.jfinalshop.dao.BrandDao;
import com.jfinalshop.dao.ProductCategoryDao;
import com.jfinalshop.dao.ProductDao;
import com.jfinalshop.dao.ProductTagDao;
import com.jfinalshop.dao.PromotionDao;
import com.jfinalshop.dao.SkuBarcodeDao;
import com.jfinalshop.dao.SkuDao;
import com.jfinalshop.dao.SnDao;
import com.jfinalshop.dao.StockLogDao;
import com.jfinalshop.dao.StoreDao;
import com.jfinalshop.dao.StoreProductCategoryDao;
import com.jfinalshop.dao.StoreProductTagDao;
import com.jfinalshop.entity.SpecificationItem;
import com.jfinalshop.model.Attribute;
import com.jfinalshop.model.Brand;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.ProductCategory;
import com.jfinalshop.model.ProductProductTag;
import com.jfinalshop.model.ProductPromotion;
import com.jfinalshop.model.ProductStoreProductTag;
import com.jfinalshop.model.ProductTag;
import com.jfinalshop.model.Promotion;
import com.jfinalshop.model.Sku;
import com.jfinalshop.model.SkuBarcode;
import com.jfinalshop.model.Sn;
import com.jfinalshop.model.StockLog;
import com.jfinalshop.model.Store;
import com.jfinalshop.model.StoreProductCategory;
import com.jfinalshop.model.StoreProductTag;
import com.jfinalshop.util.Assert;
import com.jfinalshop.util.SystemUtils;
import com.xiaoleilu.hutool.util.CollectionUtil;

/**
 * Service - 商品
 * 
 */
@Singleton
public class ProductService extends BaseService<Product> {

	/**
	 * 构造方法
	 */
	public ProductService() {
		super(Product.class);
	}
	
	private final CacheManager cacheManager = CacheKit.getCacheManager();
	@Inject
	private ProductDao productDao;
	@Inject
	private SkuDao skuDao;
	@Inject
	private SnDao snDao;
	@Inject
	private ProductCategoryDao productCategoryDao;
	@Inject
	private StoreProductCategoryDao storeProductCategoryDao;
	@Inject
	private BrandDao brandDao;
	@Inject
	private PromotionDao promotionDao;
	@Inject
	private ProductTagDao productTagDao;
	@Inject
	private StoreProductTagDao storeProductTagDao;
	@Inject
	private AttributeDao attributeDao;
	@Inject
	private StockLogDao stockLogDao;
	@Inject
	private StoreDao storeDao;
	@Inject
	private SpecificationValueService specificationValueService;
	@Inject
	private SkuBarcodeDao skuBarcodeDao;
	
	
	/**
	 * 判断编号是否存在
	 * 
	 * @param sn
	 *            编号(忽略大小写)
	 * @return 编号是否存在
	 */
	public boolean snExists(String sn) {
		return productDao.exists("sn", sn, true);
	}


	/**
	 * 根据编号查找商品
	 * 
	 * @param sn
	 *            编号(忽略大小写)
	 * @return 商品，若不存在则返回null
	 */
	public Product findBySn(String sn) {
		return productDao.find("sn", sn, true);
	}

	/**
	 * 查找商品
	 * 
	 * @param type
	 *            类型
	 * @param store
	 *            店铺
	 * @param productCategory
	 *            商品分类
	 * @param storeProductCategory
	 *            店铺商品分类
	 * @param brand
	 *            品牌
	 * @param promotion
	 *            促销
	 * @param productTag
	 *            商品标签
	 * @param storeProductTag
	 *            店铺商品标签
	 * @param attributeValueMap
	 *            属性值Map
	 * @param startPrice
	 *            最低价格
	 * @param endPrice
	 *            最高价格
	 * @param isMarketable
	 *            是否上架
	 * @param isList
	 *            是否列出
	 * @param isTop
	 *            是否置顶
	 * @param isActive
	 *            是否有效
	 * @param isOutOfStock
	 *            是否缺货
	 * @param isStockAlert
	 *            是否库存警告
	 * @param hasPromotion
	 *            是否存在促销
	 * @param orderType
	 *            排序类型
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 商品
	 */
	public List<Product> findList(Product.Type type, Store store, ProductCategory productCategory, StoreProductCategory storeProductCategory, Brand brand, Promotion promotion, ProductTag productTag, StoreProductTag storeProductTag, Map<Attribute, String> attributeValueMap, BigDecimal startPrice,
			BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop, Boolean isActive, Boolean isOutOfStock, Boolean isStockAlert, Boolean hasPromotion, Product.OrderType orderType, Integer count, List<Filter> filters, List<Order> orders) {
		return productDao.findList(type, store, productCategory, storeProductCategory, brand, promotion, productTag, storeProductTag, attributeValueMap, startPrice, endPrice, isMarketable, isList, isTop, isActive, isOutOfStock, isStockAlert, hasPromotion, orderType, count, filters, orders);
	}

	/**
	 * 查找商品
	 * 
	 * @param type
	 *            类型
	 * @param storeId
	 *            店铺ID
	 * @param productCategoryId
	 *            商品分类ID
	 * @param storeProductCategoryId
	 *            店铺商品分类ID
	 * @param brandId
	 *            品牌ID
	 * @param promotionId
	 *            促销ID
	 * @param productTagId
	 *            商品标签ID
	 * @param storeProductTagId
	 *            店铺商品标签ID
	 * @param attributeValueMap
	 *            属性值Map
	 * @param startPrice
	 *            最低价格
	 * @param endPrice
	 *            最高价格
	 * @param isMarketable
	 *            是否上架
	 * @param isList
	 *            是否列出
	 * @param isTop
	 *            是否置顶
	 * @param isActive
	 *            是否有效
	 * @param isOutOfStock
	 *            是否缺货
	 * @param isStockAlert
	 *            是否库存警告
	 * @param hasPromotion
	 *            是否存在促销
	 * @param orderType
	 *            排序类型
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @param useCache
	 *            是否使用缓存
	 * @return 商品
	 */
	public List<Product> findList(Product.Type type, Long storeId, Long productCategoryId, Long storeProductCategoryId, Long brandId, Long promotionId, Long productTagId, Long storeProductTagId, Map<Long, String> attributeValueMap, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable,
			Boolean isList, Boolean isTop, Boolean isActive, Boolean isOutOfStock, Boolean isStockAlert, Boolean hasPromotion, Product.OrderType orderType, Integer count, List<Filter> filters, List<Order> orders, boolean useCache) {
		Store store = storeDao.find(storeId);
		if (storeId != null && store == null) {
			return Collections.emptyList();
		}
		ProductCategory productCategory = productCategoryDao.find(productCategoryId);
		if (productCategoryId != null && productCategory == null) {
			return Collections.emptyList();
		}
		StoreProductCategory storeProductCategory = storeProductCategoryDao.find(storeProductCategoryId);
		if (storeProductCategoryId != null && storeProductCategory == null) {
			return Collections.emptyList();
		}
		Brand brand = brandDao.find(brandId);
		if (brandId != null && brand == null) {
			return Collections.emptyList();
		}
		Promotion promotion = promotionDao.find(promotionId);
		if (promotionId != null && promotion == null) {
			return Collections.emptyList();
		}
		ProductTag productTag = productTagDao.find(productTagId);
		if (productTagId != null && productTag == null) {
			return Collections.emptyList();
		}
		StoreProductTag storeProductTag = storeProductTagDao.find(storeProductTagId);
		if (storeProductTagId != null && storeProductTag == null) {
			return Collections.emptyList();
		}
		Map<Attribute, String> map = new HashMap<>();
		if (attributeValueMap != null) {
			for (Map.Entry<Long, String> entry : attributeValueMap.entrySet()) {
				Attribute attribute = attributeDao.find(entry.getKey());
				if (attribute != null) {
					map.put(attribute, entry.getValue());
				}
			}
		}
		if (MapUtils.isNotEmpty(attributeValueMap) && MapUtils.isEmpty(map)) {
			return Collections.emptyList();
		}
		return productDao.findList(type, store, productCategory, storeProductCategory, brand, promotion, productTag, storeProductTag, map, startPrice, endPrice, isMarketable, isList, isTop, isActive, isOutOfStock, isStockAlert, hasPromotion, orderType, count, filters, orders);
	}
	
	/**
	 * 查找商品
	 * 
	 * @param rankingType
	 *            排名类型
	 * @param store
	 *            店铺
	 * @param count
	 *            数量
	 * @return 商品
	 */
	public List<Product> findList(Product.RankingType rankingType, Store store, Integer count) {
		return productDao.findList(rankingType, store, count);
	}

	/**
	 * 查找商品分页
	 * 
	 * @param type
	 *            类型
	 * @param store
	 *            店铺
	 * @param productCategory
	 *            商品分类
	 * @param storeProductCategory
	 *            店铺商品分类
	 * @param brand
	 *            品牌
	 * @param promotion
	 *            促销
	 * @param productTag
	 *            商品标签
	 * @param storeProductTag
	 *            店铺商品标签
	 * @param attributeValueMap
	 *            属性值Map
	 * @param startPrice
	 *            最低价格
	 * @param endPrice
	 *            最高价格
	 * @param isMarketable
	 *            是否上架
	 * @param isList
	 *            是否列出
	 * @param isTop
	 *            是否置顶
	 * @param isActive
	 *            是否有效
	 * @param isOutOfStock
	 *            是否缺货
	 * @param isStockAlert
	 *            是否库存警告
	 * @param hasPromotion
	 *            是否存在促销
	 * @param orderType
	 *            排序类型
	 * @param pageable
	 *            分页信息
	 * @return 商品分页
	 */
	public Page<Product> findPage(Product.Type type, Store store, ProductCategory productCategory, StoreProductCategory storeProductCategory, Brand brand, Promotion promotion, ProductTag productTag, StoreProductTag storeProductTag, Map<Attribute, String> attributeValueMap, BigDecimal startPrice,
			BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop, Boolean isActive, Boolean isOutOfStock, Boolean isStockAlert, Boolean hasPromotion, Product.OrderType orderType, Pageable pageable) {
		return productDao.findPage(type, store, productCategory, storeProductCategory, brand, promotion, productTag, storeProductTag, attributeValueMap, startPrice, endPrice, isMarketable, isList, isTop, isActive, isOutOfStock, isStockAlert, hasPromotion, orderType, pageable);
	}

	
	/**
	 * 查询商品数量
	 * 
	 * @param type
	 *            类型
	 * @param store
	 *            店铺
	 * @param isMarketable
	 *            是否上架
	 * @param isList
	 *            是否列出
	 * @param isTop
	 *            是否置顶
	 * @param isActive
	 *            是否有效
	 * @param isOutOfStock
	 *            是否缺货
	 * @param isStockAlert
	 *            是否库存警告
	 * @return 商品数量
	 */
	public Long count(Product.Type type, Store store, Boolean isMarketable, Boolean isList, Boolean isTop, Boolean isActive, Boolean isOutOfStock, Boolean isStockAlert) {
		return productDao.count(type, store, isMarketable, isList, isTop, isActive, isOutOfStock, isStockAlert);
	}
	
	/**
	 * 查看点击数
	 * 
	 * @param id
	 *            ID
	 * @return 点击数
	 */
	public long viewHits(Long id) {
		Assert.notNull(id);

		Ehcache cache = cacheManager.getEhcache(Product.HITS_CACHE_NAME);
		cache.acquireWriteLockOnKey(id);
		try {
			Element element = cache.get(id);
			Long hits;
			if (element != null) {
				hits = (Long) element.getObjectValue() + 1;
			} else {
				Product product = productDao.find(id);
				if (product == null) {
					return 0L;
				}
				hits = product.getHits() + 1;
			}
			cache.put(new Element(id, hits));
			return hits;
		} finally {
			cache.releaseWriteLockOnKey(id);
		}
	}

	/**
	 * 增加点击数
	 * 
	 * @param product
	 *            商品
	 * @param amount
	 *            值
	 */
	public void addHits(Product product, long amount) {
		Assert.notNull(product);
		Assert.state(amount >= 0);

		if (amount == 0) {
			return;
		}

		Calendar nowCalendar = Calendar.getInstance();
		Calendar weekHitsCalendar = DateUtils.toCalendar(product.getWeekHitsDate());
		Calendar monthHitsCalendar = DateUtils.toCalendar(product.getMonthHitsDate());
		if (nowCalendar.get(Calendar.YEAR) > weekHitsCalendar.get(Calendar.YEAR) || nowCalendar.get(Calendar.WEEK_OF_YEAR) > weekHitsCalendar.get(Calendar.WEEK_OF_YEAR)) {
			product.setWeekHits(amount);
		} else {
			product.setWeekHits(product.getWeekHits() + amount);
		}
		if (nowCalendar.get(Calendar.YEAR) > monthHitsCalendar.get(Calendar.YEAR) || nowCalendar.get(Calendar.MONTH) > monthHitsCalendar.get(Calendar.MONTH)) {
			product.setMonthHits(amount);
		} else {
			product.setMonthHits(product.getMonthHits() + amount);
		}
		product.setHits(product.getHits() + amount);
		product.setWeekHitsDate(new Date());
		product.setMonthHitsDate(new Date());
		productDao.update(product);
	}

	/**
	 * 增加销量
	 * 
	 * @param product
	 *            商品
	 * @param amount
	 *            值
	 */
	public void addSales(Product product, long amount) {
		Assert.notNull(product);
		Assert.state(amount >= 0);

		if (amount == 0) {
			return;
		}

//		if (!LockModeType.PESSIMISTIC_WRITE.equals(productDao.getLockMode(product))) {
//			productDao.flush();
//			productDao.refresh(product, LockModeType.PESSIMISTIC_WRITE);
//		}

		Calendar nowCalendar = Calendar.getInstance();
		Calendar weekSalesCalendar = DateUtils.toCalendar(product.getWeekSalesDate());
		Calendar monthSalesCalendar = DateUtils.toCalendar(product.getMonthSalesDate());
		if (nowCalendar.get(Calendar.YEAR) > weekSalesCalendar.get(Calendar.YEAR) || nowCalendar.get(Calendar.WEEK_OF_YEAR) > weekSalesCalendar.get(Calendar.WEEK_OF_YEAR)) {
			product.setWeekSales(amount);
		} else {
			product.setWeekSales(product.getWeekSales() + amount);
		}
		if (nowCalendar.get(Calendar.YEAR) > monthSalesCalendar.get(Calendar.YEAR) || nowCalendar.get(Calendar.MONTH) > monthSalesCalendar.get(Calendar.MONTH)) {
			product.setMonthSales(amount);
		} else {
			product.setMonthSales(product.getMonthSales() + amount);
		}
		product.setSales(product.getSales() + amount);
		product.setWeekSalesDate(new Date());
		product.setMonthSalesDate(new Date());
		//productDao.flush();
		productDao.update(product);
	}


	/**
	 * 创建
	 * 
	 * @param product
	 *            商品
	 * @param sku
	 *            SKU
	 * @return 商品
	 */
	public Product create(Product product, Sku sku) {
		Assert.notNull(product);
		Assert.isTrue(product.isNew());
		Assert.notNull(product.getType());
		Assert.isTrue(!product.hasSpecification());
		Assert.notNull(sku);
		Assert.isTrue(sku.isNew());
		Assert.state(!sku.hasSpecification());

		switch (product.getTypeName()) {
		case general:
			sku.setExchangePoint(0L);
			break;
		case exchange:
			sku.setPrice(BigDecimal.ZERO);
			sku.setRewardPoint(0L);
			product.setPromotions(null);
			break;
		case gift:
			sku.setPrice(BigDecimal.ZERO);
			sku.setRewardPoint(0L);
			sku.setExchangePoint(0L);
			product.setPromotions(null);
			break;
		default:
			break;
		}
		if (sku.getMarketPrice() == null) {
			sku.setMarketPrice(calculateDefaultMarketPrice(sku.getPrice()));
		}
		if (sku.getRewardPoint() == null) {
			sku.setRewardPoint(calculateDefaultRewardPoint(sku.getPrice()));
		} else {
			long maxRewardPoint = calculateMaxRewardPoint(sku.getPrice());
			sku.setRewardPoint(sku.getRewardPoint() > maxRewardPoint ? maxRewardPoint : sku.getRewardPoint());
		}
		sku.setAllocatedStock(0);
		sku.setIsDefault(true);
		sku.setProduct(product);

		product.setPrice(sku.getPrice());
		product.setCost(sku.getCost());
		product.setMarketPrice(sku.getMarketPrice());
		product.setIsActive(true);
		product.setScore(0F);
		product.setTotalScore(0L);
		product.setScoreCount(0L);
		product.setHits(0L);
		product.setWeekHits(0L);
		product.setMonthHits(0L);
		product.setSales(0L);
		product.setWeekSales(0L);
		product.setMonthSales(0L);
		product.setWeekHitsDate(new Date());
		product.setMonthHitsDate(new Date());
		product.setWeekSalesDate(new Date());
		product.setMonthSalesDate(new Date());
		setValue(product);
		productDao.save(product);

		setValue(sku);
		skuDao.save(sku);
		stockIn(sku);
		afterProductSave(product);
		
		return product;
	}

	/**
	 * 创建
	 * 
	 * @param product
	 *            商品
	 * @param skus
	 *            SKU
	 * @return 商品
	 */
	public Product create(Product product, List<Sku> skus) {
		Assert.notNull(product);
		Assert.isTrue(product.isNew());
		Assert.notNull(product.getType());
		Assert.isTrue(product.hasSpecification());
		Assert.notEmpty(skus);

		final List<SpecificationItem> specificationItems = product.getSpecificationItemsConverter();
		if (CollectionUtils.exists(skus, new Predicate() {
			private Set<List<Integer>> set = new HashSet<>();
			@Override
			public boolean evaluate(Object object) {
				Sku sku = (Sku) object;
				return sku == null || !sku.isNew() || !sku.hasSpecification() || !set.add(sku.getSpecificationValueIds()) || !specificationValueService.isValid(specificationItems, sku.getSpecificationValuesConverter());
			}
		})) {
			throw new IllegalArgumentException();
		}

		Sku defaultSku = (Sku) CollectionUtils.find(skus, new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				Sku sku = (Sku) object;
				return sku != null && sku.getIsDefault();
			}
		});
		if (defaultSku == null) {
			defaultSku = skus.get(0);
			defaultSku.setIsDefault(true);
		}

		for (Sku sku : skus) {
			switch (product.getTypeName()) {
			case general:
				sku.setExchangePoint(0L);
				break;
			case exchange:
				sku.setPrice(BigDecimal.ZERO);
				sku.setRewardPoint(0L);
				product.setPromotions(null);
				break;
			case gift:
				sku.setPrice(BigDecimal.ZERO);
				sku.setRewardPoint(0L);
				sku.setExchangePoint(0L);
				product.setPromotions(null);
				break;
			default:
				break;
			}
			if (sku.getMarketPrice() == null) {
				sku.setMarketPrice(calculateDefaultMarketPrice(sku.getPrice()));
			}
			if (sku.getRewardPoint() == null) {
				sku.setRewardPoint(calculateDefaultRewardPoint(sku.getPrice()));
			} else {
				long maxRewardPoint = calculateMaxRewardPoint(sku.getPrice());
				sku.setRewardPoint(sku.getRewardPoint() > maxRewardPoint ? maxRewardPoint : sku.getRewardPoint());
			}
			if (sku != defaultSku) {
				sku.setIsDefault(false);
			}
			sku.setAllocatedStock(0);
			sku.setProduct(product);
//			sku.setCartItems(null);
//			sku.setOrderItems(null);
//			sku.setOrderShippingItems(null);
//			sku.setProductNotifies(null);
//			sku.setStockLogs(null);
//			sku.setGiftPromotions(null);
		}

		product.setPrice(defaultSku.getPrice());
		product.setCost(defaultSku.getCost());
		product.setMarketPrice(defaultSku.getMarketPrice());
		product.setIsActive(true);
		product.setScore(0F);
		product.setTotalScore(0L);
		product.setScoreCount(0L);
		product.setHits(0L);
		product.setWeekHits(0L);
		product.setMonthHits(0L);
		product.setSales(0L);
		product.setWeekSales(0L);
		product.setMonthSales(0L);
		product.setWeekHitsDate(new Date());
		product.setMonthHitsDate(new Date());
		product.setWeekSalesDate(new Date());
		product.setMonthSalesDate(new Date());
//		product.setReviews(null);
//		product.setConsultations(null);
//		product.setProductFavorites(null);
//		product.setSkus(null);
		setValue(product);
		productDao.save(product);
		afterProductSave(product);
		
		for (Sku sku : skus) {
			setValue(sku);
			skuDao.save(sku);
			stockIn(sku);
		}

		return product;
	}

	/**
	 * 修改
	 * 
	 * @param product
	 *            商品
	 * @param sku
	 *            SKU
	 * @return 商品
	 */
	public Product modify(Product product, Sku sku) {
		Assert.notNull(product);
		Assert.isTrue(!product.isNew());
		Assert.isTrue(!product.hasSpecification());
		Assert.notNull(sku);
		Assert.isTrue(sku.isNew());
		Assert.state(!sku.hasSpecification());

		Product pProduct = productDao.find(product.getId());
		switch (pProduct.getTypeName()) {
		case general:
			sku.setExchangePoint(0L);
			break;
		case exchange:
			sku.setPrice(BigDecimal.ZERO);
			sku.setRewardPoint(0L);
			product.setPromotions(null);
			break;
		case gift:
			sku.setPrice(BigDecimal.ZERO);
			sku.setRewardPoint(0L);
			sku.setExchangePoint(0L);
			product.setPromotions(null);
			break;
		default:
			break;
		}
		if (sku.getMarketPrice() == null) {
			sku.setMarketPrice(calculateDefaultMarketPrice(sku.getPrice()));
		}
		if (sku.getRewardPoint() == null) {
			sku.setRewardPoint(calculateDefaultRewardPoint(sku.getPrice()));
		} else {
			long maxRewardPoint = calculateMaxRewardPoint(sku.getPrice());
			sku.setRewardPoint(sku.getRewardPoint() > maxRewardPoint ? maxRewardPoint : sku.getRewardPoint());
		}
		sku.setAllocatedStock(0);
		sku.setIsDefault(true);
		sku.setProduct(product);

		if (pProduct.hasSpecification()) {
			for (Sku pSku : pProduct.getSkus()) {
				skuDao.remove(pSku);
			}
			if (sku.getStock() == null) {
				throw new IllegalArgumentException();
			}
			setValue(sku);
			skuDao.save(sku);
			stockIn(sku);
		} else {
			Sku defaultSku = pProduct.getDefaultSku();
			defaultSku.setPrice(sku.getPrice());
			defaultSku.setCost(sku.getCost());
			defaultSku.setMarketPrice(sku.getMarketPrice());
			defaultSku.setRewardPoint(sku.getRewardPoint());
			defaultSku.setExchangePoint(sku.getExchangePoint());
			skuDao.update(defaultSku);
		}
		
		product.setPrice(sku.getPrice());
		product.setCost(sku.getCost());
		product.setMarketPrice(sku.getMarketPrice());
		setValue(product);
		copyProperties(product, pProduct, "sn", "type", "score", "totalScore", "scoreCount", "hits", "weekHits", "monthHits", "sales", "weekSales", "monthSales", "weekHitsDate", "monthHitsDate", "weekSalesDate", "monthSalesDate", "storeId", "createdDate");
		productDao.update(pProduct);
		
		return pProduct;
	}

	/**
	 * 修改
	 * 
	 * @param product
	 *            商品
	 * @param skus
	 *            SKU
	 * @return 商品
	 */
	public Product modify(Product product, List<Sku> skus) {
		Assert.notNull(product);
		Assert.isTrue(!product.isNew());
		Assert.isTrue(product.hasSpecification());
		Assert.notEmpty(skus);

		final List<SpecificationItem> specificationItems = product.getSpecificationItemsConverter();
		if (CollectionUtils.exists(skus, new Predicate() {
			private Set<List<Integer>> set = new HashSet<>();
			@Override
			public boolean evaluate(Object object) {
				Sku sku = (Sku) object;
				return sku == null || !sku.isNew() || !sku.hasSpecification() || !set.add(sku.getSpecificationValueIds()) || !specificationValueService.isValid(specificationItems, sku.getSpecificationValuesConverter());
			}
		})) {
			throw new IllegalArgumentException();
		}

		Sku defaultSku = (Sku) CollectionUtils.find(skus, new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				Sku sku = (Sku) object;
				return sku != null && sku.getIsDefault();
			}
		});
		if (defaultSku == null) {
			defaultSku = skus.get(0);
			defaultSku.setIsDefault(true);
		}

		Product pProduct = productDao.find(product.getId());
		for (Sku sku : skus) {
			switch (pProduct.getTypeName()) {
			case general:
				sku.setExchangePoint(0L);
				break;
			case exchange:
				sku.setPrice(BigDecimal.ZERO);
				sku.setRewardPoint(0L);
				product.setPromotions(null);
				break;
			case gift:
				sku.setPrice(BigDecimal.ZERO);
				sku.setRewardPoint(0L);
				sku.setExchangePoint(0L);
				product.setPromotions(null);
				break;
			default:
				break;
			}
			if (sku.getMarketPrice() == null) {
				sku.setMarketPrice(calculateDefaultMarketPrice(sku.getPrice()));
			}
			if (sku.getRewardPoint() == null) {
				sku.setRewardPoint(calculateDefaultRewardPoint(sku.getPrice()));
			} else {
				long maxRewardPoint = calculateMaxRewardPoint(sku.getPrice());
				sku.setRewardPoint(sku.getRewardPoint() > maxRewardPoint ? maxRewardPoint : sku.getRewardPoint());
			}
			if (sku != defaultSku) {
				sku.setIsDefault(false);
			}
			sku.setAllocatedStock(0);
			sku.setProduct(pProduct);
//			sku.setCartItems(null);
//			sku.setOrderItems(null);
//			sku.setOrderShippingItems(null);
//			sku.setProductNotifies(null);
//			sku.setStockLogs(null);
//			sku.setGiftPromotions(null);
		}

		if (pProduct.hasSpecification()) {
			for (Sku pSku : pProduct.getSkus()) {
				if (!exists(skus, pSku.getSpecificationValueIds())) {
					clearStockLog(pSku);
					skuDao.remove(pSku);
				}
			}
			for (Sku sku : skus) {
				Sku pSku = find(pProduct.getSkus(), sku.getSpecificationValueIds());
				if (pSku != null) {
					pSku.setPrice(sku.getPrice());
					pSku.setCost(sku.getCost());
					pSku.setMarketPrice(sku.getMarketPrice());
					pSku.setRewardPoint(sku.getRewardPoint());
					pSku.setExchangePoint(sku.getExchangePoint());
					pSku.setIsDefault(sku.getIsDefault());
					pSku.setSpecificationValues(sku.getSpecificationValues());
					skuDao.update(pSku);
				} else {
					if (sku.getStock() == null) {
						throw new IllegalArgumentException();
					}
					setValue(sku);
					skuDao.save(sku);
					stockIn(sku);
				}
			}
		} else {
			Sku pSku = pProduct.getDefaultSku();
			clearStockLog(pSku);
			skuDao.remove(pSku);
			for (Sku sku : skus) {
				if (sku.getStock() == null) {
					throw new IllegalArgumentException();
				}
				setValue(sku);
				skuDao.save(sku);
				stockIn(sku);
			}
		}

		product.setPrice(defaultSku.getPrice());
		product.setCost(defaultSku.getCost());
		product.setMarketPrice(defaultSku.getMarketPrice());
		setValue(product);
		copyProperties(product, pProduct, "sn", "type", "score", "totalScore", "scoreCount", "hits", "weekHits", "monthHits", "sales", "weekSales", "monthSales", "weekHitsDate", "monthHitsDate", "weekSalesDate", "monthSalesDate", "storeId", "createdDate");
		productDao.update(pProduct);
		
		return pProduct;
	}

	/**
	 * 刷新过期店铺商品有效状态
	 */
	public void refreshExpiredStoreProductActive() {
		productDao.refreshExpiredStoreProductActive();
	}

	/**
	 * 刷新商品有效状态
	 * 
	 * @param store
	 *            店铺
	 */
	public void refreshActive(Store store) {
		Assert.notNull(store);

		productDao.refreshActive(store);
	}

	@Override
	public Product save(Product product) {
		return super.save(product);
	}

	@Override
	public Product update(Product product) {
		return super.update(product);
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
	public void delete(Product product) {
		super.delete(product);
	}
	
	/**
	 * 保存后条码
	 * 
	 * @param product
	 *            商品
	 */
	public void saveBarcode(Product product) {
		if (product == null) {
			return;
		}
		Sku sku = product.getDefaultSku();
		List<SkuBarcode> skuBarcodes = sku.getSkuBarcodes();
		if (CollectionUtil.isNotEmpty(skuBarcodes)) {
			Db.deleteById("sku_barcode", "sku_id", sku.getId());
			for (SkuBarcode skuBarcode : skuBarcodes) {
				skuBarcode.setSkuId(sku.getId());
				skuBarcode.setStoreId(product.getStore().getId());
				skuBarcodeDao.save(skuBarcode);
			}
		}
	}
	
	/**
	 * 清空关连表
	 * 
	 * @param product
	 *            商品
	 */
	public void clear(Product product) {
		Db.deleteById("product_promotion", "products_id", product.getId());
		Db.deleteById("product_product_tag", "products_id", product.getId());
		Db.deleteById("product_store_product_tag", "products_id", product.getId());
	}
	
	/**
	 * 保存后设置促销、产品标签、店铺产品标签
	 * 
	 * @param product
	 *            商品
	 */
	private void afterProductSave(Product product) {
		if (product == null) {
			return;
		}

		// 关联保存促销
		List<Promotion> promotions = product.getPromotions();
		if (CollectionUtil.isNotEmpty(promotions)) {
			for (Promotion promotion : promotions) {
				ProductPromotion productPromotion = new ProductPromotion();
				productPromotion.setProductsId(product.getId());
				productPromotion.setPromotionsId(promotion.getId());
				productPromotion.save();
			}
		}
		
		// 关联保存产品标签
		List<ProductTag> productTags = product.getProductTags();
		if (CollectionUtil.isNotEmpty(productTags)) {
			for (ProductTag productTag : productTags) {
				ProductProductTag productProductTag = new ProductProductTag();
				productProductTag.setProductsId(product.getId());
				productProductTag.setProductTagsId(productTag.getId());
				productProductTag.save();
			}
		}
		
		// 关联保存店铺产品标签
		List<StoreProductTag> storeProductTags = product.getStoreProductTags();
		if (CollectionUtil.isNotEmpty(storeProductTags)) {
			for (StoreProductTag storeProductTag : storeProductTags) {
				ProductStoreProductTag productStoreProductTag = new ProductStoreProductTag();
				productStoreProductTag.setProductsId(product.getId());
				productStoreProductTag.setStoreProductTagsId(storeProductTag.getId());
				productStoreProductTag.save();
			}
		}
	}
	
	
	/**
	 * 清除库表中的sku记录
	 * @param sku
	 * 			SKU
	 */
	private void clearStockLog(Sku sku) {
		if (stockLogDao.exists("sku_id", sku.getId())) {
			Db.deleteById("stock_log", "sku_id", sku.getId());
		}
	}
	
	/**
	 * 设置商品值
	 * 
	 * @param product
	 *            商品
	 */
	private void setValue(Product product) {
		if (product == null) {
			return;
		}

		if (StringUtils.isEmpty(product.getImage()) && StringUtils.isNotEmpty(product.getThumbnail())) {
			product.setImage(product.getThumbnail());
		}
		if (product.isNew()) {
			if (StringUtils.isEmpty(product.getSn())) {
				String sn;
				do {
					sn = snDao.generate(Sn.Type.product);
				} while (snExists(sn));
				product.setSn(StringUtils.lowerCase(sn));
			}
			if (CollectionUtils.isNotEmpty(product.getProductImagesConverter())) {
				Collections.sort(product.getProductImagesConverter());
			}
		}
	}

	/**
	 * 设置SKU值
	 * 
	 * @param sku
	 *            SKU
	 */
	private void setValue(Sku sku) {
		if (sku == null) {
			return;
		}

		if (sku.isNew()) {
			Product product = sku.getProduct();
			if (product != null && StringUtils.isNotEmpty(product.getSn())) {
				String sn;
				int i = sku.hasSpecification() ? 1 : 0;
				do {
					sn = product.getSn() + (i == 0 ? "" : "_" + i);
					i++;
				} while (skuDao.exists("sn", sn, true));
				sku.setSn(sn);
				sku.setProductId(product.getId());
			}
		}
	}

	/**
	 * 计算默认市场价
	 * 
	 * @param price
	 *            价格
	 * @return 默认市场价
	 */
	private BigDecimal calculateDefaultMarketPrice(BigDecimal price) {
		Assert.notNull(price);

		Setting setting = SystemUtils.getSetting();
		Double defaultMarketPriceScale = setting.getDefaultMarketPriceScale();
		return defaultMarketPriceScale != null ? setting.setScale(price.multiply(new BigDecimal(String.valueOf(defaultMarketPriceScale)))) : BigDecimal.ZERO;
	}

	/**
	 * 计算默认赠送积分
	 * 
	 * @param price
	 *            价格
	 * @return 默认赠送积分
	 */
	private long calculateDefaultRewardPoint(BigDecimal price) {
		Assert.notNull(price);

		Setting setting = SystemUtils.getSetting();
		Double defaultPointScale = setting.getDefaultPointScale();
		return defaultPointScale != null ? price.multiply(new BigDecimal(String.valueOf(defaultPointScale))).longValue() : 0L;
	}

	/**
	 * 计算最大赠送积分
	 * 
	 * @param price
	 *            价格
	 * @return 最大赠送积分
	 */
	private long calculateMaxRewardPoint(BigDecimal price) {
		Assert.notNull(price);

		Setting setting = SystemUtils.getSetting();
		Double maxPointScale = setting.getMaxPointScale();
		return maxPointScale != null ? price.multiply(new BigDecimal(String.valueOf(maxPointScale))).longValue() : 0L;
	}

	/**
	 * 根据规格值ID查找SKU
	 * 
	 * @param skus
	 *            SKU
	 * @param specificationValueIds
	 *            规格值ID
	 * @return SKU
	 */
	private Sku find(Collection<Sku> skus, final List<Integer> specificationValueIds) {
		if (CollectionUtils.isEmpty(skus) || CollectionUtils.isEmpty(specificationValueIds)) {
			return null;
		}

		return (Sku) CollectionUtils.find(skus, new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				Sku sku = (Sku) object;
				return sku != null && sku.getSpecificationValueIds() != null && sku.getSpecificationValueIds().equals(specificationValueIds);
			}
		});
	}

	/**
	 * 根据规格值ID判断SKU是否存在
	 * 
	 * @param skus
	 *            SKU
	 * @param specificationValueIds
	 *            规格值ID
	 * @return SKU是否存在
	 */
	private boolean exists(Collection<Sku> skus, final List<Integer> specificationValueIds) {
		return find(skus, specificationValueIds) != null;
	}

	/**
	 * 入库
	 * 
	 * @param sku
	 *            SKU
	 */
	private void stockIn(Sku sku) {
		if (sku == null || sku.getStock() == null || sku.getStock() <= 0) {
			return;
		}

		StockLog stockLog = new StockLog();
		stockLog.setType(StockLog.Type.stockIn.ordinal());
		stockLog.setInQuantity(sku.getStock());
		stockLog.setOutQuantity(0);
		stockLog.setStock(sku.getStock());
		stockLog.setMemo(null);
		stockLog.setSkuId(sku.getId());
		stockLogDao.save(stockLog);
	}

}

