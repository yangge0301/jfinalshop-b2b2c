package com.jfinalshop.service;

import java.util.List;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import org.apache.commons.lang.BooleanUtils;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.dao.AreaDao;
import com.jfinalshop.dao.ReceiverDao;
import com.jfinalshop.dao.StoreDao;
import com.jfinalshop.model.Area;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Receiver;
import com.jfinalshop.model.Store;
import com.jfinalshop.util.Assert;

/**
 * Service - 收货地址
 * 
 */
@Singleton
public class ReceiverService extends BaseService<Receiver> {

	/**
	 * 构造方法
	 */
	public ReceiverService() {
		super(Receiver.class);
	}
	
	@Inject
	private ReceiverDao receiverDao;
	@Inject
	private AreaDao areaDao;
	@Inject
	private StoreDao storeDao;
	
	/**
	 * 查找默认收货地址
	 * 
	 * @param member
	 *            会员
	 * @return 默认收货地址，若不存在则返回最新收货地址
	 */
	public Receiver findDefault(Member member) {
		return receiverDao.findDefault(member);
	}

	/**
	 * 查找收货地址列表
	 * 
	 * @param member
	 *            会员
	 * @return 收货地址
	 */
	public List<Receiver> findList(Member member) {
		return receiverDao.findList(member);
	}

	/**
	 * 查找收货地址分页
	 * 
	 * @param member
	 *            会员
	 * @param pageable
	 *            分页信息
	 * @return 收货地址分页
	 */
	public Page<Receiver> findPage(Member member, Pageable pageable) {
		return receiverDao.findPage(member, pageable);
	}

	/**
	 * 内部用户添加默认地址
	 * 
	 */
	public void save(Member member) {
		Assert.notNull(member);
		
		Area area = areaDao.find(1989L);
		Store store = member.getStore();
		if (store != null && area != null) {
			Receiver receiver = new Receiver();
			receiver.setAddress(store.getAddress());
			receiver.setAreaName(area.getFullName());
			receiver.setConsignee(member.getName());
			receiver.setIsDefault(true);
			receiver.setPhone(store.getMobile());
			receiver.setAreaId(area.getId());
			receiver.setMemberId(member.getId());
			if (BooleanUtils.isTrue(receiver.getIsDefault()) && receiver.getMember() != null) {
				receiverDao.clearDefault(receiver.getMember());
			}
			super.save(receiver);
		}
	}
	
	@Override
	public Receiver save(Receiver receiver) {
		Assert.notNull(receiver);

		if (BooleanUtils.isTrue(receiver.getIsDefault()) && receiver.getMember() != null) {
			receiverDao.clearDefault(receiver.getMember());
		}
		return super.save(receiver);
	}
	
	@Override
	public Receiver update(Receiver receiver) {
		Assert.notNull(receiver);

		Receiver pReceiver = super.update(receiver);
		if (BooleanUtils.isTrue(pReceiver.getIsDefault()) && pReceiver.getMember() != null) {
			receiverDao.clearDefault(pReceiver.getMember(), pReceiver);
		}
		return pReceiver;
	}
}