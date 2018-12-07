package com.jfinalshop.api.controller.shop;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StrKit;
import com.jfinalshop.api.common.bean.Code;
import com.jfinalshop.api.common.bean.DatumResponse;
import com.jfinalshop.api.controller.BaseAPIController;
import com.jfinalshop.api.version.Version;
import com.jfinalshop.api.version.VersionManager;

/**
 * 
 * 移动API - 版本更新检查
 *
 */
@ControllerBind(controllerKey = "/api/version")
public class VersionAPIController extends BaseAPIController {

	 /**
     * 检查
     */
    public void check() {
        String version = getPara("version");//版本号
        String client = getPara("client"); //终端类型, 可选值有android, iphone
        
        //检查客户端是否为空
        if (StrKit.isBlank(client)) {
            renderArgumentError("客户端不能为空!");
            return;
        }
        
        //检查版本号是否为空
        if (StrKit.isBlank(version)) {
            renderArgumentError("版本号不能为空!");
            return;
        }
        
        //检查值是否有效
        if (!Version.checkType(client)) {
            renderArgumentError("客户端类型无效!");
            return;
        }
        
        Version result = VersionManager.me().check(version, client);
        DatumResponse response = new DatumResponse(result);
        if (result == null) {
            response.setCode(Code.FAIL);//表示无更新
        }
        
        renderJson(response);
    }
    
}
