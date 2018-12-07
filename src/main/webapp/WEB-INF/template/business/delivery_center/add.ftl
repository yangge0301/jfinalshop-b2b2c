<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	<meta name="author" content="JFinalShop Team">
	<meta name="copyright" content="JFinalShop">
	<title>${message("business.deliveryCenter.add")} - Powered By JFinalShop</title>
	<link href="${base}/favicon.ico" rel="icon">
	<link href="${base}/resources/business/css/bootstrap.css" rel="stylesheet">
	<link href="${base}/resources/business/css/font-awesome.css" rel="stylesheet">
	<link href="${base}/resources/business/css/bootstrap-checkbox-x.css" rel="stylesheet">
	<link href="${base}/resources/business/css/animate.css" rel="stylesheet">
	<link href="${base}/resources/business/css/adminLTE.css" rel="stylesheet">
	<link href="${base}/resources/business/css/common.css" rel="stylesheet">
	<!--[if lt IE 9]>
		<script src="${base}/resources/business/js/html5shiv.js"></script>
		<script src="${base}/resources/business/js/respond.js"></script>
	<![endif]-->
	<script src="${base}/resources/business/js/jquery.js"></script>
	<script src="${base}/resources/business/js/bootstrap.js"></script>
	<script src="${base}/resources/business/js/bootstrap-checkbox-x.js"></script>
	<script src="${base}/resources/business/js/jquery.validate.js"></script>
	<script src="${base}/resources/business/js/adminLTE.js"></script>
	<script src="${base}/resources/business/js/jquery.lSelect.js"></script>
	<script src="${base}/resources/business/js/common.js"></script>
	<script type="text/javascript">
	$().ready(function() {
	
		var $deliveryCenterForm = $("#deliveryCenterForm");
		var $areaId = $("input[name='areaId']");
		
		[#if flashMessage?has_content]
			$.alert("${flashMessage}");
		[/#if]
		
		// 地区选择
		$areaId.lSelect({
			url: "${base}/common/area"
		});
		
		// 表单验证
		$deliveryCenterForm.validate({
			rules: {
				"deliveryCenter.name": "required",
				"deliveryCenter.contact": "required",
				areaId: "required",
				"deliveryCenter.address": "required",
				"deliveryCenter.zip_code": {
					pattern: /^\d{6}$/
				},
				"deliveryCenter.phone": {
					pattern: /^\d{3,4}-?\d{7,9}$/
				}
			}
		});
	
	});
	</script>
</head>
<body class="hold-transition sidebar-mini">
	<div class="container-fluid">
		<section class="content-header">
			<h1>${message("business.deliveryCenter.add")}</h1>
			<ol class="breadcrumb">
				<li>
					<a href="${base}/business/index/main">
						<i class="fa fa-home"></i>
						${message("business.common.index")}
					</a>
				</li>
				<li class="active">${message("business.deliveryCenter.add")}</li>
			</ol>
		</section>
		<section class="content">
			<div class="row">
				<div class="col-xs-12">
					<form id="deliveryCenterForm" class="form-horizontal" action="${base}/business/delivery_center/save" method="post">
						<div class="box">
							<div class="box-body">
								<div class="form-group">
									<label class="col-xs-2 control-label item-required" for="name">${message("DeliveryCenter.name")}:</label>
									<div class="col-xs-4">
										<input id="name" name="deliveryCenter.name" class="form-control" type="text" maxlength="200">
									</div>
								</div>
								<div class="form-group">
									<label class="col-xs-2 control-label item-required" for="contact">${message("DeliveryCenter.contact")}:</label>
									<div class="col-xs-4">
										<input id="contact" name="deliveryCenter.contact" class="form-control" type="text" maxlength="200">
									</div>
								</div>
								<div class="form-group">
									<label class="col-xs-2 control-label item-required">${message("DeliveryCenter.area")}:</label>
									<div class="col-xs-4">
										<input name="areaId" type="hidden">
									</div>
								</div>
								<div class="form-group">
									<label class="col-xs-2 control-label item-required" for="address">${message("DeliveryCenter.address")}:</label>
									<div class="col-xs-4">
										<input id="address" name="deliveryCenter.address" class="form-control" type="text" maxlength="200">
									</div>
								</div>
								<div class="form-group">
									<label class="col-xs-2 control-label" for="zipCode">${message("DeliveryCenter.zipCode")}:</label>
									<div class="col-xs-4">
										<input id="zipCode" name="deliveryCenter.zip_code" class="form-control" type="text" maxlength="200">
									</div>
								</div>
								<div class="form-group">
									<label class="col-xs-2 control-label" for="phone">${message("DeliveryCenter.phone")}:</label>
									<div class="col-xs-4">
										<input id="phone" name="deliveryCenter.phone" class="form-control" type="text" maxlength="200">
									</div>
								</div>
								<div class="form-group">
									<label class="col-xs-2 control-label" for="mobile">${message("DeliveryCenter.mobile")}:</label>
									<div class="col-xs-4">
										<input id="mobile" name="deliveryCenter.mobile" class="form-control" type="text" maxlength="200">
									</div>
								</div>
								<div class="form-group">
									<label for="isDefault" class="col-xs-2 control-label">${message("DeliveryCenter.isDefault")}:</label>
									<div class="col-xs-10 checkbox">
										<input id="isDefault" name="deliveryCenter.is_default" type="text" value="false" data-toggle="checkbox-x">
									</div>
								</div>
								<div class="form-group">
									<label class="col-xs-2 control-label" for="memo">${message("DeliveryCenter.memo")}:</label>
									<div class="col-xs-4">
										<input id="memo" name="deliveryCenter.memo" class="form-control" type="text" maxlength="200">
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