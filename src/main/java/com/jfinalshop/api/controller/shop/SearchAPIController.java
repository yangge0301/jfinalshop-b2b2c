package com.jfinalshop.api.controller.shop;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import net.hasor.core.Inject;

import org.apache.commons.collections.CollectionUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinalshop.Pageable;
import com.jfinalshop.api.common.bean.DataResponse;
import com.jfinalshop.api.controller.BaseAPIController;
import com.jfinalshop.api.interceptor.AccessInterceptor;
import com.jfinalshop.entity.ProductVO;
import com.jfinalshop.model.Product;
import com.jfinalshop.service.PromotionService;
import com.jfinalshop.service.SearchService;
import com.ld.zxw.page.Page;

/**
 * 
 * 搜索
 *
 */
@ControllerBind(controllerKey = "/api/search")
@Before(AccessInterceptor.class)
public class SearchAPIController extends BaseAPIController {

	@Inject
	private SearchService searchService;
	@Inject
	private PromotionService promotionService;
	
	/**
	 * 搜索
	 */
	public void index() {
		String keyword = getPara("keyword");
		
		if (StrKit.isBlank(keyword)) {
			renderArgumentError("搜索内容不能为空!");
			return;
		}
		
		Pageable pageable = new Pageable();
		Page<ProductVO> pVO = searchService.search(keyword, null, null, null, null, pageable);
		List<Product> products = new ArrayList<Product>();
		if (CollectionUtils.isNotEmpty(pVO.getList())) {
			for (ProductVO product : pVO.getList()) {
				Product pProduct = new Product();
				pProduct.setId(product.getId());
				pProduct.setName(product.getName());
				pProduct.setCaption(product.getCaption());
				pProduct.setPrice(new BigDecimal(product.getPrice()));
				pProduct.setMarketPrice(new BigDecimal(product.getMarketPrice()));
				pProduct.setImage(product.getImage());
				pProduct.setUnit(product.getUnit());
				pProduct.setWeight(product.getWeight());
				pProduct.put("brand", product.getBrand());
				products.add(pProduct);
			}
		}
		renderJson(new DataResponse(products));
	}
	
	
}
