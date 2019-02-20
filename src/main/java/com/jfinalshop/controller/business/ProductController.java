package com.jfinalshop.controller.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hasor.core.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.upload.UploadFile;
import com.jfinalshop.FileType;
import com.jfinalshop.Pageable;
import com.jfinalshop.Results;
import com.jfinalshop.entity.ParameterValue;
import com.jfinalshop.entity.ProductImage;
import com.jfinalshop.entity.SpecificationItem;
import com.jfinalshop.entity.SpecificationValue;
import com.jfinalshop.model.Attribute;
import com.jfinalshop.model.Brand;
import com.jfinalshop.model.Parameter;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.ProductCategory;
import com.jfinalshop.model.ProductProductTag;
import com.jfinalshop.model.ProductPromotion;
import com.jfinalshop.model.ProductStoreProductTag;
import com.jfinalshop.model.ProductTag;
import com.jfinalshop.model.Promotion;
import com.jfinalshop.model.Sku;
import com.jfinalshop.model.SkuBarcode;
import com.jfinalshop.model.Specification;
import com.jfinalshop.model.Store;
import com.jfinalshop.model.StoreProductCategory;
import com.jfinalshop.model.StoreProductTag;
import com.jfinalshop.service.AttributeService;
import com.jfinalshop.service.BrandService;
import com.jfinalshop.service.BusinessService;
import com.jfinalshop.service.FileService;
import com.jfinalshop.service.ParameterValueService;
import com.jfinalshop.service.ProductCategoryService;
import com.jfinalshop.service.ProductImageService;
import com.jfinalshop.service.ProductService;
import com.jfinalshop.service.ProductTagService;
import com.jfinalshop.service.PromotionService;
import com.jfinalshop.service.SkuService;
import com.jfinalshop.service.SpecificationItemService;
import com.jfinalshop.service.SpecificationService;
import com.jfinalshop.service.StoreProductCategoryService;
import com.jfinalshop.service.StoreProductTagService;
import com.jfinalshop.service.StoreService;
import com.jfinalshop.util.Assert;
import com.xiaoleilu.hutool.util.CollectionUtil;

/**
 * Controller - 商品
 * 
 */
@ControllerBind(controllerKey = "/business/product")
public class ProductController extends BaseController {

	@Inject
	private ProductService productService;
	@Inject
	private SkuService skuService;
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
	private StoreProductTagService storeProductTagService;
	@Inject
	private ProductImageService productImageService;
	@Inject
	private ParameterValueService parameterValueService;
	@Inject
	private SpecificationItemService specificationItemService;
	@Inject
	private AttributeService attributeService;
	@Inject
	private SpecificationService specificationService;
	@Inject
	private FileService fileService;
	@Inject
	private BusinessService businessService;

	/**
	 * 添加属性
	 */
//	public void populateModel() {
//		Long productId = getParaToLong("productId");
//		Long productCategoryId = getParaToLong("productCategoryId");
//		Store currentStore = businessService.getCurrentStore();
//		
//		Product product = productService.find(productId);
//		if (product != null && !currentStore.equals(product.getStore())) {
//			throw new UnauthorizedException();
//		}
//		ProductCategory productCategory = productCategoryService.find(productCategoryId);
//		if (productCategory != null && !storeService.productCategoryExists(currentStore, productCategory)) {
//			throw new UnauthorizedException();
//		}
//
//		setAttr("product", product);
//		setAttr("productCategory", productCategory);
//	}

	/**
	 * 检查编号是否存在
	 */
	@ActionKey("/business/product/check_sn")
	public void checkSn() {
		String sn = getPara("product.sn");
		renderJson(StringUtils.isNotEmpty(sn) && !productService.snExists(sn));
	}

	/**
	 * 上传商品图片
	 */
	@ActionKey("/business/product/upload_product_image")
	public void uploadProductImage() {
		UploadFile file = getFile();
		if (file == null || file.getFile().length() < 0) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		if (!fileService.isValid(FileType.image, file)) {
			Results.unprocessableEntity(getResponse(), "business.upload.invalid");
			return;
		}
		ProductImage productImage = productImageService.generate(file);
		if (productImage == null) {
			Results.unprocessableEntity(getResponse(), "business.upload.error");
			return;
		}
		renderJson(productImage);
	}

	/**
	 * 删除商品图片
	 */
	@ActionKey("/business/product/delete_product_image")
	public void deleteProductImage() {
		renderJson(Results.OK);
	}

	/**
	 * 获取参数
	 */
	public void parameters() {
		Long productCategoryId = getParaToLong("productCategoryId");
		ProductCategory productCategory = productCategoryService.find(productCategoryId);
		
		List<Map<String, Object>> data = new ArrayList<>();
		if (productCategory == null || CollectionUtils.isEmpty(productCategory.getParameters())) {
			renderJson(data);
			return;
		}
		for (Parameter parameter : productCategory.getParameters()) {
			Map<String, Object> item = new HashMap<>();
			item.put("group", parameter.getParameterGroup());
			item.put("names", parameter.getNamesConverter());
			data.add(item);
		}
		renderJson(data);
	}

	/**
	 * 获取属性
	 */
	public void attributes() {
		Long productCategoryId = getParaToLong("productCategoryId");
		ProductCategory productCategory = productCategoryService.find(productCategoryId);
		
		List<Map<String, Object>> data = new ArrayList<>();
		if (productCategory == null || CollectionUtils.isEmpty(productCategory.getAttributes())) {
			renderJson(data);
			return;
		}
		for (Attribute attribute : productCategory.getAttributes()) {
			Map<String, Object> item = new HashMap<>();
			item.put("id", attribute.getId());
			item.put("name", attribute.getName());
			item.put("options", attribute.getOptionsConverter());
			data.add(item);
		}
		renderJson(data);
	}

	/**
	 * 获取规格
	 */
	public void specifications() {
		Long productCategoryId = getParaToLong("productCategoryId");
		ProductCategory productCategory = productCategoryService.find(productCategoryId);
		
		List<Map<String, Object>> data = new ArrayList<>();
		if (productCategory == null || CollectionUtils.isEmpty(productCategory.getSpecifications())) {
			renderJson(data);
			return;
		}
		for (Specification specification : productCategory.getSpecifications()) {
			Map<String, Object> item = new HashMap<>();
			item.put("name", specification.getName());
			item.put("options", specification.getOptionsConverter());
			data.add(item);
		}
		renderJson(data);
	}

	/**
	 * 添加
	 */
	public void add() {
		Store currentStore = businessService.getCurrentStore();
		
		Long productCount = productService.count(null, currentStore, null, null, null, null, null, null);
		if (currentStore.getStoreRank() != null && currentStore.getStoreRank().getQuantity() != null && productCount >= currentStore.getStoreRank().getQuantity()) {
			addFlashMessage("business.product.addCountNotAllowed", currentStore.getStoreRank().getQuantity());
			redirect("list");
		}

		setAttr("types", Product.Type.values());
		setAttr("productCategoryTree", productCategoryService.findTree());
		setAttr("allowedProductCategories", productCategoryService.findList(currentStore, null, null, null));
		setAttr("allowedProductCategoryParents", getAllowedProductCategoryParents(currentStore));
		setAttr("storeProductCategoryTree", storeProductCategoryService.findTree(currentStore));
		setAttr("brands", brandService.findAll());
		setAttr("promotions", promotionService.findList(currentStore, null, true));
		setAttr("productTags", productTagService.findAll());
		setAttr("storeProductTags", storeProductTagService.findList(currentStore, null));
		setAttr("specifications", specificationService.findAll());
		render("/business/product/add.ftl");
	}

	/**
	 * 保存
	 */
	@Before(Tx.class)
	public void save() {
		Product product = getModel(Product.class);
		Sku sku = getModel(Sku.class);
		
		Long productCategoryId = getParaToLong("productCategoryId");
		ProductCategory productCategory = productCategoryService.find(productCategoryId);
		Long brandId = getParaToLong("brandId");
		Long[] promotionIds = getParaValuesToLong("promotionIds");
		Long[] productTagIds = getParaValuesToLong("productTagIds");
		Long[] storeProductTagIds = getParaValuesToLong("storeProductTagIds");
		Long storeProductCategoryId = getParaToLong("storeProductCategoryId");
		Product.Type type = getParaEnum(Product.Type.class, getPara("type"));
		// 商品条码
		List<SkuBarcode> skuBarcodes = getBeans(SkuBarcode.class, "skuBarcode");
		
		// 商品图片
		Integer productImageIndex = getIndexNum("productImages");
		if (-1 < productImageIndex) {
			List<ProductImage> productImages = new ArrayList<ProductImage>();
			for (int i = 0; i <= productImageIndex; i++) {
				ProductImage productImage = getBean(ProductImage.class, "productImages[" + i + "]");
				productImages.add(productImage);
			}
			product.setProductImages(JSONArray.toJSONString(productImages));
		}
		
		// 商品参数
		Integer parameterIndex = getIndexNum("parameterValueList");
		if (-1 < parameterIndex) {
			List<ParameterValue> parameterValues = new ArrayList<ParameterValue>();
			for (int i = 0; i <= parameterIndex; i++) {
				ParameterValue parameterValue = getBean(ParameterValue.class, "parameterValueList[" + i + "]");
				List<ParameterValue.Entry> entries = getBeans(ParameterValue.Entry.class, "parameterValues[" + i + "].entries");
				parameterValue.setEntries(entries);
				parameterValues.add(parameterValue);
			}
			product.setParameterValues(JSONArray.toJSONString(parameterValues));
		}
		
		// 商品规格
		Integer skuIndex = getIndexNum("skuList");
		List<Sku> skuList = new ArrayList<>();
		if (-1 < skuIndex) {
			for (int i = 0; i <= skuIndex; i++) {
				Sku pSku = getModel(Sku.class, "skuList[" + i + "]");
				String beanName = "skuLists[" + i + "].specificationValues";
				List<SpecificationValue> specificationValues = getBeans(SpecificationValue.class, beanName);
				pSku.setSpecificationValues(JSONArray.toJSONString(specificationValues));
				skuList.add(pSku);
			}
			
			Integer specificationItemsIndex = getIndexNum("specificationItemList");
			List<SpecificationItem> specificationItems = new ArrayList<>();
			if (-1 < specificationItemsIndex) {
				for (int i = 0; i <= specificationItemsIndex; i++) {
					SpecificationItem specificationItem = getBean(SpecificationItem.class, "specificationItemList[" + i + "]");
					List<SpecificationItem.Entry> entries = getBeans(SpecificationItem.Entry.class, "specificationItems[" + i +"].entries");
					specificationItem.setEntries(entries);
					specificationItems.add(specificationItem);
				}
				product.setSpecificationItems(JSONArray.toJSONString(specificationItems));
				product.setSpecificationItems(specificationItems);
			}
		}
		
		productImageService.filter(product.getProductImagesConverter());
		parameterValueService.filter(product.getParameterValuesConverter());
		specificationItemService.filter(product.getSpecificationItemsConverter());
		skuService.filter(skuList);

		Store currentStore = businessService.getCurrentStore();
		Long productCount = productService.count(null, currentStore, null, null, null, null, null, null);
		if (currentStore.getStoreRank() != null && currentStore.getStoreRank().getQuantity() != null && productCount >= currentStore.getStoreRank().getQuantity()) {
			setAttr("errorMessage", "产品数大于可发布商品数!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (storeProductCategoryId != null) {
			StoreProductCategory storeProductCategory = storeProductCategoryService.find(storeProductCategoryId);
			if (storeProductCategory == null || !currentStore.equals(storeProductCategory.getStore())) {
				setAttr("errorMessage", "当前店铺不属于店铺产品分类!");
				render(UNPROCESSABLE_ENTITY_VIEW);
				return;
			}
			product.setStoreProductCategoryId(storeProductCategory.getId());
		}
		if (productCategory == null) {
			setAttr("errorMessage", "产品分类为空!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		
		Brand brand = brandService.find(brandId);
		if (brand != null) {
			product.setBrandId(brand.getId());
		}
		product.setType(type.ordinal());
		product.setStoreId(currentStore.getId());
		product.setProductCategoryId(productCategory.getId());
		product.setPromotions(new ArrayList<>(promotionService.findList(promotionIds)));
		product.setProductTags(new ArrayList<>(productTagService.findList(productTagIds)));
		product.setStoreProductTags(new ArrayList<>(storeProductTagService.findList(storeProductTagIds)));

		product.removeAttributeValue();
		for (Attribute attribute : product.getProductCategory().getAttributes()) {
			String value = getPara("attribute_" + attribute.getId());
			String attributeValue = attributeService.toAttributeValue(attribute, value);
			product.setAttributeValue(attribute, attributeValue);
		}

		if (StringUtils.isNotEmpty(product.getSn()) && productService.snExists(product.getSn())) {
			setAttr("errorMessage", "编号已存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		
		if (product.hasSpecification()) {
			productService.create(product, skuList);
		} else {
			productService.create(product, sku);
			product.getDefaultSku().setSkuBarcodes(skuBarcodes);
			productService.saveBarcode(product);
		}

		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long productId = getParaToLong("productId");
		Product product = productService.find(productId);
		Store currentStore = businessService.getCurrentStore();
		
		if (product == null) {
			setAttr("errorMessage", "产品不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		setAttr("types", Product.Type.values());
		setAttr("productCategoryTree", productCategoryService.findTree());
		setAttr("allowedProductCategories", productCategoryService.findList(currentStore, null, null, null));
		setAttr("allowedProductCategoryParents", getAllowedProductCategoryParents(currentStore));
		setAttr("storeProductCategoryTree", storeProductCategoryService.findTree(currentStore));
		setAttr("brands", brandService.findAll());
		setAttr("promotions", promotionService.findList(currentStore, null, true));
		setAttr("productTags", productTagService.findAll());
		setAttr("storeProductTags", storeProductTagService.findList(currentStore, null));
		setAttr("specifications", specificationService.findAll());
		setAttr("product", product);
		render("/business/product/edit.ftl");
	}

	/**
	 * 更新
	 */
	@Before(Tx.class)
	public void update() {
		Product product = getModel(Product.class);
		Sku sku = getModel(Sku.class);
		
		Long productCategoryId = getParaToLong("productCategoryId");
		ProductCategory productCategory = productCategoryService.find(productCategoryId);
		Long brandId = getParaToLong("brandId");
		Long[] promotionIds = getParaValuesToLong("promotionIds");
		Long[] productTagIds = getParaValuesToLong("productTagIds");
		Long[] storeProductTagIds = getParaValuesToLong("storeProductTagIds");
		Long storeProductCategoryId = getParaToLong("storeProductCategoryId");
		Store currentStore = businessService.getCurrentStore();
		// 商品条码
		List<SkuBarcode> skuBarcodes = getBeans(SkuBarcode.class, "skuBarcode");
		product.getDefaultSku().setSkuBarcodes(skuBarcodes);
	
		// 商品图片
		Integer productImageIndex = getIndexNum("productImages");
		if (-1 < productImageIndex) {
			List<ProductImage> productImages = new ArrayList<ProductImage>();
			for (int i = 0; i <= productImageIndex; i++) {
				ProductImage productImage = getBean(ProductImage.class, "productImages[" + i + "]");
				productImages.add(productImage);
			}
			product.setProductImages(JSONArray.toJSONString(productImages));
		}
		
		// 商品参数
		Integer parameterIndex = getIndexNum("parameterValueList");
		if (-1 < parameterIndex) {
			List<ParameterValue> parameterValues = new ArrayList<ParameterValue>();
			for (int i = 0; i <= parameterIndex; i++) {
				ParameterValue parameterValue = getBean(ParameterValue.class, "parameterValueList[" + i + "]");
				List<ParameterValue.Entry> entries = getBeans(ParameterValue.Entry.class, "parameterValues[" + i + "].entries");
				parameterValue.setEntries(entries);
				parameterValues.add(parameterValue);
			}
			product.setParameterValues(JSONArray.toJSONString(parameterValues));
		}
		
		// 商品规格
		Integer skuIndex = getIndexNum("skuList");
		List<Sku> skuList = new ArrayList<>();
		if (-1 < skuIndex) {
			for (int i = 0; i <= skuIndex; i++) {
				Sku pSku = getModel(Sku.class, "skuList[" + i + "]");
				String beanName = "skuLists[" + i + "].specificationValues";
				List<SpecificationValue> specificationValues = getBeans(SpecificationValue.class, beanName);
				pSku.setSpecificationValues(JSONArray.toJSONString(specificationValues));
				if (pSku.getIsDefault() == null) {
					pSku.setIsDefault(false);
				}
				skuList.add(pSku);
			}
			
			Integer specificationItemsIndex = getIndexNum("specificationItemList");
			List<SpecificationItem> specificationItems = new ArrayList<>();
			if (-1 < specificationItemsIndex) {
				for (int i = 0; i <= specificationItemsIndex; i++) {
					SpecificationItem specificationItem = getBean(SpecificationItem.class, "specificationItemList[" + i + "]");
					List<SpecificationItem.Entry> entries = getBeans(SpecificationItem.Entry.class, "specificationItems[" + i +"].entries");
					specificationItem.setEntries(entries);
					specificationItems.add(specificationItem);
				}
				product.setSpecificationItems(JSONArray.toJSONString(specificationItems));
				product.setSpecificationItems(specificationItems);
			}
		}
				
		productImageService.filter(product.getProductImagesConverter());
		parameterValueService.filter(product.getParameterValuesConverter());
		specificationItemService.filter(product.getSpecificationItemsConverter());
		skuService.filter(skuList);
		
		Product pProduct = productService.find(product.getId());
		if (pProduct == null) {
			setAttr("errorMessage", "产品不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (productCategory == null) {
			setAttr("errorMessage", "产品分类不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		List<Promotion> promotions = promotionService.findList(promotionIds);
		if (CollectionUtils.isNotEmpty(promotions)) {
			if (currentStore.getPromotions() == null || !currentStore.getPromotions().containsAll(promotions)) {
				setAttr("errorMessage", "促销已在店铺促销中!");
				render(UNPROCESSABLE_ENTITY_VIEW);
				return;
			}
		}
		if (storeProductCategoryId != null) {
			StoreProductCategory storeProductCategory = storeProductCategoryService.find(storeProductCategoryId);
			if (storeProductCategory == null || !currentStore.equals(storeProductCategory.getStore())) {
				setAttr("errorMessage", "店铺分类与店铺不同!");
				render(UNPROCESSABLE_ENTITY_VIEW);
				return;
			}
			product.setStoreProductCategoryId(storeProductCategory.getId());
		}
		product.setStore(currentStore);
		product.setIsActive(true);
		product.setProductCategoryId(productCategory.getId());
		Brand brand = brandService.find(brandId);
		if (brand != null) {
			product.setBrandId(brand.getId());
		}

		product.removeAttributeValue();
		for (Attribute attribute : product.getProductCategory().getAttributes()) {
			String value = getPara("attribute_" + attribute.getId());
			String attributeValue = attributeService.toAttributeValue(attribute, value);
			product.setAttributeValue(attribute, attributeValue);
		}
		
		if (product.getTotalScore() != null && product.getScoreCount() != null && product.getScoreCount() > 0) {
			product.setScore((float) product.getTotalScore() / product.getScoreCount());
		} else {
			product.setScore(0F);
		}
		if (CollectionUtils.isNotEmpty(product.getProductImagesConverter())) {
			Collections.sort(product.getProductImagesConverter());
		}

		// 清除关连表
		productService.clear(product);
		
		// 关联保存促销
		if (CollectionUtil.isNotEmpty(promotions)) {
			for (Promotion promotion : promotions) {
				ProductPromotion productPromotion = new ProductPromotion();
				productPromotion.setProductsId(product.getId());
				productPromotion.setPromotionsId(promotion.getId());
				productPromotion.save();
			}
		}
		
		// 关联保存产品标签
		List<ProductTag> productTags = productTagService.findList(productTagIds);
		if (CollectionUtil.isNotEmpty(productTags)) {
			for (ProductTag productTag : productTags) {
				ProductProductTag productProductTag = new ProductProductTag();
				productProductTag.setProductsId(product.getId());
				productProductTag.setProductTagsId(productTag.getId());
				productProductTag.save();
			}
		}
				
		// 关联保存店铺产品标签
		List<StoreProductTag> storeProductTags = storeProductTagService.findList(storeProductTagIds);
		if (CollectionUtil.isNotEmpty(storeProductTags)) {
			for (StoreProductTag storeProductTag : storeProductTags) {
				ProductStoreProductTag productStoreProductTag = new ProductStoreProductTag();
				productStoreProductTag.setProductsId(product.getId());
				productStoreProductTag.setStoreProductTagsId(storeProductTag.getId());
				productStoreProductTag.save();
			}
		}
		
		if (product.hasSpecification()) {
			productService.modify(product, skuList);
		} else {
			productService.modify(product, sku);
			productService.saveBarcode(product);
		}
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		Product.Type type = getParaEnum(Product.Type.class, getPara("type"));
		Long productCategoryId = getParaToLong("productCategoryId");
		Long brandId = getParaToLong("brandId");
		Long promotionId = getParaToLong("promotionId");
		Long productTagId = getParaToLong("productTagId");
		Long storeProductTagId = getParaToLong("storeProductTagId"); 
		Boolean isActive = getParaToBoolean("isActive"); 
		Boolean isMarketable = getParaToBoolean("isMarketable");
		Boolean isList = getParaToBoolean("isList");
		Boolean isTop = getParaToBoolean("isTop");
		Boolean isOutOfStock = getParaToBoolean("isOutOfStock");
		Boolean isStockAlert = getParaToBoolean("isStockAlert");
		Store currentStore = businessService.getCurrentStore();
		
		ProductCategory productCategory = productCategoryService.find(productCategoryId);
		Brand brand = brandService.find(brandId);
		Promotion promotion = promotionService.find(promotionId);
		ProductTag productTag = productTagService.find(productTagId);
		StoreProductTag storeProductTag = storeProductTagService.find(storeProductTagId);
		if (promotion != null) {
			if (!currentStore.equals(promotion.getStore())) {
				setAttr("errorMessage", "当前店铺与促销店铺不同!");
				render(UNPROCESSABLE_ENTITY_VIEW);
				return;
			}
		}
		if (storeProductTag != null) {
			if (!currentStore.equals(storeProductTag.getStore())) {
				setAttr("errorMessage", "当前店铺与店铺商品标签不同!");
				render(UNPROCESSABLE_ENTITY_VIEW);
				return;
			}
		}

		setAttr("types", Product.Type.values());
		setAttr("productCategoryTree", productCategoryService.findTree());
		setAttr("allowedProductCategories", productCategoryService.findList(currentStore, null, null, null));
		setAttr("allowedProductCategoryParents", getAllowedProductCategoryParents(currentStore));
		setAttr("brands", brandService.findAll());
		setAttr("promotions", promotionService.findList(currentStore, null, true));
		setAttr("productTags", productTagService.findAll());
		setAttr("storeProductTags", storeProductTagService.findList(currentStore, true));
		setAttr("type", type);
		setAttr("productCategoryId", productCategory != null ? productCategory.getId() : null);
		setAttr("brandId", brandId);
		setAttr("promotionId", promotionId);
		setAttr("productTagId", productTagId);
		setAttr("storeProductTagId", storeProductTagId);
		setAttr("isMarketable", isMarketable);
		setAttr("isList", isList);
		setAttr("isTop", isTop);
		setAttr("isActive", isActive);
		setAttr("isOutOfStock", isOutOfStock);
		setAttr("isStockAlert", isStockAlert);
		setAttr("pageable", pageable);
		setAttr("page", productService.findPage(type, currentStore, productCategory, null, brand, promotion, productTag, storeProductTag, null, null, null, isMarketable, isList, isTop, isActive, isOutOfStock, isStockAlert, null, null, pageable));
		render("/business/product/list.ftl");
	}

	/**
	 * 删除
	 */
	@Before(Tx.class)
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		Store currentStore = businessService.getCurrentStore();
		try{
			for (Long id : ids) {
				Product product = productService.find(id);
				if (product == null) {
					Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
					return;
				}
				if (!currentStore.equals(product.getStore())) {
					Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
					return;
				}

				// 删除关连表
				List<Sku> skus = product.getSkus();
				if (CollectionUtil.isNotEmpty(skus)) {
					for (Sku sku : skus) {
						Db.deleteById("stock_log", "sku_id", sku.getId());
						Db.deleteById("sku_barcode", "sku_id", sku.getId());
					}
				}
				Db.deleteById("sku", "product_id", id);
				productService.clear(product);
				productService.delete(product.getId());
			}
			renderJson(Results.OK);
		}
		catch (Exception e){
			e.printStackTrace();
			renderJson(Results.UNPROCESSABLE_ENTITY);
		}

	}

	/**
	 * 上架商品
	 */
	@Before(Tx.class)
	public void shelves() {
		Long[] ids = getParaValuesToLong("ids");
		Store currentStore = businessService.getCurrentStore();
		
		if (ids == null) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		for (Long id : ids) {
			Product product = productService.find(id);
			if (product == null) {
				Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
				return;
			}
			if (!currentStore.equals(product.getStore())) {
				Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
				return;
			}
			if (!currentStore.getProductCategories().contains(product.getProductCategory())) {
				Results.unprocessableEntity(getResponse(), "business.product.isNotMarketable");
				return;
			}
			if (!product.getIsMarketable()) {
				product.setIsMarketable(true);
				productService.update(product);
			}
		}
		renderJson(Results.OK);
	}

	/**
	 * 下架商品
	 */
	@Before(Tx.class)
	public void shelf() {
		Long[] ids = getParaValuesToLong("ids");
		Store currentStore = businessService.getCurrentStore();
		
		if (ids == null) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		for (Long id : ids) {
			Product product = productService.find(id);
			if (product == null) {
				Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
				return;
			}
			if (!currentStore.equals(product.getStore())) {
				Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
				return;
			}
			if (product.getIsMarketable()) {
				product.setIsMarketable(false);
				productService.update(product);
			}
		}
		renderJson(Results.OK);
	}

	/**
	 * 获取允许发布商品分类上级分类
	 * 
	 * @param store
	 *            店铺
	 * @return 允许发布商品分类上级分类
	 */
	private Set<ProductCategory> getAllowedProductCategoryParents(Store store) {
		Assert.notNull(store);

		Set<ProductCategory> result = new HashSet<>();
		List<ProductCategory> allowedProductCategories = productCategoryService.findList(store, null, null, null);
		for (ProductCategory allowedProductCategory : allowedProductCategories) {
			result.addAll(allowedProductCategory.getParents());
		}
		return result;
	}

}