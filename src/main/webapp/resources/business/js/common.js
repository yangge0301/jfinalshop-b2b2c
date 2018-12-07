// UUID
var uuidChars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".split("");
function uuid() {
	var r;
	var uuid = [];
	uuid[8] = uuid[13] = uuid[18] = uuid[23] = "-";
	uuid[14] = "4";
	
	for (i = 0; i < 36; i++) {
		if (!uuid[i]) {
			r = 0 | Math.random() * 16;
			uuid[i] = uuidChars[(i == 19) ? (r & 0x3) | 0x8 : r];
		}
	}
	return uuid.join("");
}

// 添加Cookie
function addCookie(name, value, options) {
	if (arguments.length > 1 && name != null) {
		if (options == null) {
			options = {};
		}
		if (value == null) {
			options.expires = -1;
		}
		if (typeof options.expires == "number") {
			var time = options.expires;
			var expires = options.expires = new Date();
			expires.setTime(expires.getTime() + time * 1000);
		}
		if (options.path == null) {
			options.path = "/";
		}
		if (options.domain == null) {
			options.domain = "";
		}
		document.cookie = encodeURIComponent(String(name)) + "=" + encodeURIComponent(String(value)) + (options.expires != null ? "; expires=" + options.expires.toUTCString() : "") + (options.path != "" ? "; path=" + options.path : "") + (options.domain != "" ? "; domain=" + options.domain : "") + (options.secure != null ? "; secure" : "");
	}
}

// 获取Cookie
function getCookie(name) {
	if (name != null) {
		var value = new RegExp("(?:^|; )" + encodeURIComponent(String(name)) + "=([^;]*)").exec(document.cookie);
		return value ? decodeURIComponent(value[1]) : null;
	}
}

// 移除Cookie
function removeCookie(name, options) {
	addCookie(name, null, options);
}

// Html转义
function escapeHtml(str) {
	return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}

// 字符串缩略
function abbreviate(str, width, ellipsis) {
	if ($.trim(str) == "" || width == null) {
		return str;
	}
	var i = 0;
	for (var strWidth = 0; i < str.length; i++) {
		strWidth = /^[\u4e00-\u9fa5\ufe30-\uffa0]$/.test(str.charAt(i)) ? strWidth + 2 : strWidth + 1;
		if (strWidth >= width) {
			break;
		}
	}
	return ellipsis != null && i < str.length - 1 ? str.substring(0, i) + ellipsis : str.substring(0, i);
}

// 货币格式化
function currency(value, showSign, showUnit) {
	if (value != null) {
			var price = (Math.round(value * Math.pow(10, 2)) / Math.pow(10, 2)).toFixed(2);
		if (showSign) {
			price = "￥" + price;
		}
		if (showUnit) {
			price += "元";
		}
		return price;
	}
}

(function($) {

	// 警告框
	$.alert = function() {
		var type = arguments.length >= 2 ? arguments[0] : null;
		var message = arguments.length >= 2 ? arguments[1] : arguments[0];
		var alertClass;
		switch(type) {
			case "success":
				alertClass = "alert-success";
				break;
			case "info":
				alertClass = "alert-info";
				break;
			case "warning":
				alertClass = "alert-warning";
				break;
			case "danger":
				alertClass = "alert-danger";
				break;
			default:
				alertClass = "alert-dark";
		}
		var $alert = $('<div class="growl animated fadeInDown alert' + (alertClass != null ? ' ' + alertClass : '') + ' alert-dismissible fade in"><button class="close" type="button" data-dismiss="alert"><span>&times;<\/span><\/button>' + message + '<\/div>').appendTo("body");
		setTimeout(function() {
			$alert.alert("close");
		}, 3000);
	};
	
	// 重定向登录页面
	$.redirectLogin = function(redirectUrl) {
		var loginUrl = "/business/login";
		if ($.trim(redirectUrl) != "") {
			var redirectToken = uuid();
			addCookie("redirectToken", redirectToken);
			loginUrl += "?redirectUrl=" + encodeURIComponent(redirectUrl) + "&redirectToken=" + encodeURIComponent(redirectToken);
		}
		location.href = loginUrl;
	};

})(jQuery);

// 验证码图片
(function($) {

	$.fn.captchaImage = function() {
		var method = arguments[0];
		
		if (methods[method]) {
			method = methods[method];
			arguments = Array.prototype.slice.call(arguments, 1);
		} else if (typeof(method) == "object" || !method) {
			method = methods.init;
		}
		return method.apply(this, arguments);
	};
	
	$.fn.captchaImage.defaults = {
		captchaIdParameterName: "captchaId",
		imgClass: "captcha-image",
		imgSrc: function(captchaIdParameterName, captchaId) {
			return "/common/captcha/image?" + captchaIdParameterName + '=' + captchaId + '&timestamp=' + new Date().getTime() + '&fontsize=' + 30 + '&height=' + 34 + '&width=' + 117 + '&bgColor=';
		},
		imgTitle: "点击更换验证码",
		imgPlacement: function($captchaImage) {
			var $element = $(this);
			
			$inputGroupBtn = $element.nextAll(".input-group-btn");
			if ($inputGroupBtn.size() > 0) {
				$captchaImage.appendTo($inputGroupBtn);
			} else {
				$captchaImage.insertAfter($element);
			}
		}
	};
	
	var methods = {
		init: function(options) {
			return this.each(function() {
				var settings = $.extend({}, $.fn.captchaImage.defaults, options);
				var element = this;
				var $element = $(element);
				var captchaId = uuid();
				
				var refresh = function(clearValue) {
					if (clearValue) {
						$element.val("");
					}
					$captchaImage.attr("src", $.isFunction(settings.imgSrc) ? settings.imgSrc.call(element, settings.captchaIdParameterName, captchaId) : settings.imgSrc);
				};
				$element.data("refresh", refresh);
				
				var $captchaId = $('<input name="' + settings.captchaIdParameterName + '" type="hidden" value="' + captchaId + '">').insertAfter($element);
				var $captchaImage = $('<img' + (settings.imgClass != null ? ' class="' + settings.imgClass + '"' : '') + ' src="' + ($.isFunction(settings.imgSrc) ? settings.imgSrc.call(element, settings.captchaIdParameterName, captchaId) : settings.imgSrc) + '"' + (settings.imgTitle != null ? ' title="' + settings.imgTitle + '"' : '') + '>');
				if ($.isFunction(settings.imgPlacement)) {
					settings.imgPlacement.call(element, $captchaImage);
				}
				$captchaImage.click(function() {
					refresh(true);
				});
			});
		},
		refresh: function(options) {
			return this.each(function() {
				var element = this;
				var $element = $(element);
				
				var refresh = $element.data("refresh");
				if (refresh != null) {
					refresh(options);
				}
			});
		}
	};

})(jQuery);

// 删除项目
(function($) {

	$.fn.deleteItem = function() {
		var method = arguments[0];
		
		if (methods[method]) {
			method = methods[method];
			arguments = Array.prototype.slice.call(arguments, 1);
		} else if (typeof(method) == "object" || !method) {
			method = methods.init;
		}
		return method.apply(this, arguments);
	};
	
	$.fn.deleteItem.defaults = {
		url: "delete",
		type: "POST",
		data: null,
		dataType: "json",
		removeElement: null,
		complete: null
	};
	
	var methods = {
		init: function(options) {
			return this.each(function() {
				var settings = $.extend({}, $.fn.deleteItem.defaults, options);
				var element = this;
				var $element = $(element);
				
				$element.click(function() {
					if (confirm("您确定要删除吗？")) {
						var data = $.isFunction(settings.data) ? settings.data.call(element) : settings.data;
						$.ajax({
							url: settings.url,
							type: settings.type,
							data: data,
							dataType: settings.dataType,
							success: function() {
								$removeElement = $.isFunction(settings.removeElement) ? settings.removeElement.call(element) : $(settings.removeElement);
								if ($removeElement != null) {
									$removeElement.velocity("fadeOut", {
										complete: function() {
											$(this).remove();
											if ($.isFunction(settings.complete)) {
												settings.complete.call(element, data);
											}
										}
									});
								} else {
									if ($.isFunction(settings.complete)) {
										settings.complete.call(element, data);
									}
								}
							}
						});
					}
				});
			});
		}
	};

})(jQuery);

// 全选
(function($) {

	$.fn.checkAll = function() {
		var method = arguments[0];
		
		if (methods[method]) {
			method = methods[method];
			arguments = Array.prototype.slice.call(arguments, 1);
		} else if (typeof(method) == "object" || !method) {
			method = methods.init;
		}
		return method.apply(this, arguments);
	};
	
	$.fn.checkAll.defaults = {
		target: "input[name='ids']"
	};
	
	function check($element, $target) {
		$element.removeClass("fa-square-o").addClass("fa-check-square-o").data("checked", true);
		$target.iCheck("check");
	}
	
	function uncheck($element, $target) {
		$element.removeClass("fa-check-square-o").addClass("fa-square-o").data("checked", false);
		$target.iCheck("uncheck");
	}
	
	var methods = {
		init: function(options) {
			return this.each(function() {
				var settings = $.extend({}, $.fn.checkAll.defaults, options);
				var element = this;
				var $element = $(element);
				
				$element.click(function() {
					var $target = $(settings.target);
					var checked = $element.data("checked");
					
					if (checked) {
						uncheck($element, $target);
					} else {
						check($element, $target);
					}
				});
			});
		},
		check: function(options) {
			return this.each(function() {
				var settings = $.extend({}, $.fn.checkAll.defaults, options);
				var $element = $(this);
				var $target = $(settings.target);
				
				check($element, $target);
			});
		},
		uncheck: function(options) {
			return this.each(function() {
				var settings = $.extend({}, $.fn.checkAll.defaults, options);
				var $element = $(this);
				var $target = $(settings.target);
				
				uncheck($element, $target);
			});
		}
	};

})(jQuery);

// 添加项目
(function($) {

	$.fn.addItem = function() {
		var method = arguments[0];
		
		if (methods[method]) {
			method = methods[method];
			arguments = Array.prototype.slice.call(arguments, 1);
		} else if (typeof(method) == "object" || !method) {
			method = methods.init;
		}
		return method.apply(this, arguments);
	};
	
	$.fn.addItem.defaults = {
		target: null,
		template: null,
		data: null,
		complete: null
	};
	
	var methods = {
		init: function(options) {
			return this.each(function() {
				var settings = $.extend({}, $.fn.addItem.defaults, options);
				var element = this;
				var $element = $(element);
				var target = $.isFunction(settings.target) ? settings.target.call(element) : settings.target;
				var $target = target instanceof jQuery ? target : $(target);
				var template = _.template($.isFunction(settings.template) ? settings.template.call(element) : settings.template);
				var data = $.isFunction(settings.data) ? settings.data.call(element) : settings.data;
				var index = 0;
				
				$element.click(function() {
					var actualData = $.extend({}, data, {
						index: index ++
					});
					var $item = $(template(actualData)).hide().appendTo($target).velocity("fadeIn");
					if ($.isFunction(settings.complete)) {
						settings.complete.call(element, $item, actualData);
					}
				});
			});
		}
	};

})(jQuery);

$().ready(function() {

	var $form = $("form");
	var $pageSize = $("input[name='pageable.pageSize']");
	var $pageNumber = $("input[name='pageable.pageNumber']");
	var $searchProperty = $("input[name='pageable.searchProperty']");
	var $orderProperty = $("input[name='pageable.orderProperty']");
	var $orderDirection = $("input[name='orderDirection']");
	var $button = $(".btn");
	var $deleteToggle = $("[data-toggle='delete']");
	var $refreshToggle = $("[data-toggle='refresh']");
	var $filterPropertyItem = $("[data-filter-property]");
	var $pageSizeItem = $("[data-page-size]");
	var $searchPropertyItem = $("[data-search-property]");
	var $searchValue = $("#search input[name='pageable.searchValue']");
	var $searchSubmit = $("#search :submit");
	var $checkAllToggle = $("[data-toggle='checkAll']");
	var $ids = $("input[name='ids']");
	var $orderPropertyItem = $("[data-order-property]");
	var $pageNumberItem = $("[data-page-number]");
	var $backToggle = $("[data-toggle='back']");
	
	// 按钮
	$button.click(function() {
		var $element = $(this);
		
		if ($.support.transition) {
			$element.addClass("btn-clicked").one("bsTransitionEnd", function() {
				$(this).removeClass("btn-clicked");
			}).emulateTransitionEnd(300);
		}
	});
	
	// 日期选择
	if ($.fn.datetimepicker != null) {
		var $dateTimePicker = $("[data-provide='datetimepicker']");
		var $dateTimeRangePicker = $("[data-provide='datetimerangepicker']");
		
		$.extend($.fn.datetimepicker.defaults, {
			locale: moment.locale("zh_CN"),
			format: "YYYY-MM-DD"
		});
		
		$dateTimePicker.datetimepicker({
			format: $(this).data("date-format")
		});
		
		$dateTimeRangePicker.each(function() {
			var $element = $(this);
			var $startDateTimePicker = $element.find("input:text:eq(0)");
			var $endDateTimePicker = $element.find("input:text:eq(1)");
			
			$startDateTimePicker.datetimepicker({
				format: $element.data("date-format")
			}).on("dp.change", function(e) {
				$endDateTimePicker.data("DateTimePicker").minDate(e.date);
			});
			
			$endDateTimePicker.datetimepicker({
				format: $element.data("date-format"),
				useCurrent: false
			}).on("dp.change", function(e) {
				$startDateTimePicker.data("DateTimePicker").maxDate(e.date);
			});
		});
	}
	
	// 文本编辑器
	if ($.fn.summernote != null) {
		var $editor = $("[data-provide='editor']");
		
		$editor.summernote({
			minHeight: 300
		});
	}
	
	// 文件上传
	if ($.fn.fileinput != null) {
		var $fileinput = $("[data-provide='fileinput']");
		
		$fileinput.each(function() {
			var $element = $(this);
			var fileType = $element.data("file-type");
			var showPreview = $element.data("show-preview");
			var allowedFileExtensions;
			
			switch(fileType) {
				case "media":
					allowedFileExtensions = "swf,flv,mp3,wav,avi,rm,rmvb".split(",");
					break;
				case "file":
					allowedFileExtensions = "zip,rar,7z,doc,docx,xls,xlsx,ppt,pptx".split(",");
					break;
				default:
					allowedFileExtensions = "jpg,jpeg,bmp,gif,png".split(",");
			}
			
			var $file = $('<input name="file" type="file">').insertAfter($element).fileinput({
				uploadUrl: "/business/file/upload",
				uploadExtraData: {
					fileType: fileType != null ? fileType : "image"
				},
				allowedFileExtensions: allowedFileExtensions,
					maxFileSize: 10 * 1024,
				maxFileCount: 1,
				autoReplace: true,
				showUpload: false,
				showRemove: false,
				showClose: false,
				showUploadedThumbs: false,
				dropZoneEnabled: false,
				initialPreview: $element.val(),
				initialPreviewAsData: true,
				showPreview: showPreview != null ? showPreview : true,
				previewClass: "single-file-preview",
				layoutTemplates: {
					footer: '<div class="file-thumbnail-footer">{actions}</div>',
					actions: '<div class="file-actions"><div class="file-footer-buttons">{upload} {delete} {zoom} {other}</div></div>'
				},
				fileActionSettings: {
					showUpload: false,
					showRemove: false,
					showDrag: false
				},
				removeFromPreviewOnError: true,
				showAjaxErrorDetails: false
			}).on("fileloaded", function(event, file, previewId, index, reader) {
				$(this).fileinput("upload");
			}).on("fileuploaded", function(event, data, previewId, index) {
				$element.val(data.response.url);
			});
			
			$element.data("file", $file);
		});
	}
	
	// 删除
	$deleteToggle.deleteItem({
		url: "delete",
		data: function() {
			return $ids.serialize();
		},
		removeElement: function() {
			return $ids.filter(":checked").closest("tr");
		},
		complete: function() {
			$ids = $("input[name='ids']");
			$(this).attr("disabled", true);
			$checkAllToggle.checkAll("uncheck");
			if ($ids.size() < 1) {
				setTimeout(function() {
					location.reload(true);
				}, 3000);
			}
		}
	});
	
	// 刷新
	$refreshToggle.click(function() {
		location.reload(true);
		return false;
	});
	
	// 筛选
	$filterPropertyItem.click(function() {
		var $element = $(this);
		var filterProperty = $element.data("filter-property");
		var filterValue = $element.data("filter-value");
		
		$("input[name='" + filterProperty + "']").val($element.hasClass("active") ? "" : filterValue);
		$pageNumber.val(1);
		$form.submit();
	});
	
	// 每页显示
	$pageSizeItem.click(function() {
		var $element = $(this);
		
		$pageSize.val($element.data("page-size"));
		$pageNumber.val(1);
		$form.submit();
	});
	
	// 搜索属性
	$searchPropertyItem.click(function() {
		var $element = $(this);
		
		$element.addClass("active").siblings().removeClass("active");
		$element.closest("div.input-group").find("[data-toggle='dropdown'] span").text($element.text());
	});
	
	// 搜索值
	$searchValue.keypress(function(event) {
		if (event.which == 13) {
			$searchSubmit.click();
			return false;
		}
	});
	
	// 搜索提交
	$searchSubmit.click(function() {
		$pageNumber.val(1);
		$searchProperty.val($searchPropertyItem.filter(".active").data("search-property"));
	});
	
	// 全选
	$checkAllToggle.checkAll();
	
	// ID多选框
	if ($.fn.iCheck != null) {
		$ids.iCheck({
			checkboxClass: "icheckbox-flat-blue",
			radioClass: "iradio-flat-blue"
		}).on("ifChanged", function() {
			$deleteToggle.attr("disabled", $ids.filter(":checked").size() < 1);
		});
	}
	
	// 排序
	$("[data-order-property='" + $orderProperty.val() + "'] .fa").removeClass("fa-sort").addClass($orderDirection.val() == "asc" ? "fa-sort-asc" : "fa-sort-desc");
	$orderPropertyItem.click(function() {
		var $element = $(this);
		
		$orderProperty.val($element.data("order-property"));
		$orderDirection.val($orderDirection.val() == "asc" ? "desc" : "asc");
		$form.submit();
		return false;
	});
	
	// 页码
	$pageNumberItem.click(function() {
		var $element = $(this);
		
		$pageNumber.val($element.data("page-number"));
		$form.submit();
		return false;
	});
	
	// 返回
	$backToggle.click(function() {
		history.back();
		return false;
	});
	
	// AJAX全局设置
	$.ajaxSetup({
		traditional: true,
		statusCode: {
			401: function(xhr, textStatus, errorThrown) {
				var data = $.parseJSON(xhr.responseText);
				if (data.message != null) {
					$.alert("danger", data.message);
				}
				setTimeout(function() {
					$.redirectLogin(location.href);
				}, 3000);
			},
			403: function(xhr, textStatus, errorThrown) {
				var data = $.parseJSON(xhr.responseText);
				if (data.message != null) {
					$.alert("danger", data.message);
				}
			},
			422: function(xhr, textStatus, errorThrown) {
				var data = $.parseJSON(xhr.responseText);
				if (data.message != null) {
					$.alert("warning", data.message);
				}
			}
		}
	});
	
	// AJAX全局设置
	$(document).ajaxSuccess(function(event, xhr, settings, data) {
		if (data.message != null) {
			$.alert(data.message);
		}
	});
	
	// CSRF令牌
	$("form").submit(function() {
		var $element = $(this);
		
		if (!/^(GET|HEAD|TRACE|OPTIONS)$/i.test($element.attr("method")) && $element.find("input[name='csrfToken']").size() == 0) {
			var csrfToken = getCookie("csrfToken");
			if (csrfToken != null) {
				$element.append('<input name="csrfToken" type="hidden" value="' + csrfToken + '">');
			}
		}
	});
	
	// CSRF令牌
	$(document).ajaxSend(function(event, xhr, settings) {
		if (!settings.crossDomain && !/^(GET|HEAD|TRACE|OPTIONS)$/i.test(settings.type)) {
			var csrfToken = getCookie("csrfToken");
			if (csrfToken != null) {
				xhr.setRequestHeader("X-Csrf-Token", csrfToken);
			}
		}
	});

});

$().ready(function() {

	var $icheck = $("input.icheck");
	var $addItemToggle = $("[data-toggle='add-item']");
	
	// 多选框
	if ($.fn.iCheck != null) {
		$icheck.iCheck({
			checkboxClass: "icheckbox-flat-blue",
			radioClass: "iradio-flat-blue"
		});
	}
	
	// 添加项目
	$addItemToggle.addItem({
		target: function() {
			return $(this).data("target");
		},
		template: function() {
			var $element = $(this);
			
			return $($element.data("template")).html();
		}
	});
	
});

(function($) {

	// 表单验证
	if ($.validator != null) {
		$.extend($.validator.messages, {
			required: "必填",
			email: "E-mail格式错误",
			url: "网址格式错误",
			date: "日期格式错误",
			dateISO: "日期格式错误",
			pointcard: "信用卡格式错误",
			number: "只允许输入数字",
			digits: "只允许输入零或正整数",
			minlength: $.validator.format("长度不允许小于{0}"),
			maxlength: $.validator.format("长度不允许大于{0}"),
			rangelength: $.validator.format("长度必须在{0}-{1}之间"),
			min: $.validator.format("不允许小于{0}"),
			max: $.validator.format("不允许大于{0}"),
			range: $.validator.format("必须在{0}-{1}之间"),
			accept: "输入后缀错误",
			equalTo: "两次输入不一致",
			remote: "输入错误",
			integer: "只允许输入整数",
			positive: "只允许输入正数",
			negative: "只允许输入负数",
			decimal: "数值超出了允许范围",
			pattern: "格式错误",
			extension: "文件格式错误"
		});
		
		$.validator.setDefaults({
			ignore: ".ignore",
			ignoreTitle: true,
			errorElement: "span",
			errorClass: "help-block",
			highlight: function(element, errorClass, validClass) {
				$(element).closest(".form-group").addClass("has-error");
			},
			unhighlight: function(element, errorClass, validClass) {
				$(element).closest(".form-group").removeClass("has-error");
			},
			errorPlacement: function($error, $element) {
				var $formGroup = $element.closest("td, [class^='col-'], .radio, .checkbox, .form-group");
				if ($formGroup.size() > 0) {
					$error.appendTo($formGroup);
				} else {
					$error.insertAfter($element);
				}
			},
			submitHandler: function(form) {
				$(form).find("button:submit").prop("disabled", true);
				form.submit();
			}
		});
	}
	
	// 选择框
	if ($.fn.checkboxX != null) {
		$.extend($.fn.checkboxX.defaults, {
			size: "xs",
			threeState: false,
			valueChecked: "true",
			valueUnchecked: "false"
		});
	}
	
	// 下拉菜单
	if ($.fn.selectpicker != null) {
		$.fn.selectpicker.defaults = $.extend($.fn.selectpicker.defaults, {
			noneSelectedText: "没有选中任何内容",
			noneResultsText: "没有找到匹配内容",
			countSelectedText: "选中{1}中的{0}项",
			maxOptionsText: "超出限制(最多选择{n}项)",
			maxGroupOptionsText: "组选择超出限制(最多选择{n}组)",
			multipleSeparator: ","
		});
	}
	
	// 下拉菜单搜索
	if ($.fn.ajaxSelectPicker != null) {
		$.extend($.fn.ajaxSelectPicker.defaults, {
			langCode: "zh_CN"
		});
		$.fn.ajaxSelectPicker.locale["zh_CN"] = {
			searchPlaceholder: "搜索",
			statusInitialized: "输入一个搜索查询",
			statusNoResults: "没有找到匹配内容"
		}
	}
	
	// 文件上传
	if ($.fn.fileinput != null) {
		$.extend($.fn.fileinput.defaults, {
			language: "zh_CN"
		});
		
		$.fn.fileinputLocales["zh_CN"] = {
			fileSingle: "文件",
			filePlural: "个文件",
			browseLabel: "选择…",
			removeLabel: "移除",
			removeTitle: "清除选中文件",
			cancelLabel: "取消",
			cancelTitle: "取消正在上传的文件",
			uploadLabel: "上传",
			uploadTitle: "上传选中文件",
			msgNo: "没有",
			msgNoFilesSelected: "",
			msgCancelled: "取消",
			msgZoomModalHeading: "预览",
			msgSizeTooSmall: "文件 {name} 不允许小于{minSize}KB",
			msgSizeTooLarge: "文件 {name} 不允许大于{maxSize}KB",
			msgFilesTooLess: "上传文件数不允许小于{n}",
			msgFilesTooMany: "上传文件数不允许大于{m}",
			msgFileNotFound: "文件不存在 {name}",
			msgFileSecured: "文件无法读取 {name}",
			msgFileNotReadable: "文件无法读取 {name}",
			msgFilePreviewAborted: "取消 {name} 预览",
			msgFilePreviewError: "读取 {name} 错误",
			msgInvalidFileName: "文件名错误 {name}",
			msgInvalidFileType: "文件类型错误 {name}",
			msgInvalidFileExtension: "文件类型错误 {name}",
			msgFileTypes: {
				image: "图片",
				html: "HTML",
				text: "text",
				video: "video",
				audio: "audio",
				flash: "Flash",
				pdf: "PDF",
				object: "object"
			},
			msgUploadAborted: "文件上传被中止",
			msgUploadThreshold: "处理中...",
			msgUploadBegin: "初始化...",
			msgUploadEnd: "上传成功",
			msgUploadEmpty: "暂无可上传文件",
			msgValidationError: "验证错误",
			msgLoading: "加载第 {index} 文件 共 {files}…",
			msgProgress: "加载第 {index} 文件 共 {files}- {name} - {percent}% 完成",
			msgSelected: "{n} {files} 选中",
			msgFoldersNotAllowed: "只支持拖拽文件！跳过 {n} 拖拽的文件夹",
			msgImageWidthSmall: "文件 {name} 宽度不允许小于{size}像素",
			msgImageHeightSmall: "文件 {name} 高度不允许小于{size}像素",
			msgImageWidthLarge: "文件 {name} 宽度不允许大于{size}像素",
			msgImageHeightLarge: "文件 {name} 高度不允许大于{size}像素",
			msgImageResizeError: "无法调整图片大小",
			msgImageResizeException: "调整图片大小错误 {errors}",
			msgAjaxError: "{operation} 失败",
			msgAjaxProgressError: "{operation} 失败",
			ajaxOperations: {
				deleteThumb: "删除",
				uploadThumb: "上传",
				uploadBatch: "上传",
				uploadExtra: "上传"
			},
			dropZoneTitle: "拖拽文件到这里",
			dropZoneClickTitle: "(或点击{files}按钮选择文件)",
			fileActionSettings: {
				removeTitle: "删除",
				uploadTitle: "上传",
				zoomTitle: "查看",
				dragTitle: "移动 / 重置",
				indicatorNewTitle: "没有上传",
				indicatorSuccessTitle: "上传",
				indicatorErrorTitle: "上传错误",
				indicatorLoadingTitle: "上传..."
			},
			previewZoomButtonTitles: {
				prev: "上一个",
				next: "下一个",
				toggleheader: "缩放",
				fullscreen: "全屏",
				borderless: "无边界模式",
				close: "关闭"
			}
		};
	}
	
	// 文本编辑器
	if ($.summernote != null) {
		$.extend($.summernote.options, {
			lang: "zh_CN",
			dialogsInBody: true,
			dialogsFade: true,
			callbacks: {
				onImageUpload: function(files) {
					var $element = $(this);
					var $files = $(files);
					
					$files.each(function() {
						var file = this;
						var formData = new FormData();
						
						formData.append("fileType", "image");
						formData.append("file", file);
						$.ajax({
							url: "/business/file/upload",
							type: "POST",
							data: formData,
							dataType: "json",
							contentType: false,
							cache: false,
							processData: false,
							success: function(data) {
								$element.summernote("insertImage", data.url);
							}
						});
					});
				}
			}
		});
		
		$.extend($.summernote.lang, {
			"zh_CN": {
				font: {
					bold: "粗体",
					italic: "斜体",
					underline: "下划线",
					clear: "清除格式",
					height: "行高",
					name: "字体",
					strikethrough: "删除线",
					subscript: "下标",
					superscript: "上标",
					size: "字号"
				},
				image: {
					image: "图片",
					insert: "插入图片",
					resizeFull: "缩放至 100%",
					resizeHalf: "缩放至 50%",
					resizeQuarter: "缩放至 25%",
					floatLeft: "靠左浮动",
					floatRight: "靠右浮动",
					floatNone: "取消浮动",
					shapeRounded: "形状=圆角",
					shapeCircle: "形状=圆",
					shapeThumbnail: "形状=缩略图",
					shapeNone: "形状=无",
					dragImageHere: "将图片拖拽至此处",
					selectFromFiles: "从本地上传",
					maximumFileSize: "文件大小最大值",
					maximumFileSizeError: "文件大小超出最大值。",
					url: "图片地址",
					remove: "移除图片"
				},
				video: {
					video: "视频",
					videoLink: "视频链接",
					insert: "插入视频",
					url: "视频地址",
					providers: "(优酷 Instagram DailyMotion Youtube等)"
				},
				link: {
					link: "链接",
					insert: "插入链接",
					unlink: "去除链接",
					edit: "编辑链接",
					textToDisplay: "显示文本",
					url: "链接地址",
					openInNewWindow: "在新窗口打开"
				},
				table: {
					table: "表格"
				},
				hr: {
					insert: "水平线"
				},
				style: {
					style: "样式",
					p: "普通",
					blockquote: "引用",
					pre: "代码",
					h1: "标题 1",
					h2: "标题 2",
					h3: "标题 3",
					h4: "标题 4",
					h5: "标题 5",
					h6: "标题 6"
				},
				lists: {
					unordered: "无序列表",
					ordered: "有序列表"
				},
				options: {
					help: "帮助",
					fullscreen: "全屏",
					codeview: "源代码"
				},
				paragraph: {
					paragraph: "段落",
					outdent: "减少缩进",
					indent: "增加缩进",
					left: "左对齐",
					center: "居中对齐",
					right: "右对齐",
					justify: "两端对齐"
				},
				color: {
					recent: "最近使用",
					more: "更多",
					background: "背景",
					foreground: "前景",
					transparent: "透明",
					setTransparent: "透明",
					reset: "重置",
					resetToDefault: "默认"
				},
				shortcut: {
					shortcuts: "快捷键",
					close: "关闭",
					textFormatting: "文本格式",
					action: "动作",
					paragraphFormatting: "段落格式",
					documentStyle: "文档样式",
					extraKeys: "额外按键"
				},
				history: {
					undo: "撤销",
					redo: "重做"
				}
			}
		});
	}

})(jQuery);