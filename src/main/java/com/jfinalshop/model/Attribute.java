package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BaseAttribute;
import com.jfinalshop.util.JsonUtils;

/**
 * Model - 属性
 * 
 */
public class Attribute extends BaseAttribute<Attribute> {
	private static final long serialVersionUID = -2347629241645979929L;
	public static final Attribute dao = new Attribute().dao();
	
	/**
	 * 绑定分类
	 */
	private ProductCategory productCategory;

	/**
	 * 可选项
	 */
	private List<String> options = new ArrayList<String>();
	
	
	/**
	 * 获取绑定分类
	 * 
	 * @return 绑定分类
	 */
	public ProductCategory getProductCategory() {
		if (productCategory == null) {
			productCategory = ProductCategory.dao.findById(getProductCategoryId());
		}
		return productCategory;
	}

	/**
	 * 设置绑定分类
	 * 
	 * @param productCategory
	 *            绑定分类
	 */
	public void setProductCategory(ProductCategory productCategory) {
		this.productCategory = productCategory;
	}

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
	public void setOptions(List<String> options) {
		this.options = options;
	}

	/**
	 * 重写equals方法
	 * 
	 * @param obj
	 *            对象
	 * @return 是否相等
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (!Attribute.class.isAssignableFrom(obj.getClass())) {
			return false;
		}
		Attribute other = (Attribute) obj;
		return getId() != null ? getId().equals(other.getId()) : false;
	}

	/**
	 * 重写hashCode方法
	 * 
	 * @return HashCode
	 */
	@Override
	public int hashCode() {
		int hashCode = 17;
		hashCode += getId() != null ? getId().hashCode() * 31 : 0;
		return hashCode;
	}
}
