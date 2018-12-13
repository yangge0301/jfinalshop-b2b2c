package com.jfinalshop.controller.shop;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jfinalshop.model.Member;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.util.JHttp;
import net.hasor.core.Inject;

import net.hasor.core.InjectSettings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.entity.PaymentItem;
import com.jfinalshop.interceptor.CsrfInterceptor;
import com.jfinalshop.model.PaymentTransaction;
import com.jfinalshop.model.PaymentTransaction.LineItem;
import com.jfinalshop.plugin.PaymentPlugin;
import com.jfinalshop.service.PaymentTransactionService;
import com.jfinalshop.service.PluginService;
import com.jfinalshop.util.Assert;

/**
 * Controller - 支付
 * 
 */
@ControllerBind(controllerKey = "/payment")
@Clear(CsrfInterceptor.class)
public class PaymentController extends BaseController {

	@InjectSettings("${user_pay_to_wjn_url}")
	private String payUrl;
	@Inject
	private MemberService memberService;
	@Inject
	private PluginService pluginService;
	@Inject
	private PaymentTransactionService paymentTransactionService;

	/**
	 * 检查是否支付成功
	 */
	@ActionKey("/payment/check_is_pay_success")
	public void checkIsPaySuccess() {
		String paymentTransactionSn = getPara("paymentTransactionSn");
		PaymentTransaction paymentTransaction = paymentTransactionService.findBySn(paymentTransactionSn);
		renderJson(paymentTransaction != null && paymentTransaction.getIsSuccess());
	}

	/**
	 * 首页
	 */
	@Before(Tx.class)
	public void index() {
		String paymentPluginId = getPara("paymentPluginId");
		Integer paymentItemIndex = getIndexNum("paymentItemList");
		
		List<PaymentItem> paymentItems = new ArrayList<>();
		if (-1 < paymentItemIndex) {
			for (int i = 0; i <= paymentItemIndex; i++) {
				PaymentItem paymentItem = getBean(PaymentItem.class, "paymentItems[" + i + "]");
				paymentItem.setType(getParaEnum(PaymentItem.Type.class, getPara("paymentItemList[" + i + "].type")));
				paymentItems.add(paymentItem);
			}
		}
		PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin(paymentPluginId);
//		if (paymentPlugin == null || BooleanUtils.isNotTrue(paymentPlugin.getIsEnabled())) {
//			setAttr("errorMessage", "插件禁用!");
//			render(UNPROCESSABLE_ENTITY_VIEW);
//			return;
//		}
//
//		if (CollectionUtils.isEmpty(paymentItems)) {
//			setAttr("errorMessage", "支付项是空!");
//			render(UNPROCESSABLE_ENTITY_VIEW);
//			return;
//		}
		String orderNo="";
		String payMoney = "0";
		String url ="";
		Member currentUser = memberService.getCurrentUser();
		PaymentTransaction paymentTransaction = null;
		if (paymentItems.size() > 1) {
			Set<PaymentTransaction.LineItem> lineItems = new HashSet<>();
			for (PaymentItem paymentItem : paymentItems) {
				LineItem lineItem = paymentTransactionService.generate(paymentItem, null);
				if (lineItem != null) {
					lineItems.add(lineItem);
				}
			}
			paymentTransaction = paymentTransactionService.generateParent(lineItems, paymentPlugin);
			if(paymentTransaction.getType()==2){
				payMoney=paymentTransaction.getAmount().toString();
				url = payUrl +"&account="+currentUser.getUsername()+"&money=" +payMoney;
			}
			else{

				orderNo=paymentTransaction.getSn();
				payMoney=paymentTransaction.getAmount().toString();
				url = payUrl +"&account="+currentUser.getUsername()+"&orderNo="+orderNo+"&money=" +payMoney;
			}

		} else {
			PaymentItem paymentItem = paymentItems.get(0);
			LineItem lineItem = paymentTransactionService.generate(paymentItem, null);
			paymentTransaction = paymentTransactionService.generate(lineItem, paymentPlugin);
			if(paymentTransaction.getType()==2){

				payMoney=paymentTransaction.getAmount().toString();
				url = payUrl +"&account="+currentUser.getUsername()+"&fee=" +payMoney;
			}
			else{

				orderNo=paymentTransaction.getSn();
				payMoney=paymentTransaction.getAmount().toString();
				url = payUrl +"&account="+currentUser.getUsername()+"&orderNo="+orderNo+"&fee=" +payMoney;
			}
		}
		redirect(url);
//		redirect(paymentPlugin.getPrePayUrl(paymentPlugin, paymentTransaction));
	}

	/**
	 * 支付前处理
	 */
	@ActionKey("/payment/pre_pay")
	public void prePay() throws Exception {
		String paymentTransactionSn = getPara(0);
		String extra = getPara(1);
		
		PaymentTransaction paymentTransaction = paymentTransactionService.findBySn(paymentTransactionSn);
		if (paymentTransaction == null || paymentTransaction.hasExpired()) {
			setAttr("errorMessage", "支付事务不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (paymentTransaction.getIsSuccess()) {
			setAttr("errorMessage", message("shop.payment.payCompleted"));
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		String paymentPluginId = paymentTransaction.getPaymentPluginId();
		PaymentPlugin paymentPlugin = StringUtils.isNotEmpty(paymentPluginId) ? pluginService.getPaymentPlugin(paymentPluginId) : null;
		if (paymentPlugin == null || BooleanUtils.isNotTrue(paymentPlugin.getIsEnabled())) {
			setAttr("errorMessage", "支付事务不存在或禁用!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		paymentPlugin.prePayHandle(paymentPlugin, paymentTransaction, getPaymentDescription(paymentTransaction), extra, getRequest(), getResponse(), this);
	}

	/**
	 * 支付处理
	 */
	public void pay() throws Exception {
		String paymentTransactionSn = getPara(0);
		String extra = getPara(1);
		
		PaymentTransaction paymentTransaction = paymentTransactionService.findBySn(paymentTransactionSn);
		if (paymentTransaction == null || paymentTransaction.hasExpired()) {
			setAttr("errorMessage", "支付事务不存在或过期!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		if (paymentTransaction.getIsSuccess()) {
			setAttr("errorMessage", message("shop.payment.payCompleted"));
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		String paymentPluginId = paymentTransaction.getPaymentPluginId();
		PaymentPlugin paymentPlugin = StringUtils.isNotEmpty(paymentPluginId) ? pluginService.getPaymentPlugin(paymentPluginId) : null;
		if (paymentPlugin == null || BooleanUtils.isNotTrue(paymentPlugin.getIsEnabled())) {
			setAttr("errorMessage", "支付事务不存在或禁用!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		paymentPlugin.payHandle(paymentPlugin, paymentTransaction, getPaymentDescription(paymentTransaction), extra, getRequest(), getResponse(), this);
	}

	/**
	 * 支付后处理
	 * 
	 */
	@ActionKey("/payment/post_pay")
	public void postPay() throws Exception {
		String paymentTransactionSn = getPara(0);
		String extra = getPara(1);
		
		PaymentTransaction paymentTransaction = paymentTransactionService.findBySn(paymentTransactionSn);
		if (paymentTransaction == null) {
			setAttr("errorMessage", "支付事务不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		String paymentPluginId = paymentTransaction.getPaymentPluginId();
		PaymentPlugin paymentPlugin = StringUtils.isNotEmpty(paymentPluginId) ? pluginService.getPaymentPlugin(paymentPluginId) : null;
		if (paymentPlugin == null || BooleanUtils.isNotTrue(paymentPlugin.getIsEnabled())) {
			setAttr("errorMessage", "支付事务不存在或禁用!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		boolean isPaySuccess = paymentPlugin.isPaySuccess(paymentPlugin, paymentTransaction, getPaymentDescription(paymentTransaction), extra, getRequest(), getResponse());
		if (isPaySuccess) {
			paymentTransactionService.handle(paymentTransaction);
		}
		paymentPlugin.postPayHandle(paymentPlugin, paymentTransaction, getPaymentDescription(paymentTransaction), extra, isPaySuccess, getRequest(), getResponse(), this);
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
			return message("shop.payment.paymentDescription", paymentTransaction.getSn());
		}

		switch (paymentTransaction.getTypeName()) {
		case ORDER_PAYMENT:
			return message("shop.payment.orderPaymentDescription", paymentTransaction.getOrder().getSn());
		case SVC_PAYMENT:
			return message("shop.payment.svcPaymentDescription", paymentTransaction.getSvc().getSn());
		case DEPOSIT_RECHARGE:
			return message("shop.payment.depositRechargeDescription", paymentTransaction.getSn());
		case BAIL_PAYMENT:
			return message("shop.payment.bailPaymentDescription", paymentTransaction.getSn());
		default:
			return message("shop.payment.paymentDescription", paymentTransaction.getSn());
		}
	}

}