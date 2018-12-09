var arr=[
{
	index:1,
	goodsId:1,
	isSelect:'select',
	name:'王小二紫薯新鲜番薯批发包邮好吃当 季时令采摘山芋地瓜1',
	count:1,
	saleprice:30,
	bookimg:'../images/shoppingcar/book1.png',
},
{
	index:2,
	goodsId:2,
	isSelect:'select',
	name:'王小二紫薯新鲜番薯批发包邮好吃当 季时令采摘山芋地瓜2',
	count:1,
	saleprice:15,
	bookimg:'../images/shoppingcar/book1.png',
},
{
	index:3,
	goodsId:3,
	isSelect:'select',
	name:'王小二紫薯新鲜番薯批发包邮好吃当 季时令采摘山芋地瓜3',
	count:1,
	saleprice:44,
	bookimg:'../images/shoppingcar/book1.png',
},
{
	index:4,
	goodsId:4,
	isSelect:'noselect',
	name:'王小二紫薯新鲜番薯批发包邮好吃当 季时令采摘山芋地瓜4',
	count:1,
	saleprice:3,
	bookimg:'../images/shoppingcar/book1.png',
}
];
var allMoney=0;
var img={
	'select':'../images/shoppingcar/select.png',
	'noselect':'../images/shoppingcar/noselect.png',
};
let allselect=true;
let htm="";
$(function(){
	for(let i=0;i<arr.length;i++){
		if(arr[i].isSelect=='select'){
			allMoney+=arr[i].count*arr[i].saleprice;
		}
		else{
			allselect=false;
		}
		htm+='<div class="shoppingcar-text-boss" u_index="'+arr[i].index+'" u_goodsId="'+arr[i].goodsId+' "u_price="'+arr[i].saleprice+'"><div class="shoppingcar-item"><div class="shoppingcar-item-left img_select"><img class="imgselect" src="'+(arr[i].isSelect=='select'?'../images/shoppingcar/select.png':'../images/shoppingcar/noselect.png')+'"alt="logo" width="40%" /></div><div class="shoppingcar-item-book"><img src="'+arr[i].bookimg+'"alt="logo"width="100%" height="100%" /></div><div class="shoppingcar-item-right"><div class="shoppingcar-item-right-title">'+arr[i].name+'</div><div class="shoppingcar-item-right-bottom"><div class="shoppingcar-item-right-money">￥'+arr[i].saleprice+'</div><div class="shoppingcar-item-right-add" ><div class="add-icon u_sub"  u_index="'+arr[i].index+'" u_goodsId="'+arr[i].goodsId+' "u_price="'+arr[i].saleprice+'">-</div><div class="add-middle u_value">'+arr[i].count+'</div><div class="add-icon u_add"  u_index="'+arr[i].index+'" u_goodsId="'+arr[i].goodsId+' "u_price="'+arr[i].saleprice+'">+</div></div></div></div></div></div>';
	}
	if(allselect==true){
		$(".car-box-img img").attr("src",img['select']);
	}
	$(".shoppingcar-text").html(htm);
	$(".resultMoney").html("￥"+allMoney);




	$(".car-box-img").click(function(){

		for(let i=0;i<arr.length;i++){
			
			if(allselect==true){
				arr[i].isSelect="noselect";
			}
			else{
				arr[i].isSelect="select";
			}
			$(".car-box-img").attr("src",img[arr[i].isSelect]);
		}
		if(allselect==true){
			allselect=false;
		}
		else{
			allselect=true;
		}
		flushData();
	});

	$(".img_select").click(function(){
		var _index=$(this).index('.img_select');
		if(arr[_index].isSelect=='noselect'){
			arr[_index].isSelect='select';
		}
		else{
			arr[_index].isSelect='noselect';
		}
		$(this).attr("src",img[arr[_index].isSelect]);
		flushData();

	});

	$(".u_sub").click(function(index){
		var _index=$(this).index('.u_sub');
		if(arr[_index].count==0){
			return;
		}
		arr[_index].count--;
		flushMoney();
		$(".u_value").eq(_index).html(arr[_index].count);
	});

	$(".u_add").click(function(index){
		var _index=$(this).index('.u_add');
		console.log("add"+_index)
		if(arr[_index].count>=1000){
			return;
		}
		arr[_index].count++;

		flushMoney();
		$(".u_value").eq(_index).html(arr[_index].count);
	});

	$(".car-buy-btn").click(function(){

		alert("去购买")

	});

})
function flushMoney	(){
	allMoney=0;
	for(let i=0;i<arr.length;i++){
		if(arr[i].isSelect=='select'){
			allMoney+=arr[i].count*arr[i].saleprice;
			$(".imgselect").eq(i).attr("src",img['select']);
		}
		$(".shoppingcar-item-right-money").eq(i).html("￥"+arr[i].count*arr[i].saleprice);
	}
	if(allselect==true){
		$(".car-box-img img").attr("src",img['select']);
	}
	else{
		$(".car-box-img img").attr("src",img['noselect']);
	}
	$(".resultMoney").html("￥"+allMoney);

}
function flushData(){
	allMoney=0;
	for(let i=0;i<arr.length;i++){
		if(arr[i].isSelect=='select'){
			allMoney+=arr[i].count*arr[i].saleprice;
			$(".imgselect").eq(i).attr("src",img['select']);
		}
		else{
			allselect=false;
			$(".imgselect").eq(i).attr("src",img['noselect']);
		}
		$(".shoppingcar-item-right-money").eq(i).html("￥"+arr[i].count*arr[i].saleprice);
	}
	if(allselect==true){
		$(".car-box-img img").attr("src",img['select']);
	}
	else{
		$(".car-box-img img").attr("src",img['noselect']);
	}
	$(".resultMoney").html("￥"+allMoney);

}