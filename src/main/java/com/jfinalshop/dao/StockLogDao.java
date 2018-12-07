package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Order;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.StockLog;
import com.jfinalshop.model.Store;

/**
 * Dao - 库存记录
 * 
 */
public class StockLogDao extends BaseDao<StockLog> {
	
	/**
	 * 构造方法
	 */
	public StockLogDao() {
		super(StockLog.class);
	}

	/**
	 * 查找库存记录分页
	 * 
	 * @param store
	 *            店铺
	 * @param pageable
	 *            分页
	 * @return 库存记录分页
	 */
	public Page<StockLog> findPage(Store store, Pageable pageable) {
		String select = "SELECT sl.* ";
		String sqlExceptSelect = "FROM `stock_log` sl CROSS JOIN `sku` sku CROSS JOIN `product` product WHERE sl.sku_id = sku.id AND sku.product_id = product.id ";
		List<Object> params = new ArrayList<Object>();
		
		if (store != null) {
			sqlExceptSelect += " AND product.store_id = ?";
			params.add(store.getId());
		}
		// 搜索属性、搜索值
		String searchProperty = pageable.getSearchProperty();
		String searchValue = pageable.getSearchValue();
		if (StringUtils.isNotEmpty(searchProperty) && StringUtils.isNotEmpty(searchValue)) {
			sqlExceptSelect += " AND " + searchProperty + " LIKE ? ";
			params.add("%" + searchValue + "%");
		}
		// 解析Pageable.Order中的单个排序
		String orderProperty = com.jfinalshop.util.StringUtils.camelToUnderline(pageable.getOrderProperty());
		Order.Direction orderDirection = pageable.getOrderDirection();
		if (StringUtils.isNotEmpty(orderProperty) && orderDirection != null) {
			switch (orderDirection) {
			case asc:
				sqlExceptSelect += " ORDER BY " + orderProperty + " ASC ";
				break;
			case desc:
				sqlExceptSelect += " ORDER BY " + orderProperty + " DESC ";
				break;
			default:
				break;
			}
		}
		return modelManager.paginate(pageable.getPageNumber(), pageable.getPageSize(), select, sqlExceptSelect, params.toArray());
	}

}