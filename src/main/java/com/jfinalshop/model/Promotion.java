package com.jfinalshop.model;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang3.StringUtils;

import com.jfinalshop.model.base.BasePromotion;

/**
 * Model - 促销
 * 
 */
public class Promotion extends BasePromotion<Promotion> implements Comparable<Promotion> {
	private static final long serialVersionUID = -7841815369133578029L;
	public static final Promotion dao = new Promotion().dao();
	
	
	/**
	 * 路径
	 */
	private static final String PATH = "/promotion/detail/%d";

	/**
	 * 类型
	 */
	public enum Type {

		/**
		 * 折扣
		 */
		discount,

		/**
		 * 满减
		 */
		fullReduction
	}

	
	/**
	 * 店铺
	 */
	private Store store;

	/**
	 * 允许参加会员等级
	 */
	private List<MemberRank> memberRanks = new ArrayList<>();

	/**
	 * 赠送优惠券
	 */
	private List<Coupon> coupons = new ArrayList<>();

	/**
	 * 赠品
	 */
	private List<Sku> gifts = new ArrayList<>();

	/**
	 * 商品
	 */
	private List<Product> products = new ArrayList<>();

	/**
	 * 商品分类
	 */
	private List<ProductCategory> productCategories = new ArrayList<>();
	
	
	/**
	 * 类型名称
	 */
	public Type getTypeName() {
		return Type.values()[getType()];
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
	 * 设置店铺
	 * 
	 * @param store
	 *            店铺
	 */
	public void setStore(Store store) {
		this.store = store;
	}

	/**
	 * 获取允许参加会员等级
	 * 
	 * @return 允许参加会员等级
	 */
	public List<MemberRank> getMemberRanks() {
		if (CollectionUtils.isEmpty(memberRanks)) {
			String sql = "SELECT p.* FROM member_rank p LEFT JOIN promotion_member_rank pmr ON p.id = pmr.member_ranks_id WHERE pmr.promotions_id = ?";
			memberRanks = MemberRank.dao.find(sql, getId());
		}
		return memberRanks;
	}

	/**
	 * 设置允许参加会员等级
	 * 
	 * @param memberRanks
	 *            允许参加会员等级
	 */
	public void setMemberRanks(List<MemberRank> memberRanks) {
		this.memberRanks = memberRanks;
	}

	/**
	 * 获取赠送优惠券
	 * 
	 * @return 赠送优惠券
	 */
	public List<Coupon> getCoupons() {
		if (CollectionUtils.isEmpty(coupons)) {
			String sql = "SELECT p.* FROM coupon p LEFT JOIN promotion_coupon pc ON p.id = pc.coupons_id WHERE pc.promotions_id = ?";
			coupons = Coupon.dao.find(sql, getId());
		}
		return coupons;
	}

	/**
	 * 设置赠送优惠券
	 * 
	 * @param coupons
	 *            赠送优惠券
	 */
	public void setCoupons(List<Coupon> coupons) {
		this.coupons = coupons;
	}

	/**
	 * 获取赠品
	 * 
	 * @return 赠品
	 */
	public List<Sku> getGifts() {
		if (CollectionUtils.isEmpty(gifts)) {
			String sql = "SELECT p.* FROM sku p LEFT JOIN promotion_sku ps ON p.id = ps.gifts_id WHERE gift_promotions_id = ?";
			gifts = Sku.dao.find(sql, getId());
		}
		return gifts;
	}

	/**
	 * 设置赠品
	 * 
	 * @param gifts
	 *            赠品
	 */
	public void setGifts(List<Sku> gifts) {
		this.gifts = gifts;
	}

	/**
	 * 获取商品
	 * 
	 * @return 商品
	 */
	public List<Product> getProducts() {
		if (CollectionUtils.isEmpty(products)) {
			String sql = "SELECT p.* FROM product p LEFT JOIN product_promotion pp ON p.id = pp.`products_id` WHERE pp.`promotions_id` = ?";
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
	 * 获取商品分类
	 * 
	 * @return 商品分类
	 */
	public List<ProductCategory> getProductCategories() {
		if (CollectionUtils.isEmpty(productCategories)) {
			String sql = "SELECT p.*  FROM product_category p LEFT JOIN product_category_promotion pcp ON p.id = pcp.`product_categories_id` WHERE pcp.`promotions_id` = ?";
			productCategories = ProductCategory.dao.find(sql, getId());
		}
		return productCategories;
	}

	/**
	 * 设置商品分类
	 * 
	 * @param productCategories
	 *            商品分类
	 */
	public void setProductCategories(List<ProductCategory> productCategories) {
		this.productCategories = productCategories;
	}

	/**
	 * 获取路径
	 * 
	 * @return 路径
	 */
	public String getPath() {
		return String.format(Promotion.PATH, getId());
	}

	/**
	 * 判断是否已开始
	 * 
	 * @return 是否已开始
	 */
	public boolean hasBegun() {
		return getBeginDate() == null || !getBeginDate().after(new Date());
	}

	/**
	 * 判断是否已结束
	 * 
	 * @return 是否已结束
	 */
	public boolean hasEnded() {
		return getEndDate() != null && !getEndDate().after(new Date());
	}

	/**
	 * 计算促销价格
	 * 
	 * @param price
	 *            SKU价格
	 * @param quantity
	 *            SKU数量
	 * @return 促销价格
	 */
	public BigDecimal calculatePrice(BigDecimal price, Integer quantity) {
		if (price == null || quantity == null || StringUtils.isEmpty(getPriceExpression())) {
			return price;
		}
		BigDecimal result = BigDecimal.ZERO;
		try {
			Binding binding = new Binding();
			binding.setVariable("quantity", quantity);
			binding.setVariable("price", price);
			GroovyShell groovyShell = new GroovyShell(binding);
			result = new BigDecimal(groovyShell.evaluate(getPriceExpression()).toString(), MathContext.DECIMAL32);
		} catch (Exception e) {
			return price;
		}
		if (result.compareTo(price) > 0) {
			return price;
		}
		return result.compareTo(BigDecimal.ZERO) > 0 ? result : BigDecimal.ZERO;
	}

	/**
	 * 删除前处理
	 */
	public void preRemove() {
		List<Product> products = getProducts();
		if (products != null) {
			for (Product product : products) {
				product.getPromotions().remove(this);
			}
		}
		List<ProductCategory> productCategories = getProductCategories();
		if (productCategories != null) {
			for (ProductCategory productCategory : productCategories) {
				productCategory.getPromotions().remove(this);
			}
		}
	}


	@Override
	public int compareTo(Promotion promotion) {
		if (promotion == null) {
			return 1;
		}
		return new CompareToBuilder().append(getOrders(), promotion.getOrders()).append(getId(), promotion.getId()).toComparison();
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Promotion) {
			Promotion promotion = (Promotion) obj;
			return getId().equals(promotion.getId());
		}
		return super.equals(obj);
	}

	
}
