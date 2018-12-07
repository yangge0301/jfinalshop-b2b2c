package com.jfinalshop.model;

import com.jfinalshop.model.base.BaseStockLog;

/**
 * Model - 库存记录
 * 
 */
public class StockLog extends BaseStockLog<StockLog> {
	private static final long serialVersionUID = -9125794153277317852L;
	public static final StockLog dao = new StockLog().dao();
	
	/**
	 * 类型
	 */
	public enum Type {

		/**
		 * 入库
		 */
		stockIn,

		/**
		 * 出库
		 */
		stockOut
	}

	/**
	 * 类型名称
	 */
	public Type getTypeName() {
		return getType() != null ? Type.values()[getType()] : null;
	}
	
	/**
	 * SKU
	 */
	private Sku sku;
	
	/**
	 * 获取SKU
	 * 
	 * @return SKU
	 */
	public Sku getSku() {
		if (sku == null) {
			sku = Sku.dao.findById(getSkuId());
		}
		return sku;
	}

	/**
	 * 设置SKU
	 * 
	 * @param sku
	 *            SKU
	 */
	public void setSku(Sku sku) {
		this.sku = sku;
	}
	
	
}
