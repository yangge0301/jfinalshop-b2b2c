package com.jfinalshop.api.common.bean;

import java.util.List;

public class FileResponse extends DatumResponse {
	/*
	 * 保存上传失败的文件名
	 */
	private List<String>	failed;

	public List<String> getFailed() {
		return failed;
	}

	public FileResponse setFailed(List<String> failed) {
		this.failed = failed;
		return this;
	}
}
