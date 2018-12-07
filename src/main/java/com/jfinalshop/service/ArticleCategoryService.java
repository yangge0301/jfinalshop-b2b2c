package com.jfinalshop.service;

import java.util.Collections;
import java.util.List;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinalshop.dao.ArticleCategoryDao;
import com.jfinalshop.model.ArticleCategory;
import com.jfinalshop.util.Assert;

/**
 * Service - 文章分类
 * 
 */
@Singleton
public class ArticleCategoryService extends BaseService<ArticleCategory> {

	/**
	 * 构造方法
	 */
	public ArticleCategoryService() {
		super(ArticleCategory.class);
	}
	
	@Inject
	private ArticleCategoryDao articleCategoryDao;
	
	/**
	 * 查找顶级文章分类
	 * 
	 * @return 顶级文章分类
	 */
	public List<ArticleCategory> findRoots() {
		return articleCategoryDao.findRoots(null);
	}

	/**
	 * 查找顶级文章分类
	 * 
	 * @param count
	 *            数量
	 * @return 顶级文章分类
	 */
	public List<ArticleCategory> findRoots(Integer count) {
		return articleCategoryDao.findRoots(count);
	}

	/**
	 * 查找顶级文章分类
	 * 
	 * @param count
	 *            数量
	 * @param useCache
	 *            是否使用缓存
	 * @return 顶级文章分类
	 */
	public List<ArticleCategory> findRoots(Integer count, boolean useCache) {
		return articleCategoryDao.findRoots(count);
	}

	/**
	 * 查找上级文章分类
	 * 
	 * @param articleCategory
	 *            文章分类
	 * @param recursive
	 *            是否递归
	 * @param count
	 *            数量
	 * @return 上级文章分类
	 */
	public List<ArticleCategory> findParents(ArticleCategory articleCategory, boolean recursive, Integer count) {
		return articleCategoryDao.findParents(articleCategory, recursive, count);
	}

	/**
	 * 查找上级文章分类
	 * 
	 * @param articleCategoryId
	 *            文章分类ID
	 * @param recursive
	 *            是否递归
	 * @param count
	 *            数量
	 * @param useCache
	 *            是否使用缓存
	 * @return 上级文章分类
	 */
	public List<ArticleCategory> findParents(Long articleCategoryId, boolean recursive, Integer count, boolean useCache) {
		ArticleCategory articleCategory = articleCategoryDao.find(articleCategoryId);
		if (articleCategoryId != null && articleCategory == null) {
			return Collections.emptyList();
		}
		return articleCategoryDao.findParents(articleCategory, recursive, count);
	}

	/**
	 * 查找文章分类树
	 * 
	 * @return 文章分类树
	 */
	public List<ArticleCategory> findTree() {
		return articleCategoryDao.findChildren(null, true, null);
	}

	/**
	 * 查找下级文章分类
	 * 
	 * @param articleCategory
	 *            文章分类
	 * @param recursive
	 *            是否递归
	 * @param count
	 *            数量
	 * @return 下级文章分类
	 */
	public List<ArticleCategory> findChildren(ArticleCategory articleCategory, boolean recursive, Integer count) {
		return articleCategoryDao.findChildren(articleCategory, recursive, count);
	}

	/**
	 * 查找下级文章分类
	 * 
	 * @param articleCategoryId
	 *            文章分类ID
	 * @param recursive
	 *            是否递归
	 * @param count
	 *            数量
	 * @param useCache
	 *            是否使用缓存
	 * @return 下级文章分类
	 */
	public List<ArticleCategory> findChildren(Long articleCategoryId, boolean recursive, Integer count, boolean useCache) {
		ArticleCategory articleCategory = articleCategoryDao.find(articleCategoryId);
		if (articleCategoryId != null && articleCategory == null) {
			return Collections.emptyList();
		}
		return articleCategoryDao.findChildren(articleCategory, recursive, count);
	}
	
	@Override
	public ArticleCategory save(ArticleCategory articleCategory) {
		Assert.notNull(articleCategory);

		setValue(articleCategory);
		return super.save(articleCategory);
	}
	
	@Override
	public ArticleCategory update(ArticleCategory articleCategory) {
		Assert.notNull(articleCategory);

		setValue(articleCategory);
		for (ArticleCategory children : articleCategoryDao.findChildren(articleCategory, true, null)) {
			setValue(children);
		}
		return super.update(articleCategory);
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
	public void delete(ArticleCategory articleCategory) {
		super.delete(articleCategory);
	}
	
	/**
	 * 设置值
	 * 
	 * @param articleCategory
	 *            文章分类
	 */
	private void setValue(ArticleCategory articleCategory) {
		if (articleCategory == null) {
			return;
		}
		ArticleCategory parent = articleCategory.getParent();
		if (parent != null) {
			articleCategory.setTreePath(parent.getTreePath() + parent.getId() + ArticleCategory.TREE_PATH_SEPARATOR);
		} else {
			articleCategory.setTreePath(ArticleCategory.TREE_PATH_SEPARATOR);
		}
		articleCategory.setGrade(articleCategory.getParentIds().length);
	}

}