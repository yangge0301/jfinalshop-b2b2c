package com.jfinalshop.model;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinalshop.model.base.BaseBusiness;
import com.jfinalshop.util.JsonUtils;

/**
 * Model - 商家
 * 
 */
public class Business extends BaseBusiness<Business> {
	private static final long serialVersionUID = 3617364304792874603L;
	public static final Business dao = new Business().dao();
	
	/**
	 * 受限制商家权限
	 */
	public static final Set<String> RESTRICT_BUSINESS_PERMISSIONS = new HashSet<>();

	/**
	 * 正常商家权限
	 */
	public static final Set<String> NORMAL_BUSINESS_PERMISSIONS = new HashSet<>();

	/**
	 * 商家普通注册项值属性个数
	 */
	public static final int COMMON_ATTRIBUTE_VALUE_PROPERTY_COUNT = 20;

	/**
	 * 商家注册项值属性名称前缀
	 */
	public static final String ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX = "attributeValue";
	
	/**
	 * "登录失败尝试次数"缓存名称
	 */
	public static final String FAILED_LOGIN_ATTEMPTS_CACHE_NAME = "failedLoginAttempts";

	static {

		RESTRICT_BUSINESS_PERMISSIONS.add("business:index");
		RESTRICT_BUSINESS_PERMISSIONS.add("business:storeRegister");
		RESTRICT_BUSINESS_PERMISSIONS.add("business:storePayment");
		RESTRICT_BUSINESS_PERMISSIONS.add("business:storeReapply");

		NORMAL_BUSINESS_PERMISSIONS.add("business:index");
		NORMAL_BUSINESS_PERMISSIONS.add("business:product");
		NORMAL_BUSINESS_PERMISSIONS.add("business:stock");
		NORMAL_BUSINESS_PERMISSIONS.add("business:productNotify");
		NORMAL_BUSINESS_PERMISSIONS.add("business:consultation");
		NORMAL_BUSINESS_PERMISSIONS.add("business:review");
		NORMAL_BUSINESS_PERMISSIONS.add("business:order");
		NORMAL_BUSINESS_PERMISSIONS.add("business:print");
		NORMAL_BUSINESS_PERMISSIONS.add("business:deliveryTemplate");
		NORMAL_BUSINESS_PERMISSIONS.add("business:deliveryCenter");
		NORMAL_BUSINESS_PERMISSIONS.add("business:storeSetting");
		NORMAL_BUSINESS_PERMISSIONS.add("business:storeProductCategory");
		NORMAL_BUSINESS_PERMISSIONS.add("business:storeProductTag");
		NORMAL_BUSINESS_PERMISSIONS.add("business:categoryApplication");
		NORMAL_BUSINESS_PERMISSIONS.add("business:storePayment");
		NORMAL_BUSINESS_PERMISSIONS.add("business:shippingMethod");
		NORMAL_BUSINESS_PERMISSIONS.add("business:areaFreightConfig");
		NORMAL_BUSINESS_PERMISSIONS.add("business:storeAdImage");
		NORMAL_BUSINESS_PERMISSIONS.add("business:discountPromotion");
		NORMAL_BUSINESS_PERMISSIONS.add("business:fullReductionPromotion");
		NORMAL_BUSINESS_PERMISSIONS.add("business:coupon");
		NORMAL_BUSINESS_PERMISSIONS.add("business:deposit");
		NORMAL_BUSINESS_PERMISSIONS.add("business:cash");
		NORMAL_BUSINESS_PERMISSIONS.add("business:profile");
		NORMAL_BUSINESS_PERMISSIONS.add("business:password");
		NORMAL_BUSINESS_PERMISSIONS.add("business:instantMessage");
		NORMAL_BUSINESS_PERMISSIONS.add("business:orderStatistic");
		NORMAL_BUSINESS_PERMISSIONS.add("business:productRanking");
	}
	
	/**
	 * 店铺
	 */
	private Store store;
	
	/**
	 * 安全密匙
	 */
	//private SafeKey safeKey;

	/**
	 * 提现
	 */
	private List<Cash> cashes = new ArrayList<Cash>();

	/**
	 * 商家预存款记录
	 */
	private List<BusinessDepositLog> businessDepositLogs = new ArrayList<BusinessDepositLog>();
	
	/**
	 * 角色
	 */
	private List<Role> roles = new ArrayList<Role>();
	
	/**
	 * 获取安全密匙
	 * 
	 * @return 安全密匙
	 */
//	public SafeKey getSafeKey() {
//		safeKey.setExpire(this.getSafekeyExpire());
//		safeKey.setValue(this.getSafekeyValue());
//		return safeKey;
//	}
	
	/**
	 * 获取角色
	 * 
	 * @return 角色
	 */
	public List<Role> getRoles() {
		if (CollectionUtils.isEmpty(roles)) {
			String sql = "SELECT r.* FROM role r LEFT JOIN business_role ar ON r.id = ar.roles_id WHERE ar.business_id = ?";
			roles = Role.dao.find(sql, getId());
		}
		return roles;
	}
	
	/**
	 * 设置角色
	 * 
	 * @param roles
	 *            角色
	 */
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	
	/**
	 * 获取店铺
	 * 
	 * @return 店铺
	 */
	public Store getStore() {
		if (store == null) {
			String sql = "SELECT * FROM  `store` WHERE business_id = ?";
			store = Store.dao.findFirst(sql, getId());
		}
		return store;
	}

	/**
	 * 设置店铺
	 * 
	 * @param store
	 *            店铺
	 */
	public void setStore(Store store) {
		this.store = store;
	}

	/**
	 * 获取提现
	 * 
	 * @return 提现
	 */
	public List<Cash> getCashes() {
		return cashes;
	}

	/**
	 * 设置提现
	 * 
	 * @param cashes
	 *            提现
	 */
	public void setCashes(List<Cash> cashes) {
		this.cashes = cashes;
	}

	/**
	 * 获取商家预存款记录
	 * 
	 * @return 商家预存款记录
	 */
	public List<BusinessDepositLog> getBusinessDepositLogs() {
		return businessDepositLogs;
	}

	/**
	 * 设置商家预存款记录
	 * 
	 * @param businessDepositLogs
	 *            商家预存款记录
	 */
	public void setBusinessDepositLogs(List<BusinessDepositLog> businessDepositLogs) {
		this.businessDepositLogs = businessDepositLogs;
	}

	/**
	 * 获取商家注册项值
	 * 
	 * @param businessAttribute
	 *            商家注册项
	 * @return 商家注册项值
	 */
	public Object getAttributeValue(BusinessAttribute businessAttribute) {
		if (businessAttribute == null || businessAttribute.getType() == null) {
			return null;
		}
		switch (businessAttribute.getTypeName()) {
		case name:
			return getName();
		case licenseNumber:
			return getLicenseNumber();
		case licenseImage:
			return getLicenseImage();
		case legalPerson:
			return getLegalPerson();
		case idCard:
			return getIdCard();
		case idCardImage:
			return getIdCardImage();
		case phone:
			return getPhone();
		case organizationCode:
			return getOrganizationCode();
		case organizationImage:
			return getOrganizationImage();
		case identificationNumber:
			return getIdentificationNumber();
		case taxImage:
			return getTaxImage();
		case bankName:
			return getBankName();
		case bankAccount:
			return getBankAccount();
		case text:
		case select:
			if (businessAttribute.getPropertyIndex() != null) {
				try {
					String propertyName = ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + businessAttribute.getPropertyIndex();
					return PropertyUtils.getProperty(this, propertyName);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e.getMessage(), e);
				} catch (InvocationTargetException e) {
					throw new RuntimeException(e.getMessage(), e);
				} catch (NoSuchMethodException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
			break;
		case image:
		case date:
			if (businessAttribute.getPropertyIndex() != null) {
				try {
					String propertyName = ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + businessAttribute.getPropertyIndex();
					return PropertyUtils.getProperty(this, propertyName);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e.getMessage(), e);
				} catch (InvocationTargetException e) {
					throw new RuntimeException(e.getMessage(), e);
				} catch (NoSuchMethodException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
			break;
		case checkbox:
			if (businessAttribute.getPropertyIndex() != null) {
				try {
					String propertyName = ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + businessAttribute.getPropertyIndex();
					String propertyValue = (String) PropertyUtils.getProperty(this, propertyName);
					if (StringUtils.isNotEmpty(propertyValue)) {
						return JsonUtils.toObject(propertyValue, List.class);
					}
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e.getMessage(), e);
				} catch (InvocationTargetException e) {
					throw new RuntimeException(e.getMessage(), e);
				} catch (NoSuchMethodException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
			break;
		}
		return null;
	}

	/**
	 * 设置商家注册项值
	 * 
	 * @param businessAttribute
	 *            商家注册项
	 * @param businessAttributeValue
	 *            商家注册项值
	 */
	public void setAttributeValue(BusinessAttribute businessAttribute, Object businessAttributeValue) {
		if (businessAttribute == null || businessAttribute.getType() == null) {
			return;
		}
		switch (businessAttribute.getTypeName()) {
		case name:
			if (businessAttributeValue instanceof String || businessAttributeValue == null) {
				setName((String) businessAttributeValue);
			}
			break;
		case licenseNumber:
			if (businessAttributeValue instanceof String || businessAttributeValue == null) {
				setLicenseNumber((String) businessAttributeValue);
			}
			break;
		case licenseImage:
			if (businessAttributeValue instanceof String || businessAttributeValue == null) {
				setLicenseImage((String) businessAttributeValue);
			}
			break;
		case legalPerson:
			if (businessAttributeValue instanceof String || businessAttributeValue == null) {
				setLegalPerson((String) businessAttributeValue);
			}
			break;
		case idCard:
			if (businessAttributeValue instanceof String || businessAttributeValue == null) {
				setIdCard((String) businessAttributeValue);
			}
			break;
		case idCardImage:
			if (businessAttributeValue instanceof String || businessAttributeValue == null) {
				setIdCardImage((String) businessAttributeValue);
			}
			break;
		case phone:
			if (businessAttributeValue instanceof String || businessAttributeValue == null) {
				setPhone((String) businessAttributeValue);
			}
			break;
		case organizationCode:
			if (businessAttributeValue instanceof String || businessAttributeValue == null) {
				setOrganizationCode((String) businessAttributeValue);
			}
			break;
		case organizationImage:
			if (businessAttributeValue instanceof String || businessAttributeValue == null) {
				setOrganizationImage((String) businessAttributeValue);
			}
			break;
		case identificationNumber:
			if (businessAttributeValue instanceof String || businessAttributeValue == null) {
				setIdentificationNumber((String) businessAttributeValue);
			}
			break;
		case taxImage:
			if (businessAttributeValue instanceof String || businessAttributeValue == null) {
				setTaxImage((String) businessAttributeValue);
			}
			break;
		case bankName:
			if (businessAttributeValue instanceof String || businessAttributeValue == null) {
				setBankName((String) businessAttributeValue);
			}
			break;
		case bankAccount:
			if (businessAttributeValue instanceof String || businessAttributeValue == null) {
				setBankAccount((String) businessAttributeValue);
			}
			break;
		case text:
		case select:
			if ((businessAttributeValue instanceof String || businessAttributeValue == null) && businessAttribute.getPropertyIndex() != null) {
				try {
					String propertyName = ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + businessAttribute.getPropertyIndex();
					PropertyUtils.setProperty(this, propertyName, businessAttributeValue);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e.getMessage(), e);
				} catch (InvocationTargetException e) {
					throw new RuntimeException(e.getMessage(), e);
				} catch (NoSuchMethodException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
			break;
		case image:
		case date:
			if ((businessAttributeValue instanceof String || businessAttributeValue == null) && businessAttribute.getPropertyIndex() != null) {
				try {
					String propertyName = ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + businessAttribute.getPropertyIndex();
					PropertyUtils.setProperty(this, propertyName, businessAttributeValue);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e.getMessage(), e);
				} catch (InvocationTargetException e) {
					throw new RuntimeException(e.getMessage(), e);
				} catch (NoSuchMethodException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
			break;
		case checkbox:
			if ((businessAttributeValue instanceof Collection || businessAttributeValue == null) && businessAttribute.getPropertyIndex() != null) {
				try {
					String propertyName = ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + businessAttribute.getPropertyIndex();
					PropertyUtils.setProperty(this, propertyName, businessAttributeValue != null ? JsonUtils.toJson(businessAttributeValue) : null);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e.getMessage(), e);
				} catch (InvocationTargetException e) {
					throw new RuntimeException(e.getMessage(), e);
				} catch (NoSuchMethodException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
			break;
		}
	}

	/**
	 * 移除所有商家注册项值
	 */
	public void removeAttributeValue() {
		setName(null);
		setLicenseNumber(null);
		setLicenseImage(null);
		setLegalPerson(null);
		setIdCard(null);
		setIdCardImage(null);
		setPhone(null);
		setOrganizationCode(null);
		setOrganizationImage(null);
		setIdentificationNumber(null);
		setTaxImage(null);
		setBankName(null);
		setBankAccount(null);
		for (int i = 0; i < COMMON_ATTRIBUTE_VALUE_PROPERTY_COUNT; i++) {
			String propertyName = ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + i;
			try {
				PropertyUtils.setProperty(this, propertyName, null);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e.getMessage(), e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e.getMessage(), e);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
	}

	public String getDisplayName() {
		return getUsername();
	}

	public Object getPrincipal() {
		return getUsername();
	}

	public Object getCredentials() {
		return getPassword();
	}

	/**
	 * 判断是否为新建对象
	 * 
	 * @return 是否为新建对象
	 */
	public boolean isNew() {
		return getId() == null;
	}
	
}
