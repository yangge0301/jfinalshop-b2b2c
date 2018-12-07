package com.jfinalshop.api.controller.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.hasor.core.Inject;

import com.alibaba.fastjson.JSONArray;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Filter;
import com.jfinalshop.Setting;
import com.jfinalshop.api.common.bean.DatumResponse;
import com.jfinalshop.api.controller.BaseAPIController;
import com.jfinalshop.api.interceptor.AccessInterceptor;
import com.jfinalshop.model.Ad;
import com.jfinalshop.model.AdPosition;
import com.jfinalshop.model.Product;
import com.jfinalshop.service.AdPositionService;
import com.jfinalshop.service.ProductCategoryService;
import com.jfinalshop.service.ProductService;
import com.jfinalshop.util.SystemUtils;

/**
 * 移动API - 首页
 *
 */
@ControllerBind(controllerKey = "/api/index")
@Before(AccessInterceptor.class)
public class IndexAPIController extends BaseAPIController {

	@Inject
	private AdPositionService adPositionService;
	@Inject
	private ProductCategoryService productCategoryService;
	@Inject
	private ProductService productService;
	
	private Setting setting = SystemUtils.getSetting();
	
	/**
	 * 主页
	 */
	public void index() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		// 热门搜索
		map.put("hotSearch", setting.getHotSearches());
				
		// 轮播广告位
		AdPosition bannerAdPosition = adPositionService.find(1L);
		List<Ad> bannerAds = bannerAdPosition.getAds();
		map.put("bannerAds", bannerAds);
		
		// 移动首页 - 分栏菜单
		AdPosition middleAdPosition = adPositionService.find(7L);
		List<Ad> middleAds = middleAdPosition.getAds();
		map.put("middleAds", middleAds);
		
		// 移动首页 - 底部菜单
		AdPosition bottomAdPosition = adPositionService.find(8L);
		List<Ad> bottomAds = bottomAdPosition.getAds();
		map.put("bottomAds", bottomAds);
		
		// 产品分类（下级显示5个商品）
		JSONArray productCategoryArray = productCategoryService.getProductCategoryArray(5);
		map.put("productCategory", productCategoryArray);
		
		// 猜你喜欢
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(Filter.eq("is_marketable", true));
		filters.add(Filter.eq("is_top", true));
		List<Product> extra = productService.findList(0, 6, filters, null);
		map.put("extra", convertProduct(extra));
		renderJson(new DatumResponse(map));
	}
	
}
