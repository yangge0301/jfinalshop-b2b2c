package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.model.MessageConfig;
import com.jfinalshop.service.MessageConfigService;

/**
 * Controller - 消息配置
 * 
 */
@ControllerBind(controllerKey = "/admin/message_config")
public class MessageConfigController extends BaseController {

	@Inject
	private MessageConfigService messageConfigService;

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("messageConfig", messageConfigService.find(id));
		render("/admin/message_config/edit.ftl");
	}

	/**
	 * 更新
	 */
	@Before(Tx.class)
	public void update() {
		MessageConfig messageConfig = getModel(MessageConfig.class);
		Boolean isMailEnabled = getParaToBoolean("isMailEnabled", false);
		Boolean isSmsEnabled = getParaToBoolean("isSmsEnabled", false);
		
		messageConfig.setIsMailEnabled(isMailEnabled);
		messageConfig.setIsSmsEnabled(isSmsEnabled);
		messageConfigService.update(messageConfig, "type");
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 列表
	 */
	public void list() {
		setAttr("messageConfigs", messageConfigService.findAll());
		render("/admin/message_config/list.ftl");
	}

}