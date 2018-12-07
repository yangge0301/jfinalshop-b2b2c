package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BaseMemberRank;

/**
 * Model - 会员等级
 * 
 */
public class MemberRank extends BaseMemberRank<MemberRank> {
	private static final long serialVersionUID = -1347994308215849346L;
	public static final MemberRank dao = new MemberRank().dao();
	
	/**
	 * 会员
	 */
	private List<Member> members = new ArrayList<Member>();

	/**
	 * 促销
	 */
	private List<Promotion> promotions = new ArrayList<Promotion>();
	
	
	/**
	 * 获取会员
	 * 
	 * @return 会员
	 */
	public List<Member> getMembers() {
		if (CollectionUtils.isEmpty(members)) {
			String sql = "SELECT * FROM `member` WHERE member_rank_id = ?";
			members = Member.dao.find(sql, getId());
		}
		return members;
	}

	/**
	 * 设置会员
	 * 
	 * @param members
	 *            会员
	 */
	public void setMembers(List<Member> members) {
		this.members = members;
	}

	/**
	 * 获取促销
	 * 
	 * @return 促销
	 */
	public List<Promotion> getPromotions() {
		if (CollectionUtils.isEmpty(promotions)) {
			String sql = "SELECT p.*  FROM promotion p LEFT JOIN promotion_member_rank pmr ON p.id = pmr.promotions_id WHERE pmr.member_ranks_id = ?";
			promotions = Promotion.dao.find(sql, getId());
		}
		return promotions;
	}

	/**
	 * 设置促销
	 * 
	 * @param promotions
	 *            促销
	 */
	public void setPromotions(List<Promotion> promotions) {
		this.promotions = promotions;
	}

	/**
	 * 删除前处理
	 */
	public void preRemove() {
		List<Promotion> promotions = getPromotions();
		if (promotions != null) {
			for (Promotion promotion : promotions) {
				promotion.getMemberRanks().remove(this);
			}
		}
	}
}
