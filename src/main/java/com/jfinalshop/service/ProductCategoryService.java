package com.jfinalshop.service;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import org.apache.commons.collections.CollectionUtils;

import com.alibaba.fastjson.JSONArray;
import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.dao.ProductCategoryDao;
import com.jfinalshop.dao.ProductDao;
import com.jfinalshop.model.Brand;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.ProductCategory;
import com.jfinalshop.model.Promotion;
import com.jfinalshop.model.Sku;
import com.jfinalshop.model.Store;
import com.jfinalshop.util.Assert;

/**
 * Service - 商品分类
 * 
 */
@Singleton
public class ProductCategoryService extends BaseService<ProductCategory> {

	/**
	 * 构造方法
	 */
	public ProductCategoryService() {
		super(ProductCategory.class);
	}
	
	@Inject
	private ProductCategoryDao productCategoryDao;
	@Inject
	private ProductDao productDao;
	
	
	/**
	 * 查找商品分类
	 * 
	 * @param store
	 *            店铺
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 商品分类
	 */
	public List<ProductCategory> findList(Store store, Integer count, List<Filter> filters, List<Order> orders) {
		return productCategoryDao.findList(store, count, filters, orders);
	}

	/**
	 * 查找顶级商品分类
	 * 
	 * @return 顶级商品分类
	 */
	public List<ProductCategory> findRoots() {
		return productCategoryDao.findRoots(null);
	}

	/**
	 * 查找顶级商品分类
	 * 
	 * @param count
	 *            数量
	 * @return 顶级商品分类
	 */
	public List<ProductCategory> findRoots(Integer count) {
		return productCategoryDao.findRoots(count);
	}

	/**
	 * 查找顶级商品分类
	 * 
	 * @param count
	 *            数量
	 * @param useCache
	 *            是否使用缓存
	 * @return 顶级商品分类
	 */
	public List<ProductCategory> findRoots(Integer count, boolean useCache) {
		return productCategoryDao.findRoots(count);
	}
	
	/**
	 * 查找顶级商品分类
	 * 
	 * @param count
	 *            数量
	 * @param useCache
	 *            是否使用缓存
	 * @return 顶级商品分类
	 */
	public List<ProductCategory> findRoots(Boolean isMarketable, Boolean isTop) {
		return productCategoryDao.findRoots(isMarketable, isTop);
	}

	/**
	 * 查找上级商品分类
	 * 
	 * @param productCategory
	 *            商品分类
	 * @param recursive
	 *            是否递归
	 * @param count
	 *            数量
	 * @return 上级商品分类
	 */
	public List<ProductCategory> findParents(ProductCategory productCategory, boolean recursive, Integer count) {
		return productCategoryDao.findParents(productCategory, recursive, count);
	}

	/**
	 * 查找上级商品分类
	 * 
	 * @param productCategoryId
	 *            商品分类ID
	 * @param recursive
	 *            是否递归
	 * @param count
	 *            数量
	 * @param useCache
	 *            是否使用缓存
	 * @return 上级商品分类
	 */
	public List<ProductCategory> findParents(Long productCategoryId, boolean recursive, Integer count, boolean useCache) {
		ProductCategory productCategory = productCategoryDao.find(productCategoryId);
		if (productCategoryId != null && productCategory == null) {
			return Collections.emptyList();
		}
		return productCategoryDao.findParents(productCategory, recursive, count);
	}

	/**
	 * 查找商品分类树
	 * 
	 * @return 商品分类树
	 */
	public List<ProductCategory> findTree() {
		return productCategoryDao.findChildren(null, true, null);
	}

	/**
	 * 查找下级商品分类
	 * 
	 * @param productCategory
	 *            商品分类
	 * @param recursive
	 *            是否递归
	 * @param count
	 *            数量
	 * @return 下级商品分类
	 */
	public List<ProductCategory> findChildren(ProductCategory productCategory, boolean recursive, Integer count) {
		return productCategoryDao.findChildren(productCategory, recursive, count);
	}
	
	/**
	 * 查找下级商品分类
	 * 
	 * @param productCategory
	 *            商品分类
	 * @param recursive
	 *            是否递归
	 * @param count
	 *            数量
	 * @return 下级商品分类
	 */
	public List<ProductCategory> findChildren(ProductCategory productCategory) {
		return productCategoryDao.findChildren(productCategory);
	}

	/**
	 * 查找下级商品分类
	 * 
	 * @param productCategoryId
	 *            商品分类ID
	 * @param recursive
	 *            是否递归
	 * @param count
	 *            数量
	 * @param useCache
	 *            是否使用缓存
	 * @return 下级商品分类
	 */
	public List<ProductCategory> findChildren(Long productCategoryId, boolean recursive, Integer count, boolean useCache) {
		ProductCategory productCategory = productCategoryDao.find(productCategoryId);
		if (productCategoryId != null && productCategory == null) {
			return Collections.emptyList();
		}
		return productCategoryDao.findChildren(productCategory, recursive, count);
	}

	/**
	 * 拼接成json类型
	 */
	public JSONArray getProductCategoryArray(Integer count) {
		// 查询1级分类节点
		List<ProductCategory> productCategorys = productCategoryDao.findRoots(true, true);
		StringBuffer sb = new StringBuffer(); // 初始化根节点
		if (CollectionUtils.isNotEmpty(productCategorys)) {
			sb.append("[");
			for (ProductCategory productCategory : productCategorys) {
				List<Product> products = productDao.findByTopSales(productCategory, count);
				if (CollectionUtils.isEmpty(products)) continue; 
				sb.append("{\"id\":\"").append(productCategory.getId()).append("\",");
				sb.append("\"name\":\"").append(productCategory.getName()).append("\",");
				sb.append("\"image\":\"").append(productCategory.getImage()).append("\"");
				if (CollectionUtils.isNotEmpty(products)) {
					sb.append(",\"children\":[");
					for (Product product : products) { 
						Sku defaultSku = product.getDefaultSku();
						sb.append("{\"id\":\"").append(defaultSku.getId()).append("\",");
						sb.append("\"name\":\"").append(product.getName()).append("\",");
						sb.append("\"price\":\"").append(currency(product.getPrice(), false, false)).append("\",");
						sb.append("\"unit\":\"").append(product.getUnit()).append("\",");
						sb.append("\"weight\":\"").append(product.getWeight()).append("\",");
						sb.append("\"type\":\"").append(product.getTypeName()).append("\",");
						sb.append("\"availableStock\":\"").append(defaultSku.getAvailableStock()).append("\",");
						Brand brand = product.getBrand();
						sb.append("\"brand\":\"").append(brand != null ? brand.getName() : "").append("\",");
						sb.append("\"image\":\"").append(product.getImage()).append("\",");
						sb.append(getPromotions(product));
						sb.append("},");
					}
					sb = new StringBuffer(sb.substring(0,sb.lastIndexOf(",")) + "]},");
				} else {
					sb.append("},");
				}
			}
			sb = new StringBuffer(sb.substring(0, sb.length() - 1) + "]");
		}
		return JSONArray.parseArray(sb.toString());
	}
	
	/**
	 * 返回是否有促销信息
	 * @param goods
	 * @return
	 */
	private StringBuffer getPromotions(Product product) {
		Promotion promotion = null;
		StringBuffer sb = new StringBuffer();
		if (CollectionUtils.isNotEmpty(product.getValidPromotions())) {
			Set<Promotion> promotions = product.getValidPromotions();
			for (Iterator<Promotion> iterator = promotions.iterator(); iterator.hasNext();) {
				promotion = iterator.next();
			}
			sb.append("\"promotions\":").append("[{\"name\":\"" + promotion.getName() + "\",\"title\":\"" + promotion.getTitle() + "\"}]");
		} else {
			sb.append("\"promotions\":").append("[]");
		}
		return sb;
	}
	
	@Override
	public ProductCategory save(ProductCategory productCategory) {
		Assert.notNull(productCategory);

		setValue(productCategory);
		return super.save(productCategory);
	}
	
	@Override
	public ProductCategory update(ProductCategory productCategory) {
		Assert.notNull(productCategory);

		setValue(productCategory);
		for (ProductCategory children : productCategoryDao.findChildren(productCategory, true, null)) {
			setValue(children);
		}
		return super.update(productCategory);
	}
	
	@Override
	public void delete(Long id) {
		super.delete(id);
	}
	
	@Override
	public void delete(Long... ids) {
		super.delete(ids);
	}
	
	@Override
	public void delete(ProductCategory productCategory) {
		super.delete(productCategory);
	}
	
	/**
	 * 设置值
	 * 
	 * @param productCategory
	 *            商品分类
	 */
	private void setValue(ProductCategory productCategory) {
		if (productCategory == null) {
			return;
		}
		ProductCategory parent = productCategory.getParent();
		if (parent != null) {
			productCategory.setTreePath(parent.getTreePath() + parent.getId() + ProductCategory.TREE_PATH_SEPARATOR);
			productCategory.setParentId(parent.getId());
		} else {
			productCategory.setTreePath(ProductCategory.TREE_PATH_SEPARATOR);
		}
		productCategory.setGrade(productCategory.getParentIds().length);
	}
	
}