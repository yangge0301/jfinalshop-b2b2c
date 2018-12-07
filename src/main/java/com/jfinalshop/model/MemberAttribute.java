package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BaseMemberAttribute;
import com.jfinalshop.util.JsonUtils;

/**
 * Model - 会员注册项
 * 
 */
public class MemberAttribute extends BaseMemberAttribute<MemberAttribute> {
	private static final long serialVersionUID = 3443511843148683127L;
	public static final MemberAttribute dao = new MemberAttribute().dao();
	
	/**
	 * 类型
	 */
	public enum Type {

		/**
		 * 姓名
		 */
		name,

		/**
		 * 性别
		 */
		gender,

		/**
		 * 出生日期
		 */
		birth,

		/**
		 * 地区
		 */
		area,

		/**
		 * 地址
		 */
		address,

		/**
		 * 邮编
		 */
		zipCode,

		/**
		 * 电话
		 */
		phone,

		/**
		 * 文本
		 */
		text,

		/**
		 * 单选项
		 */
		select,

		/**
		 * 多选项
		 */
		checkbox
	}

	/**
	 * 类型名称
	 */
	public Type getTypeName() {
		return Type.values()[getType()];
	}
	

	/**
	 * 可选项
	 */
	private List<String> options = new ArrayList<String>();
	
	
	/**
	 * 获取可选项
	 * 
	 * @return 可选项
	 */
	public List<String> getOptionsConverter() {
		if (CollectionUtils.isEmpty(options)) {
			options = JsonUtils.convertJsonStrToList(getOptions());
		}
		return options;
	}

	/**
	 * 设置可选项
	 * 
	 * @param options
	 *            可选项
	 */
	public void setOptionsConverter(List<String> options) {
		this.options = options;
	}
}
