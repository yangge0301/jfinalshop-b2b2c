package com.jfinalshop.controller.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.hasor.core.Inject;

import org.apache.commons.lang.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Pageable;
import com.jfinalshop.exception.UnauthorizedException;
import com.jfinalshop.model.Business;
import com.jfinalshop.model.Sku;
import com.jfinalshop.model.StockLog;
import com.jfinalshop.model.Store;
import com.jfinalshop.service.BusinessService;
import com.jfinalshop.service.SkuService;
import com.jfinalshop.service.StockLogService;

/**
 * Controller - 库存
 * 
 */
@ControllerBind(controllerKey = "/business/stock")
public class StockController extends BaseController {

	@Inject
	private StockLogService stockLogService;
	@Inject
	private SkuService skuService;
	@Inject
	private BusinessService businessService;

	/**
	 * 添加属性
	 */
	public void populateModel() {
		String skuSn = getPara("skuSn");
		Store currentStore = businessService.getCurrentStore();
		
		Sku sku = skuService.findBySn(skuSn);
		if (sku != null && !currentStore.equals(sku.getStore())) {
			throw new UnauthorizedException();
		}
		setAttr("sku", sku);
	}

	/**
	 * SKU选择
	 */
	@ActionKey("/business/stock/sku_select")
	public void skuSelect() {
		String keyword = getPara("keyword");
		Business currentUser = businessService.getCurrentUser();
		
		List<Map<String, Object>> data = new ArrayList<>();
		if (StringUtils.isEmpty(keyword)) {
			renderJson(data);
			return;
		}
		List<Sku> skus = skuService.search(currentUser.getStore(), null, keyword, null, null);
		for (Sku sku : skus) {
			Map<String, Object> item = new HashMap<>();
			item.put("sn", sku.getSn());
			item.put("name", sku.getName());
			item.put("stock", sku.getStock());
			item.put("allocatedStock", sku.getAllocatedStock());
			item.put("specifications", sku.getSpecifications());
			data.add(item);
		}
		renderJson(data);
	}

	/**
	 * 入库
	 */
	@ActionKey("/business/stock/stock_in")
	public void stockIn() {
		render("/business/stock/stock_in.ftl");
	}

	/**
	 * 入库
	 */
	@Before(Tx.class)
	@ActionKey("/business/stock/save_stock_in")
	public void saveStockIn() {
		String skuSn = getPara("skuSn");
		Integer quantity = getParaToInt("quantity");
		String memo = getPara("memo"); 
		
		if (StrKit.isBlank(skuSn)) {
			setAttr("errorMessage", "skuSn不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		Sku sku = skuService.findBySn(skuSn);
		if (sku == null) {
			setAttr("errorMessage", "SKU不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (quantity == null || quantity <= 0) {
			setAttr("errorMessage", "数量等于0!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		skuService.addStock(sku, quantity, StockLog.Type.stockIn, memo);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("log");
	}

	/**
	 * 出库
	 */
	@ActionKey("/business/stock/stock_out")
	public void stockOut() {
		Sku sku = getModel(Sku.class);
		setAttr("sku", sku);
		render("/business/stock/stock_out.ftl");
	}

	/**
	 * 出库
	 */
	@Before(Tx.class)
	@ActionKey("/business/stock/save_stock_out")
	public void saveStockOut() {
		String skuSn = getPara("skuSn");
		Integer quantity = getParaToInt("quantity");
		String memo = getPara("memo"); 
		
		if (StrKit.isBlank(skuSn)) {
			setAttr("errorMessage", "skuSn不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		Sku sku = skuService.findBySn(skuSn);
		if (sku == null) {
			setAttr("errorMessage", "SKU不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (quantity == null || quantity <= 0) {
			setAttr("errorMessage", "数量等于0!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (sku.getStock() - quantity < 0) {
			setAttr("errorMessage", "库存减出库少于0!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		skuService.addStock(sku, -quantity, StockLog.Type.stockOut, memo);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("log");
	}

	/**
	 * 记录
	 */
	public void log() {
		Pageable pageable = getBean(Pageable.class);
		Business currentUser = businessService.getCurrentUser();
		
		setAttr("pageable", pageable);
		setAttr("page", stockLogService.findPage(currentUser.getStore(), pageable));
		render("/business/stock/log.ftl");
	}

}