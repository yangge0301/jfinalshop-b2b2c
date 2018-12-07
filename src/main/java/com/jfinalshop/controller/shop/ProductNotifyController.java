package com.jfinalshop.controller.shop;

import java.util.HashMap;
import java.util.Map;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Results;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.ProductNotify;
import com.jfinalshop.model.Sku;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.ProductNotifyService;
import com.jfinalshop.service.SkuService;

/**
 * Controller - 到货通知
 * 
 */
@ControllerBind(controllerKey = "/product_notify")
public class ProductNotifyController extends BaseController {

	@Inject
	private ProductNotifyService productNotifyService;
	@Inject
	private SkuService skuService;
	@Inject
	private MemberService memberService;

	/**
	 * 获取当前会员E-mail
	 */
	public void email() {
		Member currentUser = memberService.getCurrentUser();
		
		String email = currentUser != null ? currentUser.getEmail() : null;
		Map<String, String> data = new HashMap<>();
		data.put("email", email);
		renderJson(data);
	}

	/**
	 * 保存
	 */
	public void save() {
		String email = getPara("email");
		Long skuId = getParaToLong("skuId");
		Sku sku = skuService.find(skuId);
		
		if (sku == null) {
			Results.unprocessableEntity(getResponse(), "shop.productNotify.skuNotExist");
			return;
		}
		if (!sku.getIsActive()) {
			Results.unprocessableEntity(getResponse(), "shop.productNotify.skuNotActive");
			return;
		}
		if (!sku.getIsMarketable()) {
			Results.unprocessableEntity(getResponse(), "shop.productNotify.skuNotMarketable");
			return;
		}
		if (!sku.getIsOutOfStock()) {
			Results.unprocessableEntity(getResponse(), "shop.productNotify.skuInStock");
			return;
		}
		if (productNotifyService.exists(sku, email)) {
			Results.unprocessableEntity(getResponse(), "shop.productNotify.exist");
			return;
		}
		
		Member currentUser = memberService.getCurrentUser();
		ProductNotify productNotify = new ProductNotify();
		productNotify.setEmail(email);
		productNotify.setHasSent(false);
		productNotify.setMemberId(currentUser != null ? currentUser.getId() : null);
		productNotify.setSkuId(sku.getId());
		productNotifyService.save(productNotify);
		renderJson(Results.OK);
	}

}