package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import org.apache.commons.lang.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Brand;
import com.jfinalshop.service.BrandService;

/**
 * Controller - 品牌
 * 
 */
@ControllerBind(controllerKey = "/admin/brand")
public class BrandController extends BaseController {

	@Inject
	private BrandService brandService;

	/**
	 * 添加
	 */
	public void add() {
		setAttr("types", Brand.Type.values());
		render("/admin/brand/add.ftl");
	}

	/**
	 * 保存
	 */
	@Before(Tx.class)
	public void save() {
		Brand brand = getModel(Brand.class);
		Brand.Type type = getParaEnum(Brand.Type.class, getPara("type"));
		brand.setType(type);
		
		if (Brand.Type.text.equals(brand.getTypeName())) {
			brand.setLogo(null);
		} else if (StringUtils.isEmpty(brand.getLogo())) {
			setAttr("errorMessage", "品牌图标不能为空!");
			render(ERROR_VIEW);
			return;
		}
		brand.setProducts(null);
		brand.setProductCategories(null);
		brandService.save(brand);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("types", Brand.Type.values());
		setAttr("brand", brandService.find(id));
		render("/admin/brand/edit.ftl");
	}

	/**
	 * 更新
	 */
	@Before(Tx.class)
	public void update() {
		Brand brand = getModel(Brand.class);
		Brand.Type type = getParaEnum(Brand.Type.class, getPara("type"));
		brand.setType(type);
		
		if (Brand.Type.text.equals(brand.getTypeName())) {
			brand.setLogo(null);
		} else if (StringUtils.isEmpty(brand.getLogo())) {
			setAttr("errorMessage", "品牌图标不能为空!");
			render(ERROR_VIEW);
			return;
		}
		brandService.update(brand);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", brandService.findPage(pageable));
		render("/admin/brand/list.ftl");
	}

	/**
	 * 删除
	 */
	@Before(Tx.class)
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		brandService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}

}