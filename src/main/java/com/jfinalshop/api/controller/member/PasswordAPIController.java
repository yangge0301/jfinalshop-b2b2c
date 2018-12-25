package com.jfinalshop.api.controller.member;

import java.util.Date;

import net.hasor.core.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinalshop.Setting;
import com.jfinalshop.Setting.SmsType;
import com.jfinalshop.api.common.bean.BaseResponse;
import com.jfinalshop.api.controller.BaseAPIController;
import com.jfinalshop.api.interceptor.AccessInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Sms;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.SmsService;
import com.jfinalshop.shiro.hasher.Hasher;
import com.jfinalshop.shiro.hasher.HasherInfo;
import com.jfinalshop.shiro.hasher.HasherKit;
import com.jfinalshop.util.IpUtil;
import com.jfinalshop.util.PhoneUtils;
import com.xiaoleilu.hutool.util.RandomUtil;

/**
 * 密码
 * @author yangzhicong
 */
@ControllerBind(controllerKey = "/api/member/password")
@Before(AccessInterceptor.class)
public class PasswordAPIController extends BaseAPIController {

	@Inject
	private MemberService memberService;
	@Inject
	private SmsService smsService;

	/**
	 * 发送修改密码验证码
	 */
	public void sendResetPasswordSms() {
		if (!"POST".equalsIgnoreCase(getRequest().getMethod())) {
			renderArgumentError("该方法仅接受POST请求");
			return;
		}
		String username = getPara("username");

		if (StrKit.isBlank(username)) {
			renderArgumentError("用户名不能为空");
			return;
		}
		if (!PhoneUtils.isPhone(username)) {
			renderArgumentError("检查手机号是否正确");
			return;
		}

		Sms sms = new Sms();
		String code = RandomUtil.randomNumbers(6);
		sms.setCode(code);
		sms.setExpire(DateUtils.addMinutes(new Date(), 3));
		sms.setIp(IpUtil.getIpAddr(getRequest()));
		sms.setMobile(username);
		sms.setType(Setting.SmsType.resetPassword.ordinal());
		sms.setIsUsed(false);
		smsService.save(sms);

		smsService.send(username, code);
		renderJson(new BaseResponse("短信发送成功"));
	}

	/**
	 * 修改密码
	 */
	public void resetPassword() {
		if (!"POST".equalsIgnoreCase(getRequest().getMethod())) {
			renderArgumentError("该方法仅接受POST请求");
			return;
		}
		String username = getPara("username");
		String smsCode = getPara("smsCode");
		String password = getPara("password");

		if (StrKit.isBlank(username)) {
			renderArgumentError("用户名为空!");
			return;
		}
		Member member = memberService.findByUsername(username);
		if (member == null) {
			renderArgumentError("用户不存在!");
			return;
		}
		if (StringUtils.isEmpty(password)) {
			renderArgumentError("密码不能为空!");
			return;
		}
		Sms sms = smsService.findByMobile(username, smsCode, SmsType.resetPassword, false);
		if (sms == null) {
			renderArgumentError("用户名或验证码错误!");
			return;
		}

		HasherInfo passwordInfo = HasherKit.hash(password, Hasher.DEFAULT);
		member.setPassword(passwordInfo.getHashResult());
		member.setHasher(passwordInfo.getHasher().value());
		member.setSalt(passwordInfo.getSalt());
		memberService.update(member);
		// 失效短信
		sms.setIsUsed(true);
		sms.setUsedDate(new Date());
		smsService.update(sms);

		renderJson(new BaseResponse("密码修改成功"));
	}

}
