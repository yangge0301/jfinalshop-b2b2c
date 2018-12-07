package com.jfinalshop.plugin.weixinPublicPayment;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest.TokenRequestBuilder;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jfinal.core.Controller;
import com.jfinalshop.model.PaymentTransaction;
import com.jfinalshop.model.PluginConfig;
import com.jfinalshop.plugin.PaymentPlugin;
import com.jfinalshop.util.MobileWebUtils;
import com.jfinalshop.util.WebUtils;
import com.jfinalshop.util.XmlUtils;

/**
 * Plugin - 微信支付(公众号支付)
 * 
 */
public class WeixinPublicPaymentPlugin extends PaymentPlugin {

	/**
	 * code请求URL
	 */
	private static final String CODE_REQUEST_URL = "https://open.weixin.qq.com/connect/oauth2/authorize#wechat_redirect";

	/**
	 * openId请求URL
	 */
	private static final String OPEN_ID_REQUEST_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";

	/**
	 * prepay_id请求URL
	 */
	private static final String PREPAY_ID_REQUEST_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

	/**
	 * 查询订单请求URL
	 */
	private static final String ORDER_QUERY_REQUEST_URL = "https://api.mch.weixin.qq.com/pay/orderquery";

	@Override
	public String getName() {
		return "微信支付(公众号支付)";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public String getAuthor() {
		return "JFinalShop";
	}

	@Override
	public String getSiteUrl() {
		return "http://www.jfinalshop.com";
	}

	@Override
	public String getInstallUrl() {
		return "weixin_public_payment/install";
	}

	@Override
	public String getUninstallUrl() {
		return "weixin_public_payment/uninstall";
	}

	@Override
	public String getSettingUrl() {
		return "weixin_public_payment/setting";
	}

	@Override
	public boolean supports(HttpServletRequest request) {
		String userAgent = request.getHeader("USER-AGENT");
		return MobileWebUtils.check(userAgent);
	}

	@Override
	public void prePayHandle(PaymentPlugin paymentPlugin, PaymentTransaction paymentTransaction, String paymentDescription, String extra, HttpServletRequest request, HttpServletResponse response, Controller controller) throws Exception {
		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put("appid", getAppId());
		parameterMap.put("redirect_uri", getPayUrl(paymentPlugin, paymentTransaction));
		parameterMap.put("response_type", "code");
		parameterMap.put("scope", "snsapi_base");

		controller.setAttr("requestUrl", CODE_REQUEST_URL);
		controller.setAttr("parameterMap", parameterMap);
		controller.render("/plugin/weixinPublicPayment/pre_pay.ftl");
	}

	@Override
	public void payHandle(PaymentPlugin paymentPlugin, PaymentTransaction paymentTransaction, String paymentDescription, String extra, HttpServletRequest request, HttpServletResponse response, Controller controller) throws Exception {
		String code = request.getParameter("code");
		if (StringUtils.isEmpty(code)) {
			controller.render("/common/error/unprocessable_entity.ftl");
			return;
		}

		String openId = getOpenId(code);

		Map<String, Object> parameterMap = new TreeMap<>();
		parameterMap.put("appid", getAppId());
		parameterMap.put("mch_id", getMchId());
		parameterMap.put("nonce_str", DigestUtils.md5Hex(UUID.randomUUID() + RandomStringUtils.randomAlphabetic(30)));
		parameterMap.put("body", StringUtils.abbreviate(paymentDescription.replaceAll("[^0-9a-zA-Z\\u4e00-\\u9fa5 ]", ""), 600));
		parameterMap.put("out_trade_no", paymentTransaction.getSn());
		parameterMap.put("total_fee", paymentTransaction.getAmount().multiply(new BigDecimal(100)).setScale(0).toString());
		parameterMap.put("spbill_create_ip", request.getLocalAddr());
		parameterMap.put("notify_url", getPostPayUrl(paymentPlugin, paymentTransaction));
		parameterMap.put("trade_type", "JSAPI");
		parameterMap.put("openid", openId);
		parameterMap.put("sign", generateSign(parameterMap));

		String result = WebUtils.post(PREPAY_ID_REQUEST_URL, XmlUtils.toXml(parameterMap));
		Map<String, String> resultMap = XmlUtils.toObject(result, new TypeReference<Map<String, String>>() {
		});

		String prepayId = resultMap.get("prepay_id");
		String tradeType = resultMap.get("trade_type");
		String resultCode = resultMap.get("result_code");

		if (StringUtils.equals(tradeType, "JSAPI") && StringUtils.equals(resultCode, "SUCCESS")) {
			Map<String, Object> modelMap = new TreeMap<>();
			modelMap.put("appId", getAppId());
			modelMap.put("timeStamp", System.currentTimeMillis());
			modelMap.put("nonceStr", DigestUtils.md5Hex(UUID.randomUUID() + RandomStringUtils.randomAlphabetic(30)));
			modelMap.put("package", "prepay_id=" + prepayId);
			modelMap.put("signType", "MD5");
			modelMap.put("paySign", generateSign(modelMap));
			modelMap.put("postPayUrl", getPostPayUrl(paymentPlugin, paymentTransaction));
			controller.renderJson(modelMap);
			controller.render("/plugin/weixinPublicPayment/pay.ftl");
		}
	}

	@Override
	public void postPayHandle(PaymentPlugin paymentPlugin, PaymentTransaction paymentTransaction, String paymentDescription, String extra, boolean isPaySuccess, HttpServletRequest request, HttpServletResponse response, Controller controller) throws Exception {
		super.postPayHandle(paymentPlugin, paymentTransaction, paymentDescription, extra, isPaySuccess, request, response, controller);

		String xml = IOUtils.toString(request.getInputStream(), "UTF-8");
		if (StringUtils.isEmpty(xml)) {
			return;
		}
		Map<String, String> resultMap = XmlUtils.toObject(xml, new TypeReference<Map<String, String>>() {
		});

		if (StringUtils.equals(resultMap.get("return_code"), "SUCCESS")) {
			controller.setAttr("message", "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>");
		}
	}

	@Override
	public boolean isPaySuccess(PaymentPlugin paymentPlugin, PaymentTransaction paymentTransaction, String paymentDescription, String extra, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> parameterMap = new TreeMap<>();
		parameterMap.put("appid", getAppId());
		parameterMap.put("mch_id", getMchId());
		parameterMap.put("out_trade_no", paymentTransaction.getSn());
		parameterMap.put("nonce_str", DigestUtils.md5Hex(UUID.randomUUID() + RandomStringUtils.randomAlphabetic(30)));
		parameterMap.put("sign", generateSign(parameterMap));
		String result = WebUtils.post(ORDER_QUERY_REQUEST_URL, XmlUtils.toXml(parameterMap));
		Map<String, String> resultMap = XmlUtils.toObject(result, new TypeReference<Map<String, String>>() {
		});
		return StringUtils.equals(resultMap.get("return_code"), "SUCCESS") && StringUtils.equals(resultMap.get("result_code"), "SUCCESS") && StringUtils.equals(resultMap.get("trade_state"), "SUCCESS")
				&& paymentTransaction.getAmount().multiply(new BigDecimal(100)).compareTo(new BigDecimal(resultMap.get("total_fee"))) == 0;
	}

	/**
	 * 获取AppID
	 * 
	 * @return AppID
	 */
	private String getAppId() {
		PluginConfig pluginConfig = getPluginConfig();
		return pluginConfig.getAttribute("appId");
	}

	/**
	 * 获取AppSecret
	 * 
	 * @return AppSecret
	 */
	private String getAppSecret() {
		PluginConfig pluginConfig = getPluginConfig();
		return pluginConfig.getAttribute("appSecret");
	}

	/**
	 * 获取商户号
	 * 
	 * @return 商户号
	 */
	private String getMchId() {
		PluginConfig pluginConfig = getPluginConfig();
		return pluginConfig.getAttribute("mchId");
	}

	/**
	 * 获取API密钥
	 * 
	 * @return API密钥
	 */
	private String getApiKey() {
		PluginConfig pluginConfig = getPluginConfig();
		return pluginConfig.getAttribute("apiKey");
	}

	/**
	 * 获取OpenID
	 * 
	 * @param code
	 *            code值
	 * @return OpenID
	 */
	private String getOpenId(String code) {
		try {
			OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
			TokenRequestBuilder tokenRequestBuilder = OAuthClientRequest.tokenLocation(OPEN_ID_REQUEST_URL);
			tokenRequestBuilder.setParameter("appid", getAppId());
			tokenRequestBuilder.setParameter("secret", getAppSecret());
			tokenRequestBuilder.setCode(code);
			tokenRequestBuilder.setGrantType(GrantType.AUTHORIZATION_CODE);
			OAuthClientRequest accessTokenRequest = tokenRequestBuilder.buildQueryMessage();
			OAuthJSONAccessTokenResponse authJSONAccessTokenResponse = oAuthClient.accessToken(accessTokenRequest, OAuth.HttpMethod.GET);
			return authJSONAccessTokenResponse.getParam("openid");
		} catch (OAuthSystemException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (OAuthProblemException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 生成签名
	 * 
	 * @param parameterMap
	 *            参数
	 * @return 签名
	 */
	private String generateSign(Map<String, ?> parameterMap) {
		return StringUtils.upperCase(DigestUtils.md5Hex(joinKeyValue(new TreeMap<>(parameterMap), null, "&key=" + getApiKey(), "&", true)));
	}

}