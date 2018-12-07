package com.jfinalshop.dao;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.Menu;

/**
 * Dao - 菜单
 * 
 */
public class MenuDao extends BaseDao<Menu> {

	/**
	 * 构造方法
	 */
	public MenuDao() {
		super(Menu.class);
	}
	
	/**
	 * 获取最大的菜单编码
	 * 
	 * @param parentId
	 *            上级菜单Id
	 * @return 用户名是否存在
	 */
	public String findLevelCode(Long parentId) {
		String sql = "SELECT MAX(m.`level_code`) FROM menu m WHERE m.`parent_id` = ?";
		return Db.queryStr(sql, parentId == 0L ? 0 : parentId);
	}
	
	
	/**
	 * 查找下级菜单分类
	 * 
	 * @param menu
	 *            菜单分类
	 * @return 下级菜单分类
	 */
	public List<Menu> findChildren(Menu menu) {
		String sql = "SELECT * FROM menu m WHERE m.`parent_id` = ?";
		return modelManager.find(sql, menu.getId());
	}
	
}
