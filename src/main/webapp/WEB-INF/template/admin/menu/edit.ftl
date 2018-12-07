<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>菜单编辑 - Powered By JFinalShop</title>
<meta name="author" content="JFinalShop Team" />
<meta name="copyright" content="JFinalShop" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
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
			"menu.name": "required",
			"menu.icon": "required",
			"menu.url": {
				pattern: /^(http:\/\/|https:\/\/|ftp:\/\/|mailto:|\/|#).*$/i
			},
			"menu.level_code": "required"
		}
	});

});
</script>
</head>
<body>
	<div class="breadcrumb">
		<a href="${base}/admin/common/index">${message("admin.breadcrumb.home")}</a> &raquo; 菜单编辑
	</div>
	<form id="inputForm" action="update" method="post">
		<input type="hidden" name="menu.id" value="${menu.id}" />
		<table class="input">
			<tr>
				<th>
					<span class="requiredField">*</span>名称:
				</th>
				<td>
					<input type="text" name="menu.name" class="text" value="${menu.name}" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					上级菜单:
				</th>
				<td>
					<select id="parentId" name="parentId">
						<option value="">顶级分类</option>
						[#list menuTree as p_menu]
							[#if p_menu != menu && !children?seq_contains(p_menu)]
								<option value="${p_menu.id}"[#if p_menu == menu.parent] selected="selected"[/#if]>
									[#if p_menu.grade != 0]
										[#list 1..p_menu.grade as i]
											&nbsp;&nbsp;
										[/#list]
									[/#if]
									${p_menu.name}
								</option>
							[/#if]
						[/#list]
					</select>
				</td>
			</tr>
			<tr>
				<th>
					类型:
				</th>
				<td>
					<select id="type" name="type">
						[#list types as type]
							<option value="${type}"[#if type == menu.typeName] selected="selected"[/#if]>${type}</option>
						[/#list]
					</select>
				</td>
			</tr>
			<tr>
				<th>
					编码:
				</th>
				<td>
					<input type="text" name="menu.level_code" class="text" value="${menu.level_code}" maxlength="200" disabled="disabled"/>
				</td>
			</tr>
			<tr>
				<th>
					URL:
				</th>
				<td>
					<input type="text" name="menu.url" class="text" value="${menu.url}" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					图标:
				</th>
				<td>
					<input type="text" name="menu.icon" class="text" maxlength="200" value="fa fa-circle-o" />
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