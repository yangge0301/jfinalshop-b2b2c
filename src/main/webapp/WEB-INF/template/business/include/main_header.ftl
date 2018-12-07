<header class="main-header" id="header">
  <a href="/business/index" class="logo">
    <span class="logo-mini">
			<img class="img-circle" src="${currentStore.logo!setting.defaultStoreLogo}" alt="${currentStore.name}" >
	</span>
    <span class="logo-lg">${abbreviate(currentStore.name, 16, "...")}</span>
  </a>
  <nav class="navbar navbar-static-top">
    <a href="#" class="sidebar-toggle" data-toggle="offcanvas" role="button">
      <span class="sr-only">Toggle navigation</span>
    </a>
    <div class="navbar-custom-menu">
      <ul class="nav navbar-nav">
        <!-- Notifications: style can be found in dropdown.less -->
        <li class="dropdown notifications-menu">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown">
           <span class="fa fa-bell-o"></span>
            [@order_count storeId = currentStore.id status = "pendingPayment" hasExpired = false]
				[#assign pendingPaymentCount = count]
			[/@order_count]
			[@order_count storeId = currentStore.id status = "pendingReview" hasExpired = false]
				[#assign pendingReviewCount = count]
			[/@order_count]
			[@order_count storeId = currentStore.id status = "pendingShipment"]
				[#assign pendingShipmentCount = count]
			[/@order_count]
			[@order_count storeId = currentStore.id isPendingRefunds = true]
				[#assign isPendingRefundsCount = count]
			[/@order_count]
			<div class="label">
				[#if pendingPaymentCount + pendingReviewCount + pendingShipmentCount + isPendingRefundsCount > 0]
					<span class="fa fa-circle text-red"></span>
				[/#if]
			</div>
          </a>
          <ul class="dropdown-menu">
            <li class="header">${message("business.mainHeader.notifications")}</li>
            <li>
              <ul class="menu">
               <li>
					<a href="${base}/business/order/list?status=pendingPayment&hasExpired=false" target="iframe">
						<span class="fa fa-credit-card text-aqua"></span>
						${message("business.mainHeader.pendingPayment", pendingPaymentCount)}
					</a>
				</li>
				<li>
					<a href="${base}/business/order/list?status=pendingReview&hasExpired=false" target="iframe">
						<span class="fa fa-user-o text-red"></span>
						${message("business.mainHeader.pendingReview", pendingReviewCount)}
					</a>
				</li>
				<li>
					<a href="${base}/business/order/list?status=pendingShipment" target="iframe">
						<span class="fa fa-truck text-green"></span>
						${message("business.mainHeader.pendingShipment", pendingShipmentCount)}
					</a>
				</li>
				<li>
					<a href="${base}/business/order/list?isPendingRefunds=true" target="iframe">
						<span class="fa fa-rmb text-yellow"></span>
						${message("business.mainHeader.pendingRefunds", isPendingRefundsCount)}
					</a>
				</li>
              </ul>
            </li>
            <li class="footer">
				<a href="${base}/business/order/list" target="iframe">${message("business.mainHeader.viewOrders")}</a>
			</li>
          </ul>
        </li>
        <!-- User Account: style can be found in dropdown.less -->
        <li class="dropdown user user-menu">
          <a href="${base}/business/store/setting" target="iframe">
			<img class="img-circle" src="${currentStore.logo!setting.defaultStoreLogo}" alt="${currentStore.name}">
			<span class="hidden-xs">${currentUser.username}</span>
		  </a>
        </li>
        <li>
			<a class="logout" href="${base}/business/logout">
				<span class="fa fa-sign-out"></span>
				${message("business.mainHeader.logout")}
			</a>
		</li>
      </ul>
    </div>
  </nav>
</header>