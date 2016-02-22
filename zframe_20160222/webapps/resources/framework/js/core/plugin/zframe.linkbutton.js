$.extend($.fn.linkbutton.methods,{
	//图标设置成动画loading
	loading:function(jq){
		var iconCls = $(jq).linkbutton("options").iconCls;
		$(jq).linkbutton({oIconCls:iconCls});
		$(jq).linkbutton({iconCls:'icon-loading'});
		$(jq).linkbutton("disable");
	},
	//还原成原来的图标
	reset : function(jq){
		var oIconCls = $(jq).linkbutton("options").oIconCls;
		if(oIconCls)
			$(jq).linkbutton({iconCls:oIconCls});
		else
			$(jq).linkbutton({iconCls:'icon-ok'});
		$(jq).linkbutton({oIconCls:undefined});
		$(jq).linkbutton("enable");
	},
	setText : function(jq,txt){
	    $(jq).linkbutton({text:txt});
	},
	getText : function(jq){
	    return (jq).linkbutton("options").text;
	}
});