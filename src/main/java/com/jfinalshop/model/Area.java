package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinalshop.model.base.BaseArea;

/**
 * Model - 地区
 * 
 */
public class Area extends BaseArea<Area> {
	private static final long serialVersionUID = -8282057678850304465L;
	public static final Area dao = new Area().dao();
	
	/**
	 * 树路径分隔符
	 */
	public static final String TREE_PATH_SEPARATOR = ",";
	
	/**
	 * 上级地区
	 */
	private Area parent;

	/**
	 * 下级地区
	 */
	private List<Area> children = new ArrayList<Area>();

	/**
	 * 会员
	 */
	private List<Member> members = new ArrayList<Member>();

	/**
	 * 收货地址
	 */
	private List<Receiver> receivers = new ArrayList<Receiver>();

	/**
	 * 订单
	 */
	private List<Order> orders = new ArrayList<Order>();

	/**
	 * 发货点
	 */
	private List<DeliveryCenter> deliveryCenters = new ArrayList<DeliveryCenter>();

	/**
	 * 地区运费配置
	 */
	private List<AreaFreightConfig> areaFreightConfigs = new ArrayList<AreaFreightConfig>();
	
	/**
	 * 获取上级地区
	 * 
	 * @return 上级地区
	 */
	public Area getParent() {
		if (parent == null) {
			parent = findById(getParentId());
		}
		return parent;
	}

	/**
	 * 设置上级地区
	 * 
	 * @param parent
	 *            上级地区
	 */
	public void setParent(Area parent) {
		this.parent = parent;
	}

	/**
	 * 获取下级地区
	 * 
	 * @return 下级地区
	 */
	public List<Area> getChildren() {
		if (CollectionUtils.isEmpty(children)) {
			String sql = "SELECT * FROM `area` WHERE parent_id = ? ORDER BY orders ASC";
			children = Area.dao.find(sql, getId());
		}
		return children;
	}

	/**
	 * 设置下级地区
	 * 
	 * @param children
	 *            下级地区
	 */
	public void setChildren(List<Area> children) {
		this.children = children;
	}

	/**
	 * 获取会员
	 * 
	 * @return 会员
	 */
	public List<Member> getMembers() {
		if (CollectionUtils.isEmpty(members)) {
			String sql = "SELECT * FROM `member`  WHERE `area_id` = ?";
			members = Member.dao.find(sql, getId());
		}
		return members;
	}

	/**
	 * 设置会员
	 * 
	 * @param members
	 *            会员
	 */
	public void setMembers(List<Member> members) {
		this.members = members;
	}

	/**
	 * 获取收货地址
	 * 
	 * @return 收货地址
	 */
	public List<Receiver> getReceivers() {
		if (CollectionUtils.isEmpty(receivers)) {
			String sql = "SELECT * FROM `receiver`  WHERE `area_id` = ?";
			receivers = Receiver.dao.find(sql, getId());
		}
		return receivers;
	}

	/**
	 * 设置收货地址
	 * 
	 * @param receivers
	 *            收货地址
	 */
	public void setReceivers(List<Receiver> receivers) {
		this.receivers = receivers;
	}

	/**
	 * 获取订单
	 * 
	 * @return 订单
	 */
	public List<Order> getOrder() {
		if (CollectionUtils.isEmpty(orders)) {
			String sql = "SELECT * FROM `order`  WHERE `area_id` = ?";
			orders = Order.dao.find(sql, getId());
		}
		return orders;
	}

	/**
	 * 设置订单
	 * 
	 * @param orders
	 *            订单
	 */
	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	/**
	 * 获取发货点
	 * 
	 * @return 发货点
	 */
	public List<DeliveryCenter> getDeliveryCenters() {
		if (CollectionUtils.isEmpty(deliveryCenters)) {
			String sql = "SELECT * FROM `delivery_center`  WHERE `area_id` = ?";
			deliveryCenters = DeliveryCenter.dao.find(sql, getId());
		}
		return deliveryCenters;
	}

	/**
	 * 设置发货点
	 * 
	 * @param deliveryCenters
	 *            发货点
	 */
	public void setDeliveryCenters(List<DeliveryCenter> deliveryCenters) {
		this.deliveryCenters = deliveryCenters;
	}

	/**
	 * 获取地区运费配置
	 * 
	 * @return 地区运费配置
	 */
	public List<AreaFreightConfig> getAreaFreightConfigs() {
		if (CollectionUtils.isEmpty(deliveryCenters)) {
			String sql = "SELECT * FROM `area_freight_config` WHERE `area_id` = ?";
			areaFreightConfigs = AreaFreightConfig.dao.find(sql, getId());
		}
		return areaFreightConfigs;
	}

	/**
	 * 设置地区运费配置
	 * 
	 * @param areaFreightConfigs
	 *            地区运费配置
	 */
	public void setAreaFreightConfigs(List<AreaFreightConfig> areaFreightConfigs) {
		this.areaFreightConfigs = areaFreightConfigs;
	}

	/**
	 * 获取所有上级地区ID
	 * 
	 * @return 所有上级地区ID
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
	 * 获取所有上级地区
	 * 
	 * @return 所有上级地区
	 */
	public List<Area> getParents() {
		List<Area> parents = new ArrayList<>();
		recursiveParents(parents, this);
		return parents;
	}

	/**
	 * 递归上级地区
	 * 
	 * @param parents
	 *            上级地区
	 * @param area
	 *            地区
	 */
	private void recursiveParents(List<Area> parents, Area area) {
		if (area == null) {
			return;
		}
		Area parent = area.getParent();
		if (parent != null) {
			parents.add(0, parent);
			recursiveParents(parents, parent);
		}
	}

	/**
	 * 删除前处理
	 */
	public void preRemove() {
		List<Member> members = getMembers();
		if (members != null) {
			for (Member member : members) {
				member.setArea(null);
			}
		}
		List<Order> orders = getOrder();
		if (orders != null) {
			for (Order order : orders) {
				order.setArea(null);
			}
		}
		List<DeliveryCenter> deliveryCenters = getDeliveryCenters();
		if (deliveryCenters != null) {
			for (DeliveryCenter deliveryCenter : deliveryCenters) {
				deliveryCenter.setArea(null);
			}
		}
	}
	
}
