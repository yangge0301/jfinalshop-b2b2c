package com.jfinalshop.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinal.kit.JsonKit;
import com.jfinalshop.model.base.BasePluginConfig;
import com.jfinalshop.util.JsonUtils;

/**
 * Model - 插件配置
 * 
 */
public class PluginConfig extends BasePluginConfig<PluginConfig> {
	private static final long serialVersionUID = 3672462909926498260L;
	public static final PluginConfig dao = new PluginConfig().dao();
	
	
	/**
	 * 属性
	 */
	private Map<String, String> attributes = new HashMap<>();
	
	/**
	 * 获取属性
	 * 
	 * @return 属性
	 */
	public Map<String, String> getAttributesConverter() {
		if (MapUtils.isEmpty(attributes)) {
			String attributeNames = getAttributes();
			attributes = JsonUtils.convertJsonStrToMap(attributeNames);
		}
		return attributes;
	}

	/**
	 * 设置属性
	 * 
	 * @param attributes
	 *            属性
	 */
	public void setAttributes(Map<String, String> attributes) {
		set("attributes", JsonKit.toJson(attributes));
	}

	/**
	 * 获取属性值
	 * 
	 * @param name
	 *            属性名称
	 * @return 属性值
	 */
	public String getAttribute(String name) {
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		return getAttributes() != null ? getAttributesConverter().get(name) : null;
	}

}
