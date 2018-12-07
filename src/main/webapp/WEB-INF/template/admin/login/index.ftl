<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>${message("admin.login.title")} - Powered By JFinalShop</title>
	<meta name="author" content="JFinalShop Team" />
	<meta name="copyright" content="JFinalShop" />
    <!-- Tell the browser to be responsive to screen width -->
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <!--icon-->
    <link rel="shortcut icon" type="image/x-icon" href="${base}/resources/common/favicon.ico" media="screen"/>
    <!-- Bootstrap 3.3.6 -->
    <link rel="stylesheet" href="${base}/resources/adminlte/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${base}/resources/common/libs/font-awesome/css/font-awesome.min.css">
    <!-- Ionicons -->
    <link rel="stylesheet" href="${base}/resources/common/libs/ionicons/css/ionicons.min.css">
    <!-- Theme style -->
    <link rel="stylesheet" href="${base}/resources/adminlte/dist/css/AdminLTE.min.css">
    <!-- iCheck -->
    <link rel="stylesheet" href="${base}/resources/adminlte/plugins/iCheck/square/red.css">
    <link rel="stylesheet" href="${base}/resources/adminlte/plugins/bootstrap-validator/dist/css/bootstrap-validator.css"/>
    
    <!-- jQuery 2.2.3 -->
    <script src="${base}/resources/adminlte/plugins/jQuery/jquery-2.2.3.min.js"></script>
    <!-- Bootstrap 3.3.6 -->
    <script src="${base}/resources/adminlte/bootstrap/js/bootstrap.min.js"></script>
    <!-- iCheck -->
    <script src="${base}/resources/adminlte/plugins/iCheck/icheck.min.js"></script>
    <!-- bootstrap-validator-->
    <script src="${base}/resources/adminlte/plugins/bootstrap-validator/dist/js/bootstrap-validator.js"></script>
    <!--login.js-->
    <script src="${base}/resources/common/js/login.js"></script>
    
    <script src="${base}/resources/admin/js/common.js"></script>

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="${base}/resources/common/libs/html5shiv/html5shiv.min.js"></script>
    <script src="${base}/resources/common/libs/respond/respond.min.js"></script>
    <![endif]-->
    <script type="text/javascript">
	$().ready( function() {
		var $captcha = $("#captcha");
		
		// 验证码图片
		$captcha.captchaImage();
		
	});
	</script>
	<script type="text/javascript">
	//防止页面嵌套在Iframe中
	if (window != top){
		top.location.href = location.href; 
	}
</script>
</head>
<body class="hold-transition login-page">
<div class="login-box">
    <div class="login-logo">
        <a href="#"><b>管理中心</b></a>
    </div>
    <!-- /.login-logo -->
    <div class="login-box-body">
        <p class="login-box-msg">欢迎登录管理中心</p>

        <form action="login" method="post" id="login-form">
            <div class="form-group has-feedback">
                <input type="text" class="form-control"  name="username" placeholder="请输入登录名">
                <span class="glyphicon glyphicon-envelope form-control-feedback"></span>
            </div>
            <div class="form-group has-feedback">
                <input type="password" class="form-control" name="password" placeholder="请输入密码">
                <span class="glyphicon glyphicon-lock form-control-feedback"></span>
            </div>
            <div class="form-group has-feedback">
            	<div class="input-group">
                	<input type="text" class="form-control"  id="captcha" name="captcha" placeholder="请输入验证码" style="width: 60%">
                </div>
            </div>
            <div class="row">
                <div class="col-xs-6">
                    <div class="checkbox icheck">
                        <label>
                            <input type="checkbox" name="rememberMe"> 记住我
                        </label>
                    </div>
                </div>
                <!-- /.col -->
                <div class="col-xs-6">
                    <div class="checkbox pull-right">
                       
                    </div>
                </div>
                <!-- /.col -->
            </div>
            <div class="row">
                <div class="col-xs-12">
                    <button type="submit" class="btn btn-danger btn-block btn-flat">登 录</button>
                </div>
            </div>
        </form>


       <!--  <div class="social-auth-links" style="margin-bottom: 0px;">
            <div class="row">
                <div class="col-xs-5">
                    <div class="text-left" style="margin-top: 5px;">快速登录</div>
                </div>
                <div class="col-xs-7">
                    <div class="text-right">
                        <a class="btn btn-social-icon btn-primary"><i class="fa fa-qq"></i></a>
                        <a class="btn btn-social-icon btn-success"><i class="fa fa-wechat"></i></a>
                        <a class="btn btn-social-icon btn-warning"><i class="fa fa-weibo"></i></a>
                        <a class="btn btn-social-icon btn-info"><i class="fa fa-github"></i></a>
                    </div>
                </div>
            </div>
        </div> -->
    </div>
</div>
    <script>
        $(function () {
            $('input').iCheck({
                checkboxClass: 'icheckbox_square-red',
                radioClass: 'iradio_square-red',
                increaseArea: '20%' // optional
            });

            fillbackLoginForm();
            $("#login-form").bootstrapValidator({
                message:'请输入用户名/密码',
                submitHandler:function (valiadtor,loginForm,submitButton) {
                    rememberMe($("input[name='rememberMe']").is(":checked"));
                    valiadtor.defaultSubmit();
                },
                fields:{
                	username:{
                        validators:{
                            notEmpty:{
                                message:'登录用户名不能为空'
                            }
                        }
                    },
                    password:{
                        validators:{
                            notEmpty:{
                                message:'密码不能为空'
                            }
                        }
                    },
                    captcha:{
                        validators:{
                            notEmpty:{
                                message:'验证码不能为空'
                            }
                       }
                    }
                }
            });

            [#if result??]
	            new LoginValidator({
	                code:"${result.code?default('-1')}",
	                message:"${result.message?default('')}",
	                username:'useruame',
	                password:'password',
	                captcha:'captcha'
	            });
	        [/#if]
        });

        //使用本地缓存记住用户名密码
        function rememberMe(rm_flag){
            if(rm_flag){
                 localStorage.username = $("input[name='username']").val();
                 localStorage.password = $("input[name='password']").val();
                localStorage.rememberMe = 1;
            } else{
                localStorage.username = null;
                localStorage.password = null;
                localStorage.rememberMe = 0;
            }
        }

        //记住回填
        function fillbackLoginForm(){
            if(localStorage.rememberMe && localStorage.rememberMe == 1){
                $("input[name='username']").val(localStorage.username);
                $("input[name='password']").val(localStorage.password);
                $("input[name='rememberMe']").iCheck('check');
                $("input[name='rememberMe']").iCheck('update');
            }
        }
    </script>
</body>
</html>
