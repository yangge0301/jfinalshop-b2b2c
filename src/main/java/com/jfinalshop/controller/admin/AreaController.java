package com.jfinalshop.controller.admin;

import java.util.ArrayList;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Message;
import com.jfinalshop.model.Area;
import com.jfinalshop.service.AreaService;
import com.xiaoleilu.hutool.util.CollectionUtil;

/**
 * Controller - 地区
 * 
 */
@ControllerBind(controllerKey = "/admin/area")
public class AreaController extends BaseController {

	@Inject
	private AreaService areaService;

	/**
	 * 添加
	 */
	public void add() {
		Long parentId = getParaToLong("parentId");
		setAttr("parent", areaService.find(parentId));
		render("/admin/area/add.ftl");
	}

	/**
	 * 保存
	 */
	@Before(Tx.class)
	public void save() {
		Area area = getModel(Area.class);
		Long parentId = getParaToLong("parentId");
		Area pArea = areaService.find(parentId);
		if (pArea != null) {
			area.setParentId(pArea.getId());
		}
		
		areaService.save(area);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("area", areaService.find(id));
		render("/admin/area/edit.ftl");
	}

	/**
	 * 更新
	 */
	@Before(Tx.class)
	public void update() {
		Area area = getModel(Area.class);
		areaService.update(area, "fullName", "treePath", "grade", "parentId");
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 列表
	 */
	public void list() {
		Long parentId = getParaToLong("parentId");
		Area parent = areaService.find(parentId);
		if (parent != null) {
			setAttr("parent", parent);
			setAttr("areas", new ArrayList<>(parent.getChildren()));
		} else {
			setAttr("areas", areaService.findRoots());
		}
		render("/admin/area/list.ftl");
	}

	/**
	 * 删除
	 */
	@Before(Tx.class)
	public void delete() {
		Long id = getParaToLong("id");
		Area area = areaService.find(id);
		if (area != null && CollectionUtil.isNotEmpty(area.getChildren())) {
			renderJson(new Message(Message.Type.error, "删除失败! 存在下级地区。"));
			return;
		}
		areaService.delete(id);
		renderJson(SUCCESS_MESSAGE);
	}

}