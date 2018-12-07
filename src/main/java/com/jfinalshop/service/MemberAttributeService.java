package com.jfinalshop.service;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;

import com.jfinalshop.CommonAttributes;
import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.dao.AreaDao;
import com.jfinalshop.dao.MemberAttributeDao;
import com.jfinalshop.dao.MemberDao;
import com.jfinalshop.model.Area;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.MemberAttribute;
import com.jfinalshop.util.Assert;

/**
 * Service - 会员注册项
 * 
 */
@Singleton
public class MemberAttributeService extends BaseService<MemberAttribute> {

	/**
	 * 构造方法
	 */
	public MemberAttributeService() {
		super(MemberAttribute.class);
	}
	
	@Inject
	private MemberAttributeDao memberAttributeDao;
	@Inject
	private MemberDao memberDao;
	@Inject
	private AreaDao areaDao;
	
	/**
	 * 查找未使用的对象属性序号
	 * 
	 * @return 未使用的对象属性序号，若无可用序号则返回null
	 */
	public Integer findUnusedPropertyIndex() {
		return memberAttributeDao.findUnusedPropertyIndex();
	}

	/**
	 * 查找会员注册项
	 * 
	 * @param isEnabled
	 *            是否启用
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 会员注册项
	 */
	public List<MemberAttribute> findList(Boolean isEnabled, Integer count, List<Filter> filters, List<Order> orders) {
		return memberAttributeDao.findList(isEnabled, count, filters, orders);
	}

	/**
	 * 查找会员注册项
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
	public List<MemberAttribute> findList(Boolean isEnabled, Integer count, List<Filter> filters, List<Order> orders, boolean useCache) {
		return memberAttributeDao.findList(isEnabled, count, filters, orders);
	}

	/**
	 * 查找会员注册项
	 * 
	 * @param isEnabled
	 *            是否启用
	 * @param useCache
	 *            是否使用缓存
	 * @return 会员注册项
	 */
	public List<MemberAttribute> findList(Boolean isEnabled, boolean useCache) {
		return memberAttributeDao.findList(isEnabled, null, null, null);
	}

	/**
	 * 会员注册项值验证
	 * 
	 * @param memberAttribute
	 *            会员注册项
	 * @param values
	 *            值
	 * @return 是否验证通过
	 */
	public boolean isValid(MemberAttribute memberAttribute, String[] values) {
		Assert.notNull(memberAttribute);
		Assert.notNull(memberAttribute.getTypeName());

		String value = ArrayUtils.isNotEmpty(values) ? values[0].trim() : null;
		switch (memberAttribute.getTypeName()) {
		case name:
		case address:
		case zipCode:
		case phone:
		case text:
			if (memberAttribute.getIsRequired() && StringUtils.isEmpty(value)) {
				return false;
			}
			if (StringUtils.isNotEmpty(memberAttribute.getPattern()) && StringUtils.isNotEmpty(value) && !Pattern.compile(memberAttribute.getPattern()).matcher(value).matches()) {
				return false;
			}
			break;
		case gender:
			if (memberAttribute.getIsRequired() && StringUtils.isEmpty(value)) {
				return false;
			}
			if (StringUtils.isNotEmpty(value)) {
				try {
					Member.Gender.valueOf(value);
				} catch (IllegalArgumentException e) {
					return false;
				}
			}
			break;
		case birth:
			if (memberAttribute.getIsRequired() && StringUtils.isEmpty(value)) {
				return false;
			}
			if (StringUtils.isNotEmpty(value)) {
				try {
					DateUtils.parseDate(value, CommonAttributes.DATE_PATTERNS);
				} catch (ParseException e) {
					return false;
				}
			}
			break;
		case area:
			Long id = NumberUtils.toLong(value, -1L);
			Area area = areaDao.find(id);
			if (memberAttribute.getIsRequired() && area == null) {
				return false;
			}
			break;
		case select:
			if (memberAttribute.getIsRequired() && StringUtils.isEmpty(value)) {
				return false;
			}
			if (CollectionUtils.isEmpty(memberAttribute.getOptionsConverter())) {
				return false;
			}
			if (StringUtils.isNotEmpty(value) && !memberAttribute.getOptions().contains(value)) {
				return false;
			}
			break;
		case checkbox:
			if (memberAttribute.getIsRequired() && ArrayUtils.isEmpty(values)) {
				return false;
			}
			if (CollectionUtils.isEmpty(memberAttribute.getOptionsConverter())) {
				return false;
			}
			if (ArrayUtils.isNotEmpty(values) && !memberAttribute.getOptionsConverter().containsAll(Arrays.asList(values))) {
				return false;
			}
			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * 转换为会员注册项值
	 * 
	 * @param memberAttribute
	 *            会员注册项
	 * @param values
	 *            值
	 * @return 会员注册项值
	 */
	public Object toMemberAttributeValue(MemberAttribute memberAttribute, String[] values) {
		Assert.notNull(memberAttribute);
		Assert.notNull(memberAttribute.getTypeName());

		if (ArrayUtils.isEmpty(values)) {
			return null;
		}

		String value = values[0].trim();
		switch (memberAttribute.getTypeName()) {
		case name:
		case address:
		case zipCode:
		case phone:
		case text:
			return StringUtils.isNotEmpty(value) ? value : null;
		case gender:
			if (StringUtils.isEmpty(value)) {
				return null;
			}
			try {
				return Member.Gender.valueOf(value);
			} catch (IllegalArgumentException e) {
				return null;
			}
		case birth:
			if (StringUtils.isEmpty(value)) {
				return null;
			}
			try {
				return DateUtils.parseDate(value, CommonAttributes.DATE_PATTERNS);
			} catch (ParseException e) {
				return null;
			}
		case area:
			Long id = NumberUtils.toLong(value, -1L);
			return areaDao.find(id);
		case select:
			if (CollectionUtils.isNotEmpty(memberAttribute.getOptionsConverter()) && memberAttribute.getOptions().contains(value)) {
				return value;
			}
			break;
		case checkbox:
			if (CollectionUtils.isNotEmpty(memberAttribute.getOptionsConverter()) && memberAttribute.getOptionsConverter().containsAll(Arrays.asList(values))) {
				return Arrays.asList(values);
			}
			break;
		default:
			break;
		}
		return null;
	}

	@Override
	public MemberAttribute save(MemberAttribute memberAttribute) {
		Assert.notNull(memberAttribute);

		Integer unusedPropertyIndex = memberAttributeDao.findUnusedPropertyIndex();
		Assert.notNull(unusedPropertyIndex);

		memberAttribute.setPropertyIndex(unusedPropertyIndex);

		return super.save(memberAttribute);
	}
	
	@Override
	public MemberAttribute update(MemberAttribute memberAttribute) {
		return super.update(memberAttribute);
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
	public void delete(MemberAttribute memberAttribute) {
		if (memberAttribute != null) {
			memberDao.clearAttributeValue(memberAttribute);
		}

		super.delete(memberAttribute);
	}
}