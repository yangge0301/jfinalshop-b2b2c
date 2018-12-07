package com.jfinalshop.api.controller.shop;

import java.math.BigDecimal;
import java.util.Date;

import net.hasor.core.Inject;

import org.apache.commons.lang3.time.DateUtils;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Setting;
import com.jfinalshop.api.common.bean.BaseResponse;
import com.jfinalshop.api.common.bean.LoginResponse;
import com.jfinalshop.api.common.bean.Require;
import com.jfinalshop.api.common.token.TokenManager;
import com.jfinalshop.api.controller.BaseAPIController;
import com.jfinalshop.api.interceptor.AccessInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.MemberRank;
import com.jfinalshop.model.PointLog;
import com.jfinalshop.model.Sms;
import com.jfinalshop.service.CouponCodeService;
import com.jfinalshop.service.MemberRankService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.PluginService;
import com.jfinalshop.service.SmsService;
import com.jfinalshop.shiro.hasher.Hasher;
import com.jfinalshop.shiro.hasher.HasherInfo;
import com.jfinalshop.shiro.hasher.HasherKit;
import com.jfinalshop.util.IpUtil;
import com.jfinalshop.util.PhoneUtils;
import com.xiaoleilu.hutool.util.RandomUtil;

/**
 * 
 * 移动API - 帐户帐号
 *
 */
@ControllerBind(controllerKey = "/api/account")
@Before(AccessInterceptor.class)
public class AccountAPIController extends BaseAPIController {
	
	@Inject
	private MemberService memberService;
	@Inject
	private MemberRankService memberRankService;
	@Inject
	private SmsService smsService;
	@Inject
	private CouponCodeService couponCodeService;
	@Inject
	private PluginService pluginService;
	
	/** 默认头像 */
	private static final String AVATAR = "/upload/image/default_head.jpg";
	
	
	/**
	 * 发送会员登录短信
	 */
	@ActionKey("/api/account/send_sms")
	public void sendSms() {
		String mobile = getPara("mobile");
		
		if (!notNull(Require.me().put(mobile, "手机不能为空!"))) {
            return;
        }
		if (!PhoneUtils.isPhone(mobile)) {
			renderArgumentError("检查手机号是否正确!");
			return;
		} 
		
		Sms sms = new Sms();
		String code = RandomUtil.randomNumbers(4);
		sms.setCode(code);
		sms.setExpire(DateUtils.addMinutes(new Date(), 1));
		sms.setIp(IpUtil.getIpAddr(getRequest()));
		sms.setMobile(mobile);
		sms.setType(Setting.SmsType.memberLogin.ordinal());
		sms.setIsUsed(false);
		smsService.save(sms);
		
		smsService.send(mobile, code);
		renderJson(new BaseResponse("短信发送成功"));
	}
	
	
	/**
	 * 登录提交
	 * 
	 */
	@Before(Tx.class)
    public void login() {
    	String mobile = getPara("mobile");
    	String code = getPara("code");
        
        if (!notNull(Require.me().put(mobile, "手机不能为空！"))) {
            return;
        }
        if (!notNull(Require.me().put(code, "验证码不能为空！"))) {
            return;
        }
		Sms sms = smsService.findByMobile(mobile, code, Setting.SmsType.memberLogin, false);
		if (sms == null) {
			renderArgumentError("用户名或验证码错误!");
			return;
		}
		
        Member member = memberService.findByUsername(mobile);
        if (member == null) {
        	// 不存在则注册
        	member = register(mobile);
        }
		if (!member.getIsEnabled()) {
			renderArgumentError("用户禁用中!");
			return;
		}
		if (member.getIsLocked()) {
			renderArgumentError("用户锁定中!");
			return;
		}
		
		member.setLastLoginIp(IpUtil.getIpAddr(getRequest()));
		member.setLastLoginDate(new Date());
		member.update();
		
		// 失效短信
		sms.setIsUsed(true);
		sms.setUsedDate(new Date());
		smsService.update(sms);
		
        LoginResponse response = new LoginResponse();
        Member pMember = new Member();
        pMember.setId(member.getId());
        pMember.setUsername(member.getUsername());
        pMember.setAvatar(member.getAvatar());
        pMember.setPoint(member.getPoint());
        pMember.put("memberRank", member.getMemberRank().getName());
        pMember.setOpenId(member.getOpenId() == null ? "" : member.getOpenId());
 		response.setInfo(pMember);
 		response.setToken(TokenManager.getMe().generateToken(member));
		renderJson(response);
    }
    
    /**
     * 会员注册
     * 
     */
    private Member register(String mobile) {
    	Member member = new Member();
    	member.setUsername(mobile);
    	
		HasherInfo hasherInfo = HasherKit.hash(mobile.concat("#"), Hasher.DEFAULT);
		member.setPassword(hasherInfo.getHashResult());
		member.setHasher(hasherInfo.getHasher().value());
		member.setSalt(hasherInfo.getSalt());
		
		member.setAvatar(AVATAR);
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
		return member;
    }
}

