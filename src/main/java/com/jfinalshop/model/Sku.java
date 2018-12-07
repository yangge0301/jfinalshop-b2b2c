package com.jfinalshop.model;

import java.beans.Transient;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinalshop.Setting;
import com.jfinalshop.entity.SpecificationValue;
import com.jfinalshop.model.base.BaseSku;
import com.jfinalshop.util.SystemUtils;

/**
 * Model - SKU
 * 
 */
public class Sku extends BaseSku<Sku> {
	private static final long serialVersionUID = -2705934560266436673L;
	public static final Sku dao = new Sku().dao();
	
	/**
	 * 商品
	 */
	private Product product;

	/**
	 * 规格值
	 */
	private List<SpecificationValue> specificationValues = new ArrayList<>();

	/**
	 * 购物车项
	 */
	private List<CartItem> cartItems = new ArrayList<>();

	/**
	 * 订单项
	 */
	private List<OrderItem> orderItems = new ArrayList<>();

	/**
	 * 订单发货项
	 */
	private List<OrderShippingItem> orderShippingItems = new ArrayList<>();

	/**
	 * 到货通知
	 */
	private List<ProductNotify> productNotifies = new ArrayList<>();

	/**
	 * 库存记录
	 */
	private List<StockLog> stockLogs = new ArrayList<>();

	/**
	 * 赠品促销
	 */
	private List<Promotion> giftPromotions = new ArrayList<>();
	
	/**
	 * 赠品促销
	 */
	private List<SkuBarcode> skuBarcodes = new ArrayList<>();
	
	
	/**
	 * 获取商品
	 * 
	 * @return 商品
	 */
	public Product getProduct() {
		if (product == null) {
			product = Product.dao.findById(getProductId());
		}
		return product;
	}

	/**
	 * 设置商品
	 * 
	 * @param product
	 *            商品
	 */
	public void setProduct(Product product) {
		this.product = product;
	}

	/**
	 * 获取规格值
	 * 
	 * @return 规格值
	 */
	public List<SpecificationValue> getSpecificationValuesConverter() {
		if (CollectionUtils.isEmpty(specificationValues)) {
			JSONArray specificationValueArrays = JSONArray.parseArray(getSpecificationValues());
			if (CollectionUtils.isNotEmpty(specificationValueArrays)) {
				for(int i = 0; i < specificationValueArrays.size(); i++) {
					specificationValues.add(JSONObject.parseObject(specificationValueArrays.getString(i), SpecificationValue.class));
				}
			}
		}
		return specificationValues;
	}

	/**
	 * 设置规格值
	 * 
	 * @param specificationValues
	 *            规格值
	 */
//	public void setSpecificationValues(List<SpecificationValue> specificationValues) {
//		this.specificationValues = specificationValues;
//	}

	/**
	 * 获取购物车项
	 * 
	 * @return 购物车项
	 */
	public List<CartItem> getCartItems() {
		if (CollectionUtils.isEmpty(cartItems)) {
			String sql = "SELECT * FROM `cart_item` WHERE sku_id = ?";
			cartItems = CartItem.dao.find(sql, getId());
		}
		return cartItems;
	}

	/**
	 * 设置购物车项
	 * 
	 * @param cartItems
	 *            购物车项
	 */
	public void setCartItems(List<CartItem> cartItems) {
		this.cartItems = cartItems;
	}

	/**
	 * 获取订单项
	 * 
	 * @return 订单项
	 */
	public List<OrderItem> getOrderItems() {
		if (CollectionUtils.isEmpty(orderItems)) {
			String sql = "SELECT * FROM `order_item` WHERE sku_id = ?";
			orderItems = OrderItem.dao.find(sql, getId());
		}
		return orderItems;
	}

	/**
	 * 设置订单项
	 * 
	 * @param orderItems
	 *            订单项
	 */
	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}

	/**
	 * 获取订单发货项
	 * 
	 * @return 订单发货项
	 */
	public List<OrderShippingItem> getOrderShippingItems() {
		if (CollectionUtils.isEmpty(orderShippingItems)) {
			String sql = "SELECT * FROM `order_shipping_item` WHERE sku_id = ?";
			orderShippingItems = OrderShippingItem.dao.find(sql, getId());
		}
		return orderShippingItems;
	}

	/**
	 * 设置订单发货项
	 * 
	 * @param orderShippingItems
	 *            订单发货项
	 */
	public void setOrderShippingItems(List<OrderShippingItem> orderShippingItems) {
		this.orderShippingItems = orderShippingItems;
	}

	/**
	 * 获取到货通知
	 * 
	 * @return 到货通知
	 */
	public List<ProductNotify> getProductNotifies() {
		if (CollectionUtils.isEmpty(productNotifies)) {
			String sql = "SELECT * FROM `product_notify` WHERE sku_id = ?";
			productNotifies = ProductNotify.dao.find(sql, getId());
		}
		return productNotifies;
	}

	/**
	 * 设置到货通知
	 * 
	 * @param productNotifies
	 *            到货通知
	 */
	public void setProductNotifies(List<ProductNotify> productNotifies) {
		this.productNotifies = productNotifies;
	}

	/**
	 * 获取库存记录
	 * 
	 * @return 库存记录
	 */
	public List<StockLog> getStockLogs() {
		if (CollectionUtils.isEmpty(stockLogs)) {
			String sql = "SELECT * FROM `stock_log` WHERE sku_id = ?";
			stockLogs = StockLog.dao.find(sql, getId());
		}
		return stockLogs;
	}

	/**
	 * 设置库存记录
	 * 
	 * @param stockLogs
	 *            库存记录
	 */
	public void setStockLogs(List<StockLog> stockLogs) {
		this.stockLogs = stockLogs;
	}

	/**
	 * 获取赠品促销
	 * 
	 * @return 赠品促销
	 */
	public List<Promotion> getGiftPromotions() {
		if (CollectionUtils.isEmpty(giftPromotions)) {
			String sql = "SELECT p.*  FROM promotion p LEFT JOIN promotion_sku ps ON p.id = ps.`gift_promotions_id` WHERE ps.`gifts_id` = ?";
			giftPromotions = Promotion.dao.find(sql, getId());
		}
		return giftPromotions;
	}

	/**
	 * 设置赠品促销
	 * 
	 * @param giftPromotions
	 *            赠品促销
	 */
	public void setGiftPromotions(List<Promotion> giftPromotions) {
		this.giftPromotions = giftPromotions;
	}

	/**
	 * 获取条码
	 * 
	 * @return 条码
	 */
	public List<SkuBarcode> getSkuBarcodes() {
		if (CollectionUtils.isEmpty(skuBarcodes)) {
			String sql = "SELECT * FROM sku_barcode WHERE sku_id = ?";
			skuBarcodes = SkuBarcode.dao.find(sql, getId());
		}
		return skuBarcodes;
	}

	/**
	 * 设置条码
	 * 
	 * @param skuBarcode
	 *            条码
	 */
	public void setSkuBarcodes(List<SkuBarcode> skuBarcodes) {
		this.skuBarcodes = skuBarcodes;
	}

	/**
	 * 获取名称
	 * 
	 * @return 名称
	 */
	public String getName() {
		return getProduct() != null ? getProduct().getName() : null;
	}

	/**
	 * 获取类型
	 * 
	 * @return 类型
	 */
	public Product.Type getType() {
		return getProduct() != null ? getProduct().getTypeName() : null;
	}

	/**
	 * 获取展示图片
	 * 
	 * @return 展示图片
	 */
	public String getImage() {
		return getProduct() != null ? getProduct().getImage() : null;
	}

	/**
	 * 获取单位
	 * 
	 * @return 单位
	 */
	public String getUnit() {
		return getProduct() != null ? getProduct().getUnit() : null;
	}

	/**
	 * 获取重量
	 * 
	 * @return 重量
	 */
	public Integer getWeight() {
		return getProduct() != null ? getProduct().getWeight() : null;
	}

	/**
	 * 获取是否有效
	 * 
	 * @return 是否有效
	 */
	public boolean getIsActive() {
		return getProduct() != null && getProduct().getIsActive();
	}

	/**
	 * 获取是否上架
	 * 
	 * @return 是否上架
	 */
	public boolean getIsMarketable() {
		return getProduct() != null && BooleanUtils.isTrue(getProduct().getIsMarketable());
	}

	/**
	 * 获取是否列出
	 * 
	 * @return 是否列出
	 */
	public boolean getIsList() {
		return getProduct() != null && BooleanUtils.isTrue(getProduct().getIsList());
	}

	/**
	 * 获取是否置顶
	 * 
	 * @return 是否置顶
	 */
	public boolean getIsTop() {
		return getProduct() != null && BooleanUtils.isTrue(getProduct().getIsTop());
	}

	/**
	 * 获取是否需要物流
	 * 
	 * @return 是否需要物流
	 */
	public boolean getIsDelivery() {
		return getProduct() != null && BooleanUtils.isTrue(getProduct().getIsDelivery());
	}

	/**
	 * 获取路径
	 * 
	 * @return 路径
	 */
	public String getPath() {
		return getProduct() != null ? getProduct().getPath() : null;
	}

	/**
	 * 获取缩略图
	 * 
	 * @return 缩略图
	 */
	public String getThumbnail() {
		return getProduct() != null ? getProduct().getThumbnail() : null;
	}

	/**
	 * 获取店铺
	 * 
	 * @return 店铺
	 */
	public com.jfinalshop.model.Store getStore() {
		return getProduct() != null ? getProduct().getStore() : null;
	}

	/**
	 * 获取可用库存
	 * 
	 * @return 可用库存
	 */
	public int getAvailableStock() {
		int availableStock = getStock() - getAllocatedStock();
		return availableStock >= 0 ? availableStock : 0;
	}

	/**
	 * 获取是否库存警告
	 * 
	 * @return 是否库存警告
	 */
	public boolean getIsStockAlert() {
		Setting setting = SystemUtils.getSetting();
		return setting.getStockAlertCount() != null && getAvailableStock() <= setting.getStockAlertCount();
	}

	/**
	 * 获取是否缺货
	 * 
	 * @return 是否缺货
	 */
	public boolean getIsOutOfStock() {
		return getAvailableStock() <= 0;
	}

	/**
	 * 获取规格值ID
	 * 
	 * @return 规格值ID
	 */
	public List<Integer> getSpecificationValueIds() {
		List<Integer> specificationValueIds = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(getSpecificationValuesConverter())) {
			for (SpecificationValue specificationValue : getSpecificationValuesConverter()) {
				specificationValueIds.add(specificationValue.getId());
			}
		}
		return specificationValueIds;
	}

	/**
	 * 获取规格
	 * 
	 * @return 规格
	 */
	public List<String> getSpecifications() {
		List<String> specifications = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(getSpecificationValuesConverter())) {
			for (SpecificationValue specificationValue : getSpecificationValuesConverter()) {
				specifications.add(specificationValue.getValue());
			}
		}
		return specifications;
	}

	/**
	 * 获取有效促销
	 * 
	 * @return 有效促销
	 */
	@Transient
	public Set<Promotion> getValidPromotions() {
		return getProduct() != null ? getProduct().getValidPromotions() : Collections.<Promotion>emptySet();
	}

	/**
	 * 是否存在规格
	 * 
	 * @return 是否存在规格
	 */
	@Transient
	public boolean hasSpecification() {
		return CollectionUtils.isNotEmpty(getSpecificationValuesConverter());
	}

	/**
	 * 判断促销是否有效
	 * 
	 * @param promotion
	 *            促销
	 * @return 促销是否有效
	 */
	@Transient
	public boolean isValid(Promotion promotion) {
		return getProduct() != null ? getProduct().isValid(promotion) : false;
	}

	/**
	 * 持久化前处理
	 */
	public void prePersist() {
		setSn(StringUtils.lowerCase(getSn()));
	}

	/**
	 * 获取佣金
	 * 
	 * @param type
	 *            类型
	 * @return 佣金
	 */
	public BigDecimal getCommission(com.jfinalshop.model.Store.Type type) {
		BigDecimal commission = BigDecimal.ZERO;
		if (type != null && getProduct() != null && getProduct().getProductCategory() != null) {
			ProductCategory productCategory = getProduct().getProductCategory();
			if (com.jfinalshop.model.Store.Type.general.equals(type) && productCategory.getGeneralRate() > 0) {
				commission = getPrice().multiply(new BigDecimal(productCategory.getGeneralRate().toString()));
			} else if (com.jfinalshop.model.Store.Type.self.equals(type) && productCategory.getSelfRate() > 0) {
				commission = getPrice().multiply(new BigDecimal(productCategory.getSelfRate().toString()));
			}
		}
		return commission;
	}

	/**
	 * 删除前处理
	 */
	public void preRemove() {
		List<OrderItem> orderItems = getOrderItems();
		if (orderItems != null) {
			for (OrderItem orderItem : orderItems) {
				orderItem.setSku(null);
			}
		}
		List<OrderShippingItem> orderShippingItems = getOrderShippingItems();
		if (orderShippingItems != null) {
			for (OrderShippingItem orderShippingItem : getOrderShippingItems()) {
				orderShippingItem.setSku(null);
			}
		}
		List<Promotion> giftPromotions = getGiftPromotions();
		if (giftPromotions != null) {
			for (Promotion giftPromotion : giftPromotions) {
				giftPromotion.getGifts().remove(this);
			}
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

	/**
	 * 重写equals方法
	 * 
	 * @param obj
	 *            对象
	 * @return 是否相等
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (!Sku.class.isAssignableFrom(obj.getClass())) {
			return false;
		}
		Sku other = (Sku) obj;
		return getId() != null ? getId().equals(other.getId()) : false;
	}

	/**
	 * 重写hashCode方法
	 * 
	 * @return HashCode
	 */
	@Override
	public int hashCode() {
		int hashCode = 17;
		hashCode += getId() != null ? getId().hashCode() * 31 : 0;
		return hashCode;
	}
	
}
