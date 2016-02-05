// 提示框
var Notiy = {
		defaultLayout:"center",
		defaultTimeout:1000,
		defaultModal:false,
		importJs:function(layout){
			var jHead = $("head");
			if(!$.noty){
				jHead.append("<script type=\"text/javascript\" src=\""+top.Home.BasePath+"/framework/js/noty/jquery.noty.js\"></script>");
				jHead.append("<script type=\"text/javascript\" src=\""+top.Home.BasePath+"/framework/js/noty/themes/default.js\"></script>");
			}
			if(layout.toLowerCase() == "bottom"){
				if(!$.noty.layouts.bottom){
					jHead.append("<script type=\"text/javascript\" src=\""+top.Home.BasePath+"/framework/js/noty/layouts/bottom.js\"></script>");
				}
			}else if(layout.toLowerCase() == "bottomcenter"){
				if(!$.noty.layouts.bottomCenter){
					jHead.append("<script type=\"text/javascript\" src=\""+top.Home.BasePath+"/framework/js/noty/layouts/bottomCenter.js\"></script>");
				}
			}else if(layout.toLowerCase() == "bottomleft"){
				if(!$.noty.layouts.bottomLeft){
					jHead.append("<script type=\"text/javascript\" src=\""+top.Home.BasePath+"/framework/js/noty/layouts/bottomLeft.js\"></script>");
				}
			}else if(layout.toLowerCase() == "bottomright"){
				if(!$.noty.layouts.bottomRight){
					jHead.append("<script type=\"text/javascript\" src=\""+top.Home.BasePath+"/framework/js/noty/layouts/bottomRight.js\"></script>");
				}
			}else if(layout.toLowerCase() == "center"){
				if(!$.noty.layouts.center){
					jHead.append("<script type=\"text/javascript\" src=\""+top.Home.BasePath+"/framework/js/noty/layouts/center.js\"></script>");
				}
			}else if(layout.toLowerCase() == "centerleft"){
				if(!$.noty.layouts.centerLeft){
					jHead.append("<script type=\"text/javascript\" src=\""+top.Home.BasePath+"/framework/js/noty/layouts/centerLeft.js\"></script>");
				}
			}else if(layout.toLowerCase() == "centerright"){
				if(!$.noty.layouts.centerRight){
					jHead.append("<script type=\"text/javascript\" src=\""+top.Home.BasePath+"/framework/js/noty/layouts/centerRight.js\"></script>");
				}
			}else if(layout.toLowerCase() == "inline"){
				if(!$.noty.layouts.inline){
					jHead.append("<script type=\"text/javascript\" src=\""+top.Home.BasePath+"/framework/js/noty/layouts/inline.js\"></script>");
				}
			}else if(layout.toLowerCase() == "top"){
				if(!$.noty.layouts.top){
					jHead.append("<script type=\"text/javascript\" src=\""+top.Home.BasePath+"/framework/js/noty/layouts/top.js\"></script>");
				}
			}else if(layout.toLowerCase() == "topcenter"){
				if(!$.noty.layouts.topCenter){
					jHead.append("<script type=\"text/javascript\" src=\""+top.Home.BasePath+"/framework/js/noty/layouts/topCenter.js\"></script>");
				}
			}else if(layout.toLowerCase() == "topleft"){
				if(!$.noty.layouts.topLeft){
					jHead.append("<script type=\"text/javascript\" src=\""+top.Home.BasePath+"/framework/js/noty/layouts/topLeft.js\"></script>");
				}
			}else if(layout.toLowerCase() == "topright"){
				if(!$.noty.layouts.topRight){
					jHead.append("<script type=\"text/javascript\" src=\""+top.Home.BasePath+"/framework/js/noty/layouts/topRight.js\"></script>");
				}
			}
		},
		init:function(text,type,layout,modal,timeout){
			this.importJs(layout);//导入js
			noty({
				  layout: layout,
				  theme: 'defaultTheme',
				  type: type,
				  text: text,
				  dismissQueue: false, // If you want to use queue feature set this true
				  template: '<div class="noty_message"><span class="noty_text"></span><div class="noty_close"></div></div>',
				  animation: {
				    open: {height: 'toggle'},
				    close: {opacity:"toggle"},
				    easing: 'swing',
				    speed: 300 // opening & closing animation speed
				  },
				  timeout: timeout, // delay for closing event. Set false for sticky notifications
				  force: false, // adds notification to the beginning of queue when set to true
				  modal: modal,
				  closeWith: ['click'], // ['click', 'button', 'hover']
				  callback: {
				    onShow: function() {},
				    afterShow: function() {},
				    onClose: function() {},
				    afterClose: function() {}
				  },
				  buttons: false // an array of buttons
			});
		},
		alert:function(text,modal,layout,timeout){
			if(!modal){
				modal = this.defaultModal;
			}
			if(!layout){
				layout = this.defaultLayout;
			}
			if(!timeout){
				timeout = this.defaultTimeout;
			}
			this.init(text,"alert",layout,modal,timeout);
		},
		info:function(text,modal,layout,timeout){
			if(!modal){
				modal = this.defaultModal;
			}
			if(!layout){
				layout = this.defaultLayout;
			}
			if(!timeout){
				timeout = this.defaultTimeout;
			}
			this.init(text,"information",layout,modal,timeout);
		},
		error:function(text,modal,layout,timeout){
			if(!modal){
				modal = this.defaultModal;
			}
			if(!layout){
				layout = this.defaultLayout;
			}
			if(!timeout){
				timeout = this.defaultTimeout;
			}
			this.init(text,"error",layout,modal,timeout);
		},
        warning:function(text,modal,layout,timeout){
			if(!modal){
				modal = this.defaultModal;
			}
			if(!layout){
				layout = this.defaultLayout;
			}
			if(!timeout){
				timeout = this.defaultTimeout;
			}
			this.init(text,"warning",layout,modal,timeout);
		},
		notification:function(text,modal,layout,timeout){
			if(!modal){
				modal = this.defaultModal;
			}
			if(!layout){
				layout = this.defaultLayout;
            }
			if(!timeout){
				timeout = this.defaultTimeout;
			}
			this.init(text,"notification",layout,modal,timeout);
		},
		success:function(text,modal,layout,timeout){
			if(!modal){
				modal = this.defaultModal;
			}
			if(!layout){
				layout = this.defaultLayout;
				//layout = "topRight";
			}
			if(!timeout){
				timeout = this.defaultTimeout;
			}
			this.init(text,"success",layout,modal,timeout);
		}
};