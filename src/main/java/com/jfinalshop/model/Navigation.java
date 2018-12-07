package com.jfinalshop.model;

import com.jfinalshop.model.base.BaseNavigation;

/**
 * Model - 导航
 * 
 */
public class Navigation extends BaseNavigation<Navigation> {
	private static final long serialVersionUID = 1604950263772619092L;
	public static final Navigation dao = new Navigation().dao();
	
	/**
	 * 位置
	 */
	public enum Position {

		/**
		 * 顶部
		 */
		top,

		/**
		 * 中间
		 */
		middle,

		/**
		 * 底部
		 */
		bottom
	}
	
	/**
	 * 类型名称
	 */
	public Position getPositionName() {
		return getPosition() != null ? Position.values()[getPosition()] : null;
	}
}
