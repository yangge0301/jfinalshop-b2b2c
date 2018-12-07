package com.jfinalshop.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinalshop.model.base.BaseStore;

/**
 * Model - 店铺
 * 
 */
public class Store extends BaseStore<Store> {
	private static final long serialVersionUID = -5750636823094640354L;
	public static final Store dao = new Store().dao();
	
	/**
	 * 路径
	 */
	private static final String PATH = "/store/%d";

	/**
	 * 类型
	 */
	public enum Type {

		/**
		 * 普通
		 */
		general,

		/**
		 * 自营
		 */
		self
	}

	/**
	 * 状态
	 */
	public enum Status {

		/**
		 * 等待审核
		 */
		pending,

		/**
		 * 审核失败
		 */
		failed,

		/**
		 * 审核通过
		 */
		approved,

		/**
		 * 开店成功
		 */
		success
	}
	
	/**
	 * 商家
	 */
	private Business business;

	/**
	 * 店铺等级
	 */
	private StoreRank storeRank;

	/**
	 * 店铺分类
	 */
	private StoreCategory storeCategory;

	/**
	 * 店铺广告图片
	 */
	private List<StoreAdImage> storeAdImages = new ArrayList<>();

	/**
	 * 即时通讯
	 */
	private List<InstantMessage> instantMessages = new ArrayList<>();

	/**
	 * 店铺商品分类
	 */
	private List<StoreProductCategory> storeProductCategories = new ArrayList<>();

	/**
	 * 经营分类
	 */
	private List<ProductCategory> productCategories = new ArrayList<>();

	/**
	 * 经营分类申请
	 */
	private List<CategoryApplication> categoryApplications = new ArrayList<>();

	/**
	 * 店铺商品标签
	 */
	private List<StoreProductTag> storeProductTags = new ArrayList<>();

	/**
	 * 商品
	 */
	private List<Product> products = new ArrayList<>();

	/**
	 * 促销
	 */
	private List<Promotion> promotions = new ArrayList<>();

	/**
	 * 优惠券
	 */
	private List<Coupon> coupons = new ArrayList<>();

	/**
	 * 订单
	 */
	private List<Order> order = new ArrayList<>();

	/**
	 * 店铺收藏
	 */
	private List<StoreFavorite> storeFavorites = new ArrayList<>();

	/**
	 * 快递单模板
	 */
	private List<DeliveryTemplate> deliveryTemplates = new ArrayList<>();

	/**
	 * 发货点
	 */
	private List<DeliveryCenter> deliveryCenters = new ArrayList<>();

	/**
	 * 默认运费配置
	 */
	private List<DefaultFreightConfig> defaultFreightConfigs = new ArrayList<>();

	/**
	 * 地区运费配置
	 */
	private List<AreaFreightConfig> areaFreightConfigs = new ArrayList<>();

	/**
	 * 服务
	 */
	private List<Svc> svcs = new ArrayList<>();

	/**
	 * 支付事务
	 */
	private List<PaymentTransaction> paymentTransactions = new ArrayList<>();

	/**
	 * 咨询
	 */
	private List<Consultation> consultations = new ArrayList<>();

	/**
	 * 评论
	 */
	private List<Review> reviews = new ArrayList<>();

	/**
	 * 统计
	 */
	private List<Statistic> statistics = new ArrayList<>();
	
	/**
	 * 类型名称
	 */
	public Type getTypeName() {
		return getType() != null ? Type.values()[getType()] : null;
	}
	
	/**
	 * 状态
	 */
	public Status getStatusName() {
		return getStatus() != null ? Status.values()[getStatus()] : null;
	}
	
	/**
	 * 获取商家
	 * 
	 * @return 商家
	 */
	public Business getBusiness() {
		if (business == null) {
			business = Business.dao.findById(getBusinessId());
		}
		return business;
	}

	/**
	 * 设置商家
	 * 
	 * @param business
	 *            商家
	 */
	public void setBusiness(Business business) {
		this.business = business;
	}

	/**
	 * 获取店铺等级
	 * 
	 * @return 店铺等级
	 */
	public StoreRank getStoreRank() {
		if (storeRank == null) {
			storeRank = StoreRank.dao.findById(getStoreRankId());
		}
		return storeRank;
	}

	/**
	 * 设置店铺等级
	 * 
	 * @param storeRank
	 *            店铺等级
	 */
	public void setStoreRank(StoreRank storeRank) {
		this.storeRank = storeRank;
	}

	/**
	 * 获取店铺分类
	 * 
	 * @return 店铺分类
	 */
	public StoreCategory getStoreCategory() {
		if (storeCategory == null) {
			storeCategory = StoreCategory.dao.findById(getStoreCategoryId());
		}
		return storeCategory;
	}

	/**
	 * 设置店铺分类
	 * 
	 * @param storeCategory
	 *            店铺分类
	 */
	public void setStoreCategory(StoreCategory storeCategory) {
		this.storeCategory = storeCategory;
	}

	/**
	 * 获取店铺广告图片
	 * 
	 * @return 店铺广告图片
	 */
	public List<StoreAdImage> getStoreAdImages() {
		if (CollectionUtils.isEmpty(storeAdImages)) {
			String sql = "SELECT * FROM `store_ad_image` WHERE store_id = ?";
			storeAdImages = StoreAdImage.dao.find(sql, getId());
		}
		return storeAdImages;
	}

	/**
	 * 设置店铺广告图片
	 * 
	 * @param storeAdImages
	 *            店铺广告图片
	 */
	public void setStoreAdImages(List<StoreAdImage> storeAdImages) {
		this.storeAdImages = storeAdImages;
	}

	/**
	 * 获取即时通讯
	 * 
	 * @return 即时通讯
	 */
	public List<InstantMessage> getInstantMessages() {
		if (CollectionUtils.isEmpty(instantMessages)) {
			String sql = "SELECT * FROM `instant_message` WHERE store_id = ?";
			instantMessages = InstantMessage.dao.find(sql, getId());
		}
		return instantMessages;
	}

	/**
	 * 设置即时通讯
	 * 
	 * @param instantMessages
	 *            即时通讯
	 */
	public void setInstantMessages(List<InstantMessage> instantMessages) {
		this.instantMessages = instantMessages;
	}

	/**
	 * 获取店铺商品分类
	 * 
	 * @return 店铺商品分类
	 */
	public List<StoreProductCategory> getStoreProductCategories() {
		if (CollectionUtils.isEmpty(storeProductCategories)) {
			String sql = "SELECT * FROM store_product_category WHERE store_id = ?";
			storeProductCategories = StoreProductCategory.dao.find(sql, getId());
		}
		return storeProductCategories;
	}

	/**
	 * 设置店铺商品分类
	 * 
	 * @param storeProductCategories
	 *            店铺商品分类
	 */
	public void setStoreProductCategories(List<StoreProductCategory> storeProductCategories) {
		this.storeProductCategories = storeProductCategories;
	}

	/**
	 * 获取经营分类
	 * 
	 * @return 经营分类
	 */
	public List<ProductCategory> getProductCategories() {
		if (CollectionUtils.isEmpty(productCategories)) {
			String sql = "SELECT p.* FROM `product_category_store` pct LEFT JOIN `product_category` p ON pct.`product_categories_id` = p.`id` WHERE pct.`stores_id` = ? ORDER BY `orders` ASC";
			productCategories = ProductCategory.dao.find(sql, getId());
		}
		return productCategories;
	}

	/**
	 * 设置经营分类
	 * 
	 * @param productCategories
	 *            经营分类
	 */
	public void setProductCategories(List<ProductCategory> productCategories) {
		this.productCategories = productCategories;
	}

	/**
	 * 获取经营分类申请
	 * 
	 * @return 经营分类申请
	 */
	public List<CategoryApplication> getCategoryApplications() {
		if (CollectionUtils.isEmpty(categoryApplications)) {
			String sql = "SELECT * FROM `category_application` WHERE store_id = ?";
			categoryApplications = CategoryApplication.dao.find(sql, getId());
		}
		return categoryApplications;
	}

	/**
	 * 设置经营分类申请
	 * 
	 * @param categoryApplications
	 *            经营分类申请
	 */
	public void setCategoryApplications(List<CategoryApplication> categoryApplications) {
		this.categoryApplications = categoryApplications;
	}

	/**
	 * 获取店铺商品标签
	 * 
	 * @return 店铺商品标签
	 */
	public List<StoreProductTag> getStoreProductTags() {
		if (CollectionUtils.isEmpty(storeProductTags)) {
			String sql = "SELECT * FROM `store_product_tag` WHERE store_id = ?";
			storeProductTags = StoreProductTag.dao.find(sql, getId());
		}
		return storeProductTags;
	}

	/**
	 * 设置店铺商品标签
	 * 
	 * @param storeProductTags
	 *            店铺商品标签
	 */
	public void setStoreProductTags(List<StoreProductTag> storeProductTags) {
		this.storeProductTags = storeProductTags;
	}

	/**
	 * 获取商品
	 * 
	 * @return 商品
	 */
	public List<Product> getProducts() {
		if (CollectionUtils.isEmpty(products)) {
			String sql = "SELECT * FROM `product` WHERE store_id = ?";
			products = Product.dao.find(sql, getId());
		}
		return products;
	}

	/**
	 * 设置商品
	 * 
	 * @param products
	 *            商品
	 */
	public void setProducts(List<Product> products) {
		this.products = products;
	}

	/**
	 * 获取促销
	 * 
	 * @return 促销
	 */
	public List<Promotion> getPromotions() {
		if (CollectionUtils.isEmpty(promotions)) {
			String sql = "SELECT * FROM `promotion` WHERE store_id = ?";
			promotions = Promotion.dao.find(sql, getId());
		}
		return promotions;
	}

	/**
	 * 设置促销
	 * 
	 * @param promotions
	 *            促销
	 */
	public void setPromotions(List<Promotion> promotions) {
		this.promotions = promotions;
	}

	/**
	 * 获取优惠券
	 * 
	 * @return 优惠券
	 */
	public List<Coupon> getCoupons() {
		if (CollectionUtils.isEmpty(coupons)) {
			String sql = "SELECT * FROM `coupon` WHERE store_id = ?";
			coupons = Coupon.dao.find(sql, getId());
		}
		return coupons;
	}

	/**
	 * 设置优惠券
	 * 
	 * @param coupons
	 *            优惠券
	 */
	public void setCoupons(List<Coupon> coupons) {
		this.coupons = coupons;
	}

	/**
	 * 获取订单
	 * 
	 * @return 订单
	 */
	public List<Order> getOrder() {
		if (CollectionUtils.isEmpty(order)) {
			String sql = "SELECT * FROM `order` WHERE store_id = ?";
			order = Order.dao.find(sql, getId());
		}
		return order;
	}

	/**
	 * 设置订单
	 * 
	 * @param orders
	 *            订单
	 */
	public void setOrder(List<Order> order) {
		this.order = order;
	}

	/**
	 * 获取店铺收藏
	 * 
	 * @return 店铺收藏
	 */
	public List<StoreFavorite> getStoreFavorites() {
		if (CollectionUtils.isEmpty(storeFavorites)) {
			String sql = "SELECT * FROM `store_favorite` WHERE store_id = ?";
			storeFavorites = StoreFavorite.dao.find(sql, getId());
		}
		return storeFavorites;
	}

	/**
	 * 设置店铺收藏
	 * 
	 * @param storeFavorites
	 *            店铺收藏
	 */
	public void setStoreFavorites(List<StoreFavorite> storeFavorites) {
		this.storeFavorites = storeFavorites;
	}

	/**
	 * 获取快递单模板
	 * 
	 * @return 快递单模板
	 */
	public List<DeliveryTemplate> getDeliveryTemplates() {
		if (CollectionUtils.isEmpty(deliveryTemplates)) {
			String sql = "SELECT * FROM `delivery_template` WHERE store_id = ?";
			deliveryTemplates = DeliveryTemplate.dao.find(sql, getId());
		}
		return deliveryTemplates;
	}

	/**
	 * 设置快递单模板
	 * 
	 * @param deliveryTemplates
	 *            快递单模板
	 */
	public void setDeliveryTemplates(List<DeliveryTemplate> deliveryTemplates) {
		this.deliveryTemplates = deliveryTemplates;
	}

	/**
	 * 获取发货点
	 * 
	 * @return 发货点
	 */
	public List<DeliveryCenter> getDeliveryCenters() {
		if (CollectionUtils.isEmpty(deliveryCenters)) {
			String sql = "SELECT * FROM `delivery_center` WHERE store_id = ?";
			deliveryCenters = DeliveryCenter.dao.find(sql, getId());
		}
		return deliveryCenters;
	}

	/**
	 * 设置发货点
	 * 
	 * @param deliveryCenters
	 *            发货点
	 */
	public void setDeliveryCenters(List<DeliveryCenter> deliveryCenters) {
		this.deliveryCenters = deliveryCenters;
	}

	/**
	 * 获取默认运费配置
	 * 
	 * @return 默认运费配置
	 */
	public List<DefaultFreightConfig> getDefaultFreightConfigs() {
		if (CollectionUtils.isEmpty(defaultFreightConfigs)) {
			String sql = "SELECT * FROM `default_freight_config` WHERE store_id = ?";
			defaultFreightConfigs = DefaultFreightConfig.dao.find(sql, getId());
		}
		return defaultFreightConfigs;
	}

	/**
	 * 设置默认运费配置
	 * 
	 * @param defaultFreightConfigs
	 *            默认运费配置
	 */
	public void setDefaultFreightConfigs(List<DefaultFreightConfig> defaultFreightConfigs) {
		this.defaultFreightConfigs = defaultFreightConfigs;
	}

	/**
	 * 获取地区运费配置
	 * 
	 * @return 地区运费配置
	 */
	public List<AreaFreightConfig> getAreaFreightConfigs() {
		if (CollectionUtils.isEmpty(areaFreightConfigs)) {
			String sql = "SELECT * FROM `area_freight_config` WHERE store_id = ?";
			areaFreightConfigs = AreaFreightConfig.dao.find(sql, getId());
		}
		return areaFreightConfigs;
	}

	/**
	 * 设置地区运费配置
	 * 
	 * @param areaFreightConfigs
	 *            地区运费配置
	 */
	public void setAreaFreightConfigs(List<AreaFreightConfig> areaFreightConfigs) {
		this.areaFreightConfigs = areaFreightConfigs;
	}

	/**
	 * 获取服务
	 * 
	 * @return 服务
	 */
	public List<Svc> getSvcs() {
		if (CollectionUtils.isEmpty(svcs)) {
			String sql = "SELECT * FROM `svc` WHERE store_id = ?";
			svcs = Svc.dao.find(sql, getId());
		}
		return svcs;
	}

	/**
	 * 设置服务
	 * 
	 * @param svcs
	 *            服务
	 */
	public void setSvcs(List<Svc> svcs) {
		this.svcs = svcs;
	}

	/**
	 * 获取支付事务
	 * 
	 * @return 支付事务
	 */
	public List<PaymentTransaction> getPaymentTransactions() {
		if (CollectionUtils.isEmpty(paymentTransactions)) {
			String sql = "SELECT * FROM `payment_transaction` WHERE store_id = ?";
			paymentTransactions = PaymentTransaction.dao.find(sql, getId());
		}
		return paymentTransactions;
	}

	/**
	 * 设置支付事务
	 * 
	 * @param paymentTransactions
	 *            支付事务
	 */
	public void setPaymentTransactions(List<PaymentTransaction> paymentTransactions) {
		this.paymentTransactions = paymentTransactions;
	}

	/**
	 * 获取咨询
	 * 
	 * @return 咨询
	 */
	public List<Consultation> getConsultations() {
		if (CollectionUtils.isEmpty(consultations)) {
			String sql = "SELECT * FROM `consultation` WHERE store_id = ?";
			consultations = Consultation.dao.find(sql, getId());
		}
		return consultations;
	}

	/**
	 * 设置咨询
	 * 
	 * @param consultations
	 *            咨询
	 */
	public void setConsultations(List<Consultation> consultations) {
		this.consultations = consultations;
	}

	/**
	 * 获取评论
	 * 
	 * @return 评论
	 */
	public List<Review> getReviews() {
		if (CollectionUtils.isEmpty(reviews)) {
			String sql = "SELECT * FROM `review` WHERE store_id = ?";
			reviews = Review.dao.find(sql, getId());
		}
		return reviews;
	}

	/**
	 * 设置评论
	 * 
	 * @param reviews
	 *            评论
	 */
	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}

	/**
	 * 获取统计
	 * 
	 * @return 统计
	 */
	public List<Statistic> getStatistics() {
		if (CollectionUtils.isEmpty(statistics)) {
			String sql = "SELECT * FROM `statistic` WHERE store_id = ?";
			statistics = Statistic.dao.find(sql, getId());
		}
		return statistics;
	}

	/**
	 * 设置统计
	 * 
	 * @param statistics
	 *            统计
	 */
	public void setStatistics(List<Statistic> statistics) {
		this.statistics = statistics;
	}

	/**
	 * 获取路径
	 * 
	 * @return 路径
	 */
	public String getPath() {
		return String.format(Store.PATH, getId());
	}

	/**
	 * 获取应付保证金
	 * 
	 * @return 应付保证金
	 */
	public BigDecimal getBailPayable() {
		if (Store.Status.approved.equals(getStatusName())) {
			BigDecimal bailPayable = getStoreCategory().getBail().subtract(getBailPaid());
			return bailPayable.compareTo(BigDecimal.ZERO) >= 0 ? bailPayable : BigDecimal.ZERO;
		}
		return BigDecimal.ZERO;
	}

	/**
	 * 判断是否为自营店铺
	 * 
	 * @return 是否为自营店铺
	 */
	public boolean isSelf() {
		return Type.self.equals(getTypeName());
	}

	/**
	 * 判断店铺是否有效
	 * 
	 * @return 店铺是否有效
	 */
	public boolean isActive() {
		return BooleanUtils.isTrue(getIsEnabled()) && Status.success.equals(getStatusName()) && !hasExpired();
	}

	/**
	 * 判断店铺是否已过期
	 * 
	 * @return 店铺是否已过期
	 */
	public boolean hasExpired() {
		return getEndDate() != null && !getEndDate().after(new Date());
	}

	/**
	 * 判断折扣促销是否已过期
	 * 
	 * @return 折扣促销是否已过期
	 */
	public boolean discountPromotionHasExpired() {
		return !Type.self.equals(getTypeName()) && (getDiscountPromotionEndDate() == null || !getDiscountPromotionEndDate().after(new Date()));
	}

	/**
	 * 判断满减促销是否已过期
	 * 
	 * @return 满减促销是否已过期
	 */
	public boolean fullReductionPromotionHasExpired() {
		return !Type.self.equals(getTypeName()) && (getFullReductionPromotionEndDate() == null || !getFullReductionPromotionEndDate().after(new Date()));
	}

	/**
	 * 持久化前处理
	 */
	public void prePersist() {
		setEmail(StringUtils.lowerCase(getEmail()));
		setMobile(StringUtils.lowerCase(getMobile()));
	}

	/**
	 * 更新前处理
	 */
	public void preUpdate() {
		setEmail(StringUtils.lowerCase(getEmail()));
		setMobile(StringUtils.lowerCase(getMobile()));
	}
	
}
