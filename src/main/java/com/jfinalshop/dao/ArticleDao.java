package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Article;
import com.jfinalshop.model.ArticleCategory;
import com.jfinalshop.model.ArticleTag;
import com.xiaoleilu.hutool.date.DateUtil;

/**
 * Dao - 文章
 * 
 */
public class ArticleDao extends BaseDao<Article> {

	/**
	 * 构造方法
	 */
	public ArticleDao() {
		super(Article.class);
	}
	
	/**
	 * 查找文章
	 * 
	 * @param articleCategory
	 *            文章分类
	 * @param articleTag
	 *            文章标签
	 * @param isPublication
	 *            是否发布
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 文章
	 */
	public List<Article> findList(ArticleCategory articleCategory, ArticleTag articleTag, Boolean isPublication, Integer count, List<Filter> filters, List<Order> orders) {
		String sql = "SELECT * FROM article a WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		if (articleCategory != null) {
			sql += " AND article_category_id IN (SELECT id FROM `article_category` WHERE id = ? OR tree_path like ?) ";
			params.add(articleCategory.getId());
			params.add("%" + ArticleCategory.TREE_PATH_SEPARATOR + articleCategory.getId() + ArticleCategory.TREE_PATH_SEPARATOR + "%");
		}
		if (articleTag != null) {
			sql = "SELECT a.* FROM `article_article_tag` aat  LEFT JOIN `article` a ON a.`id` = aat.`articles_id` WHERE aat.`article_tags_id` = ?";
			params.add(articleTag.getId());
		}
		if (isPublication != null) {
			sql += " AND is_publication = ?";
			params.add(isPublication);
		}
		if (orders == null || orders.isEmpty()) {
			orders.add(new Order("is_top", Order.Direction.desc));
			orders.add(new Order("created_date", Order.Direction.desc));
		}
		return super.findList(sql, null, count, filters, orders, params);
	}
	
	/**
	 * 查找文章
	 * 
	 * @param articleCategory
	 *            文章分类
	 * @param isPublication
	 *            是否发布
	 * @param beginDate
	 *            起始日期
	 * @param endDate
	 *            结束日期
	 * @param first
	 *            起始记录
	 * @param count
	 *            数量
	 * @return 文章
	 */
	public List<Article> findList(ArticleCategory articleCategory, Boolean isPublication, Date beginDate, Date endDate, Integer first, Integer count) {
		String sql = "SELECT * FROM article a WHERE 1 = 1 ";
		List<Object> params = new ArrayList<Object>();
		if (articleCategory != null) {
			sql += " AND article_category_id IN (SELECT id FROM `article_category` WHERE id = ? OR tree_path LIKE ?)";
			params.add(articleCategory.getId());
			params.add("%" + ArticleCategory.TREE_PATH_SEPARATOR + articleCategory.getId() + ArticleCategory.TREE_PATH_SEPARATOR + "%");
		}
		if (isPublication != null) {
			sql += " AND is_publication = ?";
			params.add(isPublication);
		}
		if (beginDate != null) {
			sql += " AND created_date >= ?";
			params.add(DateUtil.formatDateTime(beginDate));
		}
		if (endDate != null) {
			sql += " AND created_date <= ?";
			params.add(DateUtil.formatDateTime(endDate));
		}
		return super.findList(sql, first, count, params);
	}
	
	/**
	 * 查找文章分页
	 * 
	 * @param articleCategory
	 *            文章分类
	 * @param articleTag
	 *            文章标签
	 * @param isPublication
	 *            是否发布
	 * @param pageable
	 *            分页信息
	 * @return 文章分页
	 */
	public Page<Article> findPage(ArticleCategory articleCategory, ArticleTag articleTag, Boolean isPublication, Pageable pageable) {
		String sqlExceptSelect = "FROM article a WHERE 1 = 1 ";
		
		List<Object> params = new ArrayList<Object>();
		if (articleCategory != null) {
			sqlExceptSelect += " AND article_category_id IN (SELECT id FROM `article_category` WHERE id = ? OR tree_path LIKE ?)";
			params.add(articleCategory.getId());
			params.add("%" + ArticleCategory.TREE_PATH_SEPARATOR + articleCategory.getId() + ArticleCategory.TREE_PATH_SEPARATOR + "%");
		}
		if (articleTag != null) {
			sqlExceptSelect = "SELECT a.* FROM `article_article_tag` aat  LEFT JOIN `article` a ON a.`id` = aat.`articles_id` WHERE aat.`article_tags_id` = ?";
			params.add(articleTag.getId());
		}
		if (isPublication != null) {
			sqlExceptSelect += " AND is_publication = ?";
			params.add(isPublication);
		}
		if (pageable == null || ((StringUtils.isEmpty(pageable.getOrderProperty()) || pageable.getOrderDirection() == null) && CollectionUtils.isEmpty(pageable.getOrders()))) {
			List<Order> orders = new ArrayList<Order>();
			orders.add(new Order("is_top", Order.Direction.desc));
			orders.add(new Order("created_date", Order.Direction.desc));
			pageable.setOrders(orders);
		}
		return super.findPage(sqlExceptSelect, pageable, params);
	}

}