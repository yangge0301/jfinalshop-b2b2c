package com.jfinalshop.api.version;

import java.io.IOException;
import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.kit.PathKit;

/**
 * 版本管理器*
 * 
 */
public class VersionManager {
    private VersionProperty property; //文件配置
    private static String propertyName = "/version.xml"; //默认的配置文件
    private static VersionManager me = new VersionManager();

    public static VersionManager me() {
        return me;
    }
    
    public VersionManager() {
        this(propertyName);
    }
    
    public VersionManager(String propertyName) {
        try {
            //property = new VersionProperty(VersionManager.class.getResource(propertyName).getPath());
        	property = new VersionProperty(PathKit.getRootClassPath() + propertyName);
        	
        } catch (IOException e) {
            throw new RuntimeException(propertyName + " can not found", e);
        }
    }
    
    

    /**
     * 检查版本*
     * @param version 版本号
     * @param client 终端类型
     * @return 当前最新版本
     */
    public Version check(String version, String client) {
        if (property == null || StringUtils.isEmpty(version) || StringUtils.isEmpty(client)) {
            return null;
        }

        Version nowVersion = property.getNowVersion(ClientType.getClientType(client));
        
        BigDecimal bVersion = new BigDecimal(version);
        BigDecimal bNowVersion = new BigDecimal(nowVersion.getVersion());
        if (nowVersion == null || bNowVersion.compareTo(bVersion) < 0 || bNowVersion.compareTo(bVersion) == 0) {
            return null;
        }
        
        return nowVersion;
    }
}
