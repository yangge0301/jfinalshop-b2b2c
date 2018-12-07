package com.jfinalshop.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.jfinal.kit.StrKit;
import com.jfinalshop.Pageable;
import com.jfinalshop.entity.ArticleVO;
import com.jfinalshop.entity.ProductVO;
import com.jfinalshop.entity.StoreVO;
import com.jfinalshop.model.Article;
import com.jfinalshop.model.Brand;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.Store;
import com.ld.zxw.page.Page;
import com.ld.zxw.service.LuceneService;
import com.ld.zxw.service.LuceneServiceImpl;
import com.ld.zxw.util.DateUtil;
import com.xiaoleilu.hutool.util.CollectionUtil;

/**
 * Service - 搜索
 *
 */
@Singleton
public class SearchService {

	private LuceneService luceneProductService = new LuceneServiceImpl("product");
	private LuceneService luceneStoreService = new LuceneServiceImpl("store");
	private LuceneService luceneArticleService = new LuceneServiceImpl("article");

	@Inject
	private ProductService productService;


	/**
	 * 删除索引
	 *
	 * @param type
	 *            索引类型
	 */
	public void delProductAll() {
		try {
			luceneProductService.delAll();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除索引
	 *
	 * @param type
	 *            索引类型
	 */
	public void delStoreAll() {
		try {
			luceneStoreService.delAll();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除索引
	 *
	 * @param type
	 *            索引类型
	 */
	public void delArticleAll() {
		try {
			luceneArticleService.delAll();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * 创建索引
	 *
	 * @param article
	 *            文章
	 */
	public int indexArticle(List<Article> articleList) {
		int generateCount = 0;
		long time = DateUtil.getTime();
		try {
			if (CollectionUtil.isNotEmpty(articleList)) {
				List<ArticleVO> pArticleList = new ArrayList<>();
				for (Article article : articleList) {
					ArticleVO articleVO = copyProperty(article);
					pArticleList.add(articleVO);
					generateCount++;
				}
				luceneArticleService.saveObj(pArticleList);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		DateUtil.timeConsuming("添加索引时间", time);
		return generateCount;
	}

	/**
	 * 创建索引
	 *
	 * @param article
	 *            商品
	 */
	public int indexProduct(List<Product> productList) {
		int generateCount = 0;
		long time = DateUtil.getTime();
		try {
			if (CollectionUtil.isNotEmpty(productList)) {
				List<ProductVO> pProductList = new ArrayList<>();
				for (Product product : productList) {
					ProductVO productVO = copyProperty(product);
					pProductList.add(productVO);
					generateCount++;
				}
				luceneProductService.saveObj(pProductList);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		DateUtil.timeConsuming("添加索引时间", time);
		return generateCount;
	}


	/**
	 * 创建索引
	 *
	 * @param article
	 *            文章
	 */
	public int indexStore(List<Store> storeList) {
		int generateCount = 0;
		try {
			if (CollectionUtil.isNotEmpty(storeList)) {
				List<StoreVO> pStoreList = new ArrayList<>();
				for (Store store : storeList) {
					StoreVO storeVO = copyProperty(store);
					pStoreList.add(storeVO);
					generateCount++;
				}
				luceneStoreService.saveObj(pStoreList);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return generateCount;
	}

	/**
	 * 搜索文章分页
	 *
	 * @param keyword
	 *            关键词
	 * @param pageable
	 *            分页信息
	 * @return 文章分页
	 */
	public Page<ArticleVO> search(String keyword, Pageable pageable) {
		if (StringUtils.isEmpty(keyword)) {
			return new Page<>();
		}
		if (pageable == null) {
			pageable = new Pageable();
		}
		// 在多个字段中查询相同的内容
		Page<ArticleVO> articles = null;
		try {
			String[] fields = {"title", "content"};
			Map<String, Float> boosts = new HashMap<>();
			boosts.put("title", 1.5F); //权重
			Query keywordQuery = new MultiFieldQueryParser(fields, new IKAnalyzer(), boosts).parse(keyword);
			Sort sort = new Sort(new SortField("isTop", SortField.Type.STRING, true), new SortField(null, SortField.Type.SCORE), new SortField("createdDate", SortField.Type.LONG, true));
			articles = luceneArticleService.findList(keywordQuery, pageable.getPageNumber(), pageable.getPageSize(), ArticleVO.class, sort);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidTokenOffsetsException e) {
			e.printStackTrace();
		}
		return articles;
	}

	/**
	 * 搜索商品分页
	 *
	 * @param keyword
	 *            关键词
	 * @param store
	 *            店铺
	 * @param startPrice
	 *            最低价格
	 * @param endPrice
	 *            最高价格
	 * @param orderType
	 *            排序类型
	 * @param pageable
	 *            分页信息
	 * @return 商品分页
	 */
	public Page<ProductVO> search(String keyword, Store store, BigDecimal startPrice, BigDecimal endPrice, Product.OrderType orderType, Pageable pageable) {
		if (StringUtils.isEmpty(keyword)) {
			return new Page<>();
		}

		if (pageable == null) {
			pageable = new Pageable();
		}

		// 在多个字段中查询相同的内容
		Page<ProductVO> productDtos = null;
		try {
			Builder builder = new BooleanQuery.Builder();
			Map<String, Float> boosts = new HashMap<>();

			String[] fields = {"name", "caption", "keyword", "brand"};
			boosts.put("keyword", 1.5F); //权重

			BooleanClause keywordClause = null;
			if(StrKit.notBlank(keyword)) {
				Query keywordQuery = new MultiFieldQueryParser(fields, new IKAnalyzer(), boosts).parse(keyword);
				keywordClause = new BooleanClause(keywordQuery, Occur.MUST);
				builder.add(keywordClause);
			}
			BooleanQuery query = builder.build();

			// 排序
			SortField[] sortFields =  new SortField[] { new SortField("name", SortField.Type.SCORE, true) };
			productDtos = luceneProductService.findList(query, pageable.getPageNumber(), pageable.getPageSize(), ProductVO.class, new Sort(sortFields));

		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidTokenOffsetsException e) {
			e.printStackTrace();
		}
		return productDtos;
	}

	/**
	 * 搜索店铺集合
	 *
	 * @param keyword
	 *            关键词
	 * @return 店铺集合
	 */
	public List<StoreVO> searchStore(String keyword) {
		if (StringUtils.isEmpty(keyword)) {
			return new ArrayList<>();
		}

		List<StoreVO> stores = null;
		try {
			String[] fields = {"name", "keyword", "status"};
			Map<String, Float> boosts = new HashMap<>();
			boosts.put("name", 1.5F); //权重
			Query keywordQuery = new MultiFieldQueryParser(fields, new IKAnalyzer(), boosts).parse(keyword);
			// 排序
			Sort sort = new Sort(new SortField(null, SortField.Type.SCORE), new SortField("createdDate", SortField.Type.LONG, true));
			stores = luceneStoreService.findList(keywordQuery, StoreVO.class, Integer.MAX_VALUE, sort);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException | InvalidTokenOffsetsException e) {
			e.printStackTrace();
		}
		return stores;
	}

	/**
	 * 商品属性复制
	 *
	 * @return 商品
	 */
	private ProductVO copyProperty(Product product) {
		ProductVO productVO = new ProductVO();
		productVO.setId(product.getId());
		productVO.setCreatedDate(product.getCreatedDate());
		productVO.setSn(product.getSn());
		productVO.setName(product.getName());
		productVO.setCaption(product.getCaption());
		productVO.setType(product.getType());
		productVO.setPrice(product.getPrice() != null ? product.getPrice().doubleValue() : null);
		productVO.setCost(product.getCost() != null ? product.getCost().doubleValue() : null);
		productVO.setMarketPrice(product.getMarketPrice() != null ? product.getMarketPrice().doubleValue() : null);
		productVO.setImage(product.getImage());
		productVO.setUnit(product.getUnit());
		productVO.setWeight(product.getWeight());
		productVO.setIsMarketable(product.getIsMarketable());
		productVO.setIsList(product.getIsList());
		productVO.setIsTop(product.getIsTop());
		productVO.setIsDelivery(product.getIsDelivery());
		productVO.setIsActive(product.getIsActive());
		productVO.setMemo(product.getMemo());
		productVO.setKeyword(product.getKeyword());
		productVO.setScore(product.getScore());
		productVO.setTotalScore(product.getTotalScore());
		productVO.setScoreCount(product.getScoreCount());
		productVO.setWeekHits(product.getWeekHits());
		productVO.setMonthHits(product.getMonthHits());
		productVO.setHits(product.getHits());
		productVO.setWeekSales(product.getWeekSales());
		productVO.setMonthSales(product.getMonthSales());
		productVO.setSales(product.getSales());
		productVO.setWeekHitsDate(product.getWeekHitsDate());
		productVO.setMonthHitsDate(product.getMonthHitsDate());
		productVO.setWeekSalesDate(product.getWeekSalesDate());
		productVO.setMonthSalesDate(product.getMonthSalesDate());
		productVO.setStoreId(product.getStoreId());
		productVO.setProductCategoryId(product.getProductCategoryId());
		productVO.setStoreProductCategoryId(product.getStoreProductCategoryId());
		productVO.setBrandId(product.getBrandId());
		Brand brand = product.getBrand();
		productVO.setBrand(brand == null ? null : brand.getName());
		productVO.setProductImages(product.getProductImages());
		return productVO;
	}

	/**
	 * 店铺属性复制
	 *
	 * @return 店铺
	 */
	private StoreVO copyProperty(Store store) {
		StoreVO storeVO = new StoreVO();
		storeVO.setId(store.getId());
		storeVO.setCreatedDate(store.getCreatedDate());
		storeVO.setName(store.getName());
		storeVO.setType(store.getType());
		storeVO.setStatus(store.getStatus());
		storeVO.setLogo(store.getLogo());
		storeVO.setEmail(store.getEmail());
		storeVO.setMobile(store.getMobile());
		storeVO.setPhone(store.getPhone());
		storeVO.setAddress(store.getAddress());
		storeVO.setZipCode(store.getZipCode());
		storeVO.setIntroduction(store.getIntroduction());
		storeVO.setKeyword(store.getKeyword());
		storeVO.setEndDate(store.getEndDate());
		storeVO.setDiscountPromotionEndDate(store.getDiscountPromotionEndDate());
		storeVO.setFullReductionPromotionEndDate(store.getFullReductionPromotionEndDate());
		storeVO.setIsEnabled(store.getIsEnabled());
		storeVO.setBailPaid(store.getBailPaid());
		storeVO.setStoreRankId(store.getStoreRankId());
		return storeVO;
	}

	/**
	 * 文章属性复制
	 *
	 * @return 文章
	 */
	private ArticleVO copyProperty(Article article) {
		ArticleVO articleVO = new ArticleVO();
		articleVO.setId(article.getId());
		articleVO.setCreatedDate(article.getCreatedDate());
		articleVO.setTitle(article.getTitle());
		articleVO.setAuthor(article.getAuthor());
		articleVO.setContent(article.getContent());
		articleVO.setSeoTitle(article.getSeoTitle());
		articleVO.setSeoKeywords(article.getSeoKeywords());
		articleVO.setSeoDescription(article.getSeoDescription());
		articleVO.setIsPublication(article.getIsPublication());
		articleVO.setIsTop(article.getIsTop());
		articleVO.setHits(article.getHits());
		return articleVO;
	}

}