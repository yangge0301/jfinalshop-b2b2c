package com.jfinalshop.controller.member;

import java.util.ArrayList;
import java.util.List;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.Results;
import com.jfinalshop.interceptor.MobileInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.Review;
import com.jfinalshop.model.Store;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.ReviewService;
import com.xiaoleilu.hutool.util.CollectionUtil;

/**
 * Controller - 评论
 * 
 */
@ControllerBind(controllerKey = "/member/review")
public class ReviewController extends BaseController {

	/**
	 * 每页记录数
	 */
	private static final int PAGE_SIZE = 10;

	@Inject
	private ReviewService reviewService;
	@Inject
	private MemberService memberService;

	/**
	 * 列表
	 */
	@Before(MobileInterceptor.class)
	public void list() {
		Integer pageNumber = getParaToInt("pageNumber");
		Member currentUser = memberService.getCurrentUser();
		
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("pageable", pageable);
		setAttr("page", reviewService.findPage(currentUser, null, null, null, null, pageable));
		render("/member/review/list.ftl");
	}

	/**
	 * 列表
	 */
	@ActionKey("/member/review/m_list")
	public void mList() {
		Integer pageNumber = getParaToInt("pageNumber", 1);
		Member currentUser = memberService.getCurrentUser();
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		Page<Review> pages = reviewService.findPage(currentUser, null, null, null, null, pageable);
		
		List<Review> reviews = new ArrayList<Review>();
		if (CollectionUtil.isNotEmpty(pages.getList())) {
			for (Review review : pages.getList()) {
				Product product = review.getProduct();
				product.put("type", product.getTypeName());
				product.put("thumbnail", product.getThumbnail());
				Store store = product.getStore();
				store.put("type", store.getTypeName());
				product.put("store", store);
				product.put("defaultSku", product.getDefaultSku());
				product.put("path", product.getPath());
				review.put("product", product);
				reviews.add(review);
			}
		}
		renderJson(reviews);
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long id = getParaToLong("id");
		Member currentUser = memberService.getCurrentUser();
		
		if (id == null) {
			Results.notFound(getResponse(), Results.DEFAULT_NOT_FOUND_MESSAGE);
			return;
		}
		Review review = reviewService.find(id);
		if (review == null || !currentUser.equals(review.getMember())) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		reviewService.delete(review);
		renderJson(Results.OK);
	}

}