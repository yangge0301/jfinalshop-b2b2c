package com.jfinalshop.controller.shop;

import java.util.HashMap;
import java.util.Map;

import net.hasor.core.Inject;

import org.apache.commons.lang.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.exception.ResourceNotFoundException;
import com.jfinalshop.interceptor.MobileInterceptor;
import com.jfinalshop.model.Article;
import com.jfinalshop.model.ArticleCategory;
import com.jfinalshop.service.ArticleCategoryService;
import com.jfinalshop.service.ArticleService;
import com.jfinalshop.service.SearchService;

/**
 * Controller - 文章
 * 
 */
@ControllerBind(controllerKey = "/article")
public class ArticleController extends BaseController {

	/**
	 * 每页记录数
	 */
	private static final int PAGE_SIZE = 20;

	@Inject
	private ArticleService articleService;
	@Inject
	private ArticleCategoryService articleCategoryService;
	@Inject
	private SearchService searchService;

	/**
	 * 详情
	 */
	@Before(MobileInterceptor.class)
	public void detail() {
		Long articleId = getParaToLong(0);
		Integer pageNumber = getParaToInt(1);
		
		Article article = articleService.find(articleId);
		if (article == null || pageNumber < 1 || pageNumber > article.getTotalPages()) {
			throw new ResourceNotFoundException();
		}
		setAttr("article", article);
		setAttr("pageNumber", pageNumber);
		render("/shop/article/detail.ftl");
	}

	/**
	 * 列表
	 */
	@Before(MobileInterceptor.class)
	public void list() {
		Long articleCategoryId = getParaToLong(0);
		Integer pageNumber = getParaToInt(1);
		
		ArticleCategory articleCategory = articleCategoryService.find(articleCategoryId);
		if (articleCategory == null) {
			throw new ResourceNotFoundException();
		}
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("articleCategory", articleCategory);
		setAttr("pageable", pageable);
		setAttr("page", articleService.findPage(articleCategory, null, true, pageable));
		render("/shop/article/list.ftl");
	}

	/**
	 * 列表
	 */
//	@GetMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
//	@JsonView(BaseEntity.BaseView.class)
//	public ResponseEntity<?> list(Long articleCategoryId, Integer pageNumber) {
//		ArticleCategory articleCategory = articleCategoryService.find(articleCategoryId);
//		if (articleCategory == null) {
//			return Results.NOT_FOUND;
//		}
//
//		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
//		return ResponseEntity.ok(articleService.findPage(articleCategory, null, true, pageable).getContent());
//	}

	/**
	 * 搜索
	 */
	@Before(MobileInterceptor.class)
	public void search() {
		String keyword = getPara("keyword");
		Integer pageNumber = getParaToInt("pageNumber");
		
		if (StringUtils.isEmpty(keyword)) {
			setAttr("errorMessage", "关键字不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("articleKeyword", keyword);
		setAttr("pageable", pageable);
		setAttr("page", searchService.search(keyword, pageable));
		render("/shop/article/search.ftl");
	}

	/**
	 * 搜索
	 */
//	@GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
//	@JsonView(BaseEntity.BaseView.class)
//	public ResponseEntity<?> search(String keyword, Integer pageNumber) {
//		if (StringUtils.isEmpty(keyword)) {
//			return Results.UNPROCESSABLE_ENTITY;
//		}
//		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
//		return ResponseEntity.ok(searchService.search(keyword, pageable).getContent());
//	}

	/**
	 * 点击数
	 */
	public void hits() {
		Long articleId = getParaToLong(0);
		Map<String, Object> data = new HashMap<>();
		data.put("hits", articleService.viewHits(articleId));
		renderJson(data);
	}

}