package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.ProductNotify;
import com.jfinalshop.model.Sku;
import com.jfinalshop.model.Store;

/**
 * Dao - 到货通知
 * 
 */
public class ProductNotifyDao extends BaseDao<ProductNotify> {

	/**
	 * 构造方法
	 */
	public ProductNotifyDao() {
		super(ProductNotify.class);
	}
	
	/**
	 * 判断到货通知是否存在
	 * 
	 * @param sku
	 *            SKU
	 * @param email
	 *            E-mail(忽略大小写)
	 * @return 到货通知是否存在
	 */
	public boolean exists(Sku sku, String email) {
		if (sku == null || StringUtils.isEmpty(email)) {
			return false;
		}
		String sql = "SELECT COUNT(1) FROM product_notify WHERE sku_id = ? AND email = LOWER(?) AND has_sent = FALSE";
		Long count = Db.queryLong(sql, sku.getId(), email);
		return count > 0;
	}

	/**
	 * 查找到货通知分页
	 * 
	 * @param store
	 *            店铺
	 * @param member
	 *            会员
	 * @param isMarketable
	 *            是否上架
	 * @param isOutOfStock
	 *            SKU是否缺货
	 * @param hasSent
	 *            是否已发送.
	 * @param pageable
	 *            分页信息
	 * @return 到货通知分页
	 */
	public Page<ProductNotify> findPage(Store store, Member member, Boolean isMarketable, Boolean isOutOfStock, Boolean hasSent, Pageable pageable) {
		String select = "SELECT pn.* ";
		String sqlExceptSelect = "FROM `product_notify` pn CROSS JOIN sku s CROSS JOIN product p WHERE pn.sku_id = s.id AND s.product_id = p.id ";
		List<Object> params = new ArrayList<Object>();
		
		if (store != null) {
			sqlExceptSelect += " AND p.store_id = ?";
			params.add(store.getId());
		}
		if (member != null) {
			sqlExceptSelect += " AND pn.member_id = ?";
			params.add(member.getId());
		}
		if (isMarketable != null) {
			sqlExceptSelect += " AND p.is_marketable = ?";
			params.add(isMarketable);
		}
		if (isOutOfStock != null) {
			if (isOutOfStock) {
				sqlExceptSelect += " AND (s.stock IS NOT NULL) AND s.stock <= s.allocated_stock ";
			} else {
				sqlExceptSelect += " AND (s.stock IS NULL OR s.stock > s.allocated_stock)";
			}
		}
		if (hasSent != null) {
			sqlExceptSelect += " AND pn.hasSent = " + hasSent;
			params.add(hasSent);
		}
		// 搜索属性、搜索值
		String searchProperty = pageable.getSearchProperty();
		String searchValue = pageable.getSearchValue();
		if (StringUtils.isNotEmpty(searchProperty) && StringUtils.isNotEmpty(searchValue)) {
			sqlExceptSelect += " AND " + searchProperty + " LIKE ? ";
			params.add("%" + searchValue + "%");
		}
		return modelManager.paginate(pageable.getPageNumber(), pageable.getPageSize(), select, sqlExceptSelect, params.toArray());
	}

	/**
	 * 查找到货通知数量
	 * 
	 * @param member
	 *            会员
	 * @param isMarketable
	 *            是否上架
	 * @param isOutOfStock
	 *            SKU是否缺货
	 * @param hasSent
	 *            是否已发送.
	 * @return 到货通知数量
	 */
	public Long count(Member member, Boolean isMarketable, Boolean isOutOfStock, Boolean hasSent) {
		String sql = "SELECT COUNT(1) FROM `product_notify` pn CROSS JOIN sku s CROSS JOIN product p WHERE pn.sku_id = s.id AND s.product_id = p.id ";
		List<Object> params = new ArrayList<Object>();
		
		if (member != null) {
			sql += " AND pn.member_id = ?";
			params.add(member.getId());
		}
		if (isMarketable != null) {
			sql += " AND p.is_marketable = ?";
			params.add(isMarketable);
		}
		if (isOutOfStock != null) {
			if (isOutOfStock) {
				sql += " AND (s.stock IS NOT NULL) AND s.stock <= s.allocated_stock ";
			} else {
				sql += " AND (s.stock IS NULL OR s.stock > s.allocated_stock) ";
			}
		}
		if (hasSent != null) {
			sql += " AND pn.hasSent = ?";
			params.add(hasSent);
		}
		return super.count(sql, params);
	}

}