package com.jfinalshop.util;

import net.hasor.core.InjectSettings;

public class CheckUtil {

    public static boolean checkSign(String account,String timestamp,String sign,String entryKey,String msg){
        String res = MD5Util.MD5(account+""+timestamp+""+entryKey);
        if(timestamp == null||timestamp.equals("")||account == null||account.equals("")||sign == null||sign.equals("")){
            msg="时间戳、account、sign不能为空";
            return false;
        }
        long nowtime = System.currentTimeMillis();
        long oldtime = Long.parseLong(timestamp);
        if((nowtime-oldtime)>10*60*1000||(oldtime>nowtime)){
            msg="时间戳太早或者太旧";
            return false;
        }
        if(res!=null&&res.equals(sign)){
            return  true;
        }
        msg="sign校验失败";
        return false;
    }
    public static String getSign(String key,String timestamp,String entryKey){
        return MD5Util.MD5(key+""+timestamp+""+entryKey);
    }
    public  static void main(String s[]){
        String value = "";
        System.out.println(getSign(value,"",""));

    }
}
