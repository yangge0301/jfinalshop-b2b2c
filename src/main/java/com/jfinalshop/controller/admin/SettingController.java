package com.jfinalshop.controller.admin;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.mail.AuthenticationFailedException;

import net.hasor.core.Inject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import com.jfinal.aop.Clear;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.upload.UploadFile;
import com.jfinalshop.FileType;
import com.jfinalshop.Message;
import com.jfinalshop.Setting;
import com.jfinalshop.Setting.CaptchaType;
import com.jfinalshop.Setting.Locale;
import com.jfinalshop.Setting.RegisterType;
import com.jfinalshop.Setting.RoundType;
import com.jfinalshop.Setting.StockAllocationTime;
import com.jfinalshop.Setting.WatermarkPosition;
import com.jfinalshop.interceptor.CsrfInterceptor;
import com.jfinalshop.service.CacheService;
import com.jfinalshop.service.FileService;
import com.jfinalshop.service.MailService;
import com.jfinalshop.service.SmsService;
import com.jfinalshop.util.EnumUtils;
import com.jfinalshop.util.ObjectUtils;
import com.jfinalshop.util.SystemUtils;
import com.sun.mail.smtp.SMTPSenderFailedException;

/**
 * Controller - 系统设置
 * 
 */
@ControllerBind(controllerKey = "/admin/setting")
public class SettingController extends BaseController {

	@Inject
	private FileService fileService;
	@Inject
	private MailService mailService;
	@Inject
	private SmsService smsService;
	@Inject
	private CacheService cacheService;

	/**
	 * SMTP测试
	 */
	public void testSmtp() {
		String smtpHost = getPara("smtpHost");
		Integer smtpPort = getParaToInt("smtpPort");
		String smtpUsername = getPara("smtpUsername");
		String smtpPassword = getPara("smtpPassword");
		Boolean smtpSSLEnabled = getParaToBoolean("smtpSSLEnabled");
		String smtpFromMail = getPara("smtpFromMail");
		String toMail = getPara("toMail");
		if (StringUtils.isEmpty(toMail)) {
			renderJson(ERROR_MESSAGE);
			return;
		}

		Setting setting = SystemUtils.getSetting();
		try {
			Map<String, Object> properties = new HashMap<>();
			properties.put("smtpHost", smtpHost);
			properties.put("smtpPort", smtpPort);
			properties.put("smtpUsername", smtpUsername);
			properties.put("smtpSSLEnabled", smtpSSLEnabled);
			properties.put("smtpFromMail", smtpFromMail);
			
			mailService.sendTestSmtpMail(smtpHost, smtpPort, smtpUsername, StringUtils.isNotEmpty(smtpPassword) ? smtpPassword : setting.getSmtpPassword(), smtpSSLEnabled, smtpFromMail, toMail);
		} catch (Exception e) {
			Throwable rootCause = ExceptionUtils.getRootCause(e);
			if (rootCause != null) {
				if (rootCause instanceof UnknownHostException) {
					renderJson(Message.error("admin.setting.testSmtpUnknownHost"));
					return;
				} else if (rootCause instanceof ConnectException || rootCause instanceof SocketTimeoutException) {
					renderJson(Message.error("admin.setting.testSmtpConnectFailed"));
					return;
				} else if (rootCause instanceof AuthenticationFailedException) {
					renderJson(Message.error("admin.setting.testSmtpAuthenticationFailed"));
					return;
				} else if (rootCause instanceof SMTPSenderFailedException) {
					renderJson(Message.error("admin.setting.testSmtpSenderFailed"));
					return;
				}
			}
			renderJson(Message.error("admin.setting.testSmtpFailed"));
			return;
		}
		renderJson(Message.success("admin.setting.testSmtpSuccess"));
	}

	/**
	 * 短信余额查询
	 */
	public void smsBalance() {
		long balance = smsService.getBalance();
		if (balance < 0) {
			renderJson(Message.warn("admin.setting.smsInvalid"));
			return;
		}
		renderJson(Message.success("admin.setting.smsBalanceResult", balance));
	}

	/**
	 * 编辑
	 */
	public void edit() {
		setAttr("setting", SystemUtils.getSetting());
		setAttr("locales", Setting.Locale.values());
		setAttr("watermarkPositions", Setting.WatermarkPosition.values());
		setAttr("roundTypes", Setting.RoundType.values());
		setAttr("registerTypes", Setting.RegisterType.values());
		setAttr("captchaTypes", Setting.CaptchaType.values());
		setAttr("stockAllocationTimes", Setting.StockAllocationTime.values());
		render("/admin/setting/edit.ftl");
	}

	/**
	 * 更新
	 */
	@Clear(CsrfInterceptor.class)
	public void update() {
		UploadFile watermarkImageFile = getFile("watermarkImageFile");
		Setting setting = getBean(Setting.class);
		setting.setIsShowMarketPrice(getParaToBoolean("isShowMarketPrice", false));
		setting.setIsReviewEnabled(getParaToBoolean("isReviewEnabled", false));
		setting.setIsReviewCheck(getParaToBoolean("isReviewCheck", false));
		setting.setIsConsultationEnabled(getParaToBoolean("isConsultationEnabled", false));
		setting.setIsConsultationCheck(getParaToBoolean("isConsultationCheck", false));
		setting.setIsInvoiceEnabled(getParaToBoolean("isInvoiceEnabled", false));
		setting.setIsTaxPriceEnabled(getParaToBoolean("isTaxPriceEnabled", false));
		setting.setIsDevelopmentEnabled(getParaToBoolean("isDevelopmentEnabled", false));
		
		setting.setWatermarkPosition(EnumUtils.convert(WatermarkPosition.class, getPara("watermarkPosition")));
		setting.setPriceRoundType(EnumUtils.convert(RoundType.class, getPara("priceRoundType")));
		setting.setStockAllocationTime(EnumUtils.convert(StockAllocationTime.class, getPara("stockAllocationTime")));
		setting.setLocale(EnumUtils.convert(Locale.class, getPara("locale")));
		
		// 允许注册类型
		String [] allowedRegisterNames = getParaValues("allowedRegisterTypes");
		if (!ObjectUtils.isEmpty(allowedRegisterNames)) {
			int length = allowedRegisterNames.length;
			RegisterType[] registerTypes = new RegisterType [length];
			for (int i = 0; i < length; i++) {  
				registerTypes[i] = RegisterType.valueOf(allowedRegisterNames[i]); 
			}  
			setting.setAllowedRegisterTypes(registerTypes);
		}
				
		
		// 验证码类型
		String[] captchaTypeNames = getParaValues("captchaTypes");
		if (!ObjectUtils.isEmpty(captchaTypeNames)) {
			int length = captchaTypeNames.length;
			CaptchaType [] captchaTypes = new CaptchaType [length];
			for (int i = 0; i < length; i++) {  
				captchaTypes[i] = CaptchaType.valueOf(captchaTypeNames[i]); 
			}  
			setting.setCaptchaTypes(captchaTypes);
		}
		
		if (setting.getDefaultPointScale() > setting.getMaxPointScale()) {
			setAttr("errorMessage", "默认积分换算比例大于最大积分换算比例!");
			render(ERROR_VIEW);
			return;
		}
		Setting srcSetting = SystemUtils.getSetting();
		if (StringUtils.isEmpty(setting.getSmtpPassword())) {
			setting.setSmtpPassword(srcSetting.getSmtpPassword());
		}
		if (watermarkImageFile != null && watermarkImageFile.getFile().length() <= 0) {
			if (!fileService.isValid(FileType.image, watermarkImageFile)) {
				addFlashMessage(Message.error("admin.upload.invalid"));
				redirect("edit");
			}
			String watermarkImage = fileService.uploadLocal(FileType.image, watermarkImageFile);
			setting.setWatermarkImage(watermarkImage);
		} else {
			setting.setWatermarkImage(srcSetting.getWatermarkImage());
		}
		if (StringUtils.isEmpty(setting.getSmsSn()) || StringUtils.isEmpty(setting.getSmsKey())) {
			setting.setSmsSn(null);
			setting.setSmsKey(null);
		}
		setting.setIsCnzzEnabled(srcSetting.getIsCnzzEnabled());
		setting.setCnzzSiteId(srcSetting.getCnzzSiteId());
		setting.setCnzzPassword(srcSetting.getCnzzPassword());

		SystemUtils.setSetting(setting);
		cacheService.clear();

		addFlashMessage(SUCCESS_MESSAGE);
		redirect("edit");
	}

}