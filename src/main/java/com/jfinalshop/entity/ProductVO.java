package com.jfinalshop.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinalshop.model.Consultation;
import com.jfinalshop.model.ProductFavorite;
import com.jfinalshop.model.ProductTag;
import com.jfinalshop.model.Promotion;
import com.jfinalshop.model.Review;
import com.jfinalshop.model.Sku;
import com.jfinalshop.model.Store;
import com.jfinalshop.model.StoreProductTag;

/**
 * Entity - 商品
 * 
 */
public class ProductVO {

	/**
	 * 路径
	 */
	private static final String PATH = "/product/detail/%d";
	
	/**
	 * 类型
	 */
	public enum Type {

		/**
		 * 普通商品
		 */
		general,

		/**
		 * 兑换商品
		 */
		exchange,

		/**
		 * 赠品
		 */
		gift
	}

	/**
	 * 排序类型
	 */
	public enum OrderType {

		/**
		 * 置顶降序
		 */
		topDesc,

		/**
		 * 价格升序
		 */
		priceAsc,

		/**
		 * 价格降序
		 */
		priceDesc,

		/**
		 * 销量降序
		 */
		salesDesc,

		/**
		 * 评分降序
		 */
		scoreDesc,

		/**
		 * 日期降序
		 */
		dateDesc
	}

	/**
	 * 排名类型
	 */
	public enum RankingType {

		/**
		 * 评分
		 */
		score,

		/**
		 * 评分数
		 */
		scoreCount,

		/**
		 * 周点击数
		 */
		weekHits,

		/**
		 * 月点击数
		 */
		monthHits,

		/**
		 * 点击数
		 */
		hits,

		/**
		 * 周销量
		 */
		weekSales,

		/**
		 * 月销量
		 */
		monthSales,

		/**
		 * 销量
		 */
		sales
	}

	/**
	 * ID
	 */
	private Long id;
	
	/**
	 * 创建日期
	 */
	private Date createdDate;

	/**
	 * 最后修改日期
	 */
	private Date lastModifiedDate;

	/**
	 * 版本
	 */
	private Long version;
	
	/**
	 * 编号
	 */
	private String sn;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 副标题
	 */
	private String caption;

	/**
	 * 类型
	 */
	private Integer type;

	/**
	 * 销售价
	 */
	private Double price;

	/**
	 * 成本价
	 */
	private Double cost;

	/**
	 * 市场价
	 */
	private Double marketPrice;

	/**
	 * 展示图片
	 */
	private String image;

	/**
	 * 单位
	 */
	private String unit;

	/**
	 * 重量
	 */
	private Integer weight;

	/**
	 * 是否上架
	 */
	private Boolean isMarketable;

	/**
	 * 是否列出
	 */
	private Boolean isList;

	/**
	 * 是否置顶
	 */
	private Boolean isTop;

	/**
	 * 是否需要物流
	 */
	private Boolean isDelivery;

	/**
	 * 是否有效
	 */
	private Boolean isActive;

	/**
	 * 备注
	 */
	private String memo;

	/**
	 * 搜索关键词
	 */
	private String keyword;

	/**
	 * 评分
	 */
	private Float score;

	/**
	 * 总评分
	 */
	private Long totalScore;

	/**
	 * 评分数
	 */
	private Long scoreCount;

	/**
	 * 周点击数
	 */
	private Long weekHits;

	/**
	 * 月点击数
	 */
	private Long monthHits;

	/**
	 * 点击数
	 */
	private Long hits;

	/**
	 * 周销量
	 */
	private Long weekSales;

	/**
	 * 月销量
	 */
	private Long monthSales;

	/**
	 * 销量
	 */
	private Long sales;

	/**
	 * 周点击数更新日期
	 */
	private Date weekHitsDate;

	/**
	 * 月点击数更新日期
	 */
	private Date monthHitsDate;

	/**
	 * 周销量更新日期
	 */
	private Date weekSalesDate;

	/**
	 * 月销量更新日期
	 */
	private Date monthSalesDate;

	/**
	 * 店铺
	 */
	private Long storeId;

	/**
	 * 商品分类
	 */
	private Long productCategoryId;

	/**
	 * 店铺商品分类
	 */
	private Long storeProductCategoryId;

	/**
	 * 品牌
	 */
	private Long brandId;
	
	/**
	 * 品牌
	 */
	private String brand;

	/**
	 * 商品图片
	 */
	private String productImages;
	
	/**
	 * 店铺
	 */
	private Store store;
	
	/**
	 * 商品图片
	 */
	private List<ProductImage> pProductImages = new ArrayList<>();

	/**
	 * 参数值
	 */
	private List<ParameterValue> parameterValues = new ArrayList<>();

	/**
	 * 规格项
	 */
	private List<SpecificationItem> specificationItems = new ArrayList<>();

	/**
	 * 促销
	 */
	private Set<Promotion> promotions = new HashSet<>();

	/**
	 * 商品标签
	 */
	private Set<ProductTag> productTags = new HashSet<>();

	/**
	 * 店铺商品标签
	 */
	private Set<StoreProductTag> storeProductTags = new HashSet<>();

	/**
	 * 评论
	 */
	private Set<Review> reviews = new HashSet<>();

	/**
	 * 咨询
	 */
	private Set<Consultation> consultations = new HashSet<>();

	/**
	 * 商品收藏
	 */
	private Set<ProductFavorite> productFavorites = new HashSet<>();

	/**
	 * SKU
	 */
	private List<Sku> skus = new ArrayList<>();

	/**
	 * 类型名称
	 */
	public Type getTypeName() {
		return getType() == null ? null : Type.values()[getType()];
	}
	
	/**
	 * 获取ID
	 * 
	 * @return ID
	 */
	public Long getId() {
		return id;
	}

	/**
	 * 设置ID
	 * 
	 * @param id
	 *            ID
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 获取创建日期
	 * 
	 * @return 创建日期
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * 设置创建日期
	 * 
	 * @param createdDate
	 *            创建日期
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * 获取最后修改日期
	 * 
	 * @return 最后修改日期
	 */
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	/**
	 * 设置最后修改日期
	 * 
	 * @param lastModifiedDate
	 *            最后修改日期
	 */
	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	/**
	 * 获取版本
	 * 
	 * @return 版本
	 */
	public Long getVersion() {
		return version;
	}

	/**
	 * 设置版本
	 * 
	 * @param version
	 *            版本
	 */
	public void setVersion(Long version) {
		this.version = version;
	}
	
	/**
	 * 获取编号
	 * 
	 * @return 编号
	 */
	public String getSn() {
		return sn;
	}

	/**
	 * 设置编号
	 * 
	 * @param sn
	 *            编号
	 */
	public void setSn(String sn) {
		this.sn = sn;
	}

	/**
	 * 获取名称
	 * 
	 * @return 名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置名称
	 * 
	 * @param name
	 *            名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取副标题
	 * 
	 * @return 副标题
	 */
	public String getCaption() {
		return caption;
	}

	/**
	 * 设置副标题
	 * 
	 * @param caption
	 *            副标题
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}

	/**
	 * 获取类型
	 * 
	 * @return 类型
	 */
	public Integer getType() {
		return type;
	}

	/**
	 * 设置类型
	 * 
	 * @param type
	 *            类型
	 */
	public void setType(Integer type) {
		this.type = type;
	}

	/**
	 * 获取销售价
	 * 
	 * @return 销售价
	 */
	public Double getPrice() {
		return price;
	}

	/**
	 * 设置销售价
	 * 
	 * @param price
	 *            销售价
	 */
	public void setPrice(Double price) {
		this.price = price;
	}

	/**
	 * 获取成本价
	 * 
	 * @return 成本价
	 */
	public Double getCost() {
		return cost;
	}

	/**
	 * 设置成本价
	 * 
	 * @param cost
	 *            成本价
	 */
	public void setCost(Double cost) {
		this.cost = cost;
	}

	/**
	 * 获取市场价
	 * 
	 * @return 市场价
	 */
	public Double getMarketPrice() {
		return marketPrice;
	}

	/**
	 * 设置市场价
	 * 
	 * @param marketPrice
	 *            市场价
	 */
	public void setMarketPrice(Double marketPrice) {
		this.marketPrice = marketPrice;
	}

	/**
	 * 获取展示图片
	 * 
	 * @return 展示图片
	 */
	public String getImage() {
		return image;
	}

	/**
	 * 设置展示图片
	 * 
	 * @param image
	 *            展示图片
	 */
	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * 获取单位
	 * 
	 * @return 单位
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * 设置单位
	 * 
	 * @param unit
	 *            单位
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}

	/**
	 * 获取重量
	 * 
	 * @return 重量
	 */
	public Integer getWeight() {
		return weight;
	}

	/**
	 * 设置重量
	 * 
	 * @param weight
	 *            重量
	 */
	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	/**
	 * 获取是否上架
	 * 
	 * @return 是否上架
	 */
	public Boolean getIsMarketable() {
		return isMarketable;
	}

	/**
	 * 设置是否上架
	 * 
	 * @param isMarketable
	 *            是否上架
	 */
	public void setIsMarketable(Boolean isMarketable) {
		this.isMarketable = isMarketable;
	}

	/**
	 * 获取是否列出
	 * 
	 * @return 是否列出
	 */
	public Boolean getIsList() {
		return isList;
	}

	/**
	 * 设置是否列出
	 * 
	 * @param isList
	 *            是否列出
	 */
	public void setIsList(Boolean isList) {
		this.isList = isList;
	}

	/**
	 * 获取是否置顶
	 * 
	 * @return 是否置顶
	 */
	public Boolean getIsTop() {
		return isTop;
	}

	/**
	 * 设置是否置顶
	 * 
	 * @param isTop
	 *            是否置顶
	 */
	public void setIsTop(Boolean isTop) {
		this.isTop = isTop;
	}

	/**
	 * 获取是否需要物流
	 * 
	 * @return 是否需要物流
	 */
	public Boolean getIsDelivery() {
		return isDelivery;
	}

	/**
	 * 设置是否需要物流
	 * 
	 * @param isDelivery
	 *            是否需要物流
	 */
	public void setIsDelivery(Boolean isDelivery) {
		this.isDelivery = isDelivery;
	}

	/**
	 * 获取是否有效
	 * 
	 * @return 是否有效
	 */
	public Boolean getIsActive() {
		return isActive;
	}

	/**
	 * 设置是否有效
	 * 
	 * @param isActive
	 *            是否有效
	 */
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * 获取备注
	 * 
	 * @return 备注
	 */
	public String getMemo() {
		return memo;
	}

	/**
	 * 设置备注
	 * 
	 * @param memo
	 *            备注
	 */
	public void setMemo(String memo) {
		this.memo = memo;
	}

	/**
	 * 获取搜索关键词
	 * 
	 * @return 搜索关键词
	 */
	public String getKeyword() {
		return keyword;
	}

	/**
	 * 设置搜索关键词
	 * 
	 * @param keyword
	 *            搜索关键词
	 */
	public void setKeyword(String keyword) {
		if (keyword != null) {
			keyword = keyword.replaceAll("[,\\s]*,[,\\s]*", ",").replaceAll("^,|,$", "");
		}
		this.keyword = keyword;
	}

	/**
	 * 获取评分
	 * 
	 * @return 评分
	 */
	public Float getScore() {
		return score;
	}

	/**
	 * 设置评分
	 * 
	 * @param score
	 *            评分
	 */
	public void setScore(Float score) {
		this.score = score;
	}

	/**
	 * 获取总评分
	 * 
	 * @return 总评分
	 */
	public Long getTotalScore() {
		return totalScore;
	}

	/**
	 * 设置总评分
	 * 
	 * @param totalScore
	 *            总评分
	 */
	public void setTotalScore(Long totalScore) {
		this.totalScore = totalScore;
	}

	/**
	 * 获取评分数
	 * 
	 * @return 评分数
	 */
	public Long getScoreCount() {
		return scoreCount;
	}

	/**
	 * 设置评分数
	 * 
	 * @param scoreCount
	 *            评分数
	 */
	public void setScoreCount(Long scoreCount) {
		this.scoreCount = scoreCount;
	}

	/**
	 * 获取周点击数
	 * 
	 * @return 周点击数
	 */
	public Long getWeekHits() {
		return weekHits;
	}

	/**
	 * 设置周点击数
	 * 
	 * @param weekHits
	 *            周点击数
	 */
	public void setWeekHits(Long weekHits) {
		this.weekHits = weekHits;
	}

	/**
	 * 获取月点击数
	 * 
	 * @return 月点击数
	 */
	public Long getMonthHits() {
		return monthHits;
	}

	/**
	 * 设置月点击数
	 * 
	 * @param monthHits
	 *            月点击数
	 */
	public void setMonthHits(Long monthHits) {
		this.monthHits = monthHits;
	}

	/**
	 * 获取点击数
	 * 
	 * @return 点击数
	 */
	public Long getHits() {
		return hits;
	}

	/**
	 * 设置点击数
	 * 
	 * @param hits
	 *            点击数
	 */
	public void setHits(Long hits) {
		this.hits = hits;
	}

	/**
	 * 获取周销量
	 * 
	 * @return 周销量
	 */
	public Long getWeekSales() {
		return weekSales;
	}

	/**
	 * 设置周销量
	 * 
	 * @param weekSales
	 *            周销量
	 */
	public void setWeekSales(Long weekSales) {
		this.weekSales = weekSales;
	}

	/**
	 * 获取月销量
	 * 
	 * @return 月销量
	 */
	public Long getMonthSales() {
		return monthSales;
	}

	/**
	 * 设置月销量
	 * 
	 * @param monthSales
	 *            月销量
	 */
	public void setMonthSales(Long monthSales) {
		this.monthSales = monthSales;
	}

	/**
	 * 获取销量
	 * 
	 * @return 销量
	 */
	public Long getSales() {
		return sales;
	}

	/**
	 * 设置销量
	 * 
	 * @param sales
	 *            销量
	 */
	public void setSales(Long sales) {
		this.sales = sales;
	}

	/**
	 * 获取周点击数更新日期
	 * 
	 * @return 周点击数更新日期
	 */
	public Date getWeekHitsDate() {
		return weekHitsDate;
	}

	/**
	 * 设置周点击数更新日期
	 * 
	 * @param weekHitsDate
	 *            周点击数更新日期
	 */
	public void setWeekHitsDate(Date weekHitsDate) {
		this.weekHitsDate = weekHitsDate;
	}

	/**
	 * 获取月点击数更新日期
	 * 
	 * @return 月点击数更新日期
	 */
	public Date getMonthHitsDate() {
		return monthHitsDate;
	}

	/**
	 * 设置月点击数更新日期
	 * 
	 * @param monthHitsDate
	 *            月点击数更新日期
	 */
	public void setMonthHitsDate(Date monthHitsDate) {
		this.monthHitsDate = monthHitsDate;
	}

	/**
	 * 获取周销量更新日期
	 * 
	 * @return 周销量更新日期
	 */
	public Date getWeekSalesDate() {
		return weekSalesDate;
	}

	/**
	 * 设置周销量更新日期
	 * 
	 * @param weekSalesDate
	 *            周销量更新日期
	 */
	public void setWeekSalesDate(Date weekSalesDate) {
		this.weekSalesDate = weekSalesDate;
	}

	/**
	 * 获取月销量更新日期
	 * 
	 * @return 月销量更新日期
	 */
	public Date getMonthSalesDate() {
		return monthSalesDate;
	}

	/**
	 * 设置月销量更新日期
	 * 
	 * @param monthSalesDate
	 *            月销量更新日期
	 */
	public void setMonthSalesDate(Date monthSalesDate) {
		this.monthSalesDate = monthSalesDate;
	}

	/**
	 * 获取店铺
	 * 
	 * @return 店铺
	 */
	public Long getStoreId() {
		return storeId;
	}

	/**
	 * 设置店铺
	 * 
	 * @param store
	 *            店铺
	 */
	public void setStoreId(Long storeId) {
		this.storeId = storeId;
	}

	/**
	 * 获取店铺
	 * 
	 * @return 店铺
	 */
	public Store getStore() {
		if (store == null) {
			store = Store.dao.findById(getStoreId());
		}
		return store;
	}
	
	/**
	 * 获取商品分类
	 * 
	 * @return 商品分类
	 */
	public Long getProductCategoryId() {
		return productCategoryId;
	}

	/**
	 * 设置商品分类
	 * 
	 * @param productCategory
	 *            商品分类
	 */
	public void setProductCategoryId(Long productCategoryId) {
		this.productCategoryId = productCategoryId;
	}

	/**
	 * 获取店铺商品分类
	 * 
	 * @return 店铺商品分类
	 */
	public Long getStoreProductCategoryId() {
		return storeProductCategoryId;
	}

	/**
	 * 设置店铺商品分类
	 * 
	 * @param storeProductCategory
	 *            店铺商品分类
	 */
	public void setStoreProductCategoryId(Long storeProductCategoryId) {
		this.storeProductCategoryId = storeProductCategoryId;
	}

	/**
	 * 获取品牌
	 * 
	 * @return 品牌
	 */
	public Long getBrandId() {
		return brandId;
	}

	/**
	 * 设置品牌
	 * 
	 * @param brand
	 *            品牌
	 */
	public void setBrandId(Long brandId) {
		this.brandId = brandId;
	}
	
	/**
	 * 获取品牌
	 * 
	 * @return 品牌
	 */
	public String getBrand() {
		return brand;
	}

	/**
	 * 设置品牌
	 * 
	 * @param brand
	 *            品牌
	 */
	public void setBrand(String brand) {
		this.brand = brand;
	}

	/**
	 * 获取商品图片
	 * 
	 * @return 商品图片
	 */
	public String getProductImages() {
		return productImages;
	}

	/**
	 * 设置商品图片
	 * 
	 * @param productImages
	 *            商品图片
	 */
	public void setProductImages(String productImages) {
		this.productImages = productImages;
	}

	/**
	 * 获取参数值
	 * 
	 * @return 参数值
	 */
	public List<ParameterValue> getParameterValues() {
		return parameterValues;
	}

	/**
	 * 设置参数值
	 * 
	 * @param parameterValues
	 *            参数值
	 */
	public void setParameterValues(List<ParameterValue> parameterValues) {
		this.parameterValues = parameterValues;
	}

	/**
	 * 获取规格项
	 * 
	 * @return 规格项
	 */
	public List<SpecificationItem> getSpecificationItems() {
		return specificationItems;
	}

	/**
	 * 设置规格项
	 * 
	 * @param specificationItems
	 *            规格项
	 */
	public void setSpecificationItems(List<SpecificationItem> specificationItems) {
		this.specificationItems = specificationItems;
	}

	/**
	 * 获取促销
	 * 
	 * @return 促销
	 */
	public Set<Promotion> getPromotions() {
		return promotions;
	}

	/**
	 * 设置促销
	 * 
	 * @param promotions
	 *            促销
	 */
	
	public void setPromotions(Set<Promotion> promotions) {
		this.promotions = promotions;
	}

	/**
	 * 获取商品标签
	 * 
	 * @return 商品标签
	 */
	
	public Set<ProductTag> getProductTags() {
		return productTags;
	}

	/**
	 * 设置商品标签
	 * 
	 * @param productTags
	 *            商品标签
	 */
	
	public void setProductTags(Set<ProductTag> productTags) {
		this.productTags = productTags;
	}

	/**
	 * 获取店铺商品标签
	 * 
	 * @return 店铺商品标签
	 */
	
	public Set<StoreProductTag> getStoreProductTags() {
		return storeProductTags;
	}

	/**
	 * 设置店铺商品标签
	 * 
	 * @param storeProductTags
	 *            店铺商品标签
	 */
	
	public void setStoreProductTags(Set<StoreProductTag> storeProductTags) {
		this.storeProductTags = storeProductTags;
	}

	/**
	 * 获取评论
	 * 
	 * @return 评论
	 */
	public Set<Review> getReviews() {
		return reviews;
	}

	/**
	 * 设置评论
	 * 
	 * @param reviews
	 *            评论
	 */
	public void setReviews(Set<Review> reviews) {
		this.reviews = reviews;
	}

	/**
	 * 获取咨询
	 * 
	 * @return 咨询
	 */
	public Set<Consultation> getConsultations() {
		return consultations;
	}

	/**
	 * 设置咨询
	 * 
	 * @param consultations
	 *            咨询
	 */
	public void setConsultations(Set<Consultation> consultations) {
		this.consultations = consultations;
	}

	/**
	 * 获取商品收藏
	 * 
	 * @return 商品收藏
	 */
	public Set<ProductFavorite> getProductFavorites() {
		return productFavorites;
	}

	/**
	 * 设置商品收藏
	 * 
	 * @param productFavorites
	 *            商品收藏
	 */
	public void setProductFavorites(Set<ProductFavorite> productFavorites) {
		this.productFavorites = productFavorites;
	}

	/**
	 * 获取SKU
	 * 
	 * @return SKU
	 */
	public List<Sku> getSkus() {
		if (CollectionUtils.isEmpty(skus)) {
			String sql = "SELECT * FROM `sku` WHERE product_id = ?";
			skus = Sku.dao.find(sql, getId());
		}
		return skus;
	}

	/**
	 * 设置SKU
	 * 
	 * @param skus
	 *            SKU
	 */
	public void setSkus(List<Sku> skus) {
		this.skus = skus;
	}

	/**
	 * 获取路径
	 * 
	 * @return 路径
	 */
	public String getPath() {
		return String.format(ProductVO.PATH, getId());
	}
	
	/**
	 * 获取缩略图
	 * 
	 * @return 缩略图
	 */
	public String getThumbnail() {
		if (CollectionUtils.isEmpty(getProductImagesConverter())) {
			return null;
		}
		return getProductImagesConverter().get(0).getThumbnail();
	}

	/**
	 * 获取是否库存警告
	 * 
	 * @return 是否库存警告
	 */
	public boolean getIsStockAlert() {
		return CollectionUtils.exists(getSkus(), new Predicate() {

			public boolean evaluate(Object object) {
				Sku sku = (Sku) object;
				return sku != null && sku.getIsStockAlert();
			}
		});
	}

	/**
	 * 获取是否缺货
	 * 
	 * @return 是否缺货
	 */
	public boolean getIsOutOfStock() {
		return CollectionUtils.exists(getSkus(), new Predicate() {

			public boolean evaluate(Object object) {
				Sku sku = (Sku) object;
				return sku != null && sku.getIsOutOfStock();
			}
		});
	}

	/**
	 * 获取规格项条目ID
	 * 
	 * @return 规格项条目ID
	 */
	public List<Integer> getSpecificationItemEntryIds() {
		List<Integer> specificationItemEntryIds = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(getSpecificationItems())) {
			for (SpecificationItem specificationItem : getSpecificationItems()) {
				if (CollectionUtils.isNotEmpty(specificationItem.getEntries())) {
					for (SpecificationItem.Entry entry : specificationItem.getEntries()) {
						specificationItemEntryIds.add(entry.getId());
					}
				}
			}
			Collections.sort(specificationItemEntryIds);
		}
		return specificationItemEntryIds;
	}

	/**
	 * 获取默认SKU
	 * 
	 * @return 默认SKU
	 */
	public Sku getDefaultSku() {
		return (Sku) CollectionUtils.find(getSkus(), new Predicate() {

			public boolean evaluate(Object object) {
				Sku sku = (Sku) object;
				return sku != null && sku.getIsDefault();
			}
		});
	}

	/**
	 * 获取赠送积分
	 * 
	 * @return 赠送积分
	 */
	public Long getRewardPoint() {
		Sku defaultSku = getDefaultSku();
		return defaultSku != null ? defaultSku.getRewardPoint() : null;
	}

	/**
	 * 获取兑换积分
	 * 
	 * @return 兑换积分
	 */
	public Long getExchangePoint() {
		Sku defaultSku = getDefaultSku();
		return defaultSku != null ? defaultSku.getExchangePoint() : null;
	}

	/**
	 * 是否存在规格
	 * 
	 * @return 是否存在规格
	 */
	public boolean hasSpecification() {
		return CollectionUtils.isNotEmpty(getSpecificationItems());
	}

	/**
	 * 判断是否免运费
	 * 
	 * @return 是否免运费
	 */
	public boolean isFreeShipping() {
		return CollectionUtils.exists(getPromotions(), new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				Promotion promotion = (Promotion) object;
				return promotion != null && promotion.getIsFreeShipping();
			}
		});
	}
	
	/**
	 * 获取商品图片
	 * 
	 * @return 商品图片
	 */
	public List<ProductImage> getProductImagesConverter() {
		if (CollectionUtils.isEmpty(pProductImages)) {
			JSONArray productImageArrays = JSONArray.parseArray(getProductImages());
			if (CollectionUtils.isNotEmpty(productImageArrays)) {
				for (int i = 0; i < productImageArrays.size(); i++) {
					pProductImages.add(JSONObject.parseObject(productImageArrays.getString(i), ProductImage.class));
				}
			}
		}
		return pProductImages;
	}

}