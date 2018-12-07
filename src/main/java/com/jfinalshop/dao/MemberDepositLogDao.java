package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.MemberDepositLog;

/**
 * Dao - 会员预存款记录
 * 
 */
public class MemberDepositLogDao extends BaseDao<MemberDepositLog> {

	/**
	 * 构造方法
	 */
	public MemberDepositLogDao() {
		super(MemberDepositLog.class);
	}
	
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
		if (member == null) {
			return null;
		}
		String sqlExceptSelect = "FROM member_deposit_log WHERE member_id = ?";
		List<Object> params = new ArrayList<Object>();
		params.add(member.getId());
		return super.findPage(sqlExceptSelect, pageable, params);
	}

}