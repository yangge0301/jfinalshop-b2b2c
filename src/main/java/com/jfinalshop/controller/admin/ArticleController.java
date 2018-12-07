package com.jfinalshop.controller.admin;

import java.util.ArrayList;
import java.util.List;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Article;
import com.jfinalshop.model.ArticleArticleTag;
import com.jfinalshop.model.ArticleCategory;
import com.jfinalshop.model.ArticleTag;
import com.jfinalshop.service.ArticleCategoryService;
import com.jfinalshop.service.ArticleService;
import com.jfinalshop.service.ArticleTagService;
import com.xiaoleilu.hutool.util.CollectionUtil;

/**
 * Controller - 文章
 * 
 */
@ControllerBind(controllerKey = "/admin/article")
public class ArticleController extends BaseController {

	@Inject
	private ArticleService articleService;
	@Inject
	private ArticleCategoryService articleCategoryService;
	@Inject
	private ArticleTagService articleTagService;

	/**
	 * 添加
	 */
	public void add() {
		setAttr("articleCategoryTree", articleCategoryService.findTree());
		setAttr("articleTags", articleTagService.findAll());
		render("/admin/article/add.ftl");
	}

	/**
	 * 保存
	 */
	@Before(Tx.class)
	public void save() {
		Article article = getModel(Article.class);
		Long articleCategoryId = getParaToLong("articleCategoryId");
		Long[] articleTagIds = getParaValuesToLong("articleTagIds");
		Boolean isPublication = getParaToBoolean("isPublication", false);
		Boolean isTop = getParaToBoolean("isTop", false);
		
		ArticleCategory articleCategory = articleCategoryService.find(articleCategoryId);
		if (articleCategory != null) {
			article.setArticleCategoryId(articleCategory.getId());
		}
		
		article.setIsPublication(isPublication);
		article.setIsTop(isTop);
		article.setArticleTags(new ArrayList<>(articleTagService.findList(articleTagIds)));
		article.setHits(0L);
		articleService.save(article);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("articleCategoryTree", articleCategoryService.findTree());
		setAttr("articleTags", articleTagService.findAll());
		setAttr("article", articleService.find(id));
		render("/admin/article/edit.ftl");
	}

	/**
	 * 更新
	 */
	@Before(Tx.class)
	public void update() {
		Article article = getModel(Article.class);
		Long articleCategoryId = getParaToLong("articleCategoryId");
		Long[] articleTagIds = getParaValuesToLong("articleTagIds");
		Boolean isPublication = getParaToBoolean("isPublication", false);
		Boolean isTop = getParaToBoolean("isTop", false);
		
		ArticleCategory articleCategory = articleCategoryService.find(articleCategoryId);
		if (articleCategory != null) {
			article.setArticleCategoryId(articleCategory.getId());
		}
		article.setIsPublication(isPublication);
		article.setIsTop(isTop);
		articleService.update(article, "hits");
		
		//先清除，再保存
		Db.deleteById("article_article_tag", "articles_id", article.getId());
		List<ArticleTag> articleTags = articleTagService.findList(articleTagIds);
		if (CollectionUtil.isNotEmpty(articleTags)) {
			for (ArticleTag articleTag : articleTags) {
				ArticleArticleTag articleArticleTag = new ArticleArticleTag();
				articleArticleTag.setArticlesId(article.getId());
				articleArticleTag.setArticleTagsId(articleTag.getId());
				articleArticleTag.save();
			}
		}
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", articleService.findPage(pageable));
		render("/admin/article/list.ftl");
	}

	/**
	 * 删除
	 */
	@Before(Tx.class)
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids != null) {
			for (Long id : ids) {
				Db.deleteById("article_article_tag", "articles_id", id);
			}
		}
		articleService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}

}