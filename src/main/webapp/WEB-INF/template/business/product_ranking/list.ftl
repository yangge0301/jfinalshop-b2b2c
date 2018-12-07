<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	<meta name="author" content="JFinalShop Team">
	<meta name="copyright" content="JFinalShop">
	<title>${message("business.productRanking.list")} - Powered By JFinalShop</title>
	<link href="${base}/favicon.ico" rel="icon">
	<link href="${base}/resources/business/css/bootstrap.css" rel="stylesheet">
	<link href="${base}/resources/business/css/font-awesome.css" rel="stylesheet">
	<link href="${base}/resources/business/css/animate.css" rel="stylesheet">
	<link href="${base}/resources/business/css/bootstrap-datetimepicker.css" rel="stylesheet">
	<link href="${base}/resources/business/css/adminLTE.css" rel="stylesheet">
	<link href="${base}/resources/business/css/common.css" rel="stylesheet">
	<!--[if lt IE 9]>
		<script src="${base}/resources/business/js/html5shiv.js"></script>
		<script src="${base}/resources/business/js/respond.js"></script>
	<![endif]-->
	<script src="${base}/resources/business/js/jquery.js"></script>
	<script src="${base}/resources/business/js/bootstrap.js"></script>
	<script src="${base}/resources/business/js/velocity.js"></script>
	<script src="${base}/resources/business/js/velocity.ui.js"></script>
	<script src="${base}/resources/business/js/moment.js"></script>
	<script src="${base}/resources/business/js/bootstrap-datetimepicker.js"></script>
	<script src="${base}/resources/business/js/g2.js"></script>
	<script src="${base}/resources/business/js/adminLTE.js"></script>
	<script src="${base}/resources/business/js/common.js"></script>
	<script type="text/javascript">
		$().ready(function() {
			
			var $productRankingForm = $("#productRankingForm");
			var $rankingType = $("[name='rankingType']");
			var $size = $("[name='count']");
			var $export = $("#export");
			var $rankingTypeItem = $("[data-ranking-type]");
			var $sizeItem = $("[data-size]");
			
			// 排名类型
			$rankingTypeItem.click(function() {
				var $element = $(this);
				var rankingType = $element.data("ranking-type");
				
				$element.addClass("active").siblings().removeClass("active");
				$rankingType.val(rankingType);
				
				loadData();
			});
			
			// 数量
			$sizeItem.click(function() {
				var $element = $(this);
				var size = $element.data("count");
				
				$element.addClass("active").siblings().removeClass("active");
				$size.val(size);
				
				loadData();
			});
			
			// 图表
			var Frame = G2.Frame;
			var chart = new G2.Chart({
				id: "chart",
				height: 500,
				forceFit: true,
				plotCfg: {
					margin: [20, 50, 80, 150]
				}
			});
			
			chart.source([], {
				name: {
					type: "cat",
					alias: "${message("Product.name")}"
				},
				value: {}
			});
			
			chart.axis("name", {
				title: null
			});
			chart.coord("rect").transpose();
			chart.interval().position("name*value").color("#ffab66");
			chart.render();
			
			// 加载数据
			function loadData() {
				$.ajax({
					url: $productRankingForm.attr("action"),
					type: $productRankingForm.attr("method"),
					data: $productRankingForm.serialize(),
					dataType: "json",
					success: function(data) {
						chart.col("value", {
							alias: $rankingTypeItem.filter(".active").data("ranking-type-name")
						});
						var frame = new Frame(data);
						frame = Frame.sort(frame, "value");
						chart.changeData(frame);
					}
				});
			};
			
			loadData();
			
			// 导出
			$export.click(function() {
				setTimeout(function() {
					chart.downloadImage();
				}, 1000);
			});
		
		});
	</script>
</head>
<body class="hold-transition sidebar-mini">
	<div class="container-fluid">
		<section class="content-header">
			<h1>${message("business.productRanking.list")}</h1>
			<ol class="breadcrumb">
				<li>
					<a href="${base}/business/index/main">
						<i class="fa fa-home"></i>
						${message("business.common.index")}
					</a>
				</li>
				<li class="active">${message("business.productRanking.list")}</li>
			</ol>
		</section>
		<section class="content">
			<div class="row">
				<div class="col-xs-12">
					<form id="productRankingForm" action="${base}/business/product_ranking/data" method="get">
						<input name="rankingType" type="hidden" value="${rankingType}">
						<input name="count" type="hidden" value="${count}">
						<div class="box">
							<div class="box-header">
								<div class="btn-group">
									<button id="export" class="btn btn-default" type="button">
										<i class="fa fa-download"></i>
										${message("business.productRanking.export")}
									</button>
									<div class="btn-group">
										<button class="btn btn-default dropdown-toggle" type="button" data-toggle="dropdown">
											${message("business.productRanking.type")}
											<span class="caret"></span>
										</button>
										<ul class="dropdown-menu">
											[#list rankingTypes as value]
												<li[#if value == rankingType] class="active"[/#if] data-ranking-type="${value}" data-ranking-type-name="${message("Product.RankingType." + value)}">
													<a href="javascript:;">${message("Product.RankingType." + value)}</a>
												</li>
											[/#list]
										</ul>
									</div>
									<div class="btn-group">
										<button class="btn btn-default dropdown-toggle" type="button" data-toggle="dropdown">
											${message("business.productRanking.size")}
											<span class="caret"></span>
										</button>
										<ul class="dropdown-menu">
											<li[#if count == 10] class="active"[/#if] data-size="10">
												<a href="javascript:;">10</a>
											</li>
											<li[#if count == 20] class="active"[/#if] data-size="20">
												<a href="javascript:;">20</a>
											</li>
											<li[#if count == 30] class="active"[/#if] data-size="30">
												<a href="javascript:;">30</a>
											</li>
											<li[#if count == 50] class="active"[/#if] data-size="50">
												<a href="javascript:;">50</a>
											</li>
										</ul>
									</div>
								</div>
							</div>
							<div class="box-body">
								<div id="chart"></div>
							</div>
						</div>
					</form>
				</div>
			</div>
		</section>
	</div>
</body>
</html>