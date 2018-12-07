package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinalshop.Pageable;
import com.jfinalshop.model.Ad;
import com.jfinalshop.model.AdPosition;
import com.jfinalshop.service.AdPositionService;
import com.jfinalshop.service.AdService;
import com.jfinalshop.util.EnumUtils;

/**
 * Controller - 广告
 * 
 */
@ControllerBind(controllerKey = "/admin/ad")
public class AdController extends BaseController {

	@Inject
	private AdService adService;
	@Inject
	private AdPositionService adPositionService;

	/**
	 * 添加
	 */
	public void add() {
		setAttr("types", Ad.Type.values());
		setAttr("adPositions", adPositionService.findAll());
		render("/admin/ad/add.ftl");
	}

	/**
	 * 保存
	 */
	@Before(Tx.class)
	public void save() {
		Ad ad = getModel(Ad.class);
		Long adPositionId = getParaToLong("adPositionId");
		Ad.Type type = EnumUtils.convert(Ad.Type.class, getPara("type"));
		ad.setType(type.ordinal());
		
		AdPosition adPosition = adPositionService.find(adPositionId);
		if (adPosition != null) {
			ad.setAdPositionId(adPosition.getId());
		}
		
		if (ad.getBeginDate() != null && ad.getEndDate() != null && ad.getBeginDate().after(ad.getEndDate())) {
			setAttr("errorMessage", "开始日期大于结束日期!");
			render(ERROR_VIEW);
			return;
		}
		
		if (Ad.Type.text.equals(ad.getTypeName())) {
			ad.setPath(null);
		} else {
			ad.setContent(null);
		}
		adService.save(ad);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 编辑
	 */
	public void edit() {
		Long id = getParaToLong("id");
		setAttr("types", Ad.Type.values());
		setAttr("ad", adService.find(id));
		setAttr("adPositions", adPositionService.findAll());
		render("/admin/ad/edit.ftl");
	}

	/**
	 * 更新
	 */
	@Before(Tx.class)
	public void update() {
		Ad ad = getModel(Ad.class);
		Long adPositionId = getParaToLong("adPositionId");
		String typeName = getPara("type");
		Ad.Type type = StrKit.notBlank(typeName) ? Ad.Type.valueOf(typeName) : null;
		ad.setType(type.ordinal());
		
		AdPosition adPosition = adPositionService.find(adPositionId);
		if (adPosition != null) {
			ad.setAdPositionId(adPosition.getId());
		}
		
		if (ad.getBeginDate() != null && ad.getEndDate() != null && ad.getBeginDate().after(ad.getEndDate())) {
			setAttr("errorMessage", "开始日期大于结束日期!");
			render(ERROR_VIEW);
			return;
		}
		if (Ad.Type.text.equals(ad.getTypeName())) {
			ad.setPath(null);
		} else {
			ad.setContent(null);
		}
		adService.update(ad);
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("list");
	}

	/**
	 * 列表
	 */
	public void list() {
		Pageable pageable = getBean(Pageable.class);
		setAttr("pageable", pageable);
		setAttr("page", adService.findPage(pageable));
		render("/admin/ad/list.ftl");
	}

	/**
	 * 删除
	 */
	public void delete() {
		Long[] ids = getParaValuesToLong("ids");
		adService.delete(ids);
		renderJson(SUCCESS_MESSAGE);
	}

}