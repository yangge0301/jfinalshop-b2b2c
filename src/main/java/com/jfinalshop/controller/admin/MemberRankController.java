package com.jfinalshop.controller.admin;

import java.math.BigDecimal;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Message;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.MemberRank;
import com.jfinalshop.service.MemberRankService;

/**
 * Controller - 会员等级
 * 
 */
@ControllerBind(controllerKey = "/admin/member_rank")
public class MemberRankController extends BaseController {

	@Inject
	private MemberRankService memberRankService;

	/**
	 * 检查消费金额是否唯一
	 */
	@ActionKey("/admin/member_rank/check_amount")
	public void checkAmount() {
		Long id = getParaToLong("id");
		BigDecimal amount = new BigDecimal(getPara("memberRank.amount", "0"));
		renderJson(amount != null && memberRankService.amountUnique(id, amount));
	}

	/**
	 * 添加
	 */
	public void add() {
		render("/admin/member_rank/add.ftl");
	}

	/**
	 * 保存
	 */
	@Before(Tx.class)
	public void save() {
		MemberRank memberRank = getModel(MemberRank.class);
		Boolean isDefault = getParaToBoolean("isDefault", false);
		Boolean isSpecial = getParaToBoolean("isSpecial", false);
		
		memberRank.setIsDefault(isDefault);
		memberRank.setIsSpecial(isSpecial);
		if (memberRank.getIsSpecial()) {
			memberRank.setAmount(null);
		} else if (memberRank.getAmount() == null || memberRankService.amountExists(memberRank.getAmount())) {
			setAttr("errorMessage", "消费金额已存在!");
			render(ERROR_VIEW);
			return;
		}
		
		memberRank.setMembers(null);
		memberRank.setPromotions(null);
		memberRankService.save(memberRank);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("memberRank", memberRankService.find(id));
		render("/admin/member_rank/edit.ftl");
	}

	/**
	 * 更新
	 */
	@Before(Tx.class)
	public void update() {
		MemberRank memberRank = getModel(MemberRank.class);
		Boolean isDefault = getParaToBoolean("isDefault", false);
		Boolean isSpecial = getParaToBoolean("isSpecial", false);
		
		memberRank.setIsDefault(isDefault);
		memberRank.setIsSpecial(isSpecial);
		MemberRank pMemberRank = memberRankService.find(memberRank.getId());
		if (pMemberRank == null) {
			setAttr("errorMessage", "会员等级不存在!");
			render(ERROR_VIEW);
			return;
		}
		if (pMemberRank.getIsDefault()) {
			memberRank.setIsDefault(true);
		}
		if (memberRank.getIsSpecial()) {
			memberRank.setAmount(null);
		} else if (memberRank.getAmount() == null || !memberRankService.amountUnique(memberRank.getId(), memberRank.getAmount())) {
			setAttr("errorMessage", "消费金额已存在!");
			render(ERROR_VIEW);
			return;
		}
		memberRankService.update(memberRank);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", memberRankService.findPage(pageable));
		render("/admin/member_rank/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids != null) {
			for (Long id : ids) {
				MemberRank memberRank = memberRankService.find(id);
				if (memberRank != null && memberRank.getMembers() != null && !memberRank.getMembers().isEmpty()) {
					renderJson(Message.error("admin.memberRank.deleteExistNotAllowed", memberRank.getName()));
					return;
				}
			}
			long totalCount = memberRankService.count();
			if (ids.length >= totalCount) {
				renderJson(Message.error("admin.common.deleteAllNotAllowed"));
				return;
			}
			memberRankService.delete(ids);
		}
		renderJson(SUCCESS_MESSAGE);
	}

}