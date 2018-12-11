<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	<meta name="format-detection" content="telephone=no">
	<meta name="author" content="JFinalShop Team">
	<meta name="copyright" content="JFinalShop">
	<title>${message("member.order.list")}[#if showPowered] - Powered By JFinalShop[/#if]</title>
	<link href="${base}/favicon.ico" rel="icon">
	<link href="${base}/resources/mobile/member/css/bootstrap.css" rel="stylesheet">
	<link href="${base}/resources/mobile/member/css/font-awesome.css" rel="stylesheet">
	<link href="${base}/resources/mobile/member/css/animate.css" rel="stylesheet">
	<link href="${base}/resources/mobile/member/css/common.css" rel="stylesheet">
	<link href="${base}/resources/mobile/member/css/profile.css" rel="stylesheet">
	<!--[if lt IE 9]>
		<script src="${base}/resources/mobile/member/js/html5shiv.js"></script>
		<script src="${base}/resources/mobile/member/js/respond.js"></script>
	<![endif]-->
	<script src="${base}/resources/mobile/member/js/jquery.js"></script>
	<script src="${base}/resources/mobile/member/js/bootstrap.js"></script>
	<script src="${base}/resources/mobile/member/js/underscore.js"></script>
	<script src="${base}/resources/mobile/member/js/common.js"></script>
	<script id="orderTemplate" type="text/template">
		<%
			function statusText(status) {
				switch(status) {
					case "pendingPayment":
						return "${message("Order.Status.pendingPayment")}";
					case "pendingReview":
						return "${message("Order.Status.pendingReview")}";
					case "pendingShipment":
						return "${message("Order.Status.pendingShipment")}";
					case "shipped":
						return "${message("Order.Status.shipped")}";
					case "received":
						return "${message("Order.Status.received")}";
					case "completed":
						return "${message("Order.Status.completed")}";
					case "failed":
						return "${message("Order.Status.failed")}";
					case "canceled":
						return "${message("Order.Status.canceled")}";
					case "denied":
						return "${message("Order.Status.denied")}";
				}
			}
			
			function productType(type) {
				switch(type) {
					case "exchange":
						return "${message("Product.Type.exchange")}";
					case "gift":
						return "${message("Product.Type.gift")}";
				}
			}
		%>
		<%_.each(orders, function(order, i) {%>
			<div class="panel panel-flat">
				<div class="panel-heading">
					<span class="small" style="font-size:1.8rem"> <%-order.store.name%>&#62;</span>
					<%if (order.hasExpired) {%>
						<em class="pull-right gray-darker">${message("member.order.hasExpired")}</em>
					<%} else {%>
						<em class="pull-right orange">
							<%-statusText(order.status)%>
						</em>
					<%}%>
				</div>
				<div class="panel-body">
					<div class="list-group list-group-flat">
						<%_.each(order.orderItems, function(orderItem, i) {%>
							<div class="list-group-item">
								<div class="media">
									<div class="media-left media-middle">
										<a href="view?orderSn=<%-order.sn%>">
											<img src="<%-orderItem.thumbnail != null ? orderItem.thumbnail : "${setting.defaultThumbnailProductImage}"%>" alt="<%-orderItem.name%>">
										</a>
									</div>
									<div class="media-body media-middle">
										<h4 class="media-heading">
											<a href="view?orderSn=<%-order.sn%>"><%-orderItem.name%></a>
										</h4>
										<%if (orderItem.specifications.length > 0) {%>
											<span class="small gray-darker"><%-orderItem.specifications.join(", ")%></span>
										<%}%>
										<%if (order.type != "general") {%>
											<strong class="small red">[<%-productType(order.type)%>]</strong>
										<%}%>
									</div>
                                    <div class="media-body media-middle" style="text-align:right	">
                                        <h4 class="media-heading">
                                            <div>￥<%-orderItem.price%>元</div>
                                            <div style="color:#d4cdcd">X<%-orderItem.quantity%>件</div>
                                        </h4>
                                    </div>
								</div>
							</div>
						<%})%>
					</div>
				</div>
				<div style="text-align:right;padding-right: 10px;" >
					共<%-order.quantity%>件商品，实付款：<span style="font-size:1.6rem;font-weight:bold;">￥<%-order.price%></span>元
				</div>
				<div class="panel-footer text-right">
					[#if isKuaidi100Enabled]
						<%var orderShipping = !_.isEmpty(order.orderShippings) ? order.orderShippings[0] : null;%>
						<%if (orderShipping != null && orderShipping.deliveryCorp != null && orderShipping.trackingNo != null) {%>
							<button class="transit-step btn btn-sm btn-default" type="button" data-order-shipping-sn="<%-orderShipping.sn%>">${message("member.order.transitStep")}</button>
						<%}%>
					[/#if]
					<a class="btn btn-lg btn-default" href="view?orderSn=<%-order.sn%>">${message("member.order.view")}</a>

				</div>
			</div>
		<%})%>
	</script>
	<script id="transitStepTemplate" type="text/template">
		<%if (_.isEmpty(data.transitSteps)) {%>
			<p class="gray-darker">${message("member.common.noResult")}</p>
		<%} else {%>
			<div class="list-group list-group-flat">
				<%_.each(data.transitSteps, function(transitStep, i) {%>
					<div class="list-group-item">
						<p class="small gray-darker"><%-transitStep.time%></p>
						<p class="small"><%-transitStep.context%></p>
					</div>
				<%})%>
			</div>
		<%}%>
	</script>
	<script type="text/javascript">
	$().ready(function() {

        // var $receive = $(".receive");
		var $transitStepModal = $("#transitStepModal");
		var $transitStepModalBody = $("#transitStepModal div.modal-body");
		var $orderItems = $("#orderItems");
		var orderTemplate = _.template($("#orderTemplate").html());
		var transitStepTemplate = _.template($("#transitStepTemplate").html());
		var statuskey = getUrlParam('status')?getUrlParam('status'):'';
		if(statuskey==''||statuskey==null){//全部
			$('.headtab-item').eq(0).addClass('headtab-item-alive').siblings().removeClass('headtab-item-alive');
		}
        else if(statuskey=='shipped'){//待收货

            $('.headtab-item').eq(3).addClass('headtab-item-alive').siblings().removeClass('headtab-item-alive');
        }
        else if(statuskey=='pendingPayment'){//待付款

            $('.headtab-item').eq(1).addClass('headtab-item-alive').siblings().removeClass('headtab-item-alive');
        }
        else if(statuskey=='pendingShipment'){//代发货

            $('.headtab-item').eq(2).addClass('headtab-item-alive').siblings().removeClass('headtab-item-alive');
        }
        else{

            $('.headtab-item').eq(0).addClass('headtab-item-alive').siblings().removeClass('headtab-item-alive');
		}

		// 无限滚动加载
		$orderItems.infiniteScroll({
			url: function(pageNumber) {
				return "${base}/member/order/m_list?pageNumber=" + pageNumber + "&status=${status}" + "&hasExpired=${(hasExpired?string("true", "false"))!}";
			},
			pageSize: 10,
			template: function(pageNumber, data) {
				return orderTemplate({
					orders: data
				});
			}
		});
		
		// 物流动态
		$orderItems.on("click", "button.transit-step", function() {
			var $element = $(this);
			$.ajax({
				url: "${base}/member/order/transit_step",
				type: "GET",
				data: {
					orderShippingSn: $element.data("order-shipping-sn")
				},
				dataType: "json",
				beforeSend: function() {
					$transitStepModalBody.empty();
					$transitStepModal.modal();
				},
				success: function(data) {
					$transitStepModalBody.html(transitStepTemplate({
						data: data
					}));
				}
			});
			return false;
		});
	
	});

    // 订单收货
    [#--$receive.click(function() {--]
        [#--if (confirm("${message("member.order.receiveConfirm")}")) {--]
            [#--$.ajax({--]
                [#--url: "receive?orderSn=${order.sn}",--]
                [#--type: "POST",--]
                [#--dataType: "json",--]
                [#--cache: false,--]
                [#--success: function() {--]
                    [#--location.reload(true);--]
                [#--}--]
            [#--});--]
        [#--}--]
        [#--return false;--]
    [#--});--]
    function doReceive(sn){
        if (confirm("${message("member.order.receiveConfirm")}")) {
            $.ajax({
                url: "receive?orderSn=sn",
                type: "POST",
                dataType: "json",
                cache: false,
                success: function() {
                    location.reload(true);
                }
            });
        }
        return false;


	}
    function getUrlParam(name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
        var r = window.location.search.substr(1).match(reg);  //匹配目标参数
        if (r != null) return unescape(r[2]); return null; //返回参数值
    }
	</script>
</head>
<body class="profile" style="background:#eeeeee">
	<div id="transitStepModal" class="transit-step-modal modal fade" tabindex="-1">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button class="close" type="button" data-dismiss="modal">&times;</button>
					<h4 class="modal-title">${message("member.order.transitStep")}</h4>
				</div>
				<div class="modal-body"></div>
				<div class="modal-footer">
					<button class="btn btn-sm btn-default" type="button" data-dismiss="modal">${message("member.order.close")}</button>
				</div>
			</div>
		</div>
	</div>
	<header class="header-fixed">
		<a class="pull-left" href="${base}/member/index">
			<span class="glyphicon glyphicon-menu-left"></span>
		</a>
		${message("member.order.list")}
	</header>
    <!--顶部tab 开始-->
    <div  class="headtabs">
        <div class="headtab" >
            <a href="${base}/member/order/list" class="headtab-item headtab-item-alive">全部</a>
            <a  href="${base}/member/order/list?status=pendingPayment&hasExpired=false" class="headtab-item">待付款</a>
            <a  href="${base}/member/order/list?status=pendingShipment&hasExpired=false" class="headtab-item">待发货</a>
            <a  href="${base}/member/order/list?status=shipped" class="headtab-item">待收货</a>
        </div>
    </div>
    <!--顶部tab 结束-->
	<main style="width:96%;margin: 0 auto 40px;">
		<div class="container-fluid">
			<div id="orderItems" style="background:#eeeeee"></div>
		</div>
	</main>
</body>
</html>