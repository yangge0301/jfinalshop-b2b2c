<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("admin.adPosition.edit")} - Powered By JFinalShop</title>
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
	
	[@flash_message /]

	// 表单验证
	$inputForm.validate({
		rules: {
			"adPosition.name": "required",
			"adPosition.width": {
				required: true,
				integer: true,
				min: 1
			},
			"adPosition.height": {
				required: true,
				integer: true,
				min: 1
			},
			"adPosition.template": "required"
		}
	});

});
</script>
</head>
<body>
	<div class="breadcrumb">
		<a href="${base}/admin/common/index">${message("admin.breadcrumb.home")}</a> &raquo; ${message("admin.adPosition.edit")}
	</div>
	<form id="inputForm" action="update" method="post">
		<input type="hidden" name="adPosition.id" value="${adPosition.id}" />
		<table class="input">
			<tr>
				<th>
					<span class="requiredField">*</span>${message("AdPosition.name")}:
				</th>
				<td>
					<input type="text" name="adPosition.name" class="text" value="${adPosition.name}" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("AdPosition.width")}:
				</th>
				<td>
					<input type="text" name="adPosition.width" class="text" value="${adPosition.width}" maxlength="9" title="${message("admin.adPosition.widthTitle")}" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("AdPosition.height")}:
				</th>
				<td>
					<input type="text" name="adPosition.height" class="text" value="${adPosition.height}" maxlength="9" title="${message("admin.adPosition.heightTitle")}" />
				</td>
			</tr>
			<tr>
				<th>
					${message("AdPosition.description")}:
				</th>
				<td>
					<input type="text" name="adPosition.description" class="text" value="${adPosition.description}" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("AdPosition.template")}:
				</th>
				<td>
					<textarea name="adPosition.template" class="text" style="width: 98%; height: 300px;">${adPosition.template}</textarea>
				</td>
			</tr>
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