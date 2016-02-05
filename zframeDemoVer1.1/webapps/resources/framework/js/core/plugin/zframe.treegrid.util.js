var TreeGridUtil = {
		LastSelectRow : null,//最后选中行
		TreeGridSearchParam : null,
		TreeGridContentMenuClickHandler : TreeGridContentMenuClickHandler,//树形表格右键菜单
		TreeGridRowContentMenuShowHandler : treeGridRowContentMenuShowHandler,//属性表格右键菜单点击事件
		Delete : function(url, treegridName, id){//属性菜单删除
			treeGridDelete(url, treegridName, id);
		},
		DoTreeGridSearch : doTreeGridSearch//搜索
};
//treegrid行右键菜单
function TreeGridContentMenuClickHandler(treeGridName,item){
	try {
		treeGridName = "#" + treeGridName;
		if (TreeGridUtil.LastSelectRow == null) {
			return;
		}
		if (item.name == "select") {
			if (item.text == "选中")
				$(treeGridName).treegrid("select", TreeGridUtil.LastSelectRow.id);
			else if (item.text == "取消选中")
				$(treeGridName).treegrid("unselect", TreeGridUtil.LastSelectRow.id);
		} else if (item.name == "edit") {
			var withName = "doEditForContentMenu";
			var w = $(item.target).attr("with");
			if(w){
				withName += "_"+w;
			}
			eval(withName+"("+TreeGridUtil.LastSelectRow.id+")");
		} else if (item.name == "remove") {
			var withName = "btnRemove";
			var w = $(item.target).attr("with");
			if(w){
				withName = "btnRemove";
			}
			treeGridDelete($("#"+withName).attr("deleteUrl"),treeGridName,TreeGridUtil.LastSelectRow.id);
		} else if (item.name == "removeSelected") {
			var withName = "btnRemove";
			var w = $(item.target).attr("with");
			if(w){
				withName = "btnRemove";
			}
			$("#"+withName).click();
		}else{
			var param = new JSONObject();
			param.put("grid", treeGridName);
			param.put("gridName", treeGridName);
			param.put("menuName", item.name);
			param.put("menuText", item.text);
			param.put("rowid", TreeGridUtil.LastSelectRow.id);
			
			eval(item.name+"_onClick("+param.toString()+");");
		}
	} catch (e) {
		top.Dialog.alert("错误","脚本错误:"+e,"error");
	}
}
//treegrid 右键菜单事件
function treeGridRowContentMenuShowHandler(e,crow,treeGridName,contextMenuName){
	try {
		e.preventDefault();//取消浏览器自带右键事件
		TreeGridUtil.LastSelectRow = new Object();
		TreeGridUtil.LastSelectRow.id = crow.id;
		treeGridName = "#" + treeGridName;
		contextMenuName = "#" + contextMenuName;
		
		var rMenu = $(contextMenuName);
		var selectRows = $(treeGridName).treegrid("getSelections");
		var isSelected = false;
		for ( var i = 0; i < selectRows.length; i++) {
			var row = selectRows[i];
			if (row.id == TreeGridUtil.LastSelectRow.id) {
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
function treeGridDelete(url,gridName,id){
	try {
		if(!gridName.startWith("#"))
			gridName = "#" + gridName;
		var ids = new Array();
		if(id!=undefined){
			ids.push(id);
		}else{
			var selectRows = $(gridName).treegrid("getSelections");
			$.each(selectRows,function(i,n){
				ids.push(n.id);
			});
		}
		if(ids.length==0){
			 top.Notiy.warning("请选择删除项！");
		}else{
			var isCoutinue = true;
			//如果是树形表格TreeGrid，则判断选中项是否包含有父级节点
			var hasParentRes = false;
			if(selectRows != null){//如果是多选删除
				for(var i=0;i<selectRows.length;i++){
					var selectRow = selectRows[i];
					var childRows = $(gridName).treegrid("getChildren",selectRow.id);
					if(childRows.length>0){
						hasParentRes = true;
						break;
					}
				}
			}else{//如果是单个删除
				var childRows = $(gridName).treegrid("getChildren",ids[0]);
				if(childRows.length>0){
					hasParentRes = true;
				}
			}
			if(hasParentRes){
				//改变标示下面不需要在进行删除操作
				isCoutinue = false;
				top.Dialog.confirm("提示","删除父级资源时，子资源将一起删除，是否继续?",function(r){
					if(r){
						$("#btnRemove").linkbutton("disable");
						//top.Dialog.progress();
						treeGridDeleteCallback(ids,url,gridName);
					}
				});
			}
			//如果不是TreeGrid或者TreeGrid中没有选中父节点，则按照正常流程来删除
			if(isCoutinue){
				top.Dialog.confirm("提示","确定删除选中项吗?",function(r){
					if(r){
						$("#btnRemove").linkbutton("disable");
						//top.Dialog.progress();
						treeGridDeleteCallback(ids,url,gridName);
					}
				});
			}
		}
	} catch (e) {
		top.Dialog.alert("错误","脚本错误:"+e,"error");
	}
}
function treeGridDeleteCallback(ids,url,gridName){
	$.post(url,{'ids':ids.toString()},function(data){
		if(data.NeedVerifyPassword){
			top.Dialog.verifyPass({
				callback:function(){
					treeGridDeleteCallback(ids,url,gridName);
				}
			});
		}else if(!data.success == true){
			top.Dialog.alert("错误",data.error,"error");
		}else{
			top.Notiy.success("删除成功!");
			$(gridName).treegrid("reload");
		}
		//top.Dialog.progress('close');
		$("#btnRemove").linkbutton("enable");
	},"json");
}
/**
 * TreeGrid查询
 * 
 * @param datagridid
 * @param url
 * @param paramName
 * @param paramValue
 * @return
 */
function doTreeGridSearch(datagridid,url,paramName,paramValue){
	var queryParam = $("#"+datagridid).treegrid("options").queryParams;
	var jParam = new JSONObject();
	//遍历表格查询属性
	$.each(queryParam,function(i,n){
		jParam.put(i, n);
	});
	jParam.put("name", paramName);
	jParam.put("value", paramValue);
	TreeGridUtil.TreeGridSearchParam = jParam.toJSON();
	$.post(url,jParam.toJSON(),function(data){
		$("#"+datagridid).treegrid("loadData",data);
	},"json");
}