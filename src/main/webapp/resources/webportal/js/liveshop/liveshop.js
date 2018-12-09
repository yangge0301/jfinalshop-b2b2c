var phones=[
	{
		img:'../images/liveshop/1.png',
		name:'保温茶水壶',
		url:'#',
	},
	{
		img:'../images/liveshop/2.png',
		name:'保温茶水壶',
		url:'#',
	},
	{
		img:'../images/liveshop/3.png',
		name:'保温茶水壶',
		url:'#',
	},
]
var lives=[
	{
		index:0,
		name:'直播中',
		isSelect:'select',
		liveList:[
			{
				id:0,
				img:'../images/liveshop/bottomlive.png',
				btnAction:'#',
			},
			{
				id:1,
				img:'../images/liveshop/bottomlive.png',
				btnAction:'#',
			},
			{
				id:2,
				img:'../images/liveshop/bottomlive.png',
				btnAction:'#',
			},
			{
				id:3,
				img:'../images/liveshop/bottomlive.png',
				btnAction:'#',
			},
		]
	},
	{
		index:1,
		name:'往期会看',
		isSelect:'noselect',
		liveList:[
			{
				id:11,
				img:'../images/liveshop/1.png',
				btnAction:'#',
			},
			{
				id:22,
				img:'../images/liveshop/bottomlive.png',
				btnAction:'#',
			},
			{
				id:33,
				img:'../images/liveshop/bottomlive.png',
				btnAction:'#',
			},
			{
				id:44,
				img:'../images/liveshop/bottomlive.png',
				btnAction:'#',
			},
		]
	},
];
var css={
	"alive":'livetabs-item-alive',

};

var phoneCon="";
var liveData="";

$(function(){
	var wid = $(".item img").width();
	$(".top-orange-bg-banner").height(wid);

	for(let i=0;i<phones.length;i++){
		phoneCon+='<a class="leftphoto" href="'+phones[i].url+'"><div><img src="'+phones[i].img+'" alt="" width="100%;" /></div><div style="line-height: 2rem;">'+phones[i].name+'</div></a>';
	}
	$(".photos").html(phoneCon);

	var data = lives[0].liveList;
	for(let i=0;i<data.length;i++){
		liveData+='<div style="width: 100%;margin-bottom:1rem;" u_action="'+data[i].btnAction+'"><img src="'+data[i].img+'" alt="" width="100%;" /></div>';
	}
	$(".livecon").html(liveData);
	$(".livetabs-item").click(function(){
		var _index=$(this).index('.livetabs-item');
		$(this).addClass(css['alive']).siblings().removeClass(css['alive']);
		liveData="";
		var data = lives[_index].liveList;
		for(let i=0;i<data.length;i++){
			liveData+='<div style="width: 100%;margin-bottom:1rem;" u_action="'+data[i].btnAction+'"><img src="'+data[i].img+'" alt="" width="100%;" /></div>';
		}
		$(".livecon").html(liveData);

	});

})