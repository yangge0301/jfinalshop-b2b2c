package com.jfinalshop.api.controller.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.upload.UploadFile;
import com.jfinalshop.FileType;
import com.jfinalshop.api.common.bean.Code;
import com.jfinalshop.api.common.bean.FileResponse;
import com.jfinalshop.api.controller.BaseAPIController;
import com.jfinalshop.api.interceptor.TokenInterceptor;
import com.jfinalshop.service.FileService;

/**
 * 
 * 移动API - 文件上传
 *
 */
@Before(TokenInterceptor.class)
@ControllerBind(controllerKey = "/api/fs")
public class FileAPIController extends BaseAPIController {

	private FileService	fileService	= new FileService();

	/**
	 * 处理单文件或多文件上传，上传成功后，返回url集合
	 */
	public void upload() {
		if (!methodType("post")) {
			render404();
			return;
		}
		FileResponse response = new FileResponse();
		try {
			List<UploadFile> fileList = getFiles();// 已接收到的文件
			if (fileList != null && !fileList.isEmpty()) {
				Map<String, String> urls = new HashMap<String, String>();// 用于保存上传成功的文件地址
				List<String> failedFiles = new ArrayList<String>(); // 用于保存未成功上传的文件名

				for (UploadFile uploadFile : fileList) {
					FileType fileType = FileType.valueOf("image");
					String url = fileService.upload(fileType, uploadFile, false);
					if (StringUtils.isEmpty(url)) {
						failedFiles.add(uploadFile.getParameterName());// 标记为上传失败
					} else {
						// 返回相对路径,用于响应
						urls.put(uploadFile.getParameterName(), url + uploadFile.getFileName());
					}
				}
				response.setDatum(urls);
				response.setMessage("上传成功！");
				if (failedFiles.size() > 0) {
					response.setCode(Code.FAIL);// 表示此次上传有未上传成功的文件
					response.setFailed(failedFiles);
				}
			} else {
				response.setCode(Code.ARGUMENT_ERROR).setMessage("uploadFileName can not be null");
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setCode(Code.ERROR);
		}
		renderJson(response);
	}
}