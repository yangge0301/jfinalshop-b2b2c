package com.jfinalshop.template.directive;

import java.io.IOException;
import java.util.Map;

import com.jfinalshop.model.Product;
import com.jfinalshop.model.Store;
import com.jfinalshop.service.ProductService;
import com.jfinalshop.service.StoreService;
import com.jfinalshop.util.FreeMarkerUtils;
import com.jfinalshop.util.HasorUtils;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 模板指令 - 商品数量
 * 
 */
public class ProductCountDirective extends BaseDirective {

	/**
	 * "商品类型"参数名称
	 */
	private static final String TYPE_PARAMETER_NAME = "type";

	/**
	 * "店铺ID"参数名称
	 */
	private static final String STORE_ID_PARAMETER_NAME = "storeId";

	/**
	 */
	private static final String PRODUCT_ISMARKETABLE_PARAMETER_NAME = "isMarketable";

	/**
	 * "商品是否列出"参数名称
	 */
	private static final String PRODUCT_ISLIST_PARAMETER_NAME = "isList";

	/**
	 * "商品是否置顶"参数名称
	 */
	private static final String PRODUCT_ISTOP_PARAMETER_NAME = "isTop";

	/**
	 * "商品是否有效"参数名称
	 */
	private static final String PRODUCT_ISACTIVE_PARAMETER_NAME = "isActive";

	/**
	 * "商品是否缺货"参数名称
	 */
	private static final String PRODUCT_ISOUTOFSTOCK_PARAMETER_NAME = "isOutOfStock";

	/**
	 * "商品是否库存警告"参数名称
	 */
	private static final String PRODUCT_ISSTOCKALERT_PARAMETER_NAME = "isStockAlert";

	/**
	 * 变量名称
	 */
	private static final String VARIABLE_NAME = "count";

	private ProductService productService = HasorUtils.getBean(ProductService.class);
	private StoreService storeService = HasorUtils.getBean(StoreService.class);

	/**
	 * 执行
	 * 
	 * @param env
	 *            环境变量
	 * @param params
	 *            参数
	 * @param loopVars
	 *            循环变量
	 * @param body
	 *            模板内容
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
		Product.Type type = FreeMarkerUtils.getParameter(TYPE_PARAMETER_NAME, Product.Type.class, params);
		Long storeId = FreeMarkerUtils.getParameter(STORE_ID_PARAMETER_NAME, Long.class, params);
		Boolean isMarketable = FreeMarkerUtils.getParameter(PRODUCT_ISMARKETABLE_PARAMETER_NAME, Boolean.class, params);
		Boolean isList = FreeMarkerUtils.getParameter(PRODUCT_ISLIST_PARAMETER_NAME, Boolean.class, params);
		Boolean isTop = FreeMarkerUtils.getParameter(PRODUCT_ISTOP_PARAMETER_NAME, Boolean.class, params);
		Boolean isActive = FreeMarkerUtils.getParameter(PRODUCT_ISACTIVE_PARAMETER_NAME, Boolean.class, params);
		Boolean isOutOfStock = FreeMarkerUtils.getParameter(PRODUCT_ISOUTOFSTOCK_PARAMETER_NAME, Boolean.class, params);
		Boolean isStockAlert = FreeMarkerUtils.getParameter(PRODUCT_ISSTOCKALERT_PARAMETER_NAME, Boolean.class, params);

		Store store = storeService.find(storeId);
		Long count = productService.count(type, store, isMarketable, isList, isTop, isActive, isOutOfStock, isStockAlert);
		setLocalVariable(VARIABLE_NAME, count, env, body);
	}

}