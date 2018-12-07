package com.jfinalshop.service;

import java.util.Collections;
import java.util.List;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.Pageable;
import com.jfinalshop.dao.ArticleCategoryDao;
import com.jfinalshop.dao.ArticleDao;
import com.jfinalshop.dao.ArticleTagDao;
import com.jfinalshop.model.Article;
import com.jfinalshop.model.ArticleArticleTag;
import com.jfinalshop.model.ArticleCategory;
import com.jfinalshop.model.ArticleTag;
import com.jfinalshop.util.Assert;
import com.xiaoleilu.hutool.util.CollectionUtil;

/**
 * Service - 文章
 * 
 */
@Singleton
public class ArticleService extends BaseService<Article> {

	/**
	 * 构造方法
	 */
	public ArticleService() {
		super(Article.class);
	}
	
	private CacheManager cacheManager = CacheKit.getCacheManager();
	
	@Inject
	private ArticleDao articleDao;
	@Inject
	private ArticleCategoryDao articleCategoryDao;
	@Inject
	private ArticleTagDao articleTagDao;
	
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
		return articleDao.findList(articleCategory, articleTag, isPublication, count, filters, orders);
	}

	/**
	 * 查找文章
	 * 
	 * @param articleCategoryId
	 *            文章分类ID
	 * @param articleTagId
	 *            文章标签ID
	 * @param isPublication
	 *            是否发布
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @param useCache
	 *            是否使用缓存
	 * @return 文章
	 */
	public List<Article> findList(Long articleCategoryId, Long articleTagId, Boolean isPublication, Integer count, List<Filter> filters, List<Order> orders, boolean useCache) {
		ArticleCategory articleCategory = articleCategoryDao.find(articleCategoryId);
		if (articleCategoryId != null && articleCategory == null) {
			return Collections.emptyList();
		}
		ArticleTag articleTag = articleTagDao.find(articleTagId);
		if (articleTagId != null && articleTag == null) {
			return Collections.emptyList();
		}
		return articleDao.findList(articleCategory, articleTag, isPublication, count, filters, orders);
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
		return articleDao.findPage(articleCategory, articleTag, isPublication, pageable);
	}

	/**
	 * 查看点击数
	 * 
	 * @param id
	 *            ID
	 * @return 点击数
	 */
	public long viewHits(Long id) {
		Assert.notNull(id);

		Ehcache cache = cacheManager.getEhcache(Article.HITS_CACHE_NAME);
		cache.acquireWriteLockOnKey(id);
		try {
			Element element = cache.get(id);
			Long hits;
			if (element != null) {
				hits = (Long) element.getObjectValue() + 1;
			} else {
				Article article = articleDao.find(id);
				if (article == null) {
					return 0L;
				}
				hits = article.getHits() + 1;
			}
			cache.put(new Element(id, hits));
			return hits;
		} finally {
			cache.releaseWriteLockOnKey(id);
		}
	}

	@Override
	public Article save(Article article) {
		super.save(article);
		// 关联保存
		List<ArticleTag> articleTags = article.getArticleTags();
		if (CollectionUtil.isNotEmpty(articleTags)) {
			for (ArticleTag articleTag : articleTags) {
				ArticleArticleTag articleArticleTag = new ArticleArticleTag();
				articleArticleTag.setArticlesId(article.getId());
				articleArticleTag.setArticleTagsId(articleTag.getId());
				articleArticleTag.save();
			}
		}
		return article;
	}
	
	@Override
	public Article update(Article article) {
		return super.update(article);
	}
	
	@Override
	public Article update(Article article, String... ignoreProperties) {
		return super.update(article, ignoreProperties);
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
	public void delete(Article article) {
		super.delete(article);
	}
	
}