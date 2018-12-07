package com.jfinalshop.controller.member;

import net.hasor.core.Inject;

import org.apache.shiro.authz.UnauthorizedException;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.Results;
import com.jfinalshop.interceptor.MobileInterceptor;
import com.jfinalshop.model.Area;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Receiver;
import com.jfinalshop.service.AreaService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.ReceiverService;

/**
 * Controller - 收货地址
 * 
 */
@ControllerBind(controllerKey = "/member/receiver")
public class ReceiverController extends BaseController {

	/**
	 * 每页记录数
	 */
	private static final int PAGE_SIZE = 10;

	@Inject
	private AreaService areaService;
	@Inject
	private ReceiverService receiverService;
	@Inject
	private MemberService memberService;

	/**
	 * 添加属性
	 */
	public void populateModel() {
		Long receiverId = getParaToLong("receiverId");
		Member currentUser = memberService.getCurrentUser();
		
		Receiver receiver = receiverService.find(receiverId);
		if (receiver != null && !currentUser.equals(receiver.getMember())) {
			throw new UnauthorizedException();
		}
		setAttr("receiver", receiver);
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
		setAttr("page", receiverService.findPage(currentUser, pageable));
		render("/member/receiver/list.ftl");
	}

	/**
	 * 添加
	 */
	@Before(MobileInterceptor.class)
	public void add() {
		Member currentUser = memberService.getCurrentUser();
		if (Receiver.MAX_RECEIVER_COUNT != null && currentUser.getReceivers().size() >= Receiver.MAX_RECEIVER_COUNT) {
			addFlashMessage("member.receiver.addCountNotAllowed", Receiver.MAX_RECEIVER_COUNT);
			redirect("list");
		}
		render("/member/receiver/add.ftl");
	}

	/**
	 * 保存
	 */
	public void save() {
		Receiver receiver = getModel(Receiver.class);
		Long areaId = getParaToLong("areaId");
		Boolean isDefault = getParaToBoolean("isDefault", false);
		Member currentUser = memberService.getCurrentUser();
		
		Area area = areaService.find(areaId);
		if (area != null) {
			receiver.setAreaId(area.getId());
			receiver.setAreaName(area.getFullName());
		}
		if (Receiver.MAX_RECEIVER_COUNT != null && currentUser.getReceivers().size() >= Receiver.MAX_RECEIVER_COUNT) {
			setAttr("errorMessage", "超收货地址最大保存数!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		receiver.setIsDefault(isDefault);
		receiver.setMemberId(currentUser.getId());
		receiverService.save(receiver);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 编辑
	 */
	@Before(MobileInterceptor.class)
	public void edit() {
		Long receiverId = getParaToLong("receiverId");
		Boolean isDefault = getParaToBoolean("isDefault", false);
		
		Receiver receiver = receiverService.find(receiverId);
		if (receiver == null) {
			setAttr("errorMessage", "不存在收货地址!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		receiver.setIsDefault(isDefault);
		setAttr("receiver", receiver);
		render("/member/receiver/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		Receiver receiver = getModel(Receiver.class);
		Long areaId = getParaToLong("areaId");
		
		if (receiver == null) {
			setAttr("errorMessage", "不存在收货地址!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		Area area = areaService.find(areaId);
		if (area != null) {
			receiver.setAreaId(area.getId());
			receiver.setAreaName(area.getFullName());
		}
		receiverService.update(receiver);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long receiverId = getParaToLong("receiverId");
		
		Receiver receiver = receiverService.find(receiverId);
		if (receiver == null) {
			Results.notFound(getResponse(), Results.DEFAULT_NOT_FOUND_MESSAGE);
			return;
		}

		receiverService.delete(receiver);
		renderJson(Results.OK);
	}

}