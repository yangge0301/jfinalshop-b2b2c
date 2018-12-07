package com.jfinalshop.controller.member;

import java.math.BigDecimal;
import java.util.Date;

import net.hasor.core.Inject;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Results;
import com.jfinalshop.Setting;
import com.jfinalshop.interceptor.MobileInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.MemberAttribute;
import com.jfinalshop.model.MemberRank;
import com.jfinalshop.model.PointLog;
import com.jfinalshop.model.SocialUser;
import com.jfinalshop.service.MemberAttributeService;
import com.jfinalshop.service.MemberRankService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.SocialUserService;
import com.jfinalshop.shiro.core.SubjectKit;
import com.jfinalshop.shiro.hasher.Hasher;
import com.jfinalshop.shiro.hasher.HasherInfo;
import com.jfinalshop.shiro.hasher.HasherKit;
import com.jfinalshop.util.IpUtil;
import com.jfinalshop.util.SystemUtils;

/**
 * Controller - 会员注册
 * 
 */
@ControllerBind(controllerKey = "/member/register")
public class RegisterController extends BaseController {

	@Inject
	private MemberService memberService;
	@Inject
	private MemberRankService memberRankService;
	@Inject
	private MemberAttributeService memberAttributeService;
	@Inject
	private SocialUserService socialUserService;

	/**
	 * 检查用户名是否存在
	 */
	@ActionKey("/member/register/check_username")
	public void checkUsername() {
		String username = getPara("member.username");
		renderJson(StringUtils.isNotEmpty(username) && !memberService.usernameExists(username));
	}

	/**
	 * 检查E-mail是否存在
	 */
	@ActionKey("/member/register/check_email")
	public void checkEmail() {
		String email = getPara("member.email");
		renderJson(StringUtils.isNotEmpty(email) && !memberService.emailExists(email));
	}

	/**
	 * 检查手机是否存在
	 */
	@ActionKey("/member/register/check_mobile")
	public void checkMobile() {
		String mobile = getPara("member.mobile");
		renderJson(StringUtils.isNotEmpty(mobile) && !memberService.mobileExists(mobile));
	}

	/**
	 * 注册页面
	 */
	@Before(MobileInterceptor.class)
	public void index() {
		Long socialUserId = getParaToLong("socialUserId");
		String uniqueId = getPara("uniqueId");
		if (socialUserId != null && StringUtils.isNotEmpty(uniqueId)) {
			SocialUser socialUser = socialUserService.find(socialUserId);
			if (socialUser == null || socialUser.getUser() != null || !StringUtils.equals(socialUser.getUniqueId(), uniqueId)) {
				setAttr("errorMessage", "用户不存在!");
				render(UNPROCESSABLE_ENTITY_VIEW);
				return;
			}
			setAttr("socialUserId", socialUserId);
			setAttr("uniqueId", uniqueId);
		}
		setAttr("genders", Member.Gender.values());
		render("/member/register/index.ftl");
	}

	/**
	 * 注册提交
	 */
	public void submit() {
		Member member = getModel(Member.class);
		String captcha = getPara("captcha");
		String rePassword = getPara("rePassword");
		//Long socialUserId = getParaToLong("socialUserId");
		//String uniqueId = getPara("uniqueId");
		
		if (!StringUtils.equals(member.getPassword(), rePassword)) {
			setAttr("errorMessage", "两次密码不一致!");
			Results.unprocessableEntity(getResponse(), "两次密码不一致!");
			return;
		}
		if (!SubjectKit.doCaptcha("captcha", captcha)) {
			Results.unprocessableEntity(getResponse(), "common.message.ncorrectCaptcha");
			return;
		}
		Setting setting = SystemUtils.getSetting();
		if (!ArrayUtils.contains(setting.getAllowedRegisterTypes(), Setting.RegisterType.member)) {
			Results.unprocessableEntity(getResponse(), "member.register.disabled");
			return;
		}
		if (memberService.usernameExists(member.getUsername())) {
			Results.unprocessableEntity(getResponse(), "member.register.usernameExist");
			return;
		}
		if (memberService.emailExists(member.getEmail())) {
			Results.unprocessableEntity(getResponse(), "member.register.emailExist");
			return;
		}
		if (StringUtils.isNotEmpty(member.getMobile()) && memberService.mobileExists(member.getMobile())) {
			Results.unprocessableEntity(getResponse(), "member.register.mobileExist");
			return;
		}

		member.removeAttributeValue();
		for (MemberAttribute memberAttribute : memberAttributeService.findList(true, true)) {
			String[] values = getParaValues("memberAttribute_" + memberAttribute.getId());
			if (!memberAttributeService.isValid(memberAttribute, values)) {
				Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
				return;
			}
			Object memberAttributeValue = memberAttributeService.toMemberAttributeValue(memberAttribute, values);
			member.setAttributeValue(memberAttribute, memberAttributeValue);
		}

		member.setUsername(StringUtils.lowerCase(member.getUsername()));
		member.setEmail(StringUtils.lowerCase(member.getEmail()));
		member.setMobile(StringUtils.lowerCase(member.getMobile()));
		HasherInfo hasherInfo = HasherKit.hash(member.getPassword(), Hasher.DEFAULT);
		member.setPassword(hasherInfo.getHashResult());
		member.setHasher(hasherInfo.getHasher().value());
		member.setSalt(hasherInfo.getSalt());
		
		member.setPoint(0L);
		member.setBalance(BigDecimal.ZERO);
		member.setAmount(BigDecimal.ZERO);
		member.setIsEnabled(true);
		member.setIsLocked(false);
		member.setLockDate(null);
		member.setLastLoginIp(IpUtil.getIpAddr(getRequest()));
		member.setLastLoginDate(new Date());
		MemberRank memberRank = memberRankService.findDefault();
		if (memberRank != null) {
			member.setMemberRankId(memberRank.getId());
		}
		memberService.save(member);
		// 用户注册事件
		if (setting.getRegisterPoint() > 0) {
			memberService.addPoint(member, setting.getRegisterPoint(), PointLog.Type.reward, null);
		}
		Results.ok(getResponse(), "member.register.success");
	}

}