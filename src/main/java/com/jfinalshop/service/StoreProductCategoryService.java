package com.jfinalshop.service;

import java.util.Collections;
import java.util.List;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.dao.StoreDao;
import com.jfinalshop.dao.StoreProductCategoryDao;
import com.jfinalshop.model.Store;
import com.jfinalshop.model.StoreProductCategory;
import com.jfinalshop.util.Assert;

/**
 * Service - 店铺商品分类
 * 
 */
@Singleton
public class StoreProductCategoryService extends BaseService<StoreProductCategory> {

	/**
	 * 构造方法
	 */
	public StoreProductCategoryService() {
		super(StoreProductCategory.class);
	}
	
	@Inject
	private StoreProductCategoryDao storeProductCategoryDao;
	@Inject
	private StoreDao storeDao;
	@Inject
	private StoreService storeService;
	
	/**
	 * 查找顶级店铺商品分类
	 * 
	 * @param storeId
	 *            店铺ID
	 * @return 顶级店铺商品分类
	 */
	public List<StoreProductCategory> findRoots(Long storeId) {
		Store store = storeDao.find(storeId);
		if (storeId != null && store == null) {
			return Collections.emptyList();
		}
		return storeProductCategoryDao.findRoots(store, null);
	}

	/**
	 * 查找顶级店铺商品分类
	 * 
	 * @param storeId
	 *            店铺ID
	 * @param count
	 *            数量
	 * @return 顶级店铺商品分类
	 */
	public List<StoreProductCategory> findRoots(Long storeId, Integer count) {
		Store store = storeDao.find(storeId);
		if (storeId != null && store == null) {
			return Collections.emptyList();
		}
		return storeProductCategoryDao.findRoots(store, count);
	}

	/**
	 * 查找顶级店铺商品分类
	 * 
	 * @param storeId
	 *            店铺ID
	 * @param count
	 *            数量
	 * @param useCache
	 *            是否使用缓存
	 * @return 顶级店铺商品分类
	 */
	public List<StoreProductCategory> findRoots(Long storeId, Integer count, boolean useCache) {
		Store store = storeDao.find(storeId);
		if (storeId != null && store == null) {
			return Collections.emptyList();
		}
		return storeProductCategoryDao.findRoots(store, count);
	}

	/**
	 * 查找上级店铺商品分类
	 * 
	 * @param storeProductCategoryId
	 *            店铺商品分类ID
	 * @param recursive
	 *            是否递归
	 * @param count
	 *            数量
	 * @param useCache
	 *            是否使用缓存
	 * @return 上级店铺商品分类
	 */
	public List<StoreProductCategory> findParents(Long storeProductCategoryId, boolean recursive, Integer count, boolean useCache) {
		StoreProductCategory storeProductCategory = storeProductCategoryDao.find(storeProductCategoryId);
		if (storeProductCategoryId != null && storeProductCategory == null) {
			return Collections.emptyList();
		}
		return storeProductCategoryDao.findParents(storeProductCategory, recursive, count);
	}

	/**
	 * 查找店铺商品分类树
	 * 
	 * @param store
	 *            店铺
	 * @return 店铺商品分类树
	 */
	public List<StoreProductCategory> findTree(Store store) {
		return storeProductCategoryDao.findChildren(null, store, true, null);
	}

	/**
	 * 查找下级店铺商品分类
	 * 
	 * @param storeProductCategory
	 *            店铺商品分类
	 * @param store
	 *            店铺
	 * @param recursive
	 *            是否递归
	 * @param count
	 *            数量
	 * @return 下级店铺商品分类
	 */
	public List<StoreProductCategory> findChildren(StoreProductCategory storeProductCategory, Store store, boolean recursive, Integer count) {
		return storeProductCategoryDao.findChildren(storeProductCategory, store, recursive, count);
	}

	/**
	 * 查找下级店铺商品分类
	 * 
	 * @param storeProductCategoryId
	 *            店铺商品分类ID
	 * @param storeId
	 *            店铺ID
	 * @param recursive
	 *            是否递归
	 * @param count
	 *            数量
	 * @param useCache
	 *            是否使用缓存
	 * @return 下级店铺商品分类
	 */
	public List<StoreProductCategory> findChildren(Long storeProductCategoryId, Long storeId, boolean recursive, Integer count, boolean useCache) {
		StoreProductCategory storeProductCategory = storeProductCategoryDao.find(storeProductCategoryId);
		if (storeProductCategoryId != null && storeProductCategory == null) {
			return Collections.emptyList();
		}
		Store store = storeDao.find(storeId);
		if (storeId == null && store == null) {
			return Collections.emptyList();
		}

		return storeProductCategoryDao.findChildren(storeProductCategory, store, recursive, count);
	}

	/**
	 * 查找店铺商品分类
	 * 
	 * @param store
	 *            店铺
	 * @param pageable
	 *            分页
	 * @return 店铺商品分类
	 */
	public Page<StoreProductCategory> findPage(Store store, Pageable pageable) {
		return storeProductCategoryDao.findPage(store, pageable);
	}
	
	@Override
	public StoreProductCategory save(StoreProductCategory storeProductCategory) {
		Assert.notNull(storeProductCategory);

		setValue(storeProductCategory);
		return super.save(storeProductCategory);
	}
	
	@Override
	public StoreProductCategory update(StoreProductCategory storeProductCategory) {
		Assert.notNull(storeProductCategory);

		setValue(storeProductCategory);
		for (StoreProductCategory children : storeProductCategoryDao.findChildren(storeProductCategory, storeService.getCurrent(), true, null)) {
			setValue(children);
		}
		return super.update(storeProductCategory);
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
	public void delete(StoreProductCategory storeProductCategory) {
		super.delete(storeProductCategory);
	}
	
	/**
	 * 设置值
	 * 
	 * @param storeProductCategory
	 *            店铺商品分类
	 */
	private void setValue(StoreProductCategory storeProductCategory) {
		if (storeProductCategory == null) {
			return;
		}
		StoreProductCategory parent = storeProductCategory.getParent();
		if (parent != null) {
			storeProductCategory.setTreePath(parent.getTreePath() + parent.getId() + StoreProductCategory.TREE_PATH_SEPARATOR);
		} else {
			storeProductCategory.setTreePath(StoreProductCategory.TREE_PATH_SEPARATOR);
		}
		storeProductCategory.setGrade(storeProductCategory.getParentIds().length);
	}

}