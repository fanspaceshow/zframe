var DataGridUtil = {
		LastSelectRow : null,//最后选中行
		ContentMenuClickHandler : ContentMenuClickHandler,//行右键菜单
		RowContentMenuShowHandler : rowContentMenuShowHandler,//行右键菜单执行方法
		Delete : function(url, datagridName, id){//执行删除
			dataGridDelete(url, datagridName, id);
		},
		HighShowSearch : highShowSearch,//高亮显示搜索关键词
		DoDataGridSearch : doDataGridSearch//执行搜索
};
function ContentMenuClickHandler(dataGridName,item) {
	try{
		dataGridName = "#" + dataGridName;
		if (DataGridUtil.LastSelectRow == null) {
			return;
		}
		if (item.name == "select") {
			if (item.text == "选中")
				$(dataGridName).datagrid("selectRow", DataGridUtil.LastSelectRow.index);
			else if (item.text == "取消选中")
				$(dataGridName).datagrid("unselectRow", DataGridUtil.LastSelectRow.index);
		} else if (item.name == "edit") {
			var withName = "doEditForContentMenu";
			var w = $(item.target).attr("with");
			if(w){
				withName += "_"+w;
			}
			eval(withName+"("+DataGridUtil.LastSelectRow.id+")");
		} else if (item.name == "remove") {
			var withName = "btnRemove";
			var w = $(item.target).attr("with");
			if(w){
				withName = "btnRemove";
			}
			dataGridDelete($("#"+withName).attr("deleteUrl"),dataGridName,DataGridUtil.LastSelectRow.id);
		} else if (item.name == "removeSelected") {
			var withName = "btnRemove";
			var w = $(item.target).attr("with");
			if(w){
				withName = "btnRemove";
			}
			dataGridDelete($("#"+withName).attr("deleteUrl"),dataGridName);
		}else{
			try{
				var param = new JSONObject();
				param.put("gridName", dataGridName);
				param.put("menuName", item.name);
				param.put("menuText", item.text);
				param.put("rowid", DataGridUtil.LastSelectRow.id);
				param.put("rowindex", DataGridUtil.LastSelectRow.index);
				eval(item.name+"_onClick("+param.toString()+");");
			}catch(e1){
				var oMenuDiv = $("div[name='"+item.name+"']");
				if(oMenuDiv.attr("with")){
				    $(dataGridName).datagrid("unselectAll");
				    $(dataGridName).datagrid("selectRow", DataGridUtil.LastSelectRow.index);
					$("#"+oMenuDiv.attr("with")).click();
				}
			}
		}
	}catch (e) {
		top.Dialog.alert("错误","脚本错误:"+e,"error");
	}
}
function rowContentMenuShowHandler(e,rowindex,rowData,dataGridName,contextMenuName) {
	try {
		e.preventDefault();
		DataGridUtil.LastSelectRow = new Object();
		DataGridUtil.LastSelectRow.id = rowData.id;
		DataGridUtil.LastSelectRow.index = rowindex;
		
		dataGridName = "#" + dataGridName;
		contextMenuName = "#" + contextMenuName;
		if (DataGridUtil.LastSelectRow == null) {
			return;
		}
		var rMenu = $(contextMenuName);
		var selectRows = $(dataGridName).datagrid("getSelections");
		var isSelected = false;
		for ( var i = 0; i < selectRows.length; i++) {
			var row = selectRows[i];
			if (row.id == DataGridUtil.LastSelectRow.id) {
				var itemEL = $(contextMenuName).find("div[name='select']");
				var item = rMenu.menu("getItem", itemEL);
				rMenu.menu("setText", {
					target : item.target,
					text : "取消选中"
				});
				rMenu.menu("setIcon", {
					target : item.target,
					iconCls : "icon-empty"
				});
				isSelected = true;
				break;
			}
		}
		if (!isSelected) {
			var itemEL = $(contextMenuName).find("div[name='select']");
			var item = rMenu.menu("getItem", itemEL);
			rMenu.menu("setText", {
				target : item.target,
				text : "选中"
			});
			rMenu.menu("setIcon", {
				target : item.target,
				iconCls : "icon-ok"
			});
		}
		//显示右键菜单
		$(contextMenuName).menu("show",{
			left:e.pageX,
			top:e.pageY
		});
	} catch (e) {
		top.Dialog.alert("错误","脚本错误:"+e,"error");
	}
}
function dataGridDelete(url,gridName,id){
	try {
		if(!gridName.startWith("#"))
			gridName = "#" + gridName;
		var ids = new Array();
		if(id!=undefined){
			ids.push(id);
		}else{
			var selectRows  = $(gridName).datagrid("getSelections");
			$.each(selectRows,function(i,n){
				ids.push(n.id);
			});
		}
		if(ids.length==0){
			 top.Notiy.warning("请选择删除项！");
		}else{
			var isCoutinue = true;
			//如果不是TreeGrid或者TreeGrid中没有选中父节点，则按照正常流程来删除
			if(isCoutinue){
				top.Dialog.confirm("提示","确定删除选中项吗?",function(r){
					if(r){
						$("#btnRemove").linkbutton("disable");
						//top.Dialog.progress();
						dataGridDeleteCallback(ids,url,gridName);
					}
				});
			}
		}
	} catch (e) {
		top.Dialog.alert("错误","脚本错误:"+e,"error");
	}
}
function dataGridDeleteCallback(ids,url,gridName,tag){
	$.post(url,{'ids':ids.toString()},function(data){
		if(data.NeedVerifyPassword){
			top.Dialog.verifyPass({
				callback:function(){
					dataGridDeleteCallback(ids,url,gridName,true);
				}
			});
		}else if(!data.success == true){
			top.Dialog.alert("错误",data.error,"error");
		}else{
			top.Notiy.success("删除成功!");
			$(gridName).datagrid("reload");
			DataGridUtil.LastSelectRow = null;
		}
		//top.Dialog.progress('close');
		$("#btnRemove").linkbutton("enable");
	},"json");
}

function highShowSearch(dg,name,value){
	var paramName = dg.datagrid("options").queryParams.name;
	var paramValue = dg.datagrid("options").queryParams.value;
	if(!paramName){
		if(TreeGridUtil.TreeGridSearchParam!= null && TreeGridUtil.TreeGridSearchParam.name){
			paramName = TreeGridUtil.TreeGridSearchParam.name;
			paramValue = TreeGridUtil.TreeGridSearchParam.value;
			if(paramName == name){
				return value.replace(paramValue,"<span style='color:red'>"+paramValue+"</span>");
			}else{
				return value;
			}
		}else{
			return value;
		}
	}else{
		if(paramName == name){
			return value.replace(paramValue,"<span style='color:red'>"+paramValue+"</span>");
		}else{
			return value;
		}
	}
}
function doDataGridSearch(datagridid,paramName,paramValue){
	var queryParam = $("#"+datagridid).datagrid("options").queryParams;
	var jParam = new JSONObject();
	//遍历表格查询属性
	$.each(queryParam,function(i,n){
		jParam.put(i, n);
	});
	jParam.put("name", paramName);
	jParam.put("value", paramValue);
	$("#"+datagridid).datagrid("load",jParam.toJSON());
}
//获取未选中的行
DataGridUtil.getUnSelectedRows = function(datagrid){
	var rows = datagrid.datagrid("getRows");
	var selectedRows = datagrid.datagrid("getSelections");
	var unSelectedRows = new Array();
	$.each(rows,function(i,row){
		if(!DataGridUtil._ifIncludeRow(selectedRows,row)){
			unSelectedRows.push(row);
		}
	});
	return unSelectedRows;
};
DataGridUtil._ifIncludeRow = function(rows,row){
	for(var i=0;i<rows.length;i++){
		if(rows[i].id == row.id){
			return true;
		}
	}
	return false;
};
/*---------DataGrid右键菜单----------*/
//创建
DataGridUtil.createHeadContextMenu = function(datagridName,win){
	//判断是否已经存在
	if(DataGridUtil.hasHeadContextMenu(datagridName,win))
		return;
	var tmenu = $('<div id="'+datagridName+'_headMenu" style="width:100px;"></div>').appendTo('body');
	var dg = null;
	if(win){
		dg = win.find("#"+datagridName);
	}else{
		dg = $('#'+datagridName);
	}
	//包含固定列
	/*var frozenFields = dg.datagrid('getColumnFields',true);
	$.each(frozenFields,function(i,n){
		var fieldOptions = dg.datagrid("getColumnOption",n);
		if(fieldOptions.title){
			var iconCls = "icon-ok"
			if(fieldOptions.hidden){
				iconCls = "icon-blank";
			}
			$("<div iconCls='"+iconCls+"' field='"+n+"'/>").html(dg.datagrid("getColumnOption",n).title).appendTo(tmenu);
		}
	});*/
	var fields = dg.datagrid('getColumnFields');
    $.each(fields,function(i,n){
		var fieldOptions = dg.datagrid("getColumnOption",n);
		var iconCls = "icon-ok";
		if(fieldOptions.hidden){
			iconCls = "icon-blank";
		}
		$("<div iconCls='"+iconCls+"' field='"+n+"'/>").html(dg.datagrid("getColumnOption",n).title).appendTo(tmenu);
	});
	tmenu.menu({
		onClick: function(item){
			var field = $(item.target).attr("field");
			if (item.iconCls=='icon-ok'){
				dg.datagrid('hideColumn', field);
				tmenu.menu('setIcon', {
					target: item.target,
					iconCls: 'icon-empty'
				});
			} else {
				dg.datagrid('showColumn', field);
				tmenu.menu('setIcon', {
					target: item.target,
					iconCls: 'icon-ok'
				});
			}
			DataGridUtil.saveDataGridColumnConfig(datagridName,win);
		}
	});
};
//判断是否存在
//显示
DataGridUtil.showHeadContextMenu = function(datagridName,e,win){
	if(win){
		win.find("#"+datagridName+"_headMenu").menu("show",{
			left:e.pageX,
			top:e.pageY
		});
	}else{
		$("#"+datagridName+"_headMenu").menu("show",{
			left:e.pageX,
			top:e.pageY
		});
	}
};
//初始化表格列的显示和隐藏
DataGridUtil.initDataGridColumn = function(datagridName,win){
	var savedConfig = getCookie(top.Home.BasePath+"_"+datagridName + "_column_cookie");
	var dg = null;
	if(win){
		dg = win.find("#"+datagridName);
	}else{
		dg = $("#"+datagridName);
	}
	if(savedConfig){//如果存在，加载配置信息
		savedConfig = savedConfig.replace("%2C",",");
		var allColumns = dg.datagrid('getColumnFields');
		$.each(allColumns,function(i,col){
			dg.datagrid('showColumn', col);
		});
		if(savedConfig != "isEmpty_3366"){
			var columns = savedConfig.split(",");
			$.each(columns,function(i,col){
				dg.datagrid('hideColumn', col);
			});
        }
	}else{//如果不存在，则保存当前的配置信息
		DataGridUtil.saveDataGridColumnConfig(datagridName,win);
	}
};
//保存配置到cookie
DataGridUtil.saveDataGridColumnConfig = function(datagridName,win){
	var savedConfig = getCookie(top.Home.BasePath+"_"+datagridName + "_column_cookie");
	var dg = null;
	if(win){
		dg = win.find("#"+datagridName);
	}else{
        dg = $("#"+datagridName);
	}
	var columns = dg.datagrid('getColumnFields');
	var hideColumns = new Array();
	$.each(columns,function(i,col){
		var columnOptions = dg.datagrid("getColumnOption",col);
		if(columnOptions.hidden){
			hideColumns.push(col);
		}
	});
	if(hideColumns.length>0)
		savedConfig = hideColumns.toString();
	else
		savedConfig = "isEmpty_3366";
	setCookie(top.Home.BasePath+"_"+datagridName + "_column_cookie", savedConfig, "365d");
};
//导出Excel
DataGridUtil.exportExcel = function(dg,entityName){
	var exportWin = new Windows("sys_export_excel");
	//如果列过多，则自动增加高度
	var addHeight = 0;
	var winParam = new JSONObject();
	//获取选中行
	var selectedRows = dg.datagrid("getSelections");
	if(selectedRows.length>0){
		winParam.put("exportType","selected");
		var selectedIds = new Array();
		$.each(selectedRows,function(i,row){
			selectedIds.push(row.id);
		});
		winParam.put("selectedIds",selectedIds.toString());
	}else{
		winParam.put("exportType","all");
	}
	if(!dg.datagrid("options").view.name){
		//获取所有列
		var fColumns = dg.datagrid("getColumnFields",true);//固定列
		var columns = dg.datagrid('getColumnFields');//数据列
		var exportColumns = new JSONObject();
		var showColumns = new Array();//获取显示的行，默认导出显示的行
		$.each(fColumns,function(i,col){
			var columnOptions = dg.datagrid("getColumnOption",col);
			var columnTitle = columnOptions.title;
			if(columnTitle){
				exportColumns.put(col,columnTitle);
				if(!columnOptions.hidden)
					showColumns.push(col);
			}
		});
		$.each(columns,function(i,col){
			var columnOptions = dg.datagrid("getColumnOption",col);
			var columnTitle = columnOptions.title;
			if(columnTitle){
				exportColumns.put(col,columnTitle);
				if(!columnOptions.hidden)
					showColumns.push(col);
			}
		});
		winParam.put("columns", exportColumns);
		winParam.put("showColumns", showColumns);
		//获取分页信息
		var pager = dg.datagrid("getPager");
		var pageNo = pager.pagination("options").pageNumber;
		var pageSize = pager.pagination("options").pageSize;
		var pageInfo = new JSONObject();
		pageInfo.put("pageNo", pageNo);
		pageInfo.put("pageSize",pageSize);
		winParam.put("pager", pageInfo);
		//设置实体信息
		winParam.put("entityClass",entityName);
		
		//自动变更高度
		if(exportColumns.size() > 10){
			var newCount = exportColumns.size() - 10;
			if(newCount % 5 == 0){
				addHeight = (newCount / 5) * 20;
			}else{
				addHeight = ((newCount / 5)+1) * 20;
			}
		}
		exportWin.windows({
			title:'导出Excel',
			width:500,
			height:250 + addHeight,
			modal:true,
			cache:false,
			closed:true,
			iconCls:'icon-export-excel',
			href:top.Home.BasePath+"/admin/exportExcel",
			onClose:function(){
				
			}
		});
		//将值绑定到窗口上
		exportWin.setParam(winParam.toString());
		//打开窗口
		exportWin.open();
	}else if(dg.datagrid("options").view.name == "scrollView"){
		//获取所有列
		var columns = dg.datagrid('getColumnFields');//数据列
		var exportColumns = new JSONObject();
		var showColumns = new Array();//获取显示的行，默认导出显示的行
		$.each(columns,function(i,col){
			var columnOptions = dg.datagrid("getColumnOption",col);
			var columnTitle = columnOptions.title;
			if(columnTitle){
				exportColumns.put(col,columnTitle);
				if(!columnOptions.hidden)
					showColumns.push(col);
			}
		});
		winParam.put("columns", exportColumns);
		winParam.put("showColumns", showColumns);
		//获取分页信息
		var pager = dg.datagrid("options").view.pager;
		var pageNo = pager.pageNumber;
		var pageSize = pager.pageSize;
		var pageInfo = new JSONObject();
		pageInfo.put("pageNo", pageNo);
		pageInfo.put("pageSize",pageSize);
		winParam.put("pager", pageInfo);
		//设置实体信息
		winParam.put("entityClass",entityName);
		
		//自动变更高度
		if(exportColumns.size() > 10){
			var newCount = exportColumns.size() - 10;
			if(newCount % 5 == 0){
				addHeight = (newCount / 5) * 20;
			}else{
				addHeight = ((newCount / 5)+1) * 20;
			}
        }
		exportWin.windows({
			title:'导出Excel',
			width:500,
			height:250 + addHeight,
			modal:true,
			cache:false,
			closed:true,
			iconCls:'icon-export-excel',
			href:top.Home.BasePath+"/admin/exportExcel",
			onClose:function(){
				
			}
		});
		//将值绑定到窗口上
		exportWin.setParam(winParam.toString());
		//打开窗口
		exportWin.open();
	}
};
/**
 * 获取DataGrid选中的ID集合
 */
DataGridUtil.getSelectionsIds = function(dg){
	var selectedRows = dg.datagrid("getSelections");
	var ids = new Array();
	$.each(selectedRows,function(i,row){
		ids.push(row.id);
	});
	return ids;
};
/**
 * 获取DataGrid选中的ID
 */
DataGridUtil.getSelectionsId = function(dg){
	var selectRow = dg.datagrid("getSelected");
	if(!selectRow){
		return null;
	}else{
		return selectRow.id;
	}
};