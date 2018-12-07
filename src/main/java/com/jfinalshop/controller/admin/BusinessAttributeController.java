package com.jfinalshop.controller.admin;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.hasor.core.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.AndPredicate;
import org.apache.commons.collections.functors.UniquePredicate;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.BusinessAttribute;
import com.jfinalshop.service.BusinessAttributeService;
import com.jfinalshop.util.EnumUtils;

/**
 * Controller - 商家注册项
 * 
 */
@ControllerBind(controllerKey = "/admin/business_attribute")
public class BusinessAttributeController extends BaseController {

	@Inject
	private BusinessAttributeService businessAttributeService;

	/**
	 * 检查配比语法是否正确
	 */
	@ActionKey("/admin/business_attribute/check_pattern")
	public void checkPattern() {
		String pattern = getPara("pattern");
		if (StringUtils.isEmpty(pattern)) {
			renderJson(false);
			return;
		}
		try {
			Pattern.compile(pattern);
		} catch (PatternSyntaxException e) {
			renderJson(false);
			return;
		}
		renderJson(true);
	}

	/**
	 * 添加
	 */
	public void add() {
		render("/admin/business_attribute/add.ftl");
	}

	/**
	 * 保存
	 */
	@Before(Tx.class)
	public void save() {
		BusinessAttribute businessAttribute = getModel(BusinessAttribute.class);
		Boolean isEnabled = getParaToBoolean("isEnabled", false);
		Boolean isRequired = getParaToBoolean("isRequired", false);
		BusinessAttribute.Type type = EnumUtils.convert(BusinessAttribute.Type.class, getPara("type"));
		businessAttribute.setType(type.ordinal());
		
		if (BusinessAttribute.Type.select.equals(businessAttribute.getTypeName()) || BusinessAttribute.Type.checkbox.equals(businessAttribute.getTypeName())) {
			List<String> options = Arrays.asList(getParaValues("options"));
			CollectionUtils.filter(options, new AndPredicate(new UniquePredicate(), new Predicate() {
				@Override
				public boolean evaluate(Object object) {
					String option = (String) object;
					return StringUtils.isNotEmpty(option);
				}
			}));
			if (CollectionUtils.isEmpty(options)) {
				setAttr("errorMessage", "可选项不能为空!");
				render(ERROR_VIEW);
				return;
			}
			businessAttribute.setPattern(null);
			businessAttribute.setOptions(JSONArray.toJSONString(options));
		} else if (BusinessAttribute.Type.text.equals(businessAttribute.getTypeName()) || BusinessAttribute.Type.image.equals(businessAttribute.getTypeName()) || BusinessAttribute.Type.date.equals(businessAttribute.getTypeName())) {
			businessAttribute.setOptions(null);
		} else {
			setAttr("errorMessage", "可选项类型错误!");
			render(ERROR_VIEW);
			return;
		}
		if (StringUtils.isNotEmpty(businessAttribute.getPattern())) {
			try {
				Pattern.compile(businessAttribute.getPattern());
			} catch (PatternSyntaxException e) {
				setAttr("errorMessage", "配比错误!");
				render(ERROR_VIEW);
				return;
			}
		}

		Integer propertyIndex = businessAttributeService.findUnusedPropertyIndex();
		if (propertyIndex == null) {
			setAttr("errorMessage", "未使用的对象属性序号为空!");
			render(ERROR_VIEW);
			return;
		}
		businessAttribute.setIsEnabled(isEnabled);
		businessAttribute.setIsRequired(isRequired);
		businessAttribute.setPropertyIndex(null);
		businessAttributeService.save(businessAttribute);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("businessAttribute", businessAttributeService.find(id));
		render("/admin/business_attribute/edit.ftl");
	}

	/**
	 * 更新
	 */
	@Before(Tx.class)
	public void update() {
		BusinessAttribute businessAttribute = getModel(BusinessAttribute.class);
		Boolean isEnabled = getParaToBoolean("isEnabled", false);
		Boolean isRequired = getParaToBoolean("isRequired", false);
		
		BusinessAttribute pBusinessAttribute = businessAttributeService.find(businessAttribute.getId());
		if (pBusinessAttribute == null) {
			setAttr("errorMessage", "商家注册项为空!");
			render(ERROR_VIEW);
			return;
		}
		
		
		if (BusinessAttribute.Type.select.equals(pBusinessAttribute.getTypeName()) || BusinessAttribute.Type.checkbox.equals(pBusinessAttribute.getTypeName())) {
			List<String> options = Arrays.asList(getParaValues("options"));
			CollectionUtils.filter(options, new AndPredicate(new UniquePredicate(), new Predicate() {
				@Override
				public boolean evaluate(Object object) {
					String option = (String) object;
					return StringUtils.isNotEmpty(option);
				}
			}));
			if (CollectionUtils.isEmpty(options)) {
				setAttr("errorMessage", "商家注册项可选项为空!");
				render(ERROR_VIEW);
				return;
			}
			businessAttribute.setPattern(null);
			businessAttribute.setOptions(JSONArray.toJSONString(options));
		} else {
			businessAttribute.setOptions(null);
		}
		if (StringUtils.isNotEmpty(businessAttribute.getPattern())) {
			try {
				Pattern.compile(businessAttribute.getPattern());
			} catch (PatternSyntaxException e) {
				setAttr("errorMessage", "配比错误!");
				render(ERROR_VIEW);
				return;
			}
		}
		
		businessAttribute.setIsEnabled(isEnabled);
		businessAttribute.setIsRequired(isRequired);
		businessAttributeService.update(businessAttribute);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", businessAttributeService.findPage(pageable));
		render("/admin/business_attribute/list.ftl");
	}

	/**
	 * 删除
	 */
	@Before(Tx.class)
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		businessAttributeService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}

}