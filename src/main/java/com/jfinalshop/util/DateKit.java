package com.jfinalshop.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.jfinal.kit.StrKit;
import com.xiaoleilu.hutool.date.DateUtil;

public class DateKit {

	/** 标准日期时间格式，精确到秒 */
	public final static String NORM_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	
	/**
	 * 格式化日期
	 *
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static final String formatUTC(String date) {
		String dateUTC = null;
		if (StrKit.notBlank(date)) {
			String dateStr = date.replace("Z", " UTC");
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
			
			try {
				Date d = format.parse(dateStr);
				dateUTC = DateUtil.format(d, NORM_DATETIME_PATTERN);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return dateUTC;
	}
}
