package com.jfinalshop.dao;

import com.jfinalshop.Setting;
import com.jfinalshop.model.Sms;
import com.jfinalshop.util.Assert;
import com.xiaoleilu.hutool.date.DateUtil;

public class SmsDao extends BaseDao<Sms> {

	/**
	 * 构造方法
	 */
	public SmsDao() {
		super(Sms.class);
	}
	
	/**
	 * 查找实体对象
	 * 
	 * @param attributeName
	 *            属性名称
	 * @param attributeValue
	 *            属性值
	 * @return 实体对象，若不存在则返回null
	 */
	public Sms findByMobile(String mobile, String code, Setting.SmsType type, Boolean isUsed) {
		Assert.hasText(mobile);

		String sql = "SELECT * FROM `sms` WHERE mobile = ? AND code = ? AND type = ? AND is_used = ? AND expire >= ?";
		return modelManager.findFirst(sql, mobile, code, type.ordinal(), isUsed, DateUtil.now());
	}
}
