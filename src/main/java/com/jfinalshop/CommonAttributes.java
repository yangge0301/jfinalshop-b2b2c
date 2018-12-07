package com.jfinalshop;

/**
 * 公共参数
 * 
 */
public final class CommonAttributes {

	/**
	 * 日期格式配比
	 */
	public static final String[] DATE_PATTERNS = new String[] { "yyyy", "yyyy-MM", "yyyyMM", "yyyy/MM", "yyyy-MM-dd", "yyyyMMdd", "yyyy/MM/dd", "yyyy-MM-dd HH:mm:ss", "yyyyMMddHHmmss", "yyyy/MM/dd HH:mm:ss" };

	/**
	 * jfinalshop.xml文件路径
	 */
	public static final String JFINALSHOP_XML_PATH = "/jfinalshop.xml";

	/**
	 * jfinalshop.properties文件路径
	 */
	public static final String JFINALSHOP_PROPERTIES_PATH = "jfinalshop.properties";

	/**
	 * 不可实例化
	 */
	private CommonAttributes() {
	}

}