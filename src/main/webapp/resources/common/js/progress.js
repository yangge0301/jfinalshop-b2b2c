var Progress = function(obj) {
	this.container = obj.container;
	this.color = obj.color;
	this.precent = obj.precent;

	// 创建结构
	this.initProgress();
	
}

Progress.prototype.initProgress = function() {
	var box = $("<div class='taskBoxLinks'></div>");
	var width = this.precent * 100 + "%";
	box.append('<h3 style="width:' + width + ';background:'+this.color+'"></h3><h4></h4>');
	var that = $("#" + this.container);
	$(that).append(box);
	$(that).append('进度<span>' + width + '</span>');
	$(that).addClass("taskBox");
	$(that).delegate('.taskBoxLinks', 'mousemove', this.mousemove);
	$(that).delegate('.taskBoxLinks', 'mouseleave', this.mouseleave);
	$(that).delegate('.taskBoxLinks', 'click', this.click);

}

Progress.prototype.mousemove = function(e) {
	console.log(this); 
	var $mouse = e.pageX - $(this).offset().left;
	var $span = Math.round($mouse / 22.5) * 10;
	$(this).find('h4').stop().animate({
		width : $span + '%'
	}, 50);
	$(this).next('span').text($span + '%');
}

Progress.prototype.mouseleave = function() {
	$(this).find('h4').stop().animate({
		width : '10%'
	}, 50);
	var $mousex = $(this).find('h3').width();
	var $spanx = Math.round($mousex / 22.5) * 10;
	if ($spanx == 100) {
		$(this).next('span').text('完成')
	} else {
		$(this).next('span').text($spanx + '%');
	}
}

Progress.prototype.click = function(e) {
	var $mouse = e.pageX - $(this).offset().left;
	var $span = Math.round($mouse / 22.5) * 10;
	$(this).find('h3').stop().animate({
		width : $span + '%'
	}, 100);
	if ($span == 100) {
		$(this).next('span').text('完成')
	}
	$(this).attr("value",$span/100);
}

Progress.prototype.getValue=function(){ 
	this.precent=$("#"+this.container+" .taskBoxLinks").eq(0).attr("value")||this.precent;
	return this.precent*100;
}