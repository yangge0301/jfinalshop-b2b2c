package com.jfinalshop.controller.admin;

import java.math.BigDecimal;

import net.hasor.core.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Message;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.MemberAttribute;
import com.jfinalshop.model.MemberRank;
import com.jfinalshop.model.Store;
import com.jfinalshop.service.MemberAttributeService;
import com.jfinalshop.service.MemberRankService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.StoreService;
import com.jfinalshop.shiro.hasher.Hasher;
import com.jfinalshop.shiro.hasher.HasherInfo;
import com.jfinalshop.shiro.hasher.HasherKit;

/**
 * Controller - 会员
 * 
 */
@ControllerBind(controllerKey = "/admin/member")
public class MemberController extends BaseController {

	@Inject
	private MemberService memberService;
	@Inject
	private MemberRankService memberRankService;
	@Inject
	private MemberAttributeService memberAttributeService;
	@Inject
	private StoreService storeService;

	/**
	 * 检查用户名是否存在
	 */
	@ActionKey("/admin/member/check_username")
	public void checkUsername() {
		String username = getPara("member.username");
		renderJson(StringUtils.isNotEmpty(username) && !memberService.usernameExists(username));
	}

	/**
	 * 检查E-mail是否唯一
	 */
	@ActionKey("/admin/member/check_email")
	public void checkEmail() {
		Long id = getParaToLong("id");
		String email = getPara("member.email");
		renderJson(StringUtils.isNotEmpty(email) && memberService.emailUnique(id, email));
	}

	/**
	 * 检查手机是否唯一
	 */
	@ActionKey("/admin/member/check_mobile")
	public void checkMobile() {
		Long id = getParaToLong("id");
		String mobile = getPara("member.mobile");
		renderJson(StringUtils.isNotEmpty(mobile) && memberService.mobileUnique(id, mobile));
	}

	/**
	 * 查看
	 */
	public void view() {
		Long id = getParaToLong("id");
		Member member = memberService.find(id);
		setAttr("genders", Member.Gender.values());
		setAttr("memberAttributes", memberAttributeService.findList(true, true));
		setAttr("member", member);
		render("/admin/member/view.ftl");
	}

	/**
	 * 添加
	 */
	public void add() {
		setAttr("genders", Member.Gender.values());
		setAttr("memberRanks", memberRankService.findAll());
		setAttr("stores", storeService.findList(null, Store.Status.success, true, false, null, null));
		setAttr("memberAttributes", memberAttributeService.findList(true, true));
		render("/admin/member/add.ftl");
	}

	/**
	 * 保存
	 */
	@Before(Tx.class)
	public void save() {
		Member member = getModel(Member.class);
		Long memberRankId = getParaToLong("memberRankId");
		Long storeId = getParaToLong("storeId");
		Boolean isEnabled = getParaToBoolean("isEnabled", false);
		String password = getPara("password");
		String rePassword = getPara("rePassword");
		
		if (!StringUtils.equals(password, rePassword)) {
			setAttr("errorMessage", "两次密码不一致!");
			render(ERROR_VIEW);
			return;
		}
		
		MemberRank memberRank = memberRankService.find(memberRankId);
		if (memberRank != null) {
			member.setMemberRankId(memberRank.getId());
		}
		
		if (memberService.usernameExists(member.getUsername())) {
			setAttr("errorMessage", "用户名已存在!");
			render(ERROR_VIEW);
			return;
		}
		if (memberService.emailExists(member.getEmail())) {
			setAttr("errorMessage", "E-mail已存在!");
			render(ERROR_VIEW);
			return;
		}
		if (StringUtils.isNotEmpty(member.getMobile()) && memberService.mobileExists(member.getMobile())) {
			setAttr("errorMessage", "手机已存在!");
			render(ERROR_VIEW);
			return;
		}
		member.removeAttributeValue();
		for (MemberAttribute memberAttribute : memberAttributeService.findList(true, true)) {
			String[] values = getParaValues("memberAttribute_" + memberAttribute.getId());
			if (!memberAttributeService.isValid(memberAttribute, values)) {
				setAttr("errorMessage", "会员注册项值验证失败!");
				render(ERROR_VIEW);
				return;
			}
			Object memberAttributeValue = memberAttributeService.toMemberAttributeValue(memberAttribute, values);
			member.setAttributeValue(memberAttribute, memberAttributeValue);
		}
		member.setIsEnabled(isEnabled);
		HasherInfo hasherInfo = HasherKit.hash(password, Hasher.DEFAULT);
		member.setPassword(hasherInfo.getHashResult());
		member.setHasher(hasherInfo.getHasher().value());
		member.setSalt(hasherInfo.getSalt());
		member.setPoint(0L);
		member.setBalance(BigDecimal.ZERO);
		member.setAmount(BigDecimal.ZERO);
		member.setIsLocked(false);
		member.setStoreId(storeId);
		memberService.save(member);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		Member member = memberService.find(id);
		setAttr("genders", Member.Gender.values());
		setAttr("memberRanks", memberRankService.findAll());
		setAttr("stores", storeService.findList(null, Store.Status.success, true, false, null, null));
		setAttr("memberAttributes", memberAttributeService.findList(true, true));
		setAttr("member", member);
		render("/admin/member/edit.ftl");
	}

	/**
	 * 更新
	 */
	@Before(Tx.class)
	public void update() {
		Member member = getModel(Member.class);
		Long memberRankId = getParaToLong("memberRankId");
		Long storeId = getParaToLong("storeId");
		Boolean unlock = getParaToBoolean("unlock", false);
		Boolean isEnabled = getParaToBoolean("isEnabled", false);
		String password = getPara("password");
		String rePassword = getPara("rePassword");
		
		if (!memberService.emailUnique(member.getId(), member.getEmail())) {
			setAttr("errorMessage", "E-mail已存在!");
			render(ERROR_VIEW);
			return;
		}
		if (StringUtils.isNotEmpty(member.getMobile()) && !memberService.mobileUnique(member.getId(), member.getMobile())) {
			setAttr("errorMessage", "手机号已存在!");
			render(ERROR_VIEW);
			return;
		}
		member.removeAttributeValue();
		for (MemberAttribute memberAttribute : memberAttributeService.findList(true, true)) {
			String[] values = getParaValues("memberAttribute_" + memberAttribute.getId());
			if (!memberAttributeService.isValid(memberAttribute, values)) {
				setAttr("errorMessage", "会员注册项值验证失败!");
				render(ERROR_VIEW);
				return;
			}
			Object memberAttributeValue = memberAttributeService.toMemberAttributeValue(memberAttribute, values);
			member.setAttributeValue(memberAttribute, memberAttributeValue);
		}
		Member pMember = memberService.find(member.getId());
		if (pMember == null) {
			setAttr("errorMessage", "会员不存在!");
			render(ERROR_VIEW);
			return;
		}
		MemberRank memberRank = memberRankService.find(memberRankId);
		if (memberRank != null) {
			member.setMemberRankId(memberRank.getId());
		}
		
		if (StringUtils.isNotEmpty(password) && StringUtils.isNotEmpty(rePassword)) {
			if (!StringUtils.equals(password, rePassword)) {
				setAttr("errorMessage", "两次密码不一致!");
				render(ERROR_VIEW);
				return;
			}
			HasherInfo hasherInfo = HasherKit.hash(password, Hasher.DEFAULT);
			member.setPassword(hasherInfo.getHashResult());
			member.setHasher(hasherInfo.getHasher().value());
			member.setSalt(hasherInfo.getSalt());
		} else {
			member.setPassword(pMember.getPassword());
			member.setHasher(pMember.getHasher());
			member.setSalt(pMember.getSalt());
		}
		member.setIsEnabled(isEnabled);
		member.setStoreId(storeId);
		if (BooleanUtils.isTrue(pMember.getIsLocked()) && BooleanUtils.isTrue(unlock)) {
			memberService.unlock(member);
			memberService.update(member, "username", "point", "balance", "amount", "lastLoginIp", "lastLoginDate", "loginPluginId");
		} else {
			memberService.update(member, "username", "point", "balance", "amount", "isLocked", "lockDate", "lastLoginIp", "lastLoginDate", "loginPluginId");
		}
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("memberRanks", memberRankService.findAll());
		setAttr("memberAttributes", memberAttributeService.findAll());
		setAttr("pageable", pageable);
		setAttr("page", memberService.findPage(pageable));
		render("/admin/member/list.ftl");
	}

	/**
	 * 删除
	 */
	@Before(Tx.class)
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids != null) {
			for (Long id : ids) {
				Member member = memberService.find(id);
				if (member != null && member.getBalance().compareTo(BigDecimal.ZERO) > 0) {
					renderJson(Message.error("admin.member.deleteExistDepositNotAllowed", member.getUsername()));
					return;
				}
				Db.deleteById("receiver", "member_id", id);
				Db.deleteById("member_role", "members_id", id);
			}
			memberService.delete(ids);
		}
		renderJson(SUCCESS_MESSAGE);
	}

}