package com.jfinalshop.controller.business;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Pageable;
import com.jfinalshop.Results;
import com.jfinalshop.exception.UnauthorizedException;
import com.jfinalshop.model.Consultation;
import com.jfinalshop.model.Store;
import com.jfinalshop.service.BusinessService;
import com.jfinalshop.service.ConsultationService;
import com.jfinalshop.util.IpUtil;
import com.xiaoleilu.hutool.util.CollectionUtil;

/**
 * Controller - 咨询
 * 
 */
@ControllerBind(controllerKey = "/business/consultation")
public class ConsultationController extends BaseController {

	@Inject
	private ConsultationService consultationService;
	@Inject
	private BusinessService businessService;

	/**
	 * 添加属性
	 */
	public void populateModel() {
		Long consultationId = getParaToLong("consultationId");
		Store currentStore = businessService.getCurrentStore();
		
		Consultation consultation = consultationService.find(consultationId);
		if (consultation != null && !currentStore.equals(consultation.getStore())) {
			throw new UnauthorizedException();
		}
		setAttr("consultation", consultation);
	}

	/**
	 * 回复
	 */
	public void reply() {
		Long consultationId = getParaToLong("consultationId");
		Consultation consultation = consultationService.find(consultationId);
		if (consultation == null) {
			setAttr("errorMessage", "咨询不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		setAttr("consultation", consultation);
		render("/business/consultation/reply.ftl");
	}

	/**
	 * 回复
	 */
	@Before(Tx.class)
	@ActionKey("/business/consultation/save_reply")
	public void saveReply() {
		Long consultationId = getParaToLong("consultationId");
		Consultation consultation = consultationService.find(consultationId);
		String content = getPara("content");
		if (consultation == null) {
			setAttr("errorMessage", "咨询不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		Consultation replyConsultation = new Consultation();
		replyConsultation.setContent(content);
		replyConsultation.setIp(IpUtil.getIpAddr(getRequest()));
		consultationService.reply(consultation, replyConsultation);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("reply?consultationId=" + consultation.getId());
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long consultationId = getParaToLong("consultationId");
		Consultation consultation = consultationService.find(consultationId);
		if (consultation == null) {
			setAttr("errorMessage", "咨询不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		setAttr("consultation", consultation);
		render("/business/consultation/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		Long consultationId = getParaToLong("consultationId");
		Consultation consultation = consultationService.find(consultationId);
		Boolean isShow = getParaToBoolean("isShow", false); 
		
		if (consultation == null) {
			setAttr("errorMessage", "咨询不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		if (isShow != consultation.getIsShow()) {
			consultation.setIsShow(isShow);
			consultationService.update(consultation);
		}
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		Store currentStore = businessService.getCurrentStore();
		
		setAttr("pageable", pageable);
		setAttr("page", consultationService.findPage(null, null, currentStore, null, pageable));
		render("/business/consultation/list.ftl");
	}

	/**
	 * 删除回复
	 */
	@ActionKey("/business/consultation/delete_reply")
	public void deleteReply() {
		Long consultationId = getParaToLong("consultationId");
		Consultation consultation = consultationService.find(consultationId);
		
		if (consultation == null || consultation.getForConsultation() == null) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}

		consultationService.delete(consultation);
		renderJson(Results.OK);
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		Store currentStore = businessService.getCurrentStore();
		for (Long id : ids) {
			Consultation consultation = consultationService.find(id);
			if (consultation == null || !currentStore.equals(consultation.getStore())) {
				Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
				return;
			}
			if (CollectionUtil.isNotEmpty(consultation.getReplyConsultations())) {
				Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
				return;
			}
		}
		consultationService.delete(ids);
		renderJson(Results.OK);
	}

}