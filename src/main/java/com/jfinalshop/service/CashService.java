package com.jfinalshop.service;

import java.math.BigDecimal;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.dao.CashDao;
import com.jfinalshop.model.Business;
import com.jfinalshop.model.BusinessDepositLog;
import com.jfinalshop.model.Cash;
import com.jfinalshop.util.Assert;

/**
 * Service - 提现
 * 
 */
@Singleton
public class CashService extends BaseService<Cash> {

	/**
	 * 构造方法
	 */
	public CashService() {
		super(Cash.class);
	}
	
	@Inject
	private CashDao cashDao;
	@Inject
	private BusinessService businessService;
	
	/**
	 * 申请提现
	 * 
	 * @param cash
	 *            提现
	 * @param business
	 *            商家
	 */
	public void applyCash(Cash cash, Business business) {
		Assert.notNull(cash);
		Assert.notNull(business);
		Assert.isTrue(cash.getAmount().compareTo(BigDecimal.ZERO) > 0);

		cash.setStatus(Cash.Status.pending.ordinal());
		cash.setBusinessId(business.getId());
		cashDao.save(cash);

		businessService.addBalance(cash.getBusiness(), cash.getAmount().negate(), BusinessDepositLog.Type.cash, null);
		businessService.addFrozenFund(business, cash.getAmount());

	}
	

	/**
	 * 查找提现记录分页
	 * 
	 * @param business
	 *            商家
	 * @param pageable
	 *            分页信息
	 * @return 提现记录分页
	 */
	public Page<Cash> findPage(Business business, Pageable pageable) {
		return cashDao.findPage(business, pageable);
	}

	/**
	 * 审核提现
	 * 
	 * @param cash
	 *            提现
	 * @param isPassed
	 *            是否审核通过
	 */
	public void review(Cash cash, Boolean isPassed) {
		Assert.notNull(cash);
		Assert.notNull(isPassed);

		Business business = cash.getBusiness();
		if (isPassed) {
			Assert.notNull(cash.getAmount());
			Assert.notNull(cash.getBusiness());
			Assert.notNull(cash.getBusiness());
			cash.setStatus(Cash.Status.approved.ordinal());
		} else {
			cash.setStatus(Cash.Status.failed.ordinal());
			businessService.addBalance(cash.getBusiness(), cash.getAmount(), BusinessDepositLog.Type.unfrozen, null);
		}
		businessService.addFrozenFund(business, cash.getAmount().negate());
		cashDao.update(cash);
	}
}