package com.jfinalshop.api.controller.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.hasor.core.Inject;

import org.apache.commons.collections.CollectionUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinalshop.Filter;
import com.jfinalshop.api.common.bean.DatumResponse;
import com.jfinalshop.api.controller.BaseAPIController;
import com.jfinalshop.api.interceptor.AccessInterceptor;
import com.jfinalshop.entity.ProductImage;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.ProductCategory;
import com.jfinalshop.model.Review;
import com.jfinalshop.model.Sku;
import com.jfinalshop.service.ProductService;
import com.jfinalshop.service.ReviewService;
import com.jfinalshop.service.SkuService;

/**
 * 移动API - 货品
 * 
 */
@ControllerBind(controllerKey = "/api/product")
@Before(AccessInterceptor.class)
public class ProductAPIController extends BaseAPIController {
	
	@Inject
	private ProductService productService;
	@Inject
	private SkuService skuService;
	@Inject
	private ReviewService reviewService;
	
	private static final Integer count = 6;
	
	/**
	 * 详情
	 */
	public void detail() {
		Long skuId = getParaToLong("skuId");
		Sku sku = skuService.find(skuId);
		
		if(sku == null) {
			renderArgumentError("商品没有找到!");
			return;
		}
		
		Product product = sku.getProduct();
		
		// 增加点击数
		//productService.addHits(product, 1L);
		CacheKit.put(Product.HITS_CACHE_NAME, product.getId(), 1);
		
		ProductCategory productCategory = product.getProductCategory();
		// 商品多张图片
		List<ProductImage> productImages = product.getProductImagesConverter();
		List<String> productImageList = new ArrayList<String>();
		if (CollectionUtils.isNotEmpty(productImages)) {
			for (ProductImage productImage : productImages) {
				productImageList.add(productImage.getLarge());
			}
		}
		// 保留副标题与详情页
		String caption = product.getCaption();
		String introduction = product.getIntroduction();
		
		// 转换成前端需要的格式
		List<Product> productDetail = new ArrayList<Product>();
		productDetail.add(product);
		convertProduct(productDetail);

		// 添加图片
		product.put("productImages", productImageList);
		// 加上副标题与详情页
		product.setCaption(caption);
		product.setIntroduction(introduction);
	
		// 猜你喜欢，如果商品相关分类不够6个就增加热销商品，去掉本身。
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(Filter.eq("product_category_id", productCategory.getId()));
		filters.add(Filter.ne("id", product.getId()));
		filters.add(Filter.eq("is_marketable", true));
		List<Product> extra = productService.findList(0, count, filters, null);
		if (extra.size() < count) {
			List<Filter> pFilters = new ArrayList<Filter>();
			pFilters.add(Filter.ne("id", product.getId()));
			pFilters.add(Filter.eq("is_marketable", true));
			pFilters.add(Filter.eq("is_top", true));
			List<Product> products = productService.findList(0, count, pFilters, null);
			List<Product> pExtra = new ArrayList<Product>();
			if (CollectionUtils.isNotEmpty(products)) {
				for (int i = 0; i < count - extra.size(); i++) {
					pExtra.add(products.get(i));
				}
			}
			extra.addAll(pExtra);
		}
		
		// 增加评价
		List<Review> reviews = reviewService.findList(null, product.getId(), null, true, count, null, null, false);
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("product", product);
		data.put("extra", convertProduct(extra));
		data.put("reviews", convertReview(reviews));
		renderJson(new DatumResponse(data));
	}
	
	
}
