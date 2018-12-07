package com.jfinalshop.template.directive;

import java.io.IOException;
import java.util.Map;

import com.jfinalshop.plugin.PromotionPlugin;
import com.jfinalshop.service.PluginService;
import com.jfinalshop.util.FreeMarkerUtils;
import com.jfinalshop.util.HasorUtils;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 模板指令 - 促销插件
 * 
 */
public class PromotionPluginDirective extends BaseDirective {

	/**
	 * "促销插件ID"参数名称
	 */
	private static final String PROMOTION_PLUGIN_ID_PARAMETER_NAME = "promotionPluginId";

	/**
	 * 变量名称
	 */
	private static final String VARIABLE_NAME = "promotionPlugin";

	private PluginService pluginService = HasorUtils.getBean(PluginService.class);

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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
		String promotionPluginId = FreeMarkerUtils.getParameter(PROMOTION_PLUGIN_ID_PARAMETER_NAME, String.class, params);
		PromotionPlugin promotionPlugin = pluginService.getPromotionPlugin(promotionPluginId);
		setLocalVariable(VARIABLE_NAME, promotionPlugin, env, body);
	}

}