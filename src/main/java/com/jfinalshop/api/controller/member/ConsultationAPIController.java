package com.jfinalshop.api.controller.member;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.api.common.bean.DatumResponse;
import com.jfinalshop.api.controller.BaseAPIController;
import com.jfinalshop.api.interceptor.TokenInterceptor;
import com.jfinalshop.model.Consultation;
import com.jfinalshop.model.Member;
import com.jfinalshop.service.ConsultationService;
import com.jfinalshop.util.IpUtil;


/**
 * 会员中心 - 咨询
 * 
 * 
 */
@ControllerBind(controllerKey = "/api/member/consultation")
@Before(TokenInterceptor.class)
public class ConsultationAPIController extends BaseAPIController {

	@Inject
	private ConsultationService consultationService;
	
	/**
	 * 列表
	 */
	public void list() {
		Integer pageNumber = getParaToInt("pageNumber", 1);
		Integer pageSize = getParaToInt("pageSize", 20);
		Member member = getMember();
		Pageable pageable = new Pageable(pageNumber, pageSize);
		Page<Consultation> pages = consultationService.findPage(member, pageable);
		convertConsultation(pages.getList());
		renderJson(new DatumResponse(pages));
	}
	
	
	/**
	 * 保存
	 */
	public void save() {
		String content = getPara("content");
		
		Member member = getMember();
		if (member == null) {
			renderArgumentError("当前用户不能为空!");
			return;
		}
		
		Consultation consultation = new Consultation();
		consultation.setContent(content);
		consultation.setMemberId(member.getId());
		consultation.setIsShow(true);
		consultation.setIp(IpUtil.getIpAddr(getRequest()));
		consultationService.save(consultation);
		renderJson(new DatumResponse("咨询成功!"));
	}
	
}
