package com.jfinalshop.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.hasor.core.Inject;
import net.hasor.core.Singleton;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.render.FreeMarkerRender;
import com.jfinalshop.Setting;
import com.jfinalshop.TemplateConfig;
import com.jfinalshop.entity.SafeKey;
import com.jfinalshop.model.Business;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Member.PasswordType;
import com.jfinalshop.model.MessageConfig;
import com.jfinalshop.model.Order;
import com.jfinalshop.model.ProductNotify;
import com.jfinalshop.model.Store;
import com.jfinalshop.util.Assert;
import com.jfinalshop.util.FreeMarkerUtils;
import com.jfinalshop.util.SystemUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Service - 邮件
 * 
 */
@Singleton
public class MailService {

	private Configuration configuration = FreeMarkerRender.getConfiguration();
	private final ExecutorService executorService = Executors.newFixedThreadPool(4);  
	private final Res res = I18n.use();
	
	@Inject
	private MessageConfigService messageConfigService;
	
	/**
	 * 添加邮件发送任务
	 * 
	 * @param smtpHost
	 *            SMTP服务器地址
	 * @param smtpPort
	 *            SMTP服务器端口
	 * @param smtpUsername
	 *            SMTP用户名
	 * @param smtpPassword
	 *            SMTP密码
	 * @param smtpSSLEnabled
	 *            SMTP是否启用SSL
	 * @param smtpFromMail
	 *            发件人邮箱
	 * @param toMails
	 *            收件人邮箱
	 * @param subject
	 *            主题
	 * @param content
	 *            内容
	 */
	private void addSendTask(final String smtpHost, final int smtpPort, final String smtpUsername, final String smtpPassword, final boolean smtpSSLEnabled, final String smtpFromMail, final String[] toMails, final String subject, final String content) {
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				send(smtpHost, smtpPort, smtpUsername, smtpPassword, smtpSSLEnabled, smtpFromMail, toMails, subject, content);
			}
		});
	}
	
	/**
	 * 发送邮件
	 * 
	 * @param smtpHost
	 *            SMTP服务器地址
	 * @param smtpPort
	 *            SMTP服务器端口
	 * @param smtpUsername
	 *            SMTP用户名
	 * @param smtpPassword
	 *            SMTP密码
	 * @param smtpSSLEnabled
	 *            SMTP是否启用SSL
	 * @param smtpFromMail
	 *            发件人邮箱
	 * @param toMails
	 *            收件人邮箱
	 * @param subject
	 *            主题
	 * @param content
	 *            内容
	 */
	private void send(String smtpHost, int smtpPort, String smtpUsername, String smtpPassword, boolean smtpSSLEnabled, String smtpFromMail, String[] toMails, String subject, String content) {
		Assert.hasText(smtpHost);
		Assert.hasText(smtpUsername);
		Assert.hasText(smtpPassword);
		Assert.hasText(smtpFromMail);
		Assert.notEmpty(toMails);
		Assert.hasText(subject);
		Assert.hasText(content);

		try {
			Setting setting = SystemUtils.getSetting();
			HtmlEmail email = new HtmlEmail();
			email.setHostName(smtpHost);
			email.setSmtpPort(smtpPort);
			email.setAuthentication(smtpUsername, smtpPassword);
			email.setSSLOnConnect(smtpSSLEnabled);
			email.setFrom(smtpFromMail, setting.getSiteName());
			email.addTo(toMails);
			email.setSubject(subject);
			email.setCharset("UTF-8");
			email.setHtmlMsg(content);
			email.send();
		} catch (EmailException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	/**
	 * 发送邮件
	 * 
	 * @param smtpHost
	 *            SMTP服务器地址
	 * @param smtpPort
	 *            SMTP服务器端口
	 * @param smtpUsername
	 *            SMTP用户名
	 * @param smtpPassword
	 *            SMTP密码
	 * @param smtpSSLEnabled
	 *            SMTP是否启用SSL
	 * @param smtpFromMail
	 *            发件人邮箱
	 * @param toMails
	 *            收件人邮箱
	 * @param subject
	 *            主题
	 * @param content
	 *            内容
	 * @param async
	 *            是否异步
	 */
	public void send(String smtpHost, int smtpPort, String smtpUsername, String smtpPassword, boolean smtpSSLEnabled, String smtpFromMail, String[] toMails, String subject, String content, boolean async) {
		Assert.hasText(smtpHost);
		Assert.hasText(smtpUsername);
		Assert.hasText(smtpPassword);
		Assert.hasText(smtpFromMail);
		Assert.notEmpty(toMails);
		Assert.hasText(subject);
		Assert.hasText(content);

		if (async) {
			addSendTask(smtpHost, smtpPort, smtpUsername, smtpPassword, smtpSSLEnabled, smtpFromMail, toMails, subject, content);
		} else {
			send(smtpHost, smtpPort, smtpUsername, smtpPassword, smtpSSLEnabled, smtpFromMail, toMails, subject, content);
		}
	}
	
	/**
	 * 发送邮件
	 * 
	 * @param smtpHost
	 *            SMTP服务器地址
	 * @param smtpPort
	 *            SMTP服务器端口
	 * @param smtpUsername
	 *            SMTP用户名
	 * @param smtpPassword
	 *            SMTP密码
	 * @param smtpSSLEnabled
	 *            SMTP是否启用SSL
	 * @param smtpFromMail
	 *            发件人邮箱
	 * @param toMails
	 *            收件人邮箱
	 * @param subject
	 *            主题
	 * @param templatePath
	 *            模板路径
	 * @param model
	 *            数据
	 * @param async
	 *            是否异步
	 */
	public void send(String smtpHost, int smtpPort, String smtpUsername, String smtpPassword, boolean smtpSSLEnabled, String smtpFromMail, String[] toMails, String subject, String templatePath, Map<String, Object> model, boolean async) {
		Assert.hasText(smtpHost);
		Assert.hasText(smtpUsername);
		Assert.hasText(smtpPassword);
		Assert.hasText(smtpFromMail);
		Assert.notEmpty(toMails);
		Assert.hasText(subject);
		Assert.hasText(templatePath);

		try {
			Template template = configuration.getTemplate(templatePath);
			String content = FreeMarkerUtils.processTemplateIntoString(template, model);
			send(smtpHost, smtpPort, smtpUsername, smtpPassword, smtpSSLEnabled, smtpFromMail, toMails, subject, content, async);
		} catch (TemplateException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 发送邮件
	 * 
	 * @param toMails
	 *            收件人邮箱
	 * @param subject
	 *            主题
	 * @param content
	 *            内容
	 * @param async
	 *            是否异步
	 */
	public void send(String[] toMails, String subject, String content, boolean async) {
		Assert.notEmpty(toMails);
		Assert.hasText(subject);
		Assert.hasText(content);

		Setting setting = SystemUtils.getSetting();
		String smtpHost = setting.getSmtpHost();
		int smtpPort = setting.getSmtpPort();
		String smtpUsername = setting.getSmtpUsername();
		String smtpPassword = setting.getSmtpPassword();
		boolean smtpSSLEnabled = setting.getSmtpSSLEnabled();
		String smtpFromMail = setting.getSmtpFromMail();
		send(smtpHost, smtpPort, smtpUsername, smtpPassword, smtpSSLEnabled, smtpFromMail, toMails, subject, content, async);
	}

	/**
	 * 发送邮件
	 * 
	 * @param toMails
	 *            收件人邮箱
	 * @param subject
	 *            主题
	 * @param templatePath
	 *            模板路径
	 * @param model
	 *            数据
	 * @param async
	 *            是否异步
	 */
	public void send(String[] toMails, String subject, String templatePath, Map<String, Object> model, boolean async) {
		Assert.notEmpty(toMails);
		Assert.hasText(subject);
		Assert.hasText(templatePath);

		Setting setting = SystemUtils.getSetting();
		String smtpHost = setting.getSmtpHost();
		int smtpPort = setting.getSmtpPort();
		String smtpUsername = setting.getSmtpUsername();
		String smtpPassword = setting.getSmtpPassword();
		boolean smtpSSLEnabled = setting.getSmtpSSLEnabled();
		String smtpFromMail = setting.getSmtpFromMail();
		send(smtpHost, smtpPort, smtpUsername, smtpPassword, smtpSSLEnabled, smtpFromMail, toMails, subject, templatePath, model, async);
	}

	/**
	 * 发送邮件(异步)
	 * 
	 * @param toMail
	 *            收件人邮箱
	 * @param subject
	 *            主题
	 * @param content
	 *            内容
	 */
	public void send(String toMail, String subject, String content) {
		Assert.hasText(toMail);
		Assert.hasText(subject);
		Assert.hasText(content);

		send(new String[] { toMail }, subject, content, true);
	}

	/**
	 * 发送邮件(异步)
	 * 
	 * @param toMail
	 *            收件人邮箱
	 * @param subject
	 *            主题
	 * @param templatePath
	 *            模板路径
	 * @param model
	 *            数据
	 */
	public void send(String toMail, String subject, String templatePath, Map<String, Object> model) {
		Assert.hasText(toMail);
		Assert.hasText(subject);
		Assert.hasText(templatePath);

		send(new String[] { toMail }, subject, templatePath, model, true);
	}

	/**
	 * 发送SMTP测试邮件(同步)
	 * 
	 * @param smtpHost
	 *            SMTP服务器地址
	 * @param smtpPort
	 *            SMTP服务器端口
	 * @param smtpUsername
	 *            SMTP用户名
	 * @param smtpPassword
	 *            SMTP密码
	 * @param smtpSSLEnabled
	 *            SMTP是否启用SSL
	 * @param smtpFromMail
	 *            发件人邮箱
	 * @param toMail
	 *            收件人邮箱
	 */
	public void sendTestSmtpMail(String smtpHost, int smtpPort, String smtpUsername, String smtpPassword, boolean smtpSSLEnabled, String smtpFromMail, String toMail) {
		Assert.hasText(smtpHost);
		Assert.hasText(smtpUsername);
		Assert.hasText(smtpPassword);
		Assert.hasText(smtpFromMail);
		Assert.hasText(toMail);

		Setting setting = SystemUtils.getSetting();
		//String subject = SpringUtils.getMessage("admin.mail.testSmtpSubject", setting.getSiteName());
		String subject = res.format("admin.mail.testSmtpSubject", setting.getSiteName());
		TemplateConfig templateConfig = SystemUtils.getTemplateConfig("testSmtpMail");
		send(smtpHost, smtpPort, smtpUsername, smtpPassword, smtpSSLEnabled, smtpFromMail, new String[] { toMail }, subject, templateConfig.getTemplatePath(), null, false);
	}

	/**
	 * 发送忘记密码邮件(异步)
	 * 
	 * @param member
	 *            会员
	 */
	public void sendForgotMemberPasswordMail(Member member) {
		Setting setting = SystemUtils.getSetting();
		Map<String, Object> model = new HashMap<>();
		
		String toMail = member.getEmail();
		String username = member.getUsername();
		PasswordType passwordType = PasswordType.member;
		
		SafeKey safeKey = new SafeKey();
		safeKey.setExpire(member.getSafekeyExpire());
		safeKey.setValue(member.getSafekeyValue());
		String subject = res.format("shop.mail.forgotPasswordSubject", setting.getSiteName());
		TemplateConfig templateConfig = SystemUtils.getTemplateConfig("forgotPasswordMail");
		model.put("type", passwordType);
		model.put("username", username);
		model.put("safeKey", safeKey);
		send(toMail, subject, templateConfig.getTemplatePath(), model);
	}
	
	/**
	 * 发送忘记密码邮件(异步)
	 * 
	 * @param business
	 *            商家
	 */
	public void sendForgotBusinessPasswordMail(Business business) {
		Setting setting = SystemUtils.getSetting();
		Map<String, Object> model = new HashMap<>();
		
		String toMail = business.getEmail();
		String username = business.getUsername();
		PasswordType passwordType = PasswordType.business;
		
		SafeKey safeKey = new SafeKey();
		safeKey.setExpire(business.getSafekeyExpire());
		safeKey.setValue(business.getSafekeyValue());
		String subject = res.format("shop.mail.forgotPasswordSubject", setting.getSiteName());
		TemplateConfig templateConfig = SystemUtils.getTemplateConfig("forgotPasswordMail");
		model.put("type", passwordType);
		model.put("username", username);
		model.put("safeKey", safeKey);
		send(toMail, subject, templateConfig.getTemplatePath(), model);
	}

	/**
	 * 发送到货通知邮件(异步)
	 * 
	 * @param productNotify
	 *            到货通知
	 */
	public void sendProductNotifyMail(ProductNotify productNotify) {
		if (productNotify == null || StringUtils.isEmpty(productNotify.getEmail())) {
			return;
		}
		Setting setting = SystemUtils.getSetting();
		Map<String, Object> model = new HashMap<>();
		model.put("productNotify", productNotify);
		String subject = res.format("shop.mail.productNotifySubject", setting.getSiteName());
		TemplateConfig templateConfig = SystemUtils.getTemplateConfig("productNotifyMail");
		send(productNotify.getEmail(), subject, templateConfig.getTemplatePath(), model);
	}

	/**
	 * 发送会员注册邮件(异步)
	 * 
	 * @param member
	 *            会员
	 */
	public void sendRegisterMemberMail(Member member) {
		if (member == null || StringUtils.isEmpty(member.getEmail())) {
			return;
		}
		MessageConfig messageConfig = messageConfigService.find(MessageConfig.Type.registerMember);
		if (messageConfig == null || !messageConfig.getIsMailEnabled()) {
			return;
		}
		Setting setting = SystemUtils.getSetting();
		Map<String, Object> model = new HashMap<>();
		model.put("member", member);
		String subject = res.format("shop.mail.registerMemberSubject", setting.getSiteName());
		TemplateConfig templateConfig = SystemUtils.getTemplateConfig("registerMemberMail");
		send(member.getEmail(), subject, templateConfig.getTemplatePath(), model);
	}

	/**
	 * 发送订单创建邮件(异步)
	 * 
	 * @param order
	 *            订单
	 */
	public void sendCreateOrderMail(Order order) {
		if (order == null || order.getMember() == null || StringUtils.isEmpty(order.getMember().getEmail())) {
			return;
		}
		MessageConfig messageConfig = messageConfigService.find(MessageConfig.Type.createOrder);
		if (messageConfig == null || !messageConfig.getIsMailEnabled()) {
			return;
		}
		Setting setting = SystemUtils.getSetting();
		Map<String, Object> model = new HashMap<>();
		model.put("order", order);
		String subject = res.format("shop.mail.createOrderSubject", setting.getSiteName());
		TemplateConfig templateConfig = SystemUtils.getTemplateConfig("createOrderMail");
		send(order.getMember().getEmail(), subject, templateConfig.getTemplatePath(), model);
	}

	/**
	 * 发送订单更新邮件(异步)
	 * 
	 * @param order
	 *            订单
	 */
	public void sendUpdateOrderMail(Order order) {
		if (order == null || order.getMember() == null || StringUtils.isEmpty(order.getMember().getEmail())) {
			return;
		}
		MessageConfig messageConfig = messageConfigService.find(MessageConfig.Type.updateOrder);
		if (messageConfig == null || !messageConfig.getIsMailEnabled()) {
			return;
		}
		Setting setting = SystemUtils.getSetting();
		Map<String, Object> model = new HashMap<>();
		model.put("order", order);
		String subject = res.format("shop.mail.updateOrderSubject", setting.getSiteName());
		TemplateConfig templateConfig = SystemUtils.getTemplateConfig("updateOrderMail");
		send(order.getMember().getEmail(), subject, templateConfig.getTemplatePath(), model);
	}

	/**
	 * 发送订单取消邮件(异步)
	 * 
	 * @param order
	 *            订单
	 */
	public void sendCancelOrderMail(Order order) {
		if (order == null || order.getMember() == null || StringUtils.isEmpty(order.getMember().getEmail())) {
			return;
		}
		MessageConfig messageConfig = messageConfigService.find(MessageConfig.Type.cancelOrder);
		if (messageConfig == null || !messageConfig.getIsMailEnabled()) {
			return;
		}
		Setting setting = SystemUtils.getSetting();
		Map<String, Object> model = new HashMap<>();
		model.put("order", order);
		String subject = res.format("shop.mail.cancelOrderSubject", setting.getSiteName());
		TemplateConfig templateConfig = SystemUtils.getTemplateConfig("cancelOrderMail");
		send(order.getMember().getEmail(), subject, templateConfig.getTemplatePath(), model);
	}

	/**
	 * 发送订单审核邮件(异步)
	 * 
	 * @param order
	 *            订单
	 */
	public void sendReviewOrderMail(Order order) {
		if (order == null || order.getMember() == null || StringUtils.isEmpty(order.getMember().getEmail())) {
			return;
		}
		MessageConfig messageConfig = messageConfigService.find(MessageConfig.Type.reviewOrder);
		if (messageConfig == null || !messageConfig.getIsMailEnabled()) {
			return;
		}
		Setting setting = SystemUtils.getSetting();
		Map<String, Object> model = new HashMap<>();
		model.put("order", order);
		String subject = res.format("shop.mail.reviewOrderSubject", setting.getSiteName());
		TemplateConfig templateConfig = SystemUtils.getTemplateConfig("reviewOrderMail");
		send(order.getMember().getEmail(), subject, templateConfig.getTemplatePath(), model);
	}

	/**
	 * 发送订单收款邮件(异步)
	 * 
	 * @param order
	 *            订单
	 */
	public void sendPaymentOrderMail(Order order) {
		if (order == null || order.getMember() == null || StringUtils.isEmpty(order.getMember().getEmail())) {
			return;
		}
		MessageConfig messageConfig = messageConfigService.find(MessageConfig.Type.paymentOrder);
		if (messageConfig == null || !messageConfig.getIsMailEnabled()) {
			return;
		}
		Setting setting = SystemUtils.getSetting();
		Map<String, Object> model = new HashMap<>();
		model.put("order", order);
		String subject = res.format("shop.mail.paymentOrderSubject", setting.getSiteName());
		TemplateConfig templateConfig = SystemUtils.getTemplateConfig("paymentOrderMail");
		send(order.getMember().getEmail(), subject, templateConfig.getTemplatePath(), model);
	}

	/**
	 * 发送订单退款邮件(异步)
	 * 
	 * @param order
	 *            订单
	 */
	public void sendRefundsOrderMail(Order order) {
		if (order == null || order.getMember() == null || StringUtils.isEmpty(order.getMember().getEmail())) {
			return;
		}
		MessageConfig messageConfig = messageConfigService.find(MessageConfig.Type.refundsOrder);
		if (messageConfig == null || !messageConfig.getIsMailEnabled()) {
			return;
		}
		Setting setting = SystemUtils.getSetting();
		Map<String, Object> model = new HashMap<>();
		model.put("order", order);
		String subject = res.format("shop.mail.refundsOrderSubject", setting.getSiteName());
		TemplateConfig templateConfig = SystemUtils.getTemplateConfig("refundsOrderMail");
		send(order.getMember().getEmail(), subject, templateConfig.getTemplatePath(), model);
	}

	/**
	 * 发送订单发货邮件(异步)
	 * 
	 * @param order
	 *            订单
	 */
	public void sendShippingOrderMail(Order order) {
		if (order == null || order.getMember() == null || StringUtils.isEmpty(order.getMember().getEmail())) {
			return;
		}
		MessageConfig messageConfig = messageConfigService.find(MessageConfig.Type.shippingOrder);
		if (messageConfig == null || !messageConfig.getIsMailEnabled()) {
			return;
		}
		Setting setting = SystemUtils.getSetting();
		Map<String, Object> model = new HashMap<>();
		model.put("order", order);
		String subject = res.format("shop.mail.shippingOrderSubject", setting.getSiteName());
		TemplateConfig templateConfig = SystemUtils.getTemplateConfig("shippingOrderMail");
		send(order.getMember().getEmail(), subject, templateConfig.getTemplatePath(), model);
	}

	/**
	 * 发送订单退货邮件(异步)
	 * 
	 * @param order
	 *            订单
	 */
	public void sendReturnsOrderMail(Order order) {
		if (order == null || order.getMember() == null || StringUtils.isEmpty(order.getMember().getEmail())) {
			return;
		}
		MessageConfig messageConfig = messageConfigService.find(MessageConfig.Type.returnsOrder);
		if (messageConfig == null || !messageConfig.getIsMailEnabled()) {
			return;
		}
		Setting setting = SystemUtils.getSetting();
		Map<String, Object> model = new HashMap<>();
		model.put("order", order);
		String subject = res.format("shop.mail.returnsOrderSubject", setting.getSiteName());
		TemplateConfig templateConfig = SystemUtils.getTemplateConfig("returnsOrderMail");
		send(order.getMember().getEmail(), subject, templateConfig.getTemplatePath(), model);
	}

	/**
	 * 发送订单收货邮件(异步)
	 * 
	 * @param order
	 *            订单
	 */
	public void sendReceiveOrderMail(Order order) {
		if (order == null || order.getMember() == null || StringUtils.isEmpty(order.getMember().getEmail())) {
			return;
		}
		MessageConfig messageConfig = messageConfigService.find(MessageConfig.Type.receiveOrder);
		if (messageConfig == null || !messageConfig.getIsMailEnabled()) {
			return;
		}
		Setting setting = SystemUtils.getSetting();
		Map<String, Object> model = new HashMap<>();
		model.put("order", order);
		String subject = res.format("shop.mail.receiveOrderSubject", setting.getSiteName());
		TemplateConfig templateConfig = SystemUtils.getTemplateConfig("receiveOrderMail");
		send(order.getMember().getEmail(), subject, templateConfig.getTemplatePath(), model);
	}

	/**
	 * 发送订单完成邮件(异步)
	 * 
	 * @param order
	 *            订单
	 */
	public void sendCompleteOrderMail(Order order) {
		if (order == null || order.getMember() == null || StringUtils.isEmpty(order.getMember().getEmail())) {
			return;
		}
		MessageConfig messageConfig = messageConfigService.find(MessageConfig.Type.completeOrder);
		if (messageConfig == null || !messageConfig.getIsMailEnabled()) {
			return;
		}
		Setting setting = SystemUtils.getSetting();
		Map<String, Object> model = new HashMap<>();
		model.put("order", order);
		String subject = res.format("shop.mail.completeOrderSubject", setting.getSiteName());
		TemplateConfig templateConfig = SystemUtils.getTemplateConfig("completeOrderMail");
		send(order.getMember().getEmail(), subject, templateConfig.getTemplatePath(), model);
	}

	/**
	 * 发送订单失败邮件(异步)
	 * 
	 * @param order
	 *            订单
	 */
	public void sendFailOrderMail(Order order) {
		if (order == null || order.getMember() == null || StringUtils.isEmpty(order.getMember().getEmail())) {
			return;
		}
		MessageConfig messageConfig = messageConfigService.find(MessageConfig.Type.failOrder);
		if (messageConfig == null || !messageConfig.getIsMailEnabled()) {
			return;
		}
		Setting setting = SystemUtils.getSetting();
		Map<String, Object> model = new HashMap<>();
		model.put("order", order);
		String subject = res.format("shop.mail.failOrderSubject", setting.getSiteName());
		TemplateConfig templateConfig = SystemUtils.getTemplateConfig("failOrderMail");
		send(order.getMember().getEmail(), subject, templateConfig.getTemplatePath(), model);
	}

	/**
	 * 发送商家注册邮件(异步)
	 * 
	 * @param business
	 *            商家
	 */
	public void sendRegisterBusinessMail(Business business) {
		if (business == null || StringUtils.isEmpty(business.getEmail())) {
			return;
		}
		MessageConfig messageConfig = messageConfigService.find(MessageConfig.Type.registerBusiness);
		if (messageConfig == null || !messageConfig.getIsMailEnabled()) {
			return;
		}
		Setting setting = SystemUtils.getSetting();
		Map<String, Object> model = new HashMap<>();
		model.put("business", business);
		String subject = res.format("shop.mail.registerBusinessSubject", setting.getSiteName());
		TemplateConfig templateConfig = SystemUtils.getTemplateConfig("registerBusinessMail");
		send(business.getEmail(), subject, templateConfig.getTemplatePath(), model);
	}

	/**
	 * 发送店铺审核成功邮件(异步)
	 * 
	 * @param store
	 *            店铺
	 */
	public void sendApprovalStoreMail(Store store) {
		if (store == null || StringUtils.isEmpty(store.getEmail())) {
			return;
		}
		MessageConfig messageConfig = messageConfigService.find(MessageConfig.Type.approvalStore);
		if (messageConfig == null || !messageConfig.getIsMailEnabled()) {
			return;
		}
		Setting setting = SystemUtils.getSetting();
		Map<String, Object> model = new HashMap<>();
		model.put("store", store);
		String subject = res.format("shop.mail.approvalStoreSubject", setting.getSiteName());
		TemplateConfig templateConfig = SystemUtils.getTemplateConfig("approvalStoreMail");
		send(store.getEmail(), subject, templateConfig.getTemplatePath(), model);
	}

	/**
	 * 发送店铺审核失败邮件(异步)
	 * 
	 * @param store
	 *            店铺
	 * @param content
	 *            内容
	 */
	public void sendFailStoreMail(Store store, String content) {
		if (store == null || StringUtils.isEmpty(store.getEmail()) || StringUtils.isEmpty(content)) {
			return;
		}
		MessageConfig messageConfig = messageConfigService.find(MessageConfig.Type.failStore);
		if (messageConfig == null || !messageConfig.getIsMailEnabled()) {
			return;
		}
		Setting setting = SystemUtils.getSetting();
		Map<String, Object> model = new HashMap<>();
		model.put("store", store);
		model.put("content", content);
		String subject = res.format("shop.mail.failStoreSubject", setting.getSiteName());
		TemplateConfig templateConfig = SystemUtils.getTemplateConfig("failStoreMail");
		send(store.getEmail(), subject, templateConfig.getTemplatePath(), model);
	}

}