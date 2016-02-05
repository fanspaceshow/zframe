$.fn.datagrid.defaults.striped = true;
//改写默认加载失败方法
$.fn.datagrid.defaults.onLoadError = function(){
	top.Notiy.error("数据加载失败!");
};
//分页组件
$.fn.pagination.defaults.layout=["list","sep","first","prev","links","next","last","refresh","manual"];
$.fn.datagrid.defaults.pageSize = top.Config.DataGrid.PageSize;
//datagrid中新的编辑器
$.extend($.fn.datagrid.defaults.editors, {   
	progressbar: {   
		init: function(container, options){   
			var bar = $('<div/>').appendTo(container);
			bar.progressbar(options); 				
			return bar;   
		},   
		getValue: function(target){   
			return $(target).progressbar('getValue');   
		},
		setValue: function(target, value){   
			$(target).progressbar('setValue',value);   
		},
		resize: function(target, width){    
			if ($.boxModel == true){   
				$(target).progressbar('resize',width - (input.outerWidth() - input.width()));
			} else {   
				$(target).progressbar('resize',width);
			}   
		}   
	},
	slider: {   
		init: function(container, options){   
			var slider = $('<div/>').appendTo(container);
			slider.slider(options); 				
			return slider;   
		},   
		getValue: function(target){   
			return $(target).slider('getValue');   
		},
		setValue: function(target, value){   
			$(target).slider('setValue',value);   
		},
		resize: function(target, width){    
			if ($.boxModel == true){   
				$(target).progressbar('slider',{width:width - (input.outerWidth() - input.width())});
			} else {   
				$(target).progressbar('slider',{width:width});
			}   
		}   
	},
	numberspinner:{
		init: function(container, options){   
			var numberspinner = $('<input/>').appendTo(container);
			numberspinner.numberspinner(options); 				
			return numberspinner;
		},   
		getValue: function(target){   
			return $(target).numberspinner('getValue');   
		},
		setValue: function(target, value){   
			$(target).numberspinner('setValue',value);   
		},
		resize: function(target, width){
			if ($.boxModel == true){   
				$(target).spinner('resize',width - (input.outerWidth() - input.width()));
			} else {   
				$(target).spinner('resize',width);
			}   
		}  
	}
}); 
//扩展方法
$.extend($.fn.datagrid.methods, { 
	//删除选中行
	deleteSelectedRow : function(jq){
		var selectedRowIndexs = jq.datagrid("getSelectedRowIndexs");
		for(var index in selectedRowIndexs){
			jq.datagrid("deleteRow",index);
		}
	},
	getSelectedRowIndexs : function(jq){
		var selectedRows = jq.datagrid("getSelections");
		var indexs = new Array();
		if(selectedRows.length>0){
			for(var i=0;i<selectedRows.length;i++){
				var selectedRow = selectedRows[i];
				indexs.push(jq.datagrid("getRowIndex",selectedRow));
			}
		}
		return indexs;
	},
	getSelectedRowId : function(jq){
		var selectedRow = jq.datagrid("getSelected");
		return selectedRow.id;
	},
	getSelectedRowIds : function(jq){
		var selectedRows = jq.datagrid("getSelections");
		var ids= new Array();
		if(selectedRows.length>0){
			for(var i=0;i<selectedRows.length;i++){
				var selectedRow = selectedRows[i];
				ids.push(selectedRow.id);
			}
		}
		return ids;
	},
	getRow : function(jq,idx){
		return jq.datagrid("getRows")[idx];
	}
});