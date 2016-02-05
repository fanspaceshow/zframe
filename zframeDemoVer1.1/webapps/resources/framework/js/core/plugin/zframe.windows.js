$.extend($.fn.window.methods, {
	shake : function(jq, params) {
		return jq.each(function() {
			var extent = params && params['extent'] ? params['extent'] : 1;
			var interval = params && params['interval'] ? params['interval']
					: 13;
			var style = $(this).closest('div.window')[0].style;
			if ($(this).data("window").shadow) {
				var shadowStyle = $(this).data("window").shadow[0].style;
			}
			_p = [ 4 * extent, 6 * extent, 8 * extent, 6 * extent, 4 * extent,
					0, -4 * extent, -6 * extent, -8 * extent, -6 * extent,
					-4 * extent, 0 ], _fx = function() {
				style.marginLeft = _p.shift() + 'px';
				if (shadowStyle)
					shadowStyle.marginLeft = _p.shift() + 'px';
				if (_p.length <= 0) {
					style.marginLeft = 0;
					if (shadowStyle)
						shadowStyle.marginLeft = 0;
					clearInterval(_timerId);
					_timerId = null, _p = null, _fx = null;
				}
				;
			};
			_p = _p.concat(_p.concat(_p));
			_timerId = setInterval(_fx, interval);
		});
	}
});
function Windows(id,isNew){
	try{
		//添加属性后，需要在方法结尾处初始化winCache
		this.id = (function(str){
			var e = "";
			for(var i=0;i<str.length;i++)
				e+=str.charCodeAt(i).toString();
			return e;
		})("windowDialog_"+id);
		this.windows = windows_proxy;
		this.parent = $(top.window.document.getElementById("frameContent").contentWindow.document);//父窗口document对象
		this.parentWindow = top.window.document.getElementById("frameContent").contentWindow;//父窗口window对象
		this.returnValue = false;//返回值
		this.param = null;//窗口携带的值
		this.callback = null;
		this.size = null;
		this.options = {};//窗口属性
		this.isOpened = false;//标示窗口是否已经打开
		this.setCallback = function(fun){
			if(typeof(fun) == "function"){
				this.callback = fun;
			}else{
				top.Dialog.alert("错误","回调函数类型错误！","error");
			}
		};
		//默认窗口关闭事件
		this.onPreClose = function(){
			if(Windows.UploadFileWindow != null && typeof(Windows.UploadFileWindow.callback) == "function"){
				Windows.UploadFileWindow.callback();
			}
			if(Windows.UploadImageWindow != null && typeof(Windows.UploadImageWindow.callback) == "function"){
				Windows.UploadImageWindow.callback();
			}
			if(typeof(this.callback) == "function")
				this.callback();
			top.Home.WindowsPool[this.id] = undefined;
			this.isOpened = true;
		};
		//关闭窗口
		this.close = function(clearCache){
			if(clearCache != false)
				this.onPreClose();
			this.windows('close',false);
		};
		//打开窗口
		this.open = function(){
			if(this.size!=null){
				this.windows("resize", {width:this.size.width});
				this.size = null;
			}
			this.windows('open');
			this.windows("expand");
			if(this.getOptions().cache == true){
			    this.windows("refresh");
			}
		};
		this.restore = function(){
			if(this.size!=null){
				this.windows("resize", {width:this.size.width});
				this.size = null;
			}
			this.windows("expand");
			this.windows("refresh");
		};
		this.isSuccess = function(){
			var rv = this.getReturnValue();
			if(rv == "true")
				return true;
			return false;
		};
		this.getReturnValue = function(){
			var sReturnValue = top.$("#FrameWorkWindows").find("#RV_"+this.id).val();
			if(sReturnValue==undefined){
				return false;
			}else{
				return sReturnValue;
			}
		};
		this.setReturnValue = function(value){
			this.returnValue = value;
			var RV = top.$("#FrameWorkWindows").find("#RV_"+this.id);
			if(RV.length>0)
				top.$("#FrameWorkWindows").find("#RV_"+this.id).val(value);
			else
				top.Dialog.error("窗口已销毁，无法设置ReturnValue");
		};
		this.getParam = function(){
			var param = top.$("#FrameWorkWindows").find("#PARAM_"+this.id).val();
			return param;
		};
		this.setParam = function(value){
			this.param = value;
			top.$("#FrameWorkWindows").find("#PARAM_"+this.id).val(value);
		};
		this.find = function(EL){
			return top.$("#"+this.id).find(EL);
		};
		this.destory = function(forceDestroy){
			if(forceDestroy)
				this.windows("destory",forceDestroy);
			else
				this.windows("destory");
		};
		this.reBuild = function(opt){
			var options = this.getOptions();
			if(!opt){
				options = opt;
			}
			this.destory();
			this.windows(options);
		};
		this.getOptions = function(){
			this.options = top.$("#"+this.id).window("options");
			return this.options;
		};
		this.showMask = function(){
			var oWin = top.$("#"+this.id).parent();
			if(this.getOptions().modal && this.getOptions().shadow){
				var oShadow = oWin.next();
				var oMask = oShadow.next();
				oMask.show();
			}else if(this.getOptions().model){
				var oMask = oWin.next();
				oMask.show();
			}
		};
		this.hideMask = function(){
			var oWin = top.$("#"+this.id).parent();
			if(this.getOptions().modal && this.getOptions().shadow){
				var oShadow = oWin.next();
				var oMask = oShadow.next();
				oMask.hide();
			}else if(this.getOptions().model){
				var oMask = oWin.next();
				oMask.hide();
			}
		};
		this.move = function(left,top){
			this.windows("move",{left:left,top:top});
		};
		//菜单切换的时候是否清理该窗口，为true则清理，为false则不清理
		this.setAutoClear = function(b){
			top.$("#"+this.id).attr("AutoClear",b);
		};
		this.setHref = function(href){
			this.windows({href:href});
		};
		function init(fid){
			//初始化returnValue
			var sReturnValue = top.$("#FrameWorkWindows").find("#RV_"+fid).val();
			if(sReturnValue==undefined){
				this.returnValue = false;
			}else{
				this.returnValue = sReturnValue;
			}
			//初始化 param
			var param = top.$("#FrameWorkWindows").find("#PARAM_"+fid).val();
			if(param==undefined){
				this.param = false;
			}else{
				this.param = param;
			}
		}
		if(isNew != true){
			//从对象池中取出缓存窗口对象
			var winCache = top.Home.WindowsPool[this.id];
			if(winCache != undefined && winCache != null){
				this.callback = winCache.callback;
				//this.windows = winCache.windows;
				this.parent = winCache.parent;//父窗口document对象
				this.parentWindow = winCache.parentWindow;//父窗口window对象
				this.size = winCache.size;
				this.options = winCache.options;//窗口属性
				this.isOpened = winCache.isOpened;//标示窗口是否打开
			}
		}
		//存入窗口对象池中
		top.Home.WindowsPool[this.id] = this;
		init(this.id);
	}catch(e){
		top.Dialog.alert("错误","脚本错误:"+e.message,"error");
	}
}
function windows_proxy(a,b){
	try{
		var win = this;
		// 设置默认属性
		if(a && typeof a != "string"){
			if(a.minimizable == undefined){
				a.minimizable = false;
			}
			if(a.loadingMessage == undefined){
				a.loadingMessage = "加载中...";
			}
			if(a.closed == undefined){
				a.closed = true;
			}
			if(a.cache == undefined){
			    a.cache = false;
			}
			if(a.tools == undefined){
				if(a.refreshable==undefined || a.refreshable==true){
					var iconCls = "icon-refresh";
					if(top.Home.ThemeName == "default")
						iconCls = "icon-refresh-default";
					a.tools = [{
						iconCls:iconCls,
						handler:function(){
							win.windows('refresh',a.href);
						}
					}];
					a.refreshabled = true;
				}
			}else{
				if((a.refreshable==undefined || a.refreshable == true) && !a.refreshabled){
					var iconCls = "icon-refresh";
					if(top.Home.ThemeName == "default")
						iconCls = "icon-refresh-default";
					var tool = {
						iconCls:iconCls,
						handler:function(){
							win.windows('refresh',a.href);
						}
					};
					a.refreshabled = true;
					a.tools.push(tool);
				}
			}
			
			//默认最小化时关掉遮蔽层
			if(a.onBeforeCollapse == undefined){
				a.onBeforeCollapse = function(){
					win.hideMask();
				};
			}
			//宽度设置为200像素
			if(a.onCollapse == undefined){
				a.onCollapse = function(){
					win.size = new Object();
					win.size.width = win.getOptions().width;
					var width = win.size.width;
					win.windows("resize", {width:200});
				};
			}
			//默认最大化时打开遮蔽层
			if(a.onBeforeExpand == undefined){
				a.onBeforeExpand = function(){
					win.showMask();
					win.windows("resize", {width:win.size.width});
				};
			}
			//设置默认onMove事件
			if(a.onMove == undefined){
				a.onMove = function(l,t){
					if(!win.isOpened)//判断窗口是否已经打开，如果没有打开，则不执行此事件
						return;
					var broWidth = top.$(top.window).width();
					var broHeight = top.$(top.window).height();
					if(broWidth<=0 || broHeight <=0)
						return;
					var newLeft = l,newTop = t;
					var tag = false;
					if(l<=-win.getOptions().width+100 || t<0){
						newLeft = l<=-win.getOptions().width+100?-win.getOptions().width+110:l;
						newTop = t<0?0:t;
						tag = true;
					}
					if(l>=broWidth-10 || t>=broHeight-15){
						newLeft = l>broWidth-100?broWidth-20:newLeft;
						newTop = t>=broHeight-15?broHeight-20:newTop;
						tag = true;
					}
					if(tag){
						win.move(newLeft,newTop);
						setTimeout(function(){
							win.move(newLeft,newTop);
						},500);
					}
				};
			}
		}
		if(top.$("#"+this.id)[0]==undefined){
			var winProxy = $("<div></div>");
			winProxy.attr("id",this.id);
			var btnProxy = $("<input>");
			btnProxy.attr("id","RV_"+this.id);
			btnProxy.attr("type","hidden");
			btnProxy.val(false);
			var paramProxy = $("<input>");
			paramProxy.attr("id","PARAM_"+this.id);
			paramProxy.attr("type","hidden");
			top.$("#FrameWorkWindows").append(winProxy);
			top.$("#FrameWorkWindows").append(btnProxy);
			top.$("#FrameWorkWindows").append(paramProxy);
			top.$("#FrameWorkWindows").find("#"+this.id).window(a,b);
			
			win.setAutoClear(top.Config.AutoClear);//关闭自动清理
		}else{
			top.$("#FrameWorkWindows").find("#"+this.id).remove();
			if(a=='open'){
				top.$("#"+this.id).window('center');
				top.$("#FrameWorkWindows").find("#RV_"+this.id).val("false");
				//win.restore();
				win.isOpened = true;
			}else if(a=='destory'){
				top.$("#"+this.id).window(a,b);
				top.$("#FrameWorkWindows").find("#"+this.id).remove();
				top.$("#FrameWorkWindows").find("#RV_"+this.id).remove();
				top.$("#FrameWorkWindows").find("#PARAM_"+this.id).remove();
				win.isOpened = false;
				return;
			}else if(a == 'close' && b != false){
				win.onPreClose();
			}
			top.$("#"+this.id).window(a,b);
		}
	}catch(e){
		top.Dialog.alert("错误","脚本错误:"+e.message,"error");
	}
}
Windows.UploadFileWindow = null;
Windows.UploadImageWindow = null;
Windows.UploadFile = function(options){
	try {
		var win = new Windows("sys_uploader_file");
		var op = {
			title:'文件上传',
			width:410,
			height:300,
			modal:true,
			cache:true,
			closed:true,
			refreshable:false,
			iconCls:'icon-upload',
			href:top.Home.BasePath+"/admin/uploader/upload/file"
		};
		if(options){
			if(options.title){
				op.title = options.title;
			}
			if(options.width){
				op.width = options.width;
			}
			if(options.height){
				op.height = options.height;
			}
			if(options.modal){
				op.modal = options.modal;
			}
			if(options.iconCls){
				op.iconCls = options.iconCls;
			}
		}
		win.windows(op);
		win.setCallback(function(){
				if(options){
					if(typeof(options.onUploadComplete) == "function"){
						var returnValue = win.getReturnValue();
						//调用回调函数
						options.onUploadComplete(returnValue);
					}
				}
		});
		Windows.UploadFileWindow = win;
		win.open();
	} catch (e) {
		top.Dialog.alert("错误","脚本错误:"+e.message,"error");
	}
};
Windows.UploadImage = function(options){
	try {
		var win = new Windows("sys_uploader_image");
		var op = {
			title:'图片上传',
			width:410,
			height:300,
			modal:true,
			cache:true,
			closed:true,
			refreshable:false,
			iconCls:'icon-picture',
			href:top.Home.BasePath+"/admin/uploader/upload/image"
		};
		if(options){
			if(options.title){
				op.title = options.title;
			}
			if(options.width){
				op.width = options.width;
			}
			if(options.height){
				op.height = options.height;
			}
			if(options.modal){
				op.modal = options.modal;
			}
			if(options.iconCls){
				op.iconCls = options.iconCls;
			}
		}
		win.windows(op);
		win.setCallback(function(){
			if(options){
				if(typeof(options.onUploadComplete) == "function"){
					var returnValue = win.getReturnValue();
					//调用回调函数
					options.onUploadComplete(returnValue);
				}
			}
		});
		Windows.UploadImageWindow = win;
		win.open();
	} catch (e) {
		
	}
};