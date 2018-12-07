package com.jfinalshop.dao;

import java.math.BigDecimal;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.MemberRank;
import com.jfinalshop.util.Assert;

/**
 * Dao - 会员等级
 * 
 */
public class MemberRankDao extends BaseDao<MemberRank> {

	/**
	 * 构造方法
	 */
	public MemberRankDao() {
		super(MemberRank.class);
	}
	
	/**
	 * 查找默认会员等级
	 * 
	 * @return 默认会员等级，若不存在则返回null
	 */
	public MemberRank findDefault() {
		String sql = "SELECT * FROM member_rank WHERE is_default = TRUE";
		return modelManager.findFirst(sql);
	}

	/**
	 * 根据消费金额查找符合此条件的最高会员等级
	 * 
	 * @param amount
	 *            消费金额
	 * @return 会员等级，不包含特殊会员等级，若不存在则返回null
	 */
	public MemberRank findByAmount(BigDecimal amount) {
		String sql = "SELECT * FROM member_rank WHERE is_special = FALSE AND amount <= ? ORDER BY amount DESC";
		return modelManager.findFirst(sql, amount);
	}

	/**
	 * 清除默认
	 */
	public void clearDefault() {
		String sql = "UPDATE member_rank SET is_default = FALSE WHERE is_default = TRUE";
		Db.update(sql);
	}

	/**
	 * 清除默认
	 * 
	 * @param exclude
	 *            排除会员等级
	 */
	public void clearDefault(MemberRank exclude) {
		Assert.notNull(exclude);

		String sql = "UPDATE member_rank SET is_default = FALSE WHERE is_default = TRUE AND id != ?";
		Db.update(sql, exclude.getId());
	}

}