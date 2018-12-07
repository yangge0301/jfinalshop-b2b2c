package com.jfinalshop.controller.member;

import java.util.ArrayList;
import java.util.List;

import net.hasor.core.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.shiro.authz.UnauthorizedException;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.Results;
import com.jfinalshop.interceptor.MobileInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.ProductFavorite;
import com.jfinalshop.model.Store;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.ProductFavoriteService;
import com.jfinalshop.service.ProductService;

/**
 * Controller - 商品收藏
 * 
 */
@ControllerBind(controllerKey = "/member/product_favorite")
public class ProductFavoriteController extends BaseController {

	/**
	 * 每页记录数
	 */
	private static final int PAGE_SIZE = 10;

	@Inject
	private ProductFavoriteService productFavoriteService;
	@Inject
	private ProductService productService;
	@Inject
	private MemberService memberService;

	/**
	 * 添加属性
	 */
	public void populateModel() {
		Long productId = getParaToLong("productId");
		Long productFavoriteId = getParaToLong("productFavoriteId");
		Member currentUser = memberService.getCurrentUser();
		
		setAttr("product", productService.find(productId));

		ProductFavorite productFavorite = productFavoriteService.find(productFavoriteId);
		if (productFavorite != null && !currentUser.equals(productFavorite.getMember())) {
			throw new UnauthorizedException();
		}
		setAttr("productFavorite", productFavorite);
	}

	/**
	 * 添加
	 */
	public void add() {
		Long productId = getParaToLong("productId");
		Product product = productService.find(productId);
		Member currentUser = memberService.getCurrentUser();
		
		if (product == null || BooleanUtils.isNotTrue(product.getIsActive())) {
			Results.notFound(getResponse(), Results.DEFAULT_NOT_FOUND_MESSAGE);
			return;
		}
		if (productFavoriteService.exists(currentUser, product)) {
			Results.unprocessableEntity(getResponse(), "member.productFavorite.exist");
			return;
		}
		if (BooleanUtils.isNotTrue(product.getIsMarketable())) {
			Results.unprocessableEntity(getResponse(), "member.productFavorite.notMarketable");
			return;
		}
		if (ProductFavorite.MAX_PRODUCT_FAVORITE_SIZE != null && productFavoriteService.count(currentUser) >= ProductFavorite.MAX_PRODUCT_FAVORITE_SIZE) {
			Results.unprocessableEntity(getResponse(), "member.productFavorite.addCountNotAllowed", ProductFavorite.MAX_PRODUCT_FAVORITE_SIZE);
			return;
		}
		ProductFavorite productFavorite = new ProductFavorite();
		productFavorite.setMemberId(currentUser.getId());
		productFavorite.setProductId(product.getId());
		productFavoriteService.save(productFavorite);
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
		setAttr("page", productFavoriteService.findPage(currentUser, pageable));
		render("/member/product_favorite/list.ftl");
	}

	/**
	 * 列表
	 */
	@ActionKey("/member/product_favorite/m_list")
	public void mList() {
		Integer pageNumber = getParaToInt("pageNumber");
		Member currentUser = memberService.getCurrentUser();
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		Page<ProductFavorite> pages = productFavoriteService.findPage(currentUser, pageable);
		
		List<ProductFavorite> productFavorites = new ArrayList<ProductFavorite>();
		if (CollectionUtils.isNotEmpty(pages.getList())) {
			for (ProductFavorite productFavorite : pages.getList()) {
				productFavorite.put("member", productFavorite.getMember());
				Product product = productFavorite.getProduct();
				product.put("type", product.getTypeName());
				product.put("thumbnail", product.getThumbnail());
				Store store = product.getStore();
				store.put("type", store.getTypeName());
				product.put("store", store);
				product.put("defaultSku", product.getDefaultSku());
				product.put("path", product.getPath());
				productFavorite.put("product", product);
				productFavorites.add(productFavorite);
			}
		}
		renderJson(productFavorites);
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long productFavoriteId = getParaToLong("productFavoriteId");
		ProductFavorite productFavorite = productFavoriteService.find(productFavoriteId);
		if (productFavorite == null) {
			Results.notFound(getResponse(), Results.DEFAULT_NOT_FOUND_MESSAGE);
			return;
		}

		productFavoriteService.delete(productFavorite);
		renderJson(Results.OK);
	}

}