<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("admin.orderRefunds.view")} - Powered By JFinalShop</title>
<meta name="author" content="JFinalShop Team" />
<meta name="copyright" content="JFinalShop" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
<script type="text/javascript">
var refundsFlag = false; // 退款结果标记

$().ready(function() {
	var $loading = $("#loading");
	var $button = $("a[class=button]");

	[@flash_message /]
	

});
// 提交请求
function refund(baseUrl) {
    var url = baseUrl + "&_=" + new Date().getTime();
    var $loading = $("#loading");
    var $button = $("a[class=button]");
    $button.hide();
    $loading.text("退款结果确认中,请勿重复提交......");
    
    $.post(url, null, function(result) {
        if(result.code == 0) {
        	refundsFlag = true;
        	$("#memoId").text(result.data);
            $.message("success","退款成功");
        } else {
            $.message("error","退款失败：" + result.message);
            $button.show();
        }
        $loading.text("");
    });
    
}
</script>
</head>
<body>
	<div class="breadcrumb">
		<a href="${base}/admin/common/index">${message("admin.breadcrumb.home")}</a> &raquo; ${message("admin.orderRefunds.view")}
	</div>
	<table class="input">
		<tr>
			<th>
				${message("OrderRefunds.sn")}:
			</th>
			<td>
				${refunds.sn}
			</td>
			<th>
				${message("admin.common.createdDate")}:
			</th>
			<td>
				${refunds.createdDate?string("yyyy-MM-dd HH:mm:ss")}
			</td>
		</tr>
		<tr>
			<th>
				${message("OrderRefunds.method")}:
			</th>
			<td>
				${message("OrderRefunds.Method." + refunds.methodName)}
			</td>
			<th>
				${message("OrderRefunds.paymentMethod")}:
			</th>
			<td>
				${refunds.paymentMethod!"-"}
			</td>
		</tr>
		<tr>
			<th>
				${message("OrderRefunds.bank")}:
			</th>
			<td>
				${refunds.bank!"-"}
			</td>
			<th>
				${message("OrderRefunds.account")}:
			</th>
			<td>
				${refunds.account!"-"}
			</td>
		</tr>
		<tr>
			<th>
				${message("OrderRefunds.amount")}:
			</th>
			<td>
				${currency(refunds.amount, true)}
			</td>
			<th>
				${message("OrderRefunds.payee")}:
			</th>
			<td>
				${refunds.payee!"-"}
			</td>
		</tr>
		<tr>
			<th>
				${message("OrderRefunds.order")}:
			</th>
			<td>
				${refunds.order.sn}
			</td>
		</tr>
		<tr>
			<th>
				${message("OrderRefunds.memo")}:
			</th>
			<td id="memoId" colspan="3">
				${refunds.memo!"-"}
			</td>
		</tr>
		<tr>
			<th>
				&nbsp;
			</th>
			<td colspan="3">
				<input type="button" class="button" value="${message("admin.common.back")}" onclick="history.back(); return false;" />
				<a id="weixinPublicRefunds" class="button" href="javascript:refund('${base}/admin/weixin_pay/refunds?id=${refunds.id}&paymentPluginId=weixinPublicPaymentPlugin');">微信公众号退款</a>
				<span id="loading"></span>
			</td>
		</tr>
	</table>
</body>
</html>