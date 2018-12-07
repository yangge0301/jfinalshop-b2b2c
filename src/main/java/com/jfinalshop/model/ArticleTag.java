package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BaseArticleTag;

/**
 * Model - 文章标签
 * 
 */
public class ArticleTag extends BaseArticleTag<ArticleTag> {
	private static final long serialVersionUID = 7696694880068755539L;
	public static final ArticleTag dao = new ArticleTag().dao();
	
	/**
	 * 文章
	 */
	private List<Article> articles = new ArrayList<Article>();
	
	/**
	 * 获取文章
	 * 
	 * @return 文章
	 */
	public List<Article> getArticles() {
		if (CollectionUtils.isEmpty(articles)) {
			String sql ="SELECT a.* FROM `article` a LEFT JOIN `article_article_tag` t ON a.`id` = t.`articles_id` WHERE t.`article_tags_id` = ?";
			articles = Article.dao.find(sql, getId());
		}
		return articles;
	}

	/**
	 * 设置文章
	 * 
	 * @param articles
	 *            文章
	 */
	public void setArticles(List<Article> articles) {
		this.articles = articles;
	}

	/**
	 * 删除前处理
	 */
	public void preRemove() {
		List<Article> articles = getArticles();
		if (articles != null) {
			for (Article article : articles) {
				article.getArticleTags().remove(this);
			}
		}
	}
	
	
}
