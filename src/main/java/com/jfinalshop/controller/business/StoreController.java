package com.jfinalshop.controller.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.hasor.core.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Results;
import com.jfinalshop.interceptor.BusinessInterceptor;
import com.jfinalshop.model.Business;
import com.jfinalshop.model.Store;
import com.jfinalshop.model.StoreRank;
import com.jfinalshop.model.Svc;
import com.jfinalshop.plugin.PaymentPlugin;
import com.jfinalshop.service.BusinessService;
import com.jfinalshop.service.PluginService;
import com.jfinalshop.service.ProductCategoryService;
import com.jfinalshop.service.StoreCategoryService;
import com.jfinalshop.service.StoreRankService;
import com.jfinalshop.service.StoreService;
import com.jfinalshop.service.SvcService;

/**
 * Controller - 店铺
 * 
 */
@ControllerBind(controllerKey = "/business/store")
public class StoreController extends BaseController {

	@Inject
	private StoreService storeService;
	@Inject
	private StoreRankService storeRankService;
	@Inject
	private StoreCategoryService storeCategoryService;
	@Inject
	private ProductCategoryService productCategoryService;
	@Inject
	private PluginService pluginService;
	@Inject
	private SvcService svcService;
	@Inject
	private BusinessService businessService;

	/**
	 * 检查名称是否唯一
	 */
	@Clear(BusinessInterceptor.class)
	@ActionKey("/business/store/check_name")
	public void checkName() {
		String name = getPara("store.name");
		Store store = businessService.getCurrentStore();
		Long id = store != null ? store.getId() : null;
		renderJson(StringUtils.isNotEmpty(name) && storeService.nameUnique(id, name));
	}

	/**
	 * 店铺状态
	 */
	@ActionKey("/business/store/store_status")
	public void storeStatus() {
		Store currentStore = businessService.getCurrentStore();
		
		Map<String, Object> data = new HashMap<>();
		data.put("status", currentStore.getStatus());
		renderJson(data);
	}

	/**
	 * 到期日期
	 */
	@ActionKey("/business/store/end_date")
	public void endDate() {
		Store currentStore = businessService.getCurrentStore();
		
		Map<String, Object> data = new HashMap<>();
		data.put("endDate", currentStore.getEndDate());
		renderJson(data);
	}

	/**
	 * 计算
	 */
	public void calculate() {
		String paymentPluginId = getPara("paymentPluginId");
		Integer years = getParaToInt("years");
		Store currentStore = businessService.getCurrentStore();
		
		Map<String, Object> data = new HashMap<>();
		PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin(paymentPluginId);
		if (paymentPlugin == null || !paymentPlugin.getIsEnabled()) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		if (years == null || years < 0) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		BigDecimal amount = currentStore.getStoreRank().getServiceFee().multiply(new BigDecimal(years));
		if (Store.Status.approved.equals(currentStore.getStatusName())) {
			amount = amount.add(currentStore.getBailPayable());
		}
		data.put("fee", paymentPlugin.calculateFee(amount));
		data.put("amount", paymentPlugin.calculateAmount(amount));
		renderJson(data);
	}

	/**
	 * 申请
	 */
	@Clear(BusinessInterceptor.class)
	public void register() {
		Store currentStore = businessService.getCurrentStore();
		if (currentStore != null) {
			render("/business/index.ftl");
		}

		setAttr("storeRanks", storeRankService.findList(true, null, null));
		setAttr("storeCategories", storeCategoryService.findAll());
		setAttr("productCategoryTree", productCategoryService.findTree());
		render("/business/store/register.ftl");
	}

	/**
	 * 申请
	 */
	@Clear(BusinessInterceptor.class)
	@ActionKey("/business/store/save_register")
	public void saveRegister() {
		Store store = getModel(Store.class);
		Long storeRankId = getParaToLong("storeRankId");
		Long storeCategoryId = getParaToLong("storeCategoryId");
		Long[] productCategoryIds = getParaValuesToLong("productCategoryIds");
		Business currentUser = businessService.getCurrentUser();
		
		if (currentUser == null) {
			Results.unprocessableEntity(getResponse(), "common.message.unauthorized");
			return;
		}
		if (storeService.nameExists(store.getName())) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		StoreRank storeRank = storeRankService.find(storeRankId);
		if (!storeRank.getIsAllowRegister()) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}

		store.setType(Store.Type.general.ordinal());
		store.setStatus(Store.Status.pending.ordinal());
		store.setEndDate(new Date());
		store.setIsEnabled(true);
		store.setBailPaid(BigDecimal.ZERO);
		store.setBusinessId(currentUser.getId());
		store.setStoreRankId(storeRank.getId());
		store.setStoreCategoryId(storeCategoryService.find(storeCategoryId).getId());
		store.setProductCategories(new ArrayList<>(productCategoryService.findList(productCategoryIds)));
		storeService.save(store);
		renderJson(Results.OK);
	}

	/**
	 * 重新申请
	 */
	public void reapply() {
		Store currentStore = businessService.getCurrentStore();
		if (currentStore == null) {
			setAttr("errorMessage", "当前店铺不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (!Store.Status.failed.equals(currentStore.getStatus())) {
			setAttr("errorMessage", "店铺审核状态不是失败!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		setAttr("storeRanks", storeRankService.findList(true, null, null));
		setAttr("storeCategories", storeCategoryService.findAll());
		setAttr("productCategoryTree", productCategoryService.findTree());
		render("/business/store/reapply.ftl");
	}

	/**
	 * 重新申请
	 */
	@ActionKey("/business/store/save_reapply")
	public void saveReapply() {
		String name = getPara("name");
		String email = getPara("email");
		String mobile = getPara("mobile");
		Long storeRankId = getParaToLong("storeRankId");
		Long storeCategoryId = getParaToLong("storeCategoryId");
		Long[] productCategoryIds = getParaValuesToLong("productCategoryIds");
		Store currentStore = businessService.getCurrentStore();
		
		if (currentStore == null) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		if (!Store.Status.failed.equals(currentStore.getStatusName())) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		StoreRank storeRank = storeRankService.find(storeRankId);
		if (!storeRank.getIsAllowRegister()) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}

		currentStore.setName(name);
		currentStore.setStatus(Store.Status.pending.ordinal());
		currentStore.setEmail(email);
		currentStore.setMobile(mobile);
		currentStore.setStoreRank(storeRank);
		currentStore.setStoreCategory(storeCategoryService.find(storeCategoryId));
		currentStore.setProductCategories(new ArrayList<>(productCategoryService.findList(productCategoryIds)));
		storeService.update(currentStore);
		renderJson(Results.OK);
	}

	/**
	 * 缴费
	 */
	public void payment() {
		Store currentStore = businessService.getCurrentStore();
		
		if (currentStore == null) {
			setAttr("errorMessage", " 当前店铺不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (!Store.Status.approved.equals(currentStore.getStatusName()) && !Store.Status.success.equals(currentStore.getStatusName())) {
			setAttr("errorMessage", " 当前店铺状态不是审核通过!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		List<PaymentPlugin> paymentPlugins = pluginService.getActivePaymentPlugins(getRequest());
		if (CollectionUtils.isNotEmpty(paymentPlugins)) {
			setAttr("defaultPaymentPlugin", paymentPlugins.get(0));
			setAttr("paymentPlugins", paymentPlugins);
		}
		render("/business/store/payment.ftl");
	}

	/**
	 * 缴费
	 */
	public void buy() {
		Integer years = getParaToInt("years");
		Store currentStore = businessService.getCurrentStore();
		
		Map<String, Object> data = new HashMap<>();
		if (years == null || years < 0) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		if (!Store.Status.approved.equals(currentStore.getStatusName()) && !Store.Status.success.equals(currentStore.getStatusName())) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}

		int days = years * 365;
		BigDecimal serviceFee = currentStore.getStoreRank().getServiceFee().multiply(new BigDecimal(years));
		BigDecimal bail = Store.Status.approved.equals(currentStore.getStatusName()) ? currentStore.getBailPayable() : BigDecimal.ZERO;
		if (serviceFee.compareTo(BigDecimal.ZERO) > 0) {
			Svc svc = new Svc();
			svc.setAmount(serviceFee);
			svc.setDurationDays(days);
			svc.setStoreId(currentStore.getId());
			svcService.save(svc);

			data.put("platformSvcSn", svc.getSn());
		} else {
			storeService.addEndDays(currentStore, days);
			if (bail.compareTo(BigDecimal.ZERO) <= 0) {
				currentStore.setStatus(Store.Status.success.ordinal());
				storeService.update(currentStore);
			}
		}

		if (bail.compareTo(BigDecimal.ZERO) > 0) {
			data.put("bail", bail);
		}
		renderJson(data);
	}

	/**
	 * 设置
	 */
	public void setting() {
		render("/business/store/setting.ftl");
	}

	/**
	 * 设置
	 */
	@ActionKey("/business/store/save_setting")
	public void saveSetting() {
		Store store = getModel(Store.class);
		Store currentStore = businessService.getCurrentStore();
		
		if (store == null) {
			setAttr("errorMessage", "店铺未找到!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (!storeService.nameUnique(currentStore.getId(), store.getName())) {
			setAttr("errorMessage", "当前店铺名称不唯一!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		currentStore.setName(store.getName());
		currentStore.setLogo(store.getLogo());
		currentStore.setEmail(store.getEmail());
		currentStore.setMobile(store.getMobile());
		currentStore.setPhone(store.getPhone());
		currentStore.setAddress(store.getAddress());
		currentStore.setZipCode(store.getZipCode());
		currentStore.setIntroduction(store.getIntroduction());
		currentStore.setKeyword(store.getKeyword());
		storeService.update(currentStore);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("setting");
	}

}