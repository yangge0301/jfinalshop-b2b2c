var arr=[
{
	index:0,
	name:'江宁特产',
	isSelect:'select',
	data:[
		{
			img:'../images/scoreshop/scoreimg1.png',
			title:'爱心义卖 聋哑人十字绣作品纯 手工 十字绣1',
			money:'111',
			buytip:'11人付款',
			url:'#',
		},
		{
			img:'../images/scoreshop/scoreimg1.png',
			title:'爱心义卖 聋哑人十字绣作品纯 手工 十字绣1',
			money:'112',
			buytip:'12人付款',
			url:'#',
		},
		{
			img:'../images/scoreshop/scoreimg1.png',
			title:'爱心义卖 聋哑人十字绣作品纯 手工 十字绣1',
			money:'113',
			buytip:'13人付款',
			url:'#',
		},
		{
			img:'../images/scoreshop/scoreimg1.png',
			title:'爱心义卖 聋哑人十字绣作品纯 手工 十字绣1',
			money:'114',
			buytip:'14人付款',
			url:'#',
		},
		{
			img:'../images/scoreshop/scoreimg1.png',
			title:'爱心义卖 聋哑人十字绣作品纯 手工 十字绣1',
			money:'115',
			buytip:'15人付款',
			url:'#',
		},

	]
},
{
	index:1,
	name:'爱心义买',
	isSelect:'noselect',
	data:[
		{
			img:'../images/scoreshop/scoreimg1.png',
			title:'爱心义卖 聋哑人十字绣作品纯 手工 十字绣1',
			money:'221',
			buytip:'21人付款',
			url:'#',
		},
		{
			img:'../images/scoreshop/scoreimg1.png',
			title:'爱心义卖 聋哑人十字绣作品纯 手工 十字绣1',
			money:'222',
			buytip:'22人付款',
			url:'#',
		},
		{
			img:'../images/scoreshop/scoreimg1.png',
			title:'爱心义卖 聋哑人十字绣作品纯 手工 十字绣1',
			money:'223',
			buytip:'23人付款',
			url:'#',
		},
		{
			img:'../images/scoreshop/scoreimg1.png',
			title:'爱心义卖 聋哑人十字绣作品纯 手工 十字绣1',
			money:'224',
			buytip:'24人付款',
			url:'#',
		},
		{
			img:'../images/scoreshop/scoreimg1.png',
			title:'爱心义卖 聋哑人十字绣作品纯 手工 十字绣1',
			money:'225',
			buytip:'25人付款',
			url:'#',
		},

	]
},
{
	index:2,
	name:'优惠巨献',
	isSelect:'noselect',
	data:[
		{
			img:'../images/scoreshop/scoreimg1.png',
			title:'爱心义卖 聋哑人十字绣作品纯 手工 十字绣1',
			money:'331',
			buytip:'31人付款',
			url:'#',
		},
		{
			img:'../images/scoreshop/scoreimg1.png',
			title:'爱心义卖 聋哑人十字绣作品纯 手工 十字绣1',
			money:'332',
			buytip:'32人付款',
			url:'#',
		},
		{
			img:'../images/scoreshop/scoreimg1.png',
			title:'爱心义卖 聋哑人十字绣作品纯 手工 十字绣1',
			money:'333',
			buytip:'33人付款',
			url:'#',
		},
		{
			img:'../images/scoreshop/scoreimg1.png',
			title:'爱心义卖 聋哑人十字绣作品纯 手工 十字绣1',
			money:'334',
			buytip:'34人付款',
			url:'#',
		},
		{
			img:'../images/scoreshop/scoreimg1.png',
			title:'爱心义卖 聋哑人十字绣作品纯 手工 十字绣1',
			money:'335',
			buytip:'35人付款',
			url:'#',
		},

	]
},
];
var css={
	noselect:'headtab-item',
	select:'headtab-item headtab-item-alive',
	left:'content-box-left',
	right:'content-box-right',
	alive:'headtab-item-alive',
}

var tab="";
var tabcon="";
$(function(){

	for(let i=0;i<arr.length;i++){
		tab+='<div class="'+(arr[i].isSelect=='select'?css['select']:css['noselect'])+'">'+arr[i].name+'</div>';
	}
	var list = arr[0].data;

	for(let i=0;i<list.length;i++){
		tabcon+='<a href="'+list[i].url+'" class="'+(i%2==0?css['left']:css['right'])+'"><div class="box-img"><img src="'+list[i].img+'" alt="logo" width="100%" /></div><div class="box-content">'+list[i].title+'</div><div class="box-desc"><span class="box-money">￥'+list[i].money+'</span></div><div class="box-desc"><span  class="box-paycount">'+list[i].buytip+'</span></div></a>';
	}
	$(".headtab").html(tab);
	$(".shop-hot-sale-content").html(tabcon);

	$(".headtab-item ").click(function(){
		var _index=$(this).index('.headtab-item');
		$(this).addClass(css['alive']).siblings().removeClass(css['alive']);

		tabcon="";
		var list = arr[_index].data;

		for(let i=0;i<list.length;i++){
			tabcon+='<a href="'+list[i].url+'" class="'+(i%2==0?css['left']:css['right'])+'"><div class="box-img"><img src="'+list[i].img+'" alt="logo" width="100%" /></div><div class="box-content">'+list[i].title+'</div><div class="box-desc"><span class="box-money">￥'+list[i].money+'</span></div><div class="box-desc"><span  class="box-paycount">'+list[i].buytip+'</span></div></a>';
		}

		$(".shop-hot-sale-content").html(tabcon);
	});


})