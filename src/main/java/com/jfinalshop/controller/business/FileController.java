package com.jfinalshop.controller.business;

import java.util.HashMap;
import java.util.Map;

import net.hasor.core.Inject;

import org.apache.commons.lang.StringUtils;

import com.jfinal.ext.route.ControllerBind;
import com.jfinal.upload.UploadFile;
import com.jfinalshop.FileType;
import com.jfinalshop.Results;
import com.jfinalshop.service.FileService;

/**
 * Controller - 文件
 * 
 */
@ControllerBind(controllerKey = "/business/file")
public class FileController extends BaseController {

	@Inject
	private FileService fileService;

	/**
	 * 上传
	 */
	public void upload() {
		UploadFile file = getFile();
		FileType fileType = FileType.valueOf(getPara("fileType", "image"));
		
		Map<String, Object> data = new HashMap<>();
		if (fileType == null || file == null || file.getFile().length() <= 0) {
			renderJson(Results.UNPROCESSABLE_ENTITY);
			return;
		}
		if (!fileService.isValid(fileType, file)) {
			renderJson(Results.unprocessableEntity("business.upload.invalid"));
			return;
		}
		String url = fileService.upload(fileType, file, false);
		if (StringUtils.isEmpty(url)) {
			renderJson(Results.unprocessableEntity("business.upload.error"));
			return;
		}
		data.put("url", url);
		renderJson(data);
	}

}