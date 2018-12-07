package com.jfinalshop.service;

import java.util.Collections;
import java.util.List;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.dao.AttributeDao;
import com.jfinalshop.dao.ProductCategoryDao;
import com.jfinalshop.dao.ProductDao;
import com.jfinalshop.model.Attribute;
import com.jfinalshop.model.ProductCategory;
import com.jfinalshop.util.Assert;

/**
 * Service - 属性
 * 
 */
@Singleton
public class AttributeService extends BaseService<Attribute> {

	/**
	 * 构造方法
	 */
	public AttributeService() {
		super(Attribute.class);
	}
	
	@Inject
	private AttributeDao attributeDao;
	@Inject
	private ProductCategoryDao productCategoryDao;
	@Inject
	private ProductDao productDao;
	
	/**
	 * 查找未使用的属性序号
	 * 
	 * @param productCategory
	 *            商品分类
	 * @return 未使用的属性序号，若不存在则返回null
	 */
	public Integer findUnusedPropertyIndex(ProductCategory productCategory) {
		return attributeDao.findUnusedPropertyIndex(productCategory);
	}

	/**
	 * 查找属性
	 * 
	 * @param productCategory
	 *            商品分类
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 属性
	 */
	public List<Attribute> findList(ProductCategory productCategory, Integer count, List<Filter> filters, List<Order> orders) {
		return attributeDao.findList(productCategory, count, filters, orders);
	}

	/**
	 * 查找属性
	 * 
	 * @param productCategoryId
	 *            商品分类ID
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @param useCache
	 *            是否使用缓存
	 * @return 属性
	 */
	public List<Attribute> findList(Long productCategoryId, Integer count, List<Filter> filters, List<Order> orders, boolean useCache) {
		ProductCategory productCategory = productCategoryDao.find(productCategoryId);
		if (productCategoryId != null && productCategory == null) {
			return Collections.emptyList();
		}
		return attributeDao.findList(productCategory, count, filters, orders);
	}

	/**
	 * 转换为属性值
	 * 
	 * @param attribute
	 *            属性
	 * @param value
	 *            值
	 * @return 属性值
	 */
	public String toAttributeValue(Attribute attribute, String value) {
		Assert.notNull(attribute);

		if (StringUtils.isEmpty(value) || CollectionUtils.isEmpty(attribute.getOptionsConverter()) || !attribute.getOptions().contains(value)) {
			return null;
		}
		return value;
	}

	@Override
	public Attribute save(Attribute attribute) {
		Assert.notNull(attribute);

		Integer unusedPropertyIndex = attributeDao.findUnusedPropertyIndex(attribute.getProductCategory());
		Assert.notNull(unusedPropertyIndex);

		attribute.setPropertyIndex(unusedPropertyIndex);
		return super.save(attribute);
	}
	
	@Override
	public Attribute update(Attribute attribute) {
		return super.update(attribute);
	}
	
	@Override
	public void delete(Long id) {
		super.delete(id);
	}
	
	@Override
	public void delete(Long... ids) {
		super.delete(ids);
	}
	
	@Override
	public void delete(Attribute attribute) {
		if (attribute != null) {
			productDao.clearAttributeValue(attribute);
		}

		super.delete(attribute);
	}
	
}