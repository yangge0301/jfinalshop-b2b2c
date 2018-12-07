package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.PointLog;

/**
 * Dao - 积分记录
 * 
 */
public class PointLogDao extends BaseDao<PointLog> {

	/**
	 * 构造方法
	 */
	public PointLogDao() {
		super(PointLog.class);
	}
	
	/**
	 * 查找积分记录分页
	 * 
	 * @param member
	 *            会员
	 * @param pageable
	 *            分页信息
	 * @return 积分记录分页
	 */
	public Page<PointLog> findPage(Member member, Pageable pageable) {
		if (member == null) {
			return null;
		}
		String sqlExceptSelect = "FROM point_log WHERE member_id = ?";
		List<Object> params = new ArrayList<Object>();
		params.add(member.getId());
		return super.findPage(sqlExceptSelect, pageable, params);
	}

}