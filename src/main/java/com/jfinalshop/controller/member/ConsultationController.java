package com.jfinalshop.controller.member;

import java.util.ArrayList;
import java.util.List;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.Results;
import com.jfinalshop.interceptor.MobileInterceptor;
import com.jfinalshop.model.Consultation;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Product;
import com.jfinalshop.model.Store;
import com.jfinalshop.service.ConsultationService;
import com.jfinalshop.service.MemberService;
import com.xiaoleilu.hutool.util.CollectionUtil;

/**
 * Controller - 咨询
 * 
 */
@ControllerBind(controllerKey = "/member/consultation")
public class ConsultationController extends BaseController {

	/**
	 * 每页记录数
	 */
	private static final int PAGE_SIZE = 10;

	@Inject
	private ConsultationService consultationService;
	@Inject
	private MemberService memberService;

	/**
	 * 列表
	 */
	@Before(MobileInterceptor.class)
	public void list() {
		Integer pageNumber = getParaToInt("pageNumber");
		Member currentUser = memberService.getCurrentUser();
		
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		setAttr("pageable", pageable);
		setAttr("page", consultationService.findPage(currentUser, null, null, null, pageable));
		render("/member/consultation/list.ftl");
	}

	/**
	 * 列表
	 */
	@ActionKey("/member/consultation/m_list")
	public void mList() {
		Integer pageNumber = getParaToInt("pageNumber", 1);
		Member currentUser = memberService.getCurrentUser();
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		Page<Consultation> pages = consultationService.findPage(currentUser, null, null, null, pageable);
		
		List<Consultation> consultations = new ArrayList<Consultation>();
		if (CollectionUtil.isNotEmpty(pages.getList())) {
			for (Consultation consultation : pages.getList()) {
				consultation.put("consultation", consultation.getMember());
				Product product = consultation.getProduct();
				product.put("type", product.getTypeName());
				product.put("thumbnail", product.getThumbnail());
				Store store = product.getStore();
				store.put("type", store.getTypeName());
				product.put("store", store);
				product.put("defaultSku", product.getDefaultSku());
				product.put("path", product.getPath());
				consultation.put("product", product);
				consultation.put("replyConsultations", consultation.getReplyConsultations());
				consultation.put("path", consultation.getPath());
				consultations.add(consultation);
			}
		}
		renderJson(consultations);
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long id = getParaToLong("id");
		Member currentUser = memberService.getCurrentUser();
		
		if (id == null) {
			Results.notFound(getResponse(), Results.DEFAULT_NOT_FOUND_MESSAGE);
			return;
		}
		Consultation consultation = consultationService.find(id);
		if (consultation == null || !currentUser.equals(consultation.getMember())) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}
		consultationService.delete(id);
		renderJson(Results.OK);
	}

}