package com.jfinalshop.controller.admin;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Message;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Business;
import com.jfinalshop.model.Store;
import com.jfinalshop.service.*;
import net.hasor.core.Inject;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * Controller - 店铺
 * 
 */
@ControllerBind(controllerKey = "/admin/store")
public class StoreController extends BaseController {

	@Inject
	private StoreService storeService;
	@Inject
	private BusinessService businessService;
	@Inject
	private StoreRankService storeRankService;
	@Inject
	private StoreCategoryService storeCategoryService;
	@Inject
	private ProductCategoryService productCategoryService;

	/**
	 * 检查名称是否唯一
	 */
	@ActionKey("/admin/store/check_name")
	public void checkName() {
		Long id = getParaToLong("id");
		String name = getPara("store.name");
		renderJson(StringUtils.isNotEmpty(name) && storeService.nameUnique(id, name));
	}

	/**
	 * 商家选择
	 */
	@ActionKey("/admin/store/business_select")
	public void businessSelect() {
		String keyword = getPara("q");
		Integer count = getParaToInt("limit");
		
		List<Map<String, Object>> data = new ArrayList<>();
		if (StringUtils.isEmpty(keyword)) {
			renderJson(data);
			return;
		}
		List<Business> businesses = businessService.search(keyword, count);
		for (Business businesse : businesses) {
			if (businesse.getStore() == null) {
				Map<String, Object> item = new HashMap<String, Object>();
				item.put("id", "" + businesse.getId());
				item.put("username", businesse.getUsername());
				data.add(item);
			}
		}
		renderJson(data);
	}

	/**
	 * 查看
	 */
	public void view() {
		Long id = getParaToLong("id");
		setAttr("store", storeService.find(id));
		setAttr("productCategoryTree", productCategoryService.findTree());
		render("/admin/store/view.ftl");
	}

	/**
	 * 添加
	 */
	public void add() {
		setAttr("types", Store.Type.values());
		setAttr("storeRanks", storeRankService.findAll());
		setAttr("storeCategories", storeCategoryService.findAll());
		setAttr("productCategoryTree", productCategoryService.findTree());
		render("/admin/store/add.ftl");
	}

	/**
	 * 保存
	 */
	@Before(Tx.class)
	public void save() {
		Store store = getModel(Store.class);
		Long businessId = getParaToLong("businessId");
		Long storeRankId = getParaToLong("storeRankId");
		Long storeCategoryId = getParaToLong("storeCategoryId");
		Long[] productCategoryIds = getParaValuesToLong("productCategoryIds");
		Boolean isEnabled = getParaToBoolean("isEnabled", false);
		Store.Type type = getParaEnum(Store.Type.class, getPara("type"));

		Business business = businessService.find(businessId);
		if (business == null) {
			setAttr("errorMessage", "商家不能为空!");
			render(ERROR_VIEW);
			return;
		}
		store.setBusinessId(business.getId());
		store.setStoreRankId(storeRankService.find(storeRankId).getId());
		store.setStoreCategoryId(storeCategoryService.find(storeCategoryId).getId());
		store.setType(type.ordinal());
		store.setProductCategories(productCategoryService.findList(productCategoryIds));
		store.setStatus(Store.Status.pending.ordinal());
		store.setEndDate(new Date());
		store.setIsEnabled(isEnabled);
		store.setBailPaid(BigDecimal.ZERO);
		if (storeService.nameExists(store.getName())) {
			setAttr("errorMessage", "店铺已存在!");
			render(ERROR_VIEW);
			return;
		}
		
		storeService.save(store);
		storeService.review(store, true, null);
		
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("store", storeService.find(id));
		setAttr("types", Store.Type.values());
		setAttr("storeRanks", storeRankService.findAll());
		setAttr("storeCategories", storeCategoryService.findAll());
		setAttr("productCategoryTree", productCategoryService.findTree());
		render("/admin/store/edit.ftl");
	}

	/**
	 * 更新
	 */
	@Before(Tx.class)
	public void update() {
		Store store = getModel(Store.class);
		Long storeRankId = getParaToLong("storeRankId"); 
		Long storeCategoryId = getParaToLong("storeCategoryId");
		Long[] productCategoryIds = getParaValuesToLong("productCategoryIds");
		Boolean isEnabled = getParaToBoolean("isEnabled", false);
		store.setIsEnabled(isEnabled);
		
		if (!storeService.nameUnique(store.getId(), store.getName())) {
			setAttr("errorMessage", "店铺已存在!");
			render(ERROR_VIEW);
			return;
		}
		Store pStore = storeService.find(store.getId());
		pStore.setName(store.getName());
		pStore.setLogo(store.getLogo());
		pStore.setEmail(store.getEmail());
		pStore.setMobile(store.getMobile());
		pStore.setPhone(store.getPhone());
		pStore.setAddress(store.getAddress());
		pStore.setZipCode(store.getZipCode());
		pStore.setIntroduction(store.getIntroduction());
		pStore.setKeyword(store.getKeyword());
		pStore.setEndDate(store.getEndDate());
		pStore.setIsEnabled(store.getIsEnabled());
		pStore.setStoreRankId(storeRankService.find(storeRankId).getId());
		pStore.setStoreCategoryId(storeCategoryService.find(storeCategoryId).getId());
		pStore.setProductCategories(new ArrayList<>(productCategoryService.findList(productCategoryIds)));
		
		storeService.update(pStore);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 列表
	 */
	public void list() {
		String typeName = getPara("type");
		Store.Type type = StrKit.notBlank(typeName) ? Store.Type.valueOf(typeName) : null;
		
		String statusName = getPara("status");
		Store.Status status = StrKit.notBlank(statusName) ? Store.Status.valueOf(statusName) : null;
		Boolean isEnabled = getParaToBoolean("isEnabled");
		Boolean hasExpired = getParaToBoolean("hasExpired");
		Pageable pageable = getBean(Pageable.class);
		
		setAttr("type", type);
		setAttr("status", status);
		setAttr("isEnabled", isEnabled);
		setAttr("hasExpired", hasExpired);
		setAttr("pageable", pageable);
		setAttr("page", storeService.findPage(type, status, isEnabled, hasExpired, pageable));
		render("/admin/store/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids != null) {
			for (Long id : ids) {
				Store store = storeService.find(id);
				if (store != null && Store.Status.success.equals(store.getStatus())) {
					renderJson(Message.error("admin.store.deleteSuccessNotAllowed", store.getName()));
					return;
				}
			}
			storeService.delete(ids);
		}
		renderJson(SUCCESS_MESSAGE);
	}

	/**
	 * 审核
	 */
	public void review() {
		Long id = getParaToLong("id");
		setAttr("store", storeService.find(id));
		setAttr("productCategoryTree", productCategoryService.findTree());
		render("/admin/store/review.ftl");
	}

	/**
	 * 审核
	 */
	@Before(Tx.class)
	@ActionKey("/admin/store/save_review")
	public void saveReview() {
		Long id = getParaToLong("id");
		Boolean passed = getParaToBoolean("passed");
		String content = getPara("content");
		
		Store store = storeService.find(id);
		if (store == null || !Store.Status.pending.equals(store.getStatusName()) || passed == null || (!passed && StringUtils.isEmpty(content))) {
			setAttr("errorMessage", "店铺为空或状态不是待审核!");
			render(ERROR_VIEW);
			return;
		}
		storeService.review(store, passed, content);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

}