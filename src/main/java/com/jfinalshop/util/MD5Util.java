package com.jfinalshop.util;
import net.hasor.core.InjectSettings;

import java.security.MessageDigest;
import java.util.*;

public class MD5Util {

    private static String isCheckMD5 = "true";

    public static String MD5(String s) {
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        try {
            byte[] btInput = s.getBytes();

            // 获得MD5摘要算法的 MessageDigest 对象

            MessageDigest mdInst = MessageDigest.getInstance("MD5");

            // 使用指定的字节更新摘要

            mdInst.update(btInput);

            // 获得密文

            byte[] md = mdInst.digest();

            // 把密文转换成十六进制的字符串形式

            int j = md.length;

            char str[] = new char[j * 2];

            int k = 0;

            for (int i = 0; i < j; i++) {

                byte byte0 = md[i];

                str[k++] = hexDigits[byte0 >>> 4 & 0xf];

                str[k++] = hexDigits[byte0 & 0xf];

            }

            return new String(str);

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        }

    }

    public static String createSign(SortedMap<Object,Object> parameters,String sign){
        if(isCheckMD5!=null&&isCheckMD5.equals("false")){
            return sign;
        }
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();//所有参与传参的参数按照accsii排序（升序）
        Iterator it = es.iterator();
        long times = System.currentTimeMillis();
        Object obj = parameters.get("timestamp")==null?parameters.get("timeStamp"):parameters.get("timestamp");
        long timestamp = Long.parseLong(obj.toString());
        if((times-timestamp)>=10*60*1000||(timestamp-times)>=10*60*1000){
            return "error";
        }
        // stringA="appid=wxd930ea5d5a258f4f&body=test&device_info=1000&mch_id=10000100&nonce_str=ibuaiVcKdpRxkhJA";
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            Object v = entry.getValue();
            if(null != v && !"".equals(v)
                    && !"sign".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + "wjn888WJN");
        System.out.println(sb.toString());
        // stringSignTemp="stringA&key=192006250b4c09247ec02edce69f6a2d"
        // sign=MD5(stringSignTemp).toUpperCase()="9A0A8659F005D6984697E2CA0A9CF3B7"
        return MD5(sb.toString());
    }
    public static void main(String[] args) {
        SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
        parameters.put("orderNo", "123456");
        parameters.put("timestamp", "12312312312123");
        String md5 = MD5("orderNo=123456&timestamp=12312312312123&key=wjn888WJN");

        System.out.println(md5);
        System.out.println(createSign(parameters,""));


    }

}
