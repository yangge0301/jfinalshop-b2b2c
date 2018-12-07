package com.jfinalshop.controller.admin;

import java.util.HashMap;
import java.util.Map;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Message;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.PointLog;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.PointLogService;

/**
 * Controller - 积分
 * 
 */
@ControllerBind(controllerKey = "/admin/point")
public class PointController extends BaseController {

	@Inject
	private PointLogService pointLogService;
	@Inject
	private MemberService memberService;

	/**
	 * 检查会员
	 */
	@ActionKey("/admin/point/check_member")
	public void checkMember() {
		String username = getPara("username");
		Map<String, Object> data = new HashMap<>();
		Member member = memberService.findByUsername(username);
		if (member == null) {
			data.put("message", Message.warn("admin.point.memberNotExist"));
			renderJson(data);
			return;
		}
		data.put("message", SUCCESS_MESSAGE);
		data.put("point", member.getPoint());
		renderJson(data);
	}

	/**
	 * 调整
	 */
	public void adjust() {
		render("/admin/point/adjust.ftl"); ;
	}

	/**
	 * 调整
	 */
	@Before(Tx.class)
	@ActionKey("/admin/point/save_adjust")
	public void saveAdjust() {
		String username = getPara("username");
		long amount = getParaToLong("amount");
		String memo = getPara("memo");
		
		Member member = memberService.findByUsername(username);
		if (member == null) {
			setAttr("errorMessage", "会员不能为空!");
			render(ERROR_VIEW);
			return;
		}
		if (amount == 0) {
			setAttr("errorMessage", "不能等于0!");
			render(ERROR_VIEW);
			return;
		}
		if (member.getPoint() == null || member.getPoint() + amount < 0) {
			setAttr("errorMessage", "当前积分加调整不能少于0!");
			render(ERROR_VIEW);
			return;
		}
		memberService.addPoint(member, amount, PointLog.Type.adjustment, memo);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("log");
	}

	/**
	 * 记录
	 */
	public void log() {
		Long memberId = getParaToLong("memberId");
		Pageable pageable = getBean(Pageable.class);
		
		Member member = memberService.find(memberId);
		if (member != null) {
			setAttr("member", member);
			setAttr("page", pointLogService.findPage(member, pageable));
		} else {
			setAttr("page", pointLogService.findPage(pageable));
		}
		setAttr("pageable", pageable);
		render("/admin/point/log.ftl");
	}

}