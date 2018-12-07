package com.jfinalshop.model;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.jfinalshop.entity.ParameterValue;
import com.jfinalshop.entity.ProductImage;
import com.jfinalshop.entity.SpecificationItem;
import com.jfinalshop.entity.SpecificationItem.Entry;
import com.jfinalshop.model.base.BaseProduct;

/**
 * Model - 商品
 * 
 */
public class Product extends BaseProduct<Product> {
	private static final long serialVersionUID = -428218912308567347L;
	public static final Product dao = new Product().dao();
	
	/**
	 * 点击数缓存名称
	 */
	public static final String HITS_CACHE_NAME = "productHits";

	/**
	 * 属性值属性个数
	 */
	public static final int ATTRIBUTE_VALUE_PROPERTY_COUNT = 20;

	/**
	 * 属性值属性名称前缀
	 */
	public static final String ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX = "attributeValue";

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
	 * 类型名称
	 */
	public Type getTypeName() {
		return getType() == null ? null : Type.values()[getType()];
	}
	
	
	/**
	 * 店铺
	 */
	private Store store;

	/**
	 * 商品分类
	 */
	private ProductCategory productCategory;

	/**
	 * 店铺商品分类
	 */
	private StoreProductCategory storeProductCategory;

	/**
	 * 品牌
	 */
	private Brand brand;

	/**
	 * 商品图片
	 */
	private List<ProductImage> productImages = new ArrayList<>();

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
	private List<Promotion> promotions = new ArrayList<>();

	/**
	 * 商品标签
	 */
	private List<ProductTag> productTags = new ArrayList<>();

	/**
	 * 店铺商品标签
	 */
	private List<StoreProductTag> storeProductTags = new ArrayList<>();

	/**
	 * 评论
	 */
	private List<Review> reviews = new ArrayList<>();

	/**
	 * 咨询
	 */
	private List<Consultation> consultations = new ArrayList<>();

	/**
	 * 商品收藏
	 */
	private List<ProductFavorite> productFavorites = new ArrayList<>();

	/**
	 * SKU
	 */
	private List<Sku> skus = new ArrayList<>();

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
	 * 设置店铺
	 * 
	 * @param store
	 *            店铺
	 */
	public void setStore(Store store) {
		this.store = store;
	}

	/**
	 * 获取商品分类
	 * 
	 * @return 商品分类
	 */
	public ProductCategory getProductCategory() {
		if (productCategory == null) {
			productCategory = ProductCategory.dao.findById(getProductCategoryId());
		}
		return productCategory;
	}

	/**
	 * 设置商品分类
	 * 
	 * @param productCategory
	 *            商品分类
	 */
	public void setProductCategory(ProductCategory productCategory) {
		this.productCategory = productCategory;
	}

	/**
	 * 获取店铺商品分类
	 * 
	 * @return 店铺商品分类
	 */
	public StoreProductCategory getStoreProductCategory() {
		if (storeProductCategory == null) {
			storeProductCategory = StoreProductCategory.dao.findById(getStoreProductCategoryId());
		}
		return storeProductCategory;
	}

	/**
	 * 设置店铺商品分类
	 * 
	 * @param storeProductCategory
	 *            店铺商品分类
	 */
	public void setStoreProductCategory(StoreProductCategory storeProductCategory) {
		this.storeProductCategory = storeProductCategory;
	}

	/**
	 * 获取品牌
	 * 
	 * @return 品牌
	 */
	public Brand getBrand() {
		if (brand == null) {
			brand = Brand.dao.findById(getBrandId());
		}
		return brand;
	}

	/**
	 * 设置品牌
	 * 
	 * @param brand
	 *            品牌
	 */
	public void setBrand(Brand brand) {
		this.brand = brand;
	}

	/**
	 * 获取商品图片
	 * 
	 * @return 商品图片
	 */
	public List<ProductImage> getProductImagesConverter() {
		if (CollectionUtils.isEmpty(productImages)) {
			JSONArray productImageArrays = JSONArray.parseArray(getProductImages());
			if (CollectionUtils.isNotEmpty(productImageArrays)) {
				for (int i = 0; i < productImageArrays.size(); i++) {
					productImages.add(JSONObject.parseObject(productImageArrays.getString(i), ProductImage.class));
				}
			}
		}
		return productImages;
	}

	/**
	 * 设置商品图片
	 * 
	 * @param productImages
	 *            商品图片
	 */
	public void setProductImages(List<ProductImage> productImages) {
		this.productImages = productImages;
	}

	/**
	 * 获取参数值
	 * 
	 * @return 参数值
	 */
	public List<ParameterValue> getParameterValuesConverter() {
		if (CollectionUtils.isEmpty(parameterValues)) {
			JSONArray parameterValueArrays = JSONArray.parseArray(getParameterValues());
			if (CollectionUtils.isNotEmpty(parameterValueArrays)) {
				for(int i = 0; i < parameterValueArrays.size(); i++) {
					parameterValues.add(JSONObject.parseObject(parameterValueArrays.getString(i), ParameterValue.class));
				}
			}
		}
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
	public List<SpecificationItem> getSpecificationItemsConverter() {
		if (CollectionUtils.isEmpty(specificationItems)) {
			JSONArray specificationItemArrays = JSONArray.parseArray(getSpecificationItems());
			if (CollectionUtils.isNotEmpty(specificationItemArrays)) {
				for(int i = 0; i < specificationItemArrays.size(); i++) {
					specificationItems.add(JSONObject.parseObject(specificationItemArrays.getString(i), SpecificationItem.class));
				}
				
			}
		}
		sortSpecificationItemsByIds(specificationItems);
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
	public List<Promotion> getPromotions() {
		if (CollectionUtils.isEmpty(promotions)) {
			String sql = "SELECT p.*  FROM promotion p LEFT JOIN product_promotion pp ON p.id = pp.`promotions_id` WHERE pp.`products_id` = ?";
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
	 * 获取商品标签
	 * 
	 * @return 商品标签
	 */
	public List<ProductTag> getProductTags() {
		if (CollectionUtils.isEmpty(productTags)) {
			String sql = "SELECT p.* FROM product_tag p LEFT JOIN product_product_tag ppt ON p.id = ppt.`product_tags_id` WHERE ppt.`products_id` = ?";
			productTags = ProductTag.dao.find(sql, getId());
		}
		return productTags;
	}

	/**
	 * 设置商品标签
	 * 
	 * @param productTags
	 *            商品标签
	 */
	public void setProductTags(List<ProductTag> productTags) {
		this.productTags = productTags;
	}

	/**
	 * 获取店铺商品标签
	 * 
	 * @return 店铺商品标签
	 */
	public List<StoreProductTag> getStoreProductTags() {
		if (CollectionUtils.isEmpty(storeProductTags)) {
			String sql = "SELECT p.* FROM store_product_tag p LEFT JOIN product_store_product_tag pspt ON p.id = pspt.`store_product_tags_id` WHERE pspt.`products_id` = ?";
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
	 * 获取评论
	 * 
	 * @return 评论
	 */
	public List<Review> getReviews() {
		if (CollectionUtils.isEmpty(reviews)) {
			String sql = "SELECT * FROM `review` WHERE product_id = ?";
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
	 * 获取咨询
	 * 
	 * @return 咨询
	 */
	public List<Consultation> getConsultations() {
		if (CollectionUtils.isEmpty(consultations)) {
			String sql = "SELECT * FROM `consultation` WHERE product_id = ?";
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
	 * 获取商品收藏
	 * 
	 * @return 商品收藏
	 */
	public List<ProductFavorite> getProductFavorites() {
		if (CollectionUtils.isEmpty(productFavorites)) {
			String sql = "SELECT * FROM `product_favorite` WHERE product_id = ?";
			productFavorites = ProductFavorite.dao.find(sql, getId());
		}
		return productFavorites;
	}

	/**
	 * 设置商品收藏
	 * 
	 * @param productFavorites
	 *            商品收藏
	 */
	public void setProductFavorites(List<ProductFavorite> productFavorites) {
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
		return String.format(Product.PATH, getId());
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
		if (CollectionUtils.isNotEmpty(getSpecificationItemsConverter())) {
			for (SpecificationItem specificationItem : getSpecificationItemsConverter()) {
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
	 * 获取有效促销
	 * 
	 * @return 有效促销
	 */
	@SuppressWarnings("unchecked")
	@JSONField(serialize = false)
	public Set<Promotion> getValidPromotions() {
		if (!Product.Type.general.equals(getTypeName()) || CollectionUtils.isEmpty(getPromotions())) {
			return Collections.emptySet();
		}
		return new HashSet<>(CollectionUtils.select(getPromotions(), new Predicate() {

			public boolean evaluate(Object object) {
				Promotion promotion = (Promotion) object;
				return promotion != null && promotion.getIsEnabled() && promotion.hasBegun() && !promotion.hasEnded() && CollectionUtils.isNotEmpty(promotion.getMemberRanks());
			}
		}));
	}

	/**
	 * 是否存在规格
	 * 
	 * @return 是否存在规格
	 */
	public boolean hasSpecification() {
		return CollectionUtils.isNotEmpty(getSpecificationItemsConverter());
	}

	/**
	 * 判断促销是否有效
	 * 
	 * @param promotion
	 *            促销
	 * @return 促销是否有效
	 */
	public boolean isValid(Promotion promotion) {
		if (!Product.Type.general.equals(getTypeName()) || !promotion.getIsEnabled() || !promotion.hasBegun() || promotion.hasEnded() || CollectionUtils.isEmpty(promotion.getMemberRanks())) {
			return false;
		}
		if (getValidPromotions().contains(promotion)) {
			return true;
		}
		return false;
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
	 * 获取属性值
	 * 
	 * @param attribute
	 *            属性
	 * @return 属性值
	 */
	public String getAttributeValue(Attribute attribute) {
		if (attribute == null || attribute.getPropertyIndex() == null) {
			return null;
		}
		try {
			String propertyName = ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + attribute.getPropertyIndex();
			return (String) PropertyUtils.getProperty(this, propertyName);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 设置属性值
	 * 
	 * @param attribute
	 *            属性
	 * @param attributeValue
	 *            属性值
	 */
	public void setAttributeValue(Attribute attribute, String attributeValue) {
		if (attribute == null || attribute.getPropertyIndex() == null) {
			return;
		}
		try {
			String propertyName = ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + attribute.getPropertyIndex();
			PropertyUtils.setProperty(this, propertyName, attributeValue);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 移除所有属性值
	 */
	public void removeAttributeValue() {
		for (int i = 0; i < ATTRIBUTE_VALUE_PROPERTY_COUNT; i++) {
			String propertyName = ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + i;
			try {
				PropertyUtils.setProperty(this, propertyName, null);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e.getMessage(), e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e.getMessage(), e);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
	}


	/**
	 * 按数据库添加ID 排序
	 * @param specItems
	 */
	private void sortSpecificationItemsByIds(List<SpecificationItem> specificationItems) {
		if (CollectionUtils.isEmpty(specificationItems))
			return;
		for (SpecificationItem items : specificationItems) {
			Collections.sort(items.getEntries(), new Comparator<Entry>() {
				public int compare(Entry obj1, Entry obj2) {
					Entry entry1 = obj1;
					Entry entry2 = obj2;
					return entry1.getId().compareTo(entry2.getId());
				}
			});
		}
	}

	/**
	 * 判断是否为新建对象
	 * 
	 * @return 是否为新建对象
	 */
	public boolean isNew() {
		return getId() == null;
	}
}
