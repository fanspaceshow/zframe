var JS = {
		importJS : function(url){
			var path = top.Home.BasePath + "/framework/"+url;
			$("head").append("<script type=\"text/javascript\" src=\""+path+"\"></script>");
		},
		importCoreJS : function(url){
			var path = top.Home.BasePath + "/framework/js/core/"+url;
			$("head").append("<script type=\"text/javascript\" src=\""+path+"\"></script>");
		},
		importPluginJS : function(url){
			var path = top.Home.BasePath + "/framework/js/core/plugin/"+url;
			$("head").append("<script type=\"text/javascript\" src=\""+path+"\"></script>");
		}
};