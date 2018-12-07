package com.jfinalshop.dao;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.Pageable;
import com.jfinalshop.Setting;
import com.jfinalshop.model.*;
import com.jfinalshop.util.Assert;
import com.jfinalshop.util.SystemUtils;
import com.xiaoleilu.hutool.date.DateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Dao - 商品
 * 
 */
public class ProductDao extends BaseDao<Product> {

	/**
	 * 构造方法
	 */
	public ProductDao() {
		super(Product.class);
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
		String sql = "SELECT * FROM `product` p WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		
		if (type != null) {
			sql += " AND type = ?";
			params.add(type.ordinal());
		}
		if (store != null) {
			sql += " AND store_id = ?";
			params.add(store.getId());
		}
		if (productCategory != null) {
			sql += " AND (product_category_id IN(SELECT id FROM product_category WHERE id = ? OR tree_path LIKE ?))";
			params.add(productCategory.getId());
			params.add("%" + ProductCategory.TREE_PATH_SEPARATOR + productCategory.getId() + ProductCategory.TREE_PATH_SEPARATOR + "%");
		}
		if (storeProductCategory != null) {
			sql += " AND (store_product_category_id IN (SELECT id FROM store_product_category WHERE id = ? OR tree_path ?))";
			params.add(storeProductCategory.getId());
			params.add("%" + StoreProductCategory.TREE_PATH_SEPARATOR + storeProductCategory.getId() + StoreProductCategory.TREE_PATH_SEPARATOR + "%");
		}
		if (brand != null) {
			sql += " AND brand_id = ?";
			params.add(brand.getId());
		}
		if (promotion != null) {
			sql += " AND id IN (SELECT pp.`products_id` FROM product_promotion pp WHERE pp.`promotions_id` = ?)";
			params.add(promotion.getId());
		}
		if (productTag != null) {
			sql += " AND id IN (SELECT ppt.`products_id` FROM product_product_tag ppt WHERE ppt.product_tags_id = ?)";
			params.add(productTag.getId());
		}
		if (storeProductTag != null) {
			sql += " AND id IN (SELECT pspt.`products_id` FROM product_store_product_tag pspt WHERE pspt.store_product_tags_id = ?)";
			params.add(storeProductTag.getId());
		}
		if (attributeValueMap != null) {
			for (Map.Entry<Attribute, String> entry : attributeValueMap.entrySet()) {
				String propertyName = Product.ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + entry.getKey().getPropertyIndex();
				sql += " AND " + propertyName + "=" + entry.getValue();
			}
		}
		if (startPrice != null && endPrice != null && startPrice.compareTo(endPrice) > 0) {
			BigDecimal temp = startPrice;
			startPrice = endPrice;
			endPrice = temp;
		}
		if (startPrice != null && startPrice.compareTo(BigDecimal.ZERO) >= 0) {
			sql += " AND price => ?";
			params.add(startPrice);
		}
		if (endPrice != null && endPrice.compareTo(BigDecimal.ZERO) >= 0) {
			sql += " AND price <= ?";
			params.add(endPrice);
		}
		if (isMarketable != null) {
			sql += " AND is_marketable = ?";
			params.add(isMarketable);
		}
		if (isList != null) {
			sql += " AND is_list = ?";
			params.add(isList);
		}
		if (isTop != null) {
			sql += " AND is_top = ?";
			params.add(isTop);
		}
		if (isActive != null) {
			sql += " AND is_active = ?";
			params.add(isActive);
		}
		if (isOutOfStock != null) {
			if (isOutOfStock) {
				sql += " AND EXISTS(SELECT id FROM sku WHERE product_id = p.id AND stock <= allocated_stock) ";
			} else {
				sql += " AND EXISTS(SELECT id FROM sku WHERE .product_id = p.id AND stock > allocated_stock) ";
			}
		}
		if (isStockAlert != null) {
			Setting setting = SystemUtils.getSetting();
			if (isStockAlert) {
				sql += " AND EXISTS(SELECT id FROM sku WHERE product_id = p.id AND stock <= allocated_stock + ?)";
				params.add(setting.getStockAlertCount());
			} else {
				sql += " AND EXISTS(SELECT id FROM sku WHERE product_id = p.id AND stock > allocated_stock + ?)";
				params.add(setting.getStockAlertCount());
			}
		}
		if (hasPromotion != null) {
			sql += " AND id IN (SELECT pp.`products_id` FROM product_promotion pp WHERE pp.`promotions_id` = ?)";
			params.add(promotion.getId());
		}
		if (orderType != null) {
			switch (orderType) {
			case topDesc:
				orders.add(new Order("is_top", Order.Direction.desc));
				orders.add(new Order("created_date", Order.Direction.desc));
				break;
			case priceAsc:
				orders.add(new Order("price", Order.Direction.asc));
				orders.add(new Order("created_date", Order.Direction.desc));
				break;
			case priceDesc:
				orders.add(new Order("price", Order.Direction.desc));
				orders.add(new Order("created_date", Order.Direction.desc));
				break;
			case salesDesc:
				orders.add(new Order("sales", Order.Direction.desc));
				orders.add(new Order("created_date", Order.Direction.desc));
				break;
			case scoreDesc:
				orders.add(new Order("score", Order.Direction.desc));
				orders.add(new Order("created_date", Order.Direction.desc));
				break;
			case dateDesc:
				orders.add(new Order("created_date", Order.Direction.desc));
				break;
			default:
				break;
			}
		} else if (CollectionUtils.isEmpty(orders)) {
			orders.add(new Order("is_top", Order.Direction.desc));
			orders.add(new Order("created_date", Order.Direction.desc));
		}
		return super.findList(sql, null, count, filters, orders, params);
	}

	/**
	 * 查找商品
	 * 
	 * @param productCategory
	 *            商品分类
	 * @param storeProductCategory
	 *            店铺商品分类
	 * @param isMarketable
	 *            是否上架
	 * @param isActive
	 *            是否有效
	 * @param beginDate
	 *            起始日期
	 * @param endDate
	 *            结束日期
	 * @param first
	 *            起始记录
	 * @param count
	 *            数量
	 * @return 商品
	 */
	public List<Product> findList(ProductCategory productCategory, StoreProductCategory storeProductCategory, Boolean isMarketable, Boolean isActive, Date beginDate, Date endDate, Integer first, Integer count) {
		String sql = "SELECT * FROM `product` p WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		
		if (productCategory != null) {
			sql += " AND (product_category_id IN(SELECT id FROM product_category WHERE id = ? OR tree_path LIKE ?))";
			params.add(productCategory.getId());
			params.add("%" + ProductCategory.TREE_PATH_SEPARATOR + productCategory.getId() + ProductCategory.TREE_PATH_SEPARATOR + "%");
		}
		if (storeProductCategory != null) {
			sql += " AND (store_product_category_id IN (SELECT id FROM store_product_category WHERE id = ? OR tree_path LIKE ?))";
			params.add(storeProductCategory.getId());
			params.add("%" + StoreProductCategory.TREE_PATH_SEPARATOR + storeProductCategory.getId() + StoreProductCategory.TREE_PATH_SEPARATOR + "%");
		}
		if (isMarketable != null) {
			sql += " AND is_marketable = ?";
			params.add(isMarketable);
		}
		if (isActive != null) {
			sql += " AND is_active = ";
			params.add(isActive);
		}
		if (beginDate != null) {
			sql += " AND created_date => ?";
			params.add(DateUtil.formatDateTime(beginDate));
		}
		if (endDate != null) {
			sql += " AND created_date <= ?";
			params.add(DateUtil.formatDateTime(endDate));
		}
		return super.findList(sql, first, count, params);
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
		String sql = "SELECT * FROM `product` p WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		
		if (store != null) {
			sql += " AND store_id = ?";
			params.add(store.getId());
		}
		
		List<Order> orders = new ArrayList<Order>();
		if (rankingType != null) {
			switch (rankingType) {
			case score:
				orders.add(new Order("score", Order.Direction.desc));
				orders.add(new Order("score_count", Order.Direction.desc));
				break;
			case scoreCount:
				orders.add(new Order("score_count", Order.Direction.desc));
				orders.add(new Order("score", Order.Direction.desc));
				break;
			case weekHits:
				orders.add(new Order("week_hits", Order.Direction.desc));
				break;
			case monthHits:
				orders.add(new Order("month_hits", Order.Direction.desc));
				break;
			case hits:
				orders.add(new Order("hits", Order.Direction.desc));
				break;
			case weekSales:
				orders.add(new Order("week_sales", Order.Direction.desc));
				break;
			case monthSales:
				orders.add(new Order("month_sales", Order.Direction.desc));
				break;
			case sales:
				orders.add(new Order("sales", Order.Direction.desc));
				break;
			default:
				break;
			}
		}
		return super.findList(sql, null, count, null, orders, params);
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
		String sqlExceptSelect = "FROM `product` p WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		
		if (type != null) {
			sqlExceptSelect += " AND type = ?";
			params.add(type.ordinal());
		}
		if (store != null) {
			sqlExceptSelect += " AND store_id = ?" ;
			params.add(store.getId());
		}
		if (productCategory != null) {
			sqlExceptSelect += " AND (product_category_id IN(SELECT id FROM product_category WHERE id = ? OR tree_path LIKE ?))";
			params.add(productCategory.getId());
			params.add("%" + ProductCategory.TREE_PATH_SEPARATOR + productCategory.getId() + ProductCategory.TREE_PATH_SEPARATOR + "%");
		}
		if (storeProductCategory != null) {
			sqlExceptSelect += " AND (store_product_category_id IN (SELECT id FROM store_product_category WHERE id = ? OR tree_path LIKE ?))";
			params.add(storeProductCategory.getId());
			params.add("%" + StoreProductCategory.TREE_PATH_SEPARATOR + storeProductCategory.getId() + StoreProductCategory.TREE_PATH_SEPARATOR + "%");
		}
		if (brand != null) {
			sqlExceptSelect += " AND brand_id = ? " ;
			params.add(brand.getId());
		}
		if (promotion != null) {
			sqlExceptSelect += " AND id IN (SELECT pp.`products_id` FROM product_promotion pp WHERE pp.`promotions_id` = ?)";
			params.add(promotion.getId());
		}
		if (productTag != null) {
			sqlExceptSelect += " AND id IN (SELECT ppt.`products_id` FROM product_product_tag ppt WHERE ppt.product_tags_id = ?)";
			params.add(productTag.getId());
		}
		if (storeProductTag != null) {
			sqlExceptSelect += " AND id IN (SELECT pspt.`products_id` FROM product_store_product_tag pspt WHERE pspt.store_product_tags_id = ?)";
			params.add(storeProductTag.getId());
		}
		if (attributeValueMap != null) {
			for (Map.Entry<Attribute, String> entry : attributeValueMap.entrySet()) {
				String propertyName = Product.ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + entry.getKey().getPropertyIndex();
				sqlExceptSelect += " AND " + propertyName + "=" + entry.getValue();
			}
		}
		if (startPrice != null && endPrice != null && startPrice.compareTo(endPrice) > 0) {
			BigDecimal temp = startPrice;
			startPrice = endPrice;
			endPrice = temp;
		}
		if (startPrice != null && startPrice.compareTo(BigDecimal.ZERO) >= 0) {
			sqlExceptSelect += " AND price => ?";
			params.add(startPrice);
		}
		if (endPrice != null && endPrice.compareTo(BigDecimal.ZERO) >= 0) {
			sqlExceptSelect += " AND price <= ?";
			params.add(endPrice);
		}
		if (isMarketable != null) {
			sqlExceptSelect += " AND is_marketable = ?";
			params.add(isMarketable);
		}
		if (isList != null) {
			sqlExceptSelect += " AND is_list = ?";
			params.add(isList);
		}
		if (isTop != null) {
			sqlExceptSelect += " AND is_top = ?";
			params.add(isTop);
		}
		if (isActive != null) {
			sqlExceptSelect += " AND is_active = ?";
			params.add(isActive);
		}
		if (isOutOfStock != null) {
			if (isOutOfStock) {
				sqlExceptSelect += " AND EXISTS(SELECT id FROM sku WHERE product_id = p.id AND stock <= allocated_stock) ";
			} else {
				sqlExceptSelect += " AND EXISTS(SELECT id FROM sku WHERE product_id = p.id AND stock > allocated_stock) ";
			}
		}
		if (isStockAlert != null) {
			Setting setting = SystemUtils.getSetting();
			if (isStockAlert) {
				sqlExceptSelect += " AND EXISTS(SELECT id FROM sku WHERE product_id = p.id AND stock <= allocated_stock + ?)";
				params.add(setting.getStockAlertCount());
			} else {
				sqlExceptSelect += " AND EXISTS(SELECT id FROM sku WHERE product_id = p.id AND stock > allocated_stock + ?)";
				params.add(setting.getStockAlertCount());
			}
		}
		if (hasPromotion != null) {
			sqlExceptSelect += " AND id IN (SELECT pp.`products_id` FROM product_promotion pp WHERE pp.`promotions_id` = ?)";
			params.add(promotion.getId());
		}
		List<Order> orders = new ArrayList<Order>();
		if (orderType != null) {
			switch (orderType) {
			case topDesc:
				orders.add(new Order("is_top", Order.Direction.desc));
				orders.add(new Order("created_date", Order.Direction.desc));
				break;
			case priceAsc:
				orders.add(new Order("price", Order.Direction.asc));
				orders.add(new Order("created_date", Order.Direction.desc));
				break;
			case priceDesc:
				orders.add(new Order("price", Order.Direction.desc));
				orders.add(new Order("created_date", Order.Direction.desc));
				break;
			case salesDesc:
				orders.add(new Order("sales", Order.Direction.desc));
				orders.add(new Order("created_date", Order.Direction.desc));
				break;
			case scoreDesc:
				orders.add(new Order("score", Order.Direction.desc));
				orders.add(new Order("created_date", Order.Direction.desc));
				break;
			case dateDesc:
				orders.add(new Order("created_date", Order.Direction.desc));
				break;
			default:
				break;
			}
		} else if (pageable == null || ((StringUtils.isEmpty(pageable.getOrderProperty()) || pageable.getOrderDirection() == null) && (CollectionUtils.isEmpty(pageable.getOrders())))) {
			orders.add(new Order("is_top", Order.Direction.desc));
			orders.add(new Order("created_date", Order.Direction.desc));
		}
		pageable.setOrders(orders);
		return super.findPage(sqlExceptSelect, pageable, params);
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
		String sql = "SELECT COUNT(1) FROM `product` p WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		
		if (type != null) {
			sql += " AND type = ?";
			params.add(type.ordinal());
		}
		if (store != null) {
			sql += " AND store_id = ?";
			params.add(store.getId());
		}
		if (isMarketable != null) {
			sql += " AND is_marketable = ?";
			params.add(isMarketable);
		}
		if (isList != null) {
			sql += " AND is_list = ?";
			params.add(isList);
		}
		if (isTop != null) {
			sql += " AND is_top = ?";
			params.add(isTop);
		}
		if (isActive != null) {
			sql += " AND is_active = ?";
			params.add(isActive);
		}
		if (isOutOfStock != null) {
			if (isOutOfStock) {
				sql += " AND EXISTS (SELECT id FROM sku WHERE product_id = p.id AND stock <= allocated_stock)";
			} else {
				sql += " AND EXISTS (SELECT id FROM sku WHERE .product_id = p.id AND stock > allocated_stock)";
			}
		}
		if (isStockAlert != null) {
			Setting setting = SystemUtils.getSetting();
			if (isStockAlert) {
				sql += " AND EXISTS (SELECT id FROM sku WHERE product_id = p.id AND stock <= allocated_stock + ?)";
				params.add(setting.getStockAlertCount());
			} else {
				sql += " AND EXISTS (SELECT id FROM sku WHERE product_id = p.id AND stock > allocated_stock + ?)";
				params.add(setting.getStockAlertCount());
			}
		}
		return super.count(sql, params);
	}

	/**
	 * 清除商品属性值
	 * 
	 * @param attribute
	 *            属性
	 */
	public void clearAttributeValue(Attribute attribute) {
		if (attribute == null || attribute.getPropertyIndex() == null || attribute.getProductCategory() == null) {
			return;
		}

		String sql = "UPDATE `product` SET " + com.jfinalshop.util.StringUtils.camelToUnderline(Product.ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX) + attribute.getPropertyIndex() + " = NULL WHERE product_category_id = ?";
		Db.update(sql, attribute.getProductCategory().getId());
	}

	/**
	 * 刷新过期店铺商品有效状态
	 */
	public void refreshExpiredStoreProductActive() {
		String sql = "UPDATE `product` SET is_active = FALSE WHERE is_active = TRUE AND store_id IN (SELECT id FROM store WHERE end_date IS NULL OR end_date <= ?)";
		Db.update(sql, DateUtil.now());
	}

	
	/**
	 * 刷新商品有效状态
	 * 
	 * @param store
	 *            店铺
	 */
	public void refreshActive(Store store) {
		Assert.notNull(store);

		setActive(store);
		setInactive(store);
	}

	/**
	 * 设置商品有效状态
	 * 
	 * @param store
	 *            店铺
	 */
	private void setActive(Store store) {
//		Assert.notNull(store);

//		String sql ="UPDATE product p SET p.is_active = TRUE WHERE p.is_active = false AND p.store_id = ?"
//				+ " AND p.store_id IN (SELECT id FROM store s WHERE s.is_enabled = TRUE AND s.`STATUS` = ? AND s.end_date IS NOT NULL AND s.end_date > ?) "
//				+ " AND p.product_category_id IN (SELECT id FROM product_category JOIN product_category_store WHERE stores_id = p.store_id) ";
//		Db.update(sql, store.getId(), Store.Status.success.ordinal(), DateUtil.now());
	}

	/**
	 * 设置商品无效状态
	 * 
	 * @param store
	 *            店铺
	 */
	private void setInactive(Store store) {
//		Assert.notNull(store);
//
//		String sql ="UPDATE product p SET p.is_active = FALSE WHERE p.is_active = true AND p.store_id = ?"
//				+ " AND p.store_id IN (SELECT id FROM store s WHERE s.is_enabled = TRUE AND s.`STATUS` = ? AND s.end_date IS NULL OR s.end_date > ?) "
//				+ " OR p.product_category_id NOT IN (SELECT id FROM product_category JOIN product_category_store WHERE stores_id = p.store_id) ";
//		Db.update(sql, store.getId(), Store.Status.success.ordinal(), DateUtil.now());
	}
	
	
	/**
	 * 按销量排序查找货品
	 * 
	 * @param productCategory
	 *            商品分类
	 * @param count
	 *            数量
	 * @return 货品
	 */
	public List<Product> findByTopSales(ProductCategory productCategory, Integer count) {
		String sql = "SELECT * FROM product p WHERE 1 = 1 AND is_marketable = TRUE ";
		if (productCategory != null) {
			sql += " AND `product_category_id` IN (SELECT id FROM product_category WHERE `id` = ? OR tree_path LIKE '%," + ProductCategory.TREE_PATH_SEPARATOR + productCategory.getId() + ProductCategory.TREE_PATH_SEPARATOR + ",%') ";
		}
		sql += " ORDER BY sales DESC LIMIT 0, ? ";
		return modelManager.find(sql, productCategory.getId(), count);
	}
}