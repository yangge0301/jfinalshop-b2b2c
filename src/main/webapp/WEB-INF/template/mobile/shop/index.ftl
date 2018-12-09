<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="Iphone-content" content="375">
    <meta name="format-detection" content="telephone=no">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=0, minimal-ui">
    <title>商城首页</title>
    <meta name="author" content="JFinalShop Team">
    <meta name="copyright" content="JFinalShop">
	[@seo type = "index"]
		<title>${seo.resolveTitle()}[#if showPowered] - Powered By JFinalShop[/#if]</title>
        [#if seo.resolveKeywords()?has_content]
			<meta name="keywords" content="${seo.resolveKeywords()}">
        [/#if]
        [#if seo.resolveDescription()?has_content]
			<meta name="description" content="${seo.resolveDescription()}">
        [/#if]
    [/@seo]
    <link href="${base}/favicon.ico" rel="icon">
    <link href="${base}/resources/mobile/shop/css/bootstrap.css" rel="stylesheet">
    <link href="${base}/resources/mobile/shop/css/home.css" rel="stylesheet" type="text/css">
    <script src="${base}/resources/mobile/shop/js/jquery.js"></script>
    <script src="${base}/resources/mobile/shop/js/bootstrap.js"></script>
    <script>
        $(function(){
            $('#myCarousel').carousel({interval:3000});

            //手势右滑 回到上一个画面
            $('#myCarousel').bind('swiperight swiperightup swiperightdown',function(){
                $("#myCarousel").carousel('prev');
            })
            //手势左滑 进入下一个画面
            $('#myCarousel').bind('swipeleft swipeleftup swipeleftdown',function(){
                $("#myCarousel").carousel('next');
            })

            $(document).ready(function() {
                setInterval('AutoScroll("#demo")', 2000)
            });


        })
        function AutoScroll(obj) {
            $(obj).find("ul:first").animate({
                        marginTop: "-4rem"
                    },
                    1500,
                    function() {
                        $(this).css({
                            marginTop: "0px"
                        }).find("li:first").appendTo(this);
                    });
        }
    </script>
</head>
<body >
<!--顶部有返回空间导航栏 开始-->
<div class="top-shop">
    <img src="${base}/resources/mobile/shop/images/home/top_left_icon.png" alt="logo" width="2.6%"/> <span>诚信商城</span>
</div>
<!--顶部有返回空间导航栏 结束-->
<!--中间橙色背景 开始-->
<div class="top-orange-bg">
    <div style="width:100%;position:relative;">
        <img src="${base}/resources/mobile/shop/images/home/bga_02.png" alt="" width="100%;">
        <div style="width:100%;position:absolute;top:0;left:0;">
            <!--中间橙色背景——搜索框 开始-->
            <div class="top-orange-bg-search">
                <div class="search_icon">
                    <img src="${base}/resources/mobile/shop/images/home/search_icon.png" alt="logo" width="60%" />
                </div>
                <input type="text" class="search_text" name="" value="" placeholder="请输入您想要的商品" />

            </div>
            <!--中间橙色背景——搜索框 结束-->

            <!--中间橙色背景——菜单栏 开始-->
            <div class="top-orange-menu">
                <a class="top-orange-menu-item" href="#">
                    <div class="menu-item-icon">
                        <img src="${base}/resources/mobile/shop/images/home/menu-jntc.png" alt="logo" width="60%" />
                    </div>
                    <div class="menu-item-name">
                        江宁特产
                    </div>
                </a>

                <a class="top-orange-menu-item" href="#">
                    <div class="menu-item-icon">
                        <img src="${base}/resources/mobile/shop/images/home/menu-axym.png" alt="logo" width="60%" />
                    </div>
                    <div class="menu-item-name">
                        爱心义卖
                    </div>
                </a>

                <a class="top-orange-menu-item" href="#">
                    <div class="menu-item-icon">
                        <img src="${base}/resources/mobile/shop/images/home/menu-yhjx.png" alt="logo" width="60%" />
                    </div>
                    <div class="menu-item-name">
                        优惠巨献
                    </div>
                </a>

                <a class="top-orange-menu-item" href="#">
                    <div class="menu-item-icon">
                        <img src="${base}/resources/mobile/shop/images/home/menu-jfsc.png" alt="logo" width="60%" />
                    </div>
                    <div class="menu-item-name">
                        积分商城
                    </div>
                </a>
            </div>
            <!--中间橙色背景——菜单栏 结束-->
            <!--中间橙色背景——轮播图 开始-->
            <div class="top-orange-bg-banner">
                <div id="myCarousel" class="carousel slide">
                    <!-- 轮播（Carousel）指标 -->
                    <ol class="carousel-indicators">
                        <li data-target="#myCarousel" data-slide-to="0" class="active"></li>
                        <li data-target="#myCarousel" data-slide-to="1"></li>
                        <li data-target="#myCarousel" data-slide-to="2"></li>
                    </ol>
                    <!-- 轮播（Carousel）项目 -->
                    <div class="carousel-inner">
                        <div class="item active">
                            <img src="${base}/resources/mobile/shop/images/home/banner_01.png" alt="First slide">
                        </div>
                        <div class="item">
                            <img src="${base}/resources/mobile/shop/images/home/banner_01.png" alt="Second slide">
                        </div>
                        <div class="item">
                            <img src="${base}/resources/mobile/shop/images/home/banner_01.png" alt="Third slide">
                        </div>
                    </div>

                    <!-- 轮播（Carousel）导航
                        <a class="left carousel-control" href="#myCarousel" role="button" data-slide="prev">
                            <span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>
                            <span class="sr-only">Previous</span>
                        </a>
                        <a class="right carousel-control" href="#myCarousel" role="button" data-slide="next">
                            <span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
                            <span class="sr-only">Next</span>
                        </a>
                    -->
                </div>

            </div>
            <!--中间橙色背景——轮播图 结束-->
        </div>
    </div>









</div>
<!--中间橙色背景 结束-->

<!--中间商城头条 开始-->
<div class="shop-news">
    <div class="shop-news-left">
        <img src="${base}/resources/mobile/shop/images/home/news_left.png" alt="logo" width="70%" />
    </div>
    <div class="shop-news-right">
        <div id="demo" class="right-content">
            <ul class="mingdan" id="holder" style="margin-top: 0px;">
                <li><a href="#" target="_blank">eeeeee</a></li>
                <li><a href="#" target="_blank">aaaaaa</a></li>
                <li><a href="#" target="_blank">bbbbbb</a></li>
                <li><a href="#" target="_blank">cccccc</a></li>
                <li><a href="#" target="_blank">dddddd</a></li>
            </ul>
        </div>
    </div>
</div>
<!--中间商城头条 结束-->

<!--中间商城左右结构入口 开始-->
<div class="shop-allright">
    <a class="allright allright-left" href="#">

        <img src="${base}/resources/mobile/shop/images/home/allright-left.png" alt="logo" width="100%" />
    </a>
    <a class="allright allright-right" href="#">
        <img src="${base}/resources/mobile/shop/images/home/allright-right.png" alt="logo" width="100%" />
    </a>
</div>
<!--中间商城左右结构入口 结束-->


<!--中间热卖推荐 开始-->
<div class="shop-hot-sale">
    <img src="${base}/resources/mobile/shop/images/home/hot-sale-icon.png" alt="logo" width="6%" /> 热卖推荐
</div>
<!--中间热卖推荐 结束-->

<!--中间热卖推荐content 开始-->
<div class="shop-hot-sale-content">
    <div class="content-box-left">
        <div class="box-img">
            <img src="${base}/resources/mobile/shop/images/home/hot-sale-1.png" alt="logo" width="100%" />
        </div>
        <div class="box-content">爱心义卖 聋哑人十字绣作品纯 手工 十字绣</div>
        <div class="box-desc">
            <span class="box-money">￥220</span> <span  class="box-paycount">22人付款</span>
        </div>
    </div>
    <div class="content-box-right">
        <div class="box-img">
            <img src="${base}/resources/mobile/shop/images/home/hot-sale-2.png" alt="logo" width="100%" />
        </div>
        <div class="box-content">爱心义卖 聋哑人十字绣作品纯 手工 十字绣</div>
        <div class="box-desc">
            <span class="box-money">￥220</span> <span  class="box-paycount">22人付款</span>
        </div>
    </div>
    <div class="content-box-left">
        <div class="box-img">
            <img src="${base}/resources/mobile/shop/images/home/hot-sale-3.png" alt="logo" width="100%" />
        </div>
        <div class="box-content">爱心义卖 聋哑人十字绣作品纯 手工 十字绣</div>
        <div class="box-desc">
            <span class="box-money">￥220</span> <span  class="box-paycount">22人付款</span>
        </div>
    </div>
    <div class="content-box-right">
        <div class="box-img">
            <img src="${base}/resources/mobile/shop/images/home/hot-sale-4.png" alt="logo" width="100%" />
        </div>
        <div class="box-content">爱心义卖 聋哑人十字绣作品纯 手工 十字绣</div>
        <div class="box-desc">
            <span class="box-money">￥220</span> <span  class="box-paycount">22人付款</span>
        </div>
    </div>
</div>
<!--中间热卖推荐content 结束-->



<!--中间猜你喜欢 开始-->
<div class="shop-hot-sale">
    <img src="${base}/resources/mobile/shop/images/home/guss-u-icon.png" alt="logo" width="6%" /> 猜你喜欢
</div>
<!--中间猜你喜欢 结束-->

<!--中间猜你喜欢content 开始-->
<div class="shop-hot-sale-content">
    <div class="content-box-left">
        <div class="box-img">
            <img src="${base}/resources/mobile/shop/images/home/guss-u-1.png" alt="logo" width="100%" />
        </div>
        <div class="box-content">爱心义卖 聋哑人十字绣作品纯 手工 十字绣</div>
        <div class="box-desc">
            <span class="box-money">￥220</span> <span  class="box-paycount">22人付款</span>
        </div>
    </div>
    <div class="content-box-right">
        <div class="box-img">
            <img src="${base}/resources/mobile/shop/images/home/guss-u-2.png" alt="logo" width="100%" />
        </div>
        <div class="box-content">爱心义卖 聋哑人十字绣作品纯 手工 十字绣</div>
        <div class="box-desc">
            <span class="box-money">￥220</span> <span  class="box-paycount">22人付款</span>
        </div>
    </div>
    <div class="content-box-left">
        <div class="box-img">
            <img src="${base}/resources/mobile/shop/images/home/guss-u-3.png" alt="logo" width="100%" />
        </div>
        <div class="box-content">爱心义卖 聋哑人十字绣作品纯 手工 十字绣</div>
        <div class="box-desc">
            <span class="box-money">￥220</span> <span  class="box-paycount">22人付款</span>
        </div>
    </div>
    <div class="content-box-right">
        <div class="box-img">
            <img src="${base}/resources/mobile/shop/images/home/guss-u-4.png" alt="logo" width="100%" />
        </div>
        <div class="box-content">爱心义卖 聋哑人十字绣作品纯 手工 十字绣</div>
        <div class="box-desc">
            <span class="box-money">￥220</span> <span  class="box-paycount">22人付款</span>
        </div>
    </div>
</div>
<!--中间猜你喜欢content 结束-->
<!--底部菜单栏开始-->
<div class="bottom-menu">
    <div class="bottom-menu-box">
        <a class="bottom-orange-menu-item" href="#">
            <div class="bottom-item-icon">
                <img src="${base}/resources/mobile/shop/images/home/home.png" alt="logo" width="30%" />
            </div>
            <div class="bottom-item-name">
                首页
            </div>
        </a>

        <a class="bottom-orange-menu-item" href="#">
            <div class="bottom-item-icon">
                <img src="${base}/resources/mobile/shop/images/home/category.png" alt="logo" width="30%" />
            </div>
            <div class="bottom-item-name">
                分类
            </div>
        </a>

        <a class="bottom-orange-menu-item" href="#">
            <div class="bottom-item-icon">
                <img src="${base}/resources/mobile/shop/images/home/buycar.png" alt="logo" width="30%" />
            </div>
            <div class="bottom-item-name">
                购物车
            </div>
        </a>

        <a class="bottom-orange-menu-item" href="#">
            <div class="bottom-item-icon">
                <img src="${base}/resources/mobile/shop/images/home/mine.png" alt="logo" width="30%" />
            </div>
            <div class="bottom-item-name">
                我的
            </div>
        </a>
    </div>
</div>

<!--底部菜单栏结束-->
</body>
</html>