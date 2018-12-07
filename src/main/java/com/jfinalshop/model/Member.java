package com.jfinalshop.model;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinalshop.model.base.BaseMember;
import com.jfinalshop.util.JsonUtils;

/**
 * Model - 会员
 * 
 */
public class Member extends BaseMember<Member> {
	private static final long serialVersionUID = -2107766706595334754L;
	public static final Member dao = new Member().dao();
	
	/**
	 * 权限
	 */
	public static final Set<String> PERMISSIONS = new HashSet<>(Arrays.asList("member"));
	
	/**
	 * "登录失败尝试次数"缓存名称
	 */
	public static final String FAILED_LOGIN_ATTEMPTS_CACHE_NAME = "failedLoginAttempts";


	/**
	 * 性别
	 */
	public enum Gender {

		/**
		 * 男
		 */
		male,

		/**
		 * 女
		 */
		female
	}

	/**
	 * 排名类型
	 */
	public enum RankingType {

		/**
		 * 积分
		 */
		point,

		/**
		 * 余额
		 */
		balance,

		/**
		 * 消费金额
		 */
		amount
	}
	
	/**
	 * 密码找回类型
	 */
	public enum PasswordType {

		/**
		 * 会员
		 */
		member,

		/**
		 * 商家
		 */
		business
	}

	/**
	 * "当前用户名"Cookie名称
	 */
	public static final String CURRENT_USERNAME_COOKIE_NAME = "currentMemberUsername";

	/**
	 * 会员注册项值属性个数
	 */
	public static final int ATTRIBUTE_VALUE_PROPERTY_COUNT = 10;

	/**
	 * 会员注册项值属性名称前缀
	 */
	public static final String ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX = "attributeValue";

	/**
	 * 地区
	 */
	private Area area;

	/**
	 * 会员等级
	 */
	private MemberRank memberRank;

	/**
	 * 购物车
	 */
	private Cart cart;
	
	/**
	 * 店铺
	 */
	private Store store;
	
	/**
	 * 安全密匙
	 */
	//private SafeKey safeKey;

	/**
	 * 订单
	 */
	private List<Order> orders = new ArrayList<Order>();

	/**
	 * 会员预存款记录
	 */
	private List<MemberDepositLog> memberDepositLogs = new ArrayList<MemberDepositLog>();

	/**
	 * 优惠码
	 */
	private List<CouponCode> couponCodes = new ArrayList<CouponCode>();

	/**
	 * 收货地址
	 */
	private List<Receiver> receivers = new ArrayList<Receiver>();

	/**
	 * 评论
	 */
	private List<Review> reviews = new ArrayList<Review>();

	/**
	 * 咨询
	 */
	private List<Consultation> consultations = new ArrayList<Consultation>();

	/**
	 * 商品收藏
	 */
	private List<ProductFavorite> productFavorites = new ArrayList<ProductFavorite>();

	/**
	 * 店铺收藏
	 */
	private List<StoreFavorite> storeFavorites = new ArrayList<StoreFavorite>();

	/**
	 * 到货通知
	 */
	private List<ProductNotify> productNotifies = new ArrayList<ProductNotify>();

	/**
	 * 接收的消息
	 */
	private List<Message> inMessages = new ArrayList<Message>();

	/**
	 * 发送的消息
	 */
	private List<Message> outMessages = new ArrayList<Message>();

	/**
	 * 积分记录
	 */
	private List<PointLog> pointLogs = new ArrayList<PointLog>();
	
	/**
	 * 性别名称
	 */
	public Gender getGenderName() {
		return getGender() != null ? Gender.values()[getGender()] : null;
	}
	
	/**
	 * 获取安全密匙
	 * 
	 * @return 安全密匙
	 */
//	public SafeKey getSafeKey() {
//		safeKey.setExpire(getSafekeyExpire());
//		safeKey.setValue(getSafekeyValue());
//		return safeKey;
//	}
	
	/**
	 * 获取店铺
	 * 
	 * @return 店铺
	 */
	public Store getStore() {
		if (store == null) {
			store = Store.dao.findById(getStoreId());
		}
		return store;
	}
	
	/**
	 * 获取地区
	 * 
	 * @return 地区
	 */
	public Area getArea() {
		if (area == null) {
			area = Area.dao.findById(getAreaId());
		}
		return area;
	}

	/**
	 * 设置地区
	 * 
	 * @param area
	 *            地区
	 */
	public void setArea(Area area) {
		this.area = area;
	}

	/**
	 * 获取会员等级
	 * 
	 * @return 会员等级
	 */
	public MemberRank getMemberRank() {
		if (memberRank == null) {
			memberRank = MemberRank.dao.findById(getMemberRankId());
		}
		return memberRank;
	}

	/**
	 * 设置会员等级
	 * 
	 * @param memberRank
	 *            会员等级
	 */
	public void setMemberRank(MemberRank memberRank) {
		this.memberRank = memberRank;
	}

	/**
	 * 获取购物车
	 * 
	 * @return 购物车
	 */
	public Cart getCart() {
		if (cart == null) {
			String sql = "SELECT * FROM `cart` WHERE member_id = ?";
			cart = Cart.dao.findFirst(sql, getId());
		}
		return cart;
	}

	/**
	 * 设置购物车
	 * 
	 * @param cart
	 *            购物车
	 */
	public void setCart(Cart cart) {
		this.cart = cart;
	}

	/**
	 * 获取订单
	 * 
	 * @return 订单
	 */
	public List<Order> getOrders() {
		if (CollectionUtils.isEmpty(orders)) {
			String sql = "SELECT * FROM `order` WHERE member_id = ?";
			orders = Order.dao.find(sql, getId());
		}
		return orders;
	}

	/**
	 * 设置订单
	 * 
	 * @param orders
	 *            订单
	 */
	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	/**
	 * 获取会员预存款记录
	 * 
	 * @return 会员预存款记录
	 */
	public List<MemberDepositLog> getMemberDepositLogs() {
		if (CollectionUtils.isEmpty(memberDepositLogs)) {
			String sql = "SELECT * FROM `member_deposit_log` WHERE member_id = ?";
			memberDepositLogs = MemberDepositLog.dao.find(sql, getId());
		}
		return memberDepositLogs;
	}

	/**
	 * 设置会员预存款记录
	 * 
	 * @param memberDepositLogs
	 *            会员预存款记录
	 */
	public void setMemberDepositLogs(List<MemberDepositLog> memberDepositLogs) {
		this.memberDepositLogs = memberDepositLogs;
	}

	/**
	 * 获取优惠码
	 * 
	 * @return 优惠码
	 */
	public List<CouponCode> getCouponCodes() {
		if (CollectionUtils.isEmpty(couponCodes)) {
			String sql = "SELECT * FROM `coupon_code` WHERE member_id = ?";
			couponCodes = CouponCode.dao.find(sql, getId());
		}
		return couponCodes;
	}

	/**
	 * 设置优惠码
	 * 
	 * @param couponCodes
	 *            优惠码
	 */
	public void setCouponCodes(List<CouponCode> couponCodes) {
		this.couponCodes = couponCodes;
	}

	/**
	 * 获取收货地址
	 * 
	 * @return 收货地址
	 */
	public List<Receiver> getReceivers() {
		if (CollectionUtils.isEmpty(receivers)) {
			String sql = "SELECT * FROM `receiver` WHERE member_id = ?";
			receivers = Receiver.dao.find(sql, getId());
		}
		return receivers;
	}

	/**
	 * 设置收货地址
	 * 
	 * @param receivers
	 *            收货地址
	 */
	public void setReceivers(List<Receiver> receivers) {
		this.receivers = receivers;
	}

	/**
	 * 获取评论
	 * 
	 * @return 评论
	 */
	public List<Review> getReviews() {
		if (CollectionUtils.isEmpty(reviews)) {
			String sql = "SELECT * FROM `review` WHERE member_id = ?";
			reviews = Review.dao.find(sql, getId());
		}
 		return reviews;
	}

	/**
	 * 设置评论
	 * 
	 * @param reviews
	 *            评论
	 */
	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}

	/**
	 * 获取咨询
	 * 
	 * @return 咨询
	 */
	public List<Consultation> getConsultations() {
		if (CollectionUtils.isEmpty(consultations)) {
			String sql = "SELECT * FROM `consultation` WHERE member_id = ?";
			consultations = Consultation.dao.find(sql, getId());
		}
		return consultations;
	}

	/**
	 * 设置咨询
	 * 
	 * @param consultations
	 *            咨询
	 */
	public void setConsultations(List<Consultation> consultations) {
		this.consultations = consultations;
	}

	/**
	 * 获取商品收藏
	 * 
	 * @return 商品收藏
	 */
	public List<ProductFavorite> getProductFavorites() {
		if (CollectionUtils.isEmpty(productFavorites)) {
			String sql = "SELECT * FROM `product_favorite` WHERE member_id = ?";
			productFavorites = ProductFavorite.dao.find(sql, getId());
		}
		return productFavorites;
	}

	/**
	 * 设置商品收藏
	 * 
	 * @param productFavorites
	 *            商品收藏
	 */
	public void setProductFavorites(List<ProductFavorite> productFavorites) {
		this.productFavorites = productFavorites;
	}

	/**
	 * 获取店铺收藏
	 * 
	 * @return 店铺收藏
	 */
	public List<StoreFavorite> getStoreFavorites() {
		if (CollectionUtils.isEmpty(storeFavorites)) {
			String sql = "SELECT * FROM `store_favorite` WHERE member_id = ?";
			storeFavorites = StoreFavorite.dao.find(sql, getId());
		}
		return storeFavorites;
	}

	/**
	 * 设置店铺收藏
	 * 
	 * @param storeFavorites
	 *            店铺收藏
	 */
	public void setStoreFavorites(List<StoreFavorite> storeFavorites) {
		this.storeFavorites = storeFavorites;
	}

	/**
	 * 获取到货通知
	 * 
	 * @return 到货通知
	 */
	public List<ProductNotify> getProductNotifies() {
		if (CollectionUtils.isEmpty(productNotifies)) {
			String sql = "SELECT * FROM `product_notify` WHERE member_id = ?";
			productNotifies = ProductNotify.dao.find(sql, getId());
		}
		return productNotifies;
	}

	/**
	 * 设置到货通知
	 * 
	 * @param productNotifies
	 *            到货通知
	 */
	public void setProductNotifies(List<ProductNotify> productNotifies) {
		this.productNotifies = productNotifies;
	}

	/**
	 * 获取接收的消息
	 * 
	 * @return 接收的消息
	 */
	public List<Message> getInMessages() {
		if (CollectionUtils.isEmpty(inMessages)) {
			String sql = "SELECT * FROM `message` WHERE receiver_id = ?";
			inMessages = Message.dao.find(sql, getId());
		}
		return inMessages;
	}

	/**
	 * 设置接收的消息
	 * 
	 * @param inMessages
	 *            接收的消息
	 */
	public void setInMessages(List<Message> inMessages) {
		this.inMessages = inMessages;
	}

	/**
	 * 获取发送的消息
	 * 
	 * @return 发送的消息
	 */
	public List<Message> getOutMessages() {
		if (CollectionUtils.isEmpty(outMessages)) {
			String sql = "SELECT * FROM `message` WHERE sender_id = ?";
			outMessages = Message.dao.find(sql, getId());
		}
		return outMessages;
	}

	/**
	 * 设置发送的消息
	 * 
	 * @param outMessages
	 *            发送的消息
	 */
	public void setOutMessages(List<Message> outMessages) {
		this.outMessages = outMessages;
	}

	/**
	 * 获取积分记录
	 * 
	 * @return 积分记录
	 */
	public List<PointLog> getPointLogs() {
		if (CollectionUtils.isEmpty(pointLogs)) {
			String sql = "SELECT * FROM `point_log` WHERE member_id = ?";
			pointLogs = PointLog.dao.find(sql, getId());
		}
		return pointLogs;
	}

	/**
	 * 设置积分记录
	 * 
	 * @param pointLogs
	 *            积分记录
	 */
	public void setPointLogs(List<PointLog> pointLogs) {
		this.pointLogs = pointLogs;
	}

	/**
	 * 获取会员注册项值
	 * 
	 * @param memberAttribute
	 *            会员注册项
	 * @return 会员注册项值
	 */
	public Object getAttributeValue(MemberAttribute memberAttribute) {
		if (memberAttribute == null || memberAttribute.getType() == null) {
			return null;
		}
		switch (memberAttribute.getTypeName()) {
		case name:
			return getName();
		case gender:
			return getGender();
		case birth:
			return getBirth();
		case area:
			return getArea();
		case address:
			return getAddress();
		case zipCode:
			return getZipCode();
		case phone:
			return getPhone();
		case text:
		case select:
			if (memberAttribute.getPropertyIndex() != null) {
				try {
					String propertyName = ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + memberAttribute.getPropertyIndex();
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
			if (memberAttribute.getPropertyIndex() != null) {
				try {
					String propertyName = ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + memberAttribute.getPropertyIndex();
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
	 * 设置会员注册项值
	 * 
	 * @param memberAttribute
	 *            会员注册项
	 * @param memberAttributeValue
	 *            会员注册项值
	 */
	public void setAttributeValue(MemberAttribute memberAttribute, Object memberAttributeValue) {
		if (memberAttribute == null || memberAttribute.getType() == null) {
			return;
		}
		switch (memberAttribute.getTypeName()) {
		case name:
			if (memberAttributeValue instanceof String || memberAttributeValue == null) {
				setName((String) memberAttributeValue);
			}
			break;
		case gender:
			if (memberAttributeValue instanceof Member.Gender || memberAttributeValue == null) {
				Member.Gender gender = (Member.Gender) memberAttributeValue;
				setGender(gender.ordinal());
			}
			break;
		case birth:
			if (memberAttributeValue instanceof Date || memberAttributeValue == null) {
				setBirth((Date) memberAttributeValue);
			}
			break;
		case area:
			if (memberAttributeValue instanceof Area || memberAttributeValue == null) {
				setArea((Area) memberAttributeValue);
			}
			break;
		case address:
			if (memberAttributeValue instanceof String || memberAttributeValue == null) {
				setAddress((String) memberAttributeValue);
			}
			break;
		case zipCode:
			if (memberAttributeValue instanceof String || memberAttributeValue == null) {
				setZipCode((String) memberAttributeValue);
			}
			break;
		case phone:
			if (memberAttributeValue instanceof String || memberAttributeValue == null) {
				setPhone((String) memberAttributeValue);
			}
			break;
		case text:
		case select:
			if ((memberAttributeValue instanceof String || memberAttributeValue == null) && memberAttribute.getPropertyIndex() != null) {
				try {
					String propertyName = ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + memberAttribute.getPropertyIndex();
					PropertyUtils.setProperty(this, propertyName, memberAttributeValue);
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
			if ((memberAttributeValue instanceof Collection || memberAttributeValue == null) && memberAttribute.getPropertyIndex() != null) {
				try {
					String propertyName = ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + memberAttribute.getPropertyIndex();
					PropertyUtils.setProperty(this, propertyName, memberAttributeValue != null ? JsonUtils.toJson(memberAttributeValue) : null);
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
	 * 移除所有会员注册项值
	 */
	public void removeAttributeValue() {
		setName(null);
		setGender(null);
		setBirth(null);
		setArea(null);
		setAddress(null);
		setZipCode(null);
		setPhone(null);
		for (int i = 0; i < ATTRIBUTE_VALUE_PROPERTY_COUNT; i++) {
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

	/**
	 * 判断是否为新建对象
	 * 
	 * @return 是否为新建对象
	 */
	public boolean isNew() {
		return getId() == null;
	}
	
}
