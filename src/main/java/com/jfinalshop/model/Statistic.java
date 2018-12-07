package com.jfinalshop.model;

import java.util.Calendar;
import java.util.Date;

import com.jfinalshop.model.base.BaseStatistic;

/**
 * Model - 统计
 * 
 */
public class Statistic extends BaseStatistic<Statistic> {
	private static final long serialVersionUID = -6997850608276216831L;
	public static final Statistic dao = new Statistic().dao();
	
	/**
	 * 类型
	 */
	public enum Type {

		/**
		 * 会员注册数
		 */
		registerMemberCount,

		/**
		 * 订单创建数
		 */
		createOrderCount,

		/**
		 * 订单完成数
		 */
		completeOrderCount,

		/**
		 * 订单创建金额
		 */
		createOrderAmount,

		/**
		 * 订单完成金额
		 */
		completeOrderAmount
	}

	/**
	 * 周期
	 */
	public enum Period {

		/**
		 * 年
		 */
		year,

		/**
		 * 月
		 */
		month,

		/**
		 * 日
		 */
		day
	}
	
	
	/**
	 * 店铺
	 */
	private Store store;
	
	
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

	/**
	 * 获取日期
	 * 
	 * @return 日期
	 */
	public Date getDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, getYear() != null ? getYear() : 0);
		calendar.set(Calendar.MONTH, getMonth() != null ? getMonth() : 0);
		calendar.set(Calendar.DAY_OF_MONTH, getDay() != null ? getDay() : 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}
}
