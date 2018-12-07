package com.jfinalshop.plugin.weixinNativePayment;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.util.Base64;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.jfinal.core.Controller;
import com.jfinalshop.model.PaymentTransaction;
import com.jfinalshop.model.PluginConfig;
import com.jfinalshop.plugin.PaymentPlugin;
import com.jfinalshop.util.IpUtil;
import com.jfinalshop.util.WebUtils;
import com.jfinalshop.util.XmlUtils;

/**
 * Plugin - 微信支付(扫码支付)
 * 
 */
public class WeixinNativePaymentPlugin extends PaymentPlugin {

	/**
	 * code_url请求URL
	 */
	private static final String CODE_URL_REQUEST_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

	/**
	 * 查询订单请求URL
	 */
	private static final String ORDER_QUERY_REQUEST_URL = "https://api.mch.weixin.qq.com/pay/orderquery";

	@Override
	public String getName() {
		return "微信支付(扫码支付)";
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
		return "weixin_native_payment/install";
	}

	@Override
	public String getUninstallUrl() {
		return "weixin_native_payment/uninstall";
	}

	@Override
	public String getSettingUrl() {
		return "weixin_native_payment/setting";
	}

	@Override
	public void payHandle(PaymentPlugin paymentPlugin, PaymentTransaction paymentTransaction, String paymentDescription, String extra, HttpServletRequest request, HttpServletResponse response, Controller controller) throws Exception {

		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put("appid", getAppId());
		parameterMap.put("mch_id", getMchId());
		parameterMap.put("nonce_str", DigestUtils.md5Hex(UUID.randomUUID() + RandomStringUtils.randomAlphabetic(30)));
		parameterMap.put("body", StringUtils.abbreviate(paymentDescription.replaceAll("[^0-9a-zA-Z\\u4e00-\\u9fa5 ]", ""), 600));
		parameterMap.put("out_trade_no", paymentTransaction.getSn());
		parameterMap.put("total_fee", paymentTransaction.getAmount().multiply(new BigDecimal(100)).setScale(0).toString());
		parameterMap.put("spbill_create_ip", IpUtil.getIpAddr(request));
		parameterMap.put("notify_url", getPostPayUrl(paymentPlugin, paymentTransaction));
		parameterMap.put("trade_type", "NATIVE");
		parameterMap.put("product_id", paymentTransaction.getSn());
		parameterMap.put("sign", generateSign(parameterMap));
		String result = WebUtils.post(CODE_URL_REQUEST_URL, XmlUtils.toXml(parameterMap));
		Map<String, String> resultMap = XmlUtils.toObject(result, new TypeReference<Map<String, String>>() {});
		String returnCode = resultMap.get("return_code");
		String resultCode = resultMap.get("result_code");
		String tradeType = resultMap.get("trade_type");
		controller.setAttr("imageBase64", paymentTransaction.getMember());
		if (StringUtils.equals(returnCode, "SUCCESS") && StringUtils.equals(resultCode, "SUCCESS") && StringUtils.equals(tradeType, "NATIVE")) {
			String codeUrl = resultMap.get("code_url");
			ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
			try {
				BufferedImage image = generateQrcode(codeUrl, 300, 300);
				ImageIO.write(image, "jpg", arrayOutputStream);
				controller.setAttr("imageBase64", Base64.encodeBase64String(arrayOutputStream.toByteArray()));
				controller.setAttr("paymentTransactionSn", paymentTransaction.getSn());
				controller.render("/plugin/weixinNativePayment/pay.ftl");
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage(), e);
			} finally {
				IOUtils.closeQuietly(arrayOutputStream);
			}
		} else if (StringUtils.equals(returnCode, "FAIL") || StringUtils.equals(resultCode, "FAIL")) {
			String returnMsg = resultMap.get("return_msg");
			controller.setAttr("errorMessage", returnMsg);
			controller.render("/common/error/unprocessable_entity.ftl");
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
		Map<String, String> resultMap = XmlUtils.toObject(result, new TypeReference<Map<String, String>>() {});
		return StringUtils.equals(resultMap.get("return_code"), "SUCCESS") && StringUtils.equals(resultMap.get("result_code"), "SUCCESS") && StringUtils.equals(resultMap.get("trade_state"), "SUCCESS")
				&& paymentTransaction.getAmount().multiply(new BigDecimal(100)).compareTo(new BigDecimal(resultMap.get("total_fee"))) == 0;
	}

	/**
	 * 获取公众号ID
	 * 
	 * @return 公众号ID
	 */
	private String getAppId() {
		PluginConfig pluginConfig = getPluginConfig();
		return pluginConfig.getAttribute("appId");
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
	 * 生成签名
	 * 
	 * @param parameterMap
	 *            参数
	 * @return 签名
	 */
	private String generateSign(Map<String, ?> parameterMap) {
		PluginConfig pluginConfig = getPluginConfig();
		return StringUtils.upperCase(DigestUtils.md5Hex(joinKeyValue(new TreeMap<>(parameterMap), null, "&key=" + pluginConfig.getAttribute("apiKey"), "&", true)));
	}

	/**
	 * 生成二维码图片
	 * 
	 * @param text
	 *            内容
	 * @param width
	 *            宽度
	 * @param height
	 *            高度
	 * @return 二维码图片
	 */
	public BufferedImage generateQrcode(String text, int width, int height) {
		int WHITE = 0xFFFFFFFF;
		int BLACK = 0xFF000000;
		Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		hints.put(EncodeHintType.MARGIN, 0);
		try {
			BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints);
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					image.setRGB(x, y, bitMatrix.get(x, y) ? BLACK : WHITE);
				}
			}
			return image;
		} catch (WriterException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

}