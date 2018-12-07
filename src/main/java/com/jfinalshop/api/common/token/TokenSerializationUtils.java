package com.jfinalshop.api.common.token;
import java.io.Serializable;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.shiro.codec.Base64;

import com.jfinalshop.model.Member;

public class TokenSerializationUtils {

	public static String serialize(Member member) {
    	return  Base64.encodeToString(SerializationUtils.serialize((Serializable) member));
    }
    
    public static Member deserialize(String memberStr) {
    	return (Member) SerializationUtils.deserialize(Base64.decode(memberStr));
    }

}