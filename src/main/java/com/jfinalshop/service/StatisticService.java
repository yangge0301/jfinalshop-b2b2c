package com.jfinalshop.service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinalshop.dao.MemberDao;
import com.jfinalshop.dao.OrderDao;
import com.jfinalshop.dao.StatisticDao;
import com.jfinalshop.dao.StoreDao;
import com.jfinalshop.model.Statistic;
import com.jfinalshop.model.Store;
import com.jfinalshop.util.Assert;

/**
 * Service - 统计
 * 
 */
@Singleton
public class StatisticService extends BaseService<Statistic> {

	/**
	 * 构造方法
	 */
	public StatisticService() {
		super(Statistic.class);
	}
	
	@Inject
	private StatisticDao statisticDao;
	@Inject
	private MemberDao memberDao;
	@Inject
	private OrderDao orderDao;
	@Inject
	private StoreDao storeDao;
	
	/**
	 * 判断统计是否存在
	 * 
	 * @param type
	 *            类型
	 * @param store
	 *            店铺
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @param day
	 *            日
	 * @return 统计是否存在
	 */
	public boolean exists(Statistic.Type type, Store store, int year, int month, int day) {
		return statisticDao.exists(type, store, year, month, day);
	}

	/**
	 * 收集
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @param day
	 *            日
	 */
	public void collect(int year, int month, int day) {
		for (Statistic.Type type : Statistic.Type.values()) {
			collect(type, null, year, month, day);
		}
		for (int i = 0;; i += 100) {
			List<Store> stores = storeDao.findList(null, Store.Status.success, null, null, i, 100);
			for (Store store : stores) {
				for (Statistic.Type type : Statistic.Type.values()) {
					if (!Statistic.Type.registerMemberCount.equals(type)) {
						collect(type, store, year, month, day);
					}
				}
			}
//			storeDao.flush();
//			storeDao.clear();
			if (stores.size() < 100) {
				break;
			}
		}
	}

	/**
	 * 收集
	 * 
	 * @param type
	 *            类型
	 * @param store
	 *            店铺
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @param day
	 *            日
	 */
	public void collect(Statistic.Type type, Store store, int year, int month, int day) {
		Assert.notNull(type);
		Assert.state(month >= 0);
		Assert.state(day >= 0);

		if (Statistic.Type.registerMemberCount.equals(type)) {
			if (statisticDao.exists(type, null, year, month, day)) {
				return;
			}
		} else if (statisticDao.exists(type, store, year, month, day)) {
			return;
		}

		Calendar beginCalendar = Calendar.getInstance();
		beginCalendar.set(year, month, day);
		beginCalendar.set(Calendar.HOUR_OF_DAY, beginCalendar.getActualMinimum(Calendar.HOUR_OF_DAY));
		beginCalendar.set(Calendar.MINUTE, beginCalendar.getActualMinimum(Calendar.MINUTE));
		beginCalendar.set(Calendar.SECOND, beginCalendar.getActualMinimum(Calendar.SECOND));
		Date beginDate = beginCalendar.getTime();

		Calendar endCalendar = Calendar.getInstance();
		endCalendar.set(year, month, day);
		endCalendar.set(Calendar.HOUR_OF_DAY, beginCalendar.getActualMaximum(Calendar.HOUR_OF_DAY));
		endCalendar.set(Calendar.MINUTE, beginCalendar.getActualMaximum(Calendar.MINUTE));
		endCalendar.set(Calendar.SECOND, beginCalendar.getActualMaximum(Calendar.SECOND));
		Date endDate = endCalendar.getTime();

		BigDecimal value = null;
		switch (type) {
		case registerMemberCount:
			value = new BigDecimal(memberDao.registerMemberCount(beginDate, endDate));
			break;
		case createOrderCount:
			value = new BigDecimal(orderDao.createOrderCount(store, beginDate, endDate));
			break;
		case completeOrderCount:
			value = new BigDecimal(orderDao.completeOrderCount(store, beginDate, endDate));
			break;
		case createOrderAmount:
			value = orderDao.createOrderAmount(store, beginDate, endDate);
			break;
		case completeOrderAmount:
			value = orderDao.completeOrderAmount(store, beginDate, endDate);
			break;
		default:
			break;
		}

		Statistic statistic = new Statistic();
		statistic.setType(type.ordinal());
		statistic.setYear(year);
		statistic.setMonth(month);
		statistic.setDay(day);
		statistic.setValue(value);
		statistic.setStore(store);
		statisticDao.save(statistic);
	}

	/**
	 * 分析
	 * 
	 * @param type
	 *            类型
	 * @param store
	 *            店铺
	 * @param period
	 *            周期
	 * @param beginDate
	 *            起始日期
	 * @param endDate
	 *            结束日期
	 * @return 统计
	 */
	public List<Statistic> analyze(Statistic.Type type, Store store, Statistic.Period period, Date beginDate, Date endDate) {
		return statisticDao.analyze(type, store, period, beginDate, endDate);
	}

}