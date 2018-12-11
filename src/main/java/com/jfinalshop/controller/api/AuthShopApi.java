package com.jfinalshop.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.Kv;
import com.jfinalshop.Results;
import com.jfinalshop.Setting;
import com.jfinalshop.controller.member.BaseController;
import com.jfinalshop.model.*;
import com.jfinalshop.service.*;
import com.jfinalshop.shiro.core.SubjectKit;
import com.jfinalshop.shiro.hasher.Hasher;
import com.jfinalshop.shiro.hasher.HasherInfo;
import com.jfinalshop.shiro.hasher.HasherKit;
import com.jfinalshop.util.IpUtil;
import com.jfinalshop.util.SystemUtils;
import com.jfinalshop.util.WebUtils;
import net.hasor.core.Inject;
import net.hasor.core.InjectSettings;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Controller - 广告
 *
 */
@ControllerBind(controllerKey = "/jnapi")
public class AuthShopApi  extends BaseController{

    /**
     * "重定向令牌"Cookie名称
     */
    private static final String REDIRECT_TOKEN_COOKIE_NAME = "redirectToken";

    @InjectSettings("${member_index}")
    private String memberIndex;
    @Inject
    private MemberService memberService;
    @Inject
    private PluginService pluginService;
    /**
     * 消息名称
     */
    public static final String MESSAGE = "message";

    @Inject
    private SocialUserService socialUserService;

    @InjectSettings("${member_login_view}")
    private String memberLoginView;


    @Inject
    private MemberRankService memberRankService;
    @Inject
    private MemberAttributeService memberAttributeService;
    /**
     * 注册用户添加
     */
    @ActionKey("/jnapi/registerUser")
    public Object registerUser(HttpServletRequest request) {
        try{
            Member member = getModel(Member.class);
            BufferedReader reader = request.getReader();
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            JSONObject req = JSONObject.parseObject(stringBuilder.toString());
                String account = req.getString("account");
            String password = req.getString("password");
            // TODO 注册用户

            Setting setting = SystemUtils.getSetting();
            if (memberService.usernameExists(member.getUsername())) {
                Results.unprocessableEntity(getResponse(), "member.register.usernameExist");
                JSONObject obj = new JSONObject();
                obj.put("resultCode","-1");
                obj.put("resultMsg","usernameExist");
                return obj;
            }

            member.removeAttributeValue();

            for (MemberAttribute memberAttribute : memberAttributeService.findList(true, true)) {
                String[] values = getParaValues("memberAttribute_" + memberAttribute.getId());
                if (!memberAttributeService.isValid(memberAttribute, values)) {
                    Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
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
            JSONObject obj = new JSONObject();
            obj.put("resultCode","0");
            obj.put("resultMsg","成功");
            return obj;
        }
        catch (Exception e){
            e.printStackTrace();
            JSONObject obj = new JSONObject();
            obj.put("resultCode","-1");
            obj.put("resultMsg","失败");
            return obj;
        }
    }

    /**
     * 注册用户添加
     */

    @ActionKey("/jnapi/loginUser")
    public void loginUser(HttpServletRequest request) {

        try{
            BufferedReader reader = request.getReader();
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            JSONObject req = JSONObject.parseObject(stringBuilder.toString());
            String account = req.getString("account");
            String password = req.getString("password");

            String redirectUrl = getPara("redirectUrl");
            String redirectToken = getPara("redirectToken");

            Long socialUserId = getParaToLong("socialUserId");
            String uniqueId = getPara("uniqueId");
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

        if (StringUtils.isNotEmpty(redirectUrl) && StringUtils.isNotEmpty(redirectToken) && StringUtils.equals(redirectToken, WebUtils.getCookie(getRequest(), REDIRECT_TOKEN_COOKIE_NAME))) {
            setAttr("redirectUrl", redirectUrl);
            setSessionAttr("redirectUrl", redirectUrl);
            WebUtils.removeCookie(getRequest(), getResponse(), REDIRECT_TOKEN_COOKIE_NAME);
        }
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
        setAttr("loginPlugins", pluginService.getActiveLoginPlugins(getRequest()));

        if (memberService.isAuthenticated() && memberService.getCurrentUser() != null) {
            renderJson(Kv.by("redirectUrl", memberIndex));
        } else {
            render(memberLoginView);
        }
        }
        catch (Exception e){
            e.printStackTrace();

        }


    }
}
