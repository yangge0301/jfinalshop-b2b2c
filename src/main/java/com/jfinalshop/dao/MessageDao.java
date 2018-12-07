package com.jfinalshop.dao;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Message;

/**
 * Dao - 消息
 * 
 */
public class MessageDao extends BaseDao<Message> {

	/**
	 * 构造方法
	 */
	public MessageDao() {
		super(Message.class);
	}
	
	/**
	 * 查找消息分页
	 * 
	 * @param member
	 *            会员，null表示管理员
	 * @param pageable
	 *            分页信息
	 * @return 消息分页
	 */
	public Page<Message> findPage(Member member, Pageable pageable) {
		String sqlExceptSelect = "FROM message WHERE for_message_id IS NULL AND is_draft = FALSE ";
		List<Object> params = new ArrayList<Object>();
		
		if (member != null) {
			sqlExceptSelect += "AND (sender_id = ? AND sender_delete = FALSE) OR (receiver_id = ? AND receiver_delete = FALSE) ";
			params.add(member.getId());
			params.add(member.getId());
		} else {
			sqlExceptSelect += "AND (sender_id IS NULL  AND sender_delete = FALSE) OR (receiver_id IS NULL AND receiver_delete = FALSE) ";
		}
		return super.findPage(sqlExceptSelect, pageable, params);

	}

	/**
	 * 查找草稿分页
	 * 
	 * @param sender
	 *            发件人，null表示管理员
	 * @param pageable
	 *            分页信息
	 * @return 草稿分页
	 */
	public Page<Message> findDraftPage(Member sender, Pageable pageable) {
		String sqlExceptSelect = "FROM message WHERE for_message_id IS NULL AND is_draft = TRUE ";
		List<Object> params = new ArrayList<Object>();
		
		if (sender != null) {
			sqlExceptSelect += "AND sender_id = ?";
			params.add(sender.getId());
		} else {
			sqlExceptSelect += "AND sender_id IS NULL ";
		}
		return super.findPage(sqlExceptSelect, pageable, params);
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
		String sql = "SELECT COUNT(1) FROM message WHERE for_message_id IS NULL AND is_draft = FALSE ";
		List<Object> params = new ArrayList<Object>();
		
		if (member != null) {
			if (read != null) {
				sql += "AND (sender_id = ? AND sender_delete = FALSE AND sender_read = ?) OR (receiver_id = ? AND receiver_delete = FALSE AND receiver_read = ?) ";
				params.add(member.getId());
				params.add(read);
				params.add(member.getId());
				params.add(read);
			} else {
				sql += "AND (sender_id = ? AND sender_delete = FALSE) OR (receiver_id = ? AND receiver_delete = FALSE) ";
				params.add(member.getId());
				params.add(member.getId());
			}
		} else {
			if (read != null) {
				sql += "AND (sender_id IS NULL AND sender_delete = FALSE AND sender_read = ?) OR (receiver_id IS NULL AND receiver_delete = FALSE AND receiver_read = ?) ";
				params.add(read);
				params.add(read);
			} else {
				sql += "AND (sender_id IS NULL AND sender_delete = FALSE) OR (receiver_id IS NULL AND receiver_delete = FALSE) ";
			}
		}
		return super.count(sql, params);
	}

	/**
	 * 删除消息
	 * 
	 * @param id
	 *            ID
	 * @param member
	 *            执行人，null表示管理员
	 */
	public void remove(Long id, Member member) {
		Message message = super.find(id);
		if (message == null || message.getForMessage() != null) {
			return;
		}
		if ((member != null && member.equals(message.getReceiver())) || (member == null && message.getReceiver() == null)) {
			if (!message.getIsDraft()) {
				if (message.getSenderDelete()) {
					super.remove(message);
				} else {
					message.setReceiverDelete(true);
					super.update(message);
				}
			}
		} else if ((member != null && member.equals(message.getSender())) || (member == null && message.getSender() == null)) {
			if (message.getIsDraft()) {
				super.remove(message);
			} else {
				if (message.getReceiverDelete()) {
					super.remove(message);
				} else {
					message.setSenderDelete(true);
					super.update(message);
				}
			}
		}
	}

}