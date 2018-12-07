package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinalshop.model.Statistic;
import com.jfinalshop.model.Store;
import com.jfinalshop.util.Assert;

/**
 * Dao - 统计
 * 
 */
public class StatisticDao extends BaseDao<Statistic> {
	
	/**
	 * 构造方法
	 */
	public StatisticDao() {
		super(Statistic.class);
	}
	
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
		Assert.notNull(type);

		if (store != null) {
			String sql = "SELECT COUNT(*) FROM statistic WHERE `type` = ? AND `year` = ? AND `month` = ? AND `day` = ? AND store_id = ?";
			return Db.queryInt(sql, type, year, month, day, store.getId()) > 0;
		} else {
			String sql = "SELECT COUNT(*) FROM statistic WHERE `type` = ? AND `year` = ? AND `month` = ? AND `day` = ? AND store_id IS NULL";
			return Db.queryInt(sql, type, year, month, day) > 0;
		}
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
		Assert.notNull(type);
		Assert.notNull(period);

		String sql = "";
		String groupBy = "";
		List<Object> params = new ArrayList<Object>();
		
		switch (period) {
		case year:
			sql += "SELECT `type`,`year`,sum(`value`) as `value` FROM `statistic` t ";
			groupBy = " GROUP BY `type`,`year` ";
			break;
		case month:
			sql += "SELECT `type`, `year`, `month`, sum(`value`) as `value` FROM `statistic` t ";
			groupBy = " GROUP BY `type`, `year`, `month` ";
			break;
		case day:
			sql += "SELECT `type`, `year`, `month`, `day`, `value` FROM `statistic` t ";
			break;
		default:
			break;
		}
		
		sql += " WHERE 1 = 1";
		if (type != null) {
			sql += " AND `type` = ?";
			params.add(type.ordinal());
		}
		if (store != null) {
			sql += " AND `store_id` = ?";
			params.add(store.getId());
		}
		if (beginDate != null) {
			Calendar calendar = DateUtils.toCalendar(beginDate);
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			
			sql += " AND `year` > ? OR (`year` = ? AND `month` > ?) OR (`year` = ? AND `month` = ?) OR `day` >= ? ";
			params.add(year);
			params.add(year);
			params.add(month);
			params.add(year);
			params.add(month);
			params.add(day);
		}
		if (endDate != null) {
			Calendar calendar = DateUtils.toCalendar(endDate);
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			
			sql += " AND `year` < ? OR (`year` = ? AND `month` < ?) OR (`year` = ? AND `month` = ?) OR `day` <= ? ";
			params.add(year);
			params.add(year);
			params.add(month);
			params.add(year);
			params.add(month);
			params.add(day);
		}
		sql += groupBy;
		return modelManager.find(sql, params.toArray());
	}
	
}