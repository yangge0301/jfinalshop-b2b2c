package com.jfinalshop.service;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.dao.MessageDao;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Message;

/**
 * Service - 消息
 * 
 */
@Singleton
public class MessageService extends BaseService<Message> {

	/**
	 * 构造方法
	 */
	public MessageService() {
		super(Message.class);
	}
	
	@Inject
	private MessageDao messageDao;
	
	/**
	 * 查找消息分页
	 * 
	 * @param member
	 *            会员,null表示管理员
	 * @param pageable
	 *            分页信息
	 * @return 消息分页
	 */
	public Page<Message> findPage(Member member, Pageable pageable) {
		return messageDao.findPage(member, pageable);
	}

	/**
	 * 查找草稿分页
	 * 
	 * @param sender
	 *            发件人,null表示管理员
	 * @param pageable
	 *            分页信息
	 * @return 草稿分页
	 */
	public Page<Message> findDraftPage(Member sender, Pageable pageable) {
		return messageDao.findDraftPage(sender, pageable);
	}

	/**
	 * 查找消息数量
	 * 
	 * @param member
	 *            会员，null表示管理员
	 * @param read
	 *            是否已读
	 * @return 消息数量，不包含草稿
	 */
	public Long count(Member member, Boolean read) {
		return messageDao.count(member, read);
	}

	/**
	 * 删除消息
	 * 
	 * @param id
	 *            ID
	 * @param member
	 *            执行人,null表示管理员
	 */
	public void delete(Long id, Member member) {
		messageDao.remove(id, member);
	}

}