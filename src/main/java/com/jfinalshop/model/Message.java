package com.jfinalshop.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.jfinalshop.model.base.BaseMessage;

/**
 * Model - 消息
 * 
 */
public class Message extends BaseMessage<Message> {
	private static final long serialVersionUID = -2238025904860160034L;
	public static final Message dao = new Message().dao();
	
	/**
	 * 发件人
	 */
	private Member sender;

	/**
	 * 收件人
	 */
	private Member receiver;

	/**
	 * 原消息
	 */
	private Message forMessage;

	/**
	 * 回复消息
	 */
	private List<Message> replyMessages = new ArrayList<Message>();
	
	/**
	 * 获取发件人
	 * 
	 * @return 发件人
	 */
	public Member getSender() {
		if (sender == null) {
			sender = Member.dao.findById(getSenderId());
		}
		return sender;
	}

	/**
	 * 设置发件人
	 * 
	 * @param sender
	 *            发件人
	 */
	public void setSender(Member sender) {
		this.sender = sender;
	}

	/**
	 * 获取收件人
	 * 
	 * @return 收件人
	 */
	public Member getReceiver() {
		if (receiver == null) {
			receiver = Member.dao.findById(getReceiverId());
		}
		return receiver;
	}

	/**
	 * 设置收件人
	 * 
	 * @param receiver
	 *            收件人
	 */
	public void setReceiver(Member receiver) {
		this.receiver = receiver;
	}

	/**
	 * 获取原消息
	 * 
	 * @return 原消息
	 */
	public Message getForMessage() {
		if (forMessage == null) {
			forMessage = Message.dao.findById(getForMessageId());
		}
		return forMessage;
	}

	/**
	 * 设置原消息
	 * 
	 * @param forMessage
	 *            原消息
	 */
	public void setForMessage(Message forMessage) {
		this.forMessage = forMessage;
	}

	/**
	 * 获取回复消息
	 * 
	 * @return 回复消息
	 */
	public List<Message> getReplyMessages() {
		if (CollectionUtils.isEmpty(replyMessages)) {
			String sql = "SELECT * FROM `message` WHERE for_message_id = ?";
			replyMessages = Message.dao.find(sql, getId());
		}
		return replyMessages;
	}

	/**
	 * 设置回复消息
	 * 
	 * @param replyMessages
	 *            回复消息
	 */
	public void setReplyMessages(List<Message> replyMessages) {
		this.replyMessages = replyMessages;
	}

}
