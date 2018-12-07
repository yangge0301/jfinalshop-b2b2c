package com.jfinalshop.api.controller.member;

import java.util.ArrayList;
import java.util.List;

import net.hasor.core.Inject;

import org.apache.commons.collections.CollectionUtils;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.api.common.bean.DataResponse;
import com.jfinalshop.api.common.bean.DatumResponse;
import com.jfinalshop.api.controller.BaseAPIController;
import com.jfinalshop.api.interceptor.TokenInterceptor;
import com.jfinalshop.model.Cart;
import com.jfinalshop.model.Coupon;
import com.jfinalshop.model.CouponCode;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Store;
import com.jfinalshop.service.CartService;
import com.jfinalshop.service.CouponCodeService;

/**
 * 会员中心 - 优惠码
 * 
 */
@ControllerBind(controllerKey = "/api/member/coupon_code")
@Before(TokenInterceptor.class)
public class CouponCodeAPIController extends BaseAPIController {
	
	@Inject
	CouponCodeService couponCodeService;
	@Inject
	protected CartService cartService;
	
	/** 默认分页大小 */
	private static final int PAGE_SIZE = 10;
	
	private Res res = I18n.use();
	
	/**
	 * 我的优惠券列表
	 */
	public void list() {
		Integer pageNumber = getParaToInt("pageNumber", 1);
		Integer pageSize = getParaToInt("pageSize", PAGE_SIZE);
		Boolean isUsed = getParaToBoolean("isUsed");
		
		Pageable pageable = new Pageable(pageNumber, pageSize);
		Member member = getMember();
		Page<CouponCode> page = couponCodeService.findPage(member, isUsed, pageable);
		DatumResponse data = new DatumResponse();
		data.setDatum(page);
		renderJson(data);
	}
	
	/**
	 * 用户可用优惠券
	 * 
	 */
	@ActionKey("/api/member/coupon_code/valid_coupons")
	public void validCoupons() {
		String cartKey = getPara("cartKey");
		Cart currentCart = cartService.getCurrent(cartKey);
		
		if (currentCart == null) {
			renderArgumentError(res.format("shop.cart.notEmpty"));
			return;
		}
		Member member = getMember();
		Pageable pageable = new Pageable();
		Page<CouponCode> page = couponCodeService.findPage(member, false, pageable);
		List<CouponCode> couponCodes = new ArrayList<CouponCode>();
		
		if (CollectionUtils.isNotEmpty(page.getList())) {
			for (CouponCode couponCode : page.getList()) {
				Coupon coupon = couponCode.getCoupon();
				Store store = coupon.getStore();
				
				if (!coupon.getIsEnabled()) {
					continue;
				}
				if (!coupon.hasBegun()) {
					continue;
				}
				if (coupon.hasExpired()) {
					continue;
				}
				if (!currentCart.isValid(coupon, store)) {
					continue;
				}
				if (currentCart.getStores().contains(store) && !currentCart.isCouponAllowed(store)) {
					continue;
				}
				couponCode.put("begin_date", coupon.getBeginDate());
				couponCode.put("end_date", coupon.getEndDate());
				couponCode.put("name", coupon.getName());
				couponCodes.add(couponCode);
			}
		}
		renderJson(new DataResponse(couponCodes));
	}

}
