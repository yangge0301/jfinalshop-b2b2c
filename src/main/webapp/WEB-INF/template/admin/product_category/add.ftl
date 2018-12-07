<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("admin.productCategory.add")} - Powered By JFinalShop</title>
<meta name="author" content="JFinalShop Team" />
<meta name="copyright" content="JFinalShop" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/webuploader.js"></script>
<style type="text/css">
.brands label, .promotions label {
	width: 150px;
	display: block;
	float: left;
	padding-right: 6px;
}
</style>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
	var $filePicker = $("#filePicker");
	
	[@flash_message /]
	
	// 图片上传
	$filePicker.uploader();
	
	// 表单验证
	$inputForm.validate({
		rules: {
			"productCategory.name": "required",
			"productCategory.general_rate": {
				required: true,
				min: 0,
				decimal: {
					integer: 3,
					fraction: 3
				}
			},
			"productCategory.self_rate": {
				required: true,
				min: 0,
				decimal: {
					integer: 3,
					fraction: 3
				}
			},
			"productCategory.orders": "digits"
		}
	});

});
</script>
</head>
<body>
	<div class="breadcrumb">
		<a href="${base}/admin/common/index">${message("admin.breadcrumb.home")}</a> &raquo; ${message("admin.productCategory.add")}
	</div>
	<form id="inputForm" action="save" method="post">
		<table class="input">
			<tr>
				<th>
					<span class="requiredField">*</span>${message("ProductCategory.name")}:
				</th>
				<td>
					<input type="text" id="name" name="productCategory.name" class="text" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("ProductCategory.parent")}:
				</th>
				<td>
					<select name="parentId">
						<option value="">${message("admin.productCategory.root")}</option>
						[#list productCategoryTree as category]
							<option value="${category.id}">
								[#if category.grade != 0]
									[#list 1..category.grade as i]
										&nbsp;&nbsp;
									[/#list]
								[/#if]
								${category.name}
							</option>
						[/#list]
					</select>
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("ProductCategory.generalRate")}:
				</th>
				<td>
					<input type="text" id="generalRate" name="productCategory.general_rate" class="text" maxlength="200"/>
				</td>
			</tr>
			<tr>
				<th>
					<span class="requiredField">*</span>${message("ProductCategory.selfRate")}:
				</th>
				<td>
					<input type="text" id="selfRate" name="productCategory.self_rate" class="text" maxlength="200"/>
				</td>
			</tr>
			<tr class="brands">
				<th>
					${message("ProductCategory.brands")}:
				</th>
				<td>
					[#list brands as brand]
						<label>
							<input type="checkbox" name="brandIds" value="${brand.id}" />${brand.name}
						</label>
					[/#list]
				</td>
			</tr>
			<tr class="promotions">
				<th>
					${message("ProductCategory.promotions")}:
				</th>
				<td>
					[#list promotions as promotion]
						<label title="${promotion.title}">
							<input type="checkbox" name="promotionIds" value="${promotion.id}" />${promotion.name}
						</label>
					[/#list]
				</td>
			</tr>
			<tr>
                <th>
                    ${message("admin.common.setting")}:
                </th>
                <td>
                    <label>
                        <input type="checkbox" name="isMarketable" value="true" checked="checked" />是否上架
                        <input type="hidden" name="_isMarketable" value="false" />
                    </label>
                    <label>
                        <input type="checkbox" name="isTop" value="true" />是否置顶
                        <input type="hidden" name="_isTop" value="false" />
                    </label>
                    <label>
                        <input type="checkbox" name="isCash" value="true" />货到付款
                        <input type="hidden" name="_isCash" value="false" />
                    </label>
                </td>
            </tr>
            <tr>
                <th>
                                                    分类图片:
                </th>
                <td>
                    <span class="fieldSet">
                        <input type="text" name="productCategory.image" class="text" maxlength="200" />
                        <a href="javascript:;" id="filePicker" class="button">${message("admin.upload.filePicker")}</a>
                    </span>
                </td>
            </tr>
			<tr>
				<th>
					${message("ProductCategory.seoTitle")}:
				</th>
				<td>
					<input type="text" name="productCategory.seo_title" class="text" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("ProductCategory.seoKeywords")}:
				</th>
				<td>
					<input type="text" name="productCategory.seo_keywords" class="text" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("ProductCategory.seoDescription")}:
				</th>
				<td>
					<input type="text" name="productCategory.seo_description" class="text" maxlength="200" />
				</td>
			</tr>
			<tr>
				<th>
					${message("admin.common.order")}:
				</th>
				<td>
					<input type="text" name="productCategory.orders" class="text" maxlength="9" />
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