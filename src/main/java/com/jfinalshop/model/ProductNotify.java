package com.jfinalshop.model;

import org.apache.commons.lang3.StringUtils;

import com.jfinalshop.model.base.BaseProductNotify;

/**
 * Model - 到货通知
 * 
 */
public class ProductNotify extends BaseProductNotify<ProductNotify> {
	private static final long serialVersionUID = -6220032110888351694L;
	public static final ProductNotify dao = new ProductNotify().dao();
	
	/**
	 * 会员
	 */
	private Member member;

	/**
	 * SKU
	 */
	private Sku sku;

	/**
	 * 获取会员
	 * 
	 * @return 会员
	 */
	public Member getMember() {
		if (member == null) {
			member = Member.dao.findById(getMemberId());
		}
		return member;
	}

	/**
	 * 设置会员
	 * 
	 * @param member
	 *            会员
	 */
	public void setMember(Member member) {
		this.member = member;
	}
	
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
	 * 获取所属店铺
	 * 
	 * @return 所属店铺
	 */
	public Store getStore() {
		return getSku().getProduct().getStore();
	}

	/**
	 * 持久化前处理
	 */
	public void prePersist() {
		setEmail(StringUtils.lowerCase(getEmail()));
	}

}
