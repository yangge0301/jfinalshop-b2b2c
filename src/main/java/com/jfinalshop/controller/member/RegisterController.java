package com.jfinalshop.controller.member;

import java.io.BufferedReader;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.*;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinalshop.entity.ProductImage;
import com.jfinalshop.exception.ResourceNotFoundException;
import com.jfinalshop.model.*;
import com.jfinalshop.service.*;
import com.jfinalshop.util.*;
import net.hasor.core.Inject;

import net.hasor.core.InjectSettings;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
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

import javax.servlet.http.HttpServletRequest;

/**
 * Controller - 会员注册
 *
 */
@ControllerBind(controllerKey = "/member/register")
public class RegisterController extends BaseController {

    @InjectSettings("${user_product_list}")
    private String productStr;
    @Inject
    private MemberService memberService;
    @Inject
    private MemberRankService memberRankService;
    @Inject
    private MemberAttributeService memberAttributeService;
    @Inject
    private SocialUserService socialUserService;

    @Inject
    private ProductService productService;
    private static final int NEW_ORDER_SIZE = 3;
    @Inject
    private OrderService orderService;
    @Inject
    private CouponCodeService couponCodeService;
    @Inject
    private MessageService messageService;
    @Inject
    private ProductFavoriteService productFavoriteService;
    @Inject
    private ProductNotifyService productNotifyService;
    @Inject
    private ReviewService reviewService;
    @Inject
    private ConsultationService consultationService;
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


    @InjectSettings("${mobile_product_detail_view}")
    private String productView;
    @Inject
    private PluginService pluginService;
    @InjectSettings("${mobile_login_view}")
    private String memberIndex;
    @InjectSettings("${member_user_index}")
    private String memberUserIndex;
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
        String account = URLDecoder.decode(getPara("account"));
        String password = getPara("password");
        String timestamp = getPara("timestamp")==null?getPara("timeStamp"):getPara("timestamp");
        String sign = getPara("sign");
        String type=getPara("type");

        SortedMap<Object,Object> parameters = new TreeMap<Object, Object>();
        parameters.put("account",account);
        parameters.put("password",password);
        if(getPara("timestamp")==null){

            parameters.put("timeStamp",timestamp);
        }
        else{

            parameters.put("timestamp",timestamp);
        }
        if(!MD5Util.createSign(parameters,sign).equals(sign)){
            JSONObject obj = new JSONObject();
            obj.put("resultCode","1");
            obj.put("resultMsg","sign error");
            renderJson(obj);
            return;
        }

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
        String viewUrl = "";
        if(type!=null&&type.equals("1")){
            Member currentUser = memberService.getCurrentUser();
            setAttr("pendingPaymentOrderCount", orderService.count(null, Order.Status.pendingPayment, null, currentUser, null, null, null, null, null, null, false));
            setAttr("pendingShipmentOrderCount", orderService.count(null, Order.Status.pendingShipment, null, currentUser, null, null, null, null, null, null, null));
            setAttr("shippedOrderCount", orderService.count(null, Order.Status.shipped, null, currentUser, null, null, null, null, null, null, null));
            setAttr("messageCount", messageService.count(currentUser, false));
            setAttr("couponCodeCount", couponCodeService.count(null, currentUser, null, false, false));
            setAttr("productFavoriteCount", productFavoriteService.count(currentUser));
            setAttr("productNotifyCount", productNotifyService.count(currentUser, null, null, null));
            setAttr("reviewCount", reviewService.count(currentUser, null, null, null));
            setAttr("consultationCount", consultationService.count(currentUser, null, null));
            setAttr("newOrders", orderService.findList(null, null, null, currentUser, null, null, null, null, null, null, null, NEW_ORDER_SIZE, null, null));

            viewUrl=memberUserIndex;
        }
        else if(type!=null&&type.equals("2")){
            String productId = getPara("productId");
            if(productId==null||productId.trim().equals("")){
                return;
            }

            Long pid = Long.parseLong(productId);
            Product product = productService.find(pid);
            if (product == null || BooleanUtils.isNotTrue(product.getIsActive()) || BooleanUtils.isNotTrue(product.getIsMarketable())) {
                throw new ResourceNotFoundException();
            }
            List<ProductImage> list = product.getProductImagesConverter();
            List<ProductImage> listtmp = new ArrayList<ProductImage>();
            if (list == null || list.size()==0) {
                throw new ResourceNotFoundException();
            }
            for(ProductImage p : list){
                if(p!=null&&p.getLarge()!=null&&!p.getLarge().trim().equals("")&&p.getMedium()!=null&&!p.getMedium().trim().equals("")){
                    listtmp.add(p);
                }
            }
            product.setProductImages(listtmp);
            setAttr("product", product);
            viewUrl=productView;
        }
        else{
            viewUrl= memberIndex;
        }
        setAttr("loginPlugins", pluginService.getActiveLoginPlugins(getRequest()));

        if (memberService.isAuthenticated() && memberService.getCurrentUser() != null) {
            render(viewUrl);
        } else {
            render(viewUrl);
        }

    }
    public void info() {

        String account = URLDecoder.decode(getPara("account"));
        String password = getPara("password");
        String timestamp = getPara("timestamp")==null?getPara("timeStamp"):getPara("timestamp");
        String sign = getPara("sign");
        Map<String, Object> data = new HashMap<>();
        Setting setting = SystemUtils.getSetting();

        try{

            SortedMap<Object,Object> parameters = new TreeMap<Object, Object>();
            parameters.put("account",account);
            parameters.put("password",password);
            if(getPara("timestamp")==null){

                parameters.put("timeStamp",timestamp);
            }
            else{

                parameters.put("timestamp",timestamp);
            }
            if(!MD5Util.createSign(parameters,sign).equals(sign)){
                JSONObject obj = new JSONObject();
                obj.put("resultCode","1");
                obj.put("resultMsg","sign error");
                renderJson(obj);
                return;
            }
            Member member = new Member();
            if (memberService.usernameExists(account)) {
                JSONObject obj = new JSONObject();
                obj.put("resultCode","2");
                obj.put("resultMsg","usernameExist");
                renderJson(obj);
                return;
            }
            member.removeAttributeValue();
            member.setUsername(account);
            member.setEmail(StringUtils.lowerCase("1@1.com"));
            member.setMobile(StringUtils.lowerCase(member.getMobile()));
            HasherInfo hasherInfo = HasherKit.hash(password, Hasher.DEFAULT);
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

    public void products(){
        JSONObject obj = new JSONObject();
        try{
            String str[] = productStr.split(",");
            List<Product> list = new ArrayList<Product>();
            if(str!=null&&str.length>0){
                for(String s : str){
                    Long productId = Long.parseLong(s);
                    Product product = productService.find(productId);
                    if (!(product == null || BooleanUtils.isNotTrue(product.getIsActive()) || BooleanUtils.isNotTrue(product.getIsMarketable()))) {
                        product.setImage(SystemUtils.getSetting().getSiteImageUrl()+product.getImage());
                        list.add(product);
                    }
                }
            }
            obj.put("resultCode","0");
            obj.put("resultMsg","成功");
            obj.put("products",list);
            renderJson(obj);
        }
        catch (Exception e){
            e.printStackTrace();
            obj.put("resultCode","-1");
            obj.put("resultMsg","失败");
            obj.put("products","");
            renderJson(obj);
        }
    }



    public void updatepoint() {

        String account = URLDecoder.decode(getPara("account"));
        String password = getPara("password");
        long jifen = getParaToLong("jifen");
        long add_jifen = getParaToLong("add_jifen");
        String money1 = getPara("money");
        String add_money1 = getPara("add_money");
        String timestamp = getPara("timestamp")==null?getPara("timeStamp"):getPara("timestamp");
        String sign = getPara("sign");

        SortedMap<Object,Object> parameters = new TreeMap<Object, Object>();
        parameters.put("account",account);
        parameters.put("password",password);
        parameters.put("jifen",jifen);
        parameters.put("add_jifen",add_jifen);
        parameters.put("money",money1);
        parameters.put("add_money",add_money1);
        if(getPara("timestamp")==null){

            parameters.put("timeStamp",timestamp);
        }
        else{

            parameters.put("timestamp",timestamp);
        }
        if(!MD5Util.createSign(parameters,sign).equals(sign)){
            JSONObject obj = new JSONObject();
            obj.put("resultCode","1");
            obj.put("resultMsg","sign error");
            renderJson(obj);
            return;
        }
        double money = Double.parseDouble(money1);
        double add_money = Double.parseDouble(add_money1);
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