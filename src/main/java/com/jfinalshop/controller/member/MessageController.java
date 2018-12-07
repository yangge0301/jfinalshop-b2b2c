package com.jfinalshop.controller.member;

import java.util.ArrayList;
import java.util.List;

import net.hasor.core.Inject;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.UnauthorizedException;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.Results;
import com.jfinalshop.interceptor.MobileInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Message;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.MessageService;
import com.jfinalshop.util.IpUtil;
import com.xiaoleilu.hutool.util.CollectionUtil;

/**
 * Controller - 消息
 * 
 */
@ControllerBind(controllerKey = "/member/message")
public class MessageController extends BaseController {

	/**
	 * 每页记录数
	 */
	private static final int PAGE_SIZE = 10;

	@Inject
	private MessageService messageService;
	@Inject
	private MemberService memberService;

	/**
	 * 添加属性
	 */
	public void populateModel() {
		Long draftMessageId = getParaToLong("draftMessageId");
		Long memberMessageId = getParaToLong("memberMessageId");
		Member currentUser = memberService.getCurrentUser();
		
		Message draftMessage = messageService.find(draftMessageId);
		if (draftMessage != null && !currentUser.equals(draftMessage.getSender())) {
			throw new UnauthorizedException();
		}
		setAttr("draftMessage", draftMessage);

		Message memberMessage = messageService.find(memberMessageId);
		if (memberMessage != null && !currentUser.equals(memberMessage.getSender()) && !currentUser.equals(memberMessage.getReceiver())) {
			throw new UnauthorizedException();
		}
		setAttr("memberMessage", memberMessage);
	}

	/**
	 * 检查用户名是否合法
	 */
	@ActionKey("/member/message/check_username")
	public void checkUsername() {
		String username = getPara("username");
		Member currentUser = memberService.getCurrentUser();
		renderJson(StringUtils.isNotEmpty(username) && !StringUtils.equalsIgnoreCase(username, currentUser.getUsername()) && memberService.usernameExists(username));
	}

	/**
	 * 发送
	 */
	@Before(MobileInterceptor.class)
	public void send() {
		Long draftMessageId = getParaToLong("draftMessageId");
		Message draftMessage = messageService.find(draftMessageId);
		if (draftMessage != null && draftMessage.getIsDraft()) {
			setAttr("draftMessage", draftMessage);
		}
		render("/member/message/send.ftl");
	}

	/**
	 * 发送
	 */
	public void submit() {
		Long draftMessageId = getParaToLong("draftMessageId");
		String username = getPara("username");
		String title = getPara("title");
		String content = getPara("content");
		Boolean isDraft = getParaToBoolean("isDraft", false);
		Member currentUser = memberService.getCurrentUser();
		
		Message draftMessage = messageService.find(draftMessageId);
		if (draftMessage != null && draftMessage.getIsDraft()) {
			messageService.delete(draftMessage);
		}
		Member receiver = null;
		if (StringUtils.isNotEmpty(username)) {
			receiver = memberService.findByUsername(username);
			if (currentUser.equals(receiver)) {
				setAttr("errorMessage", "收件人与当前用户相同!");
				render(UNPROCESSABLE_ENTITY_VIEW);
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
		message.setSenderId(currentUser.getId());
		message.setReceiverId(receiver != null ? receiver.getId() : null);
		messageService.save(message);
		if (isDraft) {
			addFlashMessage("member.message.saveDraftSuccess");
			redirect("draft");
		} else {
			addFlashMessage("member.message.sendSuccess");
			redirect("list");
		}
	}

	/**
	 * 查看
	 */
	@Before(MobileInterceptor.class)
	public void view() {
		Long memberMessageId = getParaToLong("memberMessageId");
		Message memberMessage = messageService.find(memberMessageId);
		Member currentUser = memberService.getCurrentUser();
		
		if (memberMessage == null || memberMessage.getIsDraft() || memberMessage.getForMessage() != null) {
			setAttr("errorMessage", "消息为空或是草稿!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if ((currentUser.equals(memberMessage.getReceiver()) && memberMessage.getReceiverDelete()) || (currentUser.equals(memberMessage.getSender()) && memberMessage.getSenderDelete())) {
			setAttr("errorMessage", "当前用与收件人相同或已删除!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (currentUser.equals(memberMessage.getReceiver())) {
			memberMessage.setReceiverRead(true);
		} else {
			memberMessage.setSenderRead(true);
		}
		messageService.update(memberMessage);
		setAttr("memberMessage", memberMessage);
		render("/member/message/view.ftl");
	}

	/**
	 * 回复
	 */
	public void reply() {
		Long memberMessageId = getParaToLong("memberMessageId");
		Message memberMessage = messageService.find(memberMessageId);
		String content = getPara("content");
		Member currentUser = memberService.getCurrentUser();

		if (memberMessage == null || memberMessage.getIsDraft() || memberMessage.getForMessage() != null) {
			setAttr("errorMessage", "消息为空或是草稿!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if ((currentUser.equals(memberMessage.getReceiver()) && memberMessage.getReceiverDelete()) || (currentUser.equals(memberMessage.getSender()) && memberMessage.getSenderDelete())) {
			setAttr("errorMessage", "当前用与收件人相同或已删除!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		Message message = new Message();
		message.setTitle("reply: " + memberMessage.getTitle());
		message.setContent(content);
		message.setIp(IpUtil.getIpAddr(getRequest()));
		message.setIsDraft(false);
		message.setSenderRead(true);
		message.setReceiverRead(false);
		message.setSenderDelete(false);
		message.setReceiverDelete(false);
		message.setSenderId(currentUser.getId());
		if (currentUser.equals(memberMessage.getReceiver())) {
			message.setReceiverId(memberMessage.getSender() != null ? memberMessage.getSender().getId() : null);
		} else {
			message.setReceiverId(memberMessage.getReceiver() != null ? memberMessage.getReceiver().getId() : null);
		}
		if ((currentUser.equals(memberMessage.getReceiver()) && !memberMessage.getSenderDelete()) || (currentUser.equals(memberMessage.getSender()) && !memberMessage.getReceiverDelete())) {
			message.setForMessageId(memberMessage.getId());
		}
		messageService.save(message);

		if (currentUser.equals(memberMessage.getSender())) {
			memberMessage.setSenderRead(true);
			memberMessage.setReceiverRead(false);
		} else {
			memberMessage.setSenderRead(false);
			memberMessage.setReceiverRead(true);
		}
		messageService.update(memberMessage);

		if ((currentUser.equals(memberMessage.getReceiver()) && !memberMessage.getSenderDelete()) || (currentUser.equals(memberMessage.getSender()) && !memberMessage.getReceiverDelete())) {
			addFlashMessage(SUCCESS_MESSAGE);
			redirect("view?memberMessageId=" + memberMessage.getId());
		} else {
			addFlashMessage("member.message.replySuccess");
			redirect("list");
		}
	}

	/**
	 * 列表
	 */
	@Before(MobileInterceptor.class)
	public void list() {
		Integer pageNumber = getParaToInt("pageNumber");
		Member currentUser = memberService.getCurrentUser();
		
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("pageable", pageable);
		setAttr("page", messageService.findPage(currentUser, pageable));
		render("/member/message/list.ftl");
	}

	/**
	 * 列表
	 */
	@ActionKey("/member/message/m_list")
	public void mList() {
		Integer pageNumber = getParaToInt("pageNumber", 1);
		Member currentUser = memberService.getCurrentUser();
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		Page<Message> pages = messageService.findPage(currentUser, pageable);
		
		List<Message> messages = new ArrayList<Message>();
		if (CollectionUtil.isNotEmpty(pages.getList())) {
			for (Message message : pages.getList()) {
				message.put("sender", message.getSender());
				messages.add(message);
			}
		}
		renderJson(messages);
	}

	/**
	 * 草稿箱
	 */
	@Before(MobileInterceptor.class)
	public void draft() {
		Integer pageNumber = getParaToInt("pageNumber");
		Member currentUser = memberService.getCurrentUser();
		
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("page", messageService.findDraftPage(currentUser, pageable));
		render("/member/message/draft.ftl");
	}

	/**
	 * 草稿箱
	 */
	@ActionKey("/member/message/m_draft")
	public void mDraft() {
		Integer pageNumber = getParaToInt("pageNumber", 1);
		Member currentUser = memberService.getCurrentUser();
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		Page<Message> pages = messageService.findDraftPage(currentUser, pageable);
		
		List<Message> messages = new ArrayList<Message>();
		if (CollectionUtil.isNotEmpty(pages.getList())) {
			for (Message message : pages.getList()) {
				message.put("sender", message.getSender());
				messages.add(message);
			}
		}
		renderJson(messages);
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long messageId = getParaToLong("messageId"); 
		Member currentUser = memberService.getCurrentUser();
		
		messageService.delete(messageId, currentUser);
		renderJson(Results.OK);
	}

}