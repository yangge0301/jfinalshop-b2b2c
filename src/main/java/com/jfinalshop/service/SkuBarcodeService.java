package com.jfinalshop.service;

import net.hasor.core.Inject;

import com.jfinalshop.dao.SkuBarcodeDao;
import com.jfinalshop.model.SkuBarcode;
import com.jfinalshop.model.Store;

public class SkuBarcodeService extends BaseService<SkuBarcode> {

	/**
	 * 构造方法
	 */
	public SkuBarcodeService() {
		super(SkuBarcode.class);
	}
	
	@Inject
	private SkuBarcodeDao skuBarcodeDao;
	
	/**
	 * 根据条码查找商品
	 * 
	 * @param sn
	 *            编号(忽略大小写)
	 * @return 商品，若不存在则返回null
	 */
	public SkuBarcode findBySn(Store store, String barcode) {
		return skuBarcodeDao.findBySn(store, barcode);
	}
	
}
