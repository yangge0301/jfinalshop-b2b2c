package com.jfinalshop.api.controller.member;

import java.util.HashMap;
import java.util.Map;

import net.hasor.core.Inject;

import org.apache.commons.lang.BooleanUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinalshop.Setting;
import com.jfinalshop.api.common.bean.DatumResponse;
import com.jfinalshop.api.controller.BaseAPIController;
import com.jfinalshop.api.interceptor.TokenInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.OrderItem;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.Review;
import com.jfinalshop.model.Sku;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.OrderItemService;
import com.jfinalshop.service.ProductService;
import com.jfinalshop.service.ReviewService;
import com.jfinalshop.service.SkuService;
import com.jfinalshop.util.IpUtil;
import com.jfinalshop.util.SystemUtils;

/**
 * 会员中心 - 评论
 * 
 */
@ControllerBind(controllerKey = "/api/member/review")
@Before(TokenInterceptor.class)
public class ReviewAPIController extends BaseAPIController {

	@Inject
	private ReviewService reviewService;
	@Inject
	private ProductService productService;
	@Inject
	private SkuService skuService;
	@Inject
	private MemberService memberService;
	@Inject
	private OrderItemService orderItemService;

	private Res res = I18n.use();
	
	/**
	 * 发表
	 */
	public void add() {
		Long skuId = getParaToLong("skuId");
		Long orderIitemId = getParaToLong("orderIitemId");
		
		Setting setting = SystemUtils.getSetting();
		if (!setting.getIsReviewEnabled()) {
			renderArgumentError("评论没有开启!");
			return;
		}
		
		Sku sku = skuService.find(skuId);
		if (sku == null) {
			renderArgumentError("商品为空!");
			return;
		}
		
		OrderItem orderItem = orderItemService.find(orderIitemId);
		if (orderItem == null) {
			renderArgumentError("订单行为空!");
			return;
		}
		
		Product product = sku.getProduct();
		if (product == null || BooleanUtils.isNotTrue(product.getIsActive()) || BooleanUtils.isNotTrue(product.getIsMarketable())) {
			renderArgumentError("商品为空或已下架!");
			return;
		}
		
		Map<String, Object> data = new HashMap<>();
		data.put("name", product.getName());
		data.put("image", product.getImage());
		data.put("orderIitemId", orderIitemId);
		renderJson(new DatumResponse(data));
	}
	
	/**
	 * 保存
	 */
	public void save() {
		Long skuId = getParaToLong("skuId");
		Integer score = getParaToInt("score");
		String content = getPara("content");
		Long orderIitemId = getParaToLong("orderIitemId");
		
		Member currentUser = getMember();
		
		Setting setting = SystemUtils.getSetting();
		if (!setting.getIsReviewEnabled()) {
			renderArgumentError("评论没有开启!");
			return;
		}
		
		OrderItem orderItem = orderItemService.find(orderIitemId);
		if (orderItem == null) {
			renderArgumentError("订单行为空!");
			return;
		}
		
		Sku sku = skuService.find(skuId);
		if (sku == null) {
			renderArgumentError("商品为空!");
			return;
		}
		
		Product product = sku.getProduct();
		if (product == null || BooleanUtils.isNotTrue(product.getIsActive()) || BooleanUtils.isNotTrue(product.getIsMarketable())) {
			renderArgumentError("商品为空或已下架!");
			return;
		}
		if (currentUser != null && !reviewService.hasPermission(currentUser, product)) {
			renderArgumentError("没有评论权限!");
			return;
		}

		Review review = new Review();
		review.setScore(score);
		review.setContent(content);
		review.setIp(IpUtil.getIpAddr(getRequest()));
		review.setMemberId(currentUser.getId());
		review.setProductId(product.getId());
		review.setStoreId(product.getStore().getId());
		if (setting.getIsReviewCheck()) {
			review.setIsShow(false);
			reviewService.save(review);
			orderItem.setIsReview(true);
			orderItemService.update(orderItem);
			renderJson(new DatumResponse(res.format("shop.review.check")));
		} else {
			review.setIsShow(true);
			reviewService.save(review);
			orderItem.setIsReview(true);
			orderItemService.update(orderItem);
			renderJson(new DatumResponse(res.format("shop.review.success")));
		}
	}
	
}
