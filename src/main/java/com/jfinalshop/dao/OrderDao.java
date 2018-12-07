package com.jfinalshop.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Filter;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Order;
import com.jfinalshop.model.PaymentMethod;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.Store;
import com.xiaoleilu.hutool.date.DateUtil;

/**
 * Dao - 订单
 * 
 */
public class OrderDao extends BaseDao<Order> {
	
	/**
	 * 构造方法
	 */
	public OrderDao() {
		super(Order.class);
	}

	/**
	 * 查找订单
	 * 
	 * @param type
	 *            类型
	 * @param status
	 *            状态
	 * @param store
	 *            店铺
	 * @param member
	 *            会员
	 * @param product
	 *            商品
	 * @param isPendingReceive
	 *            是否等待收款
	 * @param isPendingRefunds
	 *            是否等待退款
	 * @param isUseCouponCode
	 *            是否已使用优惠码
	 * @param isExchangePoint
	 *            是否已兑换积分
	 * @param isAllocatedStock
	 *            是否已分配库存
	 * @param hasExpired
	 *            是否已过期
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 订单
	 */
	public List<Order> findList(Order.Type type, Order.Status status, Store store, Member member, Product product, Boolean isPendingReceive, Boolean isPendingRefunds, Boolean isUseCouponCode, Boolean isExchangePoint, Boolean isAllocatedStock, Boolean hasExpired, Integer count, List<Filter> filters, List<com.jfinalshop.Order> orders) {
		String sql = "SELECT * FROM `order` t WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		if (type != null) {
			sql += " AND t.type = ?";
			params.add(type.ordinal());
		}
		if (status != null) {
			sql += " AND t.status = ?";
			params.add(status.ordinal());
		}
		if (store != null) {
			sql += " AND t.store_id = ?";
			params.add(store.getId());
		}
		if (member != null) {
			sql += " AND t.member_id = ?";
			params.add(member.getId());
		}
		if (product != null) {
			sql += " AND EXISTS (SELECT 1 FROM `order_item` i WHERE i.order_id = t.id AND i.sku_id IN (SELECT s.id FROM `sku` s WHERE s.product_id = ?)) ";
			params.add(product.getId());
		}
		if (isPendingReceive != null) {
			if (isPendingReceive) {
				sql += " AND (expire IS NULL OR expire > '" + DateUtil.now() + "') AND payment_method_type = " + PaymentMethod.Type.cashOnDelivery.ordinal();
				sql += " AND status <> " + Order.Status.completed.ordinal();
				sql += " AND status <> " + Order.Status.failed.ordinal();
				sql += " AND status <> " + Order.Status.canceled.ordinal();
				sql += " AND status <> " + Order.Status.denied.ordinal();
				sql += " AND amount_paid < amount ";
			} else {
				sql += " AND (expire IS NULL OR expire < '" + DateUtil.now() + "') AND payment_method_type != " + PaymentMethod.Type.cashOnDelivery.ordinal();
				sql += " AND status = " + Order.Status.completed.ordinal();
				sql += " AND status = " + Order.Status.failed.ordinal();
				sql += " AND status = " + Order.Status.canceled.ordinal();
				sql += " AND status = " + Order.Status.denied.ordinal();
				sql += " AND amount_paid > amount ";
			}
		}
		if (isPendingRefunds != null) {
			if (isPendingRefunds) {
				sql += " AND (expire IS NOT NULL OR expire <= '" + DateUtil.now() + "'";
				sql += " OR status = " + Order.Status.failed.ordinal();
				sql += " OR status = " + Order.Status.canceled.ordinal();
				sql += " OR status = " + Order.Status.denied.ordinal() + ")";
				sql += " AND amount_paid > " + BigDecimal.ZERO;
				sql += " AND status = " + Order.Status.completed.ordinal();
				sql += " AND amount_paid > amount ";
			} else {
				sql += " AND (expire IS NULL OR expire >= '" + DateUtil.now() + "'";
				sql += " OR status <> " + Order.Status.failed.ordinal();
				sql += " OR status <> " + Order.Status.canceled.ordinal();
				sql += " OR status <> " + Order.Status.denied.ordinal() + ")";
				sql += " AND amount_paid < " + BigDecimal.ZERO;
				sql += " AND status != " + Order.Status.completed.ordinal();
				sql += " AND amount_paid < amount ";
			}
		}
		if (isUseCouponCode != null) {
			sql += " AND t.is_use_coupon_code = ?";
			params.add(isUseCouponCode);
		}
		if (isExchangePoint != null) {
			sql += " AND t.is_exchange_point = ?";
			params.add(isExchangePoint);
		}
		if (isAllocatedStock != null) {
			sql += " AND t.is_allocated_stock = ?";
			params.add(isAllocatedStock);
		}
		if (hasExpired != null) {
			if (hasExpired) {
				sql += " AND t.expire IS NOT NULL AND t.expire <= '" + DateUtil.now() + "'";
			} else {
				sql += " AND (t.expire IS NULL OR t.expire > '" + DateUtil.now() + "')";
			}
		}
		return super.findList(sql, null, count, filters, orders, params);
	}
	
	/**
	 * 查找订单分页
	 * 
	 * @param type
	 *            类型
	 * @param status
	 *            状态
	 * @param store
	 *            店铺
	 * @param member
	 *            会员
	 * @param product
	 *            商品
	 * @param isPendingReceive
	 *            是否等待收款
	 * @param isPendingRefunds
	 *            是否等待退款
	 * @param isUseCouponCode
	 *            是否已使用优惠码
	 * @param isExchangePoint
	 *            是否已兑换积分
	 * @param isAllocatedStock
	 *            是否已分配库存
	 * @param hasExpired
	 *            是否已过期
	 * @param pageable
	 *            分页信息
	 * @return 订单分页
	 */
	public Page<Order> findPage(Order.Type type, Order.Status status, Store store, Member member, Product product, Boolean isPendingReceive, Boolean isPendingRefunds, Boolean isUseCouponCode, Boolean isExchangePoint, Boolean isAllocatedStock, Boolean hasExpired, Pageable pageable) {
		String sqlExceptSelect = "FROM `order` t WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		if (type != null) {
			sqlExceptSelect += " AND t.type = ?";
			params.add(type.ordinal());
		}
		if (status != null) {
			if (Order.Status.unfinished.equals(status)) {
				sqlExceptSelect += " AND t.status in (0, 1, 2, 3) ";
			} else if (Order.Status.allCanceled.equals(status)) {
				sqlExceptSelect += " AND t.status in (6, 7, 8, 10, 11) ";
			} else if (Order.Status.completed.equals(status)) {
				sqlExceptSelect += " AND t.status in (5, 13) ";
			} else {
				sqlExceptSelect += " AND t.status = ?";
				params.add(status.ordinal());
			}
		}
		if (store != null) {
			sqlExceptSelect += " AND t.store_id = ?";
			params.add(store.getId());
		}
		if (member != null) {
			sqlExceptSelect += " AND t.member_id = ?";
			params.add(member.getId());
		}
		if (product != null) {
			sqlExceptSelect += " AND EXISTS (SELECT 1 FROM `order_item` i WHERE i.order_id = t.id AND i.sku_id IN (SELECT s.id FROM `sku` s WHERE s.product_id = ?)) ";
			params.add(product.getId());
		}
		if (isPendingReceive != null) {
			if (isPendingReceive) {
				sqlExceptSelect += " AND (expire IS NULL OR expire > ?) AND payment_method_type = ?";
				sqlExceptSelect += " AND status <> " + Order.Status.completed.ordinal();
				sqlExceptSelect += " AND status <> " + Order.Status.failed.ordinal();
				sqlExceptSelect += " AND status <> " + Order.Status.canceled.ordinal();
				sqlExceptSelect += " AND status <> " + Order.Status.denied.ordinal();
				sqlExceptSelect += " AND amount_paid < amount ";
				params.add(DateUtil.now());
				params.add(PaymentMethod.Type.cashOnDelivery.ordinal());
			} else {
				sqlExceptSelect += " AND (expire IS NULL OR expire < ?) AND payment_method_type != ?";
				sqlExceptSelect += " AND status = " + Order.Status.completed.ordinal();
				sqlExceptSelect += " AND status = " + Order.Status.failed.ordinal();
				sqlExceptSelect += " AND status = " + Order.Status.canceled.ordinal();
				sqlExceptSelect += " AND status = " + Order.Status.denied.ordinal();
				sqlExceptSelect += " AND amount_paid > amount ";
				params.add(DateUtil.now());
				params.add(PaymentMethod.Type.cashOnDelivery.ordinal());
			}
		}
		if (isPendingRefunds != null) {
			if (isPendingRefunds) {
				sqlExceptSelect += " AND (expire IS NOT NULL OR expire <= ?";
				sqlExceptSelect += " OR status = " + Order.Status.failed.ordinal();
				sqlExceptSelect += " OR status = " + Order.Status.canceled.ordinal();
				sqlExceptSelect += " OR status = " + Order.Status.denied.ordinal() + ")";
				sqlExceptSelect += " AND amount_paid > " + BigDecimal.ZERO;
				sqlExceptSelect += " AND status = " + Order.Status.completed.ordinal();
				sqlExceptSelect += " AND amount_paid > amount ";
				params.add(DateUtil.now());
			} else {
				sqlExceptSelect += " AND (expire IS NULL OR expire >= ?";
				sqlExceptSelect += " OR status <> " + Order.Status.failed.ordinal();
				sqlExceptSelect += " OR status <> " + Order.Status.canceled.ordinal();
				sqlExceptSelect += " OR status <> " + Order.Status.denied.ordinal() + ")";
				sqlExceptSelect += " AND amount_paid < " + BigDecimal.ZERO;
				sqlExceptSelect += " AND status != " + Order.Status.completed.ordinal();
				sqlExceptSelect += " AND amount_paid < amount ";
				params.add(DateUtil.now());
			}
		}
		if (isUseCouponCode != null) {
			sqlExceptSelect += " AND t.is_use_coupon_code = ?";
			params.add(isUseCouponCode);
		}
		if (isExchangePoint != null) {
			sqlExceptSelect += " AND t.is_exchange_point = ?";
			params.add(isExchangePoint);
		}
		if (isAllocatedStock != null) {
			sqlExceptSelect += " AND t.is_allocated_stock = ?";
			params.add(isAllocatedStock);
		}
		if (hasExpired != null) {
			if (hasExpired) {
				sqlExceptSelect += " AND t.expire IS NOT NULL AND t.expire <= ?";
				params.add(DateUtil.now());
			} else {
				sqlExceptSelect += " AND (t.expire IS NULL OR t.expire > ?)";
				params.add(DateUtil.now());
			}
		}
		return super.findPage(sqlExceptSelect, pageable, params);
	}
	

	/**
	 * 线下订单查询
	 * @param type
	 * @param status
	 * @param source
	 * @param member
	 * @param pageable
	 * @param deleteFlag
	 * @return
	 */
	public Page<Order> findPage(Order.Type type, String sn, Order.Status status, Order.Source source, Member member, PaymentMethod paymentMethod, String startTime, String endTime, Pageable pageable, Boolean deleteFlag) {
		String sqlExceptSelect = "FROM `order` t WHERE EXISTS (SELECT 1 FROM `order_item` i WHERE  t.id = i.`order_id` AND i.type = 0) ";
		List<Object> params = new ArrayList<Object>();
		if (type != null) {
			sqlExceptSelect += " AND type = ?";
			params.add(type.ordinal());
		}
		if (sn != null) {
			sqlExceptSelect += " AND sn = ?";
			params.add(sn);
		}
		if (status != null) {
			sqlExceptSelect += " AND status = ?";
			params.add(status.ordinal());
		}
		if (source != null) {
			sqlExceptSelect += " AND source = ?";
			params.add(status.ordinal());
		} else {
			sqlExceptSelect += " AND source != 4";
		}
		if (member != null) {
			sqlExceptSelect += " AND member_id = ?";
			params.add(member.getId());
		}
		if (paymentMethod != null) {
			sqlExceptSelect += " AND payment_method_id = ?";
			params.add(paymentMethod.getId());
		}
		if (StrKit.notBlank(startTime)) {
			sqlExceptSelect += " AND created_date >= '" + startTime + "' ";
		}
		if (StrKit.notBlank(endTime)) {
			sqlExceptSelect += " AND created_date <= '" + endTime + "' ";
		}
		return super.findPage(sqlExceptSelect, pageable, params);
	}
	
	/**
	 * 查询订单数量
	 * 
	 * @param type
	 *            类型
	 * @param status
	 *            状态
	 * @param store
	 *            店铺
	 * @param member
	 *            会员
	 * @param product
	 *            商品
	 * @param isPendingReceive
	 *            是否等待收款
	 * @param isPendingRefunds
	 *            是否等待退款
	 * @param isUseCouponCode
	 *            是否已使用优惠码
	 * @param isExchangePoint
	 *            是否已兑换积分
	 * @param isAllocatedStock
	 *            是否已分配库存
	 * @param hasExpired
	 *            是否已过期
	 * @return 订单数量
	 */
	public Long count(Order.Type type, Order.Status status, Store store, Member member, Product product, Boolean isPendingReceive, Boolean isPendingRefunds, Boolean isUseCouponCode, Boolean isExchangePoint, Boolean isAllocatedStock, Boolean hasExpired) {
		String sql = "SELECT COUNT(1) FROM `order` t WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		
		if (type != null) {
			sql += " AND t.type = ?";
			params.add(type.ordinal());
		}
		if (status != null) {
			sql += " AND t.status = ?";
			params.add(status.ordinal());
		}
		if (store != null) {
			sql += " AND t.store_id = ?";
			params.add(store.getId());
		}
		if (member != null) {
			sql += " AND t.member_id = ?";
			params.add(member.getId());
		}
		if (product != null) {
			sql += " AND EXISTS (SELECT 1 FROM `order_item` i WHERE i.order_id = t.id AND i.sku_id IN (SELECT s.id FROM `sku` s WHERE s.product_id = ?)) ";
			params.add(product.getId());
		}
		if (isPendingReceive != null) {
			if (isPendingReceive) {
				sql += " AND (expire IS NULL OR expire > ?) AND payment_method_type = ? ";
				sql += " AND status <> " + Order.Status.completed.ordinal();
				sql += " AND status <> " + Order.Status.failed.ordinal();
				sql += " AND status <> " + Order.Status.canceled.ordinal();
				sql += " AND status <> " + Order.Status.denied.ordinal();
				sql += " AND amount_paid < amount ";
				params.add(DateUtil.now());
				params.add(PaymentMethod.Type.cashOnDelivery.ordinal());
			} else {
				sql += " AND (expire IS NULL OR expire < ?) AND payment_method_type != ? ";
				sql += " AND status = " + Order.Status.completed.ordinal();
				sql += " AND status = " + Order.Status.failed.ordinal();
				sql += " AND status = " + Order.Status.canceled.ordinal();
				sql += " AND status = " + Order.Status.denied.ordinal();
				sql += " AND amount_paid > amount ";
				params.add(DateUtil.now());
				params.add(PaymentMethod.Type.cashOnDelivery.ordinal());
			}
		}
		if (isPendingRefunds != null) {
			if (isPendingRefunds) {
				sql += " AND (expire IS NOT NULL OR expire <= ?";
				sql += " OR status = " + Order.Status.failed.ordinal();
				sql += " OR status = " + Order.Status.canceled.ordinal();
				sql += " OR status = " + Order.Status.denied.ordinal() + ")";
				sql += " AND amount_paid > " + BigDecimal.ZERO;
				sql += " AND status = " + Order.Status.completed.ordinal();
				sql += " AND amount_paid > amount ";
				params.add(DateUtil.now());
			} else {
				sql += " AND (expire IS NULL OR expire >= ?";
				sql += " OR status <> " + Order.Status.failed.ordinal();
				sql += " OR status <> " + Order.Status.canceled.ordinal();
				sql += " OR status <> " + Order.Status.denied.ordinal() + ")";
				sql += " AND amount_paid < " + BigDecimal.ZERO;
				sql += " AND status != " + Order.Status.completed.ordinal();
				sql += " AND amount_paid < amount ";
				params.add(DateUtil.now());
			}
		}
		if (isUseCouponCode != null) {
			sql += " AND t.is_use_coupon_code = ?";
			params.add(isUseCouponCode);
		}
		if (isExchangePoint != null) {
			sql += " AND t.is_exchange_point = ?";
			params.add(isExchangePoint);
		}
		if (isAllocatedStock != null) {
			sql += " AND t.is_allocated_stock = ?";
			params.add(isAllocatedStock);
		}
		if (hasExpired != null) {
			if (hasExpired) {
				sql += " AND t.expire IS NOT NULL AND t.expire <= ?";
				params.add(DateUtil.now());
			} else {
				sql += " AND (t.expire IS NULL OR t.expire > ?)";
				params.add(DateUtil.now());
			}
		}
		return super.count(sql, params);
	}
	
	/**
	 * 查询订单创建数
	 * 
	 * @param store
	 *            店铺
	 * @param beginDate
	 *            起始日期
	 * @param endDate
	 *            结束日期
	 * @return 订单创建数
	 */
	public Long createOrderCount(Store store, Date beginDate, Date endDate) {
		String sql = "SELECT COUNT(1) FROM `order` t WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		
		if (store != null) {
			sql += " AND t.store_id = ?";
			params.add(store.getId());
		}
		if (beginDate != null) {
			sql += " AND t.created_date >= ?";
			params.add(DateUtil.formatTime(beginDate));
		}
		if (endDate != null) {
			sql += " AND t.created_date <= ?";
			params.add(DateUtil.formatTime(endDate));
		}
		return super.count(sql, params);
	}

	/**
	 * 查询订单完成数
	 * 
	 * @param store
	 *            店铺
	 * @param beginDate
	 *            起始日期
	 * @param endDate
	 *            结束日期
	 * @return 订单完成数
	 */
	public Long completeOrderCount(Store store, Date beginDate, Date endDate) {
		String sql = "SELECT COUNT(1) FROM `order` t WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		
		if (store != null) {
			sql += " AND t.store_id = ?";
			params.add(store.getId());
		}
		if (beginDate != null) {
			sql += " AND t.created_date >= ?";
			params.add(DateUtil.formatTime(beginDate));
		}
		if (endDate != null) {
			sql += " AND t.created_date <= ?";
			params.add(DateUtil.formatTime(endDate));
		}
		return super.count(sql, params);
	}

	/**
	 * 查询订单创建金额
	 * 
	 * @param store
	 *            店铺
	 * @param beginDate
	 *            起始日期
	 * @param endDate
	 *            结束日期
	 * @return 订单创建金额
	 */
	public BigDecimal createOrderAmount(Store store, Date beginDate, Date endDate) {
		String sql = "SELECT SUM(amount) FROM `order` t WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		
		if (store != null) {
			sql += " AND t.store_id = ?";
			params.add(store.getId());
		}
		if (beginDate != null) {
			sql += " AND t.created_date >= ?";
			params.add(DateUtil.formatTime(beginDate));
		}
		if (endDate != null) {
			sql += " AND t.created_date <= ?";
			params.add(DateUtil.formatTime(endDate));
		}
		BigDecimal result = Db.queryBigDecimal(sql, params.toArray());
		return result != null ? result : BigDecimal.ZERO;
	}

	/**
	 * 查询订单完成金额
	 * 
	 * @param store
	 *            店铺
	 * @param beginDate
	 *            起始日期
	 * @param endDate
	 *            结束日期
	 * @return 订单完成金额
	 */
	public BigDecimal completeOrderAmount(Store store, Date beginDate, Date endDate) {
		String sql = "SELECT SUM(amount) FROM `order` t WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		
		if (store != null) {
			sql += " AND t.store_id = ?";
			params.add(store.getId());
		}
		if (beginDate != null) {
			sql += " AND t.complete_date >= ?";
			params.add(DateUtil.formatTime(beginDate));
		}
		if (endDate != null) {
			sql += " AND t.complete_date <= ?";
			params.add(DateUtil.formatTime(endDate));
		}
		BigDecimal result = Db.queryBigDecimal(sql, params.toArray());
		return result != null ? result : BigDecimal.ZERO;
	}

	/**
	 * 收银统计
	 * 
	 */
	public List<Order> cashTotal(String startTime, String endTime, Member member, Member currentUser) {
		String sql = "SELECT t.member_id , (SELECT m.name FROM member m WHERE m.`id` = t.`member_id`) AS consignee, t.payment_method_name, SUM(amount_paid)  as amount_paid FROM `order` t WHERE t.source = 4 AND t.`status` IN (1, 2, 3, 4, 5, 13) ";
		List<Object> params = new ArrayList<Object>();
		
		if (StrKit.notBlank(startTime)) {
			sql += " AND created_date >= ? ";
			params.add(startTime);
		}
		
		if (StrKit.notBlank(endTime)) {
			sql += " AND created_date <= ?";
			params.add(endTime);
		}
		sql += " AND store_id = ?";
		params.add(currentUser.getStoreId());
		
		if (member == null) {
			sql += " AND t.member_id IN(SELECT id FROM member WHERE member_rank_id = 5 ) GROUP BY member_id, t.payment_method_name "; 
		} else {
			sql += " AND t.member_id = " + member.getId() + " GROUP BY member_id, t.payment_method_name ";
		}
		List<Order> orders = modelManager.find(sql, params.toArray());
		return orders;
	}
	
	/**
	 * 货到付款
	 * 
	 */
	public List<Order> deliveryTotal(String startTime, String endTime, Member currentUser) {
		String sql = "SELECT `status` ,count(1) as quantity ,SUM(amount_paid)  as amount_paid FROM `order` t WHERE t.source in (0, 1, 2, 3) AND t.`status` IN (3, 4, 5, 13) and t.`payment_method_id` = 2 ";
		sql += " AND NOT EXISTS (SELECT 1 FROM `order_item` i WHERE t.`id` = i.`order_id` AND i.`type` != 0) ";
		
		List<Object> params = new ArrayList<Object>();
		if (StrKit.notBlank(startTime)) {
			sql += "AND created_date >= ? ";
			params.add(startTime);
		}
		if (StrKit.notBlank(endTime)) {
			sql += "AND created_date <= ?";
			params.add(endTime);
		}
		sql += " AND store_id = ?";
		params.add(currentUser.getStoreId());
		
		sql += " GROUP BY `status` ";
		return modelManager.find(sql, params.toArray());
	}
	
}