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
import com.jfinalshop.Pageable;
import com.jfinalshop.model.ProductCategory;
import com.jfinalshop.model.Specification;
import com.jfinalshop.service.ProductCategoryService;
import com.jfinalshop.service.SpecificationService;

/**
 * Controller - 规格
 * 
 */
@ControllerBind(controllerKey = "/admin/specification")
public class SpecificationController extends BaseController {
	
	@Inject
	private SpecificationService specificationService;
	@Inject
	private ProductCategoryService productCategoryService;

	/**
	 * 添加
	 */
	public void add() {
		Long sampleId = getParaToLong("sampleId");
		setAttr("sample", specificationService.find(sampleId));
		setAttr("productCategoryTree", productCategoryService.findTree());
		render("/admin/specification/add.ftl");
	}

	/**
	 * 保存
	 */
	@Before(Tx.class)
	public void save() {
		Specification specification = getModel(Specification.class);
		Long productCategoryId = getParaToLong("productCategoryId");
		List<String> options = Arrays.asList(getParaValues("options"));
		
		CollectionUtils.filter(specification.getOptionsConverter(), new AndPredicate(new UniquePredicate(), new Predicate() {
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
		specification.setProductCategoryId(productCategory.getId());
		specification.setOptions(JSONArray.toJSONString(options));
		specificationService.save(specification);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("productCategoryTree", productCategoryService.findTree());
		setAttr("specification", specificationService.find(id));
		render("/admin/specification/edit.ftl");
	}

	/**
	 * 更新
	 */
	@Before(Tx.class)
	public void update() {
		Specification specification = getModel(Specification.class);
		List<String> options = Arrays.asList(getParaValues("options"));
		
		CollectionUtils.filter(specification.getOptionsConverter(), new AndPredicate(new UniquePredicate(), new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				String option = (String) object;
				return StringUtils.isNotEmpty(option);
			}
		}));
		
		specification.setOptions(JSONArray.toJSONString(options));
		specificationService.update(specification, "productCategoryId");
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", specificationService.findPage(pageable));
		render("/admin/specification/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		specificationService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}

}