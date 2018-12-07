package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.DeliveryCorp;
import com.jfinalshop.service.DeliveryCorpService;

/**
 * Controller - 物流公司
 * 
 */
@ControllerBind(controllerKey = "/admin/delivery_corp")
public class DeliveryCorpController extends BaseController {

	@Inject
	private DeliveryCorpService deliveryCorpService;

	/**
	 * 添加
	 */
	public void add() {
		render("/admin/delivery_corp/add.ftl");
	}

	/**
	 * 保存
	 */
	@Before(Tx.class)
	public void save() {
		DeliveryCorp deliveryCorp = getModel(DeliveryCorp.class);
		
		deliveryCorpService.save(deliveryCorp);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("deliveryCorp", deliveryCorpService.find(id));
		render("/admin/delivery_corp/edit.ftl");
	}

	/**
	 * 更新
	 */
	@Before(Tx.class)
	public void update() {
		DeliveryCorp deliveryCorp = getModel(DeliveryCorp.class);
		deliveryCorpService.update(deliveryCorp);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", deliveryCorpService.findPage(pageable));
		render("/admin/delivery_corp/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		deliveryCorpService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}

}