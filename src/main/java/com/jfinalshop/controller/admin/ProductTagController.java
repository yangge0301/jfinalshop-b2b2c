package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.ProductTag;
import com.jfinalshop.service.ProductTagService;

/**
 * Controller - 商品标签
 * 
 */
@ControllerBind(controllerKey = "/admin/product_tag")
public class ProductTagController extends BaseController {

	@Inject
	private ProductTagService productTagService;

	/**
	 * 添加
	 */
	public void add() {
		render("/admin/product_tag/add.ftl");
	}

	/**
	 * 保存
	 */
	public void save() {
		ProductTag productTag = getModel(ProductTag.class);
		
		productTag.setProducts(null);
		productTagService.save(productTag);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("productTag", productTagService.find(id));
		render("/admin/product_tag/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		ProductTag productTag = getModel(ProductTag.class);
		
		productTagService.update(productTag);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", productTagService.findPage(pageable));
		render("/admin/product_tag/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		productTagService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}

}