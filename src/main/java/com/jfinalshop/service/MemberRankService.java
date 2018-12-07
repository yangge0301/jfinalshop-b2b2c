package com.jfinalshop.service;

import java.math.BigDecimal;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import org.apache.commons.lang.BooleanUtils;

import com.jfinalshop.dao.MemberRankDao;
import com.jfinalshop.model.MemberRank;
import com.jfinalshop.util.Assert;

/**
 * Service - 会员等级
 * 
 */
@Singleton
public class MemberRankService extends BaseService<MemberRank> {

	/**
	 * 构造方法
	 */
	public MemberRankService() {
		super(MemberRank.class);
	}
	
	@Inject
	private MemberRankDao memberRankDao;

	
	/**
	 * 判断消费金额是否存在
	 * 
	 * @param amount
	 *            消费金额
	 * @return 消费金额是否存在
	 */
	public boolean amountExists(BigDecimal amount) {
		return memberRankDao.exists("amount", amount);
	}

	/**
	 * 判断消费金额是否唯一
	 * 
	 * @param id
	 *            ID
	 * @param amount
	 *            消费金额
	 * @return 消费金额是否唯一
	 */
	public boolean amountUnique(Long id, BigDecimal amount) {
		return memberRankDao.unique(id, "amount", amount);
	}

	/**
	 * 查找默认会员等级
	 * 
	 * @return 默认会员等级，若不存在则返回null
	 */
	public MemberRank findDefault() {
		return memberRankDao.findDefault();
	}

	/**
	 * 根据消费金额查找符合此条件的最高会员等级
	 * 
	 * @param amount
	 *            消费金额
	 * @return 会员等级，不包含特殊会员等级，若不存在则返回null
	 */
	public MemberRank findByAmount(BigDecimal amount) {
		return memberRankDao.findByAmount(amount);
	}

	@Override
	public MemberRank save(MemberRank memberRank) {
		Assert.notNull(memberRank);

		if (BooleanUtils.isTrue(memberRank.getIsDefault())) {
			memberRankDao.clearDefault();
		}
		return super.save(memberRank);
	}
	
	@Override
	public MemberRank update(MemberRank memberRank) {
		Assert.notNull(memberRank);

		MemberRank pMemberRank = super.update(memberRank);
		if (BooleanUtils.isTrue(pMemberRank.getIsDefault())) {
			memberRankDao.clearDefault(pMemberRank);
		}
		return pMemberRank;
	}
}