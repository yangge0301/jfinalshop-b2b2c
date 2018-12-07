package com.jfinalshop.model;

import com.jfinalshop.model.base.BaseStoreFavorite;

/**
 * Model - 店铺收藏
 * 
 */
public class StoreFavorite extends BaseStoreFavorite<StoreFavorite> {
	private static final long serialVersionUID = -2942089279511539856L;
	public static final StoreFavorite dao = new StoreFavorite().dao();
	
	/**
	 * 最大店铺收藏数
	 */
	public static final Integer MAX_STORE_FAVORITE_SIZE = 10;
	
	/**
	 * 会员
	 */
	private Member member;
	
	/**
	 * 店铺
	 */
	private Store store;
	
	/**
	 * 获取会员
	 * 
	 * @return 会员
	 */
	public Member getMember() {
		if (member == null) {
			member = Member.dao.findById(getMemberId());
		}
		return member;
	}

	/**
	 * 设置会员
	 * 
	 * @param member
	 *            会员
	 */
	public void setMember(Member member) {
		this.member = member;
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
