package com.jfinalshop.api.controller.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.hasor.core.Inject;

import org.apache.commons.collections.CollectionUtils;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Setting;
import com.jfinalshop.api.common.bean.DatumResponse;
import com.jfinalshop.api.controller.BaseAPIController;
import com.jfinalshop.api.interceptor.AccessInterceptor;
import com.jfinalshop.model.Brand;
import com.jfinalshop.model.Cart;
import com.jfinalshop.model.CartItem;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.Sku;
import com.jfinalshop.model.Store;
import com.jfinalshop.service.CartService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.SkuService;
import com.jfinalshop.util.SystemUtils;

/**
 * 移动API - 购物车
 *
 */
@ControllerBind(controllerKey = "/api/cart")
@Before(AccessInterceptor.class)
public class CartAPIController extends BaseAPIController {
	
	@Inject
	private SkuService skuService;
	@Inject
	private MemberService memberService;
	@Inject
	protected CartService cartService;
	
	private Res res = I18n.use();
	
	/**
	 * 获取购物车总数
	 * 
	 */
	@ActionKey("/api/cart/sku_quantity")
	public void skuQuantity() {
		String cartKey = getPara("cartKey");
		
		int skuQuantity = 0;
		Cart currentCart = cartService.getCurrent(cartKey);
		if (currentCart == null) {
			renderJson(new DatumResponse().setDatum(skuQuantity));
            return;
		}
		skuQuantity = currentCart.getSkuQuantity(null);
		renderJson(new DatumResponse().setDatum(skuQuantity));
	}
	
	/**
	 * 信息
	 */
	public void info() {
		String cartKey = getPara("cartKey");
		Cart currentCart = cartService.getCurrent(cartKey);
		
		Map<String, Object> data = new HashMap<>();
		List<Store> stores = new ArrayList<>();
		Setting setting = SystemUtils.getSetting();
		
		if (currentCart != null) {
			data.put("tag", currentCart.getTag());
			data.put("skuQuantity", currentCart.getSkuQuantity(null));
			data.put("effectivePrice", currentCart.getEffectivePrice(null));
			for (Store store : currentCart.getStores()) {
				Store pStore = converterStore(store);
				stores.add(pStore);
				List<Map<String, Object>> items = new ArrayList<>();
				for (CartItem cartItem : currentCart.getCartItems(store)) {
					Map<String, Object> item = new HashMap<>();
					Sku sku = cartItem.getSku();
					Product product = sku.getProduct();
					Brand brand = product.getBrand();
					item.put("skuId", sku.getId());
					item.put("skuName", sku.getName());
					item.put("skuThumbnail", sku.getThumbnail() != null ? sku.getThumbnail() : setting.getDefaultThumbnailProductImage());
					item.put("price", cartItem.getPrice());
					item.put("quantity", cartItem.getQuantity());
					item.put("subtotal", cartItem.getSubtotal());
					item.put("unit", sku.getUnit());
					item.put("brand", brand != null ? brand.getName() : null);
					item.put("giftNames", currentCart.getGiftNames(store));
					item.put("promotionNames", currentCart.getPromotionTitles(store));
					items.add(item);
				}
				pStore.put("items", items);
			}
			data.put("stores", stores);
		}
		renderJson(new DatumResponse(data));
	}
	
	/**
	 * 添加
	 * 
	 */
	@Before(Tx.class)
	public void add() {
		Long skuId = getParaToLong("skuId");
		Integer quantity = getParaToInt("quantity");
		String cartKey = getPara("cartKey");
		
		if (!"post".equalsIgnoreCase(getRequest().getMethod())) {
			renderArgumentError("必须采用POST请求!");
            return;
        }
		
		Map<String, Object> data = new HashMap<>();
		Cart currentCart = cartService.getCurrent(cartKey);
		if (quantity == null || quantity < 1) {
			renderArgumentError("数量不能为空哟!");
			return;
		}
		Sku sku = skuService.find(skuId);
		if (sku == null) {
			renderArgumentError("商品没有找到哟!");
			return;
		}
		if (!Product.Type.general.equals(sku.getType())) {
			renderArgumentError(res.format("shop.cart.skuNotForSale"));
			return;
		}
		if (!sku.getIsActive()) {
			renderArgumentError(res.format("shop.cart.skuNotActive"));
			return;
		}
		if (!sku.getIsMarketable()) {
			renderArgumentError(res.format("shop.cart.skuNotMarketable"));
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
			renderArgumentError(res.format("shop.cart.addCartItemCountNotAllowed" , Cart.MAX_CART_ITEM_SIZE));
			return;
		}
		if (CartItem.MAX_QUANTITY != null && skuQuantity > CartItem.MAX_QUANTITY) {
			renderArgumentError(res.format("shop.cart.addQuantityNotAllowed" , CartItem.MAX_QUANTITY));
			return;
		}
		if (skuQuantity > sku.getAvailableStock()) {
			renderArgumentError(res.format("shop.cart.skuLowStock"));
			return;
		}
		if (currentCart == null) {
			currentCart = cartService.create();
		}
		cartService.add(currentCart, sku, quantity);
		if (currentCart != null) {
			data.put("cartKey", currentCart.getCartKey());
			data.put("quantity", currentCart.getSkuQuantity(null));
		}
		renderJson(new DatumResponse(data));
	}
	
	/**
	 * 修改
	 */
	@Before(Tx.class)
	public void modify() {
		Long skuId = getParaToLong("skuId");
		Integer quantity = getParaToInt("quantity");
		String cartKey = getPara("cartKey");
		Cart currentCart = cartService.getCurrent(cartKey);
		
		if (!"post".equalsIgnoreCase(getRequest().getMethod())) {
			renderArgumentError("必须采用POST请求!");
            return;
        }
		Map<String, Object> data = new HashMap<>();
		if (quantity == null || quantity < 1) {
			renderArgumentError("数量不能为空哟!");
			return;
		}
		Sku sku = skuService.find(skuId);
		if (sku == null) {
			renderArgumentError(res.format("shop.cart.skuNotExist"));
			return;
		}
		if (currentCart == null || currentCart.isEmpty()) {
			renderArgumentError(res.format("shop.cart.notEmpty"));
			return;
		}
		Store store = sku.getProduct().getStore();
		if (store == null) {
			renderArgumentError("SKU未找到对应的店铺!");
			return;
		}
		if (!currentCart.contains(sku, null)) {
			renderArgumentError(res.format("shop.cart.cartItemNotExist"));
			return;
		}
		if (!sku.getIsActive()) {
			cartService.remove(currentCart, sku);
			renderArgumentError(res.format("shop.cart.skuNotActive"));
			return;
		}
		if (!sku.getIsMarketable()) {
			cartService.remove(currentCart, sku);
			renderArgumentError(res.format("shop.cart.skuNotMarketable"));
			return;
		}
		if (CartItem.MAX_QUANTITY != null && quantity > CartItem.MAX_QUANTITY) {
			renderArgumentError(res.format("shop.cart.addQuantityNotAllowed", CartItem.MAX_QUANTITY));
			return;
		}
		if (quantity > sku.getAvailableStock()) {
			renderArgumentError(res.format("shop.cart.skuLowStock"));
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
		renderJson(new DatumResponse(data));
	}
	
	/**
	 * 移除
	 */
	@Before(Tx.class)
	public void remove() {
		Long[] skuIds = convertToLong(getPara("skuIds"));
		String cartKey = getPara("cartKey");
		Cart currentCart = cartService.getCurrent(cartKey);
		
		Map<String, Object> data = new HashMap<>();
		List<Sku> skus = skuService.findList(skuIds);
		Store store = null;
		if (CollectionUtils.isNotEmpty(skus)) {
			for (Sku sku : skus) {
				if (sku == null) {
					renderArgumentError(res.format("shop.cart.skuNotExist"));
					return;
				}
				if (currentCart == null || currentCart.isEmpty()) {
					renderArgumentError(res.format("shop.cart.notEmpty"));
					return;
				}
				if (!currentCart.contains(sku, null)) {
					renderArgumentError(res.format("shop.cart.cartItemNotExist"));
					return;
				}
				store = sku.getProduct().getStore();
				cartService.remove(currentCart, sku);
			}
		}
		
		List<Store> stores = currentCart.getStores();
		data.put("isLowStock", currentCart.getIsLowStock(store));
		data.put("quantity", currentCart.getSkuQuantity(store));
		data.put("effectiveRewardPoint", currentCart.getEffectiveRewardPointTotal(stores));
		data.put("effectivePrice", currentCart.getEffectivePriceTotal(stores));
		data.put("promotionDiscount", currentCart.getDiscountTotal(stores));
		data.put("giftNames", currentCart.getGiftNames(store));
		data.put("promotionNames", currentCart.getPromotionNames(store));
		renderJson(new DatumResponse(data));
	}
	
	/**
	 * 清空
	 */
	@Before(Tx.class)
	public void clear() {
		String cartKey = getPara("cartKey");
		Cart currentCart = cartService.getCurrent(cartKey);
		
		if (!"post".equalsIgnoreCase(getRequest().getMethod())) {
			renderArgumentError("必须采用POST请求!");
            return;
        }
		if (currentCart == null) {
			renderArgumentError("购物车没有找到!");
            return;
		}
		cartService.clear(currentCart);
		renderJson(new DatumResponse("OK"));
	}
}
