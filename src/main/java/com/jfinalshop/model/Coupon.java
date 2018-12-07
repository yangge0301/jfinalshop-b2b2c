package com.jfinalshop.model;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinalshop.model.base.BaseCoupon;

/**
 * Model - 优惠券
 * 
 */
public class Coupon extends BaseCoupon<Coupon> {
	private static final long serialVersionUID = -6063171273339816295L;
	public static final Coupon dao = new Coupon().dao();
	
	/**
	 * 店铺
	 */
	private Store store;

	/**
	 * 优惠码
	 */
	private List<CouponCode> couponCodes = new ArrayList<CouponCode>();

	/**
	 * 促销
	 */
	private List<Promotion> promotions = new ArrayList<Promotion>();

	/**
	 * 订单
	 */
	private List<Order> order = new ArrayList<Order>();
	
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
	 * 获取优惠码
	 * 
	 * @return 优惠码
	 */
	public List<CouponCode> getCouponCodes() {
		if (CollectionUtils.isEmpty(couponCodes)) {
			String sql = "SELECT * FROM `coupon_code` WHERE coupon_id = ?";
			couponCodes = CouponCode.dao.find(sql, getId());
		}
		return couponCodes;
	}

	/**
	 * 设置优惠码
	 * 
	 * @param couponCodes
	 *            优惠码
	 */
	public void setCouponCodes(List<CouponCode> couponCodes) {
		this.couponCodes = couponCodes;
	}

	/**
	 * 获取促销
	 * 
	 * @return 促销
	 */
	public List<Promotion> getPromotions() {
		if (CollectionUtils.isEmpty(promotions)) {
			String sql = "SELECT p.*  FROM promotion p LEFT JOIN promotion_coupon pc ON p.id = pc.promotions_id WHERE pc.coupons_id = ?";
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
	 * 获取订单
	 * 
	 * @return 订单
	 */
	public List<Order> getOrder() {
		if (CollectionUtils.isEmpty(order)) {
			String sql = "SELECT * FROM `order` WHERE coupon_code_id = ?";
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
	 * 判断是否已开始
	 * 
	 * @return 是否已开始
	 */
	public boolean hasBegun() {
		return getBeginDate() == null || !getBeginDate().after(new Date());
	}

	/**
	 * 判断是否已过期
	 * 
	 * @return 是否已过期
	 */
	public boolean hasExpired() {
		return getEndDate() != null && !getEndDate().after(new Date());
	}

	/**
	 * 计算优惠价格
	 * 
	 * @param price
	 *            商品价格
	 * @param quantity
	 *            商品数量
	 * @return 优惠价格
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
			result = new BigDecimal(groovyShell.evaluate(getPriceExpression()).toString());
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
		List<Promotion> promotions = getPromotions();
		if (promotions != null) {
			for (Promotion promotion : promotions) {
				promotion.getCoupons().remove(this);
			}
		}
		List<Order> orders = getOrder();
		if (orders != null) {
			for (Order order : orders) {
				order.getCoupons().remove(this);
			}
		}
	}

}
