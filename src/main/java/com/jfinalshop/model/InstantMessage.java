package com.jfinalshop.model;

import com.jfinalshop.model.base.BaseInstantMessage;

/**
 * Model - 即时通讯
 * 
 */
public class InstantMessage extends BaseInstantMessage<InstantMessage> {
	private static final long serialVersionUID = 7057235008834634224L;
	public static final InstantMessage dao = new InstantMessage().dao();
	
	/**
	 * 类型
	 */
	public enum Type {

		/**
		 * QQ
		 */
		qq,

		/**
		 * 阿里旺旺
		 */
		aliTalk
	}

	/**
	 * 店铺
	 */
	private Store store;
	
	/**
	 * 获取类型
	 * 
	 * @return 类型
	 */
	public InstantMessage.Type getTypeName() {
		return getType() == null ? null : Type.values()[getType()];
	}
	
	/**
	 * 获取店铺
	 * 
	 * @return 店铺
	 */
	public Store getStore() {
		if (store == null) {
			store = Store.dao.findById(getStoreId());
		}
		return store;
	}

	/**
	 * 设置店铺
	 * 
	 * @param store
	 *            店铺
	 */
	public void setStore(Store store) {
		this.store = store;
	}
}
