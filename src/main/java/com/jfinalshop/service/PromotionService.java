package com.jfinalshop.service;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import org.apache.commons.collections.CollectionUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.Pageable;
import com.jfinalshop.dao.MemberRankDao;
import com.jfinalshop.dao.ProductCategoryDao;
import com.jfinalshop.dao.PromotionDao;
import com.jfinalshop.dao.StoreDao;
import com.jfinalshop.model.Coupon;
import com.jfinalshop.model.MemberRank;
import com.jfinalshop.model.ProductCategory;
import com.jfinalshop.model.Promotion;
import com.jfinalshop.model.PromotionCoupon;
import com.jfinalshop.model.PromotionMemberRank;
import com.jfinalshop.model.PromotionSku;
import com.jfinalshop.model.Sku;
import com.jfinalshop.model.Store;
import com.jfinalshop.util.Assert;

/**
 * Service - 促销
 * 
 */
@Singleton
public class PromotionService extends BaseService<Promotion> {

	/**
	 * 构造方法
	 */
	public PromotionService() {
		super(Promotion.class);
	}
	
	@Inject
	private PromotionDao promotionDao;
	@Inject
	private MemberRankDao memberRankDao;
	@Inject
	private ProductCategoryDao productCategoryDao;
	@Inject
	private StoreDao storeDao;
	
	/**
	 * 价格表达式变量
	 */
	private static final List<Map<String, Object>> PRICE_EXPRESSION_VARIABLES = new ArrayList<>();

	/**
	 * 积分表达式变量
	 */
	private static final List<Map<String, Object>> POINT_EXPRESSION_VARIABLES = new ArrayList<>();
	
	static {
		Map<String, Object> variable0 = new HashMap<>();
		Map<String, Object> variable1 = new HashMap<>();
		Map<String, Object> variable2 = new HashMap<>();
		Map<String, Object> variable3 = new HashMap<>();
		variable0.put("quantity", 99);
		variable0.put("price", new BigDecimal("99"));
		variable1.put("quantity", 99);
		variable1.put("price", new BigDecimal("9.9"));
		variable2.put("quantity", 99);
		variable2.put("price", new BigDecimal("0.99"));
		variable3.put("quantity", 99);
		variable3.put("point", 99L);
		PRICE_EXPRESSION_VARIABLES.add(variable0);
		PRICE_EXPRESSION_VARIABLES.add(variable1);
		PRICE_EXPRESSION_VARIABLES.add(variable2);
		POINT_EXPRESSION_VARIABLES.add(variable3);
	}
	
	/**
	 * 验证价格运算表达式
	 * 
	 * @param priceExpression
	 *            价格运算表达式
	 * @return 验证结果
	 */
	public boolean isValidPriceExpression(String priceExpression) {
		Assert.hasText(priceExpression);

		for (Map<String, Object> variable : PRICE_EXPRESSION_VARIABLES) {
			try {
				Binding binding = new Binding();
				for (Map.Entry<String, Object> entry : variable.entrySet()) {
					binding.setVariable(entry.getKey(), entry.getValue());
				}
				GroovyShell groovyShell = new GroovyShell(binding);
				Object result = groovyShell.evaluate(priceExpression);
				new BigDecimal(result.toString());
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 查找促销
	 * 
	 * @param store
	 *            店铺
	 * @param type
	 *            类型
	 * @param isEnabled
	 *            是否开启
	 * @return 促销
	 */
	public List<Promotion> findList(Store store, Promotion.Type type, Boolean isEnabled) {
		return promotionDao.findList(store, type, isEnabled);
	}


	/**
	 * 查找促销
	 * 
	 * @param store
	 *            店铺
	 * @param type
	 *            类型
	 * @param memberRank
	 *            会员等级
	 * @param productCategory
	 *            商品分类
	 * @param hasBegun
	 *            是否已开始
	 * @param hasEnded
	 *            是否已结束
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 促销
	 */
	public List<Promotion> findList(Store store, Promotion.Type type, MemberRank memberRank, ProductCategory productCategory, Boolean hasBegun, Boolean hasEnded, Integer count, List<Filter> filters, List<Order> orders) {
		return promotionDao.findList(store, type, memberRank, productCategory, hasBegun, hasEnded, count, filters, orders);
	}

	/**
	 * 查找促销
	 * 
	 * @param type
	 *            促销类型
	 * @param storeId
	 *            店铺ID
	 * @param memberRankId
	 *            会员等级ID
	 * @param productCategoryId
	 *            商品分类ID
	 * @param hasBegun
	 *            是否已开始
	 * @param hasEnded
	 *            是否已结束
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @param useCache
	 *            是否使用缓存
	 * @return 促销
	 */
	public List<Promotion> findList(Promotion.Type type, Long storeId, Long memberRankId, Long productCategoryId, Boolean hasBegun, Boolean hasEnded, Integer count, List<Filter> filters, List<Order> orders, boolean useCache) {
		Store store = storeDao.find(storeId);
		if (storeId != null && store == null) {
			return Collections.emptyList();
		}
		MemberRank memberRank = memberRankDao.find(memberRankId);
		if (memberRankId != null && memberRank == null) {
			return Collections.emptyList();
		}
		ProductCategory productCategory = productCategoryDao.find(productCategoryId);
		if (productCategoryId != null && productCategory == null) {
			return Collections.emptyList();
		}
		return promotionDao.findList(store, type, memberRank, productCategory, hasBegun, hasEnded, count, filters, orders);
	}

	/**
	 * 查找促销
	 * 
	 * @param store
	 *            店铺
	 * @param type
	 *            类型
	 * @param pageable
	 *            分页
	 * @return 促销分页
	 */
	public Page<Promotion> findPage(Store store, Promotion.Type type, Pageable pageable) {
		return promotionDao.findPage(store, type, pageable);
	}

	/**
	 * 根据促销类型关闭促销
	 * 
	 * @param type
	 *            类型
	 */
	public void shutDownPromotion(Promotion.Type type) {
		while (true) {
			List<Promotion> promotions = promotionDao.findList(null, type, null, null, null, null, 100, null, null);
			if (CollectionUtils.isNotEmpty(promotions)) {
				for (Promotion promotion : promotions) {
					promotion.setIsEnabled(false);
				}
//				promotionDao.flush();
//				promotionDao.clear();
			}
			if (promotions.size() < 100) {
				break;
			}
		}
	}
	
	@Override
	public Promotion save(Promotion promotion) {
		super.save(promotion);
		// 允许参加会员等级
		List<MemberRank> memberRanks = promotion.getMemberRanks();
		if (CollectionUtils.isNotEmpty(memberRanks)) {
			for (MemberRank memberRank : memberRanks) {
				PromotionMemberRank promotionMemberRank = new PromotionMemberRank();
				promotionMemberRank.setMemberRanksId(memberRank.getId());
				promotionMemberRank.setPromotionsId(promotion.getId());
				promotionMemberRank.save();
			}
		}
		// 赠送优惠券
		List<Coupon> coupons = promotion.getCoupons();
		if (CollectionUtils.isNotEmpty(coupons)) {
			for (Coupon coupon : coupons) {
				PromotionCoupon promotionCoupon = new PromotionCoupon();
				promotionCoupon.setCouponsId(coupon.getId());
				promotionCoupon.setPromotionsId(promotion.getId());
				promotionCoupon.save();
			}
		}
		// 赠品
		List<Sku> gifts = promotion.getGifts();
		if (CollectionUtils.isNotEmpty(gifts)) {
			for (Sku gift : gifts) {
				PromotionSku promotionSku = new PromotionSku();
				promotionSku.setGiftPromotionsId(promotion.getId());
				promotionSku.setGiftsId(gift.getId());
				promotionSku.save();
			}
		}
		return promotion;
	}
	
	@Override
	public Promotion update(Promotion promotion) {
		super.update(promotion);
		// 允许参加会员等级
		List<MemberRank> memberRanks = promotion.getMemberRanks();
		if (CollectionUtils.isNotEmpty(memberRanks)) {
			Db.deleteById("promotion_member_rank", "promotions_id", promotion.getId());
			for (MemberRank memberRank : memberRanks) {
				PromotionMemberRank promotionMemberRank = new PromotionMemberRank();
				promotionMemberRank.setMemberRanksId(memberRank.getId());
				promotionMemberRank.setPromotionsId(promotion.getId());
				promotionMemberRank.save();
			}
		}
		// 赠送优惠券
		List<Coupon> coupons = promotion.getCoupons();
		if (CollectionUtils.isNotEmpty(coupons)) {
			Db.deleteById("promotion_coupon", "promotions_id", promotion.getId());
			for (Coupon coupon : coupons) {
				PromotionCoupon promotionCoupon = new PromotionCoupon();
				promotionCoupon.setCouponsId(coupon.getId());
				promotionCoupon.setPromotionsId(promotion.getId());
				promotionCoupon.save();
			}
		}
		// 赠品
		List<Sku> gifts = promotion.getGifts();
		if (CollectionUtils.isNotEmpty(gifts)) {
			Db.deleteById("promotion_sku", "gift_promotions_id", promotion.getId());
			for (Sku gift : gifts) {
				PromotionSku promotionSku = new PromotionSku();
				promotionSku.setGiftPromotionsId(promotion.getId());
				promotionSku.setGiftsId(gift.getId());
				promotionSku.save();
			}
		}
		return promotion;
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
	public void delete(Promotion promotion) {
		super.delete(promotion);
	}
}