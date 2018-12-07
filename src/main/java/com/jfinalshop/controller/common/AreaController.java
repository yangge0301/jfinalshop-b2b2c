package com.jfinalshop.controller.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.hasor.core.Inject;

import com.jfinal.core.Controller;
import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.model.Area;
import com.jfinalshop.service.AreaService;

/**
 * Controller - 地区
 * 
 */
@ControllerBind(controllerKey = "/common/area")
public class AreaController extends Controller {

	@Inject
	private AreaService areaService;

	/**
	 * 地区
	 */
	public void index() {
		Long parentId = getParaToLong("parentId");
		List<Map<String, Object>> data = new ArrayList<>();
		Area parent = areaService.find(parentId);
		Collection<Area> areas = parent != null ? parent.getChildren() : areaService.findRoots();
		for (Area area : areas) {
			Map<String, Object> item = new HashMap<>();
			item.put("name", area.getName());
			item.put("value", area.getId());
			data.add(item);
		}
		renderJson(data);
	}

}