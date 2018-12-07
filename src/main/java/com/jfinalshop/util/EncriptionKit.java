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
	
	public static  void main(String args[]) throws  Exception{
		String pubKey="MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKHGwq7q2RmwuRgKxBypQHw0mYu4BQZ3eMsTrdK8E6igRcxsobUC7uT0SoxIjl1WveWniCASejoQtn/BY6hVKWsCAwEAAQ==";
		String meKey="MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAocbCrurZGbC5GArEHKlAfDSZi7gFBnd4yxOt0rwTqKBFzGyhtQLu5PRKjEiOXVa95aeIIBJ6OhC2f8FjqFUpawIDAQABAkAPejKaBYHrwUqUEEOe8lpnB6lBAsQIUFnQI/vXU4MV+MhIzW0BLVZCiarIQqUXeOhThVWXKFt8GxCykrrUsQ6BAiEA4vMVxEHBovz1di3aozzFvSMdsjTcYRRo82hS5Ru2/OECIQC2fAPoXixVTVY7bNMeuxCP4954ZkXp7fEPDINCjcQDywIgcc8XLkkPcs3Jxk7uYofaXaPbg39wuJpEmzPIxi3k0OECIGubmdpOnin3HuCP/bbjbJLNNoUdGiEmFL5hDI4UdwAdAiEAtcAwbm08bKN7pwwvyqaCBC//VnEWaq39DCzxr+Z2EIk=";
		String str = "root0822.Zyy";
		System.out.println(ConfigTools.encrypt(meKey,str));
		System.out.println(ConfigTools.decrypt(pubKey,ConfigTools.encrypt(meKey,str)));

	}
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
