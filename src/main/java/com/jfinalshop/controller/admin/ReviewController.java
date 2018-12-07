package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Review;
import com.jfinalshop.service.ReviewService;

/**
 * Controller - 评论
 * 
 */
@ControllerBind(controllerKey = "/admin/review")
public class ReviewController extends BaseController {

	@Inject
	private ReviewService reviewService;

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("review", reviewService.find(id));
		render("/admin/review/edit.ftl");
	}

	/**
	 * 更新
	 */
	public void update() {
		Long id = getParaToLong("id");
		Boolean isShow = getParaToBoolean("isShow", false);
		Review review = reviewService.find(id);
		if (review == null) {
			setAttr("errorMessage", "评论为空!");
			render(ERROR_VIEW);
			return;
		}
		review.setIsShow(isShow);
		reviewService.update(review);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
		
	}

	/**
	 * 列表
	 */
	public void list() {
		Review.Type type = getParaEnum(Review.Type.class, getPara("type"));
		Pageable pageable = getBean(Pageable.class);
		setAttr("type", type);
		setAttr("types", Review.Type.values());
		setAttr("pageable", pageable);
		setAttr("page", reviewService.findPage(null, null, null, type, null, pageable));
		render("/admin/review/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		reviewService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}

}