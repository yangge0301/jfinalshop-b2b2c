package com.jfinalshop.controller.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.Store;
import com.jfinalshop.service.BusinessService;
import com.jfinalshop.service.ProductService;

/**
 * Controller - 商品排名
 * 
 */
@ControllerBind(controllerKey = "/business/product_ranking")
public class ProductRankingController extends BaseController {

	/**
	 * 默认排名类型
	 */
	private static final Product.RankingType DEFAULT_RANKING_TYPE = Product.RankingType.score;

	/**
	 * 默认数量
	 */
	private static final int DEFAULT_SIZE = 10;

	@Inject
	private ProductService productService;
	@Inject
	private BusinessService businessService;

	/**
	 * 列表
	 */
	public void list() {
		setAttr("rankingTypes", Product.RankingType.values());
		setAttr("rankingType", DEFAULT_RANKING_TYPE);
		setAttr("count", DEFAULT_SIZE);
		render("/business/product_ranking/list.ftl");
	}

	/**
	 * 数据
	 */
	public void data() {
		Product.RankingType rankingType = getParaEnum(Product.RankingType.class, getPara("rankingType"));
		Integer size = getParaToInt("count");
		Store currentStore = businessService.getCurrentStore();
		
		if (rankingType == null) {
			rankingType = DEFAULT_RANKING_TYPE;
		}
		if (size == null) {
			size = DEFAULT_SIZE;
		}
		List<Map<String, Object>> data = new ArrayList<>();
		for (Product product : productService.findList(rankingType, currentStore, size)) {
			Object value = null;
			switch (rankingType) {
			case score:
				value = product.getScore();
				break;
			case scoreCount:
				value = product.getScoreCount();
				break;
			case weekHits:
				value = product.getWeekHits();
				break;
			case monthHits:
				value = product.getMonthHits();
				break;
			case hits:
				value = product.getHits();
				break;
			case weekSales:
				value = product.getWeekSales();
				break;
			case monthSales:
				value = product.getMonthSales();
				break;
			case sales:
				value = product.getSales();
				break;
			default:
				break;
			}
			Map<String, Object> item = new HashMap<>();
			item.put("name", product.getName());
			item.put("value", value);
			data.add(item);
		}
		renderJson(data);
	}

}