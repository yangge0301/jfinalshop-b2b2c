package com.jfinalshop.controller.admin;


import net.hasor.core.Inject;

import org.apache.commons.lang.StringUtils;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.render.FreeMarkerRender;
import com.jfinalshop.TemplateConfig;
import com.jfinalshop.service.TemplateService;
import com.jfinalshop.util.SystemUtils;

import freemarker.template.Configuration;

/**
 * Controller - 模板
 * 
 */
@ControllerBind(controllerKey = "/admin/template")
public class TemplateController extends BaseController {

	@Inject
	private TemplateService templateService;
	private Configuration freeMarkerConfigurer = FreeMarkerRender.getConfiguration();

	/**
	 * 编辑
	 */
	public void edit() {
		String id = getPara("id");
		if (StringUtils.isEmpty(id)) {
			setAttr("errorMessage", "id不能为空!");
			render(ERROR_VIEW);
			return;
		}
		setAttr("templateConfig", SystemUtils.getTemplateConfig(id));
		setAttr("content", templateService.read(id));
		render("/admin/template/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		String id = getPara("id");
		String content = getPara("content");
		
		if (StringUtils.isEmpty(id) || content == null) {
			setAttr("errorMessage", "id不能为空!");
			render(ERROR_VIEW);
			return;
		}
		templateService.write(id, content);
		freeMarkerConfigurer.clearTemplateCache();
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 列表
	 */
	public void list() {
		TemplateConfig.Type type = getParaEnum(TemplateConfig.Type.class, getPara("type"));
		setAttr("type", type);
		setAttr("types", TemplateConfig.Type.values());
		setAttr("templateConfigs", SystemUtils.getTemplateConfigs(type));
		render("/admin/template/list.ftl");
	}

}