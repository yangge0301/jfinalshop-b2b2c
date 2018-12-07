package com.jfinalshop.service;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.servlet.http.HttpServletRequest;

import net.hasor.core.Singleton;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import com.jfinalshop.util.Assert;
import com.jfinalshop.util.RSAUtils;

/**
 * Service - RSA安全
 * 
 */
@Singleton
public class RSAService {

	/**
	 * 密钥大小
	 */
	private static final int KEY_SIZE = 1024;

	/**
	 * "私钥"属性名称
	 */
	private static final String PRIVATE_KEY_ATTRIBUTE_NAME = "privateKey";
	
	/**
	 * 生成密钥(添加私钥至Session并返回公钥)
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return 公钥
	 */
	public RSAPublicKey generateKey(HttpServletRequest request) {
		Assert.notNull(request);

		KeyPair keyPair = RSAUtils.generateKeyPair(KEY_SIZE);
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		request.getSession().setAttribute(PRIVATE_KEY_ATTRIBUTE_NAME, privateKey);
		return publicKey;
	}

	/**
	 * 移除私钥
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public void removePrivateKey(HttpServletRequest request) {
		Assert.notNull(request);

		request.getSession().removeAttribute(PRIVATE_KEY_ATTRIBUTE_NAME);
	}

	/**
	 * 解密参数
	 * 
	 * @param name
	 *            参数名称
	 * @param request
	 *            HttpServletRequest
	 * @return 解密内容
	 */
	public String decryptParameter(String name, HttpServletRequest request) {
		Assert.notNull(request);

		if (StringUtils.isEmpty(name)) {
			return null;
		}
		RSAPrivateKey privateKey = (RSAPrivateKey) request.getSession().getAttribute(PRIVATE_KEY_ATTRIBUTE_NAME);
		String parameter = request.getParameter(name);
		if (privateKey != null && StringUtils.isNotEmpty(parameter)) {
			try {
				return new String(RSAUtils.decrypt(privateKey, Base64.decodeBase64(parameter)));
			} catch (RuntimeException e) {
				return null;
			}
		}
		return null;
	}

}