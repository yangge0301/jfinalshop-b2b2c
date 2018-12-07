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
import com.jfinalshop.model.Coupon;
import com.jfinalshop.model.CouponCode;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Store;
import com.jfinalshop.service.CouponCodeService;
import com.jfinalshop.service.CouponService;
import com.jfinalshop.service.MemberService;
import com.xiaoleilu.hutool.util.CollectionUtil;

/**
 * Controller - 优惠码
 * 
 */
@ControllerBind(controllerKey = "/member/coupon_code")
public class CouponCodeController extends BaseController {

	/**
	 * 每页记录数
	 */
	private static final int PAGE_SIZE = 10;
	@Inject
	private CouponService couponService;
	@Inject
	private CouponCodeService couponCodeService;
	@Inject
	private MemberService memberService;

	/**
	 * 添加属性
	 */
	public void populateModel() {
		Long couponId = getParaToLong("couponId");
		setAttr("coupon", couponService.find(couponId));
	}

	/**
	 * 兑换
	 */
	@Before(MobileInterceptor.class)
	public void exchange() {
		Integer pageNumber = getParaToInt("pageNumber");
		
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		
		setAttr("pageable", pageable);
		setAttr("page", couponService.findPage(true, true, false, pageable));
		render("/member/coupon_code/exchange.ftl");
	}

	/**
	 * 兑换
	 */
	@ActionKey("/member/coupon_code/save_exchange")
	public void saveExchange() {
		Coupon coupon = getModel(Coupon.class);
		Member currentUser = memberService.getCurrentUser();
		
		if (coupon == null) {
			Results.notFound(getResponse(), Results.DEFAULT_NOT_FOUND_MESSAGE);
			return;
		}

		if (!coupon.getIsEnabled() || !coupon.getIsExchange() || coupon.hasExpired()) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		if (currentUser.getPoint() < coupon.getPoint()) {
			Results.unprocessableEntity(getResponse(), "member.couponCode.point");
			return;
		}
		couponCodeService.exchange(coupon, currentUser);
		renderJson(Results.OK);
	}

	/**
	 * 列表
	 */
	@Before(MobileInterceptor.class)
	public void list() {
		Integer pageNumber = getParaToInt("pageNumber");
		Member currentUser = getModel(Member.class); 
		
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("page", couponCodeService.findPage(currentUser, null, pageable));
		render("/member/coupon_code/list.ftl");
	}

	/**
	 * 列表
	 */
	@ActionKey("/member/coupon_code/m_list")
	public void mList() {
		Integer pageNumber = getParaToInt("pageNumber", 1);
		Member currentUser = memberService.getCurrentUser();
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		
		Page<CouponCode> pages = couponCodeService.findPage(currentUser, null, pageable);
		
		List<CouponCode> couponCodes = new ArrayList<CouponCode>();
		if (CollectionUtil.isNotEmpty(pages.getList())) {
			for (CouponCode couponCode : pages.getList()) {
				Coupon coupon = couponCode.getCoupon();
				Store store = coupon.getStore();
				store.put("path", store.getPath());
				coupon.put("store", store);
				couponCode.put("coupon", coupon);
				couponCodes.add(couponCode);
			}
		}
		renderJson(couponCodes);
	}

}