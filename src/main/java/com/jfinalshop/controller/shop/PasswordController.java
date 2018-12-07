package com.jfinalshop.controller.shop;

import java.util.Date;
import java.util.UUID;

import net.hasor.core.Inject;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.Kv;
import com.jfinalshop.Message;
import com.jfinalshop.Results;
import com.jfinalshop.Setting;
import com.jfinalshop.entity.SafeKey;
import com.jfinalshop.model.Business;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Member.PasswordType;
import com.jfinalshop.service.BusinessService;
import com.jfinalshop.service.MailService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.shiro.core.SubjectKit;
import com.jfinalshop.shiro.hasher.Hasher;
import com.jfinalshop.shiro.hasher.HasherInfo;
import com.jfinalshop.shiro.hasher.HasherKit;
import com.jfinalshop.util.SystemUtils;

/**
 * Controller - 密码
 * 
 */
@ControllerBind(controllerKey = "/password")
public class PasswordController extends BaseController {

	@Inject
	private BusinessService businessService;
	@Inject
	private MemberService memberService;
	@Inject
	private MailService mailService;

	/**
	 * 消息名称
	 */
	public static final String MESSAGE = "message";
	
	/**
	 * 忘记密码
	 */
	public void forgot() {
		PasswordType type = getParaEnum(PasswordType.class, getPara("type"));
		setAttr("type", type);
		render("/shop/password/forgot.ftl");
	}

	
	/**
	 * 忘记密码
	 */
	public void submit() {
		PasswordType type = getParaEnum(PasswordType.class, getPara("type"));
		String captcha = getPara("captcha");
		
		if (!SubjectKit.doCaptcha("captcha", captcha)) {
			renderJson(Kv.by(MESSAGE, "验证码输入错误!"));
			return;
		}
		
		if (type.equals(PasswordType.business)) {
			forgotBusiness();
		} else if (type.equals(PasswordType.member)) {
			forgotMember();
		}
	}
	
	
	/**
	 * 会员忘记密码
	 */
	private void forgotMember() {
		String username = getPara("username");
		String email = getPara("email");
		
		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(email)) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		Member member = memberService.findByUsername(username);
		if (member == null) {
			Results.unprocessableEntity(getResponse(), "shop.password.memberNotExist");
			return;
		}
		if (!StringUtils.equalsIgnoreCase(member.getEmail(), email)) {
			Results.unprocessableEntity(getResponse(), "shop.password.invalidEmail");
			return;
		}

		Setting setting = SystemUtils.getSetting();
		member.setSafekeyValue(DigestUtils.md5Hex(UUID.randomUUID() + RandomStringUtils.randomAlphabetic(30)));
		member.setSafekeyExpire(setting.getSafeKeyExpiryTime() != 0 ? DateUtils.addMinutes(new Date(), setting.getSafeKeyExpiryTime()) : null);
		memberService.update(member);
		mailService.sendForgotMemberPasswordMail(member);
		Results.ok(getResponse(), "shop.password.mailSuccess");
	}

	/**
	 * 商家忘记密码
	 */
	private void forgotBusiness() {
		String username = getPara("username");
		String email = getPara("email");
		
		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(email)) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		Business business = businessService.findByUsername(username);
		if (business == null) {
			Results.unprocessableEntity(getResponse(), "shop.password.businessNotExist");
			return;
		}
		if (!StringUtils.equalsIgnoreCase(business.getEmail(), email)) {
			Results.unprocessableEntity(getResponse(), "shop.password.invalidEmail");
			return;
		}

		Setting setting = SystemUtils.getSetting();
		business.setSafekeyValue(DigestUtils.md5Hex(UUID.randomUUID() + RandomStringUtils.randomAlphabetic(30)));
		business.setSafekeyExpire(setting.getSafeKeyExpiryTime() != 0 ? DateUtils.addMinutes(new Date(), setting.getSafeKeyExpiryTime()) : null);
		
		businessService.update(business);
		mailService.sendForgotBusinessPasswordMail(business);
		Results.ok(getResponse(), "shop.password.mailSuccess");
	}

	/**
	 * 重置密码
	 */
	public void reset() {
		PasswordType type = getParaEnum(PasswordType.class, getPara("type"));
		if (type.equals(PasswordType.business)) {
			resetBusiness();
		} else if (type.equals(PasswordType.member)) {
			resetMember();
		}
	}
	
	
	/**
	 * 会员重置密码
	 */
	private void resetMember() {
		String username = getPara("username");
		String key = getPara("key");
		
		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(key)) {
			setAttr("errorMessage", "用户名和key为空!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		Member member = memberService.findByUsername(username);
		if (member == null) {
			setAttr("errorMessage", "用户名为空!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		SafeKey safeKey = new SafeKey();
		safeKey.setExpire(member.getSafekeyExpire());
		safeKey.setValue(member.getSafekeyValue());
		if (safeKey == null || safeKey.getValue() == null || !StringUtils.equals(safeKey.getValue(), key)) {
			setAttr("errorMessage", "SafeKey为空!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (safeKey.hasExpired()) {
			setAttr("errorMessage", Message.warn("shop.password.hasExpired"));
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		setAttr("user", member);
		setAttr("type", PasswordType.member);
		setAttr("key", key);
		render("/shop/password/reset.ftl");
	}

	/**
	 * 商家重置密码
	 */
	private void resetBusiness() {
		String username = getPara("username");
		String key = getPara("key");
		
		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(key)) {
			setAttr("errorMessage", "用户名和key为空!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		Business business = businessService.findByUsername(username);
		if (business == null) {
			setAttr("errorMessage", "用户名为空!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		SafeKey safeKey = new SafeKey();
		safeKey.setExpire(business.getSafekeyExpire());
		safeKey.setValue(business.getSafekeyValue());
		if (safeKey == null || safeKey.getValue() == null || !StringUtils.equals(safeKey.getValue(), key)) {
			setAttr("errorMessage", "SafeKey为空!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (safeKey.hasExpired()) {
			setAttr("errorMessage", Message.warn("shop.password.hasExpired"));
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		setAttr("user", business);
		setAttr("type", PasswordType.business);
		setAttr("key", key);
		render("/shop/password/reset.ftl");
	}

	/**
	 * 重置密码
	 */
	public void resetPost() {
		PasswordType type = getParaEnum(PasswordType.class, getPara("type"));
		String captcha = getPara("captcha");
		
		if (!SubjectKit.doCaptcha("captcha", captcha)) {
			renderJson(Kv.by(MESSAGE, "验证码输入错误!"));
			return;
		}
		
		if (type.equals(PasswordType.business)) {
			resetBusinessPost();
		} else if (type.equals(PasswordType.member)) {
			resetMemberPost();
		}
	}
	
	/**
	 * 会员重置密码
	 */
	private void resetMemberPost() {
		String username = getPara("username");
		String newPassword = getPara("newPassword");
		String key = getPara("key");
		
		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(newPassword) || StringUtils.isEmpty(key)) {
			setAttr("errorMessage", "用户名和key为空!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		Member member = memberService.findByUsername(username);
		if (member == null) {
			setAttr("errorMessage", "用户名为空!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		SafeKey safeKey = new SafeKey();
		safeKey.setExpire(member.getSafekeyExpire());
		safeKey.setValue(member.getSafekeyValue());
		if (safeKey == null || safeKey.getValue() == null || !StringUtils.equals(safeKey.getValue(), key)) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		if (safeKey.hasExpired()) {
			Results.unprocessableEntity(getResponse(), "shop.password.hasExpired");
			return;
		}
		member.setPassword(newPassword);
		HasherInfo hasherInfo = HasherKit.hash(member.getPassword(), Hasher.DEFAULT);
		member.setPassword(hasherInfo.getHashResult());
		member.setHasher(hasherInfo.getHasher().value());
		member.setSalt(hasherInfo.getSalt());
		
		member.setSafekeyExpire(null);
		member.setSafekeyValue(null);
		memberService.update(member);
		Results.ok(getResponse(), "shop.password.resetSuccess");
	}

	/**
	 * 商家重置密码
	 */
	private void resetBusinessPost() {
		String username = getPara("username");
		String newPassword = getPara("newPassword");
		String key = getPara("key");
		
		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(newPassword) || StringUtils.isEmpty(key)) {
			Results.unprocessableEntity(Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		Business business = businessService.findByUsername(username);
		if (business == null) {
			Results.unprocessableEntity(Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}

		SafeKey safeKey = new SafeKey();
		safeKey.setExpire(business.getSafekeyExpire());
		safeKey.setValue(business.getSafekeyValue());
		if (safeKey == null || safeKey.getValue() == null || !StringUtils.equals(safeKey.getValue(), key)) {
			Results.unprocessableEntity(Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		if (safeKey.hasExpired()) {
			Results.unprocessableEntity(getResponse(), "shop.password.hasExpired");
		}
		business.setPassword(newPassword);
		HasherInfo hasherInfo = HasherKit.hash(business.getPassword(), Hasher.DEFAULT);
		business.setPassword(hasherInfo.getHashResult());
		business.setHasher(hasherInfo.getHasher().value());
		business.setSalt(hasherInfo.getSalt());
		
		business.setSafekeyExpire(null);
		business.setSafekeyValue(null);
		businessService.update(business);
		Results.ok(getResponse(), "shop.password.resetSuccess");
	}

}