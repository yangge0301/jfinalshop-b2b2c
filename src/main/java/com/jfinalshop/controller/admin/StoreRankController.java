package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import org.apache.commons.lang.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Message;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.StoreRank;
import com.jfinalshop.service.StoreRankService;

/**
 * Controller - 店铺等级
 * 
 */
@ControllerBind(controllerKey = "/admin/store_rank")
public class StoreRankController extends BaseController {

	@Inject
	private StoreRankService storeRankService;

	/**
	 * 检查名称是否唯一
	 */
	@ActionKey("/admin/store_rank/check_name")
	public void checkName() {
		Long id = getParaToLong("id");
		String name = getPara("storeRank.name");
		renderJson(StringUtils.isNotEmpty(name) && storeRankService.nameUnique(id, name));
	}

	/**
	 * 添加
	 */
	public void add() {
		render("/admin/store_rank/add.ftl");
	}

	/**
	 * 保存
	 */
	public void save() {
		StoreRank storeRank = getModel(StoreRank.class);
		Boolean isAllowRegister = getParaToBoolean("isAllowRegister", false);
		
		if (storeRankService.nameExists(storeRank.getName())) {
			setAttr("errorMessage", "店铺等级名已存在!");
			render(ERROR_VIEW);
			return;
		}
		storeRank.setIsAllowRegister(isAllowRegister);
		storeRankService.save(storeRank);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("storeRank", storeRankService.find(id));
		render("/admin/store_rank/edit.ftl"); ;
	}

	/**
	 * 更新
	 */
	public void update() {
		StoreRank storeRank = getModel(StoreRank.class);
		Boolean isAllowRegister = getParaToBoolean("isAllowRegister", false);
		
		StoreRank pStoreRank = storeRankService.find(storeRank.getId());
		if (pStoreRank == null) {
			setAttr("errorMessage", "店铺等级名不存在!");
			render(ERROR_VIEW);
			return;
		}
		if (!storeRankService.nameUnique(storeRank.getId(), storeRank.getName())) {
			setAttr("errorMessage", "名称不唯一!");
			render(ERROR_VIEW);
			return;
		}
		
		storeRank.setIsAllowRegister(isAllowRegister);
		storeRankService.update(storeRank);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", storeRankService.findPage(pageable));
		render("/admin/store_rank/list.ftl");
	}

	/**
	 * 删除
	 */
	@Before(Tx.class)
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids != null) {
			for (Long id : ids) {
				StoreRank storeRank = storeRankService.find(id);
				if (storeRank != null && storeRank.getStores() != null && !storeRank.getStores().isEmpty()) {
					renderJson(Message.error("admin.storeRank.deleteExistNotAllowed", storeRank.getName()));
					return;
				}
			}
			long totalCount = storeRankService.count();
			if (ids.length >= totalCount) {
				renderJson(Message.error("admin.common.deleteAllNotAllowed"));
				return;
			}
			storeRankService.delete(ids);
		}
		storeRankService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}

}