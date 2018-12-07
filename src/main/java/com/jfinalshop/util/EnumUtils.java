package com.jfinalshop.util;

import com.jfinal.kit.StrKit;
import com.jfinalshop.EnumConverter;

/**
 * 枚举类型转换
 * 
 */
public class EnumUtils {

	/**
	 * 类型转换
	 * @param clazz
	 * @param value
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> T convert(final Class<T> clazz, String value) {
		if (StrKit.isBlank(value) || !clazz.isEnum()) {
			return null;
		}
		EnumConverter enumConverter = new EnumConverter(clazz.getClass());
		return (T) enumConverter.convert((Class<Enum>)clazz, value);
	}
	
}
