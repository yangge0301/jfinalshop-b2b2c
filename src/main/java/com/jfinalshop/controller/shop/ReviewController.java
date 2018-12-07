package com.jfinalshop.controller.shop;

import java.util.ArrayList;
import java.util.List;

import net.hasor.core.Inject;

import org.apache.commons.lang.BooleanUtils;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.Results;
import com.jfinalshop.Setting;
import com.jfinalshop.exception.ResourceNotFoundException;
import com.jfinalshop.interceptor.MobileInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.Review;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.ProductService;
import com.jfinalshop.service.ReviewService;
import com.jfinalshop.shiro.core.SubjectKit;
import com.jfinalshop.util.IpUtil;
import com.jfinalshop.util.SystemUtils;
import com.xiaoleilu.hutool.util.CollectionUtil;

/**
 * Controller - 评论
 * 
 */
@ControllerBind(controllerKey = "/review")
public class ReviewController extends BaseController {

	/**
	 * 每页记录数
	 */
	private static final int PAGE_SIZE = 10;
	

	@Inject
	private ReviewService reviewService;
	@Inject
	private ProductService productService;
	@Inject
	private MemberService memberService;

	/**
	 * 列表
	 */
	@ActionKey("/review/m_list")
	public void mList() {
		Long productId = getParaToLong("productId");
		Integer pageNumber = getParaToInt("pageNumber");
		
		Product product = productService.find(productId);
		if (product == null || BooleanUtils.isNotTrue(product.getIsActive()) || BooleanUtils.isNotTrue(product.getIsMarketable())) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}

		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		Page<Review> pages = reviewService.findPage(null, product, null, null, true, pageable);
		
		List<Review> reviews = new ArrayList<Review>();
		if (CollectionUtil.isNotEmpty(pages.getList())) {
			for (Review review : pages.getList()) {
				review.put("member", review.getMember());
				review.put("replyReviews", review.getReplyReviews());
				reviews.add(review);
			}
		}
		renderJson(reviews);
	}

	/**
	 * 发表
	 */
	@Before(MobileInterceptor.class)
	public void add() {
		Long productId = getParaToLong(0);
		
		Setting setting = SystemUtils.getSetting();
		if (!setting.getIsReviewEnabled()) {
			throw new ResourceNotFoundException();
		}
		Product product = productService.find(productId);
		if (product == null || BooleanUtils.isNotTrue(product.getIsActive()) || BooleanUtils.isNotTrue(product.getIsMarketable())) {
			throw new ResourceNotFoundException();
		}

		setAttr("product", product);
		render("/shop/review/add.ftl");
	}

	/**
	 * 详情
	 */
	public void detail() {
		Long productId = getParaToLong(0);
		Review.Type type = getParaEnum(Review.Type.class, getPara("type"));
		Integer pageNumber = getParaToInt("pageNumber", 1);
		
		Setting setting = SystemUtils.getSetting();
		if (!setting.getIsReviewEnabled()) {
			throw new ResourceNotFoundException();
		}
		Product product = productService.find(productId);
		if (product == null || BooleanUtils.isNotTrue(product.getIsActive()) || BooleanUtils.isNotTrue(product.getIsMarketable())) {
			throw new ResourceNotFoundException();
		}

		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("type", type);
		setAttr("types", Review.Type.values());
		setAttr("product", product);
		setAttr("pageable", pageable);
		setAttr("page", reviewService.findPage(null, product, product.getStore(), type, true, pageable));
		render("/shop/review/detail.ftl");
	}

	/**
	 * 保存
	 * 
	 */
	public void save() {
		Long productId = getParaToLong("productId");
		Integer score = getParaToInt("score");
		String content = getPara("content");
		String captcha = getPara("captcha");
		Member currentUser = memberService.getCurrentUser();
		
		if (!SubjectKit.doCaptcha("captcha", captcha)) {
			renderJson(Kv.by(MESSAGE, "验证码输入错误!"));
			return;
		}
		
		Setting setting = SystemUtils.getSetting();
		if (!setting.getIsReviewEnabled()) {
			Results.unprocessableEntity(getResponse(), "shop.review.disabled");
			return;
		}
		Product product = productService.find(productId);
		if (product == null || BooleanUtils.isNotTrue(product.getIsActive()) || BooleanUtils.isNotTrue(product.getIsMarketable())) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		if (currentUser != null && !reviewService.hasPermission(currentUser, product)) {
			Results.unprocessableEntity(getResponse(), "shop.review.noPermission");
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
			Results.ok(getResponse(), "shop.review.check");
		} else {
			review.setIsShow(true);
			reviewService.save(review);
			Results.ok(getResponse(), "shop.review.success");
		}
	}

}