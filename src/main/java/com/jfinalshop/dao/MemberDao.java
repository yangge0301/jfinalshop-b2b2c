package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.MemberAttribute;
import com.jfinalshop.util.StringUtils;
import com.xiaoleilu.hutool.date.DateUtil;

/**
 * Dao - 会员
 * 
 */
public class MemberDao extends BaseDao<Member> {

	/**
	 * 构造方法
	 */
	public MemberDao() {
		super(Member.class);
	}
	
	/**
	 * 查找会员分页
	 * 
	 * @param rankingType
	 *            排名类型
	 * @param pageable
	 *            分页信息
	 * @return 会员分页
	 */
	public Page<Member> findPage(Member.RankingType rankingType, Pageable pageable) {
		String sqlExceptSelect = "SELECT * FROM member WHERE 1 = 1 ";
		if (rankingType != null) {
			switch (rankingType) {
			case point:
				sqlExceptSelect += "ORDER BY `point` DESC";
				break;
			case balance:
				sqlExceptSelect += "ORDER BY `balance` DESC";
				break;
			case amount:
				sqlExceptSelect += "ORDER BY `amount` DESC";
				break;
			default:
				break;
			}
		}
		return super.findPage(sqlExceptSelect, pageable, null);
	}

	/**
	 * 查询会员注册数
	 * 
	 * @param beginDate
	 *            起始日期
	 * @param endDate
	 *            结束日期
	 * @return 会员注册数
	 */
	public Long registerMemberCount(Date beginDate, Date endDate) {
		String sql = "SELECT COUNT(1) FROM member m, `user` u WHERE m.id = u.id ";
		List<Object> params = new ArrayList<Object>();
		
		if (beginDate != null) {
			sql += " AND u.`created_date` >= ?";
			params.add(DateUtil.formatDateTime(beginDate));
		}
		if (endDate != null) {
			sql += " AND u.`created_date` <= ?";
			params.add(DateUtil.formatDateTime(endDate));
		}
		return super.count(sql, params);
	}

	/**
	 * 清除会员注册项值
	 * 
	 * @param memberAttribute
	 *            会员注册项
	 */
	public void clearAttributeValue(MemberAttribute memberAttribute) {
		if (memberAttribute == null || memberAttribute.getTypeName() == null || memberAttribute.getPropertyIndex() == null) {
			return;
		}

		String propertyName;
		switch (memberAttribute.getTypeName()) {
		case text:
		case select:
		case checkbox:
			propertyName = StringUtils.camelToUnderline(Member.ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX) + memberAttribute.getPropertyIndex();
			break;
		default:
			propertyName = String.valueOf(memberAttribute.getTypeName());
			break;
		}
		String sql = "UPDATE member SET " + propertyName + " = NULL";
		Db.update(sql);
	}

}