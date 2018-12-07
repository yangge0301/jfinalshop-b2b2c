<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("admin.permission.edit")} - Powered By JFinalShop</title>
<meta name="author" content="JFinalShop Team" />
<meta name="copyright" content="JFinalShop" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.tools.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
	var $amount = $("#amount");
	var $isSpecial = $("#isSpecial");
	
	[@flash_message /]
	
	// 表单验证
	$inputForm.validate({
		rules: {
			"permission.name": "required",
			"permission.value": "required",
			"permission.url": "required",
			"permission.module": "required"
		}
	});

});
</script>
</head>
<body>
	<div class="breadcrumb">
		${message("admin.permission.edit")}
	</div>
	<form id="inputForm" action="update" method="post">
		<input type="hidden" name="permission.id" value="${permission.id}" />
		<table class="input">
			<tr>
				<th>
					<span class="requiredField">*</span>${message("Permission.name")}:
				</th>
				<td>
					<input type="text" name="permission.name" class="text" value="${permission.name}" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("Permission.value")}:
				</th>
				<td>
					<input type="text" name="permission.value" class="text" value="${permission.value}" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("Permission.url")}:
				</th>
				<td>
					<input type="text" name="permission.url" class="text" value="${permission.url}" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("Permission.module")}:
				</th>
				<td>
					<input type="text" name="permission.module" class="text" value="${permission.module}" maxlength="50" />
				</td>
			</tr>
			<tr>
				<th>
					${message("Permission.description")}:
				</th>
				<td>
					<input type="text" name="permission.description" class="text" value="${permission.description}" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("admin.common.setting")}:
				</th>
				<td>
					<label>
						<input type="checkbox" name="isEnabled" value="true"[#if permission.isEnabled] checked="checked"[/#if] />${message("Permission.isEnabled")}
						<input type="hidden" name="_isEnabled" value="false" />
					</label>
				</td>
			</tr>
			<tr>
				<th>
					&nbsp;
				</th>
				<td>
					<input type="submit" class="button" value="${message("admin.common.submit")}"[#if role.isSystem] disabled="disabled"[/#if] />
					<input type="button" class="button" value="${message("admin.common.back")}" onclick="location.href='list'" />
				</td>
			</tr>
		</table>
	</form>
</body>
</html>