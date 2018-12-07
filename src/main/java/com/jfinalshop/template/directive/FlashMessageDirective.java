package com.jfinalshop.template.directive;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinalshop.Message;
import com.jfinalshop.model.Admin;
import com.jfinalshop.service.AdminService;
import com.jfinalshop.util.HasorUtils;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 模板指令 - 瞬时消息
 * 
 */
public class FlashMessageDirective extends BaseDirective {

	/**
	 * "瞬时消息"属性名称
	 */
	public static final String FLASH_MESSAGE_NAME = "flash_message";

	/**
	 * 变量名称
	 */
	private static final String VARIABLE_NAME = "flashMessage";

	private AdminService adminService = HasorUtils.getBean(AdminService.class);
	
	/**
	 * 执行
	 * 
	 * @param env
	 *            环境变量
	 * @param params
	 *            参数
	 * @param loopVars
	 *            循环变量
	 * @param body
	 *            模板内容
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
		Admin admin = adminService.getCurrent();
		if (admin != null) {
			// 从缓存取出
			Message message = CacheKit.get(FLASH_MESSAGE_NAME, admin.getId());
			if (body != null) {
				setLocalVariable(VARIABLE_NAME, message, env, body);
			} else {
				if (message != null) {
					CacheKit.remove(FLASH_MESSAGE_NAME, admin.getId());
					Writer out = env.getOut();
					out.write("$.message(\"" + message.getType() + "\", \"" + message.getContent() + "\");");
				}
			}
		}
	}

}