var arr=[
{
	index:0,
	name:'江宁特产',
	isSelect:'select',
	data:[
		{
			img:'../images/business/1.png',
			title:'爱心义卖 聋哑人十字绣作品纯 手工 十字绣1',
			money:'111',
			buytip:'11人付款',
			url:'#',
		},
		{
			img:'../images/business/2.png',
			title:'爱心义卖 聋哑人十字绣作品纯 手工 十字绣2',
			money:'112',
			buytip:'12人付款',
			url:'#',
		},
		{
			img:'../images/business/3.png',
			title:'爱心义卖 聋哑人十字绣作品纯 手工 十字绣3',
			money:'113',
			buytip:'13人付款',
			url:'#',
		},
		{
			img:'../images/business/4.png',
			title:'爱心义卖 聋哑人十字绣作品纯 手工 十字绣4',
			money:'114',
			buytip:'14人付款',
			url:'#',
		},
		{
			img:'../images/business/5.png',
			title:'爱心义卖 聋哑人十字绣作品纯 手工 十字绣5',
			money:'115',
			buytip:'15人付款',
			url:'#',
		},
		{
			img:'../images/business/6.png',
			title:'爱心义卖 聋哑人十字绣作品纯 手工 十字绣6',
			money:'116',
			buytip:'16人付款',
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

	
	var list = arr[0].data;

	for(let i=0;i<list.length;i++){
		tabcon+='<a href="'+list[i].url+'" class="'+(i%2==0?css['left']:css['right'])+'"><div class="box-img"><img src="'+list[i].img+'" alt="logo" width="100%" /></div><div class="box-content">'+list[i].title+'</div><div class="box-desc"><span class="box-money">￥'+list[i].money+'</span></div><div class="box-desc"><span  class="box-paycount">'+list[i].buytip+'</span></div></a>';
	}
	$(".shop-hot-sale-content").html(tabcon);

	

})