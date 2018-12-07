<header class="main-header" id="header">
   <a href="/admin/index" class="logo">
     <span class="logo-mini">
			<img class="img-circle" src="${setting.logo!setting.defaultStoreLogo}" alt="${currentStore.name}" >
	</span>
     <span class="logo-lg"><b>${setting.siteName}</b></span>
   </a>
   <nav class="navbar navbar-static-top">
      <a href="#" class="sidebar-toggle" data-toggle="offcanvas" role="button">
       <span class="sr-only">Toggle navigation</span>
      </a>

     <div class="navbar-custom-menu">
       <ul class="nav navbar-nav">
         <li class="dropdown messages-menu">
           <a href="${base}/admin/message/list" target="iframe">
             <i class="fa fa-envelope-o"></i>
             <span class="label label-success">${unreadMessageCount}</span>
           </a>
         </li> 
          
         <li class="dropdown user user-menu">
           <a href="profile/edit" target="iframe">
             <img src="${base}/resources/adminlte/dist/img/user0-160x160.jpg" class="user-image" alt="User Image">
             <span class="hidden-xs">${message("admin.index.hello")}![@shiro.principal name="username" /]</span>
           </a>
         </li>
         
         <li>
			<a class="logout" href="${base}/business/logout">
				<span class="fa fa-sign-out"></span>
				${message("admin.index.logout")}
			</a>
		  </li>
       </ul>
     </div>
   </nav>
 </header>