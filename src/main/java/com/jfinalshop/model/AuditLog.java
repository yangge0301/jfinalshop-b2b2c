package com.jfinalshop.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinalshop.model.base.BaseAuditLog;

/**
 * Model - 审计日志
 * 
 */
public class AuditLog extends BaseAuditLog<AuditLog> {
	private static final long serialVersionUID = -1954099781788238694L;
	public static final AuditLog dao = new AuditLog().dao();
	
	/**
	 * "审计日志"属性名称
	 */
	public static final String AUDIT_LOG_ATTRIBUTE_NAME = AuditLog.class.getName() + ".AUDIT_LOG";
	
	/**
	 * 用户
	 */
	private Admin admin;
	
	
	/**
	 * 获取用户
	 * 
	 * @return 用户
	 */
	public Admin getAdmin() {
		if (admin == null) {
			admin = Admin.dao.findById(getAdminId());
		}
		return admin;
	}

	/**
	 * 设置用户
	 * 
	 * @param user
	 *            用户
	 */
	public void setAdmin(Admin admin) {
		this.admin = admin;
	}
	
	/**
	 * 获取请求参数
	 * 
	 * @return 请求参数
	 */
	public Map<String, String[]> getParametersConverter() {
		JSONObject jsonObject = JSONObject.parseObject(getParameters());
		Map<String, String[]> resultMap = new HashMap<String, String[]>();
		Iterator<Entry<String, Object>> it = jsonObject.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> param = (Map.Entry<String, Object>) it.next();
			resultMap.put(param.getKey(), json2List(param.getValue()));
		}
		return resultMap;
	}

	
	private static String[] json2List(Object json) {
		JSONArray jsonArr = (JSONArray) json;
		String[] arrList = new String[] {""};
		for (int i = 0; i < jsonArr.size(); i++) {
			arrList[i] = jsonArr.getString(i);
		}
		return arrList;
	}
	
}
