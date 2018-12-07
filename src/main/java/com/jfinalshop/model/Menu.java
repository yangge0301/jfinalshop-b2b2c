package com.jfinalshop.model;

import com.jfinalshop.model.base.BaseMenu;

/**
 * Model - 菜单
 * 
 */
public class Menu extends BaseMenu<Menu> {
	private static final long serialVersionUID = -1126759319717533192L;
	public static final Menu dao = new Menu().dao();
	
	/**
	 * 菜单类型
	 */
	public enum Type {

		/**
		 * 管理员
		 */
		admin,

		/**
		 * 商家
		 */
		business
	}
	
	/**
	 * 上级分类
	 */
	private Menu parent;
	
	/**
	 * 获取类型
	 * 
	 * @return 类型
	 */
	public Menu.Type getTypeName() {
		return getType() == null ? null : Type.values()[getType()];
	}
	
	
	/**
	 * 获取上级分类
	 * 
	 * @return 上级分类
	 */
	public Menu getParent() {
		if (parent == null) {
			parent = Menu.dao.findById(getParentId());
		}
		return parent;
	}
	
	
}
