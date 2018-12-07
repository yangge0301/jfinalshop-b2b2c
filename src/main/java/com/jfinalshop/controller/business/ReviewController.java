package com.jfinalshop.controller.business;

import net.hasor.core.Inject;

import org.apache.shiro.authz.UnauthorizedException;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Pageable;
import com.jfinalshop.Results;
import com.jfinalshop.model.Review;
import com.jfinalshop.model.Store;
import com.jfinalshop.service.BusinessService;
import com.jfinalshop.service.ReviewService;
import com.jfinalshop.util.IpUtil;

/**
 * Controller - 评论
 * 
 */
@ControllerBind(controllerKey = "/business/review")
public class ReviewController extends BaseController {

	@Inject
	private ReviewService reviewService;
	@Inject
	private BusinessService businessService;

	/**
	 * 添加属性
	 */
	public void populateModel() {
		Long reviewId = getParaToLong("reviewId"); 
		Store currentStore = businessService.getCurrentStore();
		
		Review review = reviewService.find(reviewId);
		if (review != null && !currentStore.equals(review.getStore())) {
			throw new UnauthorizedException();
		}
		setAttr("review", review);
	}

	/**
	 * 回复
	 */
	public void reply() {
		Long reviewId = getParaToLong("reviewId"); 
		Review review = reviewService.find(reviewId);
		
		if (review == null) {
			setAttr("errorMessage", "评论不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		setAttr("review", review);
		render("/business/review/reply.ftl");
	}

	/**
	 * 回复
	 */
	@Before(Tx.class)
	@ActionKey("/business/review/save_reply")
	public void saveReply() {
		Long reviewId = getParaToLong("reviewId"); 
		Review review = reviewService.find(reviewId);
		String content = getPara("content");
		if (review == null) {
			setAttr("errorMessage", "评论不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}

		Review replyReview = new Review();
		replyReview.setContent(content);
		replyReview.setIp(IpUtil.getIpAddr(getRequest()));
		review.setIsShow(true);
		reviewService.reply(review, replyReview);
		reviewService.update(review);

		addFlashMessage(SUCCESS_MESSAGE);
		redirect("reply?reviewId=" + review.getId());
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long reviewId = getParaToLong("reviewId"); 
		Review review = reviewService.find(reviewId);
		if (review == null) {
			setAttr("errorMessage", "评论不存在!");
			render(UNPROCESSABLE_ENTITY_VIEW);
			return;
		}
		setAttr("review", review);
		render("/business/review/edit.ftl");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		Review.Type type = getParaEnum(Review.Type.class, getPara("type"));
		
		Store currentStore = businessService.getCurrentStore();
		
		setAttr("type", type);
		setAttr("types", Review.Type.values());
		setAttr("pageable", pageable);
		setAttr("page", reviewService.findPage(null, null, currentStore, type, null, pageable));
		render("/business/review/list.ftl");
	}

	/**
	 * 删除回复
	 */
	@ActionKey("/business/review/delete_reply")
	public void deleteReply() {
		Long reviewId = getParaToLong("reviewId"); 
		Review review = reviewService.find(reviewId);
		
		if (review == null || review.getForReview() == null) {
			Results.unprocessableEntity(getResponse(), Results.DEFAULT_UNPROCESSABLE_ENTITY_MESSAGE);
			return;
		}

		reviewService.delete(review);
		renderJson(Results.OK);
	}

}