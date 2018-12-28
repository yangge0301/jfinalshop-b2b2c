package com.jfinalshop.util;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class IdCreater {
	private static String Head = "";
	private static Map<String, AtomicLong> atomicMap = new HashMap<String, AtomicLong>();
	
	static{
		//构造唯一前缀
		try{
			InetAddress netAddress = InetAddress.getLocalHost();  
			String hostName = netAddress.getHostName();
			if(hostName!=null && hostName.indexOf("-")>-1){
				hostName = hostName.replaceAll("-", "");
			}
			Head = (Long.toHexString(hostName.hashCode()));
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println("IdCreater Exception");
		}
	}
	
	/**
	 * 生成id
	 * @return
	 */
	public static synchronized String getId(String name){
		AtomicLong atomicLong = atomicMap.get(name);
		if (null == atomicLong){
			atomicLong = new AtomicLong(System.currentTimeMillis());
			atomicMap.put(name, atomicLong);
		}
		return "PRE"+Head + Long.toHexString(System.currentTimeMillis() + atomicLong.incrementAndGet());
	}
	
	public static void main(String[] args) {
		System.out.println(getId("order"));
	}
}
