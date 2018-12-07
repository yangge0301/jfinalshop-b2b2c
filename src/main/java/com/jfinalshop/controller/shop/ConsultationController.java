package com.jfinalshop.controller.shop;

import java.util.ArrayList;
import java.util.List;

import net.hasor.core.Inject;

import org.apache.commons.lang.BooleanUtils;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.Results;
import com.jfinalshop.Setting;
import com.jfinalshop.exception.ResourceNotFoundException;
import com.jfinalshop.interceptor.MobileInterceptor;
import com.jfinalshop.model.Consultation;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Product;
import com.jfinalshop.service.ConsultationService;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.ProductService;
import com.jfinalshop.shiro.core.SubjectKit;
import com.jfinalshop.util.IpUtil;
import com.jfinalshop.util.SystemUtils;
import com.xiaoleilu.hutool.util.CollectionUtil;

/**
 * Controller - 咨询
 * 
 */
@ControllerBind(controllerKey = "/consultation")
public class ConsultationController extends BaseController {

	/**
	 * 每页记录数
	 */
	private static final int PAGE_SIZE = 10;

	@Inject
	private ConsultationService consultationService;
	@Inject
	private ProductService productService;
	@Inject
	private MemberService memberService;

	/**
	 * 列表
	 */
	@ActionKey("/consultation/m_list")
	public void mList() {
		Long productId = getParaToLong("productId");
		Integer pageNumber = getParaToInt("pageNumber", 1);
		Product product = productService.find(productId);
		if (product == null || BooleanUtils.isNotTrue(product.getIsActive()) || BooleanUtils.isNotTrue(product.getIsMarketable())) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}

		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		Page<Consultation> pages = consultationService.findPage(null, product, null, true, pageable);
		
		List<Consultation> consultations = new ArrayList<Consultation>();
		if (CollectionUtil.isNotEmpty(pages.getList())) {
			for (Consultation consultation : pages.getList()) {
				consultation.put("member", consultation.getMember());
				consultation.put("replyConsultations", consultation.getReplyConsultations());
				consultations.add(consultation);
			}
		}
		renderJson(consultations);
	}

	/**
	 * 发表
	 */
	@Before(MobileInterceptor.class)
	public void add() {
		Long productId = getParaToLong(0);
		
		Setting setting = SystemUtils.getSetting();
		if (!setting.getIsConsultationEnabled()) {
			throw new ResourceNotFoundException();
		}
		Product product = productService.find(productId);
		if (product == null || BooleanUtils.isNotTrue(product.getIsActive()) || BooleanUtils.isNotTrue(product.getIsMarketable())) {
			throw new ResourceNotFoundException();
		}

		setAttr("product", product);
		render("/shop/consultation/add.ftl");
	}

	/**
	 * 详情
	 */
	public void detail() {
		Long productId = getParaToLong(0);
		Integer pageNumber = getParaToInt("pageNumber", 1);
		
		Setting setting = SystemUtils.getSetting();
		if (!setting.getIsConsultationEnabled()) {
			throw new ResourceNotFoundException();
		}
		Product product = productService.find(productId);
		if (product == null || BooleanUtils.isNotTrue(product.getIsActive()) || BooleanUtils.isNotTrue(product.getIsMarketable())) {
			throw new ResourceNotFoundException();
		}

		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("pageable", pageable);
		setAttr("product", product);
		setAttr("page", consultationService.findPage(null, product, product.getStore(), true, pageable));
		render("/shop/consultation/detail.ftl");
	}

	/**
	 * 保存
	 */
	public void save() {
		Long productId = getParaToLong("productId");
		String content = getPara("content");
		String captcha = getPara("captcha");
		Member currentUser = memberService.getCurrentUser();
		
		if (!SubjectKit.doCaptcha("captcha", captcha)) {
			renderJson(Kv.by(MESSAGE, "验证码输入错误!"));
			return;
		}
		
		Setting setting = SystemUtils.getSetting();
		if (!setting.getIsConsultationEnabled()) {
			Results.unprocessableEntity(getResponse(), "shop.consultation.disabled");
			return;
		}
		Product product = productService.find(productId);
		if (product == null || BooleanUtils.isNotTrue(product.getIsActive()) || BooleanUtils.isNotTrue(product.getIsMarketable())) {
			Results.notFound(getResponse(), Results.DEFAULT_NOT_FOUND_MESSAGE);
			return;
		}

		Consultation consultation = new Consultation();
		consultation.setContent(content);
		consultation.setIp(IpUtil.getIpAddr(getRequest()));
		consultation.setMemberId(currentUser.getId());
		consultation.setProductId(product.getId());
		consultation.setStoreId(product.getStore().getId());
		if (setting.getIsConsultationCheck()) {
			consultation.setIsShow(false);
			consultationService.save(consultation);
			Results.ok(getResponse(), "shop.consultation.check");
		} else {
			consultation.setIsShow(true);
			consultationService.save(consultation);
			Results.ok(getResponse(), "shop.consultation.success");
		}
	}

}