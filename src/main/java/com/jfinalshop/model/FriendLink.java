package com.jfinalshop.model;

import com.jfinalshop.model.base.BaseFriendLink;

/**
 * Model - 友情链接
 * 
 */
public class FriendLink extends BaseFriendLink<FriendLink> {
	private static final long serialVersionUID = 7198378002468899464L;
	public static final FriendLink dao = new FriendLink().dao();
	
	/**
	 * 类型
	 */
	public enum Type {

		/**
		 * 文本
		 */
		text,

		/**
		 * 图片
		 */
		image
	}
	
	/**
	 * 类型名称
	 */
	public Type getTypeName() {
		return getType() != null ? Type.values()[getType()] : null;
	}
	
}
