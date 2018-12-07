package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import org.apache.commons.lang.StringUtils;

import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Message;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.StoreCategory;
import com.jfinalshop.service.StoreCategoryService;

/**
 * Controller - 店铺分类
 * 
 */
@ControllerBind(controllerKey = "/admin/store_category")
public class StoreCategoryController extends BaseController {

	@Inject
	private StoreCategoryService storeCategoryService;

	/**
	 * 检查用户名是否存在
	 */
	@ActionKey("/admin/store_category/check_name")
	public void checkName() {
		String name = getPara("storeCategory.name");
		renderJson(StringUtils.isNotEmpty(name) && !storeCategoryService.nameExists(name));
	}

	/**
	 * 添加
	 */
	public void add() {
		render("/admin/store_category/add.ftl");
	}

	/**
	 * 保存
	 */
	public void save() {
		StoreCategory storeCategory = getModel(StoreCategory.class);
		
		storeCategory.setStores(null);
		storeCategoryService.save(storeCategory);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("storeCategory", storeCategoryService.find(id));
		render("/admin/store_category/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		StoreCategory storeCategory = getModel(StoreCategory.class);
		
		storeCategoryService.update(storeCategory);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", storeCategoryService.findPage(pageable));
		render("/admin/store_category/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids != null) {
			for (Long id : ids) {
				StoreCategory storeCategory = storeCategoryService.find(id);
				if (storeCategory != null && storeCategory.getStores() != null && !storeCategory.getStores().isEmpty()) {
					renderJson(Message.error("admin.storeCategory.deleteExistNotAllowed", storeCategory.getName()));
					return;
				}
			}
			long totalCount = storeCategoryService.count();
			if (ids.length >= totalCount) {
				renderJson(Message.error("admin.common.deleteAllNotAllowed"));
				return;
			}
			storeCategoryService.delete(ids);
		}
		renderJson(SUCCESS_MESSAGE);
	}

}