package com.jfinalshop.service;

import java.util.List;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinalshop.dao.AreaDao;
import com.jfinalshop.model.Area;
import com.jfinalshop.util.Assert;

/**
 * Service - 地区
 * 
 */
@Singleton
public class AreaService extends BaseService<Area> {
	
	/**
	 * 构造方法
	 */
	public AreaService() {
		super(Area.class);
	}
	
	@Inject
	private AreaDao areaDao;

	/**
	 * 查找顶级地区
	 * 
	 * @return 顶级地区
	 */
	public List<Area> findRoots() {
		return areaDao.findRoots(null);
	}

	/**
	 * 查找顶级地区
	 * 
	 * @param count
	 *            数量
	 * @return 顶级地区
	 */
	public List<Area> findRoots(Integer count) {
		return areaDao.findRoots(count);
	}

	/**
	 * 查找上级地区
	 * 
	 * @param area
	 *            地区
	 * @param recursive
	 *            是否递归
	 * @param count
	 *            数量
	 * @return 上级地区
	 */
	public List<Area> findParents(Area area, boolean recursive, Integer count) {
		return areaDao.findParents(area, recursive, count);
	}

	/**
	 * 查找下级地区
	 * 
	 * @param area
	 *            地区
	 * @param recursive
	 *            是否递归
	 * @param count
	 *            数量
	 * @return 下级地区
	 */
	public List<Area> findChildren(Area area, boolean recursive, Integer count) {
		return areaDao.findChildren(area, recursive, count);
	}

	@Override
	public Area save(Area area) {
		Assert.notNull(area);

		setValue(area);
		return super.save(area);
	}

	@Override
	public Area update(Area area) {
		Assert.notNull(area);

		setValue(area);
		for (Area children : areaDao.findChildren(area, true, null)) {
			setValue(children);
		}
		return super.update(area);
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
	public void delete(Area area) {
		super.delete(area);
	}
	
	/**
	 * 设置值
	 * 
	 * @param area
	 *            地区
	 */
	private void setValue(Area area) {
		if (area == null) {
			return;
		}
		Area parent = area.getParent();
		if (parent != null) {
			area.setFullName(parent.getFullName() + area.getName());
			area.setTreePath(parent.getTreePath() + parent.getId() + Area.TREE_PATH_SEPARATOR);
		} else {
			area.setFullName(area.getName());
			area.setTreePath(Area.TREE_PATH_SEPARATOR);
		}
		area.setGrade(area.getParentIds().length);
	}
}