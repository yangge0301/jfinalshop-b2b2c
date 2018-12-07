package com.jfinalshop.service;

import java.util.List;

import javax.inject.Inject;

import net.hasor.core.Singleton;

import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.dao.ArticleTagDao;
import com.jfinalshop.model.ArticleTag;

/**
 * Service - 文章标签
 * 
 */
@Singleton
public class ArticleTagService extends BaseService<ArticleTag> {

	/**
	 * 构造方法
	 */
	public ArticleTagService() {
		super(ArticleTag.class);
	}
	
	@Inject
	private ArticleTagDao articleTagDao;
	
	/**
	 * 查找文章标签
	 * 
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @param useCache
	 *            是否使用缓存
	 * @return 文章标签
	 */
	public List<ArticleTag> findList(Integer count, List<Filter> filters, List<Order> orders, boolean useCache) {
		return articleTagDao.findList(null, count, filters, orders);
	}

	@Override
	public ArticleTag save(ArticleTag articleTag) {
		return super.save(articleTag);
	}
	
	@Override
	public ArticleTag update(ArticleTag articleTag) {
		return super.update(articleTag);
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
	public void delete(ArticleTag articleTag) {
		super.delete(articleTag);
	}
}