<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("member.register.title")}[#if showPowered] - Powered By JFinalShop[/#if]</title>
<meta name="author" content="JFinalShop Team" />
<meta name="copyright" content="JFinalShop" />
<link href="${base}/resources/member/css/animate.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/member/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/member/css/register.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/member/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/member/js/jquery.lSelect.js"></script>
<script type="text/javascript" src="${base}/resources/member/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/member/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/member/datePicker/WdatePicker.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $registerForm = $("#registerForm");
	var $areaId = $("#areaId");
	var $captcha = $("#captcha");
	var $submit = $("input:submit");
	
	// 地区选择
	$areaId.lSelect({
		url: "${base}/common/area"
	});
	
	// 验证码图片
	$captcha.captchaImage();
	
	$.validator.addMethod("notAllNumber",
		function(value, element) {
			return this.optional(element) || /^.*[^\d].*$/.test(value);
		},
		"${message("member.register.notAllNumber")}"
	);
	
	// 表单验证
	$registerForm.validate({
		rules: {
			"member.username": {
				required: true,
				minlength: 4,
				pattern: /^[0-9a-zA-Z_\u4e00-\u9fa5]+$/,
				notAllNumber: true,
				remote: {
					url: "${base}/member/register/check_username",
					cache: false
				}
			},
			password: {
				required: true,
				minlength: 4
			},
			rePassword: {
				required: true,
				equalTo: "#password"
			},
			"member.email": {
				required: true,
				email: true,
				remote: {
					url: "${base}/member/register/check_email",
					cache: false
				}
			},
			"member.mobile": {
				pattern: /^1[3|4|5|7|8]\d{9}$/,
				remote: {
					url: "${base}/member/register/check_mobile",
					cache: false
				}
			},
			captcha: "required"
			[@member_attribute_list]
				[#list memberAttributes as memberAttribute]
					[#if memberAttribute.isRequired || memberAttribute.pattern?has_content]
						,memberAttribute_${memberAttribute.id}: {
							[#if memberAttribute.isRequired]
								required: true
								[#if memberAttribute.pattern?has_content],[/#if]
							[/#if]
							[#if memberAttribute.pattern?has_content]
								pattern: /${memberAttribute.pattern}/
							[/#if]
						}
					[/#if]
				[/#list]
			[/@member_attribute_list]
		},
		messages: {
			"member.username": {
				pattern: "${message("member.register.usernameIllegal")}",
				remote: "${message("member.register.usernameExist")}"
			},
			"member.email": {
				remote: "${message("member.register.emailExist")}"
			},
			"member.mobile": {
				pattern: "${message("member.register.mobileIllegal")}",
				remote: "${message("member.register.mobileExist")}"
			}
		},
		submitHandler: function(form) {
			$.ajax({
				url: $registerForm.attr("action"),
				type: "POST",
				data: $registerForm.serialize(),
				dataType: "json",
				beforeSend: function() {
					$submit.prop("disabled", true);
				},
				success: function() {
					setTimeout(function() {
						$submit.prop("disabled", false);
						location.href = "${base}/";
					}, 3000);
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
	<div class="container register">
		<div class="row">
			<div class="span12">
				<div class="wrap">
					<div class="main clearfix">
						<div class="title">
							[#if socialUserId?has_content && uniqueId?has_content]
								<strong>${message("member.register.bind")}</strong>REGISTER BIND
							[#else]
								<strong>${message("member.register.title")}</strong>USER REGISTER
							[/#if]
						</div>
						<form id="registerForm" action="${base}/member/register/submit" method="post">
							<input name="socialUserId" type="hidden" value="${socialUserId}" />
							<input name="uniqueId" type="hidden" value="${uniqueId}" />
							<table>
								<tr>
									<th>
										<span class="requiredField">*</span>${message("member.register.username")}:
									</th>
									<td>
										<input type="text" name="member.username" class="text" maxlength="20" />
									</td>
								</tr>
								<tr>
									<th>
										<span class="requiredField">*</span>${message("member.register.password")}:
									</th>
									<td>
										<input type="password" id="password" name="member.password" class="text" maxlength="20" autocomplete="off" />
									</td>
								</tr>
								<tr>
									<th>
										<span class="requiredField">*</span>${message("member.register.rePassword")}:
									</th>
									<td>
										<input type="password" name="rePassword" class="text" maxlength="20" autocomplete="off" />
									</td>
								</tr>
								<tr>
									<th>
										<span class="requiredField">*</span>${message("member.register.email")}:
									</th>
									<td>
										<input type="text" name="member.email" class="text" maxlength="200" />
									</td>
								</tr>
								<tr>
									<th>
										${message("member.register.mobile")}:
									</th>
									<td>
										<input type="text" name="member.mobile" class="text" maxlength="200" />
									</td>
								</tr>
								[@member_attribute_list]
									[#list memberAttributes as memberAttribute]
										<tr>
											<th>
												[#if memberAttribute.isRequired]<span class="requiredField">*</span>[/#if]${memberAttribute.name}:
											</th>
											<td>
												[#if memberAttribute.typeName == "name"]
													<input type="text" name="memberAttribute_${memberAttribute.id}" class="text" maxlength="200" />
												[#elseif memberAttribute.typeName == "gender"]
													<span class="fieldSet">
														[#list genders as gender]
															<label>
																<input type="radio" name="memberAttribute_${memberAttribute.id}" value="${gender}" />${message("Member.Gender." + gender)}
															</label>
														[/#list]
													</span>
												[#elseif memberAttribute.typeName == "birth"]
													<input type="text" name="memberAttribute_${memberAttribute.id}" class="text" onfocus="WdatePicker();" />
												[#elseif memberAttribute.typeName == "area"]
													<span class="fieldSet">
														<input type="hidden" id="areaId" name="memberAttribute_${memberAttribute.id}" />
													</span>
												[#elseif memberAttribute.typeName == "address"]
													<input type="text" name="memberAttribute_${memberAttribute.id}" class="text" maxlength="200" />
												[#elseif memberAttribute.typeName == "zipCode"]
													<input type="text" name="memberAttribute_${memberAttribute.id}" class="text" maxlength="200" />
												[#elseif memberAttribute.typeName == "phone"]
													<input type="text" name="memberAttribute_${memberAttribute.id}" class="text" maxlength="200" />
												[#elseif memberAttribute.typeName == "text"]
													<input type="text" name="memberAttribute_${memberAttribute.id}" class="text" maxlength="200" />
												[#elseif memberAttribute.typeName == "select"]
													<select name="memberAttribute_${memberAttribute.id}">
														<option value="">${message("member.common.choose")}</option>
														[#list memberAttribute.options as option]
															<option value="${option}">
																${option}
															</option>
														[/#list]
													</select>
												[#elseif memberAttribute.type == "checkbox"]
													<span class="fieldSet">
														[#list memberAttribute.optionsConverter as option]
															<label>
																<input type="checkbox" name="memberAttribute_${memberAttribute.id}" value="${option}" />${option}
															</label>
														[/#list]
													</span>
												[/#if]
											</td>
										</tr>
									[/#list]
								[/@member_attribute_list]
								[#if setting.captchaTypes?? && setting.captchaTypes?seq_contains("memberRegister")]
									<tr>
										<th>
											<span class="requiredField">*</span>${message("common.captcha.name")}:
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
										<input type="submit" class="submit" value="${message("member.register.submit")}" />
									</td>
								</tr>
								<tr>
									<th>
										&nbsp;
									</th>
									<td>
										<a href="${base}/article/detail/1_1" target="_blank">${message("member.register.agreement")}</a>
									</td>
								</tr>
							</table>
							<div class="login">
								<dl>
									<dt>${message("member.register.hasAccount")}</dt>
									<dd>
										${message("member.register.tips")}
										<a href="${base}/member/login">${message("member.register.login")}</a>
									</dd>
								</dl>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
	[#include "/shop/include/footer.ftl" /]
</body>
</html>