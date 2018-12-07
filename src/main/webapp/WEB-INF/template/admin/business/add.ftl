<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("admin.business.add")} - Powered By JFinalShop</title>
<meta name="author" content="JFinalShop Team" />
<meta name="copyright" content="JFinalShop" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.tools.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.lSelect.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/webuploader.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<script type="text/javascript" src="${base}/resources/admin/datePicker/WdatePicker.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
	var $areaId = $("#areaId");
	var $filePicker = $("a.filePicker");
	
	[@flash_message /]
	
	// 文件上传
	$filePicker.uploader();
	
	$.validator.addMethod("notAllNumber",
		function(value, element) {
			return this.optional(element) || /^.*[^\d].*$/.test(value);
		},
		"${message("admin.business.notAllNumber")}"
	);
	
	// 表单验证
	$inputForm.validate({
		rules: {
			"business.username": {
				required: true,
				minlength: 4,
				pattern: /^[0-9a-zA-Z_\u4e00-\u9fa5]+$/,
				notAllNumber: true,
				remote: {
					url: "check_username",
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
			"business.email": {
				required: true,
				email: true,
				remote: {
					url: "check_email",
					cache: false
				}
			},
			"business.mobile": {
				pattern: /^1[3|4|5|7|8]\d{9}$/,
				remote: {
					url: "check_mobile",
					cache: false
				}
			},
			roleIds: "required"
			[#list businessAttributes as businessAttribute]
				[#if businessAttribute.isRequired || businessAttribute.pattern?has_content]
					,businessAttribute_${businessAttribute.id}: {
						[#if businessAttribute.isRequired]
							required: true
							[#if businessAttribute.pattern?has_content],[/#if]
						[/#if]
						[#if businessAttribute.pattern?has_content]
							pattern: /${businessAttribute.pattern}/
						[/#if]
					}
				[/#if]
			[/#list]
		},
		messages: {
			"business.username": {
				pattern: "${message("common.validate.illegal")}",
				remote: "${message("common.validate.exist")}"
			},
			"business.email": {
				remote: "${message("common.validate.exist")}"
			},
			"business.mobile": {
				remote: "${message("common.validate.exist")}"
			}
		}
	});

});
</script>
</head>
<body>
	<div class="breadcrumb">
		${message("admin.business.add")}
	</div>
	<form id="inputForm" action="save" method="post">
		<ul id="tab" class="tab">
			<li>
				<input type="button" value="${message("admin.business.base")}" />
			</li>
			[#if businessAttributes?has_content]
				<li>
					<input type="button" value="${message("admin.business.profile")}" />
				</li>
			[/#if]
		</ul>
		<table class="input tabContent">
			<tr>
				<th>
					<span class="requiredField">*</span>${message("Business.username")}:
				</th>
				<td>
					<input type="text" name="business.username" class="text" maxlength="20" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("Business.password")}:
				</th>
				<td>
					<input type="password" id="password" name="password" class="text" maxlength="20" autocomplete="off" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("admin.business.rePassword")}:
				</th>
				<td>
					<input type="password" name="rePassword" class="text" maxlength="20" autocomplete="off" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("Business.email")}:
				</th>
				<td>
					<input type="text" name="business.email" class="text" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("Business.mobile")}:
				</th>
				<td>
					<input type="text" name="business.mobile" class="text" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("admin.common.setting")}:
				</th>
				<td>
					<label>
						<input type="checkbox" name="isEnabled" value="true" checked="checked" />${message("User.isEnabled")}
						<input type="hidden" name="_isEnabled" value="false" />
					</label>
				</td>
			</tr>
			<tr class="roles">
				<th>
					<span class="requiredField">*</span>${message("Admin.roles")}:
				</th>
				<td>
					<span class="fieldSet">
						[#list roles as role]
							<label>
								<input type="checkbox" name="roleIds" value="${role.id}" />${role.name}
							</label>
						[/#list]
					</span>
				</td>
			</tr>
		</table>
		[#if businessAttributes?has_content]
			<table class="input tabContent">
				[#list businessAttributes as businessAttribute]
					<tr>
						<th>
							[#if businessAttribute.isRequired]<span class="requiredField">*</span>[/#if]${businessAttribute.name}:
						</th>
						<td>
							[#if businessAttribute.typeName == "text" || businessAttribute.typeName == "name" || businessAttribute.typeName == "licenseNumber" || businessAttribute.typeName == "legalPerson" || businessAttribute.typeName == "idCard" || businessAttribute.typeName == "phone" || businessAttribute.typeName == "organizationCode" || businessAttribute.typeName == "identificationNumber" || businessAttribute.typeName == "bankName" || businessAttribute.typeName == "bankAccount"]
								<input type="text" name="businessAttribute_${businessAttribute.id}" class="text" maxlength="200" />
							[#elseif businessAttribute.typeName == "image" || businessAttribute.typeName == "licenseImage" || businessAttribute.typeName == "idCardImage" || businessAttribute.typeName == "organizationImage" || businessAttribute.typeName == "taxImage"]
								<span class="fieldSet">
									<input type="text" name="businessAttribute_${businessAttribute.id}" class="text" maxlength="200"/>
									<a href="javascript:;" class="button filePicker">${message("admin.upload.filePicker")}</a>
								</span>
							[#elseif businessAttribute.typeName == "select"]
								<select name="businessAttribute_${businessAttribute.id}">
									<option value="">${message("admin.common.choose")}</option>
									[#list businessAttribute.options as option]
										<option value="${option}">
											${option}
										</option>
									[/#list]
								</select>
							[#elseif businessAttribute.typeName == "checkbox"]
								<span class="fieldSet">
									[#list businessAttribute.options as option]
										<label>
											<input type="checkbox" name="businessAttribute_${businessAttribute.id}" value="${option}" />${option}
										</label>
									[/#list]
								</span>
							[#elseif businessAttribute.typeName == "date"]
								<input type="text" name="businessAttribute_${businessAttribute.id}" class="text Wdate" onfocus="WdatePicker({dateFmt: 'yyyy-MM-dd HH:mm:ss'});" />
							[/#if]
						</td>
					</tr>
				[/#list]
			</table>
		[/#if]
		<table class="input">
			<tr>
				<th>
					&nbsp;
				</th>
				<td>
					<input type="submit" class="button" value="${message("admin.common.submit")}" />
					<input type="button" class="button" value="${message("admin.common.back")}" onclick="history.back(); return false;" />
				</td>
			</tr>
		</table>
	</form>
</body>
</html>