package com.jfinalshop.controller.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinalshop.Order;
import com.jfinalshop.Pageable;
import com.jfinalshop.Setting;
import com.jfinalshop.interceptor.BusinessInterceptor;
import com.jfinalshop.util.Assert;
import com.jfinalshop.util.EnumUtils;
import com.jfinalshop.util.SystemUtils;

/**
 * Controller - 基类
 * 
 */
@Before(BusinessInterceptor.class)
public class BaseController extends Controller {

	/**
	 * 错误消息
	 */
	protected static final String ERROR_MESSAGE = "common.message.error";

	/**
	 * 成功消息
	 */
	protected static final String SUCCESS_MESSAGE = "common.message.success";

	/**
	 * 请求无法处理视图
	 */
	protected static final String UNPROCESSABLE_ENTITY_VIEW = "/common/error/error_page.ftl";

	/**
	 * "瞬时消息"属性名称
	 */
	protected static final String FLASH_MESSAGE_ATTRIBUTE_NAME = "flashMessage";
	
	/**
	 * 国际化
	 */
	protected Res res = I18n.use();
	
	/**
	 * 枚举类型转换
	 * @param clazz
	 * @param value
	 * @return
	 */
	public <T> T getParaEnum(Class<T> clazz, String value) {
		return (T) EnumUtils.convert(clazz, value);
	}
	
	
	/**
	 * 获取前端传来的数组对象并响应成Bean列表
	 */
	public <T> List<T> getBeans(Class<T> beanClass, String beanName) {
		List<String> indexes = getIndexes(beanName);
		List<T> list = new ArrayList<T>();
		for (String index : indexes) {
			T b = getBean(beanClass, beanName + "[" + index + "]");
			if (b != null) {
				list.add(b);
			}
		}
		return list;
	}
	
	
	/**
	 * 提取model对象数组的标号
	 */
	private List<String> getIndexes(String modelName) {
		// 提取标号
		List<String> list = new ArrayList<String>();
		String modelNameAndLeft = modelName + "[";
		Map<String, String[]> parasMap = getRequest().getParameterMap();
		for (Map.Entry<String, String[]> e : parasMap.entrySet()) {
			String paraKey = e.getKey();
			if (paraKey.startsWith(modelNameAndLeft)) {
				String no = paraKey.substring(paraKey.lastIndexOf("[") + 1, paraKey.lastIndexOf("]"));
				
				if (!list.contains(no)) {
					list.add(no);
				}
			}
		}
		return list;
	}
	
	/**
	 * 提取model对象数组最大的标号
	 */
	public Integer getIndexNum(String modelName) {
		Integer indexNum = -1;
		// 提取标号
		String modelNameAndLeft = modelName + "[";
		Map<String, String[]> parasMap = getRequest().getParameterMap();
		for (Map.Entry<String, String[]> e : parasMap.entrySet()) {
			String paraKey = e.getKey();
			if (paraKey.startsWith(modelNameAndLeft)) {
				String str = paraKey.substring(paraKey.lastIndexOf("[") + 1, paraKey.lastIndexOf("]"));
				int no = Integer.parseInt(str);
				indexNum = indexNum < no ? no : indexNum;
			}
		}
		return indexNum;
	}


	/**
	 * 货币格式化
	 * 
	 * @param amount
	 *            金额
	 * @param showSign
	 *            显示标志
	 * @param showUnit
	 *            显示单位
	 * @return 货币格式化
	 */
	protected String currency(BigDecimal amount, boolean showSign, boolean showUnit) {
		Setting setting = SystemUtils.getSetting();
		String price = setting.setScale(amount).toString();
		if (showSign) {
			price = setting.getCurrencySign() + price;
		}
		if (showUnit) {
			price += setting.getCurrencyUnit();
		}
		return price;
	}

	/**
	 * 获取国际化消息
	 * 
	 * @param code
	 *            代码
	 * @param args
	 *            参数
	 * @return 国际化消息
	 */
	protected String message(String code, Object... args) {
		return res.format(code, args);
	}

	/**
	 * 添加瞬时消息
	 *
	 * @param message
	 *            消息
	 * @param args
	 *            参数
	 */
	protected void addFlashMessage(String message, Object... args) {
		Assert.hasText(message);
		setSessionAttr(FLASH_MESSAGE_ATTRIBUTE_NAME, res.format(message, args));
	}

}