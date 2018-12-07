package com.jfinalshop.service;

import java.util.ArrayList;
import java.util.List;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.dao.MenuDao;
import com.jfinalshop.model.Menu;

/**
 * Service - 菜单
 * 
 */
@Singleton
public class MenuService extends BaseService<Menu> {

	
	/**
	 * 构造方法
	 */
	public MenuService() {
		super(Menu.class);
	}
	
	@Inject
	private MenuDao menuDao;
	
	/**
	 * 查找所有菜单
	 * 
	 * @return 菜单
	 */
	public List<Menu> findAll(Menu.Type type) {
		List<Order> orders = new ArrayList<Order>();
		orders.add(new Order("level_code", Order.Direction.asc));
		
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(Filter.eq("type", type.ordinal()));
		filters.add(Filter.eq("is_enabled", true));
		return menuDao.findList(null, null, filters, orders);
	}
	
	/**
	 * 查找菜单分类树
	 * 
	 * @return 菜单分类树
	 */
	public List<Menu> findTree() {
		// 增加排序
		List<Order> orders = new ArrayList<Order>();
		orders.add(new Order("level_code", Order.Direction.asc));
		
		// 增加条件
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(Filter.eq("is_enabled", true));
		return menuDao.findList(null, null, filters, orders);
	}
	
	/**
	 * 查找下级菜单分类
	 * 
	 * @param menu
	 *            菜单分类
	 * @return 下级菜单分类
	 */
	public List<Menu> findChildren(Menu menu) {
		return menuDao.findChildren(menu);
	}
	
	/**
	 * 获取最大的菜单编码
	 * 
	 * @param parentId
	 *            上级菜单Id
	 * @return 用户名是否存在
	 */
	public String findLevelCode(Long parentId) {
		return menuDao.findLevelCode(parentId);
	}
}
