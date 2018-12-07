package com.jfinalshop.controller.member;

import java.util.ArrayList;
import java.util.List;

import net.hasor.core.Inject;

import org.apache.shiro.authz.UnauthorizedException;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.Results;
import com.jfinalshop.interceptor.MobileInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.ProductNotify;
import com.jfinalshop.model.Sku;
import com.jfinalshop.model.Store;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.ProductNotifyService;
import com.xiaoleilu.hutool.util.CollectionUtil;

/**
 * Controller - 到货通知
 * 
 */
@ControllerBind(controllerKey = "/member/product_notify")
public class ProductNotifyController extends BaseController {

	/**
	 * 每页记录数
	 */
	private static final int PAGE_SIZE = 10;
	
	@Inject
	private ProductNotifyService productNotifyService;
	@Inject
	private MemberService memberService;

	/**
	 * 添加属性
	 */
	public void populateModel() {
		Long productNotifyId = getParaToLong("productNotifyId");
		Member currentUser = memberService.getCurrentUser();
		
		ProductNotify productNotify = productNotifyService.find(productNotifyId);
		if (productNotify != null && !currentUser.equals(productNotify.getMember())) {
			throw new UnauthorizedException();
		}
		setAttr("productNotify", productNotify);
	}

	/**
	 * 列表
	 */
	@Before(MobileInterceptor.class)
	public void list() {
		Integer pageNumber = getParaToInt("pageNumber");
		Member currentUser = memberService.getCurrentUser();
		
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("pageable", pageable);
		setAttr("page", productNotifyService.findPage(null, currentUser, null, null, null, pageable));
		render("/member/product_notify/list.ftl");
	}

	/**
	 * 列表
	 */
	@ActionKey("/member/product_notify/m_list")
	public void mList() {
		Integer pageNumber = getParaToInt("pageNumber", 1);
		Member currentUser = memberService.getCurrentUser();
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		Page<ProductNotify> pages = productNotifyService.findPage(null, currentUser, null, null, null, pageable);
		
		List<ProductNotify> productNotifys = new ArrayList<ProductNotify>();
		if (CollectionUtil.isNotEmpty(pages.getList())) {
			for (ProductNotify productNotify : pages.getList()) {
				productNotify.put("member", productNotify.getMember());
				Sku sku = productNotify.getSku();
				sku.put("specificationValues", sku.getSpecificationValuesConverter());
				sku.put("thumbnail", sku.getThumbnail());
				sku.put("image", sku.getImage());
				sku.put("type", sku.getType());
				sku.put("path", sku.getPath());
				sku.put("name", sku.getName());
				productNotify.put("sku", sku);
				
				Store store = productNotify.getStore();
				store.put("type", store.getTypeName());
				store.put("path", store.getPath());
				productNotify.put("store", store);
				productNotifys.add(productNotify);
			}
		}
		renderJson(productNotifys);
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long productNotifyId = getParaToLong("productNotifyId");
		ProductNotify productNotify = productNotifyService.find(productNotifyId);
		
		if (productNotify == null) {
			Results.notFound(getResponse(), Results.DEFAULT_NOT_FOUND_MESSAGE);
			return;
		}

		productNotifyService.delete(productNotify);
		renderJson(Results.OK);
	}

}