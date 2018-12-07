package com.jfinalshop.controller.admin;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.service.CacheService;

/**
 * Controller - 缓存
 * 
 */
@ControllerBind(controllerKey = "/admin/cache")
public class CacheController extends BaseController {

	@Inject
	private CacheService cacheService;

	/**
	 * 缓存查看
	 */
	public void clear() {
		Long totalMemory = null;
		Long maxMemory = null;
		Long freeMemory = null;
		try {
			totalMemory = Runtime.getRuntime().totalMemory() / 1024 / 1024;
			maxMemory = Runtime.getRuntime().maxMemory() / 1024 / 1024;
			freeMemory = Runtime.getRuntime().freeMemory() / 1024 / 1024;
		} catch (Exception e) {
		}
		setAttr("totalMemory", totalMemory);
		setAttr("maxMemory", maxMemory);
		setAttr("freeMemory", freeMemory);
		setAttr("cacheSize", cacheService.getCacheSize());
		setAttr("diskStorePath", cacheService.getDiskStorePath());
		render("/admin/cache/clear.ftl");
	}

	/**
	 * 清除缓存
	 */
	public void clearSubmit() {
		cacheService.clear();
		addFlashMessage(SUCCESS_MESSAGE);
		redirect("clear");
	}

}