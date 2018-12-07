package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.ArticleTag;
import com.jfinalshop.service.ArticleTagService;

/**
 * Controller - 文章标签
 * 
 */
@ControllerBind(controllerKey = "/admin/article_tag")
public class ArticleTagController extends BaseController {

	@Inject
	private ArticleTagService articleTagService;

	/**
	 * 添加
	 */
	public void add() {
		render("/admin/article_tag/add.ftl");
	}

	/**
	 * 保存
	 */
	public void save() {
		ArticleTag articleTag = getModel(ArticleTag.class);
		
		articleTagService.save(articleTag);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("articleTag", articleTagService.find(id));
		render("/admin/article_tag/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		ArticleTag articleTag = getModel(ArticleTag.class);
		
		articleTagService.update(articleTag);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", articleTagService.findPage(pageable));
		render("/admin/article_tag/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		articleTagService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}

}