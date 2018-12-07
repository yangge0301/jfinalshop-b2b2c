package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BaseParameter;
import com.jfinalshop.util.JsonUtils;

/**
 * Model - 参数
 * 
 */
public class Parameter extends BaseParameter<Parameter> {
	private static final long serialVersionUID = -1547404574180576143L;
	public static final Parameter dao = new Parameter().dao();
	
	/** 参数组 */
	private String group;
	
	/**
	 * 绑定分类
	 */
	private ProductCategory productCategory;

	/**
	 * 参数名称
	 */
	private List<String> names = new ArrayList<>();
	
	/**
	 * 获取参数组
	 * 
	 * @return 参数组
	 */
	public String getGroup() {
		group = getParameterGroup();
		return group;
	}

	/**
	 * 设置参数组
	 * 
	 * @param group
	 *            参数组
	 */
	public void setGroup(String group) {
		setParameterGroup(group);
	}
	
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
	 * 获取参数名称
	 * 
	 * @return 参数名称
	 */
	public List<String> getNamesConverter() {
		if (CollectionUtils.isEmpty(names)) {
			names = JsonUtils.convertJsonStrToList(getNames());
		}
		return names;
	}

	/**
	 * 设置参数名称
	 * 
	 * @param names
	 *            参数名称
	 */
	public void setNames(List<String> names) {
		this.names = names;
	}

}
