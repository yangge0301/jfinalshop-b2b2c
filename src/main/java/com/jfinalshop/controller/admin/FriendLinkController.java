package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import org.apache.commons.lang.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.FriendLink;
import com.jfinalshop.service.FriendLinkService;

/**
 * Controller - 友情链接
 * 
 */
@ControllerBind(controllerKey = "/admin/friend_link")
public class FriendLinkController extends BaseController {

	@Inject
	private FriendLinkService friendLinkService;

	/**
	 * 添加
	 */
	public void add() {
		setAttr("types", FriendLink.Type.values());
		render("/admin/friend_link/add.ftl");
	}

	/**
	 * 保存
	 */
	@Before(Tx.class)
	public void save() {
		FriendLink friendLink = getModel(FriendLink.class);
		FriendLink.Type type = getParaEnum(FriendLink.Type.class, getPara("type"));
		friendLink.setType(type.ordinal());
		
		if (FriendLink.Type.text.equals(friendLink.getTypeName())) {
			friendLink.setLogo(null);
		} else if (StringUtils.isEmpty(friendLink.getLogo())) {
			setAttr("errorMessage", "友情链接图标不能为空!");
			render(ERROR_VIEW);
			return;
		}
		friendLinkService.save(friendLink);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("types", FriendLink.Type.values());
		setAttr("friendLink", friendLinkService.find(id));
		render("/admin/friend_link/edit.ftl");
	}

	/**
	 * 更新
	 */
	@Before(Tx.class)
	public void update() {
		FriendLink friendLink = getModel(FriendLink.class);
		FriendLink.Type type = getParaEnum(FriendLink.Type.class, getPara("type"));
		friendLink.setType(type.ordinal());
		
		if (FriendLink.Type.text.equals(friendLink.getTypeName())) {
			friendLink.setLogo(null);
		} else if (StringUtils.isEmpty(friendLink.getLogo())) {
			setAttr("errorMessage", "友情链接图标不能为空!");
			render(ERROR_VIEW);
			return;
		}
		friendLinkService.update(friendLink);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", friendLinkService.findPage(pageable));
		render("/admin/friend_link/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		friendLinkService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}

}