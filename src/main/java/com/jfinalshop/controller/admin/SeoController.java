package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Seo;
import com.jfinalshop.service.SeoService;

/**
 * Controller - SEO设置
 * 
 */
@ControllerBind(controllerKey = "/admin/seo")
public class SeoController extends BaseController {

	@Inject
	private SeoService seoService;

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("seo", seoService.find(id));
		render("/admin/seo/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		Seo seo = getModel(Seo.class);
		
		seoService.update(seo, "type");
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", seoService.findPage(pageable));
		render("/admin/seo/list.ftl");
	}

}