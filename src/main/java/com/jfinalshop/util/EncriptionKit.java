/**
 *  2012-5-25 下午4:59:05
 * @create_user wangrenhui
 * @whattodo
 * @modify_time like:date1/date2
 * @modify_user like:user1/user2
 * @modify_content like:content1/content2
 */
package com.jfinalshop.util;

import java.security.MessageDigest;

import com.alibaba.druid.filter.config.ConfigTools;

/**
 * @author wangrenhui
 *         加密工具
 *         2012-5-25 下午4:59:05
 */
public class EncriptionKit {
	
	
  /**
   * password解密
   *
   */
	public static String passwordDecrypt(String publicKey, String password) {
		String result = null;
		try {
			result = ConfigTools.decrypt(publicKey, password);// 解密
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	   * md5加密
	   *
	   * @param srcStr input string
	   * @return output encription string
	   */
	  public static final String encrypt(String srcStr) {
	    try {
	      String result = "";
	      MessageDigest md = MessageDigest.getInstance("MD5");
	      byte[] bytes = md.digest(srcStr.getBytes("utf-8"));
	      for (byte b : bytes) {
	        String hex = Integer.toHexString(b & 0xFF).toUpperCase();
	        result += ((hex.length() == 1) ? "0" : "") + hex;
	      }
	      return result;
	    } catch (Exception e) {
	      throw new RuntimeException(e);
	    }
	  }
}
