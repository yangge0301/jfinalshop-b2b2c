package com.jfinalshop.controller.shop;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.Results;
import com.jfinalshop.entity.ProductImage;
import com.jfinalshop.entity.ProductVO;
import com.jfinalshop.exception.ResourceNotFoundException;
import com.jfinalshop.interceptor.MobileInterceptor;
import com.jfinalshop.model.*;
import com.jfinalshop.service.*;
import net.hasor.core.Inject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller - 商品
 * 
 */
@ControllerBind(controllerKey = "/product")
public class ProductController extends BaseController {

	/**
	 * 最大对比商品数
	 */
	public static final Integer MAX_COMPARE_PRODUCT_COUNT = 4;

	/**
	 * 最大浏览记录商品数
	 */
	public static final Integer MAX_HISTORY_PRODUCT_COUNT = 10;

	@Inject
	private ProductService productService;
	@Inject
	private StoreService storeService;
	@Inject
	private ProductCategoryService productCategoryService;
	@Inject
	private StoreProductCategoryService storeProductCategoryService;
	@Inject
	private BrandService brandService;
	@Inject
	private PromotionService promotionService;
	@Inject
	private ProductTagService productTagService;
	@Inject
	private AttributeService attributeService;
	@Inject
	private SearchService searchService;

	/**
	 * 详情
	 */
	@Before(MobileInterceptor.class)
	public void detail() {
		Long productId = getParaToLong(0);
		Product product = productService.find(productId);
		if (product == null || BooleanUtils.isNotTrue(product.getIsActive()) || BooleanUtils.isNotTrue(product.getIsMarketable())) {
			throw new ResourceNotFoundException();
		}
		List<ProductImage> list = product.getProductImagesConverter();
		List<ProductImage> listtmp = new ArrayList<ProductImage>();
		if (list == null || list.size()==0) {
			throw new ResourceNotFoundException();
		}
		for(ProductImage p : list){
			if(p!=null&&p.getLarge()!=null&&!p.getLarge().trim().equals("")&&p.getMedium()!=null&&!p.getMedium().trim().equals("")){
				listtmp.add(p);
			}
		}
		product.setProductImages(listtmp);
		setAttr("product", product);
		render("/shop/product/detail.ftl");
	}

	/**
	 * 对比栏
	 */
	@ActionKey("/product/compare_bar")
	public void compareBar() {
		Long[] productIds = getParaValuesToLong("productIds");
		List<Map<String, Object>> data = new ArrayList<>();
		if (ArrayUtils.isEmpty(productIds) || productIds.length > MAX_COMPARE_PRODUCT_COUNT) {
			renderJson(data);
			return;
		}

		List<Product> products = productService.findList(productIds);
		for (Product product : products) {
			if (product != null && BooleanUtils.isTrue(product.getIsActive()) && BooleanUtils.isTrue(product.getIsMarketable())) {
				Map<String, Object> item = new HashMap<>();
				item.put("id", product.getId());
				item.put("name", product.getName());
				item.put("price", product.getPrice());
				item.put("marketPrice", product.getMarketPrice());
				item.put("thumbnail", product.getThumbnail());
				item.put("path", product.getPath());
				data.add(item);
			}
		}
		renderJson(data);
	}

	/**
	 * 添加对比
	 */
	@ActionKey("/product/add_compare")
	public void addCompare() {
		Long productId = getParaToLong("productId");
		Map<String, Object> data = new HashMap<>();
		Product product = productService.find(productId);
		if (product == null || BooleanUtils.isNotTrue(product.getIsActive()) || BooleanUtils.isNotTrue(product.getIsMarketable())) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}

		data.put("id", product.getId());
		data.put("name", product.getName());
		data.put("price", product.getPrice());
		data.put("marketPrice", product.getMarketPrice());
		data.put("thumbnail", product.getThumbnail());
		data.put("path", product.getPath());
		renderJson(data);
	}

	/**
	 * 浏览记录
	 */
	public void history() {
		Long[] productIds = getParaValuesToLong("productIds");
		List<Map<String, Object>> data = new ArrayList<>();
		if (ArrayUtils.isEmpty(productIds) || productIds.length > MAX_HISTORY_PRODUCT_COUNT) {
			renderJson(data);
			return;
		}

		List<Product> products = productService.findList(productIds);
		for (Product product : products) {
			if (product != null && BooleanUtils.isTrue(product.getIsActive()) && BooleanUtils.isTrue(product.getIsMarketable())) {
				Map<String, Object> item = new HashMap<>();
				item.put("name", product.getName());
				item.put("price", product.getPrice());
				item.put("thumbnail", product.getThumbnail());
				item.put("path", product.getPath());
				data.add(item);
			}
		}
		renderJson(data);
	}

	/**
	 * 列表
	 */
	@Before(MobileInterceptor.class)
	public void list() {
		Long productCategoryId = getParaToLong(0);
		Product.Type type = getParaEnum(Product.Type.class, getPara("type"));
		if (productCategoryId == null || Product.Type.exchange.equals(type)) {
			exchange();
		} else {
			general();
		}
	}
	
	/**
	 * 列表
	 */
	private void general() {
		Long productCategoryId = getParaToLong(0);
		Product.Type type = getParaEnum(Product.Type.class, getPara("type"));
		Long brandId = getParaToLong("brandId");
		Long promotionId = getParaToLong("promotionId");
		Long productTagId = getParaToLong("productTagId");
		
		String startPriceStr = getPara("startPrice");
		BigDecimal startPrice = null;
		if (StrKit.notBlank(startPriceStr)) {
			startPrice = new BigDecimal(startPriceStr);
		}
		String endPriceStr = getPara("endPrice");
		BigDecimal endPrice = null;
		if (StrKit.notBlank(endPriceStr)) {
			endPrice = new BigDecimal(endPriceStr);
		}
		
		Product.OrderType orderType = getParaEnum(Product.OrderType.class, getPara("orderType"));
		Integer pageNumber = getParaToInt("pageNumber", 1);
		Integer pageSize = getParaToInt("pageSize", 20);
		
		ProductCategory productCategory = productCategoryService.find(productCategoryId);
		if (productCategory == null) {
			throw new ResourceNotFoundException();
		}

		Brand brand = brandService.find(brandId);
		Promotion promotion = promotionService.find(promotionId);
		ProductTag productTag = productTagService.find(productTagId);
		Map<Attribute, String> attributeValueMap = new HashMap<>();
		List<Attribute> attributes = productCategory.getAttributes();
		if (CollectionUtils.isNotEmpty(attributes)) {
			for (Attribute attribute : attributes) {
				String value = getPara("attribute_" + attribute.getId());
				String attributeValue = attributeService.toAttributeValue(attribute, value);
				if (attributeValue != null) {
					attributeValueMap.put(attribute, attributeValue);
				}
			}
		}

		if (startPrice != null && endPrice != null && startPrice.compareTo(endPrice) > 0) {
			BigDecimal tempPrice = startPrice;
			startPrice = endPrice;
			endPrice = tempPrice;
		}

		Pageable pageable = new Pageable(pageNumber, pageSize);
		setAttr("orderTypes", Product.OrderType.values());
		setAttr("productCategory", productCategory);
		setAttr("type", type);
		setAttr("brand", brand);
		setAttr("promotion", promotion);
		setAttr("productTag", productTag);
		setAttr("attributeValueMap", attributeValueMap);
		setAttr("startPrice", startPrice);
		setAttr("endPrice", endPrice);
		setAttr("orderType", orderType);
		setAttr("pageNumber", pageNumber);
		setAttr("pageSize", pageSize);
		Page<Product> p = productService.findPage(type, null, productCategory, null, brand, promotion, productTag, null, attributeValueMap, startPrice, endPrice, true, true, null, true, null, null, null, orderType, pageable);
		setAttr("page", p);
		render("/shop/product/list.ftl");

	}

	/**
	 * 列表
	 */
	private void exchange() {
		Product.Type type = getParaEnum(Product.Type.class, getPara("type"));
		Long storeProductCategoryId = getParaToLong("storeProductCategoryId");
		Long brandId = getParaToLong("brandId");
		Long promotionId = getParaToLong("promotionId");
		Long productTagId = getParaToLong("productTagId");
		
		String startPriceStr = getPara("startPrice");
		BigDecimal startPrice = null;
		if (StrKit.notBlank(startPriceStr)) {
			startPrice = new BigDecimal(startPriceStr);
		}
		
		String endPriceStr = getPara("endPrice");
		BigDecimal endPrice = null;
		if (StrKit.notBlank(endPriceStr)) {
			endPrice = new BigDecimal(endPriceStr);
		}
		
		Product.OrderType orderType = getParaEnum(Product.OrderType.class, getPara("orderType"));
		Integer pageNumber = getParaToInt("pageNumber", 1);
		Integer pageSize = getParaToInt("pageSize", 20);
		
		StoreProductCategory storeProductCategory = storeProductCategoryService.find(storeProductCategoryId);
		Brand brand = brandService.find(brandId);
		Promotion promotion = promotionService.find(promotionId);
		ProductTag productTag = productTagService.find(productTagId);

		if (startPrice != null && endPrice != null && startPrice.compareTo(endPrice) > 0) {
			BigDecimal tempPrice = startPrice;
			startPrice = endPrice;
			endPrice = tempPrice;
		}

		Pageable pageable = new Pageable(pageNumber, pageSize);
		setAttr("orderTypes", Product.OrderType.values());
		setAttr("type", type);
		setAttr("storeProductCategory", storeProductCategory);
		setAttr("brand", brand);
		setAttr("promotion", promotion);
		setAttr("productTag", productTag);
		setAttr("startPrice", startPrice);
		setAttr("endPrice", endPrice);
		setAttr("orderType", orderType);
		setAttr("pageNumber", pageNumber);
		setAttr("pageSize", pageSize);
		setAttr("page", productService.findPage(type, null, null, storeProductCategory, brand, promotion, productTag, null, null, startPrice, endPrice, true, true, null, true, null, null, null, orderType, pageable));
		render("/shop/product/list.ftl");
	}

	/**
	 * 移动端列表
	 */
	@ActionKey("/product/m_list")
	public void mList() {
		Long productCategoryId = getParaToLong("productCategoryId");
		Product.Type type = getParaEnum(Product.Type.class, getPara("type"));
		Long storeProductCategoryId = getParaToLong("storeProductCategoryId");
		Long brandId = getParaToLong("brandId");
		Long promotionId = getParaToLong("promotionId");
		Long productTagId = getParaToLong("productTagId");
		String startPriceStr = getPara("startPrice");
		BigDecimal startPrice = null;
		if (StrKit.notBlank(startPriceStr)) {
			startPrice = new BigDecimal(startPriceStr);
		}
		
		String endPriceStr = getPara("endPrice");
		BigDecimal endPrice = null;
		if (StrKit.notBlank(endPriceStr)) {
			endPrice = new BigDecimal(endPriceStr);
		}
		Product.OrderType orderType = getParaEnum(Product.OrderType.class, getPara("orderType"));
		Integer pageNumber = getParaToInt("pageNumber", 1);
		Integer pageSize = getParaToInt("pageSize", 20);
		
		ProductCategory productCategory = productCategoryService.find(productCategoryId);
		StoreProductCategory storeProductCategory = storeProductCategoryService.find(storeProductCategoryId);
		Brand brand = brandService.find(brandId);
		Promotion promotion = promotionService.find(promotionId);
		ProductTag productTag = productTagService.find(productTagId);
		Map<Attribute, String> attributeValueMap = new HashMap<>();
		if (productCategory != null) {
			List<Attribute> attributes = productCategory.getAttributes();
			if (CollectionUtils.isNotEmpty(attributes)) {
				for (Attribute attribute : attributes) {
					String value = getPara("attribute_" + attribute.getId());
					String attributeValue = attributeService.toAttributeValue(attribute, value);
					if (attributeValue != null) {
						attributeValueMap.put(attribute, attributeValue);
					}
				}
			}
		}

		if (startPrice != null && endPrice != null && startPrice.compareTo(endPrice) > 0) {
			BigDecimal tempPrice = startPrice;
			startPrice = endPrice;
			endPrice = tempPrice;
		}

		Pageable pageable = new Pageable(pageNumber, pageSize);
		Page<Product> pages = productService.findPage(type, null, productCategory, storeProductCategory, brand, promotion, productTag, null, attributeValueMap, startPrice, endPrice, true, true, null, true, null, null, null, orderType, pageable);
		
		List<Product> products = new ArrayList<Product>();
		if (CollectionUtils.isNotEmpty(pages.getList())) {
			for (Product product : pages.getList()) {
				product.put("type", product.getTypeName());
				product.put("thumbnail", product.getThumbnail());
				Store store = product.getStore();
				store.put("type", store.getTypeName());
				product.put("store", store);
				product.put("defaultSku", product.getDefaultSku());
				product.put("path", product.getPath());
				products.add(product);
			}
		}
		renderJson(products);
	}

	/**
	 * 搜索
	 */
	@Before(MobileInterceptor.class)
	public void search() {
		String keyword = getPara("keyword");
		Long storeId = getParaToLong("storeId");
		Product.OrderType orderType = getParaEnum(Product.OrderType.class, getPara("orderType"));
		Pageable pageable = getBean(Pageable.class);
		
		String pStartPrice = getPara("startPrice");
		BigDecimal startPrice = null;
		if (StringUtils.isNotEmpty(pStartPrice)) {
			startPrice = new BigDecimal(pStartPrice);
		}
		String pEndPrice = getPara("endPrice");
		BigDecimal endPrice = null;
		if (StringUtils.isNotEmpty(pEndPrice)) {
			endPrice = new BigDecimal(pEndPrice);
		}
		
		if (StringUtils.isEmpty(keyword)) {
			setAttr("errorMessage", "搜索关键字不能为空!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		if (startPrice != null && endPrice != null && startPrice.compareTo(endPrice) > 0) {
			BigDecimal tempPrice = startPrice;
			startPrice = endPrice;
			endPrice = tempPrice;
		}
		Store store = storeService.find(storeId);

		setAttr("pageable", pageable);
		setAttr("orderTypes", Product.OrderType.values());
		setAttr("productKeyword", keyword);
		setAttr("store", store);
		setAttr("startPrice", startPrice);
		setAttr("endPrice", endPrice);
		setAttr("orderType", orderType);
		setAttr("page", searchService.search(keyword, store, startPrice, endPrice, orderType, pageable));
		//setAttr("stores", searchService.searchStore(keyword));
		render("/shop/product/search.ftl");
	}

	/**
	 * 搜索
	 */
	@ActionKey("/product/m_search")
	public void mSearch() {
		String keyword = getPara("keyword");
		Long storeId = getParaToLong("storeId");
		Product.OrderType orderType = getParaEnum(Product.OrderType.class, getPara("orderType"));
		
		String pStartPrice = getPara("startPrice");
		BigDecimal startPrice = null;
		if (StringUtils.isNotEmpty(pStartPrice)) {
			startPrice = new BigDecimal(pStartPrice);
		}
		String pEndPrice = getPara("endPrice");
		BigDecimal endPrice = null;
		if (StringUtils.isNotEmpty(pEndPrice)) {
			endPrice = new BigDecimal(pEndPrice);
		}
		Integer pageNumber = getParaToInt("pageNumber", 1);
		Integer pageSize = getParaToInt("pageSize", 20);
		
		if (StringUtils.isEmpty(keyword)) {
			return;
		}

		if (startPrice != null && endPrice != null && startPrice.compareTo(endPrice) > 0) {
			BigDecimal tempPrice = startPrice;
			startPrice = endPrice;
			endPrice = tempPrice;
		}
		Store store = storeService.find(storeId);

		Pageable pageable = new Pageable(pageNumber, pageSize);
		com.ld.zxw.page.Page<ProductVO> pages = searchService.search(keyword, store, startPrice, endPrice, orderType, pageable);
		
		List<Product> products = new ArrayList<Product>();
		if (CollectionUtils.isNotEmpty(pages.getList())) {
			for (ProductVO productVO : pages.getList()) {
				Product product = productService.find(productVO.getId());
				product.put("type", product.getTypeName());
				product.put("thumbnail", product.getThumbnail());
				Store pStore = product.getStore();
				pStore.put("type", pStore.getTypeName());
				product.put("store", pStore);
				product.put("defaultSku", product.getDefaultSku());
				product.put("path", product.getPath());
				products.add(product);
			}
		}
		renderJson(products);
	}

	/**
	 * 对比
	 */
	public void compare() {
		Long[] productIds = getParaValuesToLong("productIds");
		if (ArrayUtils.isEmpty(productIds) || productIds.length > MAX_COMPARE_PRODUCT_COUNT) {
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		List<Product> products = productService.findList(productIds);
		CollectionUtils.filter(products, new Predicate() {
			@Override
			public boolean evaluate(Object obj) {
				Product product = (Product) obj;
				return BooleanUtils.isTrue(product.getIsActive()) && BooleanUtils.isTrue(product.getIsMarketable());
			}
		});
		if (CollectionUtils.isEmpty(products)) {
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		setAttr("products", products);
		render("/shop/product/compare.ftl");
	}

	/**
	 * 点击数
	 */
	public void hits() {
		Long productId = getParaToLong(0);
		if (productId == null) {
			renderJson(0L);
			return;
		}
		renderJson(productService.viewHits(productId));
	}

}