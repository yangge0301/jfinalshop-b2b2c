package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BaseReview;

/**
 * Model - 评论
 * 
 */
public class Review extends BaseReview<Review> {
	private static final long serialVersionUID = -7786965922193880425L;
	public static final Review dao = new Review().dao();
	
	/**
	 * 路径
	 */
	private static final String PATH = "/review/detail/%d";

	/**
	 * 类型
	 */
	public enum Type {

		/**
		 * 好评
		 */
		positive,

		/**
		 * 中评
		 */
		moderate,

		/**
		 * 差评
		 */
		negative
	}
	
	/**
	 * 会员
	 */
	private Member member;

	/**
	 * 商品
	 */
	private Product product;

	/**
	 * 店铺
	 */
	private Store store;

	/**
	 * 回复
	 */
	private List<Review> replyReviews = new ArrayList<>();

	/**
	 * 评论
	 */
	private Review forReview;
	
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

	/**
	 * 获取店铺
	 * 
	 * @return 店铺
	 */
	public Store getStore() {
		if (store == null) {
			store = Store.dao.findById(getStoreId());
		}
		return store;
	}

	/**
	 * 设置店铺
	 * 
	 * @param store
	 *            店铺
	 */
	public void setStore(Store store) {
		this.store = store;
	}

	/**
	 * 获取回复
	 * 
	 * @return 回复
	 */
	public List<Review> getReplyReviews() {
		if (CollectionUtils.isEmpty(replyReviews)) {
			String sql = "SELECT * FROM review r WHERE r.for_review_id = ?";
			replyReviews = Review.dao.find(sql, getId());
		}
		return replyReviews;
	}

	/**
	 * 设置回复
	 * 
	 * @param replyReviews
	 *            回复
	 */
	public void setReplyReviews(List<Review> replyReviews) {
		this.replyReviews = replyReviews;
	}

	/**
	 * 获取评论
	 * 
	 * @return 评论
	 */
	public Review getForReview() {
		if (forReview == null) {
			forReview = Review.dao.findById(getForReviewId());
		}
		return forReview;
	}

	/**
	 * 设置评论
	 * 
	 * @param forReview
	 *            评论
	 */
	public void setForReview(Review forReview) {
		this.forReview = forReview;
	}

	/**
	 * 获取路径
	 * 
	 * @return 路径
	 */
	public String getPath() {
		return String.format(Review.PATH, getProduct().getId());
	}
}
