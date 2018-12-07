package com.jfinalshop.controller.admin;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import net.hasor.core.Inject;

import com.alibaba.druid.util.StringUtils;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.api.PaymentApi;
import com.jfinal.weixin.sdk.jfinal.ApiController;
import com.jfinalshop.api.common.bean.AjaxResult;
import com.jfinalshop.model.Admin;
import com.jfinalshop.model.Order;
import com.jfinalshop.model.OrderRefunds;
import com.jfinalshop.model.PaymentTransaction;
import com.jfinalshop.model.PluginConfig;
import com.jfinalshop.plugin.PaymentPlugin;
import com.jfinalshop.service.AdminService;
import com.jfinalshop.service.OrderRefundsService;
import com.jfinalshop.service.OrderService;
import com.jfinalshop.service.PaymentTransactionService;
import com.jfinalshop.service.PluginService;
import com.xiaoleilu.hutool.date.DateUtil;

/**
 * Controller - 微信
 * 
 */
@ControllerBind(controllerKey = "/admin/weixin_pay")
public class WeixinPayController extends ApiController {
	
	@Inject
	private OrderRefundsService orderRefundsService;
	@Inject
	private PluginService pluginService;
	@Inject
	private PaymentTransactionService paymentTransactionService;
	@Inject
	private OrderService orderService;
	@Inject
	private AdminService adminService;
	
	private AjaxResult ajax = new AjaxResult();
	
	/** 业务结果 */
	public static final String RETURN_CODE = "return_code";
	/** 业务结果 */
	public static final String RESULT_CODE = "result_code";
	/** 返回信息 */
	public static final String RETURN_MSG = "return_msg";
	/** 错误代码描述 */
	public static final String ERR_CODE_DES = "err_code_des";
	/** 微信公众号 */
	public static final String WEIXIN_PAYMENT_PLUGIN = "weixinPublicPaymentPlugin";
	/** 微信APP */
	public static final String WEIXINAPP_PAYMENT_PLUGIN = "weixinApp";
	/** 微信小程序 */
	public static final String WEIXINMINI_PAYMENT_PLUGIN = "weixinMini";
	
	/**
	 * 微信支付退款确认
	 */
	public void refunds() {
		Long id = getParaToLong("id");
		String paymentPluginId = getPara("paymentPluginId");
		
		Admin admin = adminService.getCurrent();
		if (admin == null) {
			ajax.addError("管理员没有登录!");
			renderJson(ajax);
			return;
		}
		OrderRefunds orderRefunds = orderRefundsService.find(id);
		if (orderRefunds == null) {
			ajax.addError("退款单不存在!");
			renderJson(ajax);
			return;
		}
		if (orderRefunds.getBank() != null) {
			ajax.addError("已经退过款啦!");
			renderJson(ajax);
			return;
		}
		PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin(paymentPluginId);
		if (paymentPlugin == null || !paymentPlugin.getIsEnabled()) {
			ajax.addError("支付插件不存在或未开启!");
			renderJson(ajax);
			return;
		}
		PluginConfig pluginConfig = paymentPlugin.getPluginConfig();
		Order order = orderRefunds.getOrder();
		if (order == null) {
			ajax.addError("订单不存在!");
			renderJson(ajax);
			return;
		}
		/*if (order.getStatus() == null || !order.getStatus().equals(Order.Status.refunding.ordinal())) {
			ajax.addError("该订单不是退款中!");
			renderJson(ajax);
			return;
		}*/
		PaymentTransaction paymentTransaction = paymentTransactionService.findSuccessOrderPayment(paymentPluginId, order);
		if (paymentTransaction == null) {
			ajax.addError("支付成功事务记录不存在!");
			renderJson(ajax);
			return;
		}
		
		// 退款请求参数
		Map<String, String> params = new HashMap<String, String>();
		params.put("appid", pluginConfig.getAttribute("appId")); // 公众账号ID
		params.put("mch_id", pluginConfig.getAttribute("mchId")); // 商户号 
		params.put("out_trade_no", paymentTransaction.getSn()); // 商户订单号
		params.put("out_refund_no", orderRefunds.getSn()); // 商户退款单号
		params.put("total_fee", paymentTransaction.getAmount().multiply(new BigDecimal(100)).setScale(0).toString());// 订单金额
		params.put("refund_fee", orderRefunds.getAmount().multiply(new BigDecimal(100)).setScale(0).toString());// 退款金额
		params.put("sign_type", "MD5");
		params.put("refund_fee_type", "CNY");
		
		// 微信返回结果
		Map<String, String> result = null;
		String apiKey = pluginConfig.getAttribute("apiKey");
		switch (paymentPlugin.getId()) {
		case WEIXIN_PAYMENT_PLUGIN:
			result = PaymentApi.refund(params, apiKey, PathKit.getRootClassPath() + File.separator + "apiclient_cert.p12");
			break;
		case WEIXINMINI_PAYMENT_PLUGIN:
			result = PaymentApi.refund(params, apiKey, PathKit.getRootClassPath() + File.separator + "apiclient_cert.p12");
			break;
		case WEIXINAPP_PAYMENT_PLUGIN:
			result = PaymentApi.refund(params, apiKey, PathKit.getRootClassPath() + File.separator + "apiclient_cert_app.p12");
			break;
		default:
			ajax.addError("支付插件不是微信支付!");
			
			renderJson(ajax);
			return;
		}
		
		// 处理微信返回信息
		LogKit.info("返回result: " + result);
		String returnCode = result.get(RETURN_CODE);
		if (StrKit.isBlank(returnCode) || !StringUtils.equals("SUCCESS", returnCode)) {
			String returnMsg = result.get(RETURN_MSG);
			ajax.addError(returnMsg);
			renderJson(ajax);
			return;
		}
		String resultCode = result.get(RESULT_CODE);
		if (StrKit.isBlank(resultCode) || !StringUtils.equals("SUCCESS", resultCode)) {
			String errCodeDes = result.get(ERR_CODE_DES);
			ajax.addError(errCodeDes);
			renderJson(ajax);
			return;
		}
		
		// 退款成功, 更新状态
		String memo = " & 退款审核人 : " + admin.getName() + " 日期 : " + DateUtil.now();
		orderRefunds.setMemo(orderRefunds.getMemo() == null ? memo : orderRefunds.getMemo() + memo);
		orderRefunds.setBank(paymentPlugin.getName());
		orderRefundsService.update(orderRefunds);
		order.setStatus(Order.Status.refunded.ordinal());
		orderService.update(order);
		
		ajax.success(memo);
		renderJson(ajax);
	}
	
}
