package com.jfinalshop.controller.business;

import net.hasor.core.Inject;

import org.apache.shiro.authz.UnauthorizedException;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.model.DeliveryCenter;
import com.jfinalshop.model.DeliveryTemplate;
import com.jfinalshop.model.Order;
import com.jfinalshop.model.Store;
import com.jfinalshop.service.DeliveryCenterService;
import com.jfinalshop.service.DeliveryTemplateService;
import com.jfinalshop.service.OrderService;

/**
 * Controller - 打印
 * 
 */
@ControllerBind(controllerKey = "/business/print")
public class PrintController extends BaseController {

	@Inject
	private OrderService orderService;
	@Inject
	private DeliveryTemplateService deliveryTemplateService;
	@Inject
	private DeliveryCenterService deliveryCenterService;

	/**
	 * 添加属性
	 */
	public void populateModel() {
		Long orderId = getParaToLong("orderId");
		Long deliveryTemplateId = getParaToLong("deliveryTemplateId");
		Long deliveryCenterId = getParaToLong("deliveryCenterId");
		Store currentStore = getModel(Store.class);
		
		Order order = orderService.find(orderId);
		if (order != null && !currentStore.equals(order.getStore())) {
			throw new UnauthorizedException();
		}
		setAttr("order", order);

		DeliveryTemplate deliveryTemplate = deliveryTemplateService.find(deliveryTemplateId);
		if (deliveryTemplate != null && !currentStore.equals(deliveryTemplate.getStore())) {
			throw new UnauthorizedException();
		}
		setAttr("deliveryTemplate", deliveryTemplate);

		DeliveryCenter deliveryCenter = deliveryCenterService.find(deliveryCenterId);
		if (deliveryCenter != null && !currentStore.equals(deliveryCenter.getStore())) {
			throw new UnauthorizedException();
		}
		setAttr("deliveryCenter", deliveryCenter);
	}

	/**
	 * 订单打印
	 */
	public void order() {
		Long orderId = getParaToLong("orderId");
		Order order = orderService.find(orderId);
				
		if (order == null) {
			setAttr("errorMessage", "订单为空");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		setAttr("order", order);
		render("/business/print/order.ftl");
	}

	/**
	 * 购物单打印
	 */
	public void product() {
		Long orderId = getParaToLong("orderId");
		Order order = orderService.find(orderId);
		
		if (order == null) {
			setAttr("errorMessage", "订单为空");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		setAttr("order", order);
		render("/business/print/product.ftl");
	}

	/**
	 * 发货单打印
	 */
	public void shipping() {
		Long orderId = getParaToLong("orderId");
		Order order = orderService.find(orderId);
		
		if (order == null) {
			setAttr("errorMessage", "订单为空");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		setAttr("order", order);
		render("/business/print/shipping.ftl");
	}

	/**
	 * 快递单打印
	 */
	public void delivery() {
		Long orderId = getParaToLong("orderId");
		Order order = orderService.find(orderId);
		
		DeliveryTemplate deliveryTemplate = getModel(DeliveryTemplate.class);
		DeliveryCenter deliveryCenter = getModel(DeliveryCenter.class);
		Store currentStore = getModel(Store.class);
		
		if (order == null) {
			setAttr("errorMessage", "订单为空");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (deliveryTemplate == null) {
			deliveryTemplate = deliveryTemplateService.findDefault(currentStore);
		}
		if (deliveryCenter == null) {
			deliveryCenter = deliveryCenterService.findDefault(currentStore);
		}

		setAttr("deliveryTemplates", deliveryTemplateService.findList(currentStore));
		setAttr("deliveryCenters", deliveryCenterService.findAll(currentStore));
		setAttr("order", order);
		setAttr("deliveryTemplate", deliveryTemplate);
		setAttr("deliveryCenter", deliveryCenter);
		if (deliveryTemplate != null) {
			setAttr("content", deliveryTemplateService.resolveContent(deliveryTemplate, currentStore, deliveryCenter, order));
		}
		render("/business/print/delivery.ftl");
	}

}