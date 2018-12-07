<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("admin.business.view")} - Powered By JFinalShop</title>
<meta name="author" content="JFinalShop Team" />
<meta name="copyright" content="JFinalShop" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/jquery.tools.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/input.js"></script>
</head>
<body>
	<div class="breadcrumb">
		<a href="${base}/admin/common/index">${message("admin.breadcrumb.home")}</a> &raquo; ${message("admin.business.view")}
	</div>
	<ul id="tab" class="tab">
		<li>
			<input type="button" value="${message("admin.business.base")}" />
		</li>
		[#if businessAttributes?has_content]
			<li>
				<input type="button" value="${message("admin.business.profile")}" />
			</li>
		[/#if]
	</ul>
	<table class="input tabContent">
		<tr>
			<th>
				${message("Business.username")}:
			</th>
			<td>
				${business.username}
				[#if loginPlugin??]
					<span class="silver">[${loginPlugin.name}]</span>
				[/#if]
			</td>
		</tr>
		<tr>
			<th>
				${message("Business.email")}:
			</th>
			<td>
				${business.email}
			</td>
		</tr>
		<tr>
			<th>
				${message("Business.mobile")}:
			</th>
			<td>
				${business.mobile}
			</td>
		</tr>
		<tr>
			<th>
				${message("Business.balance")}:
			</th>
			<td>
				${currency(business.balance, true)}
				<a href="../business_deposit/log?businessId=${business.id}">[${message("admin.common.view")}]</a>
			</td>
		</tr>
		[#if business.frozenFund > 0]
			<tr>
				<th>
					${message("Business.frozenFund")}:
				</th>
				<td>
					${currency(business.frozenFund, true)}
				</td>
			</tr>
		[/#if]
		<tr>
			<th>
				${message("admin.business.status")}:
			</th>
			<td>
				[#if !business.isEnabled]
					<span class="red">${message("admin.business.disabled")}</span>
				[#elseif business.isLocked]
					<span class="red"> ${message("admin.business.locked")} </span>
				[#else]
					<span class="green">${message("admin.business.normal")}</span>
				[/#if]
			</td>
		</tr>
		<tr>
			<th>
				${message("admin.common.createdDate")}:
			</th>
			<td>
				${business.createdDate?string("yyyy-MM-dd HH:mm:ss")}
			</td>
		</tr>
		<tr>
			<th>
				${message("User.lastLoginIp")}:
			</th>
			<td>
				${business.lastLoginIp!"-"}
			</td>
		</tr>
		<tr>
			<th>
				${message("User.lastLoginDate")}:
			</th>
			<td>
				${(business.lastLoginDate?string("yyyy-MM-dd HH:mm:ss"))!"-"}
			</td>
		</tr>
	</table>
	[#if businessAttributes?has_content]
		<table class="input tabContent">
			[#list businessAttributes as businessAttribute]
				<tr>
					<th>
						${businessAttribute.name}:
					</th>
					<td>
						[#if businessAttribute.typeName == "name"]
							${business.name}
						[#elseif businessAttribute.typeName == "text" || businessAttribute.typeName == "name" || businessAttribute.typeName == "licenseNumber" || businessAttribute.typeName == "legalPerson" || businessAttribute.typeName == "idCard" || businessAttribute.typeName == "phone" || businessAttribute.typeName == "organizationCode" || businessAttribute.typeName == "identificationNumber" || businessAttribute.typeName == "bankName" || businessAttribute.typeName == "bankAccount" || businessAttribute.typeName == "date"]
							${business.getAttributeValue(businessAttribute)}
						[#elseif businessAttribute.typeName == "image" || businessAttribute.typeName == "licenseImage" || businessAttribute.typeName == "idCardImage" || businessAttribute.typeName == "organizationImage" || businessAttribute.typeName == "taxImage"]
							<a href="${business.getAttributeValue(businessAttribute)}" target="_blank"><img src="${business.getAttributeValue(businessAttribute)}" width="56" height="56" /></a>
						[#elseif businessAttribute.typeName == "select"]
							${business.getAttributeValue(businessAttribute)}
						[#elseif businessAttribute.typeName == "checkbox"]
							[#list business.getAttributeValue(businessAttribute) as option]
								${option}
							[/#list]
						[/#if]
					</td>
				</tr>
			[/#list]
		</table>
	[/#if]
	<table class="input">
		<tr>
			<th>
				&nbsp;
			</th>
			<td>
				<input type="button" class="button" value="${message("admin.common.back")}" onclick="history.back(); return false;" />
			</td>
		</tr>
	</table>
</body>
</html>