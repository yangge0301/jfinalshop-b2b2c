package com.jfinalshop.controller.admin;

import java.util.List;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Message;
import com.jfinalshop.model.Article;
import com.jfinalshop.model.ArticleCategory;
import com.jfinalshop.service.ArticleCategoryService;

/**
 * Controller - 文章分类
 * 
 */
@ControllerBind(controllerKey = "/admin/article_category")
public class ArticleCategoryController extends BaseController {

	@Inject
	private ArticleCategoryService articleCategoryService;

	/**
	 * 添加
	 */
	public void add() {
		setAttr("articleCategoryTree", articleCategoryService.findTree());
		render("/admin/article_category/add.ftl");;
	}

	/**
	 * 保存
	 */
	@Before(Tx.class)
	public void save() {
		ArticleCategory articleCategory = getModel(ArticleCategory.class);
		
		Long parentId = getParaToLong("parentId");
		ArticleCategory pArticleCategory = articleCategoryService.find(parentId);
		if (pArticleCategory != null) {
			articleCategory.setParentId(pArticleCategory.getId());
		}
		
		articleCategoryService.save(articleCategory);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		ArticleCategory articleCategory = articleCategoryService.find(id);
		setAttr("articleCategoryTree", articleCategoryService.findTree());
		setAttr("articleCategory", articleCategory);
		setAttr("children", articleCategoryService.findChildren(articleCategory, true, null));
		render("/admin/article_category/edit.ftl");
	}

	/**
	 * 更新
	 */
	@Before(Tx.class)
	public void update() {
		ArticleCategory articleCategory = getModel(ArticleCategory.class);
		Long parentId = getParaToLong("parentId");
		
		ArticleCategory pArticleCategory = articleCategoryService.find(parentId);
		articleCategory.setParentId(pArticleCategory != null ? pArticleCategory.getId() : null);
		
		if (articleCategory.getParent() != null) {
			ArticleCategory parent = articleCategory.getParent();
			if (parent.getId().equals(articleCategory.getId())) {
				setAttr("errorMessage", "当前分类与上级分类相同!");
				render(ERROR_VIEW);
				return;
			}
			List<ArticleCategory> children = articleCategoryService.findChildren(parent, true, null);
			if (children != null && children.contains(parent)) {
				setAttr("errorMessage", "当前分类已存在上级分类中!");
				render(ERROR_VIEW);
				return;
			}
		}
		articleCategoryService.update(articleCategory, "treePath", "grade");
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 列表
	 */
	public void list() {
		setAttr("articleCategoryTree", articleCategoryService.findTree());
		render("/admin/article_category/list.ftl");;
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long id = getParaToLong("id");
		ArticleCategory articleCategory = articleCategoryService.find(id);
		if (articleCategory == null) {
			renderJson(ERROR_MESSAGE);
			return;
		}
		List<ArticleCategory> children = articleCategory.getChildren();
		if (children != null && !children.isEmpty()) {
			renderJson(Message.error("admin.articleCategory.deleteExistChildrenNotAllowed"));
			return;
		}
		List<Article> articles = articleCategory.getArticles();
		if (articles != null && !articles.isEmpty()) {
			renderJson(Message.error("admin.articleCategory.deleteExistArticleNotAllowed"));
			return;
		}
		articleCategoryService.delete(id);
		renderJson(SUCCESS_MESSAGE);
	}

}