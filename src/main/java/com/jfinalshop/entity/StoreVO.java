package com.jfinalshop.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.jfinalshop.model.Business;
import com.jfinalshop.model.StoreCategory;
import com.jfinalshop.model.StoreRank;

public class StoreVO {

	/**
	 * 路径
	 */
	private static final String PATH = "/store/%d";

	/**
	 * 类型
	 */
	public enum Type {

		/**
		 * 普通
		 */
		general,

		/**
		 * 自营
		 */
		self
	}

	/**
	 * 状态
	 */
	public enum Status {

		/**
		 * 等待审核
		 */
		pending,

		/**
		 * 审核失败
		 */
		failed,

		/**
		 * 审核通过
		 */
		approved,

		/**
		 * 开店成功
		 */
		success
	}

	/**
	 * ID
	 */
	private Long id;
	
	/**
	 * 创建日期
	 */
	private Date createdDate;

	/**
	 * 最后修改日期
	 */
	private Date lastModifiedDate;

	/**
	 * 版本
	 */
	private Long version;
	
	/**
	 * 名称
	 */
	private String name;

	/**
	 * 类型
	 */
	private Integer type;

	/**
	 * 状态
	 */
	private Integer status;

	/**
	 * logo
	 */
	private String logo;

	/**
	 * E-mail
	 */
	private String email;

	/**
	 * 手机
	 */
	private String mobile;

	/**
	 * 电话
	 */
	private String phone;

	/**
	 * 地址
	 */
	private String address;

	/**
	 * 邮编
	 */
	private String zipCode;

	/**
	 * 简介
	 */
	private String introduction;

	/**
	 * 搜索关键词
	 */
	private String keyword;

	/**
	 * 到期日期
	 */
	private Date endDate;

	/**
	 * 折扣促销到期日期
	 */
	private Date discountPromotionEndDate;

	/**
	 * 满减促销到期日期
	 */
	private Date fullReductionPromotionEndDate;

	/**
	 * 是否启用
	 */
	private Boolean isEnabled;

	/**
	 * 已付保证金
	 */
	private BigDecimal bailPaid;

	/**
	 * 商家
	 */
	private Business business;

	/**
	 * 店铺等级
	 */
	private StoreRank storeRank;
	
	/**
	 * 店铺等级
	 */
	private Long storeRankId;

	/**
	 * 店铺分类
	 */
	private StoreCategory storeCategory;

	/**
	 * 类型名称
	 */
	public Type getTypeName() {
		return getType() == null ? null : Type.values()[getType()];
	}
	
	/**
	 * 类型名称
	 */
	public Status getStatusName() {
		return getStatus() == null ? null : Status.values()[getStatus()];
	}
	
	/**
	 * 获取ID
	 * 
	 * @return ID
	 */
	public Long getId() {
		return id;
	}

	/**
	 * 设置ID
	 * 
	 * @param id
	 *            ID
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 获取创建日期
	 * 
	 * @return 创建日期
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * 设置创建日期
	 * 
	 * @param createdDate
	 *            创建日期
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * 获取最后修改日期
	 * 
	 * @return 最后修改日期
	 */
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	/**
	 * 设置最后修改日期
	 * 
	 * @param lastModifiedDate
	 *            最后修改日期
	 */
	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	/**
	 * 获取版本
	 * 
	 * @return 版本
	 */
	public Long getVersion() {
		return version;
	}

	/**
	 * 设置版本
	 * 
	 * @param version
	 *            版本
	 */
	public void setVersion(Long version) {
		this.version = version;
	}
	
	/**
	 * 获取名称
	 * 
	 * @return 名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置名称
	 * 
	 * @param name
	 *            名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取类型
	 * 
	 * @return 类型
	 */
	public Integer getType() {
		return type;
	}

	/**
	 * 设置类型
	 * 
	 * @param type
	 *            类型
	 */
	public void setType(Integer type) {
		this.type = type;
	}

	/**
	 * 获取状态
	 * 
	 * @return 状态
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * 设置状态
	 * 
	 * @param status
	 *            状态
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * 获取logo
	 * 
	 * @return logo
	 */
	public String getLogo() {
		return logo;
	}

	/**
	 * 设置logo
	 * 
	 * @param logo
	 *            logo
	 */
	public void setLogo(String logo) {
		this.logo = logo;
	}

	/**
	 * 获取E-mail
	 * 
	 * @return E-mail
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * 设置E-mail
	 * 
	 * @param email
	 *            E-mail
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * 获取手机
	 * 
	 * @return 手机
	 */
	public String getMobile() {
		return mobile;
	}

	/**
	 * 设置手机
	 * 
	 * @param mobile
	 *            手机
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	/**
	 * 获取电话
	 * 
	 * @return 电话
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * 设置电话
	 * 
	 * @param phone
	 *            电话
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * 获取地址
	 * 
	 * @return 地址
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * 设置地址
	 * 
	 * @param address
	 *            地址
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * 获取邮编
	 * 
	 * @return 邮编
	 */
	public String getZipCode() {
		return zipCode;
	}

	/**
	 * 设置邮编
	 * 
	 * @param zipCode
	 *            邮编
	 */
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	/**
	 * 获取简介
	 * 
	 * @return 简介
	 */
	public String getIntroduction() {
		return introduction;
	}

	/**
	 * 设置简介
	 * 
	 * @param introduction
	 *            简介
	 */
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	/**
	 * 获取搜索关键词
	 * 
	 * @return 搜索关键词
	 */
	public String getKeyword() {
		return keyword;
	}

	/**
	 * 设置搜索关键词
	 * 
	 * @param keyword
	 *            搜索关键词
	 */
	public void setKeyword(String keyword) {
		if (keyword != null) {
			keyword = keyword.replaceAll("[,\\s]*,[,\\s]*", ",").replaceAll("^,|,$", "");
		}
		this.keyword = keyword;
	}

	/**
	 * 获取到期日期
	 * 
	 * @return 到期日期
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * 设置到期日期
	 * 
	 * @param endDate
	 *            到期日期
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * 获取折扣促销到期日期
	 * 
	 * @return 折扣促销到期日期
	 */
	public Date getDiscountPromotionEndDate() {
		return discountPromotionEndDate;
	}

	/**
	 * 设置折扣促销到期日期
	 * 
	 * @param discountPromotionEndDate
	 *            折扣促销到期日期
	 */
	public void setDiscountPromotionEndDate(Date discountPromotionEndDate) {
		this.discountPromotionEndDate = discountPromotionEndDate;
	}

	/**
	 * 获取满减促销到期日期
	 * 
	 * @return 满减促销到期日期
	 */
	public Date getFullReductionPromotionEndDate() {
		return fullReductionPromotionEndDate;
	}

	/**
	 * 设置满减促销到期日期
	 * 
	 * @param fullReductionPromotionEndDate
	 *            满减促销到期日期
	 */
	public void setFullReductionPromotionEndDate(Date fullReductionPromotionEndDate) {
		this.fullReductionPromotionEndDate = fullReductionPromotionEndDate;
	}

	/**
	 * 获取是否启用
	 * 
	 * @return 是否启用
	 */
	public Boolean getIsEnabled() {
		return isEnabled;
	}

	/**
	 * 设置是否启用
	 * 
	 * @param isEnabled
	 *            是否启用
	 */
	public void setIsEnabled(Boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	/**
	 * 获取已付保证金
	 * 
	 * @return 已付保证金
	 */
	public BigDecimal getBailPaid() {
		return bailPaid;
	}

	/**
	 * 设置已付保证金
	 * 
	 * @param bailPaid
	 *            已付保证金
	 */
	public void setBailPaid(BigDecimal bailPaid) {
		this.bailPaid = bailPaid;
	}

	/**
	 * 获取商家
	 * 
	 * @return 商家
	 */
	public Business getBusiness() {
		return business;
	}

	/**
	 * 设置商家
	 * 
	 * @param business
	 *            商家
	 */
	public void setBusiness(Business business) {
		this.business = business;
	}

	/**
	 * 获取店铺等级
	 * 
	 * @return 店铺等级
	 */
	public StoreRank getStoreRank() {
		if (storeRank == null) {
			storeRank = StoreRank.dao.findById(getStoreRankId());
		}
		return storeRank;
	}

	/**
	 * 设置店铺等级
	 * 
	 * @param storeRank
	 *            店铺等级
	 */
	public void setStoreRank(StoreRank storeRank) {
		this.storeRank = storeRank;
	}
	
	/**
	 * 获取店铺等级
	 * 
	 * @return 店铺等级
	 */
	public Long getStoreRankId() {
		return storeRankId;
	}

	/**
	 * 设置店铺等级
	 * 
	 * @param storeRank
	 *            店铺等级
	 */
	public void setStoreRankId(Long storeRankId) {
		this.storeRankId = storeRankId;
	}

	/**
	 * 获取店铺分类
	 * 
	 * @return 店铺分类
	 */
	public StoreCategory getStoreCategory() {
		return storeCategory;
	}

	/**
	 * 设置店铺分类
	 * 
	 * @param storeCategory
	 *            店铺分类
	 */
	public void setStoreCategory(StoreCategory storeCategory) {
		this.storeCategory = storeCategory;
	}

	/**
	 * 获取路径
	 * 
	 * @return 路径
	 */
	public String getPath() {
		return String.format(PATH, getId());
	}


}
