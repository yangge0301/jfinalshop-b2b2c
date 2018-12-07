<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	<meta name="author" content="JFinalShop Team">
	<meta name="copyright" content="JFinalShop">
	<title>${message("business.businessDeposit.log")} - Powered By JFinalShop</title>
	<link href="${base}/favicon.ico" rel="icon">
	<link href="${base}/resources/business/css/bootstrap.css" rel="stylesheet">
	<link href="${base}/resources/business/css/font-awesome.css" rel="stylesheet">
	<link href="${base}/resources/business/css/animate.css" rel="stylesheet">
	<link href="${base}/resources/business/css/adminLTE.css" rel="stylesheet">
	<link href="${base}/resources/business/css/common.css" rel="stylesheet">
	<!--[if lt IE 9]>
		<script src="${base}/resources/business/js/html5shiv.js"></script>
		<script src="${base}/resources/business/js/respond.js"></script>
	<![endif]-->
	<script src="${base}/resources/business/js/jquery.js"></script>
	<script src="${base}/resources/business/js/bootstrap.js"></script>
	<script src="${base}/resources/business/js/adminLTE.js"></script>
	<script src="${base}/resources/business/js/common.js"></script>
	<script type="text/javascript">
	$().ready(function() {
		
		[#if flashMessage?has_content]
			$.alert("${flashMessage}");
		[/#if]
		
	});
	</script>
</head>
<body class="hold-transition sidebar-mini">
	<div class="container-fluid">
		<section class="content-header">
			<h1>${message("business.businessDeposit.log")}</h1>
			<ol class="breadcrumb">
				<li>
					<a href="${base}/business/index/main">
						<i class="fa fa-home"></i>
						${message("business.common.index")}
					</a>
				</li>
				<li class="active">${message("business.businessDeposit.log")}</li>
			</ol>
		</section>
		<section class="content">
			<div class="row">
				<div class="col-xs-12">
					<form action="${base}/business/deposit/log" method="get">
						<input name="pageSize" type="hidden" value="${pageable.pageSize}">
						<input name="pageNumber" type="hidden" value="${pageable.pageNumber}">
						<input name="searchProperty" type="hidden" value="${pageable.searchProperty}">
						<input name="orderProperty" type="hidden" value="${pageable.orderProperty}">
						<input name="pageable.orderDirection" type="hidden" value="${pageable.orderDirection}">
						<div class="box">
							<div class="box-header">
								<div class="row">
									<div class="col-xs-9">
										<div class="btn-group">
											<button class="btn btn-default" type="button" data-toggle="refresh">
												<i class="fa fa-refresh"></i>
												${message("business.common.refresh")}
											</button>
											<div class="btn-group">
												<button class="btn btn-default dropdown-toggle" type="button" data-toggle="dropdown">
													${message("business.common.pageSize")}
													<span class="caret"></span>
												</button>
												<ul class="dropdown-menu">
													<li[#if pageable.pageSize == 10] class="active"[/#if] data-page-size="10">
														<a href="javascript:;">10</a>
													</li>
													<li[#if pageable.pageSize == 20] class="active"[/#if] data-page-size="20">
														<a href="javascript:;">20</a>
													</li>
													<li[#if pageable.pageSize == 50] class="active"[/#if] data-page-size="50">
														<a href="javascript:;">50</a>
													</li>
													<li[#if pageable.pageSize == 100] class="active"[/#if] data-page-size="100">
														<a href="javascript:;">100</a>
													</li>
												</ul>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class="box-body table-responsive no-padding">
								<table class="table table-hover">
									<thead>
										<tr>
											<th>${message("BusinessDepositLog.type")}</th>
											<th>${message("BusinessDepositLog.credit")}</th>
											<th>${message("BusinessDepositLog.debit")}</th>
											<th>${message("BusinessDepositLog.balance")}</th>
											<th>${message("business.common.createdDate")}</th>
										</tr>
									</thead>
									<tbody>
										[#list page.list as businessDepositLog]
											<tr>
												<td>${message("BusinessDepositLog.Type." + businessDepositLog.typeName)}</td>
												<td>${currency(businessDepositLog.credit)}</td>
												<td>${currency(businessDepositLog.debit)}</td>
												<td>${currency(businessDepositLog.balance)}</td>
												<td>
													<span title="${businessDepositLog.createdDate?string("yyyy-MM-dd HH:mm:ss")}">${businessDepositLog.createdDate}</span>
												</td>
											</tr>
										[/#list]
									</tbody>
								</table>
								[#if !page.list?has_content]
									<p class="no-result">${message("business.common.noResult")}</p>
								[/#if]
							</div>
							[@pagination pageNumber = page.pageNumber totalPages = page.totalPage]
								[#if totalPages > 1]
									<div class="box-footer clearfix">
										[#include "/business/include/pagination.ftl"]
									</div>
								[/#if]
							[/@pagination]
						</div>
					</form>
				</div>
			</div>
		</section>
	</div>
</body>
</html>