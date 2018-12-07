package com.jfinalshop.controller.admin;

import java.util.Arrays;
import java.util.List;

import net.hasor.core.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.AndPredicate;
import org.apache.commons.collections.functors.UniquePredicate;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Message;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Attribute;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.ProductCategory;
import com.jfinalshop.service.AttributeService;
import com.jfinalshop.service.ProductCategoryService;

/**
 * Controller - 属性
 * 
 */
@ControllerBind(controllerKey = "/admin/attribute")
public class AttributeController extends BaseController {

	@Inject
	private AttributeService attributeService;
	@Inject
	private ProductCategoryService productCategoryService;

	/**
	 * 添加
	 */
	public void add() {
		Long sampleId = getParaToLong("sampleId");
		setAttr("sample", attributeService.find(sampleId));
		setAttr("productCategoryTree", productCategoryService.findTree());
		render("/admin/attribute/add.ftl");
	}

	/**
	 * 保存
	 */
	@Before(Tx.class)
	public void save() {
		Attribute attribute = getModel(Attribute.class);
		Long productCategoryId = getParaToLong("productCategoryId");
		List<String> options = Arrays.asList(getParaValues("options"));
		
		CollectionUtils.filter(attribute.getOptionsConverter(), new AndPredicate(new UniquePredicate(), new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				String option = (String) object;
				return StringUtils.isNotEmpty(option);
			}
		}));
		
		ProductCategory productCategory = productCategoryService.find(productCategoryId);
		if (productCategory == null) {
			setAttr("errorMessage", "产品分类不能为空!");
			render(ERROR_VIEW);
			return;
		}
		
		attribute.setProductCategoryId(productCategory.getId());
		attribute.setOptions(JSONArray.toJSONString(options));
		Integer propertyIndex = attributeService.findUnusedPropertyIndex(attribute.getProductCategory());
		if (propertyIndex == null) {
			addFlashMessage(Message.error("admin.attribute.addCountNotAllowed", Product.ATTRIBUTE_VALUE_PROPERTY_COUNT));
		} else {
			attribute.setPropertyIndex(null);
			attributeService.save(attribute);
			addFlashMessage(SUCCESS_MESSAGE);
		}
		redirect("list");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("productCategoryTree", productCategoryService.findTree());
		setAttr("attribute", attributeService.find(id));
		render("/admin/attribute/edit.ftl");
	}

	/**
	 * 更新
	 */
	@Before(Tx.class)
	public void update() {
		Attribute attribute = getModel(Attribute.class);
		List<String> options = Arrays.asList(getParaValues("options"));
		
		CollectionUtils.filter(attribute.getOptionsConverter(), new AndPredicate(new UniquePredicate(), new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				String option = (String) object;
				return StringUtils.isNotEmpty(option);
			}
		}));
		
		attribute.setOptions(JSONArray.toJSONString(options));
		attributeService.update(attribute, "propertyIndex", "productCategoryId");
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", attributeService.findPage(pageable));
		render("/admin/attribute/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		attributeService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}

}