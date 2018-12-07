package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BaseConsultation;

/**
 * Model - 咨询
 * 
 */
public class Consultation extends BaseConsultation<Consultation> {
	private static final long serialVersionUID = -5464078454735650427L;
	public static final Consultation dao = new Consultation().dao();
	
	/**
	 * 路径
	 */
	private static final String PATH = "/consultation/detail/%d";
	
	/**
	 * 会员
	 */
	private Member member;

	/**
	 * 商品
	 */
	private Product product;

	/**
	 * 咨询
	 */
	private Consultation forConsultation;

	/**
	 * 回复
	 */
	private List<Consultation> replyConsultations = new ArrayList<Consultation>();

	/**
	 * 店铺
	 */
	private Store store;
	
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
	 * 获取咨询
	 * 
	 * @return 咨询
	 */
	public Consultation getForConsultation() {
		if (forConsultation == null) {
			forConsultation = Consultation.dao.findById(getForConsultationId());
		}
		return forConsultation;
	}

	/**
	 * 设置咨询
	 * 
	 * @param forConsultation
	 *            咨询
	 */
	public void setForConsultation(Consultation forConsultation) {
		this.forConsultation = forConsultation;
	}

	/**
	 * 获取回复
	 * 
	 * @return 回复
	 */
	public List<Consultation> getReplyConsultations() {
		if (CollectionUtils.isEmpty(replyConsultations)) {
			String sql = "SELECT * FROM `consultation` WHERE for_consultation_id = ?";
			replyConsultations = Consultation.dao.find(sql, getId());
		}
		return replyConsultations;
	}

	/**
	 * 设置回复
	 * 
	 * @param replyConsultations
	 *            回复
	 */
	public void setReplyConsultations(List<Consultation> replyConsultations) {
		this.replyConsultations = replyConsultations;
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
	 * 获取路径
	 * 
	 * @return 路径
	 */
	public String getPath() {
		return String.format(Consultation.PATH, getProduct().getId());
	}
}
