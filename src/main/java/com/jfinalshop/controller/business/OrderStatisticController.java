package com.jfinalshop.controller.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.hasor.core.Inject;

import org.apache.commons.lang.time.DateUtils;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.model.Statistic;
import com.jfinalshop.model.Store;
import com.jfinalshop.service.StatisticService;
import com.jfinalshop.service.StoreService;
import com.jfinalshop.util.EnumUtils;

/**
 * Controller - 订单统计
 * 
 */
@ControllerBind(controllerKey = "/business/order_statistic")
public class OrderStatisticController extends BaseController {

	/**
	 * 默认类型
	 */
	private static final Statistic.Type DEFAULT_TYPE = Statistic.Type.createOrderCount;

	/**
	 * 默认周期
	 */
	private static final Statistic.Period DEFAULT_PERIOD = Statistic.Period.day;

	@Inject
	private StatisticService statisticService;
	@Inject
	private StoreService storeService;

	/**
	 * 列表
	 */
	public void list() {
		List<Statistic.Type> types = new ArrayList<>();
		types.add(Statistic.Type.createOrderCount);
		types.add(Statistic.Type.completeOrderCount);
		types.add(Statistic.Type.createOrderAmount);
		types.add(Statistic.Type.completeOrderAmount);
		setAttr("types", types);
		setAttr("type", DEFAULT_TYPE);
		setAttr("periods", Statistic.Period.values());
		setAttr("period", DEFAULT_PERIOD);
		setAttr("beginDate", DateUtils.addMonths(new Date(), -1));
		setAttr("endDate", new Date());
		render("/business/order_statistic/list.ftl");
	}

	/**
	 * 数据
	 */
	public void data() {
		
		Statistic.Type type = EnumUtils.convert(Statistic.Type.class, getPara("type"));
		Statistic.Period period = EnumUtils.convert(Statistic.Period.class, getPara("period"));
		
		Date beginDate = getParaToDate("beginDate", null);
		Date endDate = getParaToDate("endDate", null);
		Store currentStore = storeService.find(1L);
		
		if (type == null) {
			type = DEFAULT_TYPE;
		}
		if (period == null) {
			period = DEFAULT_PERIOD;
		}
		if (beginDate == null) {
			switch (period) {
			case year:
				beginDate = DateUtils.addYears(new Date(), -10);
				break;
			case month:
				beginDate = DateUtils.addYears(new Date(), -1);
				break;
			case day:
				beginDate = DateUtils.addMonths(new Date(), -1);
				break;
			default:
				break;
			}
		}
		if (endDate == null) {
			endDate = new Date();
		}
		Calendar beginCalendar = DateUtils.toCalendar(beginDate);
		Calendar endCalendar = DateUtils.toCalendar(endDate);
		switch (period) {
		case year:
			beginCalendar.set(Calendar.MONTH, beginCalendar.getActualMinimum(Calendar.MONTH));
			beginCalendar.set(Calendar.DAY_OF_MONTH, beginCalendar.getActualMinimum(Calendar.DAY_OF_MONTH));
			endCalendar.set(Calendar.MONTH, endCalendar.getActualMaximum(Calendar.MONTH));
			endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			break;
		case month:
			beginCalendar.set(Calendar.DAY_OF_MONTH, beginCalendar.getActualMinimum(Calendar.DAY_OF_MONTH));
			endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			break;
		default:
			break;
		case day:
		}
		renderJson(statisticService.analyze(type, currentStore, period, beginCalendar.getTime(), endCalendar.getTime()));
	}

}