package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Consultation;
import com.jfinalshop.service.ConsultationService;

/**
 * Controller - 咨询
 * 
 */
@ControllerBind(controllerKey = "/admin/consultation")
public class ConsultationController extends BaseController {

	@Inject
	private ConsultationService consultationService;

	/**
	 * 回复
	 */
	public void reply() {
		Long id = getParaToLong("id");
		setAttr("consultation", consultationService.find(id));
		render("/admin/consultation/reply.ftl");
	}
	
	/**
	 * 回复
	 */
	@Before(Tx.class)
	@ActionKey("/admin/consultation/save_reply")
	public void saveReply() {
		Long id = getParaToLong("id");
		String content = getPara("content");
		Consultation consultation = consultationService.find(id);
		if (consultation == null) {
			setAttr("errorMessage", "咨询为空!");
			redirect(ERROR_VIEW);
			return;
		}
		Consultation replyConsultation = new Consultation();
		replyConsultation.setContent(content);
		replyConsultation.setIp(getRequest().getRemoteAddr());
		consultationService.reply(consultation, replyConsultation);

		addFlashMessage(SUCCESS_MESSAGE);
		redirect("reply?id=" + id);
	}
	
	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("consultation", consultationService.find(id));
		render("/admin/consultation/edit.ftl");
	}

	/**
	 * 更新
	 */
	@Before(Tx.class)
	public void update() {
		Long id = getParaToLong("id");
		Boolean isShow = getParaToBoolean("isShow", false);
		
		Consultation consultation = consultationService.find(id);
		if (consultation == null) {
			setAttr("errorMessage", "咨询不存!");
			render(ERROR_VIEW);
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
		setAttr("pageable", pageable);
		setAttr("page", consultationService.findPage(null, null, null, null, pageable));
		render("/admin/consultation/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		if (ids != null) {
			consultationService.delete(ids);
		}
		renderJson(SUCCESS_MESSAGE);
	}

}