package com.jfinalshop.service;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.dao.MemberDepositLogDao;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.MemberDepositLog;

/**
 * Service - 会员预存款记录
 * 
 */
@Singleton
public class MemberDepositLogService extends BaseService<MemberDepositLog> {

	/**
	 * 构造方法
	 */
	public MemberDepositLogService() {
		super(MemberDepositLog.class);
	}
	
	@Inject
	private MemberDepositLogDao memberDepositLogDao;
	
	/**
	 * 查找会员预存款记录分页
	 * 
	 * @param member
	 *            会员
	 * @param pageable
	 *            分页信息
	 * @return 会员预存款记录分页
	 */
	public Page<MemberDepositLog> findPage(Member member, Pageable pageable) {
		return memberDepositLogDao.findPage(member, pageable);
	}

}