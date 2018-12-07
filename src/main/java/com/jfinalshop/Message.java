package com.jfinalshop;

import java.io.Serializable;

import com.jfinal.i18n.I18n;


/**
 * 消息
 * 
 */
public class Message implements Serializable {

	private static final long serialVersionUID = -3995717697244498765L;

	/**
	 * 类型
	 */
	public enum Type {

		/**
		 * 成功
		 */
		success,

		/**
		 * 警告
		 */
		warn,

		/**
		 * 错误
		 */
		error
	}

	/**
	 * 类型
	 */
	private Message.Type type;

	/**
	 * 内容
	 */
	private String content;
	
	/**
	 * 构造方法
	 */
	public Message() {
	}

	/**
	 * 构造方法
	 * 
	 * @param type
	 *            类型
	 * @param content
	 *            内容
	 */
	public Message(Message.Type type, String content) {
		this.type = type;
		this.content = content;
	}

	/**
	 * 构造方法
	 * 
	 * @param type
	 *            类型
	 * @param content
	 *            内容
	 * @param args
	 *            参数
	 */
	public Message(Message.Type type, String content, Object... args) {
		this.type = type;
		this.content = I18n.use().format(content, args);
	}

	/**
	 * 返回成功消息
	 * 
	 * @param content
	 *            内容
	 * @param args
	 *            参数
	 * @return 成功消息
	 */
	public static Message success(String content, Object... args) {
		return new Message(Message.Type.success, content, args);
	}

	/**
	 * 返回警告消息
	 * 
	 * @param content
	 *            内容
	 * @param args
	 *            参数
	 * @return 警告消息
	 */
	public static Message warn(String content, Object... args) {
		return new Message(Message.Type.warn, content, args);
	}

	/**
	 * 返回错误消息
	 * 
	 * @param content
	 *            内容
	 * @param args
	 *            参数
	 * @return 错误消息
	 */
	public static Message error(String content, Object... args) {
		return new Message(Message.Type.error, content, args);
	}

	/**
	 * 获取类型
	 * 
	 * @return 类型
	 */
	public Message.Type getType() {
		return type;
	}

	/**
	 * 设置类型
	 * 
	 * @param type
	 *            类型
	 */
	public void setType(Message.Type type) {
		this.type = type;
	}

	/**
	 * 获取内容
	 * 
	 * @return 内容
	 */
	public String getContent() {
		return content;
	}

	/**
	 * 设置内容
	 * 
	 * @param content
	 *            内容
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * 重写toString方法
	 * 
	 * @return 字符串
	 */
	@Override
	public String toString() {
		return I18n.use().get(content);
	}

}