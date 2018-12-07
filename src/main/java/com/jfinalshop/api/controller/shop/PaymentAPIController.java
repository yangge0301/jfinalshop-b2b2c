package com.jfinalshop.api.controller.shop;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.hasor.core.Inject;
import net.hasor.core.InjectSettings;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.weixin.sdk.api.PaymentApi;
import com.jfinal.weixin.sdk.api.PaymentApi.TradeType;
import com.jfinal.weixin.sdk.api.SnsAccessToken;
import com.jfinal.weixin.sdk.api.SnsAccessTokenApi;
import com.jfinal.weixin.sdk.jfinal.ApiController;
import com.jfinal.weixin.sdk.kit.PaymentKit;
import com.jfinal.weixin.sdk.utils.JsonUtils;
import com.jfinalshop.Setting;
import com.jfinalshop.api.common.bean.AjaxResult;
import com.jfinalshop.api.common.bean.BaseResponse;
import com.jfinalshop.api.common.bean.Code;
import com.jfinalshop.api.common.token.TokenManager;
import com.jfinalshop.entity.PaymentItem;
import com.jfinalshop.interceptor.CsrfInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.PaymentTransaction;
import com.jfinalshop.model.PaymentTransaction.LineItem;
import com.jfinalshop.model.PluginConfig;
import com.jfinalshop.plugin.PaymentPlugin;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.PaymentTransactionService;
import com.jfinalshop.service.PluginConfigService;
import com.jfinalshop.service.PluginService;
import com.jfinalshop.shiro.core.ShiroInterceptor;
import com.jfinalshop.util.Assert;
import com.jfinalshop.util.IpUtil;
import com.jfinalshop.util.SystemUtils;

/**
 * 移动API - 支付
 * 
 * 
 */
@ControllerBind(controllerKey = "/api/payment")
@Clear({CsrfInterceptor.class, ShiroInterceptor.class})
public class PaymentAPIController extends ApiController {
	
	@Inject
	private PluginService pluginService;
	@Inject
	private MemberService memberService;
	@Inject
	private PaymentTransactionService paymentTransactionService;
	@Inject
	private PluginConfigService pluginConfigService;
	
	@InjectSettings("${is_test_url}")
	private Boolean isTestURL;
	
	@InjectSettings("${redirect_uri}")
	private String redirectUri;
	
	private String paymentPluginId = "weixinPublicPaymentPlugin";
	
	private Res res = I18n.use();
	
	private AjaxResult ajax = new AjaxResult();
	
	/**
	 * 获取的code参数
	 * 
	 */
	public void getOpenId() {
		Long memberId = getParaToLong("memberId");
		String urlForward = getPara("url_forward");
		
		String state = urlForward + "," + memberId;
		
		Setting setting = SystemUtils.getSetting();
		String redirectUri = setting.getSiteUrl() + "/api/payment/openIdNotify";
		
		String url;
		if(isTestURL) {
			url = "http://www.omengo.com/get-weixin-code.html?appid="+getAppId()+"&scope=snsapi_base&state=" + state + "&redirect_uri=http%3A%2F%2Ftest.jfinalshop.com%2Fapi%2Fpayment%2FopenIdNotify";
		} else {
			url =  SnsAccessTokenApi.getAuthorizeURL(getAppId(), redirectUri, state, true);
		}
		
		try {
			getResponse().sendRedirect(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		renderNull();
	}
	
	/**
	 * 授权回调页面域名
	 * 
	 */
	public void openIdNotify() {
		String code = getPara("code"); 
		String state = getPara("state");
		
		// 通过code获取access_token
		SnsAccessToken snsAccessToken = SnsAccessTokenApi.getSnsAccessToken(getAppId(), getAppSecret(), code);
		String openId = snsAccessToken.getOpenid();
		
		LogKit.info("SnsAccessToken" + snsAccessToken.getJson());
		LogKit.info("state" + state);
		int separator = StringUtils.indexOf(state, ",");
		if (StrKit.notBlank(openId) && 0 < separator) {
			Long id = Long.parseLong(StringUtils.substring(state, separator + 1));
			Member member = memberService.find(id);
			if (member != null) {
				member.setOpenId(openId);
				memberService.update(member);
			}
		}
		redirectUri += StringUtils.substring(state, 0, separator);
		redirect(redirectUri);
	}
	
	/**
	 * 支付首页
	 * 
	 */
	@Before(Tx.class)
	public void index() {
		String[] orderSns = getParaValues("orderSns");
		
		if (orderSns == null || orderSns.length <= 0) {
			renderArgumentError("订单号不能为空!");
			return;
		}
		
		List<PaymentItem> paymentItems = new ArrayList<>();
		for (String orderSn : orderSns) {
			PaymentItem paymentItem = new PaymentItem();
			paymentItem.setOrderSn(orderSn);
			paymentItem.setType(PaymentItem.Type.valueOf("ORDER_PAYMENT"));
			paymentItems.add(paymentItem);
		}
		
		Member member = getMember();
		if (member == null) {
			renderArgumentError("用户不能为空!");
			return;
		} else {
			member = memberService.find(member.getId());
		}
		
		PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin(paymentPluginId);
		if (paymentPlugin == null || BooleanUtils.isNotTrue(paymentPlugin.getIsEnabled())) {
			renderArgumentError("插件禁用!");
			return;
		}
		
		if (CollectionUtils.isEmpty(paymentItems)) {
			renderArgumentError("支付项是空!");
			return;
		}

		PaymentTransaction paymentTransaction = null;
		if (paymentItems.size() > 1) {
			Set<PaymentTransaction.LineItem> lineItems = new HashSet<>();
			for (PaymentItem paymentItem : paymentItems) {
				LineItem lineItem = paymentTransactionService.generate(paymentItem, member);
				if (lineItem != null) {
					lineItems.add(lineItem);
				}
			}
			paymentTransaction = paymentTransactionService.generateParent(lineItems, paymentPlugin);
		} else {
			PaymentItem paymentItem = paymentItems.get(0);
			LineItem lineItem = paymentTransactionService.generate(paymentItem, member);
			if (lineItem == null) {
				renderArgumentError("支付明细不能为空!");
				return;
			}
			paymentTransaction = paymentTransactionService.generate(lineItem, paymentPlugin);
		}
		
		if (paymentTransaction == null || paymentTransaction.hasExpired()) {
			renderArgumentError("支付事务不存在或过期!");
			return;
		}
		
		if (paymentTransaction.getIsSuccess()) {
			renderArgumentError(res.format("shop.payment.payCompleted"));
			return;
		}
		
		String openId = member.getOpenId();

		Map<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("appid", getAppId());
		parameterMap.put("mch_id", getMchId());
		parameterMap.put("nonce_str", DigestUtils.md5Hex(UUID.randomUUID() + RandomStringUtils.randomAlphabetic(30)));
		parameterMap.put("body", StringUtils.abbreviate(getPaymentDescription(paymentTransaction).replaceAll("[^0-9a-zA-Z\\u4e00-\\u9fa5 ]", ""), 600));
		parameterMap.put("out_trade_no", paymentTransaction.getSn());
		parameterMap.put("total_fee", paymentTransaction.getAmount().multiply(new BigDecimal(100)).setScale(0).toString());
		String ip = IpUtil.getIpAddr(getRequest());
		if (StrKit.isBlank(ip) || ip.equals("0:0:0:0:0:0:0:1")) {
			ip = "127.0.0.1";
		}
		Setting setting = SystemUtils.getSetting();
		parameterMap.put("spbill_create_ip", ip);
		parameterMap.put("notify_url", setting.getSiteUrl() + "/api/payment/payment_notify");
		parameterMap.put("trade_type", TradeType.JSAPI.name());
		parameterMap.put("openid", openId);
		parameterMap.put("sign", PaymentKit.createSign(parameterMap, getApiKey()));

		String result = PaymentApi.pushOrder(parameterMap);
		Map<String, String> resultMap = PaymentKit.xmlToMap(result);
		
		String prepayId = resultMap.get("prepay_id");
		String tradeType = resultMap.get("trade_type");
		String returnCode = resultMap.get("return_code");
		String returnMsg = resultMap.get("return_msg");

		if (!StringUtils.equals(tradeType, "JSAPI") || StringUtils.equals("FAIL", returnCode)) {
			renderArgumentError(returnMsg);
			return;
		}
		
		Map<String, String> modelMap = new HashMap<String, String>();
		modelMap.put("appId", getAppId());
		modelMap.put("timeStamp", System.currentTimeMillis() / 1000 + "");
		modelMap.put("nonceStr", DigestUtils.md5Hex(UUID.randomUUID() + RandomStringUtils.randomAlphabetic(30)));
		modelMap.put("package", "prepay_id=" + prepayId);
		modelMap.put("signType", "MD5");
		modelMap.put("paySign", PaymentKit.createSign(modelMap, getApiKey()));
		
		String jsonStr = JsonUtils.toJson(modelMap);
		ajax.success(jsonStr);
		LogKit.info("ajax=" + ajax);
		renderJson(ajax);
	}

	/**
	 * 支付后通知
	 * 支付结果通用通知文档: https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_7
	 */
	@ActionKey("/api/payment/payment_notify")
	public void paymentNotify() {
		String xml = HttpKit.readData(getRequest());
		LogKit.info("支付通知=" + xml);
		
		if (StringUtils.isEmpty(xml)) {
			return;
		}
		Map<String, String> params = PaymentKit.xmlToMap(xml);
		
		// 总金额
		//String totalFee = params.get("total_fee"); 
		// 微信支付订单号
		//String transactionId = params.get("transaction_id"); 
		// 商户订单号
		String outTradeNo = params.get("out_trade_no"); 
		// 交易类型
		//String tradeType = params.get("trade_type");
		// 支付完成时间，格式为yyyyMMddHHmmss
		//String timeEnd = params.get("time_end");
		// 以下是附加参数
		//String openId = params.get("openid");
		
		//PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin(paymentPluginId);
		
		Map<String, String> resultMap = PaymentApi.queryByOutTradeNo(getAppId(), getMchId(), getApiKey(), outTradeNo);
		
		LogKit.info("resultMap" + resultMap);
		PaymentTransaction paymentTransaction = paymentTransactionService.findBySn(outTradeNo);
		boolean isPaySuccess = StringUtils.equals(resultMap.get("return_code"), "SUCCESS")
				&& StringUtils.equals(resultMap.get("result_code"), "SUCCESS")
				&& StringUtils.equals(resultMap.get("trade_state"), "SUCCESS")
				&& paymentTransaction.getAmount().multiply(new BigDecimal(100)).compareTo(new BigDecimal(resultMap.get("total_fee"))) == 0;
		
		if (isPaySuccess) {
			// 执行更新订单
			paymentTransactionService.handle(paymentTransaction);
			// 发送通知等
			Map<String, String> xmlMap = new HashMap<String, String>();
			xmlMap.put("return_code", "SUCCESS");
			xmlMap.put("return_msg", "OK");
			renderText(PaymentKit.toXml(xmlMap));
			return;
		}
		renderNull();
	}

	
	/**
	 * 获取插件配置
	 * 
	 * @return 插件配置
	 */
	public PluginConfig getPluginConfig() {
		return pluginConfigService.findByPluginId(paymentPluginId);
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
	 * 获取支付描述
	 * 
	 * @param paymentTransaction
	 *            支付事务
	 * @return 支付描述
	 */
	private String getPaymentDescription(PaymentTransaction paymentTransaction) {
		Assert.notNull(paymentTransaction);
		if (CollectionUtils.isEmpty(paymentTransaction.getChildren())) {
			Assert.notNull(paymentTransaction.getType());
		} else {
			return res.format("shop.payment.paymentDescription", paymentTransaction.getSn());
		}

		switch (paymentTransaction.getTypeName()) {
		case ORDER_PAYMENT:
			return res.format("shop.payment.orderPaymentDescription", paymentTransaction.getOrder().getSn());
		case SVC_PAYMENT:
			return res.format("shop.payment.svcPaymentDescription", paymentTransaction.getSvc().getSn());
		case DEPOSIT_RECHARGE:
			return res.format("shop.payment.depositRechargeDescription", paymentTransaction.getSn());
		case BAIL_PAYMENT:
			return res.format("shop.payment.bailPaymentDescription", paymentTransaction.getSn());
		default:
			return res.format("shop.payment.paymentDescription", paymentTransaction.getSn());
		}
	}
	
	/**
     * 响应请求参数有误*
     * @param message 错误信息
     */
    private void renderArgumentError(String message) {
        renderJson(new BaseResponse(Code.ARGUMENT_ERROR, message));
    }
    
    /**
     * 获取当前用户对象
     * @return
     */
    private Member getMember() {
    	String token = getPara("token");
        if (StringUtils.isNotEmpty(token)) {
            return TokenManager.getMe().validate(token);
        }
        return null;
    }
    
}
