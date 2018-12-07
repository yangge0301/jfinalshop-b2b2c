<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	<meta name="author" content="JFinalShop Team">
	<meta name="copyright" content="JFinalShop">
	<title>${message("business.defaultFreightConfig.edit")} - Powered By JFinalShop</title>
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
	<script src="${base}/resources/business/js/jquery.validate.js"></script>
	<script src="${base}/resources/business/js/adminLTE.js"></script>
	<script src="${base}/resources/business/js/common.js"></script>
	<script type="text/javascript">
	$().ready(function() {

		var $shippingMethodForm = $("#shippingMethodForm");
		
		// 表单验证
		$shippingMethodForm.validate({
			rules: {
				"defaultFreightConfig.first_weight": {
					required: true,
					digits: true
				},
				"defaultFreightConfig.continue_weight": {
					required: true,
					integer: true,
					min: 1
				},
				"defaultFreightConfig.first_price": {
					required: true,
					min: 0,
					decimal: {
						integer: 12,
						fraction: ${setting.priceScale}
					}
				},
				"defaultFreightConfig.continue_price": {
					required: true,
					min: 0,
					decimal: {
						integer: 12,
						fraction: ${setting.priceScale}
					}
				}
			}
		});
	
	});
	</script>
</head>
<body class="hold-transition sidebar-mini">
	<div class="container-fluid">
		<section class="content-header">
			<h1>${message("business.defaultFreightConfig.edit")}</h1>
			<ol class="breadcrumb">
				<li>
					<a href="${base}/business/index/main">
						<i class="fa fa-home"></i>
						${message("business.common.index")}
					</a>
				</li>
				<li class="active">${message("business.defaultFreightConfig.edit")}</li>
			</ol>
		</section>
		<section class="content">
			<div class="row">
				<div class="col-xs-12">
					<form id="shippingMethodForm" class="form-horizontal" action="${base}/business/shipping_method/update" method="post">
						<input name="shippingMethodId" type="hidden" value="${shippingMethod.id}">
						<input name="defaultFreightConfig.id" type="hidden" value="${defaultFreightConfig.id}">
						<input name="defaultFreightConfigId" type="hidden" value="${defaultFreightConfig.id}">
						<div class="box">
							<div class="box-body">
								<div class="form-group">
									<label class="col-xs-2 control-label item-required" for="firstWeight">${message("FreightConfig.firstWeight")}:</label>
									<div class="col-xs-4">
										<input id="firstWeight" name="defaultFreightConfig.first_weight" class="form-control" type="text" value="${defaultFreightConfig.firstWeight}" maxlength="9" title="${message("FreightConfig.firstWeight")}">
									</div>
								</div>
								<div class="form-group">
									<label class="col-xs-2 control-label item-required" for="continueWeight">${message("FreightConfig.continueWeight")}:</label>
									<div class="col-xs-4">
										<input id="continueWeight" name="defaultFreightConfig.continue_weight" class="form-control" type="text" value="${defaultFreightConfig.continueWeight}" maxlength="9" title="${message("FreightConfig.continueWeight")}">
									</div>
								</div>
								<div class="form-group">
									<label class="col-xs-2 control-label item-required" for="firstPrice">${message("FreightConfig.firstPrice")}:</label>
									<div class="col-xs-4">
										<input id="firstPrice" name="defaultFreightConfig.first_price" class="form-control" type="text" value="${defaultFreightConfig.firstPrice}" maxlength="16">
									</div>
								</div>
								<div class="form-group">
									<label class="col-xs-2 control-label item-required" for="continuePrice">${message("FreightConfig.continuePrice")}:</label>
									<div class="col-xs-4">
										<input id="continuePrice" name="defaultFreightConfig.continue_price" class="form-control" type="text" value="${defaultFreightConfig.continuePrice}" maxlength="16">
									</div>
								</div>
							</div>
							<div class="box-footer">
								<div class="row">
									<div class="col-xs-4 col-xs-offset-2">
										<button class="btn btn-primary" type="submit">${message("business.common.submit")}</button>
										<button class="btn btn-default" type="button" data-toggle="back">${message("business.common.back")}</button>
									</div>
								</div>
							</div>
						</div>
					</form>
				</div>
			</div>
		</section>
	</div>
</body>
</html>