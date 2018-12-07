package com.jfinalshop.controller.shop;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.exception.ResourceNotFoundException;
import com.jfinalshop.model.Brand;
import com.jfinalshop.service.BrandService;

/**
 * Controller - 品牌
 * 
 */
@ControllerBind(controllerKey = "/brand")
public class BrandController extends BaseController {

	/**
	 * 每页记录数
	 */
	private static final int PAGE_SIZE = 40;

	@Inject
	private BrandService brandService;

	/**
	 * 列表
	 */
	public void list() {
		Integer pageNumber = getParaToInt(0);
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("pageable", pageable);
		setAttr("page", brandService.findPage(pageable));
		render("/shop/brand/list.ftl");
	}

	/**
	 * 详情
	 */
	public void detail() {
		Long brandId = getParaToLong(0);
		
		Brand brand = brandService.find(brandId);
		if (brand == null) {
			throw new ResourceNotFoundException();
		}
		setAttr("brand", brand);
		render("/shop/brand/detail.ftl");
	}

}