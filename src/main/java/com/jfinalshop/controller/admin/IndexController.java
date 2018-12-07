package com.jfinalshop.controller.admin;

import javax.servlet.ServletContext;

import net.hasor.core.Inject;
import net.hasor.core.InjectSettings;

import com.jfinal.core.JFinal;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.model.Order;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.MessageService;
import com.jfinalshop.service.OrderService;
import com.jfinalshop.service.ProductService;

/**
 * Controller - 首页
 * 
 */
@ControllerBind(controllerKey = "/admin/index")
public class IndexController extends BaseController {

	@InjectSettings("${system.name}")
	private String systemName;
	@InjectSettings("${system.version}")
	private String systemVersion;
	@InjectSettings("${system.description}")
	private String systemDescription;

	private ServletContext servletContext = JFinal.me().getServletContext();
	@Inject
	private OrderService orderService;
	@Inject
	private ProductService productService;
	@Inject
	private MemberService memberService;
	@Inject
	private MessageService messageService;

	/**
	 * 首页
	 */
	public void index() {
		setAttr("unreadMessageCount", messageService.count(null, false));
		render("/admin/index.ftl");
	}

	/**
	 * 仪表盘
	 */
	public void main() {
		setAttr("systemName", systemName);
		setAttr("systemVersion", systemVersion);
		setAttr("systemDescription", systemDescription);
		setAttr("javaVersion", System.getProperty("java.version"));
		setAttr("javaHome", System.getProperty("java.home"));
		setAttr("osName", System.getProperty("os.name"));
		setAttr("osArch", System.getProperty("os.arch"));
		setAttr("serverInfo", servletContext.getServerInfo());
		setAttr("servletVersion", servletContext.getMajorVersion() + "." + servletContext.getMinorVersion());
		setAttr("pendingReviewOrderCount", orderService.count(null, Order.Status.pendingReview, null, null, null, null, null, null, null, null, null));
		setAttr("pendingShipmentOrderCount", orderService.count(null, Order.Status.pendingShipment, null, null, null, null, null, null, null, null, null));
		setAttr("pendingReceiveOrderCount", orderService.count(null, null, null, null, null, true, null, null, null, null, null));
		setAttr("pendingRefundsOrderCount", orderService.count(null, null, null, null, null, null, true, null, null, null, null));
		setAttr("marketableSkuCount", productService.count(null, null, true, null, null, null, null, null));
		setAttr("notMarketableSkuCount", productService.count(null, null, false, null, null, null, null, null));
		setAttr("outOfStockSkuCount", productService.count(null, null, null, null, null, null, true, null));
		setAttr("stockAlertSkuCount", productService.count(null, null, null, null, null, null, null, true));
		setAttr("memberCount", memberService.count());
		setAttr("unreadMessageCount", messageService.count(null, false));
		render("/admin/common/main.ftl");
	}
}