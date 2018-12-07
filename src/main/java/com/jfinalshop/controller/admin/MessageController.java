package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import org.apache.commons.lang.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Message;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.MessageService;
import com.jfinalshop.util.IpUtil;

/**
 * Controller - 消息
 * 
 */
@ControllerBind(controllerKey = "/admin/message")
public class MessageController extends BaseController {

	@Inject
	private MessageService messageService;
	@Inject
	private MemberService memberService;

	/**
	 * 检查用户名是否合法
	 */
	@ActionKey("/admin/message/check_username")
	public void checkUsername() {
		String username = getPara("username");
		if (StringUtils.isEmpty(username)) {
			renderJson(false);
			return;
		}
		renderJson(memberService.usernameExists(username));
	}

	/**
	 * 发送
	 */
	public void send() {
		Long draftMessageId = getParaToLong("draftMessageId");
		Message draftMessage = messageService.find(draftMessageId);
		if (draftMessage != null && draftMessage.getIsDraft() && draftMessage.getSender() == null) {
			setAttr("draftMessage", draftMessage);
		}
		render("/admin/message/send.ftl");
	}

	/**
	 * 发送
	 */
	@Before(Tx.class)
	@ActionKey("/admin/message/save_send")
	public void saveSend() {
		Long draftMessageId = getParaToLong("draftMessageId"); 
		String username = getPara("username"); 
		String title = getPara("title"); 
		String content = getPara("content"); 
		Boolean isDraft = getParaToBoolean("isDraft", false);
		
		Message draftMessage = messageService.find(draftMessageId);
		if (draftMessage != null && draftMessage.getIsDraft() && draftMessage.getSender() == null) {
			messageService.delete(draftMessage);
		}
		Member receiver = null;
		if (StringUtils.isNotEmpty(username)) {
			receiver = memberService.findByUsername(username);
			if (receiver == null) {
				setAttr("errorMessage", "接收用户不能为空!");
				render(ERROR_VIEW);
				return;
			}
		}
		Message message = new Message();
		message.setTitle(title);
		message.setContent(content);
		message.setIp(getRequest().getRemoteAddr());
		message.setIsDraft(isDraft);
		message.setSenderRead(true);
		message.setReceiverRead(false);
		message.setSenderDelete(false);
		message.setReceiverDelete(false);
		message.setSender(null);
		message.setReceiver(receiver);
		message.setForMessage(null);
		message.setReplyMessages(null);
		messageService.save(message);
		if (isDraft) {
			addFlashMessage(com.jfinalshop.Message.success("admin.message.saveDraftSuccess"));
			redirect("draft");
		} else {
			addFlashMessage(com.jfinalshop.Message.success("admin.message.sendSuccess"));
			redirect("list");
		}
	}

	/**
	 * 查看
	 */
	public void view() {
		Long id = getParaToLong("id");
		Message message = messageService.find(id);
		if (message == null || message.getIsDraft() || message.getForMessage() != null) {
			setAttr("errorMessage", "消息未找到!");
			render(ERROR_VIEW);
			return;
		}
		if ((message.getSender() != null && message.getReceiver() != null) || (message.getReceiver() == null && message.getReceiverDelete()) || (message.getSender() == null && message.getSenderDelete())) {
			setAttr("errorMessage", "消息未找到或删除!");
			render(ERROR_VIEW);
			return;
		}
		if (message.getReceiver() == null) {
			message.setReceiverRead(true);
		} else {
			message.setSenderRead(true);
		}
		messageService.update(message);
		setAttr("adminMessage", message);
		render("/admin/message/view.ftl");
	}

	/**
	 * 回复
	 */
	@Before(Tx.class)
	public void reply() {
		Long id = getParaToLong("id");
		String content = getPara("content");
		
		Message forMessage = messageService.find(id);
		if (forMessage == null || forMessage.getIsDraft() || forMessage.getForMessage() != null) {
			setAttr("errorMessage", "消息未找到!");
			render(ERROR_VIEW);
			return;
		}
		if ((forMessage.getSender() != null && forMessage.getReceiver() != null) || (forMessage.getReceiver() == null && forMessage.getReceiverDelete()) || (forMessage.getSender() == null && forMessage.getSenderDelete())) {
			setAttr("errorMessage", "消息未找到或没有接收人!");
			render(ERROR_VIEW);
			return;
		}
		Message message = new Message();
		message.setTitle("reply: " + forMessage.getTitle());
		message.setContent(content);
		message.setIp(IpUtil.getIpAddr(getRequest()));
		message.setIsDraft(false);
		message.setSenderRead(true);
		message.setReceiverRead(false);
		message.setSenderDelete(false);
		message.setReceiverDelete(false);
		message.setSender(null);
		message.setReceiverId(forMessage.getReceiver() == null ? forMessage.getSender().getId() : forMessage.getReceiver().getId());
		if ((forMessage.getReceiver() == null && !forMessage.getSenderDelete()) || (forMessage.getSender() == null && !forMessage.getReceiverDelete())) {
			message.setForMessageId(forMessage.getId());
		}
		message.setReplyMessages(null);
		messageService.save(message);

		if (forMessage.getSender() == null) {
			forMessage.setSenderRead(true);
			forMessage.setReceiverRead(false);
		} else {
			forMessage.setSenderRead(false);
			forMessage.setReceiverRead(true);
		}
		messageService.update(forMessage);

		if ((forMessage.getReceiver() == null && !forMessage.getSenderDelete()) || (forMessage.getSender() == null && !forMessage.getReceiverDelete())) {
			addFlashMessage(SUCCESS_MESSAGE);
			redirect("redirect:view?id=" + forMessage.getId());
		} else {
			addFlashMessage(com.jfinalshop.Message.success("admin.message.replySuccess"));
			redirect("list");
		}
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", messageService.findPage(null, pageable));
		render("/admin/message/list.ftl");
	}

	/**
	 * 草稿箱
	 */
	public void draft() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("page", messageService.findDraftPage(null, pageable));
		render("/admin/message/draft.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids != null) {
			for (Long id : ids) {
				messageService.delete(id, null);
			}
		}
		renderJson(SUCCESS_MESSAGE);
	}

}