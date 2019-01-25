var jfinalshop = {
	base: "",
	locale: "zh_CN"
};

var setting = {
	priceScale: 2,
	priceRoundType: "roundHalfUp",
	currencySign: "￥",
	currencyUnit: "元",
	uploadMaxSize: 10,
	uploadImageExtension: "jpg,jpeg,bmp,gif,png",
	uploadMediaExtension: "swf,flv,mp3,wav,avi,rm,rmvb",
	uploadFileExtension: "zip,rar,7z,doc,docx,xls,xlsx,ppt,pptx"
};

var messages = {
	"shop.dialog.ok": "确 定",
	"shop.dialog.cancel": "取 消",
	"shop.dialog.deleteConfirm": "您确定要删除吗？",
	"shop.dialog.clearConfirm": "您确定要清空吗？"
};
document.title = "微江宁";
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
			price = '￥' + price;
		}
		if (showUnit) {
			price += '元';
		}
		return price;
	}
}

// 多语言
function message(code) {
	if (code != null) {
		var content = messages[code] != null ? messages[code] : code;
		if (arguments.length == 1) {
			return content;
		} else {
			if ($.isArray(arguments[1])) {
				$.each(arguments[1], function(i, n) {
					content = content.replace(new RegExp("\\{" + i + "\\}", "g"), n);
				});
				return content;
			} else {
				$.each(Array.prototype.slice.apply(arguments).slice(1), function(i, n) {
					content = content.replace(new RegExp("\\{" + i + "\\}", "g"), n);
				});
				return content;
			}
		}
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
		var $alert = $('<div class="growl animated fadeInDown alert' + (alertClass != null ? ' ' + alertClass : '') + ' alert-dismissible"><button class="close" type="button"><span>&times;<\/span><\/button>' + message + '<\/div>').appendTo("body");
		$alert.find("button.close").click(function() {
			$alert.remove();
		});
		setTimeout(function() {
			$alert.remove();
		}, 3000);
	}
	
	// 重定向登录页面
	$.redirectLogin = function(redirectUrl) {
		var loginUrl = "/member/login";
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
			return "/common/captcha/image?" + captchaIdParameterName + '=' + captchaId + '&timestamp=' + new Date().getTime() + '&fontsize=' + 30;;
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

(function($) {

	var zIndex = 100;
	
	// 对话框
	$.dialog = function(options) {
		var settings = {
			width: 320,
			height: "auto",
			modal: true,
			ok: '确 定',
			cancel: '取 消',
			onShow: null,
			onClose: null,
			onOk: null,
			onCancel: null
		};
		$.extend(settings, options);
		
		if (settings.content == null) {
			return false;
		}
		
		var $dialog = $('<div class="xxDialog"><\/div>');
		var $dialogTitle;
		var $dialogClose = $('<div class="dialogClose"><\/div>').appendTo($dialog);
		var $dialogContent;
		var $dialogBottom;
		var $dialogOk;
		var $dialogCancel;
		var $dialogOverlay;
		if (settings.title != null) {
			$dialogTitle = $('<div class="dialogTitle"><\/div>').appendTo($dialog);
		}
		if (settings.type != null) {
			$dialogContent = $('<div class="dialogContent dialog' + escapeHtml(settings.type) + 'Icon"><\/div>').appendTo($dialog);
		} else {
			$dialogContent = $('<div class="dialogContent"><\/div>').appendTo($dialog);
		}
		if (settings.ok != null || settings.cancel != null) {
			$dialogBottom = $('<div class="dialogBottom"><\/div>').appendTo($dialog);
		}
		if (settings.ok != null) {
			$dialogOk = $('<input type="button" class="button" value="' + escapeHtml(settings.ok) + '" \/>').appendTo($dialogBottom);
		}
		if (settings.cancel != null) {
			$dialogCancel = $('<input type="button" class="button" value="' + escapeHtml(settings.cancel) + '" \/>').appendTo($dialogBottom);
		}
		if (!window.XMLHttpRequest) {
			$dialog.append('<iframe class="dialogIframe"><\/iframe>');
		}
		$dialog.appendTo("body");
		if (settings.modal) {
			$dialogOverlay = $('<div class="dialogOverlay"><\/div>').insertAfter($dialog);
		}
		
		var dragStart = {};
		var dragging = false;
		if (settings.title != null) {
			$dialogTitle.text(settings.title);
		}
		$dialogContent.html(settings.content);
		$dialog.css({"width": settings.width, "height": settings.height, "margin-left": - parseInt(settings.width / 2), "z-index": zIndex ++});
		dialogShow();
		
		if ($dialogTitle != null) {
			$dialogTitle.mousedown(function(event) {
				$dialog.css({"z-index": zIndex ++});
				var offset = $dialog.offset();
				dragStart.pageX = event.pageX;
				dragStart.pageY = event.pageY;
				dragStart.left = offset.left;
				dragStart.top = offset.top;
				dragging = true;
				return false;
			}).mouseup(function() {
				dragging = false;
			});
			
			$(document).mousemove(function(event) {
				if (dragging) {
					$dialog.offset({"left": dragStart.left + event.pageX - dragStart.pageX, "top": dragStart.top + event.pageY - dragStart.pageY});
					return false;
				}
			}).mouseup(function() {
				dragging = false;
			});
		}
		
		if ($dialogClose != null) {
			$dialogClose.click(function() {
				dialogClose();
				return false;
			});
		}
		
		if ($dialogOk != null) {
			$dialogOk.click(function() {
				if (settings.onOk && typeof settings.onOk == "function") {
					if (settings.onOk($dialog) != false) {
						dialogClose();
					}
				} else {
					dialogClose();
				}
				return false;
			});
		}
		
		if ($dialogCancel != null) {
			$dialogCancel.click(function() {
				if (settings.onCancel && typeof settings.onCancel == "function") {
					if (settings.onCancel($dialog) != false) {
						dialogClose();
					}
				} else {
					dialogClose();
				}
				return false;
			});
		}
		
		function dialogShow() {
			if (settings.onShow && typeof settings.onShow == "function") {
				if (settings.onShow($dialog) != false) {
					$dialog.show();
					$dialogOverlay.show();
				}
			} else {
				$dialog.show();
				$dialogOverlay.show();
			}
		}
		
		function dialogClose() {
			if (settings.onClose && typeof settings.onClose == "function") {
				if (settings.onClose($dialog) != false) {
					$dialogOverlay.remove();
					$dialog.remove();
				}
			} else {
				$dialogOverlay.remove();
				$dialog.remove();
			}
		}
		return $dialog;
	}
})(jQuery);

$().ready(function() {

	var $window = $(window);
	var $goTop = $('<div class="goTop"><\/div>').appendTo("body");
	var $top = $('<a href="javascript:;">&nbsp;<\/a>').appendTo($goTop);
	var $addFavorite = $('<a href="javascript:;">&nbsp;<\/a>').appendTo($goTop);
	
	// 返回顶部
	$window.scroll(function() {
		if ($window.scrollTop() > 100) {
			$goTop.fadeIn();
		} else {
			$goTop.fadeOut();
		}
	});
	
	// 返回顶部
	$top.click(function() {
		$("body, html").animate({scrollTop: 0});
	});
	
	// 添加收藏
	$addFavorite.click(function() {
		var title = document.title;
		var url = document.url;
		if (document.all) {
			window.external.addFavorite(url, title);
		} else if (window.sidebar && window.sidebar.addPanel) {
			window.sidebar.addPanel(title, url, "");
		} else {
			alert("请使用 Ctrl+D 进行添加");
		}
	});
	
	// 购物车信息
	var cartInfo
	setInterval(function() {
		if (cartInfo == null || cartInfo.tag != getCookie("cartTag")) {
			$.ajax({
				url: "/cart/info",
				type: "GET",
				dataType: "json",
				success: function(data) {
					cartInfo = data;
					if (cartInfo.tag != null) {
						addCookie("cartTag", cartInfo.tag);
					} else {
						removeCookie("cartTag");
					}
					$window.trigger("cartInfoLoad", [cartInfo]);
				}
			});
		}
	}, 500);
	
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

// 表单验证
if ($.validator != null) {

	$.extend($.validator.messages, {
		required: '必填',
		email: 'E-mail格式错误',
		url: '网址格式错误',
		date: '日期格式错误',
		dateISO: '日期格式错误',
		pointcard: '信用卡格式错误',
		number: '只允许输入数字',
		digits: '只允许输入零或正整数',
		minlength: $.validator.format('长度不允许小于{0}'),
		maxlength: $.validator.format('长度不允许大于{0}'),
		rangelength: $.validator.format('长度必须在{0}-{1}之间'),
		min: $.validator.format('不允许小于{0}'),
		max: $.validator.format('不允许大于{0}'),
		range: $.validator.format('必须在{0}-{1}之间'),
		accept: '输入后缀错误',
		equalTo: '两次输入不一致',
		remote: '输入错误',
		integer: '只允许输入整数',
		positive: '只允许输入正数',
		negative: '只允许输入负数',
		decimal: '数值超出了允许范围',
		pattern: '格式错误',
		extension: '文件格式错误'
	});
	
	$.validator.setDefaults({
		errorClass: "fieldError",
		ignore: ".ignore",
		ignoreTitle: true,
		errorPlacement: function($error, $element) {
			var $fieldSet = $element.closest("span.fieldSet");
			if ($fieldSet.size() > 0) {
				$error.appendTo($fieldSet);
			} else {
				$error.insertAfter($element);
			}
		},
		submitHandler: function(form) {
			$(form).find("input:submit").prop("disabled", true);
			form.submit();
		}
	});

}