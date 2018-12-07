package com.jfinalshop.controller.business;

import net.hasor.core.Inject;

import org.apache.shiro.authz.UnauthorizedException;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Pageable;
import com.jfinalshop.Results;
import com.jfinalshop.model.DeliveryTemplate;
import com.jfinalshop.model.Store;
import com.jfinalshop.service.BusinessService;
import com.jfinalshop.service.DeliveryTemplateService;

/**
 * Controller - 快递单模板
 * 
 */
@ControllerBind(controllerKey = "/business/delivery_template")
public class DeliveryTemplateController extends BaseController {

	@Inject
	private DeliveryTemplateService deliveryTemplateService;
	@Inject
	private BusinessService businessService;

	/**
	 * 添加属性
	 */
	public void populateModel() {
		Long deliveryTemplateId = getParaToLong("deliveryTemplateId");
		Store currentStore = getModel(Store.class);
		
		DeliveryTemplate deliveryTemplate = deliveryTemplateService.find(deliveryTemplateId);
		if (deliveryTemplate != null && !currentStore.equals(deliveryTemplate.getStore())) {
			throw new UnauthorizedException();
		}
		setAttr("deliveryTemplate", deliveryTemplate);
	}

	/**
	 * 添加
	 */
	public void add() {
		setAttr("storeAttributes", DeliveryTemplate.StoreAttribute.values());
		setAttr("deliveryCenterAttributes", DeliveryTemplate.DeliveryCenterAttribute.values());
		setAttr("orderAttributes", DeliveryTemplate.OrderAttribute.values());
		render("/business/delivery_template/add.ftl");
	}

	/**
	 * 保存
	 */
	@Before(Tx.class)
	public void save() {
		DeliveryTemplate deliveryTemplate = getModel(DeliveryTemplate.class);
		Store currentStore = businessService.getCurrentStore();
		
		deliveryTemplate.setStoreId(currentStore.getId());
		deliveryTemplateService.save(deliveryTemplate);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long deliveryTemplateId = getParaToLong("deliveryTemplateId");
		DeliveryTemplate deliveryTemplate = deliveryTemplateService.find(deliveryTemplateId);
		
		if (deliveryTemplate == null) {
			setAttr("errorMessage", "快递单模板不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		setAttr("deliveryTemplate", deliveryTemplate);
		render("/business/delivery_template/edit.ftl");
	}

	/**
	 * 更新
	 */
	@Before(Tx.class)
	public void update() {
		DeliveryTemplate deliveryTemplate = getModel(DeliveryTemplate.class);
		
		if (deliveryTemplate == null) {
			setAttr("errorMessage", "快递单模板不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		deliveryTemplateService.update(deliveryTemplate);
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
		setAttr("page", deliveryTemplateService.findPage(currentStore, pageable));
		render("/business/delivery_template/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids"); 
		Store currentStore = businessService.getCurrentStore();
		
		for (Long id : ids) {
			DeliveryTemplate deliveryTemplate = deliveryTemplateService.find(id);
			if (deliveryTemplate == null || !currentStore.equals(deliveryTemplate.getStore())) {
				Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
				return;
			}
		}
		deliveryTemplateService.delete(ids);
		renderJson(Results.OK);
	}
}