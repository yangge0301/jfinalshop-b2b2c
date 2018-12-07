package com.jfinalshop.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.jfinalshop.Setting;
import com.jfinalshop.model.base.BaseCartItem;
import com.jfinalshop.util.SystemUtils;

/**
 * Model - 购物车项
 * 
 */
public class CartItem extends BaseCartItem<CartItem> {
	private static final long serialVersionUID = -5006709902370682355L;
	public static final CartItem dao = new CartItem().dao();
	
	/**
	 * 最大数量
	 */
	public static final Integer MAX_QUANTITY = 10000;
	
	/**
	 * SKU
	 */
	private Sku sku;

	/**
	 * 购物车
	 */
	private Cart cart;
	
	
	/**
	 * 获取SKU
	 * 
	 * @return SKU
	 */
	public Sku getSku() {
		if (sku == null) {
			sku = Sku.dao.findById(getSkuId());
		}
		return sku;
	}

	/**
	 * 设置SKU
	 * 
	 * @param sku
	 *            SKU
	 */
	public void setSku(Sku sku) {
		this.sku = sku;
	}

	/**
	 * 获取购物车
	 * 
	 * @return 购物车
	 */
	public Cart getCart() {
		if (cart == null) {
			cart = Cart.dao.findById(getCartId());
		}
		return cart;
	}

	/**
	 * 设置购物车
	 * 
	 * @param cart
	 *            购物车
	 */
	public void setCart(Cart cart) {
		this.cart = cart;
	}

	/**
	 * 获取SKU重量
	 * 
	 * @return SKU重量
	 */
	public int getWeight() {
		if (getSku() != null && getSku().getWeight() != null && getQuantity() != null) {
			return getSku().getWeight() * getQuantity();
		} else {
			return 0;
		}
	}

	/**
	 * 获取赠送积分
	 * 
	 * @return 赠送积分
	 */
	public long getRewardPoint() {
		if (getSku() != null && getSku().getRewardPoint() != null && getQuantity() != null) {
			return getSku().getRewardPoint() * getQuantity();
		} else {
			return 0L;
		}
	}

	/**
	 * 获取兑换积分
	 * 
	 * @return 兑换积分
	 */
	public long getExchangePoint() {
		if (getSku() != null && getSku().getExchangePoint() != null && getQuantity() != null) {
			return getSku().getExchangePoint() * getQuantity();
		} else {
			return 0L;
		}
	}

	/**
	 * 获取价格
	 * 
	 * @return 价格
	 */
	public BigDecimal getPrice() {
		if (getSku() != null && getSku().getPrice() != null) {
			Setting setting = SystemUtils.getSetting();
			if (getCart() != null && getCart().getMember() != null && getCart().getMember().getMemberRank() != null) {
				MemberRank memberRank = getCart().getMember().getMemberRank();
				if (memberRank.getScale() != null) {
					return setting.setScale(getSku().getPrice().multiply(new BigDecimal(String.valueOf(memberRank.getScale()))));
				}
			}
			return setting.setScale(getSku().getPrice());
		} else {
			return BigDecimal.ZERO;
		}
	}

	/**
	 * 获取小计
	 * 
	 * @return 小计
	 */
	public BigDecimal getSubtotal() {
		if (getQuantity() != null) {
			return getPrice().multiply(new BigDecimal(getQuantity()));
		} else {
			return BigDecimal.ZERO;
		}
	}

	/**
	 * 获取是否有效
	 * 
	 * @return 是否有效
	 */
	public boolean getIsActive() {
		return getSku() != null && getSku().getIsActive();
	}

	/**
	 * 获取是否上架
	 * 
	 * @return 是否上架
	 */
	public boolean getIsMarketable() {
		return getSku() != null && getSku().getIsMarketable();
	}

	/**
	 * 获取是否需要物流
	 * 
	 * @return 是否需要物流
	 */
	public boolean getIsDelivery() {
		return getSku() != null && getSku().getIsDelivery();
	}

	/**
	 * 获取是否库存不足
	 * 
	 * @return 是否库存不足
	 */
	public boolean getIsLowStock() {
		return getQuantity() != null && getSku() != null && getQuantity() > getSku().getAvailableStock();
	}

	/**
	 * 增加SKU数量
	 * 
	 * @param quantity
	 *            数量
	 */
	public void add(int quantity) {
		if (quantity < 1) {
			return;
		}
		if (getQuantity() != null) {
			setQuantity(getQuantity() + quantity);
		} else {
			setQuantity(quantity);
		}
	}

	/**
	 * 获取店铺
	 * 
	 * @return 店铺
	 */
	public Store getStore() {
		if (getSku() != null && getSku().getProduct() != null) {
			return getSku().getProduct().getStore();
		}
		return null;
	}

	/**
	 * 获取促销名称
	 * 
	 * @param store
	 *            店铺
	 * @return 促销名称
	 */
	public List<String> getPromotionNames(Store store) {
		List<String> promotionNames = new ArrayList<>();
		for (Promotion promotion : getSku().getValidPromotions()) {
			if (promotion.getStore().equals(store)) {
				promotionNames.add(promotion.getName());
			}
		}
		return promotionNames;
	}

}
