package com.jfinalshop.dao;

import com.jfinalshop.model.Seo;

/**
 * Dao - SEO设置
 * 
 */
public class SeoDao extends BaseDao<Seo> {

	/**
	 * 构造方法
	 */
	public SeoDao() {
		super(Seo.class);
	}
	
	/**
	 * 查找SEO设置
	 * 
	 * @param type
	 *            类型
	 * @return SEO设置
	 */
	public Seo find(Seo.Type type) {
		if (type == null) {
			return null;
		}
		String sql = "SELECT * FROM seo WHERE `type` = ?";
		return modelManager.findFirst(sql, type.ordinal());
	}

}