package com.jfinalshop.model;

import com.jfinalshop.model.base.BasePromotionMemberRank;

/**
 * Model - 促销会员等级中间表
 * 
 */
public class PromotionMemberRank extends BasePromotionMemberRank<PromotionMemberRank> {
	private static final long serialVersionUID = -4213974174429728467L;
	public static final PromotionMemberRank dao = new PromotionMemberRank().dao();
}
