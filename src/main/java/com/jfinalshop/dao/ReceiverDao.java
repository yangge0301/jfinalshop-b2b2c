package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Receiver;
import com.jfinalshop.util.Assert;

/**
 * Dao - 收货地址
 * 
 */
public class ReceiverDao extends BaseDao<Receiver> {

	/**
	 * 构造方法
	 */
	public ReceiverDao() {
		super(Receiver.class);
	}
	
	/**
	 * 查找默认收货地址
	 * 
	 * @param member
	 *            会员
	 * @return 默认收货地址，若不存在则返回最新收货地址
	 */
	public Receiver findDefault(Member member) {
		if (member == null) {
			return null;
		}
		
		String sql = "SELECT * FROM receiver WHERE member_id = ? AND is_default = TRUE";
		Receiver receiver = modelManager.findFirst(sql, member.getId());
		if (receiver == null) {
			sql = "SELECT * FROM receiver WHERE member_id = ? ORDER BY last_modified_date DESC";
			receiver = modelManager.findFirst(sql, member.getId());
		}
		return receiver;
	}

	/**
	 * 查找收货地址
	 * 
	 * @param member
	 *            会员
	 * @return 收货地址
	 */
	public List<Receiver> findList(Member member) {
		if (member == null) {
			return Collections.emptyList();
		}
		String sql = "SELECT * FROM receiver WHERE member_id = ?";
		List<Object> params = new ArrayList<Object>();
		params.add(member.getId());
		return super.findList(sql, params);
	}

	/**
	 * 查找收货地址分页
	 * 
	 * @param member
	 *            会员
	 * @param pageable
	 *            分页信息
	 * @return 收货地址分页
	 */
	public Page<Receiver> findPage(Member member, Pageable pageable) {
		String sqlExceptSelect = "FROM receiver WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		
		if (member != null) {
			sqlExceptSelect += "AND member_id = ?";
			params.add(member.getId());
		}
		return super.findPage(sqlExceptSelect, pageable, params);
	}

	/**
	 * 清除默认
	 * 
	 * @param member
	 *            会员
	 */
	public void clearDefault(Member member) {
		Assert.notNull(member);

		String sql = "UPDATE receiver SET is_default = FALSE WHERE member_id = ? AND member_id = TRUE";
		Db.update(sql, member.getId());
	}

	/**
	 * 清除默认
	 * 
	 * @param member
	 *            会员
	 * @param exclude
	 *            排除收货地址
	 */
	public void clearDefault(Member member, Receiver exclude) {
		Assert.notNull(member);
		Assert.notNull(exclude);

		String sql = "UPDATE receiver SET is_default = FALSE WHERE member_id = ? AND is_default = TRUE AND id != ?";
		Db.update(sql, member.getId(), exclude.getId());
	}

}