package com.jfinalshop.dao;

import com.jfinalshop.model.MessageConfig;

/**
 * Dao - 消息配置
 * 
 */
public class MessageConfigDao extends BaseDao<MessageConfig> {

	/**
	 * 构造方法
	 */
	public MessageConfigDao() {
		super(MessageConfig.class);
	}
	
	/**
	 * 查找消息配置
	 * 
	 * @param type
	 *            类型
	 * @return 消息配置
	 */
	public MessageConfig find(MessageConfig.Type type) {
		if (type == null) {
			return null;
		}
		String sql = "SELECT * FROM message_config WHERE type = ?";
		return modelManager.findFirst(sql, type.ordinal());
	}

}