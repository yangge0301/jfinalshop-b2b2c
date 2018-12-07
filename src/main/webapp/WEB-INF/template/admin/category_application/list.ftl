<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("admin.categoryApplication.list")} - Powered By JFinalShop</title>
<meta name="author" content="JFinalShop Team" />
<meta name="copyright" content="JFinalShop" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/list.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $reviewForm = $("#reviewForm");
	var $id = $("#id");
	var $isPassed = $("#isPassed");
	
	[@flash_message /]
	
	// 审核
	$.review = function(categoryApplicationId) {
		$.dialog({
			type: "warn",
			content: "${message("admin.categoryApplication.reviewConfirm")}",
			ok: "${message("admin.common.true")}",
			cancel: "${message("admin.common.false")}",
			onOk: function() {
				$id.val(categoryApplicationId);
				$isPassed.val("true");
				$reviewForm.submit();
				return false;
			},
			onCancel: function() {
				$id.val(categoryApplicationId);
				$isPassed.val("false");
				$reviewForm.submit();
				return false;
			}
		});
	};


});
</script>
</head>
<body>
	<form id="reviewForm" action="review" method="post">
		<input type="hidden" id="id" name="id"/>
		<input type="hidden" id="isPassed" name="isPassed" />
	</form>
	<div class="breadcrumb">
		<a href="${base}/admin/index">${message("admin.breadcrumb.home")}</a> &raquo; ${message("admin.categoryApplication.list")} <span>(${message("admin.page.total", page.totalRow)})</span>
	</div>
	<form id="listForm" action="list" method="get">
		<div class="bar">
			<div class="buttonGroup">
				<a href="javascript:;" id="refreshButton" class="iconButton">
					<span class="refreshIcon">&nbsp;</span>${message("admin.common.refresh")}
				</a>
				<div id="pageSizeMenu" class="dropdownMenu">
					<a href="javascript:;" class="button">
						${message("admin.page.pageSize")}<span class="arrow">&nbsp;</span>
					</a>
					<ul>
						<li[#if page.pageSize == 10] class="current"[/#if] val="10">10</li>
						<li[#if page.pageSize == 20] class="current"[/#if] val="20">20</li>
						<li[#if page.pageSize == 50] class="current"[/#if] val="50">50</li>
						<li[#if page.pageSize == 100] class="current"[/#if] val="100">100</li>
					</ul>
				</div>
			</div>
			<div id="searchPropertyMenu" class="dropdownMenu">
				<div class="search">
					<span class="arrow">&nbsp;</span>
					<input type="text" id="searchValue" name="pageable.searchValue" value="${pageable.searchValue}" maxlength="200" />
					<button type="submit">&nbsp;</button>
				</div>
				<ul>
					<li[#if pageable.searchProperty == "productCategory.name"] class="current"[/#if] val="productCategory.name">${message("CategoryApplication.productCategory")}</li>
					<li[#if pageable.searchProperty == "store.name"] class="current"[/#if] val="store.name">${message("CategoryApplication.store")}</li>
				</ul>
			</div>
		</div>
		<table id="listTable" class="list">
			<tr>
				<th>
					<a href="javascript:;" class="sort" name="productCategory.name">${message("CategoryApplication.productCategory")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="rate">${message("CategoryApplication.rate")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="store.name">${message("CategoryApplication.store")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="status">${message("CategoryApplication.status")}</a>
				</th>
				<th>
					<span>${message("admin.common.action")}</span>
				</th>
			</tr>
			[#list page.list as categoryApplication]
				<tr>
					<td>
						${categoryApplication.productCategory.name}
					</td>
					<td>
						${categoryApplication.rate}
					</td>
					<td>
						${categoryApplication.store.name}
					</td>
					<td>
						<span[#if categoryApplication.statusName == "pending" || categoryApplication.statusName == "failed"] class="red"[#elseif categoryApplication.statusName == "approved"] class="green"[/#if]>${message("CategoryApplication.Status." + categoryApplication.statusName)}</span>
					</td>
					<td>
						[#if categoryApplication.statusName == "pending"]
							<a href="javascript:$.review('${categoryApplication.id}');">[${message("admin.common.review")}]</a>
						[#else]
							<span title="${message("CategoryApplication.Status." + categoryApplication.statusName)}">[${message("admin.common.review")}]</span>
						[/#if]
					</td>
				</tr>
			[/#list]
		</table>
		[@pagination pageNumber = page.pageNumber totalPages = page.totalPage]
			[#include "/admin/include/pagination.ftl"]
		[/@pagination]
	</form>
</body>
</html>