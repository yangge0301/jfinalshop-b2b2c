package com.jfinalshop.controller.shop;

import net.hasor.core.Inject;

import com.jfinal.ext.route.ControllerBind;
import com.jfinalshop.model.FriendLink;
import com.jfinalshop.service.FriendLinkService;

/**
 * Controller - 友情链接
 * 
 */
@ControllerBind(controllerKey = "/friend_link")
public class FriendLinkController extends BaseController {

	@Inject
	private FriendLinkService friendLinkService;

	/**
	 * 首页
	 */
	public void index() {
		setAttr("textFriendLinks", friendLinkService.findList(FriendLink.Type.text));
		setAttr("imageFriendLinks", friendLinkService.findList(FriendLink.Type.image));
		render("/shop/friend_link/index.ftl");
	}

}