package com.jfinalshop.controller.business;

import java.util.List;

import net.hasor.core.Inject;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.UnauthorizedException;

import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.Results;
import com.jfinalshop.model.Coupon;
import com.jfinalshop.model.CouponCode;
import com.jfinalshop.model.Store;
import com.jfinalshop.service.BusinessService;
import com.jfinalshop.service.CouponCodeService;
import com.jfinalshop.service.CouponService;

/**
 * Controller - 优惠券
 * 
 */
@ControllerBind(controllerKey = "/business/coupon")
public class CouponController extends BaseController {

	@Inject
	private CouponService couponService;
	@Inject
	private CouponCodeService couponCodeService;
	@Inject
	private BusinessService businessService;

	/**
	 * 添加属性
	 */
	public void populateModel() {
		Long couponId = getParaToLong("couponId");
		Store currentStore = businessService.getCurrentStore();
		
		Coupon coupon = couponService.find(couponId);
		if (coupon != null && !currentStore.equals(coupon.getStore())) {
			throw new UnauthorizedException();
		}
		setAttr("coupon", coupon);
	}

	/**
	 * 检查价格运算表达式是否正确
	 */
	@ActionKey("/business/coupon/check_price_expression")
	public void checkPriceExpression() {
		String priceExpression = getPara("coupon.price_expression");
		if (StringUtils.isEmpty(priceExpression)) {
			renderJson(false);
			return;
		}
		renderJson(couponService.isValidPriceExpression(priceExpression));
	}

	/**
	 * 添加
	 */
	public void add() {
		render("/business/coupon/add.ftl");
	}

	/**
	 * 保存
	 */
	public void save() {
		Coupon coupon = getModel(Coupon.class);
		Store currentStore = businessService.getCurrentStore();
		
		if (coupon.getBeginDate() != null && coupon.getEndDate() != null && coupon.getBeginDate().after(coupon.getEndDate())) {
			setAttr("errorMessage", "优惠券过期!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (coupon.getMinimumQuantity() != null && coupon.getMaximumQuantity() != null && coupon.getMinimumQuantity() > coupon.getMaximumQuantity()) {
			setAttr("errorMessage", "优惠券最小数量错误!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (coupon.getMinimumPrice() != null && coupon.getMaximumPrice() != null && coupon.getMinimumPrice().compareTo(coupon.getMaximumPrice()) > 0) {
			setAttr("errorMessage", "优惠券最小金额错误!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (StringUtils.isNotEmpty(coupon.getPriceExpression()) && !couponService.isValidPriceExpression(coupon.getPriceExpression())) {
			setAttr("errorMessage", "价格运算表达式错误!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (coupon.getIsExchange() && coupon.getPoint() == null) {
			setAttr("errorMessage", "优惠券对应积分为空!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (!coupon.getIsExchange()) {
			coupon.setPoint(null);
		}
		coupon.setStoreId(currentStore.getId());
		couponService.save(coupon);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long couponId = getParaToLong("couponId");
		Coupon coupon = couponService.find(couponId);
		if (coupon == null) {
			setAttr("errorMessage", "优惠券为空!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		setAttr("coupon", coupon);
		render("/business/coupon/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		Coupon coupon = getModel(Coupon.class);
		
		if (coupon == null) {
			setAttr("errorMessage", "优惠券是空!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (coupon.getBeginDate() != null && coupon.getEndDate() != null && coupon.getBeginDate().after(coupon.getEndDate())) {
			setAttr("errorMessage", "优惠券过期!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (coupon.getMinimumQuantity() != null && coupon.getMaximumQuantity() != null && coupon.getMinimumQuantity() > coupon.getMaximumQuantity()) {
			setAttr("errorMessage", "优惠券最小数量错误!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (coupon.getMinimumPrice() != null && coupon.getMaximumPrice() != null && coupon.getMinimumPrice().compareTo(coupon.getMaximumPrice()) > 0) {
			setAttr("errorMessage", "优惠券最小金额错误!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (StringUtils.isNotEmpty(coupon.getPriceExpression()) && !couponService.isValidPriceExpression(coupon.getPriceExpression())) {
			setAttr("errorMessage", "价格运算表达式错误!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (coupon.getIsExchange() && coupon.getPoint() == null) {
			setAttr("errorMessage", "优惠券对应积分为空!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (!coupon.getIsExchange()) {
			coupon.setPoint(null);
		}

		couponService.update(coupon);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		Store currentStore = businessService.getCurrentStore();
		
		setAttr("pageable", pageable);
		setAttr("page", couponService.findPage(currentStore, pageable));
		render("/business/coupon/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		Store currentStore = businessService.getCurrentStore();
		for (Long id : ids) {
			Coupon coupon = couponService.find(id);
			if (coupon != null && currentStore.equals(coupon.getStore())) {
				couponService.delete(coupon);
			}
		}
		renderJson(Results.OK);
	}

	/**
	 * 生成优惠码
	 */
	public void generate() {
		Long couponId = getParaToLong("couponId");
		Coupon coupon = couponService.find(couponId);
		
		if (coupon == null) {
			setAttr("errorMessage", "优惠券是空!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		setAttr("coupon", coupon);
		setAttr("totalCount", couponCodeService.count(coupon, null, null, null, null));
		setAttr("usedCount", couponCodeService.count(coupon, null, null, null, true));
		render("/business/coupon/generate.ftl");
	}

	/**
	 * 下载优惠码
	 */
	public void download() {
		Long couponId = getParaToLong("couponId");
		Integer count = getParaToInt("count");
		
		Coupon coupon = couponService.find(couponId);
		if (coupon == null) {
			setAttr("errorMessage", "优惠券是空!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (count == null || count <= 0) {
			count = 100;
		}

		List<CouponCode> couponCodes = couponCodeService.generate(coupon, null, count);
		setAttr("couponCodes", couponCodes);
		render("/business/coupon_code/list.ftl");
	}
	
	/**
	 * 查看优惠码
	 */
	public void view() {
		Pageable pageable = getBean(Pageable.class);
		Long couponId = getParaToLong("couponId");
		Coupon coupon = couponService.find(couponId);
		Store currentStore = businessService.getCurrentStore();
		
		setAttr("pageable", pageable);
		setAttr("couponCodes", couponCodeService.findPage(coupon, currentStore, pageable).getList());
		render("/business/coupon_code/list.ftl");
		
	}

}