package com.jfinalshop.service;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.dao.BusinessAttributeDao;
import com.jfinalshop.model.BusinessAttribute;
import com.jfinalshop.util.Assert;

/**
 * Service - 商家注册项
 * 
 */
@Singleton
public class BusinessAttributeService extends BaseService<BusinessAttribute> {

	/**
	 * 构造方法
	 */
	public BusinessAttributeService() {
		super(BusinessAttribute.class);
	}
	
	@Inject
	private BusinessAttributeDao businessAttributeDao;
	
	/**
	 * 查找未使用的对象属性序号
	 * 
	 * @return 未使用的对象属性序号，若无可用序号则返回null
	 */
	public Integer findUnusedPropertyIndex() {
		return businessAttributeDao.findUnusedPropertyIndex();
	}

	/**
	 * 查找 商家注册项
	 * 
	 * @param isEnabled
	 *            是否启用
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @param useCache
	 *            是否使用缓存
	 * @return 会员注册项
	 */
	public List<BusinessAttribute> findList(Boolean isEnabled, Integer count, List<Filter> filters, List<Order> orders, boolean useCache) {
		return businessAttributeDao.findList(isEnabled, count, filters, orders);

	}

	/**
	 * 查找商家注册项
	 * 
	 * @param isEnabled
	 *            是否启用
	 * @param useCache
	 *            是否使用缓存
	 * @return 会员注册项
	 */
	public List<BusinessAttribute> findList(Boolean isEnabled, boolean useCache) {
		return businessAttributeDao.findList(isEnabled, null, null, null);
	}

	/**
	 * 商家注册项值验证
	 * 
	 * @param businessAttribute
	 *            商家注册项
	 * @param values
	 *            值
	 * @return 是否验证通过
	 */
	public boolean isValid(BusinessAttribute businessAttribute, String[] values) {
		Assert.notNull(businessAttribute);
		Assert.notNull(businessAttribute.getTypeName());

		String value = ArrayUtils.isNotEmpty(values) ? values[0].trim() : null;
		switch (businessAttribute.getTypeName()) {
		case name:
		case licenseNumber:
		case licenseImage:
		case legalPerson:
		case idCard:
		case idCardImage:
		case phone:
		case organizationCode:
		case organizationImage:
		case identificationNumber:
		case taxImage:
		case bankName:
		case bankAccount:
		case text:
		case image:
		case date:
			if (businessAttribute.getIsRequired() && StringUtils.isEmpty(value)) {
				return false;
			}
			if (StringUtils.isNotEmpty(businessAttribute.getPattern()) && StringUtils.isNotEmpty(value) && !Pattern.compile(businessAttribute.getPattern()).matcher(value).matches()) {
				return false;
			}
			break;
		case select:
			if (businessAttribute.getIsRequired() && StringUtils.isEmpty(value)) {
				return false;
			}
			if (CollectionUtils.isEmpty(businessAttribute.getOptionsConverter())) {
				return false;
			}
			if (StringUtils.isNotEmpty(value) && !businessAttribute.getOptions().contains(value)) {
				return false;
			}
			break;
		case checkbox:
			if (businessAttribute.getIsRequired() && ArrayUtils.isEmpty(values)) {
				return false;
			}
			if (CollectionUtils.isEmpty(businessAttribute.getOptionsConverter())) {
				return false;
			}
			if (ArrayUtils.isNotEmpty(values) && !businessAttribute.getOptionsConverter().containsAll(Arrays.asList(values))) {
				return false;
			}
			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * 转换为商家注册项值
	 * 
	 * @param businessAttribute
	 *            商家注册项
	 * @param values
	 *            值
	 * @return 会员注册项值
	 */
	public Object toBusinessAttributeValue(BusinessAttribute businessAttribute, String[] values) {
		Assert.notNull(businessAttribute);
		Assert.notNull(businessAttribute.getTypeName());

		if (ArrayUtils.isEmpty(values)) {
			return null;
		}

		String value = values[0].trim();
		switch (businessAttribute.getTypeName()) {
		case name:
		case licenseNumber:
		case licenseImage:
		case legalPerson:
		case idCard:
		case idCardImage:
		case phone:
		case organizationCode:
		case organizationImage:
		case identificationNumber:
		case taxImage:
		case bankName:
		case bankAccount:
		case text:
		case image:
		case date:
			return StringUtils.isNotEmpty(value) ? value : null;
		case select:
			if (CollectionUtils.isNotEmpty(businessAttribute.getOptionsConverter()) && businessAttribute.getOptions().contains(value)) {
				return value;
			}
			break;
		case checkbox:
			if (CollectionUtils.isNotEmpty(businessAttribute.getOptionsConverter()) && businessAttribute.getOptionsConverter().containsAll(Arrays.asList(values))) {
				return Arrays.asList(values);
			}
			break;
		default:
			break;
		}
		return null;
	}

	@Override
	public BusinessAttribute save(BusinessAttribute businessAttribute) {
		Assert.notNull(businessAttribute);

		Integer unusedPropertyIndex = businessAttributeDao.findUnusedPropertyIndex();
		Assert.notNull(unusedPropertyIndex);

		businessAttribute.setPropertyIndex(unusedPropertyIndex);

		return super.save(businessAttribute);
	}
	
	@Override
	public BusinessAttribute update(BusinessAttribute businessAttribute) {
		return super.update(businessAttribute);
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
	public void delete(BusinessAttribute businessAttribute) {
		if (businessAttribute != null) {
			businessAttributeDao.clearAttributeValue(businessAttribute);
		}

		super.delete(businessAttribute);
	}
}