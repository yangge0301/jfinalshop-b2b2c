package com.jfinalshop.controller.member;

import java.io.BufferedReader;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.*;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.StrKit;
import com.jfinalshop.api.controller.util.HttpClient;
import com.jfinalshop.api.controller.util.resbean.JsonResult;
import com.jfinalshop.dao.OrderItemDao;
import com.jfinalshop.dao.OrderLogDao;
import com.jfinalshop.entity.ProductImage;
import com.jfinalshop.exception.ResourceNotFoundException;
import com.jfinalshop.model.*;
import com.jfinalshop.service.*;
import com.jfinalshop.shiro.session.RedisManager;
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
import org.jsoup.helper.StringUtil;

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
    private RedisManager redisManager;
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

    private static  String appId="wx3952700d4b424681";
    private static  String appSecret="125345e12744f9c5fd9d610676804a19";
    private static  String serverUrl="https://api.mubag.top/";
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

    @ActionKey("/userlogin")
    public void userlogin(){
        try{
            String code = getRequest().getParameter("code");
            LogKit.info("userlogin==>code="+code);
            String id = getRequest().getSession().getId();
            String url = "https://api.weixin.qq.com/sns/jscode2session?appid="+appId+"&secret="+appSecret+"&js_code="+code+"&grant_type=authorization_code";
            JSONObject obj = HttpClient.getAccessToken(url);
            LogKit.info("userlogin userinfo==>"+obj.toJSONString());
            String openId = obj.getString("openid");
            String session_key = obj.getString("session_key");
            if((StringUtil.isBlank(openId)||StringUtil.isBlank(session_key))){
                renderJson(new JsonResult("-1","获取用户信息失败，code错误.",null,null,null));
            }
            else{
                obj.put("openId",openId);
                obj.put("session_key",session_key);
                LogKit.info("userlogin userregister==>"+getResponse().getHeader("Set-Cookie"));
                if(getResponse().getHeader("Set-Cookie")!=null&&getResponse().getHeader("Set-Cookie").contains("csrfToken")){
                    obj.put("csrfToken", getResponse().getHeader("Set-Cookie").split(";")[0].split("=")[1]);
                }
//                redisManager.set(id,obj.toJSONString(),30*60);
                //注册
                Member member = memberService.findByUsername(openId);
                if (member == null) {
                    member = new Member();
                    member.removeAttributeValue();
                    member.setUsername(openId);
                    member.setEmail(org.apache.commons.lang.StringUtils.lowerCase("1@1.com"));
                    member.setMobile(org.apache.commons.lang.StringUtils.lowerCase(member.getMobile()));
                    HasherInfo hasherInfo = HasherKit.hash("3441901P1o", Hasher.DEFAULT);
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
                    member = memberService.save(member);
                }
                if (SubjectKit.login(openId, "3441901P1o", SubjectKit.UserType.MEMBER)) {
                    member.setLastLoginIp(IpUtil.getIpAddr(getRequest()));
                    member.setLastLoginDate(new Date());
                    member.update();
                }
                Member m = memberService.getCurrentUser();
                System.out.println(m);
//                pluginService.getActiveLoginPlugins(getRequest());
                renderJson(new JsonResult("1","登录成功",null,obj,id));
            }
            return;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        renderText("");
    }

    @Inject
    private BusinessService businessService;
    @Inject
    private OrderItemDao orderItemDao;
    @Inject
    private OrderLogDao orderLogDao;

    @Inject
    private AreaService areaService;
    @Inject
    private ReceiverService receiverService;
    /**
     * 删除
     */
    public void deleteme() {
        Long[] ids = getParaValuesToLong("ids");
        Member currentUser = memberService.getCurrentUser();

        if (ids != null) {
            for (Long id : ids) {
                Order order = orderService.find(id);
                if (order!=null&&!orderService.acquireLock(order, currentUser)) {
                    Results.unprocessableEntity(getResponse(), "business.order.deleteLockedNotAllowed", order.getSn());
                    return;
                }
                if (order!=null&&!order.canDelete()) {
                    Results.unprocessableEntity(getResponse(), "business.order.deleteStatusNotAllowed", order.getSn());
                    return;
                }
            }

            if (ids != null) {
                for (Long id : ids) {
                    OrderLog orderLog = orderLogDao.findOrderLog(id, OrderLog.Type.create);
                    if(orderLog!=null){
                        orderLogDao.remove(orderLog);
                    }

                    List<OrderItem> orderItems = orderItemDao.findOrderLog(id);
                    if(orderItems!=null&&orderItems.size()>0){
                        for (OrderItem orderItem: orderItems) {
                            orderItemDao.remove(orderItem);
                        }
                    }
                }
            }
            orderService.delete(ids);
        }
        renderJson("{res:success}");
    }
    /**
     * 保存
     */
    public void saveme() {
        Receiver receiver = new Receiver();
        String address = getPara("address");
        Long areaId = getParaToLong("areaId");
        String consignee = getPara("consignee");
        String phone = getPara("phone");
        String zip_code = getPara("zip_code");
        Boolean isDefault = getParaToBoolean("isDefault", false);
        receiver.setPhone(phone);
        receiver.setAddress(address);
        receiver.setConsignee(consignee);
        receiver.setZipCode(zip_code);
        receiver.setAreaId(areaId);
        Member currentUser = memberService.getCurrentUser();
        if(areaId == null||receiver.getConsignee()==null){
            renderJson("");
            return;
        }
        Area area = areaService.find(areaId);
        if (area != null) {
            receiver.setAreaId(area.getId());

            receiver.setAreaName(area.getFullName());
        }
        if (Receiver.MAX_RECEIVER_COUNT != null && currentUser.getReceivers().size() >= Receiver.MAX_RECEIVER_COUNT) {
            setAttr("errorMessage", "超收货地址最大保存数!");
            render(UNPROCESSABLE_ENTITY_VIEW);
            return;
        }
        receiver.setIsDefault(isDefault);
        receiver.setMemberId(currentUser.getId());
        Receiver re = receiverService.save(receiver);
        renderJson(re);
    }


    /**
     * 保存
     */
    public void defaultCount() {
        Member currentUser = memberService.getCurrentUser();
        JSONObject obj = new JSONObject();
        obj.put("resultCode", "1");
        obj.put("pendingPaymentOrderCount", orderService.count(null, Order.Status.pendingPayment, null, currentUser, null, null, null, null, null, null, false));
        obj.put("pendingShipmentOrderCount",orderService.count(null, Order.Status.pendingShipment, null, currentUser, null, null, null, null, null, null, null));
        obj.put("shippedOrderCount", orderService.count(null, Order.Status.shipped, null, currentUser, null, null, null, null, null, null, null));
        renderJson(obj);
    }
    /**
     * 默认地址
     */
    public void defaultaddress() {
            Member currentUser = memberService.getCurrentUser();
            Receiver defaultReceiver = receiverService.findDefault(currentUser);
            renderJson(defaultReceiver);
    }
    /**
     * 获取收货地址
     */
    public void addList() {
        try{
            Member currentUser = memberService.getCurrentUser();
            List<Receiver> list = receiverService.findList(currentUser);
            List<Receiver> list1 = new ArrayList<>();
            if(list!=null&&list.size()>0){
                for(Receiver r : list){
                    r.setCoupId(r.getId()+"");
                    r.setMembId(r.getMemberId()+"");
                    list1.add(r);
                }
            }
            renderJson(list1);
        }
        catch (Exception e){
            e.printStackTrace();
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
        String jifen = getPara("jifen");
        String add_jifen = getPara("add_jifen");
        String money1 = getPara("money");
        String add_money1 = getPara("add_money");
        String timestamp = getPara("timestamp")==null?getPara("timeStamp"):getPara("timestamp");
        String sign = getPara("sign");

        SortedMap<Object,Object> parameters = new TreeMap<Object, Object>();
        parameters.put("account",account);
        parameters.put("password",password==null||password.equals("null")?"":password);
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
        long jf = new BigDecimal(jifen).longValue();
        long ad_jf=new BigDecimal(add_jifen).longValue();
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


            if (jf > 0) {
                memberService.addPointV2(member, jf,ad_jf, PointLog.Type.reward, null);
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