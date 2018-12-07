package com.jfinalshop.model;

import com.jfinalshop.model.base.BaseArticleArticleTag;

/**
 * Model - 文章文章标签中间表
 * 
 */
public class ArticleArticleTag extends BaseArticleArticleTag<ArticleArticleTag> {
	private static final long serialVersionUID = 7552415049658357304L;
	public static final ArticleArticleTag dao = new ArticleArticleTag().dao();
}
