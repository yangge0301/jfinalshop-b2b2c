package com.jfinalshop.model;

import com.jfinalshop.model.base.BaseProductFavorite;

/**
 * Model - 商品收藏
 * 
 */
public class ProductFavorite extends BaseProductFavorite<ProductFavorite> {
	private static final long serialVersionUID = -4129240798924919545L;
	public static final ProductFavorite dao = new ProductFavorite().dao();
	
	/**
	 * 最大商品收藏数量
	 */
	public static final Integer MAX_PRODUCT_FAVORITE_SIZE = 10;

	/**
	 * 会员
	 */
	private Member member;

	/**
	 * 商品
	 */
	private Product product;

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
	 * 获取商品
	 * 
	 * @return 商品
	 */
	public Product getProduct() {
		if (product == null) {
			product = Product.dao.findById(getProductId());
		}
		return product;
	}

	/**
	 * 设置商品
	 * 
	 * @param product
	 *            商品
	 */
	public void setProduct(Product product) {
		this.product = product;
	}
}
