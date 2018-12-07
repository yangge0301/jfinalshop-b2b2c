package com.jfinalshop.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.base.BaseIdGenerator;

/**
 * ID生成器
 * 
 */
public class IdGenerator extends BaseIdGenerator<IdGenerator> {
	private static final long serialVersionUID = -5146304955812924009L;
	public static final IdGenerator dao = new IdGenerator().dao();
	
	/**
	 * 获取末值
	 * 
	 * @param type
	 *            类型
	 * @return 末值
	 */
	public long getNextVal(String sequenceName) {
		String sql = "SELECT * FROM id_generator WHERE sequence_name = ?";
		IdGenerator idGenerator = findFirst(sql, sequenceName);
		if (idGenerator == null) {
			throw new IllegalArgumentException("未找到序列名");
		}
		long nextVal = idGenerator.getNextVal();
		String updateSql = "UPDATE id_generator SET next_val = ? WHERE sequence_name = ? AND next_val = ?";
		int result = Db.update(updateSql, nextVal + 1, sequenceName, nextVal);
		return 0 < result ? nextVal : getNextVal(sequenceName);
	}
	
	
}
