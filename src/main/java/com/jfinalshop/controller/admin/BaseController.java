package com.jfinalshop.controller.admin;

import java.math.BigDecimal;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinalshop.Message;
import com.jfinalshop.Setting;
import com.jfinalshop.interceptor.AuditLogInterceptor;
import com.jfinalshop.service.AdminService;
import com.jfinalshop.template.directive.FlashMessageDirective;
import com.jfinalshop.util.EnumUtils;
import com.jfinalshop.util.SystemUtils;

/**
 * Controller - 基类
 * 
 */
@Before(AuditLogInterceptor.class)
public class BaseController extends Controller {

	/**
	 * 错误视图
	 */
	protected static final String ERROR_VIEW = "/common/error/error_page.ftl";

	/**
	 * 错误消息
	 */
	protected static final Message ERROR_MESSAGE = Message.error("common.message.error");

	/**
	 * 成功消息
	 */
	protected static final Message SUCCESS_MESSAGE = Message.success("common.message.success");

	@Inject
	protected AdminService adminService;
	
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
	 * 添加瞬时消息
	 * 
	 * @param redirectAttributes
	 *            RedirectAttributes
	 * @param message
	 *            消息
	 */
	protected void addFlashMessage(Message message) {
		if (message != null && adminService.getCurrent() != null) {
			CacheKit.put(FlashMessageDirective.FLASH_MESSAGE_NAME, adminService.getCurrent().getId(), message);
		}
	}

}