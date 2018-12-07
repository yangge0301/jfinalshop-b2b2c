package com.jfinalshop.controller.business;

import net.hasor.core.Inject;

import org.apache.shiro.authz.UnauthorizedException;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.Results;
import com.jfinalshop.model.InstantMessage;
import com.jfinalshop.model.Store;
import com.jfinalshop.service.BusinessService;
import com.jfinalshop.service.InstantMessageService;

/**
 * Controller - 即时通讯
 * 
 */
@ControllerBind(controllerKey = "/business/instant_message")
public class InstantMessageController extends BaseController {

	@Inject
	private InstantMessageService instantMessageService;
	@Inject
	private BusinessService businessService;

	/**
	 * 添加属性
	 */
	public void populateModel() {
		Long instantMessageId = getParaToLong("instantMessageId");
		Store currentStore = businessService.getCurrentStore();
		
		InstantMessage instantMessage = instantMessageService.find(instantMessageId);
		if (instantMessage != null && !currentStore.equals(instantMessage.getStore())) {
			throw new UnauthorizedException();
		}
		setAttr("instantMessage", instantMessage);
	}

	/**
	 * 添加
	 */
	public void add() {
		setAttr("types", InstantMessage.Type.values());
		render("/business/instant_message/add.ftl");
	}

	/**
	 * 保存
	 */
	public void save() {
		InstantMessage instantMessage = getModel(InstantMessage.class);
		InstantMessage.Type type = getParaEnum(InstantMessage.Type.class, getPara("type"));
		
		Store currentStore = businessService.getCurrentStore();
		
		instantMessage.setType(type.ordinal());
		instantMessage.setStoreId(currentStore.getId());
		instantMessageService.save(instantMessage);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long instantMessageId = getParaToLong("instantMessageId");
		InstantMessage instantMessage = instantMessageService.find(instantMessageId);
		
		if (instantMessage == null) {
			setAttr("errorMessage", "即时通讯不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		setAttr("types", InstantMessage.Type.values());
		setAttr("instantMessage", instantMessage);
		render("/business/instant_message/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		InstantMessage instantMessage = getModel(InstantMessage.class);
		InstantMessage.Type type = getParaEnum(InstantMessage.Type.class, getPara("type"));
		
		if (instantMessage == null) {
			setAttr("errorMessage", "即时通讯不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		instantMessage.setType(type.ordinal());
		instantMessageService.update(instantMessage);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		Store currentStore = businessService.getCurrentStore();
		
		setAttr("pageable", pageable);
		setAttr("page", instantMessageService.findPage(currentStore, pageable));
		render("/business/instant_message/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		Store currentStore = businessService.getCurrentStore();
		
		for (Long id : ids) {
			InstantMessage instantMessage = instantMessageService.find(id);
			if (instantMessage == null || !currentStore.equals(instantMessage.getStore())) {
				Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
				return;
			}
			instantMessageService.delete(id);
		}
		renderJson(Results.OK);
	}

}