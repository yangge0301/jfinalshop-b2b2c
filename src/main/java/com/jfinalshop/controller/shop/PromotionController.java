package com.jfinalshop.controller.shop;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.exception.ResourceNotFoundException;
import com.jfinalshop.model.Promotion;
import com.jfinalshop.service.PromotionService;

/**
 * Controller - 促销
 * 
 */
@ControllerBind(controllerKey = "/promotion")
public class PromotionController extends BaseController {

	@Inject
	private PromotionService promotionService;

	/**
	 * 详情
	 */
	public void detail() {
		Long promotionId = getParaToLong(0);
		Promotion promotion = promotionService.find(promotionId);
		if (promotion == null) {
			throw new ResourceNotFoundException();
		}
		setAttr("promotion", promotion);
		render("/shop/promotion/detail.ftl");
	}

}