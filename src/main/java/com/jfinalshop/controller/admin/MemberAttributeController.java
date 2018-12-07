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
import com.jfinalshop.Message;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.MemberAttribute;
import com.jfinalshop.service.MemberAttributeService;

/**
 * Controller - 会员注册项
 * 
 */
@ControllerBind(controllerKey = "/admin/member_attribute")
public class MemberAttributeController extends BaseController {

	@Inject
	private MemberAttributeService memberAttributeService;

	/**
	 * 检查配比语法是否正确
	 */
	@ActionKey("/admin/member_attribute/check_pattern")
	public void checkPattern() {
		String pattern = getPara("memberAttribute.pattern");
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
		if (memberAttributeService.findUnusedPropertyIndex() == null) {
			addFlashMessage(Message.warn("admin.memberAttribute.addCountNotAllowed", Member.ATTRIBUTE_VALUE_PROPERTY_COUNT));
			redirect("list");
			return;
		}
		render("/admin/member_attribute/add.ftl");
	}

	/**
	 * 保存
	 */
	@Before(Tx.class)
	public void save() {
		MemberAttribute memberAttribute = getModel(MemberAttribute.class);
		Boolean isEnabled = getParaToBoolean("isEnabled", false);
		Boolean isRequired = getParaToBoolean("isRequired", false);
		MemberAttribute.Type type = getParaEnum(MemberAttribute.Type.class, getPara("type"));
		memberAttribute.setType(type.ordinal());
		
		if (MemberAttribute.Type.select.equals(memberAttribute.getTypeName()) || MemberAttribute.Type.checkbox.equals(memberAttribute.getTypeName())) {
			List<String> options = Arrays.asList(getParaValues("options"));
			memberAttribute.setOptionsConverter(options);
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
			memberAttribute.setOptions(JSONArray.toJSONString(options));
			memberAttribute.setPattern(null);
		} else if (MemberAttribute.Type.text.equals(memberAttribute.getTypeName())) {
			memberAttribute.setOptions(null);
		} else {
			setAttr("errorMessage", "未知可选项!");
			render(ERROR_VIEW);
			return;
		}
		if (StringUtils.isNotEmpty(memberAttribute.getPattern())) {
			try {
				Pattern.compile(memberAttribute.getPattern());
			} catch (PatternSyntaxException e) {
				setAttr("errorMessage", "配比异常!");
				render(ERROR_VIEW);
				return;
			}
		}
		Integer propertyIndex = memberAttributeService.findUnusedPropertyIndex();
		if (propertyIndex == null) {
			setAttr("errorMessage", "未使用的对象属性序号为空!");
			render(ERROR_VIEW);
			return;
		}
		memberAttribute.setIsEnabled(isEnabled);
		memberAttribute.setIsRequired(isRequired);
		memberAttribute.setPropertyIndex(null);
		memberAttributeService.save(memberAttribute);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("memberAttribute", memberAttributeService.find(id));
		render("/admin/member_attribute/edit.ftl");
	}

	/**
	 * 更新
	 */
	@Before(Tx.class)
	public void update() {
		MemberAttribute memberAttribute = getModel(MemberAttribute.class);
		Boolean isEnabled = getParaToBoolean("isEnabled", false);
		Boolean isRequired = getParaToBoolean("isRequired", false);
		
		MemberAttribute pMemberAttribute = memberAttributeService.find(memberAttribute.getId());
		if (pMemberAttribute == null) {
			setAttr("errorMessage", "会员注册项为空!");
			render(ERROR_VIEW);
			return;
		}
		if (MemberAttribute.Type.select.equals(pMemberAttribute.getType()) || MemberAttribute.Type.checkbox.equals(pMemberAttribute.getType())) {
			List<String> options = Arrays.asList(getParaValues("options"));
			memberAttribute.setOptionsConverter(options);
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
			memberAttribute.setOptions(JSONArray.toJSONString(options));
			memberAttribute.setPattern(null);
		} else {
			memberAttribute.setOptions(null);
		}
		if (StringUtils.isNotEmpty(memberAttribute.getPattern())) {
			try {
				Pattern.compile(memberAttribute.getPattern());
			} catch (PatternSyntaxException e) {
				setAttr("errorMessage", "配比异常!");
				render(ERROR_VIEW);
				return;
			}
		}
		memberAttribute.setIsEnabled(isEnabled);
		memberAttribute.setIsRequired(isRequired);
		memberAttributeService.update(memberAttribute, "type", "propertyIndex", "createdDate");
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", memberAttributeService.findPage(pageable));
		render("/admin/member_attribute/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		memberAttributeService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}

}