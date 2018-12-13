package com.jfinalshop.util;

import com.jfinal.kit.HttpKit;


public class JHttp {

    public static void get(String url) {
        System.out.println(url);
        HttpKit.get(url);
    }
}
