package com.jfinalshop.controller.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Results;
import com.jfinalshop.Setting;
import com.jfinalshop.interceptor.MobileInterceptor;
import com.jfinalshop.model.Cart;
import com.jfinalshop.model.CartItem;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.Sku;
import com.jfinalshop.model.Store;
import com.jfinalshop.service.CartService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.SkuService;
import com.jfinalshop.util.SystemUtils;
import com.jfinalshop.util.WebUtils;

/**
 * Controller - 购物车
 * 
 */
@ControllerBind(controllerKey = "/cart")
public class CartController extends BaseController {

	@Inject
	private SkuService skuService;
	@Inject
	private CartService cartService;
	@Inject
	private MemberService memberService;

	/**
	 * 信息
	 */
	public void info() {
		Cart currentCart = cartService.getCurrent(getRequest());
		
		Map<String, Object> data = new HashMap<>();
		Setting setting = SystemUtils.getSetting();
		if (currentCart != null) {
			data.put("tag", currentCart.getTag());
			data.put("skuQuantity", currentCart.getSkuQuantity(null));
			data.put("effectivePrice", currentCart.getEffectivePrice(null));
			List<Map<String, Object>> items = new ArrayList<>();
			for (CartItem cartItem : currentCart.getCartItems()) {
				Map<String, Object> item = new HashMap<>();
				Sku sku = cartItem.getSku();
				item.put("skuId", sku.getId());
				item.put("skuName", sku.getName());
				item.put("skuPath", sku.getPath());
				item.put("skuThumbnail", sku.getThumbnail() != null ? sku.getThumbnail() : setting.getDefaultThumbnailProductImage());
				item.put("price", cartItem.getPrice());
				item.put("quantity", cartItem.getQuantity());
				item.put("subtotal", cartItem.getSubtotal());
				items.add(item);
			}
			data.put("items", items);
		}
		renderJson(data);
	}

	/**
	 * 添加
	 */
	@Before(Tx.class)
	public void add() {
		Long skuId = getParaToLong("skuId");
		Integer quantity = getParaToInt("quantity");
		Cart currentCart = cartService.getCurrent(getRequest());
		Member currentUser = memberService.getCurrentUser();
		
		if (quantity == null || quantity < 1) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		Sku sku = skuService.find(skuId);
		if (sku == null) {
			Results.notFound(getResponse(), Results.DEFAULT_NOT_FOUND_MESSAGE);
			return;
		}
		if (!Product.Type.general.equals(sku.getType())) {
			Results.unprocessableEntity(getResponse(), "shop.cart.skuNotForSale");
			return;
		}
		if (!sku.getIsActive()) {
			Results.unprocessableEntity(getResponse(), "shop.cart.skuNotActive");
			return;
		}
		if (!sku.getIsMarketable()) {
			Results.unprocessableEntity(getResponse(), "shop.cart.skuNotMarketable");
			return;
		}

		int cartItemSize = 1;
		int skuQuantity = quantity;
		if (currentCart != null) {
			if (currentCart.contains(sku, null)) {
				CartItem cartItem = currentCart.getCartItem(sku, null);
				cartItemSize = currentCart.size();
				skuQuantity = cartItem.getQuantity() + quantity;
			} else {
				cartItemSize = currentCart.size() + 1;
				skuQuantity = quantity;
			}
		}
		if (Cart.MAX_CART_ITEM_SIZE != null && cartItemSize > Cart.MAX_CART_ITEM_SIZE) {
			Results.unprocessableEntity(getResponse(), "shop.cart.addCartItemCountNotAllowed", Cart.MAX_CART_ITEM_SIZE);
			return;
		}
		if (CartItem.MAX_QUANTITY != null && skuQuantity > CartItem.MAX_QUANTITY) {
			Results.unprocessableEntity(getResponse(), "shop.cart.addQuantityNotAllowed", CartItem.MAX_QUANTITY);
			return;
		}
		if (skuQuantity > sku.getAvailableStock()) {
			Results.unprocessableEntity(getResponse(), "shop.cart.skuLowStock");
			return;
		}
		if (currentCart == null) {
			currentCart = cartService.create();
		}
		cartService.add(currentCart, sku, quantity);
		if (currentUser != null) {
			WebUtils.removeCookie(getRequest(), getResponse(), Cart.KEY_COOKIE_NAME);
		} else {
			WebUtils.addCookie(getRequest(), getResponse(), Cart.KEY_COOKIE_NAME, currentCart.getCartKey(), Cart.TIMEOUT);
		}
		WebUtils.addCookie(getRequest(), getResponse(), Cart.TAG_COOKIE_NAME, currentCart.getTag());
		renderJson(Results.ok("shop.cart.addSuccess", currentCart.getSkuQuantity(null), currency(currentCart.getEffectivePrice(null), true, false)));
	}

	/**
	 * 列表
	 */
	@Before(MobileInterceptor.class)
	public void list() {
		setAttr("currentCart",  cartService.getCurrent(getRequest()));
		render("/shop/cart/list.ftl");
	}

	/**
	 * 修改
	 */
	@Before(Tx.class)
	public void modify() {
		Long skuId = getParaToLong("skuId");
		Integer quantity = getParaToInt("quantity");
		Cart currentCart = cartService.getCurrent(getRequest());
		
		Map<String, Object> data = new HashMap<>();
		if (quantity == null || quantity < 1) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		Sku sku = skuService.find(skuId);
		if (sku == null) {
			Results.notFound(getResponse(), "shop.cart.skuNotExist");
			return;
		}
		if (currentCart == null || currentCart.isEmpty()) {
			Results.unprocessableEntity(getResponse(), "shop.cart.notEmpty");
			return;
		}
		Store store = sku.getProduct().getStore();
		if (store == null) {
			Results.notFound(getResponse(), Results.DEFAULT_NOT_FOUND_MESSAGE);
			return;
		}
		if (!currentCart.contains(sku, null)) {
			Results.unprocessableEntity(getResponse(), "shop.cart.cartItemNotExist");
			return;
		}
		if (!sku.getIsActive()) {
			cartService.remove(currentCart, sku);
			Results.notFound(getResponse(), "shop.cart.skuNotActive");
			return;
		}
		if (!sku.getIsMarketable()) {
			cartService.remove(currentCart, sku);
			Results.notFound(getResponse(), "shop.cart.skuNotMarketable");
			return; 
		}
		if (CartItem.MAX_QUANTITY != null && quantity > CartItem.MAX_QUANTITY) {
			Results.unprocessableEntity(getResponse(), "shop.cart.addQuantityNotAllowed", CartItem.MAX_QUANTITY);
			return;
		}
		if (quantity > sku.getAvailableStock()) {
			Results.unprocessableEntity(getResponse(), "shop.cart.skuLowStock");
			return;
		}
		cartService.modify(currentCart, sku, quantity);
		CartItem cartItem = currentCart.getCartItem(sku, store);
		List<Store> stores = currentCart.getStores();

		data.put("subtotal", cartItem.getSubtotal());
		data.put("isLowStock", cartItem.getIsLowStock());
		data.put("quantity", currentCart.getSkuQuantity(store));
		data.put("effectiveRewardPoint", currentCart.getEffectiveRewardPointTotal(stores));
		data.put("effectivePrice", currentCart.getEffectivePriceTotal(stores));
		data.put("promotionDiscount", currentCart.getDiscountTotal(stores));
		data.put("giftNames", currentCart.getGiftNames(store));
		data.put("promotionNames", currentCart.getPromotionNames(store));
		renderJson(data);
	}

	/**
	 * 移除
	 */
	@Before(Tx.class)
	public void remove() {
		Long skuId = getParaToLong("skuId");
		Cart currentCart = cartService.getCurrent(getRequest());
		
		Map<String, Object> data = new HashMap<>();
		Sku sku = skuService.find(skuId);
		if (sku == null) {
			Results.notFound(getResponse(), "shop.cart.skuNotExist");
			return;
		}
		if (currentCart == null || currentCart.isEmpty()) {
			Results.unprocessableEntity(getResponse(), "shop.cart.notEmpty");
			return;
		}
		if (!currentCart.contains(sku, null)) {
			Results.unprocessableEntity(getResponse(), "shop.cart.cartItemNotExist");
			return;
		}
		Store store = sku.getProduct().getStore();
		cartService.remove(currentCart, sku);
		List<Store> stores = currentCart.getStores();

		data.put("isLowStock", currentCart.getIsLowStock(store));
		data.put("quantity", currentCart.getSkuQuantity(store));
		data.put("effectiveRewardPoint", currentCart.getEffectiveRewardPointTotal(stores));
		data.put("effectivePrice", currentCart.getEffectivePriceTotal(stores));
		data.put("promotionDiscount", currentCart.getDiscountTotal(stores));
		data.put("giftNames", currentCart.getGiftNames(store));
		data.put("promotionNames", currentCart.getPromotionNames(store));
		renderJson(data);
	}

	/**
	 * 清空
	 */
	@Before(Tx.class)
	public void clear() {
		Cart currentCart = cartService.getCurrent(getRequest());
		if (currentCart != null) {
			cartService.clear(currentCart);
		}
		renderJson(Results.OK);
	}

}