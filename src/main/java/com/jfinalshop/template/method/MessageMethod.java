package com.jfinalshop.template.method;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinalshop.util.FreeMarkerUtils;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;

/**
 * 模板方法 - 多语言
 * 
 */
public class MessageMethod implements TemplateMethodModelEx {

	/**
	 * 执行
	 * 
	 * @param arguments
	 *            参数
	 * @return 结果
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Object exec(List arguments) throws TemplateModelException {
		String code = FreeMarkerUtils.getArgument(0, String.class, arguments);
		if (StringUtils.isNotEmpty(code)) {
			String message;
			Res res = I18n.use();
			if (arguments.size() > 1) {
				Object[] args = new Object[arguments.size() - 1];
				for (int i = 1; i < arguments.size(); i++) {
					Object argument = arguments.get(i);
					if (argument != null && argument instanceof TemplateModel) {
						args[i - 1] = DeepUnwrap.unwrap((TemplateModel) argument);
					} else {
						args[i - 1] = argument;
					}
				}
				message = res.format(code, args);
			} else {
				message = res.get(code);
			}
			return new SimpleScalar(message);
		}
		return null;
	}

}