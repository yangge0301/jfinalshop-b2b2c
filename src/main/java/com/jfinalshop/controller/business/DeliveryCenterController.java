package com.jfinalshop.controller.business;

import net.hasor.core.Inject;

import org.apache.shiro.authz.UnauthorizedException;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.Results;
import com.jfinalshop.model.Area;
import com.jfinalshop.model.DeliveryCenter;
import com.jfinalshop.model.Store;
import com.jfinalshop.service.AreaService;
import com.jfinalshop.service.BusinessService;
import com.jfinalshop.service.DeliveryCenterService;

/**
 * Controller - 发货点
 * 
 */
@ControllerBind(controllerKey = "/business/delivery_center")
public class DeliveryCenterController extends BaseController {

	@Inject
	private DeliveryCenterService deliveryCenterService;
	@Inject
	private AreaService areaService;
	@Inject
	private BusinessService businessService;

	/**
	 * 添加属性
	 */
	public void populateModel() {
		Long areaId = getParaToLong("areaId");
		Long deliveryCenterId = getParaToLong("deliveryCenterId");
		Store currentStore = businessService.getCurrentStore();
		
		setAttr("area", areaService.find(areaId));

		DeliveryCenter deliveryCenter = deliveryCenterService.find(deliveryCenterId);
		if (deliveryCenter != null && !currentStore.equals(deliveryCenter.getStore())) {
			throw new UnauthorizedException();
		}
		setAttr("deliveryCenter", deliveryCenter);
	}

	/**
	 * 添加
	 */
	public void add() {
		render("/business/delivery_center/add.ftl");
	}

	/**
	 * 保存
	 */
	public void save() {
		DeliveryCenter deliveryCenter = getModel(DeliveryCenter.class);
		Long areaId = getParaToLong("areaId");
		Store currentStore = businessService.getCurrentStore();
		
		Area area = areaService.find(areaId);
		if (area == null) {
			setAttr("errorMessage", "区域不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		deliveryCenter.setAreaId(area.getId());
		deliveryCenter.setAreaName(area.getFullName());
		deliveryCenter.setStoreId(currentStore.getId());
		deliveryCenterService.save(deliveryCenter);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long deliveryCenterId = getParaToLong("deliveryCenterId");
		
		DeliveryCenter deliveryCenter = deliveryCenterService.find(deliveryCenterId);
		if (deliveryCenter == null) {
			setAttr("errorMessage", "发货点不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		setAttr("deliveryCenter", deliveryCenter);
		render("/business/delivery_center/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		DeliveryCenter deliveryCenter = getModel(DeliveryCenter.class);
		Long areaId = getParaToLong("areaId");
		
		Area area = areaService.find(areaId);
		if (area == null) {
			setAttr("errorMessage", "区域不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (deliveryCenter == null) {
			setAttr("errorMessage", "发货点不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		deliveryCenter.setAreaId(area.getId());
		deliveryCenter.setAreaName(area.getFullName());
		deliveryCenterService.update(deliveryCenter);
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
		setAttr("page", deliveryCenterService.findPage(currentStore, pageable));
		render("/business/delivery_center/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		Store currentStore = businessService.getCurrentStore();
		for (Long id : ids) {
			DeliveryCenter deliveryCenter = deliveryCenterService.find(id);
			if (deliveryCenter == null || !currentStore.equals(deliveryCenter.getStore())) {
				Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
				return;
			}
		}
		deliveryCenterService.delete(ids);
		renderJson(Results.OK);
	}

}