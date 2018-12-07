package com.jfinalshop.shiro.session;
import java.io.Serializable;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.session.Session;

public class ShiroSerializationUtils {

	public static String serialize(Session session) {
    	return  Base64.encodeToString(SerializationUtils.serialize((Serializable) session));
    }
    
    public static Session deserialize(String sessionStr) {
    	return (Session) SerializationUtils.deserialize(Base64.decode(sessionStr));
    }

}