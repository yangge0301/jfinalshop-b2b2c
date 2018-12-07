<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	<meta name="author" content="JFinalShop Team">
	<meta name="copyright" content="JFinalShop">
	<title>${message("business.areaFreightConfig.edit")} - Powered By JFinalShop</title>
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
	<script src="${base}/resources/business/js/jquery.lSelect.js"></script>
	<script src="${base}/resources/business/js/jquery.validate.js"></script>
	<script src="${base}/resources/business/js/adminLTE.js"></script>
	<script src="${base}/resources/business/js/common.js"></script>
	<script type="text/javascript">
	$().ready(function() {
	
		var $areaFreightConfigForm = $("#areaFreightConfigForm");
		var $areaId = $("input[name='areaId']");
		
		$areaId.lSelect({
			url: "${base}/common/area"
		});
		
		// 表单验证
		$areaFreightConfigForm.validate({
			rules: {
				areaId: {
					required: true,
					remote: {
						url: "${base}/business/area_freight_config/check_area?id=${areaFreightConfig.id}&shippingMethodId=${areaFreightConfig.shippingMethod.id}",
						cache: false
					}
				},
				"areaFreightConfig.first_weight": {
					required: true,
					digits: true
				},
				"areaFreightConfig.continue_weight": {
					required: true,
					min: 1,
					digits: true
				},
				"areaFreightConfig.first_price": {
					required: true,
					min: 0,
					decimal: {
						integer: 12,
						fraction: ${setting.priceScale}
					}
				},
				"areaFreightConfig.continue_price": {
					required: true,
					min: 0,
					decimal: {
						integer: 12,
						fraction: ${setting.priceScale}
					}
				}
			},
			messages: {
				areaId: {
					remote: "${message("business.areaFreightConfig.areaExists")}"
				}
			}
		});
	
	});
	</script>
</head>
<body class="hold-transition sidebar-mini">
	<section class="content-header">
		<h1>${message("business.areaFreightConfig.edit")}</h1>
		<ol class="breadcrumb">
			<li>
				<a href="${base}/business/index/main">
					<i class="fa fa-home"></i>
					${message("business.common.index")}
				</a>
			</li>
			<li class="active">${message("business.areaFreightConfig.edit")}</li>
		</ol>
	</section>
	<section class="content">
		<div class="row">
			<div class="col-xs-12">
				<form id="areaFreightConfigForm" class="form-horizontal" action="${base}/business/area_freight_config/update" method="post">
					<input name="areaFreightConfig.id" type="hidden" value="${areaFreightConfig.id}">
					<div class="box">
						<div class="box-body">
							<div class="form-group">
								<label class="col-xs-2 control-label">${message("FreightConfig.shippingMethod")}:</label>
								<div class="col-xs-4">
									<p class="form-control-static">${areaFreightConfig.shippingMethod.name}</p>
								</div>
							</div>
							<div class="form-group">
								<label class="col-xs-2 control-label item-required">${message("AreaFreightConfig.area")}:</label>
								<div class="col-xs-4">
									<input name="areaId" type="hidden" value="${areaFreightConfig.area.id}" treePath="${areaFreightConfig.area.treePath}">
								</div>
							</div>
							<div class="form-group">
								<label class="col-xs-2 control-label item-required" for="firstWeight">${message("FreightConfig.firstWeight")}:</label>
								<div class="col-xs-4">
									<input id="firstWeight" name="areaFreightConfig.first_weight" class="form-control" type="text" value="${areaFreightConfig.firstWeight}" maxlength="9">
								</div>
							</div>
							<div class="form-group">
								<label class="col-xs-2 control-label item-required" for="continueWeight">${message("FreightConfig.continueWeight")}:</label>
								<div class="col-xs-4">
									<input id="continueWeight" name="areaFreightConfig.continue_weight" class="form-control" type="text" value="${areaFreightConfig.continueWeight}" maxlength="9">
								</div>
							</div>
							<div class="form-group">
								<label class="col-xs-2 control-label item-required" for="firstPrice">${message("FreightConfig.firstPrice")}:</label>
								<div class="col-xs-4">
									<input id="firstPrice" name="areaFreightConfig.first_price" class="form-control" type="text" value="${areaFreightConfig.firstPrice}" maxlength="16">
								</div>
							</div>
							<div class="form-group">
								<label class="col-xs-2 control-label item-required" for="continuePrice">${message("FreightConfig.continuePrice")}:</label>
								<div class="col-xs-4">
									<input id="continuePrice" name="areaFreightConfig.continue_price" class="form-control" type="text" value="${areaFreightConfig.continuePrice}" maxlength="16">
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
</body>
</html>