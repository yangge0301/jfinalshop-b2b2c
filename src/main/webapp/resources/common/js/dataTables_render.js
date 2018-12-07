//格式化日期
function fnRenderPackage(b,type,rowObj,oSettings){
	alert(b+"type:"+type);  
	console.log(JSON.stringify(oSettings));
	return b; 
}

function fnRenderDate(value,type,rowObj,oSetting){ 
	console.log(this);
	console.log("---------------------------------------------"); 
	console.log(value+"--->"+type+"--->"+JSON.stringify(rowObj)+"--->"+JSON.stringify(oSetting));
	return value+"hello world";	
}

