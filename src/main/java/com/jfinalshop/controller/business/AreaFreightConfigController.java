package com.jfinalshop.controller.business;

import net.hasor.core.Inject;

import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.Results;
import com.jfinalshop.exception.UnauthorizedException;
import com.jfinalshop.model.Area;
import com.jfinalshop.model.AreaFreightConfig;
import com.jfinalshop.model.ShippingMethod;
import com.jfinalshop.model.Store;
import com.jfinalshop.service.AreaFreightConfigService;
import com.jfinalshop.service.AreaService;
import com.jfinalshop.service.BusinessService;
import com.jfinalshop.service.ShippingMethodService;

/**
 * Controller - 地区运费配置
 * 
 */
@ControllerBind(controllerKey = "/business/area_freight_config")
public class AreaFreightConfigController extends BaseController {

	@Inject
	private AreaFreightConfigService areaFreightConfigService;
	@Inject
	private ShippingMethodService shippingMethodService;
	@Inject
	private AreaService areaService;
	@Inject
	private BusinessService businessService;

	/**
	 * 添加属性
	 */
	public void populateModel() {
		Long shippingMethodId = getParaToLong("shippingMethodId");
		Long areaId = getParaToLong("areaId"); 
		Long areaFreightConfigId = getParaToLong("areaFreightConfigId");
		Store currentStore = businessService.getCurrentStore();
		
		setAttr("area", areaService.find(areaId));
		setAttr("shippingMethod", shippingMethodService.find(shippingMethodId));

		AreaFreightConfig areaFreightConfig = areaFreightConfigService.find(areaFreightConfigId);
		if (areaFreightConfig != null && !currentStore.equals(areaFreightConfig.getStore())) {
			throw new UnauthorizedException();
		}
		setAttr("areaFreightConfig", areaFreightConfig);
	}

	/**
	 * 检查地区是否唯一
	 */
	@ActionKey("/business/area_freight_config/check_area")
	public void checkArea() {
		Long id = getParaToLong("id");
		Long areaId = getParaToLong("areaId");
		Area area = areaService.find(areaId);
		
		Long shippingMethodId = getParaToLong("shippingMethodId");
		ShippingMethod shippingMethod = shippingMethodService.find(shippingMethodId);
		
		Store currentStore = businessService.getCurrentStore();
		
		if (shippingMethod == null || area == null) {
			renderJson(false);
			return;
		}
		renderJson(areaFreightConfigService.unique(id, shippingMethod, currentStore, area));
	}

	/**
	 * 添加
	 */
	public void add() {
		Long shippingMethodId = getParaToLong("shippingMethodId");
		ShippingMethod shippingMethod = shippingMethodService.find(shippingMethodId);
		setAttr("shippingMethod", shippingMethod);
		render("/business/area_freight_config/add.ftl");
	}

	/**
	 * 保存
	 */
	public void save() {
		AreaFreightConfig areaFreightConfig = getModel(AreaFreightConfig.class);
		Long shippingMethodId = getParaToLong("shippingMethodId");
		ShippingMethod shippingMethod = shippingMethodService.find(shippingMethodId);
		
		Long areaId = getParaToLong("areaId");
		Area area = areaService.find(areaId);
		Store currentStore = businessService.getCurrentStore();
		
		areaFreightConfig.setAreaId(area.getId());
		areaFreightConfig.setShippingMethodId(shippingMethod.getId());
		areaFreightConfig.setStoreId(currentStore.getId());
		if (areaFreightConfigService.exists(shippingMethod, currentStore, area)) {
			setAttr("errorMessage", "运费配置不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		areaFreightConfigService.save(areaFreightConfig);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list?shippingMethodId=" + shippingMethod.getId());
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long areaFreightConfigId = getParaToLong("areaFreightConfigId");
		AreaFreightConfig areaFreightConfig = areaFreightConfigService.find(areaFreightConfigId);
		
		if (areaFreightConfig == null) {
			setAttr("errorMessage", "运费配置不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		setAttr("areaFreightConfig", areaFreightConfig);
		render("/business/area_freight_config/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		AreaFreightConfig areaFreightConfig = getModel(AreaFreightConfig.class);
		Long areaId = getParaToLong("areaId");
		Area area = areaService.find(areaId);
		
		Store currentStore = businessService.getCurrentStore();
		
		if (areaFreightConfig == null) {
			setAttr("errorMessage", "运费配置不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		areaFreightConfig.setAreaId(area.getId());
		AreaFreightConfig pAreaFreightConfig = areaFreightConfigService.find(areaFreightConfig.getId());
		if (!areaFreightConfigService.unique(areaFreightConfig.getId(), pAreaFreightConfig.getShippingMethod(), currentStore, area)) {
			setAttr("errorMessage", "运费配置不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		areaFreightConfigService.update(areaFreightConfig);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list?shippingMethodId=" + pAreaFreightConfig.getShippingMethod().getId());
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		Long shippingMethodId = getParaToLong("shippingMethodId");
		ShippingMethod shippingMethod = shippingMethodService.find(shippingMethodId);
		Store currentStore = businessService.getCurrentStore();
		
		setAttr("pageable", pageable);
		setAttr("shippingMethod", shippingMethod);
		setAttr("page", areaFreightConfigService.findPage(shippingMethod, currentStore, pageable));
		render("/business/area_freight_config/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		Store currentStore = businessService.getCurrentStore();
		for (AreaFreightConfig areaFreightConfig : areaFreightConfigService.findList(ids)) {
			if (!currentStore.equals(areaFreightConfig.getStore())) {
				Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
				return;
			}
		}
		areaFreightConfigService.delete(ids);
		renderJson(Results.OK);
	}

}