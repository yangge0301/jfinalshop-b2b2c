package com.jfinalshop.api.controller.member;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinalshop.api.common.bean.BaseResponse;
import com.jfinalshop.api.common.bean.Code;
import com.jfinalshop.api.common.bean.DataResponse;
import com.jfinalshop.api.common.bean.DatumResponse;
import com.jfinalshop.api.controller.BaseAPIController;
import com.jfinalshop.api.interceptor.TokenInterceptor;
import com.jfinalshop.model.Area;
import com.jfinalshop.model.Member;
import com.jfinalshop.model.Receiver;
import com.jfinalshop.service.AreaService;
import com.jfinalshop.service.ReceiverService;
import com.jfinalshop.util.PhoneUtils;

/**
 * 会员中心 - 收货地址
 * @author yangzhicong
 */
@ControllerBind(controllerKey = "/api/member/receiver")
@Before(TokenInterceptor.class)
public class ReceiverAPIController extends BaseAPIController {

	@Inject
	private ReceiverService receiverService;
	@Inject
	private AreaService areaService;
	
	/**
	 * 全国区域列表
	 */
	public void areaList() {
		Long parentId = getParaToLong("parentId");
		List<Map<String, Object>> data = new ArrayList<>();
		Area parent = areaService.find(parentId);
		Collection<Area> areas = parent != null ? parent.getChildren() : areaService.findRoots();
		for (Area area : areas) {
			Map<String, Object> item = new HashMap<>();
			item.put("name", area.getName());
			item.put("value", area.getId());
			item.put("grade", area.getGrade());
			data.add(item);
		}
		renderJson(new DataResponse(data));
	}

	/**
	 * 最大收货地址保存条数
	 */
	public void maxCount() {
		DatumResponse response = new DatumResponse();
		response.setDatum(Receiver.MAX_RECEIVER_COUNT);
		renderJson(response);
	}
	
	/**
	 * 查找默认收货地址
	 */
	public void findDefault() {
		Member member = getMember();
		Receiver receiver = receiverService.findDefault(member);
		renderJson(new DatumResponse(receiver));
	}
	
	/**
	 * 保存收货地址
	 */
	public void save() {
		Receiver receiver = getModel(Receiver.class);

		if (receiver == null) {
			renderArgumentError("地址不能为空!");
			return;
		}
		Area area = areaService.find(receiver.getAreaId());
		if (area == null) {
			renderArgumentError("地区不能为空!");
			return;
		}
		if (StrKit.isBlank(receiver.getAddress())) {
			renderArgumentError("详细地址不能为空!");
			return;
		}
		if (StrKit.isBlank(receiver.getPhone()) || !PhoneUtils.isPhone(receiver.getPhone())) {
			renderArgumentError("检查手机号是否正确!");
			return;
		}
		
		Member member = getMember();
		if (Receiver.MAX_RECEIVER_COUNT != null
				&& member.getReceivers().size() >= Receiver.MAX_RECEIVER_COUNT) {
			renderArgumentError("超过收货地址最大保存数【" + Receiver.MAX_RECEIVER_COUNT
					+ "】");
			return;
		}

		receiver.setAreaName(area.getFullName());
		receiver.setMemberId(member.getId());
		receiver.setIsDefault(false);
		receiverService.save(receiver);
		renderJson(new BaseResponse(Code.SUCCESS, "保存成功!"));
	}

	/**
	 * 删除收货地址
	 */
	public void delete() {
		Long id = getParaToLong("id");
		Receiver receiver = receiverService.find(id);

		if (receiver == null) {
			renderArgumentError("收货地址不存在");
			return;
		}

		if (receiver.getIsDefault()) {
			renderArgumentError("不能删除默认地址");
			return;
		}

		Member member = getMember();
		if (!member.getId().equals(receiver.getMemberId())) {
			renderArgumentError("地址可能不是我哟!");
			return;
		}

		receiverService.delete(id);
		renderJson(new BaseResponse(Code.SUCCESS, "删除成功!"));
	}

	/**
	 * 更新收货地址
	 */
	public void update() {
		Receiver receiver = getModel(Receiver.class);
		
		if (receiver == null) {
			renderArgumentError("地址不能为空!");
			return;
		}
		Area area = areaService.find(receiver.getAreaId());
		if (area == null) {
			renderArgumentError("地区不能为空!");
			return;
		}
		if (StrKit.isBlank(receiver.getAddress())) {
			renderArgumentError("详细地址不能为空!");
			return;
		}
		if (StrKit.isBlank(receiver.getPhone()) || !PhoneUtils.isPhone(receiver.getPhone())) {
			renderArgumentError("检查手机号是否正确!");
			return;
		}
		Receiver pReceiver = receiverService.find(receiver.getId());
		if (pReceiver == null) {
			renderArgumentError("没有找到地址!");
			return;
		}
		Member member = getMember();
		if (!member.getId().equals(pReceiver.getMemberId())) {
			renderArgumentError("地址可能不是我哟!");
			return;
		}
		
		receiver.setAreaName(area.getFullName());
		receiver.setMemberId(pReceiver.getMemberId());
		receiverService.update(receiver);
		renderJson(new BaseResponse(Code.SUCCESS, "更新成功!"));
	}
	
	/**
	 * 收货地址列表
	 */
	public void list() {
		Member member = getMember();
		List<Receiver> list = receiverService.findList(member);
		
		DataResponse data = new DataResponse();
		if (list != null) {
			data.setData(list);
		}
		
		renderJson(data);
	}

}
