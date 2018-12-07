package com.jfinalshop.controller.member;

import java.math.BigDecimal;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinalshop.Setting;
import com.jfinalshop.interceptor.MemberInterceptor;
import com.jfinalshop.util.Assert;
import com.jfinalshop.util.EnumUtils;
import com.jfinalshop.util.SystemUtils;

/**
 * Controller - 基类
 * 
 */
@Before(MemberInterceptor.class)
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
	protected static final String UNPROCESSABLE_ENTITY_VIEW = "/common/error/unprocessable_entity.ftl";

	/**
	 * "瞬时消息"属性名称
	 */
	protected static final String FLASH_MESSAGE_ATTRIBUTE_NAME = "flashMessage";

	/**
	 * "验证结果"属性名称
	 */
	//private static final String CONSTRAINT_VIOLATIONS_ATTRIBUTE_NAME = "constraintViolations";
	
	private Res res = I18n.use();

//	@Inject
//	private Validator validator;

	/**
	 * 枚举类型转换
	 * @param clazz
	 * @param value
	 * @return
	 */
	protected <T> T getParaEnum(Class<T> clazz, String value) {
		return (T) EnumUtils.convert(clazz, value);
	}
	
	/**
	 * 数据验证
	 * 
	 * @param target
	 *            验证对象
	 * @param groups
	 *            验证组
	 * @return 验证结果
	 */
//	protected boolean isValid(Object target, Class<?>... groups) {
//		Assert.notNull(target);
//
//		Set<ConstraintViolation<Object>> constraintViolations = validator.validate(target, groups);
//		if (constraintViolations.isEmpty()) {
//			return true;
//		}
//		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
//		requestAttributes.setAttribute(CONSTRAINT_VIOLATIONS_ATTRIBUTE_NAME, constraintViolations, RequestAttributes.SCOPE_REQUEST);
//		return false;
//	}

	/**
	 * 数据验证
	 * 
	 * @param targets
	 *            验证对象
	 * @param groups
	 *            验证组
	 * @return 验证结果
	 */
//	protected boolean isValid(Collection<Object> targets, Class<?>... groups) {
//		Assert.notEmpty(targets);
//
//		for (Object target : targets) {
//			if (!isValid(target, groups)) {
//				return false;
//			}
//		}
//		return true;
//	}

	/**
	 * 数据验证
	 * 
	 * @param type
	 *            类型
	 * @param property
	 *            属性
	 * @param value
	 *            值
	 * @param groups
	 *            验证组
	 * @return 验证结果
	 */
//	protected boolean isValid(Class<?> type, String property, Object value, Class<?>... groups) {
//		Assert.notNull(type);
//		Assert.hasText(property);
//
//		Set<?> constraintViolations = validator.validateValue(type, property, value, groups);
//		if (constraintViolations.isEmpty()) {
//			return true;
//		}
//		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
//		requestAttributes.setAttribute(CONSTRAINT_VIOLATIONS_ATTRIBUTE_NAME, constraintViolations, RequestAttributes.SCOPE_REQUEST);
//		return false;
//	}

	/**
	 * 数据验证
	 * 
	 * @param type
	 *            类型
	 * @param properties
	 *            属性
	 * @param groups
	 *            验证组
	 * @return 验证结果
	 */
//	protected boolean isValid(Class<?> type, Map<String, Object> properties, Class<?>... groups) {
//		Assert.notNull(type);
//		Assert.notEmpty(properties);
//
//		for (Map.Entry<String, Object> entry : properties.entrySet()) {
//			if (!isValid(type, entry.getKey(), entry.getValue(), groups)) {
//				return false;
//			}
//		}
//		return true;
//	}

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
	 * @param redirectAttributes
	 *            RedirectAttributes
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