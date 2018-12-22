package com.jfinalshop.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.hasor.core.Init;
import net.hasor.core.Singleton;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import com.jfinalshop.plugin.LoginPlugin;
import com.jfinalshop.plugin.PaymentPlugin;
import com.jfinalshop.plugin.PromotionPlugin;
import com.jfinalshop.plugin.StoragePlugin;
import com.jfinalshop.plugin.alipayBankPayment.AlipayBankPaymentPlugin;
import com.jfinalshop.plugin.alipayDirectPayment.AlipayDirectPaymentPlugin;
import com.jfinalshop.plugin.alipayDualPayment.AlipayDualPaymentPlugin;
import com.jfinalshop.plugin.alipayEscowPayment.AlipayEscowPaymentPlugin;
import com.jfinalshop.plugin.alipayLogin.AlipayLoginPlugin;
import com.jfinalshop.plugin.alipayPayment.AlipayPaymentPlugin;
import com.jfinalshop.plugin.discountPromotion.DiscountPromotionPlugin;
import com.jfinalshop.plugin.ftpStorage.FtpStoragePlugin;
import com.jfinalshop.plugin.fullReductionPromotion.FullReductionPromotionPlugin;
import com.jfinalshop.plugin.localStorage.LocalStoragePlugin;
import com.jfinalshop.plugin.ossStorage.OssStoragePlugin;
import com.jfinalshop.plugin.qqLogin.QqLoginPlugin;
import com.jfinalshop.plugin.unionpayPayment.UnionpayPaymentPlugin;
import com.jfinalshop.plugin.weiboLogin.WeiboLoginPlugin;
import com.jfinalshop.plugin.weixinLogin.WeixinLoginPlugin;
import com.jfinalshop.plugin.weixinNativePayment.WeixinNativePaymentPlugin;
import com.jfinalshop.plugin.weixinPublicPayment.WeixinPublicPaymentPlugin;

/**
 * Service - 插件
 * 
 */
@Singleton
public class PluginService {
	
	// 支付插件
	private List<PaymentPlugin> paymentPlugins = new ArrayList<>();
	// 存储插件
	private List<StoragePlugin> storagePlugins = new ArrayList<>();
	// 登录插件
	private List<LoginPlugin> loginPlugins = new ArrayList<>();
	// 促销插件
	private List<PromotionPlugin> promotionPlugins = new ArrayList<>();
	
	// 支付插件
	private Map<String, PaymentPlugin> paymentPluginMap = new HashMap<>();
	// 存储插件
	private Map<String, StoragePlugin> storagePluginMap = new HashMap<>();
	// 登录插件
	private Map<String, LoginPlugin> loginPluginMap = new HashMap<>();
	// 促销插件
	private Map<String, PromotionPlugin> promotionPluginMap = new HashMap<>();
	
	/**
	 * 构造方法
	 */
	@Init
	public void init() {
		// 支付宝(纯网关)
		AlipayBankPaymentPlugin alipayBankPaymentPlugin = new AlipayBankPaymentPlugin();
		
		// 支付宝(即时交易)
		AlipayDirectPaymentPlugin alipayDirectPaymentPlugin  = new AlipayDirectPaymentPlugin();
		
		// 支付宝(双接口)
		AlipayDualPaymentPlugin alipayDualPaymentPlugin = new AlipayDualPaymentPlugin();
		
		// 支付宝(担保交易)
		AlipayEscowPaymentPlugin alipayEscowPaymentPlugin = new AlipayEscowPaymentPlugin();
		
		// 支付宝支付
		AlipayPaymentPlugin alipayPaymentPlugin = new AlipayPaymentPlugin();
		
		// 微信支付(扫码支付)
		WeixinNativePaymentPlugin weixinNativePaymentPlugin = new WeixinNativePaymentPlugin();
		
		// 微信支付(公众号支付)
		WeixinPublicPaymentPlugin weixinPublicPaymentPlugin = new WeixinPublicPaymentPlugin();
		
		// 银联在线支付
		UnionpayPaymentPlugin unionpayPaymentPlugin = new UnionpayPaymentPlugin();
		
		
		// 折扣促销
		DiscountPromotionPlugin discountPromotionPlugin = new DiscountPromotionPlugin();
		
		// 满减促销
		FullReductionPromotionPlugin fullReductionPromotionPlugin = new FullReductionPromotionPlugin();
		
		
		// FTP存储
		FtpStoragePlugin ftpStoragePlugin = new FtpStoragePlugin();
				
		// 本地文件存储
		LocalStoragePlugin localStoragePlugin = new LocalStoragePlugin();
		
		// 阿里云存储
		OssStoragePlugin ossStoragePlugin = new OssStoragePlugin();
		
		
		// 支付宝快捷登录
		AlipayLoginPlugin alipayLoginPlugin = new AlipayLoginPlugin();
				
		// QQ登录
		QqLoginPlugin qqLoginPlugin = new QqLoginPlugin();
		
		// 新浪微博登录
		WeiboLoginPlugin weiboLoginPlugin = new WeiboLoginPlugin();
		
		// 微信登录
		WeixinLoginPlugin weixinLoginPlugin = new WeixinLoginPlugin();
		
		/*****  支付插件  ******/
//		paymentPlugins.add(alipayBankPaymentPlugin);
//		paymentPlugins.add(alipayDirectPaymentPlugin);
//		paymentPlugins.add(alipayDualPaymentPlugin);
//		paymentPlugins.add(alipayEscowPaymentPlugin);
//		paymentPlugins.add(alipayPaymentPlugin);
		paymentPlugins.add(weixinNativePaymentPlugin);
//		paymentPlugins.add(weixinPublicPaymentPlugin);
//		paymentPlugins.add(unionpayPaymentPlugin);
		
//		paymentPluginMap.put(alipayBankPaymentPlugin.getId(), alipayBankPaymentPlugin);
//		paymentPluginMap.put(alipayDirectPaymentPlugin.getId(), alipayDirectPaymentPlugin);
//		paymentPluginMap.put(alipayDualPaymentPlugin.getId(), alipayDualPaymentPlugin);
//		paymentPluginMap.put(alipayEscowPaymentPlugin.getId(), alipayEscowPaymentPlugin);
//		paymentPluginMap.put(alipayPaymentPlugin.getId(), alipayPaymentPlugin);
		paymentPluginMap.put(weixinNativePaymentPlugin.getId(), weixinNativePaymentPlugin);
		paymentPluginMap.put(weixinPublicPaymentPlugin.getId(), weixinPublicPaymentPlugin);
//		paymentPluginMap.put(unionpayPaymentPlugin.getId(), unionpayPaymentPlugin);
		
		/*****  促销插件  ******/
		promotionPlugins.add(fullReductionPromotionPlugin);
		promotionPlugins.add(discountPromotionPlugin);
		
		promotionPluginMap.put(fullReductionPromotionPlugin.getId(), fullReductionPromotionPlugin);
		promotionPluginMap.put(discountPromotionPlugin.getId(), discountPromotionPlugin);
		
		/*****  存储插件  ******/
		storagePlugins.add(ossStoragePlugin);
		storagePlugins.add(localStoragePlugin);
		storagePlugins.add(ftpStoragePlugin);
		
		storagePluginMap.put(ossStoragePlugin.getId(), ossStoragePlugin);
		storagePluginMap.put(localStoragePlugin.getId(), localStoragePlugin);
		storagePluginMap.put(localStoragePlugin.getId(), localStoragePlugin);
		
		/*****  登录插件  ******/
		loginPlugins.add(weixinLoginPlugin);
		loginPlugins.add(weiboLoginPlugin);
		loginPlugins.add(qqLoginPlugin);
		loginPlugins.add(alipayLoginPlugin);
		
		loginPluginMap.put(weixinLoginPlugin.getId(), weixinLoginPlugin);
		loginPluginMap.put(weiboLoginPlugin.getId(), weiboLoginPlugin);
		loginPluginMap.put(qqLoginPlugin.getId(), qqLoginPlugin);
		loginPluginMap.put(alipayLoginPlugin.getId(), alipayLoginPlugin);
	}
	
	/**
	 * 获取支付插件
	 * 
	 * @return 支付插件
	 */
	public List<PaymentPlugin> getPaymentPlugins() {
		Collections.sort(paymentPlugins);
		return paymentPlugins;
	}

	/**
	 * 获取存储插件
	 * 
	 * @return 存储插件
	 */
	public List<StoragePlugin> getStoragePlugins() {
		Collections.sort(storagePlugins);
		return storagePlugins;
	}

	/**
	 * 获取登录插件
	 * 
	 * @return 登录插件
	 */
	public List<LoginPlugin> getLoginPlugins() {
		Collections.sort(loginPlugins);
		return loginPlugins;
	}

	/**
	 * 获取促销插件
	 * 
	 * @return 促销插件
	 */
	public List<PromotionPlugin> getPromotionPlugins() {
		Collections.sort(promotionPlugins);
		return promotionPlugins;
	}


	/**
	 * 获取支付插件
	 * 
	 * @param isEnabled
	 *            是否启用
	 * @return 支付插件
	 */
	public List<PaymentPlugin> getPaymentPlugins(final boolean isEnabled) {
		List<PaymentPlugin> result = new ArrayList<>();
		CollectionUtils.select(paymentPlugins, new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				PaymentPlugin paymentPlugin = (PaymentPlugin) object;
				return paymentPlugin.getIsEnabled() == isEnabled;
			}
		}, result);
		Collections.sort(result);
		return result;
	}

	/**
	 * 获取有效支付插件
	 * 
	 * @param request
	 *            request
	 * @return 有效支付插件
	 */
	public List<PaymentPlugin> getActivePaymentPlugins(final HttpServletRequest request) {
		List<PaymentPlugin> result = new ArrayList<>();
		CollectionUtils.select(getPaymentPlugins(true), new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				PaymentPlugin paymentPlugin = (PaymentPlugin) object;
				return paymentPlugin.supports(request);
			}
		}, result);
		return result;
	}


	/**
	 * 获取存储插件
	 * 
	 * @param isEnabled
	 *            是否启用
	 * @return 存储插件
	 */
	public List<StoragePlugin> getStoragePlugins(final boolean isEnabled) {
		List<StoragePlugin> result = new ArrayList<>();
		CollectionUtils.select(storagePlugins, new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				StoragePlugin storagePlugin = (StoragePlugin) object;
				return storagePlugin.getIsEnabled() == isEnabled;
			}
		}, result);
		Collections.sort(result);
		return result;
	}

	/**
	 * 获取登录插件
	 * 
	 * @param isEnabled
	 *            是否启用
	 * @return 登录插件
	 */
	public List<LoginPlugin> getLoginPlugins(final boolean isEnabled) {
		List<LoginPlugin> result = new ArrayList<>();
		CollectionUtils.select(loginPlugins, new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				LoginPlugin loginPlugin = (LoginPlugin) object;
				return loginPlugin.getIsEnabled() == isEnabled;
			}
		}, result);
		Collections.sort(result);
		return result;
	}

	/**
	 * 获取有效登录插件
	 * 
	 * @param request
	 *            request
	 * @return 有效登录插件
	 */
	public List<LoginPlugin> getActiveLoginPlugins(final HttpServletRequest request) {
		List<LoginPlugin> result = new ArrayList<>();
		List<LoginPlugin> loginPlugins = getLoginPlugins(true);
		CollectionUtils.select(loginPlugins, new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				LoginPlugin loginPlugin = (LoginPlugin) object;
				return loginPlugin.supports(request);
			}
		}, result);
		return result;
	}

	/**
	 * 获取促销插件
	 * 
	 * @param isEnabled
	 *            是否启用
	 * @return 促销插件
	 */
	public List<PromotionPlugin> getPromotionPlugins(final boolean isEnabled) {
		List<PromotionPlugin> result = new ArrayList<>();
		CollectionUtils.select(promotionPlugins, new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				PromotionPlugin promotionPlugin = (PromotionPlugin) object;
				return promotionPlugin.getIsEnabled() == isEnabled;
			}
		}, result);
		Collections.sort(result);
		return result;
	}

	/**
	 * 获取支付插件
	 * 
	 * @param id
	 *            ID
	 * @return 支付插件
	 */
	public PaymentPlugin getPaymentPlugin(String id) {
		return paymentPluginMap.get(id);
	}


	/**
	 * 获取存储插件
	 * 
	 * @param id
	 *            ID
	 * @return 存储插件
	 */
	public StoragePlugin getStoragePlugin(String id) {
		return storagePluginMap.get(id);
	}


	/**
	 * 获取登录插件
	 * 
	 * @param id
	 *            ID
	 * @return 登录插件
	 */
	public LoginPlugin getLoginPlugin(String id) {
		return loginPluginMap.get(id);
	}

	/**
	 * 获取促销插件
	 * 
	 * @param id
	 *            ID
	 * @return 促销插件
	 */
	public PromotionPlugin getPromotionPlugin(String id) {
		return promotionPluginMap.get(id);
	}

}