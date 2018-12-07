package com.jfinalshop.controller.admin;

import java.util.HashMap;
import java.util.Map;

import net.hasor.core.Inject;

import org.apache.commons.lang.StringUtils;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.upload.UploadFile;
import com.jfinalshop.FileType;
import com.jfinalshop.Message;
import com.jfinalshop.service.FileService;

/**
 * Controller - 文件
 * 
 */
@ControllerBind(controllerKey = "/admin/file")
public class FileController extends BaseController {

	@Inject
	private FileService fileService;

	private Res res = I18n.use();
	/**
	 * 上传
	 */
	public void upload() {
		UploadFile file = getFile();
		FileType fileType = FileType.valueOf(getPara("fileType", "image"));
		
		Map<String, Object> data = new HashMap<>();
		if (fileType == null || file == null || file.getFile().length() <= 0) {
			data.put("message", ERROR_MESSAGE);
			data.put("state", ERROR_MESSAGE);
			renderJson(data);
			return;
		}
		if (!fileService.isValid(fileType, file)) {
			data.put("message", Message.warn("admin.upload.invalid"));
			data.put("state", res.format("admin.upload.invalid"));
			renderJson(data);
			return;
		}
		String url = fileService.upload(fileType, file, false);
		if (StringUtils.isEmpty(url)) {
			data.put("message", Message.warn("admin.upload.error"));
			data.put("state", res.format("admin.upload.error"));
			renderJson(data);
			return;
		}
		data.put("message", SUCCESS_MESSAGE);
		data.put("state", "SUCCESS");
		data.put("url", url);
		renderJson(data);
	}

}