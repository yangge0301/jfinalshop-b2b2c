/**
 *  add by billJiang 2017/1/5
 *  tableTool 扩展dataTables操作
 *  目前只用于rolefunc_select中的数据权限操作
 *  not used  （）
 */
(function($, window, document, undefined) {
	'use strict';

	var pluginName = 'tableTool';

	$.fn[pluginName] = function(options, args) {
		if(this==null)
			return null;
		return new TableTool(this, $.extend(true, {},options));
	};


	var TableTool=function(element,options){
		this.$element=$(element);
		if(!options.table){
			modals.warn("请指定表格");
			return;
		}
		this.table=options.table;
	}

	TableTool.prototype.getTableObject=function(){
		return this.table;
	}

	//新增一行数据
	TableTool.prototype.addRow=function () {
       //this.table.table.$("tbody").append("<tr><td colspan='5'>hello world</td></tr>");
	}

	//删除一行数据
	TableTool.prototype.deleteRow=function(){
        this.table.table.row("tr.selected").remove().draw(false);
	}







})(jQuery, window, document);


