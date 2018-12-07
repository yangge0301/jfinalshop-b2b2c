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
import com.jfinalshop.model.Store;
import com.jfinalshop.model.StoreFavorite;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.StoreFavoriteService;
import com.jfinalshop.service.StoreService;
import com.xiaoleilu.hutool.util.CollectionUtil;

/**
 * Controller - 店铺收藏
 * 
 */
@ControllerBind(controllerKey = "/member/store_favorite")
public class StoreFavoriteController extends BaseController {

	/**
	 * 每页记录数
	 */
	private static final int PAGE_SIZE = 10;

	@Inject
	private StoreFavoriteService storeFavoriteService;
	@Inject
	private StoreService storeService;
	@Inject
	private MemberService memberService;

	/**
	 * 添加属性
	 */
	public void populateModel() {
		Long storeId = getParaToLong("storeId");
		Long storeFavoriteId = getParaToLong("storeFavoriteId");
		Member currentUser = memberService.getCurrentUser();
		
		setAttr("store", storeService.find(storeId));

		StoreFavorite storeFavorite = storeFavoriteService.find(storeFavoriteId);
		if (storeFavorite != null && !currentUser.equals(storeFavorite.getMember())) {
			throw new UnauthorizedException();
		}
		setAttr("storeFavorite", storeFavorite);
	}

	/**
	 * 添加
	 */
	public void add() {
		Long storeId = getParaToLong("storeId");
		Store store = storeService.find(storeId);
		Member currentUser = memberService.getCurrentUser();
		
		if (store == null) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}

		if (storeFavoriteService.exists(currentUser, store)) {
			Results.unprocessableEntity(getResponse(), "member.storeFavorite.exist");
			return;
		}
		if (StoreFavorite.MAX_STORE_FAVORITE_SIZE != null && storeFavoriteService.count(currentUser) >= StoreFavorite.MAX_STORE_FAVORITE_SIZE) {
			Results.unprocessableEntity(getResponse(), "member.storeFavorite.addCountNotAllowed", StoreFavorite.MAX_STORE_FAVORITE_SIZE);
			return;
		}
		StoreFavorite storeFavorite = new StoreFavorite();
		storeFavorite.setMemberId(currentUser.getId());
		storeFavorite.setStoreId(store.getId());
		storeFavoriteService.save(storeFavorite);
		renderJson(Results.OK);
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
		setAttr("page", storeFavoriteService.findPage(currentUser, pageable));
		render("/member/store_favorite/list.ftl");
	}

	/**
	 * 列表
	 */
	@ActionKey("/member/store_favorite/m_list")
	public void mList() {
		Integer pageNumber = getParaToInt("pageNumber", 1);
		Member currentUser = memberService.getCurrentUser();
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		Page<StoreFavorite> pages = storeFavoriteService.findPage(currentUser, pageable);
		
		List<StoreFavorite> storeFavorites = new ArrayList<StoreFavorite>();
		if (CollectionUtil.isNotEmpty(pages.getList())) {
			for (StoreFavorite storeFavorite : pages.getList()) {
				storeFavorite.put("member", storeFavorite.getMember());
				Store store = storeFavorite.getStore();
				store.put("type", store.getTypeName());
				store.put("path", store.getPath());
				storeFavorite.put("store", store);
				storeFavorites.add(storeFavorite);
			}
		}
		renderJson(storeFavorites);
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long storeFavoriteId = getParaToLong("storeFavoriteId");
		StoreFavorite storeFavorite = storeFavoriteService.find(storeFavoriteId);
		
		if (storeFavorite == null) {
			Results.notFound(getResponse(), Results.DEFAULT_NOT_FOUND_MESSAGE);
			return;
		}

		storeFavoriteService.delete(storeFavorite);
		renderJson(Results.OK);
	}

}