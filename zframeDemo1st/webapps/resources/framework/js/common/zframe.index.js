var Home = {
		BasePath:"",
		BackHome:backHome,//返回桌面
		BackLastNav:function(){//返回上次打开的导航菜单
			//读取上次打开导航菜单
			var oUrl = getCookie(top.Home.BasePath.replace("/","")+"_FrameUrl");
			var oIcon = getCookie(top.Home.BasePath.replace("/","")+"_FrameIcon");
			if(!oUrl){
				backHome();
				return;
			}
			var oTitle = "桌面";
			$.each($("#navMenu").find("li"),function(i,li){
				if($(li).find("a[src]").attr("src") == oUrl){
					oTitle = $(li).find("a[src]").text();
				}
			});
			//Navigate(oUrl,oTitle,oIcon);
		},
		InitFrameHeight:autoHeight,//自动计算高度
		InitNavMenu:function(){//初始化导航菜单
			$("#navMenu").find("li").click(function(){
				//设置所有a标签的字体样式为默认
				$.each($("#navMenu").find("li"),function(i,li){
					$(li).find("a[src]").css("fontWeight","normal");
					//$(li).find("a[src]").css("color","#333");
				});
				var a = $(this).find("a[src]");
				a.css("fontWeight","bold");
				//a.css("color","#FFFFFF");
				//设置样式
				$.each($("#navMenu").find("li"),function(i,li){
					li.className = "nav-default";
				});
				$(this)[0].className = "nav-selected";
				Navigate(a.attr("src"), a.text(),a.attr("class"));
			});
		},
		Navigate:Navigate,//导航
		InitThemes:initThemes,//初始化皮肤
		ThemeName : "default",//皮肤名称
		InitKeyboardEvent:initKeyboardEvent,//初始化键盘事件
		Exit:exit,//退出系统
		FrameReload:reload,//重新加载iframe
		ChangePwd:changePwd,//修改密码
		WindowsPool:{},//窗口对象缓存池
		FontSize:"12px",
		SetFontSize:function(type,fontSize){//设置字体大小
			return;
			if(type){
				if(type == "showWin"){
					var oft = $(fontSize).offset();
					$("#winFontSize").window({left:oft.left+20,top:oft.top});
					$("#winFontSize").window("open");
				}else if(type == "setFontSize"){
					top.Home.FontSize = fontSize + "px";
				}
			}else{
				var fontSize = top.Home.FontSize;
				var c1 = ".panel-body,.panel-title,.combo .combo-text,.combobox-item,.combobox-group,.tabs li a.tabs-close,.tabs-p-tool,.tabs li a.tabs-close,";
				var c2 = ".tabs-p-tool a,.tabs-titl,.l-btn-text,.datagrid-cell,.datagrid-cell-group,.datagrid-header-rownumber,.datagrid-cell-rownumber,";
				var c3 = ".datagrid-header .datagrid-cell span,.pagination-info,.pagination span,.calendar table td,.calendar table th,.calendar-title span,";
				var c4 = ".calendar-menu,.calendar-menu-year,.datebox-button a,.spinner .spinner-text,.progressbar-text,.searchbox .searchbox-text,.searchbox .searchbox-prompt,";
				var c5 = ".slider-tip,.slider-rulelabel span,.menu-text span,.tree-title,.tree-node-proxy,.tree-editor,.tooltip-content";
				$(c1+c2+c3+c4+c5).css("font-size",fontSize);
				try{
					top.Frame.window.$(c1+c2+c3+c4+c5).css("font-size",fontSize);
				}catch(e){
				}
				setTimeout(function(){top.Home.SetFontSize();},50);
			}
		},
		FrameOut : function(){
			if(top.Frame.FrameUrl != ""){
				var win = new Windows("out_"+top.Frame.FrameUrl.replaceAll("/","_"));
				win.windows({
					width:top.$("#divFrame").width(),
					height:top.$("#divFrame").height()+30,
					iconCls:top.Frame.FrameIcon,
					title : top.Frame.FrameTitle,
					isFrameOut:true,
					href : top.Home.BasePath+"/admin/index/frameOut?url="+top.Frame.FrameUrl+"&width="+top.$("#divFrame").width()+"&height="+(top.$("#divFrame").height()),
					onClose:function(){
						win.setAutoClear(true);
					}
				});
				win.setAutoClear(false);
				win.open();
			}
		},
		resizeTabs : function(t){
			$("#mainTabs>.tabs-header,#mainTabs>.tabs-panels,#mainTabs>.tabs-panels>.panel,#mainTabs>.tabs-panels>.panel>.panel-body").width($("#mainTabs").width()+"px");
			$("#mainTabs>.tabs-panels,#mainTabs>.tabs-panels>.panel,#mainTabs>.tabs-panels>.panel>.panel-body").height(($("#mainTabs").height()-30)+"px");
		}
};
var Frame = {
	FrameHeight:0,
	FrameTitle:"桌面",
	FrameUrl:"",
	FrameIcon:"icon-home",
	window:null,
	document:null
};
$(document).ready(function(){
	//保存项目的BasePath
	top.Home.BasePath = $("#basePath").val();
	$("#frameContent").load(function() {
		autoHeight();
	});
	$(window).resize = null;
	$(window).resize(function(){
		$("#frameContent").height($(window).height()-top.Frame.FrameHeight);
		setTimeout(top.Home.resizeTabs,200);	
	});
	//绑定Iframe内部Dom对象
	top.Frame.window = $("#frameContent")[0].contentWindow;
	//根据浏览器设置iframe高度
	var bro = jQuery.browser;
	if (bro.msie) {// IE浏览器
		if (bro.version == "6.0" || bro.version == "7.0") {//IE6.0 7.0
			autoHeight();
		}
	}
	//初始化导航菜单
	top.Home.InitNavMenu();
	if(Config.Home.RememberLastNav){
		top.Home.BackLastNav();
	}else{
		//显示桌面
		top.Home.BackHome();
	}
	//初始化选中的皮肤
	top.Home.InitThemes();
	//初始化键盘事件
	top.Home.InitKeyboardEvent();
	
	//重写$.ajax方法，添加错误提示
	var _ajax = $.ajax;
	$.ajax = function(opt){
		if(!opt.error){
			opt.error = function(XMLHttpRequest, textStatus, errorThrown){
				if(XMLHttpRequest.status == 400){
					top.Notiy.error("请求出现语法错误!");
				}else if(XMLHttpRequest.status == 401){
					top.Notiy.error("您的访问被拒绝!");
				}else if(XMLHttpRequest.status == 401){
					top.Notiy.error("您的访问被拒绝!");
				}else if(XMLHttpRequest.status == 403){
					top.Notiy.error("请求的资源不可用!");
				}else if(XMLHttpRequest.status == 404){
					top.Notiy.error("没有找到文件或目录!");
				}else if(XMLHttpRequest.status == 405){
					top.Notiy.error("请求方法类型错误(GET/POST)!");
				}else if(XMLHttpRequest.status == 408){
					top.Notiy.error("请求超时，请重新登陆!");
				}else if(XMLHttpRequest.status == 500){
					top.Notiy.error("服务器出错，请联系管理员!");
				}else if(XMLHttpRequest.status == 503){
					top.Notiy.error("服务器维护中....");
				}else{
					top.Notiy.error("请求出错，请稍后重试!");
				}
			};
		}
		_ajax(opt);
	};
});
function autoHeight() {
	var contentHeight = $("#frameContent").contents().height();
	var parentContentHeight = $("#divFrame").height();
	top.Frame.FrameHeight = $(window).height() - parentContentHeight;
	var bro = jQuery.browser;
	if (bro.msie) {// IE浏览器
		if (bro.version == "6.0" || bro.version == "7.0") {//IE6.0 7.0
			//IE6、7
		} else if(bro.version == "8.0" || bro.version == "9.0"){//IE8 IE9
			parentContentHeight -= 2;
			//IE8、9
		}else{
			//IE10+
			parentContentHeight -=1;
		}
	} else if(bro.webkit || bro.chrome){//Webkit内核浏览器
		//谷歌浏览器
	}else if(bro.mozilla && parseInt(bro.version) >=15){//火狐浏览器
		//火狐浏览器
	}else{
		parentContentHeight-=1;
	}
	top.Frame.FrameHeight = $(window).height() - parentContentHeight;
	$("#frameContent").height(parentContentHeight);
	onFrameLoaded();
}
function createFrame(oUrl){
	return "<iframe id=\"frameContent\" src=\""+oUrl+"\" frameborder=\"0\" width=\"100%\" height=\"100%\" scrolling=\"auto\" marginheight=\"0\" marginwidth=\"0\" frameSpacing=\"0\"></iframe>"
}
function Navigate(oUrl,oTitle,oIcon,tag) {
	if (oUrl != $("#frameContent").attr("src") || tag==1) {
		var idx = -1;
		var tabs = $('#mainTabs').tabs("tabs");
		$.each(tabs, function(i,n){
			if(n.find("iframe").attr("src") == oUrl)
				idx = i;
		});
		if(idx ==-1){
			$('#mainTabs').tabs('add',{
				title:oTitle,
				content:createFrame(oUrl),
				closable:true,
				icon:oIcon
			});
		}else{
			var o_idx = $('#mainTabs').tabs("getTabIndex",$('#mainTabs').tabs("getSelected"));
			$('#mainTabs').tabs("select", idx);
			if(o_idx == idx){
				var c_tab = $("#mainTabs").tabs("getTab", idx);
				c_tab.find("iframe")[0].contentWindow.location.reload();
			}
		}
		//设置样式
		//$("#home").panel("header").find(".panel-icon").attr("class","panel-icon "+oIcon);
		//$("#home").panel("setTitle",oTitle);
		//$("#frameContent").attr("src", oUrl);
		//document.getElementById("frameContent").src = oUrl;
		top.Frame.FrameTitle = oTitle;
		top.Frame.FrameUrl = oUrl;
		top.Frame.FrameIcon = oIcon;
		top.Frame.window = $('#mainTabs').tabs('getSelected').find("iframe")[0].contentWindow;
		top.Frame.document = $('#mainTabs').tabs('getSelected').find("iframe")[0].contentWindow.document;
		$.each($("#navMenu").find("li"),function(i,li){
			if($(li).find("a[src]").attr("src") == oUrl){
				$(li).find("a[src]").css("fontWeight","blod");
				//$(li).find("a[src]").css("color","#FFFFFF");
				li.className = "nav-selected";
				//选择所属的accordion
				var accordionTitle = $(li).parent().parent().parent().find(".panel-header>.panel-title").text();
				$("#navMenu").accordion("select",accordionTitle);
			}else{
				$(li).find("a[src]").css("fontWeight","normal");
				//$(li).find("a[src]").css("color","#333");
				li.className = "nav-default";
			}
		});
		//存入cookie
		setCookie(top.Home.BasePath.replace("/","")+"_FrameUrl", oUrl);
		setCookie(top.Home.BasePath.replace("/","")+"_FrameIcon", oIcon);
		//清理打开过的窗口
		var windows = $("#FrameWorkWindows").find("input");
		for(var i=0;i<windows.length;i++){
			var id = windows[i].id.substring(windows[i].id.indexOf("_")+1);
			if($("#"+id).attr("AutoClear") == "true"){
				$("#RV_"+id).remove();
				$("#PARAM_"+id).remove();
				$("#"+id).window('destroy',false);
				Home.WindowsPool[id] = undefined;
			}
		}
		//设置字体大小
		if(typeof top.Home.SetFontSize == "function"){
			//top.Home.SetFontSize();
		}
		//设置动态图标
		var $pHead = $("#centerDiv").prev();
		$pHead.find("#frameReload").attr("class","icon-loading2");
		setTimeout(onFrameLoaded, 4000);
	}
}
function onFrameLoaded(){
	var $pHead = $("#centerDiv").prev();
	$pHead.find("#frameReload").attr("class","icon-reload");
	//设置字体大小
	if(typeof top.Home.SetFontSize == "function"){
		top.Home.SetFontSize();
	}
}
function exit(){
	Dialog.confirm("提示信息","确定退出系统吗?",function(r){
		if(r)
			window.location.href= top.Home.BasePath+"/admin/login/loginOut";
	});
}
function reload(){
	Navigate(top.Frame.FrameUrl,top.Frame.FrameTitle,top.Frame.FrameIcon,1);
}
function navHover(obj){
	if(obj.className != "nav-selected"){
		$.each($("#navMenu").find("li"),function(i,li){
			if(li.className != "nav-selected"){
				li.className = "nav-default";
				$(li).find("a[src]").css("fontWeight","normal");
				//$(li).find("a[src]").css("color","#333");
			}
		});
		//设置当前为
		obj.className = "nav-hover";
		$(obj).find("a[src]").css("fontWeight","normal");
		//$(obj).find("a[src]").css("color","blue");
	}
}
function navOut(obj){
	if(obj.className != "nav-selected"){
		$.each($("#navMenu").find("li"),function(i,li){
			if(li.className != "nav-selected"){
				li.className = "nav-default";
				$(li).find("a[src]").css("fontWeight","normal");
				//$(li).find("a[src]").css("color","#333");
			}
		});
	}
}
function initThemes(){
	var mainStyle = $("#mainStyle");
	var oStyle = mainStyle.attr("href");
	var currentTheme = oStyle.substring(oStyle.indexOf("easyui/")+7).replace("/easyui.css","");
	$.each($("#themeList").children(),function(i,n){
		var oDiv = $(n);
		if(oDiv.attr("themeName") == currentTheme){
			$("#themeList").menu("setIcon",{
				target:n,
				iconCls:'icon-ok'
			});
		}else{
			$("#themeList").menu("setIcon",{
				target:n,
				iconCls:'icon-blank'
			});
		}
	});
}
/**
 * 更换皮肤
 * @param item
 */
function changeThemes(item){
	var jItem = $(item.target);
	var themeName = jItem.attr("themeName");
	//同步到服务器
	$.post(top.Home.BasePath+"/admin/index/changeThemes",{"themeName":themeName},function(data){
		if(data.result == "success"){
			var mainStyle = $("#mainStyle");
			var oldStyle = mainStyle.attr("href");
			var newStyle = oldStyle.substring(0,oldStyle.indexOf("easyui/"))+"easyui/"+themeName+"/easyui.css";
			mainStyle.attr("href",newStyle);
			$.each($("#themeList").children(),function(i,n){
				var oDiv = $(n);
				$("#themeList").menu("setIcon",{
					target:n,
					iconCls:'icon-blank'
				});
			});
			$("#themeList").menu("setIcon",{
				target:item.target,
				iconCls:'icon-ok'
			});
			//同步iframe中的皮肤样式
			var iframeStyle = $("#frameContent")[0].contentWindow.document.getElementById("mainStyle");
			if(iframeStyle){
				iframeStyle.href = newStyle;
			}
			top.Home.ThemeName = themeName;
		}else{
			top.Notiy.error("皮肤切换失败，请稍后重试！");
		}
	},"json");
}
//显示桌面
function backHome(){
	var homePath = top.Home.BasePath+"/admin/index/welcome";
	if(top.Frame.FrameUrl != homePath){
		$.each($("#navMenu").find("li"),function(i,li){
			li.className = "nav-default";
			$(li).find("a[src]").css("fontWeight","normal");
			//$(li).find("a[src]").css("color","#333");
		});
		Navigate(homePath, "桌面", "icon-home");
		top.Frame.FrameUrl = homePath;
	}
}
//修改密码
function changePwd(){
	$.post(top.Home.BasePath+"/admin/user/toChangePass",{},function(data){
		data = JSONObject.parseJSON(data);
		if(data.NeedVerifyPassword){
			top.Dialog.verifyPass({msg:"请输入当前用户的原始密码：",errorMsg:"",showType:"notiy",callback:_changePwd_callback});
		}else{
			_showChnagePwdWindows();
		}
	});
}
function _showChnagePwdWindows(){
	var winChangePass = new Windows("win_changePass");
	if(!top.Home.changePwdWindow){
		winChangePass.windows({
			title:"修改密码",
			width:350,
			height:180,
			modal:true,
			expand:true,
			closed:false,
			iconCls:'icon-key',
			href:top.Home.BasePath+"/admin/user/changePass"
		});
		top.Home.changePwdWindow  = winChangePass;
	}else{
		winChangePass.open();
	}
}
function _changePwd_callback(result){
	if(result){
		_showChnagePwdWindows();
	}else{
		top.Dialog.verifyPass({msg:"请重新输入当前用户的原始密码：",errorMsg:"",showType:"notiy",callback:_changePwd_callback});
	}
}
function initKeyboardEvent(dom){
	if(!dom){
		dom = document;
	}
	$(dom).keydown(function(event){
		if(event.keyCode == 27){//按下Esc键
			//逐个关闭弹出窗口
			var oMessageBoxes = $(".messager-window");
			if(oMessageBoxes.length > 0){
				var oMessager = $(oMessageBoxes[0]);
				var oMessagerButtons = oMessager.find(".messager-button").find("a");
				for(var i=0;i<oMessagerButtons.length;i++){
					if(oMessagerButtons[i].innerText == "取消"){
						oMessagerButtons[i].click();
						break;
					}
				}
			}else{
				top.Home.Exit();
			}
		}
	});
}