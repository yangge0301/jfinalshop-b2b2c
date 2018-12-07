package com.jfinalshop.controller.admin;

import java.util.Arrays;
import java.util.List;

import net.hasor.core.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.AndPredicate;
import org.apache.commons.collections.functors.UniquePredicate;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Parameter;
import com.jfinalshop.model.ProductCategory;
import com.jfinalshop.service.ParameterService;
import com.jfinalshop.service.ProductCategoryService;

/**
 * Controller - 参数
 * 
 */
@ControllerBind(controllerKey = "/admin/parameter")
public class ParameterController extends BaseController {

	@Inject
	private ParameterService parameterService;
	@Inject
	private ProductCategoryService productCategoryService;

	/**
	 * 添加
	 */
	public void add() {
		Long sampleId = getParaToLong("sampleId");
		setAttr("sample", parameterService.find(sampleId));
		setAttr("productCategoryTree", productCategoryService.findTree());
		render("/admin/parameter/add.ftl");
	}

	/**
	 * 保存
	 */
	@Before(Tx.class)
	public void save() {
		Parameter parameter = getModel(Parameter.class);
		Long productCategoryId = getParaToLong("productCategoryId");
		List<String> names = Arrays.asList(getParaValues("names"));
		String group = getPara("group");
		
		CollectionUtils.filter(parameter.getNamesConverter(), new AndPredicate(new UniquePredicate(), new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				String name = (String) object;
				return StringUtils.isNotEmpty(name);
			}
		}));
		
		ProductCategory productCategory = productCategoryService.find(productCategoryId);
		if (productCategory == null) {
			setAttr("errorMessage", "产品分类不能为空!");
			render(ERROR_VIEW);
			return;
		}
		
		parameter.setGroup(group);
		parameter.setNames(JSON.toJSONString(names));
		parameter.setProductCategoryId(productCategory.getId());
		parameterService.save(parameter);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("parameter", parameterService.find(id));
		render("/admin/parameter/edit.ftl");
	}

	/**
	 * 更新
	 */
	@Before(Tx.class)
	public void update() {
		List<String> names = Arrays.asList(getParaValues("names"));
		String group = getPara("group");
		Parameter parameter = getModel(Parameter.class);
		
		CollectionUtils.filter(parameter.getNamesConverter(), new AndPredicate(new UniquePredicate(), new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				String name = (String) object;
				return StringUtils.isNotEmpty(name);
			}
		}));
		
		parameter.setGroup(group);
		parameter.setNames(JSON.toJSONString(names));
		parameterService.update(parameter, "productCategoryId");
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", parameterService.findPage(pageable));
		render("/admin/parameter/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		parameterService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}

}