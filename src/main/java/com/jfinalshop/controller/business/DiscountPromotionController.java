package com.jfinalshop.controller.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hasor.core.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.UnauthorizedException;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Pageable;
import com.jfinalshop.Results;
import com.jfinalshop.model.Business;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.Promotion;
import com.jfinalshop.model.Sku;
import com.jfinalshop.model.Store;
import com.jfinalshop.model.Svc;
import com.jfinalshop.plugin.PaymentPlugin;
import com.jfinalshop.plugin.PromotionPlugin;
import com.jfinalshop.plugin.discountPromotion.DiscountPromotionPlugin;
import com.jfinalshop.service.BusinessService;
import com.jfinalshop.service.CouponService;
import com.jfinalshop.service.MemberRankService;
import com.jfinalshop.service.PluginService;
import com.jfinalshop.service.PromotionService;
import com.jfinalshop.service.SkuService;
import com.jfinalshop.service.StoreService;
import com.jfinalshop.service.SvcService;

/**
 * Controller - 折扣促销
 * 
 */
@ControllerBind(controllerKey = "/business/discount_promotion")
public class DiscountPromotionController extends BaseController {

	@Inject
	private PromotionService promotionService;
	@Inject
	private MemberRankService memberRankService;
	@Inject
	private SkuService skuService;
	@Inject
	private CouponService couponService;
	@Inject
	private StoreService storeService;
	@Inject
	private PluginService pluginService;
	@Inject
	private SvcService svcService;
	@Inject
	private BusinessService businessService;

	/**
	 * 添加属性
	 */
	public void populateModel() {
		Long promotionId = getParaToLong("promotionId");
		Store currentStore = businessService.getCurrentStore();
		
		Promotion promotion = promotionService.find(promotionId);
		if (promotion != null && !currentStore.equals(promotion.getStore())) {
			throw new UnauthorizedException();
		}
		setAttr("promotion", promotion);
	}

	/**
	 * 计算
	 */
	public void calculate() {
		String paymentPluginId = getPara("paymentPluginId");
		Integer months = getParaToInt("months");
		Boolean useBalance = getParaToBoolean("useBalance");
		
		Map<String, Object> data = new HashMap<>();
		PromotionPlugin promotionPlugin = pluginService.getPromotionPlugin(DiscountPromotionPlugin.ID);
		if (promotionPlugin == null || !promotionPlugin.getIsEnabled()) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		if (months == null || months <= 0) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		BigDecimal amount = promotionPlugin.getPrice().multiply(new BigDecimal(months));
		if (BooleanUtils.isTrue(useBalance)) {
			data.put("fee", BigDecimal.ZERO);
			data.put("amount", amount);
			data.put("useBalance", true);
		} else {
			PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin(paymentPluginId);
			if (paymentPlugin == null || !paymentPlugin.getIsEnabled()) {
				Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
				return;
			}
			data.put("fee", paymentPlugin.calculateFee(amount));
			data.put("amount", paymentPlugin.calculateAmount(amount));
			data.put("useBalance", false);
		}
		renderJson(data);
	}

	/**
	 * 到期日期
	 */
	@ActionKey("/business/discount_promotion/end_date")
	public void endDate() {
		Store currentStore = businessService.getCurrentStore();
		
		Map<String, Object> data = new HashMap<>();
		data.put("endDate", currentStore.getDiscountPromotionEndDate());
		renderJson(data);
	}

	/**
	 * 购买
	 */
	public void buy() {
		Store currentStore = businessService.getCurrentStore();
		PromotionPlugin promotionPlugin = pluginService.getPromotionPlugin(DiscountPromotionPlugin.ID);
		if (currentStore.getTypeName().equals(Store.Type.self) && currentStore.getDiscountPromotionEndDate() == null) {
			redirect("list");
			return;
		}
		if (promotionPlugin == null || !promotionPlugin.getIsEnabled()) {
			setAttr("errorMessage", "促销插件未启用!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		List<PaymentPlugin> paymentPlugins = pluginService.getActivePaymentPlugins(getRequest());
		if (CollectionUtils.isNotEmpty(paymentPlugins)) {
			setAttr("defaultPaymentPlugin", paymentPlugins.get(0));
			setAttr("paymentPlugins", paymentPlugins);
		}
		setAttr("promotionPlugin", promotionPlugin);
		render("/business/discount_promotion/buy.ftl");
	}

	/**
	 * 购买
	 */
	@Before(Tx.class)
	@ActionKey("/business/discount_promotion/save_buy")
	public void saveBuy() {
		Integer months = getParaToInt("months");
		Boolean useBalance = getParaToBoolean("useBalance");
		Store currentStore = businessService.getCurrentStore();
		Business currentUser = businessService.getCurrentUser();
		
		Map<String, Object> data = new HashMap<>();
		PromotionPlugin promotionPlugin = pluginService.getPromotionPlugin(DiscountPromotionPlugin.ID);
		if (currentStore.getTypeName().equals(Store.Type.self) && currentStore.getDiscountPromotionEndDate() == null) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		if (promotionPlugin == null || !promotionPlugin.getIsEnabled()) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		if (months == null || months <= 0) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		int days = months * 30;
		BigDecimal amount = promotionPlugin.getPrice().multiply(new BigDecimal(months));
		if (amount.compareTo(BigDecimal.ZERO) > 0) {
			if (BooleanUtils.isTrue(useBalance)) {
				if (currentUser.getBalance().compareTo(amount) < 0) {
					Results.unprocessableEntity(getResponse(), "business.discountPromotion.insufficientBalance");
				}
				storeService.buy(currentStore, promotionPlugin, months);
			} else {
				Svc promotionPluginSvc = new Svc();
				promotionPluginSvc.setAmount(amount);
				promotionPluginSvc.setDurationDays(days);
				promotionPluginSvc.setStoreId(currentStore.getId());
				//promotionPluginSvc.setPromotionPluginId(DiscountPromotionPlugin.ID);
				svcService.save(promotionPluginSvc);
				data.put("promotionPluginSvcSn", promotionPluginSvc.getSn());
			}
		} else {
			storeService.addDiscountPromotionEndDays(currentStore, days);
		}
		renderJson(data);
	}

	/**
	 * 赠品选择
	 */
	@ActionKey("/business/discount_promotion/gift_select")
	public void giftSelect() {
		String keyword = getPara("keyword");
		Long[] excludeIds = getParaValuesToLong("excludeIds");
		Business currentUser = businessService.getCurrentUser();
		
		List<Map<String, Object>> data = new ArrayList<>();
		if (StringUtils.isEmpty(keyword)) {
			renderJson(data);
			return;
		}
		Set<Sku> excludes = new HashSet<>(skuService.findList(excludeIds));
		List<Sku> skus = skuService.search(currentUser.getStore(), Product.Type.gift, keyword, excludes, null);
		for (Sku sku : skus) {
			Map<String, Object> item = new HashMap<>();
			item.put("id", sku.getId());
			item.put("sn", sku.getSn());
			item.put("name", sku.getName());
			item.put("specifications", sku.getSpecifications());
			item.put("path", sku.getPath());
			data.add(item);
		}
		renderJson(data);
	}

	/**
	 * 添加
	 */
	public void add() {
		Store currentStore = businessService.getCurrentStore();
		
		setAttr("memberRanks", memberRankService.findAll());
		setAttr("coupons", couponService.findList(currentStore));
		render("/business/discount_promotion/add.ftl");
	}

	/**
	 * 保存
	 */
	public void save() {
		Promotion promotion = getModel(Promotion.class);
		Boolean useAmountPromotion = getParaToBoolean("useAmountPromotion");
		Boolean useNumberPromotion = getParaToBoolean("useNumberPromotion");
		Long[] memberRankIds = getParaValuesToLong("memberRankIds");
		Long[] couponIds = getParaValuesToLong("couponIds");
		Long[] giftIds = getParaValuesToLong("giftIds");
		Store currentStore = businessService.getCurrentStore();
		
		promotion.setType(Promotion.Type.discount.ordinal());
		promotion.setStoreId(currentStore.getId());
		promotion.setMemberRanks(new ArrayList<>(memberRankService.findList(memberRankIds)));
		promotion.setCoupons(new ArrayList<>(couponService.findList(couponIds)));
		if (ArrayUtils.isNotEmpty(giftIds)) {
			List<Sku> gifts = skuService.findList(giftIds);
			CollectionUtils.filter(gifts, new Predicate() {
				@Override
				public boolean evaluate(Object object) {
					Sku gift = (Sku) object;
					return gift != null && Product.Type.gift.equals(gift.getType());
				}
			});
			promotion.setGifts(new ArrayList<>(gifts));
		} else {
			promotion.setGifts(null);
		}
		if (promotion.getBeginDate() != null && promotion.getEndDate() != null && promotion.getBeginDate().after(promotion.getEndDate())) {
			setAttr("errorMessage", "促销已过期!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (promotion.getMinimumQuantity() != null && promotion.getMaximumQuantity() != null && promotion.getMinimumQuantity() > promotion.getMaximumQuantity()) {
			setAttr("errorMessage", "促销最小数量错误!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (promotion.getMinimumPrice() != null && promotion.getMaximumPrice() != null && promotion.getMinimumPrice().compareTo(promotion.getMaximumPrice()) > 0) {
			setAttr("errorMessage", "促销最小单价错误!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		PromotionPlugin promotionPlugin = pluginService.getPromotionPlugin(DiscountPromotionPlugin.ID);
		String priceExpression = promotionPlugin.getPriceExpression(promotion, useAmountPromotion, useNumberPromotion);
		if (StringUtils.isNotEmpty(priceExpression) && !promotionService.isValidPriceExpression(priceExpression)) {
			setAttr("errorMessage", "价格运算表达式错误!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		promotion.setPriceExpression(priceExpression);
		promotion.setProducts(null);
		promotion.setProductCategories(null);
		promotionService.save(promotion);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long promotionId = getParaToLong("promotionId");
		Promotion promotion = promotionService.find(promotionId);
		Store currentStore = businessService.getCurrentStore();
		
		if (promotion == null) {
			setAttr("errorMessage", "促销为空!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		setAttr("promotion", promotion);
		setAttr("memberRanks", memberRankService.findAll());
		setAttr("coupons", couponService.findList(currentStore));
		render("/business/discount_promotion/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		Promotion promotion = getModel(Promotion.class);
		Boolean useAmountPromotion = getParaToBoolean("useAmountPromotion");
		Boolean useNumberPromotion = getParaToBoolean("useNumberPromotion");
		Long[] memberRankIds = getParaValuesToLong("memberRankIds");
		Long[] couponIds = getParaValuesToLong("couponIds");
		Long[] giftIds = getParaValuesToLong("giftIds");
		//Store currentStore = businessService.getCurrentStore();
		
		if (promotion == null) {
			setAttr("errorMessage", "促销为空!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		promotion.setMemberRanks(new ArrayList<>(memberRankService.findList(memberRankIds)));
		promotion.setCoupons(new ArrayList<>(couponService.findList(couponIds)));
		if (ArrayUtils.isNotEmpty(giftIds)) {
			List<Sku> gifts = skuService.findList(giftIds);
			CollectionUtils.filter(gifts, new Predicate() {
				@Override
				public boolean evaluate(Object object) {
					Sku gift = (Sku) object;
					return gift != null && Product.Type.gift.equals(gift.getType());
				}
			});
			promotion.setGifts(new ArrayList<>(gifts));
		} else {
			promotion.setGifts(null);
		}
		if (promotion.getBeginDate() != null && promotion.getEndDate() != null && promotion.getBeginDate().after(promotion.getEndDate())) {
			setAttr("errorMessage", "促销已过期!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (promotion.getMinimumQuantity() != null && promotion.getMaximumQuantity() != null && promotion.getMinimumQuantity() > promotion.getMaximumQuantity()) {
			setAttr("errorMessage", "促销最小数量错误!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (promotion.getMinimumPrice() != null && promotion.getMaximumPrice() != null && promotion.getMinimumPrice().compareTo(promotion.getMaximumPrice()) > 0) {
			setAttr("errorMessage", "促销最小单价错误!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		PromotionPlugin promotionPlugin = pluginService.getPromotionPlugin(DiscountPromotionPlugin.ID);
		String priceExpression = promotionPlugin.getPriceExpression(promotion, useAmountPromotion, useNumberPromotion);
		if (StringUtils.isNotEmpty(priceExpression) && !promotionService.isValidPriceExpression(priceExpression)) {
			setAttr("errorMessage", "价格运算表达式错误!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (useAmountPromotion != null && useAmountPromotion) {
			promotion.setConditionsNumber(null);
			promotion.setCreditNumber(null);
		} else if (useNumberPromotion != null && useNumberPromotion) {
			promotion.setConditionsAmount(null);
			promotion.setCreditAmount(null);
		} else {
			promotion.setConditionsNumber(null);
			promotion.setCreditNumber(null);
			promotion.setConditionsAmount(null);
			promotion.setCreditAmount(null);
		}
		promotion.setPriceExpression(priceExpression);

		promotionService.update(promotion);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		Store currentStore = businessService.getCurrentStore();
		
		PromotionPlugin promotionPlugin = pluginService.getPromotionPlugin(DiscountPromotionPlugin.ID);
		setAttr("isEnabled", promotionPlugin.getIsEnabled());
		setAttr("currentStore", currentStore);
		setAttr("pageable", pageable);
		setAttr("page", promotionService.findPage(currentStore, Promotion.Type.discount, pageable));
		render("/business/discount_promotion/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		Store currentStore = businessService.getCurrentStore();
		
		for (Long id : ids) {
			Promotion promotion = promotionService.find(id);
			if (promotion == null) {
				Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
				return;
			}
			if (!currentStore.equals(promotion.getStore())) {
				Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
				return;
			}
		}
		promotionService.delete(ids);
		renderJson(Results.OK);
	}

}