package com.jfinalshop.service;

import java.util.Date;
import java.util.Enumeration;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.jfinalshop.dao.CartDao;
import com.jfinalshop.dao.CartItemDao;
import com.jfinalshop.model.Cart;
import com.jfinalshop.model.CartItem;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Sku;
import com.jfinalshop.util.Assert;
import com.jfinalshop.util.WebUtils;

/**
 * Service - 购物车
 * 
 */
@Singleton
public class CartService extends BaseService<Cart> {

	/**
	 * 构造方法
	 */
	public CartService() {
		super(Cart.class);
	}
	
	@Inject
	private CartDao cartDao;
	@Inject
	private CartItemDao cartItemDao;
	@Inject
	private MemberService memberService;
	
	
	/**
	 * 获取当前购物车
	 * 
	 * @return 当前购物车，若不存在则返回null
	 */
	public Cart getCurrent(HttpServletRequest request) {
		Member currentUser = memberService.getCurrentUser();
		Cart anonymousCart = getAnonymousCart(request);
		if (currentUser != null && anonymousCart != null) {
			anonymousCart.setMemberId(currentUser.getId());
			cartDao.update(anonymousCart);
		}
		return currentUser != null ? currentUser.getCart() : getAnonymousCart(request);
	}

	/**
	 * api获取当前购物车
	 * 
	 * @return 当前购物车，若不存在则返回null
	 */
	public Cart getCurrent(String cartKey) {
		Cart cart = StringUtils.isNotEmpty(cartKey) ? cartDao.find("cart_key", cartKey) : null;
		return cart != null ? cart : null;
	}

	/**
	 * 创建购物车
	 * 
	 * @return 购物车
	 */
	public Cart create() {
		Member currentUser = memberService.getCurrentUser();
		if (currentUser != null && currentUser.getCart() != null) {
			return currentUser.getCart();
		}
		Cart cart = new Cart();
		if (currentUser != null) {
			cart.setMemberId(currentUser.getId());
			currentUser.setCart(cart);
			cart.setExpire(DateUtils.addSeconds(new Date(), Cart.TIMEOUT));
		}
		cart.setCartKey(DigestUtils.md5Hex(UUID.randomUUID() + RandomStringUtils.randomAlphabetic(30)));
		cartDao.save(cart);
		return cart;
	}

	/**
	 * 添加购物车SKU
	 * 
	 * @param cart
	 *            购物车
	 * @param sku
	 *            SKU
	 * @param quantity
	 *            数量
	 */
	public void add(Cart cart, Sku sku, int quantity) {
		Assert.notNull(cart);
		Assert.isTrue(!cart.isNew());
		Assert.notNull(sku);
		Assert.isTrue(!sku.isNew());
		Assert.state(quantity > 0);

		addInternal(cart, sku, quantity);
	}

	/**
	 * 修改购物车SKU
	 * 
	 * @param cart
	 *            购物车
	 * @param sku
	 *            SKU
	 * @param quantity
	 *            数量
	 */
	public void modify(Cart cart, Sku sku, int quantity) {
		Assert.notNull(cart);
		Assert.isTrue(!cart.isNew());
		Assert.notNull(sku);
		Assert.isTrue(!sku.isNew());
		Assert.isTrue(cart.contains(sku, null));
		Assert.state(quantity > 0);

		if (CartItem.MAX_QUANTITY != null && quantity > CartItem.MAX_QUANTITY) {
			return;
		}

		CartItem cartItem = cart.getCartItem(sku, null);
		cartItem.setQuantity(quantity);
		cartItem.update();
	}

	/**
	 * 移除购物车SKU
	 * 
	 * @param cart
	 *            购物车
	 * @param sku
	 *            SKU
	 */
	public void remove(Cart cart, Sku sku) {
		Assert.notNull(cart);
		Assert.isTrue(!cart.isNew());
		Assert.notNull(sku);
		Assert.isTrue(!sku.isNew());
		Assert.isTrue(cart.contains(sku, null));

		CartItem cartItem = cart.getCartItem(sku, null);
		cartItemDao.remove(cartItem);
		cart.remove(cartItem);
	}

	/**
	 * 清空购物车SKU
	 * 
	 * @param cart
	 *            购物车
	 */
	public void clear(Cart cart) {
		Assert.notNull(cart);
		Assert.isTrue(!cart.isNew());

		for (CartItem cartItem : cart.getCartItems()) {
			cartItemDao.remove(cartItem);
		}
		cart.clear();
	}

	/**
	 * 合并购物车
	 * 
	 * @param cart
	 *            购物车
	 */
	public void merge(Cart cart, HttpServletRequest request) {
		Assert.notNull(cart);
		Assert.isTrue(!cart.isNew());
		Assert.notNull(cart.getMember());

		Cart anonymousCart = getAnonymousCart(request);
		if (anonymousCart != null) {
			for (CartItem cartItem : anonymousCart.getCartItems()) {
				Sku sku = cartItem.getSku();
				int quantity = cartItem.getQuantity();
				addInternal(cart, sku, quantity);
			}
			cartDao.remove(anonymousCart);
		}
	}

	/**
	 * 删除过期购物车
	 */
	public void deleteExpired() {
		cartDao.deleteExpired();
	}

	/**
	 * 获取匿名购物车
	 * 
	 * @return 匿名购物车，若不存在则返回null
	 */
	private Cart getAnonymousCart(HttpServletRequest request) {
		if (request == null) {
			return null;
		}
		String key = WebUtils.getCookie(request, Cart.KEY_COOKIE_NAME);
		Cart cart = StringUtils.isNotEmpty(key) ? cartDao.find("cart_key", key) : null;
		return cart != null && cart.getMemberId() == null ? cart : null;
	}

	/**
	 * 添加购物车SKU
	 * 
	 * @param cart
	 *            购物车
	 * @param sku
	 *            SKU
	 * @param quantity
	 *            数量
	 */
	private void addInternal(Cart cart, Sku sku, int quantity) {
		Assert.notNull(cart);
		Assert.isTrue(!cart.isNew());
		Assert.notNull(sku);
		Assert.isTrue(!sku.isNew());
		Assert.state(quantity > 0);

		if (cart.contains(sku, null)) {
			CartItem cartItem = cart.getCartItem(sku, null);
			if (CartItem.MAX_QUANTITY != null && cartItem.getQuantity() + quantity > CartItem.MAX_QUANTITY) {
				return;
			}
			cartItem.add(quantity);
			cartItemDao.update(cartItem);
		} else {
			if (Cart.MAX_CART_ITEM_SIZE != null && cart.size() >= Cart.MAX_CART_ITEM_SIZE) {
				return;
			}
			if (CartItem.MAX_QUANTITY != null && quantity > CartItem.MAX_QUANTITY) {
				return;
			}
			CartItem cartItem = new CartItem();
			cartItem.setQuantity(quantity);
			cartItem.setSkuId(sku.getId());
			cartItem.setCartId(cart.getId());
			cart.add(cartItem);
			cartItemDao.save(cartItem);
		}
	}
	
}