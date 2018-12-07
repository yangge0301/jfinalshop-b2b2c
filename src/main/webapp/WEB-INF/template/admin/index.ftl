<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <title>${message("admin.index.title")} - Powered By JFinalShop</title>
  <meta name="author" content="JFinalShop Team" />
  <meta name="copyright" content="JFinalShop" />
  <!-- Tell the browser to be responsive to screen width -->
  <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
  
  <link rel="shortcut icon" type="image/x-icon" href="${base}/resources/common/favicon.ico" media="screen" />
  <!-- Bootstrap 3.3.6 -->
  <link rel="stylesheet" href="${base}/resources/adminlte/bootstrap/css/bootstrap.min.css">
  <!-- Font Awesome --> 
  <link rel="stylesheet" href="${base}/resources/common/libs/font-awesome/css/font-awesome.min.css">

  <!-- AdminLTE Skins. Choose a skin from the css/skins
       folder instead of downloading all of them to reduce the load. -->

  <link rel="stylesheet" href="${base}/resources/common/css/base.css">
  <link rel="stylesheet" href="${base}/resources/business/css/common.css">
 
  <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
  <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
  <!--[if lt IE 9]>
  <script src="${base}/resources/common/libs/html5shiv/html5shiv.min.js"></script>
  <script src="${base}/resources/common/libs/respond/respond.min.js"></script>
  <![endif]-->
  <!-- Theme style -->
  <link rel="stylesheet" href="${base}/resources/adminlte/dist/css/AdminLTE.min.css">
  <link rel="stylesheet" href="${base}/resources/adminlte/dist/css/skins/_all-skins.css">
  <script>
    	// iframe高度自适应的方法
       function setIframeHeight(iframe) {
    		var iframe = document.getElementById("iframe");
           if (iframe) {
               var iframeWin = iframe.contentWindow || iframe.contentDocument.parentWindow;
               if (iframeWin.document.body) {
               	var header = document.getElementById("header");
				var h = header.offsetHeight;  //高度
                   iframe.height = document.body.clientHeight-h-5;
               }
           }
       };
   </script>
</head>
<body class="skin-blue sidebar-mini fixed">
<a name="main"></a>
<div class="wrapper">
  [#include "/admin/include/main_header.ftl" /]
  <aside class="main-sidebar" style="height: 900px">
    <section class="sidebar">
      <ul class="sidebar-menu">
      </ul>
    </section>
  </aside>

  <!-- Content Wrapper. Contains page content -->
  <div class="content-wrapper" id="mainDiv">
    <iframe src="/admin/index/main" frameborder="0" scrolling="auto" id="iframe" name="iframe"  onload="setIframeHeight(this)" height=100% width=100%></iframe>
  </div>
  <!-- /.content-wrapper -->

  <!-- Add the sidebar's background. This div must be placed
       immediately after the control sidebar -->
  <div class="control-sidebar-bg"></div> 
  
</div>
<!-- ./wrapper -->
<script type="text/javascript">
var base="${base}";//给外部js文件传递路径参数
</script>
<!-- jQuery 2.2.0 -->
<script src="${base}/resources/adminlte/plugins/jQuery/jQuery-2.2.0.min.js"></script>
<!--JSON2-->
<!-- <script src="${base}/resources/common/json/json2.js"></script> -->
<!-- Bootstrap 3.3.6 -->
<script src="${base}/resources/adminlte/bootstrap/js/bootstrap.min.js"></script>
<!-- FastClick -->
<script src="${base}/resources/adminlte/plugins/fastclick/fastclick.js"></script>
<!-- SlimScroll 1.3.0 -->
<script src="${base}/resources/adminlte/plugins/slimScroll/jquery.slimscroll.min.js"></script>
<!-- AdminLTE App -->
<script src="${base}/resources/adminlte/dist/js/app.min.js"></script>

<script type="text/javascript" src="${base}/resources/common/js/base.js"></script>

<!-- 加载菜单 -->
<script>
   ajaxPost(base + "/admin/menu/findAll", null, function(data) {
	  var $li, $menu_f_ul;
	  $.each(data,function(index,item) {
		  if (item.level_code.length == 6) {	
			  $li = $('<li class="treeview"></li>');
			  var $menu_f = $('<a href="#">\n'
							+ '<i class="'+item.icon+'"></i> <span>'
							+ item.name
							+ '</span>\n'
							+ ' <span class="pull-right-container">\n'
							+ '<i class="fa fa-angle-left pull-right"></i>\n'
							+ '</span></a>');
				$li.append($menu_f);
				$menu_f_ul = $('<ul class="treeview-menu"></ul>');
				$li.append($menu_f_ul);
				$("ul.sidebar-menu").append($li);
			} else if (item.level_code.length == 12) {
				$menu_s = $('<li><a href="${base}'+item.url+'" target="iframe"><i class="'+item.icon+'"></i>' + item.name + '</a></li>');
				$menu_f_ul.append($menu_s);
			}
		});
	});
</script>
</body>

</html>