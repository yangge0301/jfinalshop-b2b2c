package com.jfinalshop.controller.admin;

import java.math.BigDecimal;
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
import com.jfinalshop.model.MemberDepositLog;
import com.jfinalshop.service.MemberDepositLogService;
import com.jfinalshop.service.MemberService;

/**
 * Controller - 会员预存款
 * 
 */
@ControllerBind(controllerKey = "/admin/member_deposit")
public class MemberDepositController extends BaseController {

	@Inject
	private MemberDepositLogService memberDepositLogService;
	@Inject
	private MemberService memberService;

	/**
	 * 检查会员
	 */
	@ActionKey("/admin/member_deposit/check_member")
	public void checkMember() {
		String username = getPara("username");
		Map<String, Object> data = new HashMap<>();
		Member member = memberService.findByUsername(username);
		if (member == null) {
			data.put("message", Message.warn("admin.memberDeposit.memberNotExist"));
			renderJson(data);
			return;
		}
		data.put("message", SUCCESS_MESSAGE);
		data.put("balance", member.getBalance());
		renderJson(data);
	}

	/**
	 * 调整
	 */
	public void adjust() {
		render("/admin/member_deposit/adjust.ftl");
	}

	/**
	 * 调整
	 */
	@Before(Tx.class)
	@ActionKey("/admin/member_deposit/save_adjust")
	public void saveAdjust() {
		String username = getPara("username");
		BigDecimal amount = new BigDecimal(getPara("amount", "0"));
		String memo = getPara("memo");
		
		Member member = memberService.findByUsername(username);
		if (member == null) {
			setAttr("errorMessage", "会员不能为空!");
			render(ERROR_VIEW);
			return;
		}
		if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
			setAttr("errorMessage", "金额不能为0!");
			render(ERROR_VIEW);
			return;
		}
		if (member.getBalance() == null || member.getBalance().add(amount).compareTo(BigDecimal.ZERO) < 0) {
			setAttr("errorMessage", "金额加余额不能为0!");
			render(ERROR_VIEW);
			return;
		}
		memberService.addBalance(member, amount, MemberDepositLog.Type.adjustment, memo);
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
			setAttr("page", memberDepositLogService.findPage(member, pageable));
		} else {
			setAttr("page", memberDepositLogService.findPage(pageable));
		}
		setAttr("pageable", pageable);
		render("/admin/member_deposit/log.ftl");
	}

}