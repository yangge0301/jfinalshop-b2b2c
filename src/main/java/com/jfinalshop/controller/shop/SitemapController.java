package com.jfinalshop.controller.shop;

import java.util.ArrayList;
import java.util.List;

import net.hasor.core.Inject;
import net.hasor.core.InjectSettings;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinalshop.entity.SitemapIndex;
import com.jfinalshop.entity.SitemapUrl;
import com.jfinalshop.entity.SitemapUrl.Type;
import com.jfinalshop.service.SitemapIndexService;
import com.jfinalshop.service.SitemapUrlService;


/**
 * Controller - Sitemap
 * 
 */
@ControllerBind(controllerKey = "/sitemap")
public class SitemapController extends BaseController {

	/**
	 * 最大Sitemap URL数量
	 */
	private static final Integer MAX_SITEMAP_URL_SIZE = 10000;

	/**
	 * 更新频率
	 */
	private static final SitemapUrl.Changefreq CHANGEFREQ = SitemapUrl.Changefreq.daily;

	/**
	 * 权重
	 */
	private static final float PRIORITY = 0.6F;

	@InjectSettings("${xml_content_type}")
	private String xmlContentType;

	@Inject
	private SitemapIndexService sitemapIndexService;
	@Inject
	private SitemapUrlService sitemapUrlService;

	/**
	 * 索引
	 */
	//@GetMapping("/index.xml")
	public void index() {
		List<SitemapIndex> sitemapIndexs = new ArrayList<>();
		sitemapIndexs.addAll(sitemapIndexService.generate(Type.article, MAX_SITEMAP_URL_SIZE));
		sitemapIndexs.addAll(sitemapIndexService.generate(Type.product, MAX_SITEMAP_URL_SIZE));
		setAttr("sitemapIndexs", sitemapIndexs);
		getResponse().setContentType(xmlContentType);
		render("/shop/sitemap/index.ftl");
	}

	/**
	 * URL
	 */
	//@GetMapping("/{type}/{index}.xml")
	public void url() {
		String typeName = getPara("type");
		SitemapUrl.Type type = StrKit.notBlank(typeName) ? SitemapUrl.Type.valueOf(typeName) : null;
		Integer index = getParaToInt("index");
		
		setAttr("sitemapUrls", sitemapUrlService.generate(type, CHANGEFREQ, PRIORITY, index * MAX_SITEMAP_URL_SIZE, MAX_SITEMAP_URL_SIZE));
		getResponse().setContentType(xmlContentType);
		render("/shop/sitemap/url");
	}

}