package com.jfinalshop.controller.member;

import java.io.BufferedReader;
import java.math.BigDecimal;
import java.util.*;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinalshop.model.*;
import com.jfinalshop.service.*;
import com.jfinalshop.util.JHttp;
import com.jfinalshop.util.WebUtils;
import net.hasor.core.Inject;

import net.hasor.core.InjectSettings;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Results;
import com.jfinalshop.Setting;
import com.jfinalshop.interceptor.MobileInterceptor;
import com.jfinalshop.shiro.core.SubjectKit;
import com.jfinalshop.shiro.hasher.Hasher;
import com.jfinalshop.shiro.hasher.HasherInfo;
import com.jfinalshop.shiro.hasher.HasherKit;
import com.jfinalshop.util.IpUtil;
import com.jfinalshop.util.SystemUtils;

import javax.servlet.http.HttpServletRequest;

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

//	public void info(HttpServletRequest request){
//		try{
//
//			String account = request.getParameter("account");
//			String password = request.getParameter("password");
//
//			Member member = new Member();
//			Setting setting = SystemUtils.getSetting();
//			if (memberService.usernameExists(account)) {
//				JSONObject obj = new JSONObject();
//				obj.put("resultCode","2");
//				obj.put("resultMsg","usernameExist");
//				renderJson(obj);
//				return;
//			}
//
//			System.out.println(44);
//			member.removeAttributeValue();
//
//			for (MemberAttribute memberAttribute : memberAttributeService.findList(true, true)) {
//				String[] values = getParaValues("memberAttribute_" + memberAttribute.getId());
//				if (!memberAttributeService.isValid(memberAttribute, values)) {
//					Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
//				}
//				Object memberAttributeValue = memberAttributeService.toMemberAttributeValue(memberAttribute, values);
//				member.setAttributeValue(memberAttribute, memberAttributeValue);
//			}
//
//			System.out.println(55);
//			member.setUsername(StringUtils.lowerCase(account));
//			member.setEmail(StringUtils.lowerCase(member.getEmail()));
//			member.setMobile(StringUtils.lowerCase(member.getMobile()));
//			HasherInfo hasherInfo = HasherKit.hash(password, Hasher.DEFAULT);
//			member.setPassword(hasherInfo.getHashResult());
//			member.setHasher(hasherInfo.getHasher().value());
//			member.setSalt(hasherInfo.getSalt());
//
//			System.out.println(66);
//			member.setPoint(0L);
//			member.setBalance(BigDecimal.ZERO);
//			member.setAmount(BigDecimal.ZERO);
//			member.setIsEnabled(true);
//			member.setIsLocked(false);
//			member.setLockDate(null);
//			member.setLastLoginIp(IpUtil.getIpAddr(getRequest()));
//			member.setLastLoginDate(new Date());
//			MemberRank memberRank = memberRankService.findDefault();
//			if (memberRank != null) {
//				member.setMemberRankId(memberRank.getId());
//			}
//			memberService.save(member);
//			// 用户注册事件
//			if (setting.getRegisterPoint() > 0) {
//				memberService.addPoint(member, setting.getRegisterPoint(), PointLog.Type.reward, null);
//			}
//			System.out.println(22);
//			JSONObject obj = new JSONObject();
//			obj.put("resultCode","0");
//			obj.put("resultMsg","成功");
//			renderJson(obj);
//		}
//		catch (Exception e){
//			e.printStackTrace();
//			JSONObject obj = new JSONObject();
//			obj.put("resultCode","3");
//			obj.put("resultMsg","失败");
//			renderJson(obj);
//		}
//	}

	/**
	 * "重定向令牌"Cookie名称
	 */
	private static final String REDIRECT_TOKEN_COOKIE_NAME = "redirectToken";


	@Inject
	private PluginService pluginService;
	@InjectSettings("${mobile_login_view}")
	private String memberIndex;
	@InjectSettings("${member_login_view}")
	private String memberLoginView;
	@InjectSettings("${user_register_to_wjn_url}")
	private String registerurl;
	/**
	 * 消息名称
	 */
	public static final String MESSAGE = "message";

	@Before(MobileInterceptor.class)
	public void login() {
		String account = getPara("account");
		String password = getPara("password");
		Map<String, Object> data = new HashMap<>();
		if (StrKit.notBlank(account) || StrKit.notBlank(password)) {
			Member member = memberService.findByUsername(account);
			if (member == null) {
				renderJson(Kv.by(MESSAGE, "用户不存在!"));
				return;
			}
			if (!member.getIsEnabled()) {
				renderJson(Kv.by(MESSAGE, "用户禁用中!"));
				return;
			}
			if (member.getIsLocked()) {
				renderJson(Kv.by(MESSAGE, "用户锁定中!"));
				return;
			}
			if (SubjectKit.login(account, password, SubjectKit.UserType.MEMBER)) {
				member.setLastLoginIp(IpUtil.getIpAddr(getRequest()));
				member.setLastLoginDate(new Date());
				member.update();
			} else {
				renderJson(Kv.by(MESSAGE, "用户名或密码错误!"));
				return;
			}
		}

		setAttr("loginPlugins", pluginService.getActiveLoginPlugins(getRequest()));

		if (memberService.isAuthenticated() && memberService.getCurrentUser() != null) {
			render(memberIndex);
		} else {
			render(memberIndex);
		}

	}
	public void info() {

		String account = getPara("account");
		String password = getPara("password");
		Map<String, Object> data = new HashMap<>();
		Setting setting = SystemUtils.getSetting();
		try{


			Member member = new Member();
			if (memberService.usernameExists(account)) {
				JSONObject obj = new JSONObject();
				obj.put("resultCode","2");
				obj.put("resultMsg","usernameExist");
				renderJson(obj);
				return;
			}

			member.removeAttributeValue();

			System.out.println(55);
			member.setUsername(StringUtils.lowerCase(account));
			member.setEmail(StringUtils.lowerCase("1@1.com"));
			member.setMobile(StringUtils.lowerCase(member.getMobile()));
			HasherInfo hasherInfo = HasherKit.hash(password, Hasher.DEFAULT);
			member.setPassword(hasherInfo.getHashResult());
			member.setHasher(hasherInfo.getHasher().value());
			member.setSalt(hasherInfo.getSalt());

			System.out.println(66);
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
			System.out.println(22);
			JSONObject obj = new JSONObject();
			obj.put("resultCode","0");
			obj.put("resultMsg","成功");
			renderJson(obj);
		}
		catch (Exception e){
			e.printStackTrace();
			JSONObject obj = new JSONObject();
			obj.put("resultCode","3");
			obj.put("resultMsg","失败");
			renderJson(obj);
		}
	}




    public void updatepoint() {

        String account = getPara("account");
        String password = getPara("password");
        long jifen = getParaToLong("jifen");
        long add_jifen = getParaToLong("add_jifen");
        long money = getParaToLong("money");
        long add_money = getParaToLong("add_money");
        Map<String, Object> data = new HashMap<>();
        Setting setting = SystemUtils.getSetting();
        try{


            Member member = memberService.findByUsername(account);
            if(member == null){
                return;
            }
            if (!memberService.usernameExists(account)) {
                JSONObject obj = new JSONObject();
                obj.put("resultCode","2");
                obj.put("resultMsg","usernameExist");
                renderJson(obj);
                return;
            }


            if (jifen > 0) {
                memberService.addPointV2(member, jifen,add_jifen, PointLog.Type.reward, null);
            }
            if(money>0){
                memberService.addBalanceV2(member, BigDecimal.valueOf(money),BigDecimal.valueOf(add_money), MemberDepositLog.Type.recharge, null);
            }
            JSONObject obj = new JSONObject();
            obj.put("resultCode","0");
            obj.put("resultMsg","成功");
            renderJson(obj);
        }
        catch (Exception e){
            e.printStackTrace();
            JSONObject obj = new JSONObject();
            obj.put("resultCode","3");
            obj.put("resultMsg","失败");
            renderJson(obj);
        }
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
		//同步注册信息
		String urls = registerurl+"&account="+member.getUsername()+"&source=shop";
		JHttp.get(urls);
		// 用户注册事件
		if (setting.getRegisterPoint() > 0) {
			memberService.addPoint(member, setting.getRegisterPoint(), PointLog.Type.reward, null);
		}
		Results.ok(getResponse(), "member.register.success");
	}

}