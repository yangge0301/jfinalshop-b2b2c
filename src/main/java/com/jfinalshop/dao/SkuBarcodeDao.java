package com.jfinalshop.dao;

import com.jfinalshop.model.SkuBarcode;
import com.jfinalshop.model.Store;


public class SkuBarcodeDao extends BaseDao<SkuBarcode> {

	/**
	 * 构造方法
	 */
	public SkuBarcodeDao() {
		super(SkuBarcode.class);
	}
	
	/**
	 * 根据条码查找商品
	 * 
	 * @param sn
	 *            编号(忽略大小写)
	 * @return 商品，若不存在则返回null
	 */
	public SkuBarcode findBySn(Store store, String barcode) {
		String sql = "SELECT * from sku_barcode WHERE store_id = ? AND barcode = ?";
		return modelManager.findFirst(sql, store.getId(), barcode);
	}
	
}
