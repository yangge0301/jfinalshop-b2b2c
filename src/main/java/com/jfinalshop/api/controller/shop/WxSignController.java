package com.jfinalshop.api.controller.shop;


import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.kit.Kv;
import com.jfinal.kit.LogKit;
import com.jfinalshop.api.controller.util.HttpClient;
import com.jfinalshop.api.controller.util.SignUtil;
import com.jfinalshop.api.controller.util.resbean.JsonResult;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.MemberRank;
import com.jfinalshop.model.PointLog;
import com.jfinalshop.service.MemberRankService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.PluginService;
import com.jfinalshop.shiro.core.SubjectKit;
import com.jfinalshop.shiro.hasher.Hasher;
import com.jfinalshop.shiro.hasher.HasherInfo;
import com.jfinalshop.shiro.hasher.HasherKit;
import com.jfinalshop.shiro.session.RedisManager;
import com.jfinalshop.util.IpUtil;
import com.jfinalshop.util.StringUtils;
import net.hasor.core.Inject;
import org.jsoup.helper.StringUtil;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;


public class WxSignController  extends Controller {
    private static  String appId="wx8ccc40e63fabeebd";
    private static  String appSecret="0cd97c839191770ecfcab3114e65b0b4";
    private static  String serverUrl="https://api.mubag.top/";

    @Inject
    private MemberRankService memberRankService;

    @Inject
    private MemberService memberService;
    @Inject
    private PluginService pluginService;
    @Inject
    private RedisManager redisManager;
    @ActionKey("/wxsign")
    public void index(){
        try{
            String signature = getRequest().getParameter("signature");
            String timestamp = getRequest().getParameter("timestamp");
            String nonce = getRequest().getParameter("nonce");
            String echostr = getRequest().getParameter("echostr");
            LogKit.info("signature="+signature+"&timestamp="+timestamp+"&nonce="+nonce+"&echostr="+echostr);
            if (SignUtil.checkSignature(signature, timestamp, nonce)) {
                if(null != echostr && !"".equals(echostr)){
                    //服务器验证消息请求
                    renderText(echostr);
                    return;
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        renderText("");
    }
    public boolean checklogin(String sessionid){
        try{
            String obj = redisManager.get(sessionid);
            if(obj == null||obj.equals("")){
                return false;
            }
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @ActionKey("/redirectTo")
    public void redirectTo(){
        try{
            String sessionid = getPara("sessionid");
            LogKit.info("redirectTo:==>sessionid="+sessionid);
            if(sessionid!=null&&!sessionid.equals("")&&!sessionid.trim().equals("")){
                String obj = redisManager.get(sessionid);
                if(obj == null||obj.equals("")){
                    renderJson(new JsonResult("0","登录状态失效",null,null,null));
                    return;
                }
                else{
                    JSONObject jsobj = JSONObject.parseObject(obj);
                    String registerUrl = serverUrl +"member/register/login?account="+jsobj.getString("openId")+"&password=3";
                    String sessid = getRequest().getHeader("Set-Cookie");
                    getResponse().setHeader("Set-Cookie",sessid);
                    renderJson(new JsonResult("1","成功",null,registerUrl,sessionid));
                    return;
                }
            }
            else{
                renderJson(new JsonResult("0","登录状态失效.",null,null,null));
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

}
