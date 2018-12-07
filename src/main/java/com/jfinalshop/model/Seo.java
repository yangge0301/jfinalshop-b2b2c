package com.jfinalshop.model;

import java.io.IOException;

import com.jfinalshop.model.base.BaseSeo;
import com.jfinalshop.util.FreeMarkerUtils;

import freemarker.core.Environment;
import freemarker.template.TemplateException;

/**
 * Model - SEO设置
 * 
 */
public class Seo extends BaseSeo<Seo> {
	private static final long serialVersionUID = 8405086780619131548L;
	public static final Seo dao = new Seo().dao();
	
	/**
	 * 类型
	 */
	public enum Type {

		/**
		 * 首页
		 */
		index,

		/**
		 * 文章列表
		 */
		articleList,

		/**
		 * 文章搜索
		 */
		articleSearch,

		/**
		 * 文章详情
		 */
		articleDetail,

		/**
		 * 商品列表
		 */
		productList,

		/**
		 * 商品搜索
		 */
		productSearch,

		/**
		 * 商品详情
		 */
		productDetail,

		/**
		 * 品牌列表
		 */
		brandList,

		/**
		 * 品牌详情
		 */
		brandDetail,

		/**
		 * 店铺首页
		 */
		storeIndex,

		/**
		 * 店铺搜索
		 */
		storeSearch
	}

	/**
	 * 类型名称
	 */
	public Type getTypeName() {
		return getType() != null ? Type.values()[getType()] : null;
	}
	
	/**
	 * 解析页面标题
	 * 
	 * @return 页面标题
	 */
	public String resolveTitle() {
		try {
			Environment environment = FreeMarkerUtils.getCurrentEnvironment();
			return FreeMarkerUtils.process(getTitle(), environment != null ? environment.getDataModel() : null);
		} catch (IOException e) {
			return null;
		} catch (TemplateException e) {
			return null;
		}
	}

	/**
	 * 解析页面关键词
	 * 
	 * @return 页面关键词
	 */
	public String resolveKeywords() {
		try {
			Environment environment = FreeMarkerUtils.getCurrentEnvironment();
			return FreeMarkerUtils.process(getKeywords(), environment != null ? environment.getDataModel() : null);
		} catch (IOException e) {
			return null;
		} catch (TemplateException e) {
			return null;
		}
	}

	/**
	 * 解析页面描述
	 * 
	 * @return 页面描述
	 */
	public String resolveDescription() {
		try {
			Environment environment = FreeMarkerUtils.getCurrentEnvironment();
			return FreeMarkerUtils.process(getDescription(), environment != null ? environment.getDataModel() : null);
		} catch (IOException e) {
			return null;
		} catch (TemplateException e) {
			return null;
		}
	}
}
