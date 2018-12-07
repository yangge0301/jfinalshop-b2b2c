package com.jfinalshop.util;

import net.hasor.core.AppContext;
import net.hasor.core.Hasor;

public class HasorUtils {

	/** Hasor容器注入 */
	private static final AppContext appContext = Hasor.createAppContext();

	/**
	 * 不可实例化
	 */
	private HasorUtils() {
	}

	/**
	 * 获取实例
	 * 
	 * @param type
	 *            Bean类型
	 * @return 实例
	 */
	public static <T> T getBean(Class<T> type) {
		Assert.notNull(type);

		return appContext.getInstance(type);
	}

	
}
