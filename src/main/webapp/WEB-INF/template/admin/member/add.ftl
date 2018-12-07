<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("admin.member.add")} - Powered By JFinalShop</title>
<meta name="author" content="JFinalShop Team" />
<meta name="copyright" content="JFinalShop" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.tools.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.lSelect.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<script type="text/javascript" src="${base}/resources/admin/datePicker/WdatePicker.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
	var $areaId = $("#areaId");
	
	[@flash_message /]
	
	// 地区选择
	$areaId.lSelect({
		url: "${base}/common/area"
	});
	
	$.validator.addMethod("notAllNumber",
		function(value, element) {
			return this.optional(element) || /^.*[^\d].*$/.test(value);
		},
		"${message("admin.member.notAllNumber")}"
	);
	
	// 表单验证
	$inputForm.validate({
		rules: {
			"member.username": {
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
			"member.email": {
				required: true,
				email: true,
				remote: {
					url: "check_email",
					cache: false
				}
			},
			"member.mobile": {
				pattern: /^1[3|4|5|7|8]\d{9}$/,
				remote: {
					url: "check_mobile",
					cache: false
				}
			}
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
		},
		messages: {
			"member.username": {
				pattern: "${message("common.validate.illegal")}",
				remote: "${message("common.validate.exist")}"
			},
			"member.email": {
				remote: "${message("common.validate.exist")}"
			},
			"member.mobile": {
				remote: "${message("common.validate.exist")}"
			}
		}
	});

});
</script>
</head>
<body>
	<div class="breadcrumb">
		<a href="${base}/admin/common/index">${message("admin.breadcrumb.home")}</a> &raquo; ${message("admin.member.add")}
	</div>
	<form id="inputForm" action="save" method="post">
		<ul id="tab" class="tab">
			<li>
				<input type="button" value="${message("admin.member.base")}" />
			</li>
			[#if memberAttributes?has_content]
				<li>
					<input type="button" value="${message("admin.member.profile")}" />
				</li>
			[/#if]
		</ul>
		<table class="input tabContent">
			<tr>
				<th>
					<span class="requiredField">*</span>${message("Member.username")}:
				</th>
				<td>
					<input type="text" name="member.username" class="text" maxlength="20" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("Member.password")}:
				</th>
				<td>
					<input type="password" id="password" name="password" class="text" maxlength="20" autocomplete="off" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("admin.member.rePassword")}:
				</th>
				<td>
					<input type="password" name="rePassword" class="text" maxlength="20" autocomplete="off" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("Member.email")}:
				</th>
				<td>
					<input type="text" name="member.email" class="text" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("Member.mobile")}:
				</th>
				<td>
					<input type="text" name="member.mobile" class="text" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("Member.memberRank")}:
				</th>
				<td>
					<select name="memberRankId">
						[#list memberRanks as memberRank]
							<option value="${memberRank.id}"[#if memberRank.isDefault] selected="selected"[/#if]>${memberRank.name}</option>
						[/#list]
					</select>
				</td>
			</tr>
			<tr>
				<th>
					所属店铺:
				</th>
				<td>
					<select name="storeId">
						<option value="" selected="selected" >---请选择---</option>
						[#list stores as store]
							<option value="${store.id}">${store.name}</option>
						[/#list]
					</select>
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
		</table>
		[#if memberAttributes?has_content]
			<table class="input tabContent">
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
								<input type="text" name="memberAttribute_${memberAttribute.id}" class="text Wdate" onfocus="WdatePicker();" />
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
									<option value="">${message("admin.common.choose")}</option>
									[#list memberAttribute.options as option]
										<option value="${option}">
											${option}
										</option>
									[/#list]
								</select>
							[#elseif memberAttribute.typeName == "checkbox"]
								<span class="fieldSet">
									[#list memberAttribute.options as option]
										<label>
											<input type="checkbox" name="memberAttribute_${memberAttribute.id}" value="${option}" />${option}
										</label>
									[/#list]
								</span>
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