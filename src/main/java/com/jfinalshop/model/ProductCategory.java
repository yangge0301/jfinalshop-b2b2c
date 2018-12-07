package com.jfinalshop.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinalshop.model.base.BaseProductCategory;
import com.jfinalshop.util.FreeMarkerUtils;

import freemarker.core.Environment;
import freemarker.template.TemplateException;

/**
 * Model - 商品分类
 * 
 */
public class ProductCategory extends BaseProductCategory<ProductCategory> {
	private static final long serialVersionUID = -2936605043952329686L;
	public static final ProductCategory dao = new ProductCategory().dao();
	
	/**
	 * 树路径分隔符
	 */
	public static final String TREE_PATH_SEPARATOR = ",";

	/**
	 * 路径
	 */
	private static final String PATH = "/product/list/%d";
	
	/**
	 * 上级分类
	 */
	private ProductCategory parent;

	/**
	 * 下级分类
	 */
	private List<ProductCategory> children = new ArrayList<>();

	/**
	 * 商品
	 */
	private List<Product> products = new ArrayList<>();

	/**
	 * 关联品牌
	 */
	private List<Brand> brands = new ArrayList<>();

	/**
	 * 关联促销
	 */
	private List<Promotion> promotions = new ArrayList<>();

	/**
	 * 参数
	 */
	private List<Parameter> parameters = new ArrayList<>();

	/**
	 * 属性
	 */
	private List<Attribute> attributes = new ArrayList<>();

	/**
	 * 规格
	 */
	private List<Specification> specifications = new ArrayList<>();

	/**
	 * 店铺
	 */
	private List<Store> stores = new ArrayList<>();

	/**
	 * 经营分类申请
	 */
	private List<CategoryApplication> categoryApplications = new ArrayList<>();

	/**
	 * 获取上级分类
	 * 
	 * @return 上级分类
	 */
	public ProductCategory getParent() {
		if (parent == null) {
			parent = ProductCategory.dao.findById(getParentId());
		}
		return parent;
	}

	/**
	 * 设置上级分类
	 * 
	 * @param parent
	 *            上级分类
	 */
	public void setParent(ProductCategory parent) {
		this.parent = parent;
	}

	/**
	 * 获取下级分类
	 * 
	 * @return 下级分类
	 */
	public List<ProductCategory> getChildren() {
		if (CollectionUtils.isEmpty(children)) {
			String sql = "SELECT * FROM `product_category` WHERE parent_id = ?";
			children = ProductCategory.dao.find(sql, getId());
		}
		return children;
	}

	/**
	 * 设置下级分类
	 * 
	 * @param children
	 *            下级分类
	 */
	public void setChildren(List<ProductCategory> children) {
		this.children = children;
	}

	/**
	 * 获取商品
	 * 
	 * @return 商品
	 */
	public List<Product> getProducts() {
		if (CollectionUtils.isEmpty(products)) {
			String sql = "SELECT * FROM `product` WHERE product_category_id = ?";
			products = Product.dao.find(sql, getId());
		}
		return products;
	}

	/**
	 * 设置商品
	 * 
	 * @param products
	 *            商品
	 */
	public void setProducts(List<Product> products) {
		this.products = products;
	}

	/**
	 * 获取关联品牌
	 * 
	 * @return 关联品牌
	 */
	public List<Brand> getBrands() {
		if (CollectionUtils.isEmpty(brands)) {
			String sql = "SELECT p.*  FROM brand p LEFT JOIN product_category_brand pcb ON p.id = pcb.brands_id WHERE pcb.`product_categories_id` = ?";
			brands = Brand.dao.find(sql, getId());
		}
		return brands;
	}

	/**
	 * 设置关联品牌
	 * 
	 * @param brands
	 *            关联品牌
	 */
	public void setBrands(List<Brand> brands) {
		this.brands = brands;
	}

	/**
	 * 获取关联促销
	 * 
	 * @return 关联促销
	 */
	public List<Promotion> getPromotions() {
		if (CollectionUtils.isEmpty(promotions)) {
			String sql = "SELECT p.*  FROM promotion p LEFT JOIN product_category_promotion pcp ON p.id = pcp.promotions_id WHERE pcp.`product_categories_id` = ?";
			promotions = Promotion.dao.find(sql, getId());
		}
		return promotions;
	}

	/**
	 * 设置关联促销
	 * 
	 * @param promotions
	 *            关联促销
	 */
	public void setPromotions(List<Promotion> promotions) {
		this.promotions = promotions;
	}

	/**
	 * 获取参数
	 * 
	 * @return 参数
	 */
	public List<Parameter> getParameters() {
		if (CollectionUtils.isEmpty(parameters)) {
			String sql = "SELECT * FROM `parameter` WHERE product_category_id = ?";
			parameters = Parameter.dao.find(sql, getId());
		}
		return parameters;
	}

	/**
	 * 设置参数
	 * 
	 * @param parameters
	 *            参数
	 */
	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	/**
	 * 获取属性
	 * 
	 * @return 属性
	 */
	public List<Attribute> getAttributes() {
		if (CollectionUtils.isEmpty(attributes)) {
			String sql = "SELECT * FROM `attribute` WHERE product_category_id = ?";
			attributes = Attribute.dao.find(sql, getId());
		}
		return attributes;
	}

	/**
	 * 设置属性
	 * 
	 * @param attributes
	 *            属性
	 */
	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	/**
	 * 获取规格
	 * 
	 * @return 规格
	 */
	public List<Specification> getSpecifications() {
		if (CollectionUtils.isEmpty(specifications)) {
			String sql = "SELECT * FROM `specification` WHERE product_category_id = ?";
			specifications = Specification.dao.find(sql, getId());
		}
		return specifications;
	}

	/**
	 * 设置规格
	 * 
	 * @param specifications
	 *            规格
	 */
	public void setSpecifications(List<Specification> specifications) {
		this.specifications = specifications;
	}

	/**
	 * 获取店铺
	 * 
	 * @return 店铺
	 */
	public List<Store> getStores() {
		if (CollectionUtils.isEmpty(stores)) {
			String sql = "SELECT * FROM `store` WHERE store_category_id = ?";
			stores = Store.dao.find(sql, getId());
		}
		return stores;
	}

	/**
	 * 设置店铺
	 * 
	 * @param stores
	 *            店铺
	 */
	public void setStores(List<Store> stores) {
		this.stores = stores;
	}

	/**
	 * 获取经营分类申请
	 * 
	 * @return 经营分类申请
	 */
	public List<CategoryApplication> getCategoryApplications() {
		if (CollectionUtils.isEmpty(categoryApplications)) {
			String sql = "SELECT * FROM `category_application` WHERE product_category_id = ?";
			categoryApplications = CategoryApplication.dao.find(sql, getId());
		}
		return categoryApplications;
	}

	/**
	 * 设置经营分类申请
	 * 
	 * @param categoryApplications
	 *            经营分类申请
	 */
	public void setCategoryApplications(List<CategoryApplication> categoryApplications) {
		this.categoryApplications = categoryApplications;
	}

	/**
	 * 获取路径
	 * 
	 * @return 路径
	 */
	public String getPath() {
		return String.format(ProductCategory.PATH, getId());
	}

	/**
	 * 获取所有上级分类ID
	 * 
	 * @return 所有上级分类ID
	 */
	public Long[] getParentIds() {
		String[] parentIds = StringUtils.split(getTreePath(), TREE_PATH_SEPARATOR);
		Long[] result = new Long[parentIds.length];
		for (int i = 0; i < parentIds.length; i++) {
			result[i] = Long.valueOf(parentIds[i]);
		}
		return result;
	}

	/**
	 * 获取所有上级分类
	 * 
	 * @return 所有上级分类
	 */
	public List<ProductCategory> getParents() {
		List<ProductCategory> parents = new ArrayList<>();
		recursiveParents(parents, this);
		return parents;
	}

	/**
	 * 解析页面标题
	 * 
	 * @return 页面标题
	 */
	public String resolveSeoTitle() {
		try {
			Environment environment = FreeMarkerUtils.getCurrentEnvironment();
			return FreeMarkerUtils.process(getSeoTitle(), environment != null ? environment.getDataModel() : null);
		} catch (IOException e) {
			return null;
		} catch (TemplateException e) {
			return null;
		}
	}

	/**
	 * 解析页面关键词
	 * 
	 * @return 页面关键词
	 */
	public String resolveSeoKeywords() {
		try {
			Environment environment = FreeMarkerUtils.getCurrentEnvironment();
			return FreeMarkerUtils.process(getSeoKeywords(), environment != null ? environment.getDataModel() : null);
		} catch (IOException e) {
			return null;
		} catch (TemplateException e) {
			return null;
		}
	}

	/**
	 * 解析页面描述
	 * 
	 * @return 页面描述
	 */
	public String resolveSeoDescription() {
		try {
			Environment environment = FreeMarkerUtils.getCurrentEnvironment();
			return FreeMarkerUtils.process(getSeoDescription(), environment != null ? environment.getDataModel() : null);
		} catch (IOException e) {
			return null;
		} catch (TemplateException e) {
			return null;
		}
	}

	/**
	 * 递归上级分类
	 * 
	 * @param parents
	 *            上级分类
	 * @param productCategory
	 *            商品分类
	 */
	private void recursiveParents(List<ProductCategory> parents, ProductCategory productCategory) {
		if (productCategory == null) {
			return;
		}
		ProductCategory parent = productCategory.getParent();
		if (parent != null) {
			parents.add(0, parent);
			recursiveParents(parents, parent);
		}
	}

	/**
	 * 删除前处理
	 */
	public void preRemove() {
		List<Store> stores = getStores();
		if (stores != null) {
			for (Store store : stores) {
				store.getProductCategories().remove(this);
			}
		}
	}
	
	
	/**
	 * 重写equals方法
	 * 
	 * @param obj
	 *            对象
	 * @return 是否相等
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (!ProductCategory.class.isAssignableFrom(obj.getClass())) {
			return false;
		}
		ProductCategory other = (ProductCategory) obj;
		return getId() != null ? getId().equals(other.getId()) : false;
	}

	/**
	 * 重写hashCode方法
	 * 
	 * @return HashCode
	 */
	@Override
	public int hashCode() {
		int hashCode = 17;
		hashCode += getId() != null ? getId().hashCode() * 31 : 0;
		return hashCode;
	}
}
