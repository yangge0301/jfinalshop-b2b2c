package com.jfinalshop.controller.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Filter;
import com.jfinalshop.model.Article;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.Store;
import com.jfinalshop.service.ArticleService;
import com.jfinalshop.service.ProductService;
import com.jfinalshop.service.SearchService;
import com.jfinalshop.service.StoreService;

/**
 * Controller - 生成索引
 * 
 */
@ControllerBind(controllerKey = "/admin/generate")
public class GenerateController extends BaseController {

	@Inject
	private SearchService searchService;
	@Inject
	private ArticleService articleService;
	@Inject
	private ProductService productService;
	@Inject
	private StoreService storeService;
	
	/**
	 * 生成类型
	 */
	public enum GenerateType {
		/**
		 * 文章
		 */
		article,

		/**
		 * 商品
		 */
		product,
		
		/**
		 * 店铺
		 */
		store
	}
	
	public void list() {
		setAttr("generateTypes", GenerateType.values());
		render("/admin/generate/list.ftl");
	}
	
	/**
	 * 生成索引
	 */
	public void index() {
		GenerateType generateType = getParaEnum(GenerateType.class, getPara("generateType"));
		Boolean isPurge = getParaToBoolean("isPurge");
		Integer first = getParaToInt("first");
		Integer count = getParaToInt("count");
		
		long startTime = System.currentTimeMillis();
		if (first == null || first < 0) {
			first = 0;
		}
		if (count == null || count <= 0) {
			count = 100;
		}
		int generateCount = 0;
		boolean isCompleted = true;
		List<Filter> filters = new ArrayList<Filter>();
		switch (generateType) {
		case article:
			if (first == 0 && isPurge != null && isPurge) {
				searchService.delArticleAll();
			}
			filters.add(Filter.eq("is_publication", true));
			List<Article> articleList = articleService.findList(first, count, null, null);
			generateCount = searchService.indexArticle(articleList);
			first += articleList.size();
			if (articleList.size() == count) {
				isCompleted = false;
			}
			break;
		case product:
			if (first == 0 && isPurge != null && isPurge) {
				searchService.delProductAll();
			}
			filters.add(Filter.eq("is_marketable", true));
			filters.add(Filter.eq("is_list", true));
			filters.add(Filter.eq("is_active", true));
			List<Product> productList = productService.findList(first, count, filters, null);
			generateCount = searchService.indexProduct(productList);
			first += productList.size();
			if (productList.size() == count) {
				isCompleted = false;
			}
			break;
		case store:
			if (first == 0 && isPurge != null && isPurge) {
				searchService.delStoreAll();
			}
			filters.add(Filter.eq("is_enabled", true));
			List<Store> storeList = storeService.findList(first, count, filters, null);
			generateCount = searchService.indexStore(storeList);
			first += storeList.size();
			if (storeList.size() == count) {
				isCompleted = false;
			}
			break;
		default :
			break;
		}
		long endTime = System.currentTimeMillis();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("first", first);
		data.put("generateCount", generateCount);
		data.put("generateTime", endTime - startTime);
		data.put("isCompleted", isCompleted);
		renderJson(data);
	}
	
	
}
