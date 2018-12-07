[#escape x as x?html]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("admin.index.generate")} - Powered By JFinalShop</title>
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
	var $generateType = $("#generateType");
	var $count = $("#count");
	var $isPurge = $("#isPurge");
	var $status = $("#status");
	var $submit = $("input:submit");
	
	var first;
	var generateCount;
	var generateTime;
	var generateType;
	var count;
	var isPurge;
	
	// 表单验证
	$inputForm.validate({
		rules: {
			count: {
				required: true,
				integer: true,
				min: 1
			}
		},
		submitHandler: function(form) {
			first = 0;
			generateCount = 0;
			generateTime = 0;
			generateType = $generateType.val();
			count = parseInt($count.val());
			isPurge = $isPurge.prop("checked");
			$generateType.prop("disabled", true);
			$count.prop("disabled", true);
			$isPurge.prop("disabled", true);
			$submit.prop("disabled", true);
			$status.closest("tr").show();
			generate();
		}
	});
	
	function generate() {
		$.ajax({
			url: "generateSubmit.jhtml",
			type: "POST",
			data: {generateType: generateType, isPurge: isPurge, first: first, count: count},
			dataType: "json",
			cache: false,
			success: function(data) {
				generateCount += data.generateCount;
				generateTime += data.generateTime;
				if (!data.isCompleted) {
					first = data.first;
					$status.text("${message("admin.index.beingProcessed")} [" + first + " - " + (first + count) + "]");
					generate();
				} else {
					$generateType.prop("disabled", false);
					$count.prop("disabled", false);
					$isPurge.prop("disabled", false);
					$submit.prop("disabled", false);
					$status.closest("tr").hide();
					$status.empty();
					var time;
					if (generateTime < 60000) {
						time = (generateTime / 1000).toFixed(2) + "${message("admin.index.second")}";
					} else {
						time = (generateTime / 60000).toFixed(2) + "${message("admin.index.minute")}";
					}
					$.message("success", "${message("admin.index.success")} [${message("admin.index.generateCount")}: " + generateCount + " ${message("admin.index.generateTime")}: " + time + "]");
				}
			}
		});
	}

});
</script>
</head>
<body>
	<div class="breadcrumb">
		<a href="${base}/admin/common/index.jhtml">${message("admin.breadcrumb.home")}</a> &raquo; ${message("admin.index.generate")}
	</div>
	<form id="inputForm" action="generateSubmit.jhtml" method="post">
		<table class="input">
			<tr>
				<th>
					${message("admin.index.generateType")}:
				</th>
				<td>
					<select id="generateType" name="generateType">
						[#list generateTypes as generateType]
							<option value="${generateType}">${message("admin.index." + generateType)}</option>
						[/#list]
					</select>
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("admin.index.count")}:
				</th>
				<td>
					<input type="text" id="count" name="count" class="text" value="100" maxlength="9" />
				</td>
			</tr>
			<tr>
				<th>
					${message("admin.index.isPurge")}:
				</th>
				<td>
					<input type="checkbox" id="isPurge" name="isPurge" value="true" />
				</td>
			</tr>
			<tr class="hidden">
				<th>
					&nbsp;
				</th>
				<td>
					<span class="loadingBar">&nbsp;</span>
					<div id="status"></div>
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
[/#escape]