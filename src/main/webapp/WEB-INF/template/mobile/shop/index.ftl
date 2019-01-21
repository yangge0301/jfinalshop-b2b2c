<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
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
    <link href="${base}/resources/mobile/shop/css/font-awesome.css" rel="stylesheet">
    <link href="${base}/resources/mobile/shop/css/animate.css" rel="stylesheet">
    <link href="${base}/resources/mobile/shop/css/common.css" rel="stylesheet">
    <link href="${base}/resources/mobile/shop/css/index.css" rel="stylesheet">
    <link href="${base}/resources/mobile/shop/css/home.css" rel="stylesheet" type="text/css" />
    <!--[if lt IE 9]>
    <script src="${base}/resources/mobile/shop/js/html5shiv.js"></script>
    <script src="${base}/resources/mobile/shop/js/respond.js"></script>
    <![endif]-->
    <script src="${base}/resources/mobile/shop/js/jquery.js"></script>
    <script src="${base}/resources/mobile/shop/js/jquery.lazyload.js"></script>
    <script src="${base}/resources/mobile/shop/js/bootstrap.js"></script>
    <script src="${base}/resources/mobile/shop/js/velocity.js"></script>
    <script src="${base}/resources/mobile/shop/js/velocity.ui.js"></script>
    <script src="${base}/resources/mobile/shop/js/underscore.js"></script>
    <script src="${base}/resources/mobile/shop/js/hammer.js"></script>
    <script src="${base}/resources/mobile/shop/js/common.js"></script>
    <script type="text/javascript">
        $().ready(function() {

            var $searchIcon = $("#searchIcon");
            var $searchPlaceholder = $("#searchPlaceholder");
            var $search = $("#search");
            var $searchSlideUp = $("#searchSlideUp");
            var $searchForm = $("#searchForm");
            var $keyword = $("#keyword");
            var $login = $("#login");
            var $member = $("#member");
            var $masthead = $("#masthead");
            var $productImage = $("div.products img");

            // 登录/会员中心
            if (getCookie("currentMemberUsername") != null) {
                $member.show();
            } else {
                $login.show();
            }

            // 搜索
            $searchIcon.add($searchPlaceholder).click(function() {
                $search.velocity("transition.slideDownBigIn");
            });

            // 搜索
            $searchSlideUp.click(function() {
                $search.velocity("transition.slideUpBigOut");
            });

            // 搜索
            $searchForm.submit(function() {
                if ($.trim($keyword.val()) == "") {
                    return false;
                }
            });

            // 广告
            new Hammer($masthead.get(0)).on("swipeleft", function() {
                $masthead.carousel("next");
            }).on("swiperight", function() {
                $masthead.carousel("prev");
            });

            // 商品图片
            $productImage.lazyload({
                threshold: 100,
                effect: "fadeIn"
            });

        });
    </script>
</head>
<body class="index">

<header class="header-fixed"  style="background:#fff;opacity:1;z-index:999999">
    <a class="pull-left" href="javascript: history.back();">
        <span class="glyphicon glyphicon-menu-left"></span>
    </a>
首页
</header>

<!--中间橙色背景 开始-->
<div class="top-orange-bg">
    <div style="width:100%;position:relative;padding-bottom:3rem;">
        <img src="${base}/resources/mobile/shop/images/home/bga_02.png" alt="" width="100%;">
        <div style="width:100%;position:absolute;top:5rem;;left:0;">

            <header style="margin-top:5rem:position:none;opacity:1;background: none;">
                <div class="container-fluid">
                    <div class="row" style="height:4rem;">
                        <div class="col-xs-2 text-center" style="width:2%;">
                        </div>
                        <div class="col-xs-8" style="width:96%;height:4rem;">
                            <div id="searchPlaceholder" class="search-placeholder" style="height:4rem;line-height:4rem;">
                            ${message("shop.index.keyword")}<span class="glyphicon glyphicon-search"></span>
                            </div>
                        </div>
                        <div class="col-xs-2 text-center"  style="width:2%;">
                        [#--<a id="login" class="login" href="${base}/member/login">${message("shop.index.login")}</a>--]
                        [#--<a id="member" class="member" href="${base}/member/index">--]
                        [#--<span class="fa fa-user-o"></span>--]
                        [#--</a>--]
                        </div>
                    </div>
                    <div id="search" class="search" style="position:absolute">
                        <div class="row">
                            <div class="col-xs-1 text-center">
                                <span id="searchSlideUp" class="glyphicon glyphicon-menu-up"></span>
                            </div>
                            <div class="col-xs-11">
                                <form id="searchForm" action="${base}/product/search" method="get">
                                    <div class="input-group">
                                        <input id="keyword" name="keyword" class="form-control" type="text" placeholder="${message("shop.index.keyword")}">
                                        <span class="input-group-btn">
									<button class="btn btn-default" type="submit">
										<span class="glyphicon glyphicon-search"></span>
									</button>
								</span>
                                    </div>
                                </form>
                            </div>
                        </div>
				[#if setting.hotSearches?has_content]
					<dl class="hot-search">
                        <dt>
                            <span class="glyphicon glyphicon-star-empty"></span>${message("shop.index.hotSearch")}
                        </dt>
						[#list setting.hotSearches as hotSearch]
							<dd>
                                <a href="${base}/product/search?keyword=${hotSearch?url}">${hotSearch}</a>
                            </dd>
                        [/#list]
                    </dl>
                [/#if]
                    </div>
                </div>
            </header>
            <nav>
                <div class="row">
                    <div class="col-xs-3 text-center"  style="color:#fff">
                        <a href="${base}/product/list/1"  style="color:#fff;font-size:1.4rem">
                            <img src="${base}/resources/mobile/shop/images/home/menu-jntc.png" alt="江宁特产">
                            江宁特产
                        </a>
                    </div>
                    <div class="col-xs-3 text-center" style="color:#fff">
                        <a href="${base}/product/list/2"  style="color:#fff;font-size:1.4rem">
                            <img src="${base}/resources/mobile/shop/images/home/menu-axym.png" alt="爱心义买">
                            爱心义买
                        </a>
                    </div>
                    <div class="col-xs-3 text-center"  style="color:#fff">
                        <a href="${base}/product/list/3"  style="color:#fff;font-size:1.4rem">
                            <img src="${base}/resources/mobile/shop/images/home/menu-yhjx.png" alt="优惠巨献">
                            优惠巨献
                        </a>
                    </div>
                    <div class="col-xs-3 text-center"  style="color:#fff">
                        <a href="${base}/product/list/4"  style="color:#fff;font-size:1.4rem">
                            <img src="${base}/resources/mobile/shop/images/home/menu-jfsc.png" alt="积分商城">
                            积分商城
                        </a>
                    </div>
                    [#--<div class="col-xs-3 text-center">--]
                        [#--<a href="${base}/product/list/1">--]
                            [#--<img src="${base}/upload/image/index_nav_5.png" alt="手机专场">--]
                            [#--手机专场--]
                        [#--</a>--]
                    [#--</div>--]
                    [#--<div class="col-xs-3 text-center">--]
                        [#--<a href="${base}/product/list/1">--]
                            [#--<img src="${base}/upload/image/index_nav_6.png" alt="心随乐动">--]
                            [#--心随乐动--]
                        [#--</a>--]
                    [#--</div>--]
                    [#--<div class="col-xs-3 text-center">--]
                        [#--<a href="${base}/product/list/1">--]
                            [#--<img src="${base}/upload/image/index_nav_7.png" alt="发现好货">--]
                            [#--发现好货--]
                        [#--</a>--]
                    [#--</div>--]
                    [#--<div class="col-xs-3 text-center">--]
                        [#--<a href="${base}/product/list/1">--]
                            [#--<img src="${base}/upload/image/index_nav_8.png" alt="乐享视界">--]
                            [#--乐享视界--]
                        [#--</a>--]
                    [#--</div>--]
                </div>
            </nav>
            <div id="masthead" class="masthead carousel slide" data-ride="carousel" style="width:96%;margin: 0 auto">
                <ol class="carousel-indicators">
                    <li class="active" data-target="#masthead" data-slide-to="0"></li>
                    <li data-target="#masthead" data-slide-to="1"></li>
                    <li data-target="#masthead" data-slide-to="2"></li>
                </ol>
                <ul class="carousel-inner" style="border-radius:0.5rem">
                    <li class="item active">
                        <a href="#">
                            <img src="${base}/upload/image/index_slider1.jpg" alt="荣耀8">
                        </a>
                    </li>
                    <li class="item">
                        <a href="#">
                            <img src="${base}/upload/image/index_slider2.jpg" alt="百万豪礼">
                        </a>
                    </li>
                    <li class="item">
                        <a href="#">
                            <img src="${base}/upload/image/index_slider3.jpg" alt="百万豪礼">
                        </a>
                    </li>
                </ul>
            </div>





        </div>
    </div>
</div>
<!--中间橙色背景 结束-->




<main>
    <div class="container-fluid">


        <div class="promotion">
            <div class="row">
                <div class="col-xs-2 text-center">
                    <span class="glyphicon red-dark">
			            <img src="${base}/resources/mobile/shop/images/home/news_left.png" alt="logo" width="70%" />
                    </span>
                </div>
                <div class="col-xs-10">
                    <div class="carousel" data-ride="carousel">
                        <ul class="carousel-inner">
                            <li class="item active">
                                <a href="${base}/product/list/1">
                                    <em class="blue-dark">杨震金牌鸭王南京美食</em>
                                    色泽鲜艳，皮脆肉嫩，配加秘制卤汁
                                </a>
                            </li>
                            <li class="item">
                                <a href="${base}/product/list/1">
                                    <em class="blue-dark">南国有福椰年货特产大礼包春节礼品</em>
                                    新品上市 特产礼盒年货送礼
                                </a>
                            </li>
                            <li class="item">
                                <a href="${base}/product/list/1">
                                    <em class="blue-dark">南京特产-江宁金箔书</em>
                                    金箔工艺品 商务礼品 道德经金箔礼品书
                                </a>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>

        <!--中间商城左右结构入口 开始-->
        <div class="shop-allright">
            <a class="allright allright-left" href="${base}/product/list/6">

                <img src="${base}/resources/mobile/shop/images/home/allright-left.png" alt="logo" width="100%" />
            </a>
            <a class="allright allright-right" href="${base}/product/list/1">
                <img src="${base}/resources/mobile/shop/images/home/allright-right.png" alt="logo" width="100%" />
            </a>
        </div>
        <!--中间商城左右结构入口 结束-->
        [#--<div class="ad">--]
            [#--<ul>--]
                [#--<li>--]
                    [#--<a href="${base}/product/list/1">--]
                        [#--<img src="${base}/upload/image/row3_slider_1.jpg" alt="音响">--]
                    [#--</a>--]
                [#--</li>--]
                [#--<li>--]
                    [#--<a href="${base}/product/list/1">--]
                        [#--<img src="${base}/upload/image/row3_slider_2.jpg" alt="音响">--]
                    [#--</a>--]
                [#--</li>--]
                [#--<li>--]
                    [#--<a href="${base}/product/list/1">--]
                        [#--<img src="${base}/upload/image/row3_slider_3.jpg" alt="音响">--]
                    [#--</a>--]
                [#--</li>--]
                [#--<li>--]
                    [#--<a href="${base}/product/list/1">--]
                        [#--<img src="${base}/upload/image/row3_slider_4.jpg" alt="音响">--]
                    [#--</a>--]
                [#--</li>--]
            [#--</ul>--]
        [#--</div>--]
			[@product_category_root_list count = 3]
                [#list productCategories as productCategory]
					<div class="products panel panel-flat panel-condensed">

                        <div class="panel-heading orange" style="background: #fff">
                            <div class="shop-hot-sale">
                                <img src="${base}/resources/mobile/shop/images/home/hot-sale-icon.png" alt="logo" width="6%" /> ${productCategory.name}
                            </div>
                        </div>
                        <div class="panel-body">
                            <div class="row">
								[@product_list product_category_id = productCategory.id count = 6]
									[#list products as product]
                                        [#assign defaultSku = product.defaultSku /]
										<div class="col-xs-4" style="margin-left:1.33%;width:48%;">
                                            <div class="thumbnail thumbnail-flat thumbnail-condensed">
                                                <a href="${base}${product.path}">
                                                    <div style="width:100%;">
                                                        <img class="img-responsive center-block" src="/b2b2c/5.0/201601/e44bc02b-e142-4fce-b88e-02b4ee8f392e-thumbnail.jpg" alt="${product.name}" data-original="${product.image!setting.defaultThumbnailProductImage}">

                                                    </div>
                                                    <h4 class="text-overflow">${product.name}</h4>
                                                    <p class="text-overflow text-muted small">${product.caption}&nbsp;</p>
                                                </a>
												[#if product.typeName == "general"]
													<strong class="red">${currency(defaultSku.price, true)}</strong>
                                                [#elseif product.typeName == "exchange"]
													<span class="small">${message("Sku.exchangePoint")}:</span>
													<strong class="red">${defaultSku.exchangePoint}</strong>
                                                [/#if]
                                            </div>
                                        </div>
                                    [/#list]
                                [/@product_list]
                            </div>
                        </div>
                    </div>
                [/#list]
            [/@product_category_root_list]
    </div>
</main>
<footer class="footer-fixed">
    <div class="container-fluid">
        <div class="row">
            <div class="col-xs-3 text-center active">
                <span class="glyphicon">
                    <img src="${base}/resources/mobile/shop/images/home/home.png" alt=""  width="26%"/>
                </span>
                <a   style="display: block" href="${base}/">${message("shop.common.index")}</a>
            </div>
            <div class="col-xs-3 text-center">
                    <span class="glyphicon">
						<img src="${base}/resources/mobile/shop/images/home/category.png" alt="" width="26%"/>
					</span>
                <a   style="display: block" href="${base}/product_category">${message("shop.common.productCategory")}</a>
            </div>
            <div class="col-xs-3 text-center">
                    <span class="glyphicon">
						<img src="${base}/resources/mobile/shop/images/home/buycar.png" alt="" width="26%"/>
					</span>
                <a   style="display: block" href="${base}/cart/list">${message("shop.common.cart")}</a>
            </div>
            <div class="col-xs-3 text-center">
                    <span class="glyphicon" >
						<img src="${base}/resources/mobile/shop/images/home/mine.png" alt="" width="26%"/>
					</span>
                <a   style="display: block" href="${base}/member/index">${message("shop.common.member")}</a>
            </div>
        </div>
    </div>
</footer>
</body>
</html>