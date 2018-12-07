package com.jfinalshop.dao;

import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.*;
import com.jfinalshop.Filter;
import com.jfinalshop.Order;
import com.jfinalshop.Pageable;
import com.jfinalshop.util.Assert;
import com.jfinalshop.util.GenericsUtils;
import com.jfinalshop.util.ReflectionUtils;
import com.jfinalshop.util.sequence.Sequence;
import com.jfinalshop.util.sequence.UidGenerator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * Dao - 基类
 * 
 */
public class BaseDao<M extends Model<M>> {
	
	/**
	 * "ID"属性名称
	 */
	public static final String ID_PROPERTY_NAME = "id";

	/**
	 * "创建日期"属性名称
	 */
	public static final String CREATED_DATE_PROPERTY_NAME = "created_date";

	/**
	 * "最后修改日期"属性名称
	 */
	public static final String LAST_MODIFIED_DATE_PROPERTY_NAME = "last_modified_date";

	/**
	 * "版本"属性名称
	 */
	public static final String VERSION_PROPERTY_NAME = "version";
	
	/**
	 * "排序"属性名称
	 */
	public static final String ORDER_PROPERTY_NAME = "orders";
	
	/**
	 * 
	 */
	private Sequence idWorker;
	
	/**
	 * 实体类类型
	 */
	private Class<M> modelClass;

	protected M modelManager;

	public Class<M> getModelClass() {
		return modelClass;
	}

	public void setModelClass(Class<M> modelClass) {
		this.modelClass = modelClass;
	}
	
	/**
	 * 构造方法
	 */
	@SuppressWarnings("unchecked")
	public BaseDao(Class<M> entity) {
		try {
			Class<M> clazz = GenericsUtils.getSuperClassGenricType(entity);
			setModelClass(clazz);
			modelManager = modelClass.newInstance();
		} catch (InstantiationException e) {
			LogKit.error("instance model fail" + e);
		} catch (IllegalAccessException e) {
			LogKit.error("instance model fail" + e);
		}
	}
	
	/**
	 * Model的表名
	 */
	public String getTableName() {
		Table table = TableMapping.me().getTable(getModelClass());
		return table.getName();
	}

	
	/**
	 * 判断是否存在
	 * 
	 * @param attributeName
	 *            属性名称
	 * @param attributeValue
	 *            属性值
	 * @return 是否存在
	 */
	public boolean exists(String attributeName, Object attributeValue) {
		Assert.hasText(attributeName);
		
		String sql = "SELECT COUNT(1) FROM `" + getTableName() + "` WHERE " + attributeName + " = ?";

		return Db.queryLong(sql, attributeValue) > 0;
	}

	/**
	 * 判断是否存在
	 * 
	 * @param attributeName
	 *            属性名称
	 * @param attributeValue
	 *            属性值
	 * @param ignoreCase
	 *            忽略大小写
	 * @return 是否存在
	 */
	public boolean exists(String attributeName, String attributeValue, boolean ignoreCase) {
		Assert.hasText(attributeName);

		String sql = "SELECT COUNT(1) FROM `" + getTableName() + "` WHERE ";
		List<Object> params = new ArrayList<Object>();
		if (ignoreCase) {
			sql += attributeName + " = ?";
			params.add(StringUtils.lowerCase(attributeValue));
		} else {
			sql += attributeName + " = ?";
			params.add(attributeValue);
		}
		return Db.queryLong(sql, params.toArray()) > 0;
	}

	/**
	 * 判断是否唯一
	 * 
	 * @param id
	 *            ID
	 * @param attributeName
	 *            属性名称
	 * @param attributeValue
	 *            属性值
	 * @return 是否唯一
	 */
	public boolean unique(Long id, String attributeName, Object attributeValue) {
		Assert.hasText(attributeName);

		if (id != null) {
			String sql = "SELECT COUNT(1) FROM `" + getTableName() + "` WHERE " + attributeName + " = ? AND id != ?";
			
			return Db.queryLong(sql, attributeValue, id) <= 0;
		} else {
			return !exists(attributeName, attributeValue);
		}
	}

	/**
	 * 判断是否唯一
	 * 
	 * @param id
	 *            ID
	 * @param attributeName
	 *            属性名称
	 * @param attributeValue
	 *            属性值
	 * @param ignoreCase
	 *            忽略大小写
	 * @return 是否唯一
	 */
	public boolean unique(Long id, String attributeName, String attributeValue, boolean ignoreCase) {
		Assert.hasText(attributeName);

		if (id != null) {
			String sql = "SELECT COUNT(1) FROM `" + getTableName() + "` WHERE ";
			List<Object> params = new ArrayList<Object>();
			if (ignoreCase) {
				sql += attributeName + " = ? AND id != ?";
				params.add(StringUtils.lowerCase(attributeValue));
				params.add(id);
			} else {
				sql += attributeName + " = ? AND id != ?";
				params.add(attributeValue);
				params.add(id);
			}
			return Db.queryLong(sql, params.toArray()) <= 0;
		} else {
			return !exists(attributeName, attributeValue);
		}
	}

	/**
	 * 查找实体对象
	 * 
	 * @param id
	 *            ID
	 * @return 实体对象，若不存在则返回null
	 */
	public M find(Long id) {
		if (id == null) {
			return null;
		}
		return modelManager.findById(id);
	}

	/**
	 * 查找实体对象
	 * 
	 * @param attributeName
	 *            属性名称
	 * @param attributeValue
	 *            属性值
	 * @return 实体对象，若不存在则返回null
	 */
	public M find(String attributeName, Object attributeValue) {
		Assert.hasText(attributeName);
		if (attributeValue == null) {
			return null;
		}
		String sql = "SELECT * FROM `" + getTableName() + "` WHERE " + attributeName + " = ?";
		return modelManager.findFirst(sql, attributeValue);
	}

	/**
	 * 查找实体对象
	 * 
	 * @param attributeName
	 *            属性名称
	 * @param attributeValue
	 *            属性值
	 * @param ignoreCase
	 *            忽略大小写
	 * @return 实体对象，若不存在则返回null
	 */
	public M find(String attributeName, String attributeValue, boolean ignoreCase) {
		Assert.hasText(attributeName);

		String sql = "SELECT * FROM `" + getTableName();
		List<Object> params = new ArrayList<Object>();
		if (ignoreCase) {
			sql += "` WHERE " + attributeName + " = ?";
			params.add(StringUtils.lowerCase(attributeValue));
		} else {
			sql += "` WHERE " + attributeName + " = ?";
			params.add(attributeValue);
		}
		return modelManager.findFirst(sql, params.toArray());
	}
	
	/**
	 * 查找实体对象集合
	 * 
	 * @param first
	 *            起始记录
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 实体对象集合
	 */
	public List<M> findList(Integer first, Integer count, List<Filter> filters, List<Order> orders) {
		String sql = "SELECT * FROM `" + getTableName() + "` WHERE 1 = 1 ";
		return findList(sql, first, count, filters, orders, null);
	}

	/**
	 * 查找实体对象分页
	 * 
	 * @param pageable
	 *            分页信息
	 * @return 实体对象分页
	 */
	public Page<M> findPage(Pageable pageable) {
		String sqlExceptSelect = "FROM `" + getTableName() + "` WHERE 1 = 1 ";
		return findPage(sqlExceptSelect, pageable, null);
	}

	/**
	 * 查询实体对象数量
	 * 
	 * @param filters
	 *            筛选
	 * @return 实体对象数量
	 */
	public long count(Filter... filters) {
		String sql = "SELECT COUNT(1) FROM `" + getTableName() + "` WHERE 1 = 1 ";
		return count(sql, ArrayUtils.isNotEmpty(filters) ? Arrays.asList(filters) : null, null);
	}

	/**
	 * 持久化实体对象
	 * 
	 * @param entity
	 *            实体对象
	 */
	public void save(M model) {
		Assert.notNull(model);
		model.set(ID_PROPERTY_NAME, UidGenerator.nextId());
		model.set(CREATED_DATE_PROPERTY_NAME, new Date());
		model.set(LAST_MODIFIED_DATE_PROPERTY_NAME, new Date());
		model.set(VERSION_PROPERTY_NAME, 0);
		model.save();
	}
	
	/**
	 * 更新实体对象
	 * 
	 * @param entity
	 *            实体对象
	 * @return 实体对象
	 */
	public M update(M model) {
		Assert.notNull(model);
		model.set(LAST_MODIFIED_DATE_PROPERTY_NAME, new Date());
		M pModel = find(model.getLong(ID_PROPERTY_NAME));
		model.set(VERSION_PROPERTY_NAME, pModel.getLong(VERSION_PROPERTY_NAME) + 1);
		model.update();
		return model;
	}

	/**
	 * 移除实体对象
	 * 
	 * @param entity
	 *            实体对象
	 */
	public void remove(M model) {
		if (model != null) {
			model.delete();
		}
	}


	/**
	 * 查找实体对象集合
	 * 
	 * @param criteriaQuery
	 *            查询条件
	 * @param first
	 *            起始记录
	 * @param count
	 *            数量
	 * @param filters
	 *            筛选
	 * @param orders
	 *            排序
	 * @return 实体对象集合
	 */
	protected List<M> findList(String sql, Integer first, Integer count, List<Filter> filters, List<Order> orders, List<Object> params) {
		Assert.notNull(sql);

		if (params == null) {
			params = new ArrayList<Object>();
		}
		
		// 解析Filter过滤条件
		String filterBuilder = convertFilter(filters);
		if (StringUtils.isNotEmpty(filterBuilder)) {
			sql += filterBuilder;
		}

		String orderBuilder = convertOrder(orders);
		if (StringUtils.isEmpty(orderBuilder)) {
			Method method = ReflectionUtils.getDeclaredMethod(getModelClass(), "getOrders") ;
			if (method != null) {
				sql += " ORDER BY " + ORDER_PROPERTY_NAME + " ASC ";
			} else {
				sql += " ORDER BY " + CREATED_DATE_PROPERTY_NAME + " DESC ";
			}
		} else {
			sql += orderBuilder;
		}

		if (first != null && count != null) {
			sql += " LIMIT ?, ?";
			params.add(first);
			params.add(count);
		}
		if (first == null && count != null) {
			sql += " LIMIT 0, ?";
			params.add(count);
		}
		return modelManager.find(sql, params.toArray());
	}

	/**
	 * 查找实体对象集合
	 * 
	 * @param criteriaQuery
	 *            查询条件
	 * @param first
	 *            起始记录
	 * @param count
	 *            数量
	 * @return 实体对象集合
	 */
	protected List<M> findList(String sql, Integer first, Integer count, List<Object> params) {
		return findList(sql, first, count, null, null, params);
	}

	/**
	 * 查找实体对象集合
	 * 
	 * @param criteriaQuery
	 *            查询条件
	 * @return 实体对象集合
	 */
	protected List<M> findList(String sql, List<Object> params) {
		return findList(sql, null, null, null, null, params);
	}
	
	
	/**
	 * 查找实体对象分页
	 * 
	 * @param criteriaQuery
	 *            查询条件
	 * @param pageable
	 *            分页信息
	 * @return 实体对象分页
	 */
	protected Page<M> findPage(String sqlExceptSelect, Pageable pageable, List<Object> params) {
		Assert.notNull(sqlExceptSelect);
		
		if (pageable == null) {
			pageable = new Pageable();
		}
		if (params == null) {
			params = new ArrayList<Object>();
		}

		String select = "SELECT * ";
		// 搜索属性、搜索值
		String searchProperty = pageable.getSearchProperty();
		String searchValue = pageable.getSearchValue();
		if (StringUtils.isNotEmpty(searchProperty) && StringUtils.isNotEmpty(searchValue)) {
			sqlExceptSelect += " AND " + searchProperty + " LIKE ? ";
			params.add("%" + searchValue + "%");
		}
		// 解析Filter过滤条件
		String filterBuilder = convertFilter(pageable.getFilters());
		if (StringUtils.isEmpty(filterBuilder)) {
			sqlExceptSelect += filterBuilder;
		}
		
		// 解析Pageable.Order中的单个排序
		String orderProperty = com.jfinalshop.util.StringUtils.camelToUnderline(pageable.getOrderProperty());
		Order.Direction orderDirection = pageable.getOrderDirection();
		if (StringUtils.isNotEmpty(orderProperty) && orderDirection != null) {
			switch (orderDirection) {
			case asc:
				sqlExceptSelect += " ORDER BY " + orderProperty + " ASC ";
				break;
			case desc:
				sqlExceptSelect += " ORDER BY " + orderProperty + " DESC ";
				break;
			default:
				break;
			}
		} else {
			// 解析Pageable.Orders中的多个排序
			String orderBuilder = convertOrder(pageable.getOrders());
			if (StringUtils.isEmpty(orderBuilder)) {
				sqlExceptSelect += " ORDER BY " + LAST_MODIFIED_DATE_PROPERTY_NAME + " DESC ";
			} else {
				sqlExceptSelect += orderBuilder;
			}
		}
		return modelManager.paginate(pageable.getPageNumber(), pageable.getPageSize(), select, sqlExceptSelect, params.toArray());
	}
	
	/**
	 * 查询实体对象数量
	 * 
	 * @param criteriaQuery
	 *            查询条件
	 * @param filters
	 *            筛选
	 * @return 实体对象数量
	 */
	protected Long count(String sql, List<Filter> filters, List<Object> params) {
		Assert.notNull(sql);
		if (params == null) {
			params = new ArrayList<Object>();
		}
		// 解析Filter过滤条件
		String filterBuilder = convertFilter(filters);
		if (StringUtils.isNotEmpty(filterBuilder)) {
			sql += filterBuilder;
		}
		Long result = Db.queryLong(sql, params.toArray());
		return result == null ? 0L : result;
	}

	/**
	 * 查询实体对象数量
	 * 
	 * @param criteriaQuery
	 *            查询条件
	 * @return 实体对象数量
	 */
	protected Long count(String sql, List<Object> params) {
		return count(sql, null, params);
	}
	
	/**
	 * 解析Filter过滤条件
	 * 
	 * @param root
	 *            Root
	 * @param filters
	 *            筛选
	 * @return Predicate
	 */
	private String convertFilter(List<Filter> filters) {
		String sqlBuilder = ""; 
		if (CollectionUtils.isEmpty(filters)) {
			return "";
		}
		for (Filter filter : filters) {
			if (filter == null) {
				continue;
			}
			String property = filter.getProperty();
			Filter.Operator operator = filter.getOperator();
			Object value = filter.getValue();
			switch (operator) {
			case eq:
				if (value != null) {
					sqlBuilder += " AND " + property + " = " + value;
				} else {
					sqlBuilder += " AND " + property + " IS NULL ";
				}
				break;
			case ne:
				if (value != null) {
					sqlBuilder += " AND " + property + " != " + value;
				} else {
					sqlBuilder += " AND " + property + " IS NOT NULL ";
				}
				break;
			case gt:
				if (value instanceof Number) {
					sqlBuilder += " AND " + property + " > " + (Number) value;
				}
				break;
			case lt:
				if (value instanceof Number) {
					sqlBuilder += " AND " + property + " < " + (Number) value;
				}
				break;
			case ge:
				if (value instanceof Number) {
					sqlBuilder += " AND " + property + " >= " + (Number) value;
				}
				break;
			case le:
				if (value instanceof Number) {
					sqlBuilder += " AND " + property + " <= " + (Number) value;
				}
				break;
			case like:
				if (value instanceof String) {
					sqlBuilder += " AND " + property + " LIKE '%" + (String) value+ "%'";
				}
				break;
			case in:
				sqlBuilder += " AND " + property + " IN (" + value + ")";
				break;
			case isNull:
				sqlBuilder += " AND " + property + " IS NULL";
				break;
			case isNotNull:
				sqlBuilder += " AND " + property + " IS NOT NULL";
				break;
			default:
				break;
			}
		}
		return sqlBuilder;
	}
	
	/**
	 * 解析Orders中的多个排序
	 * @param orders
	 * @return
	 */
	private String convertOrder(List<Order> orders) {
		String sql = "";
		if (CollectionUtils.isNotEmpty(orders)) {
			sql = " ORDER BY ";
			for (Order order : orders) {
				String property = order.getProperty();
				Order.Direction direction = order.getDirection();
				switch (direction) {
				case asc:
					sql += property + " ASC, ";
					break;
				case desc:
					sql += property + " DESC, ";
					break;
				default:
					break;
				}
			}
			sql = StringUtils.substringBeforeLast(sql, ",");
		}
		return sql;
	}
	
}