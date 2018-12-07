package com.jfinalshop.controller.member;

import java.util.ArrayList;
import java.util.List;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.activerecord.Page;
import com.jfinalshop.Pageable;
import com.jfinalshop.interceptor.MobileInterceptor;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.PointLog;
import com.jfinalshop.service.MemberService;
import com.jfinalshop.service.PointLogService;
import com.xiaoleilu.hutool.util.CollectionUtil;

/**
 * Controller - 我的积分
 * 
 */
@ControllerBind(controllerKey = "/member/point_log")
public class PointLogController extends BaseController {

	/**
	 * 每页记录数
	 */
	private static final int PAGE_SIZE = 10;
	@Inject
	private PointLogService pointLogService;
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
		setAttr("page", pointLogService.findPage(currentUser, pageable));
		render("/member/point_log/list.ftl");
	}

	/**
	 * 列表
	 */
	@ActionKey("/member/point_log/m_list")
	public void mList() {
		Integer pageNumber = getParaToInt("pageNumber", 1);
		Member currentUser = memberService.getCurrentUser();
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		Page<PointLog> pages = pointLogService.findPage(currentUser, pageable);
		
		List<PointLog> pointLogs = new ArrayList<PointLog>();
		if (CollectionUtil.isNotEmpty(pages.getList())) {
			for (PointLog couponCode : pages.getList()) {
				couponCode.put("type", couponCode.getTypeName());
				pointLogs.add(couponCode);
			}
		}
		renderJson(pointLogs);
	}

}