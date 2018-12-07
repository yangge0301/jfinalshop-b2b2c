package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BaseBusinessAttribute;
import com.jfinalshop.util.JsonUtils;

/**
 * Entity - 商家注册项
 * 
 */
public class BusinessAttribute extends BaseBusinessAttribute<BusinessAttribute> {
	private static final long serialVersionUID = 5171028003584284917L;
	public static final BusinessAttribute dao = new BusinessAttribute().dao();
	
	/**
	 * 类型
	 */
	public enum Type {

		/**
		 * 名称
		 */
		name,

		/**
		 * 营业执照
		 */
		licenseNumber,

		/**
		 * 营业执照图片
		 */
		licenseImage,

		/**
		 * 法人姓名
		 */
		legalPerson,

		/**
		 * 法人身份证
		 */
		idCard,

		/**
		 * 法人身份证图片
		 */
		idCardImage,

		/**
		 * 电话
		 */
		phone,

		/**
		 * 组织机构代码
		 */
		organizationCode,

		/**
		 * 组织机构代码证图片
		 */
		organizationImage,

		/**
		 * 纳税人识别号
		 */
		identificationNumber,

		/**
		 * 税务登记证图片
		 */
		taxImage,

		/**
		 * 银行开户名
		 */
		bankName,

		/**
		 * 公司银行账号
		 */
		bankAccount,

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
		checkbox,

		/**
		 * 图片
		 */
		image,

		/**
		 * 日期
		 */
		date
	}
	
	/**
	 * 类型名称
	 */
	public Type getTypeName() {
		return getType() != null ? Type.values()[getType()] : null;
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
