<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("member.login.title")}[#if showPowered] - Powered By JFinalShop[/#if]</title>
<meta name="author" content="JFinalShop Team" />
<meta name="copyright" content="JFinalShop" />
<link href="${base}/resources/member/css/animate.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/member/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/member/css/login.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/member/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/member/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/member/js/common.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $loginForm = $("#loginForm");
	var $username = $("#username");
	var $password = $("#password");
	var $captcha = $("#captcha");
	var $isRememberUsername = $("#isRememberUsername");
	var $submit = $("input:submit");
	
	// 记住用户名
	if (getCookie("memberUsername") != null) {
		$isRememberUsername.prop("checked", true);
		$username.val(getCookie("memberUsername"));
		$password.focus();
	} else {
		$isRememberUsername.prop("checked", false);
		$username.focus();
	}
	
	// 验证码图片
	$captcha.captchaImage();
	
	// 表单验证、记住用户名
	$loginForm.validate({
		rules: {
			username: "required",
			password: "required",
			captcha: "required"
		},
		submitHandler: function(form) {
			$.ajax({
				url: $loginForm.attr("action"),
				type: "POST",
				data: $loginForm.serialize(),
				dataType: "json",
				beforeSend: function() {
					$submit.prop("disabled", true);
				},
				success: function(data) {
					$submit.prop("disabled", false);
					if ($isRememberUsername.prop("checked")) {
						addCookie("memberUsername", $username.val(), {expires: 7 * 24 * 60 * 60});
					} else {
						removeCookie("memberUsername");
					}
					[#if redirectUrl?has_content]
						location.href = "${redirectUrl?js_string}";
					[#else]
						if (data.redirectUrl != null) {
							location.href = data.redirectUrl;
						}
					[/#if]
				},
				error: function(xhr, textStatus, errorThrown) {
					setTimeout(function() {
						$submit.prop("disabled", false);
					}, 3000);
					$captcha.captchaImage("refresh", true);
				}
			});
		}
	});

});
</script>
</head>
<body>
	[#include "/shop/include/header.ftl" /]
	<div class="container login">
		<div class="row">
			<div class="span6">
				[@ad_position id = 6]
					[#noautoesc]
						${adPosition.resolveTemplate()}
					[/#noautoesc]
				[/@ad_position]
			</div>
			<div class="span6">
				<div class="wrap">
					<div class="main">
						<div class="title">
							[#if socialUserId?has_content && uniqueId?has_content]
								<strong>${message("member.login.bind")}</strong>USER BIND
							[#else]
								<strong>${message("member.login.title")}</strong>USER LOGIN
							[/#if]
						</div>
						<form id="loginForm" action="${base}/member/login" method="post">
							<input name="socialUserId" type="hidden" value="${socialUserId}" />
							<input name="uniqueId" type="hidden" value="${uniqueId}" />
							<input name="redirectUrl" type="hidden" value="${redirectUrl}" />
							<table>
								<tr>
									<th>
										${message("member.login.username")}:
									</th>
									<td>
										<input type="text" id="username" name="username" class="text" maxlength="200" title="${message("member.login.usernameTitle")}" />
									</td>
								</tr>
								<tr>
									<th>
										${message("member.login.password")}:
									</th>
									<td>
										<input type="password" id="password" name="password" class="text" maxlength="200" autocomplete="off" />
									</td>
								</tr>
								[#if setting.captchaTypes?? && setting.captchaTypes?seq_contains("memberLogin")]
									<tr>
										<th>
											${message("common.captcha.name")}:
										</th>
										<td>
											<span class="fieldSet">
												<input type="text" id="captcha" name="captcha" class="text captcha" maxlength="4" autocomplete="off" />
											</span>
										</td>
									</tr>
								[/#if]
								<tr>
									<th>
										&nbsp;
									</th>
									<td>
										<label>
											<input type="checkbox" id="isRememberUsername" name="isRememberUsername" value="true" />${message("member.login.isRememberUsername")}
										</label>
										<label>
											&nbsp;&nbsp;<a href="${base}/password/forgot?type=member">${message("member.login.forgotPassword")}</a>
										</label>
									</td>
								</tr>
								<tr>
									<th>
										&nbsp;
									</th>
									<td>
										[#if socialUserId?has_content && uniqueId?has_content]
											<input type="submit" class="submit" value="${message("member.login.bind")}" />
										[#else]
											<input type="submit" class="submit" value="${message("member.login.submit")}" />
										[/#if]
									</td>
								</tr>
								[#if loginPlugins?has_content && !socialUserId?has_content && !uniqueId?has_content]
									<tr class="loginPlugin">
										<th>
											&nbsp;
										</th>
										<td>
											<ul>
												[#list loginPlugins as loginPlugin]
													<li>
														<a href="${base}/social_user_login?loginPluginId=${loginPlugin.id}"[#if loginPlugin.description??] title="${loginPlugin.description}"[/#if]>
															[#if loginPlugin.logo?has_content]
																<img src="${loginPlugin.logo}" alt="${loginPlugin.loginMethodName}" />
															[#else]
																${loginPlugin.loginMethodName}
															[/#if]
														</a>
													</li>
												[/#list]
											</ul>
										</td>
									</tr>
								[/#if]
								<tr class="register">
									<th>
										&nbsp;
									</th>
									<td>
										<dl>
											<dt>${message("member.login.noAccount")}</dt>
											<dd>
												${message("member.login.tips")}
												[#if socialUserId?has_content && uniqueId?has_content]
													<a href="${base}/member/register?socialUserId=${socialUserId}&uniqueId=${uniqueId}">${message("member.login.registerBind")}</a>
												[#else]
													<a href="${base}/member/register">${message("member.login.register")}</a>
												[/#if]
											</dd>
										</dl>
									</td>
								</tr>
							</table>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
	[#include "/shop/include/footer.ftl" /]
</body>
</html>